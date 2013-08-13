package com.dev.codegen.main;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.MathTool;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import com.dev.codegen.exception.CodegenException;

/**
 * The Class.
 */
public class Main {

	/** The Constant LOG. */
	private static final Log LOG = LogFactory.getLog(Main.class);

	/** The Constant VELOCITY_CONTEXT. */
	private static final VelocityContext VELOCITY_CONTEXT = new VelocityContext();

	/** The Constant DATA_TYPE_MAP. */
	private static final Map<String, String> DATA_TYPE_MAP = new HashMap<String, String>(1);

	/** The Constant IGNORE_TABLES. */
	private static final String[] IGNORE_TABLES = new String[] { "Zudit", "TEMP_", "ZTEMP_", "ZZTEMP_" };

	/** The basic data source. */
	private static final BasicDataSource DATA_SOURCE = new BasicDataSource();

	/** The base package. */
	private static String BASE_PACKAGE;

	/** The entity package. */
	private static String ENTITY_PACKAGE;

	/** The sc package. */
	private static String SC_PACKAGE;

	/** The dao package. */
	private static String DAO_PACKAGE;

	/** The dao impl package. */
	private static String DAO_IMPL_PACKAGE;

	/** The rm package. */
	private static String RM_PACKAGE;

	/** The Constant CONFIGURATION. */
	private static final CompositeConfiguration CONFIGURATION = new CompositeConfiguration();

	/**
	 * The Enum StatementType.
	 */
	public enum StatementType {

		/** The insert. */
		INSERT,
		/** The UPDATE. */
		UPDATE;
	}

	static {

		DATA_TYPE_MAP.put("varchar", "java.lang.String");
		DATA_TYPE_MAP.put("nvarchar", "java.lang.String");
		DATA_TYPE_MAP.put("text", "java.lang.String");
		DATA_TYPE_MAP.put("char", "java.lang.String");
		DATA_TYPE_MAP.put("nchar", "java.lang.String");
		DATA_TYPE_MAP.put("bit", "java.lang.Boolean");
		DATA_TYPE_MAP.put("datetime", "java.util.Date");
		DATA_TYPE_MAP.put("smalldatetime", "java.util.Date");
		DATA_TYPE_MAP.put("datetime2", "java.util.Date");
		DATA_TYPE_MAP.put("date", "java.util.Date");
		DATA_TYPE_MAP.put("int", "java.lang.Integer");
		DATA_TYPE_MAP.put("int identity", "java.lang.Integer");
		DATA_TYPE_MAP.put("smallint", "java.lang.Integer");
		DATA_TYPE_MAP.put("smallint identity", "java.lang.Integer");
		DATA_TYPE_MAP.put("tinyint", "java.lang.Integer");
		DATA_TYPE_MAP.put("tinyint identity", "java.lang.Integer");
		DATA_TYPE_MAP.put("bigint", "java.lang.Long");
		DATA_TYPE_MAP.put("bigint identity", "java.lang.Long");
		DATA_TYPE_MAP.put("float", "java.lang.Float");
		DATA_TYPE_MAP.put("decimal", "java.lang.Double");
		DATA_TYPE_MAP.put("numeric", "java.math.BigDecimal");
		DATA_TYPE_MAP.put("image", "java.lang.Byte[]");
		DATA_TYPE_MAP.put("varbinary", "java.lang.Byte[]");
		DATA_TYPE_MAP.put("xml", "org.w3c.dom.Document");
		DATA_TYPE_MAP.put("sysname", "java.lang.String");

		Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

		LOG.info("Velocity Intialized");
		Velocity.init();
		VELOCITY_CONTEXT.put("STRING_UTILS", StringUtils.class);
		VELOCITY_CONTEXT.put("math", new MathTool());

		try {
			init();
		} catch (Exception e) {
			LOG.error("error occurred while initializing", e);
		}

	}// static

	/**
	 * Creates the data source.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	static void init() throws IOException, ConfigurationException {
		final URL url = Thread.currentThread().getContextClassLoader().getResource("codegen.properties");
		CONFIGURATION.addConfiguration(new PropertiesConfiguration(url));

		DATA_SOURCE.setUrl(CONFIGURATION.getString("codegen.jdbc.url"));
		DATA_SOURCE.setDriverClassName(CONFIGURATION.getString("codegen.jdbc.driver"));
		DATA_SOURCE.setUsername(CONFIGURATION.getString("codegen.jdbc.username"));
		DATA_SOURCE.setPassword(CONFIGURATION.getString("codegen.jdbc.password"));

		BASE_PACKAGE = CONFIGURATION.getString("codegen.base.package.name");
		ENTITY_PACKAGE = String.format("%s.%s", BASE_PACKAGE, CONFIGURATION.getString("codegen.entity.package.name"));
		RM_PACKAGE = String.format("%s.%s", BASE_PACKAGE, CONFIGURATION.getString("codegen.rm.package.name"));
		SC_PACKAGE = String.format("%s.%s", BASE_PACKAGE, CONFIGURATION.getString("codegen.sc.package.name"));
		DAO_PACKAGE = String.format("%s.%s", BASE_PACKAGE, CONFIGURATION.getString("codegen.dao.package.name"));
		DAO_IMPL_PACKAGE = String.format("%s.%s", BASE_PACKAGE, CONFIGURATION.getString("codegen.dao.impl.package.name"));

		VELOCITY_CONTEXT.put("entity_suffix", CONFIGURATION.getString("codegen.entity.suffix"));
		VELOCITY_CONTEXT.put("rm_suffix", CONFIGURATION.getString("codegen.rm.suffix"));
		VELOCITY_CONTEXT.put("dao_suffix", CONFIGURATION.getString("codegen.dao.suffix"));
		VELOCITY_CONTEXT.put("dao_impl_suffix", CONFIGURATION.getString("codegen.dao.impl.suffix"));
	}// init()

	/**
	 * Close data source.
	 */
	void closeDataSource() {
		try {
			if (DATA_SOURCE != null) {
				DATA_SOURCE.close();
			}// if
		} catch (SQLException e) {
			e.printStackTrace();
		}// try-catch
	}

	/**
	 * Close connection.
	 * 
	 * @param connection
	 *            the connection
	 */
	void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}// try-catch
		}// if
	}

	/**
	 * Discover tables.
	 * 
	 * @param tablePattern
	 *            the table pattern
	 * @param connection
	 *            the connection
	 * @return the list
	 * @throws CodegenException
	 *             the codegen exception
	 */
	public List<Table> discoverTables(final String tablePattern, final Connection connection) throws CodegenException {
		final List<Table> tables = new ArrayList<Table>(1);
		final Set<String> missingColumnList = new HashSet<String>(1);

		try {

			final DatabaseMetaData databaseMetaData = connection.getMetaData();

			final ResultSet tableResultSet = databaseMetaData.getTables(null, null, tablePattern, new String[] { "TABLE" });

			while (tableResultSet.next()) {
				final String tableName = tableResultSet.getString(3);
				
				// ResultSet exportedKeysResultSet =
				// databaseMetaData.getExportedKeys(null, null, tableName);
				// System.out.println("==============" + tableName +
				// "==============");
				// while (exportedKeysResultSet.next()) {
				// System.out.println("PKTABLE_NAME " +
				// exportedKeysResultSet.getString("PKTABLE_NAME"));
				// System.out.println("PKCOLUMN_NAME " +
				// exportedKeysResultSet.getString("PKCOLUMN_NAME"));
				// System.out.println("FKTABLE_NAME " +
				// exportedKeysResultSet.getString("FKTABLE_NAME"));
				// System.out.println("FKCOLUMN_NAME " +
				// exportedKeysResultSet.getString("FKCOLUMN_NAME"));
				// }
				// System.out.println("=============================================");
				final Table table = new Table(tableName, formatTableName(tableName));
				if (StringUtils.startsWithAny(tableName, IGNORE_TABLES)) {
					continue;
				}// if

				LOG.info(tableName);

				final ResultSet columnNameResultSet = databaseMetaData.getColumns(null, null, tableName, "%");

				while (columnNameResultSet.next()) {
					final String columnName = columnNameResultSet.getString("COLUMN_NAME");
					final String columnType = columnNameResultSet.getString("TYPE_NAME");
					final boolean isNull = (columnNameResultSet.getString("IS_NULLABLE").equals("YES"));
					final boolean isIdentity = (columnNameResultSet.getString("IS_AUTOINCREMENT").equals("YES"));
					final int size = columnNameResultSet.getInt("COLUMN_SIZE");

					if (!DATA_TYPE_MAP.containsKey(columnType)) {
						missingColumnList.add(columnType);
					}// if
					final Column column = new Column(columnName, formatTableName(columnName), DATA_TYPE_MAP.get(columnType));
					column.setNull(isNull);
					column.setPrimaryKey(isPrimaryKey(databaseMetaData, tableName, columnName));
					column.setIdentity(isIdentity);
					column.setSize(size);
					table.getColumns().add(column);
				}// while
				tables.add(table);
			}// while

			for (String columnsTypes : missingColumnList) {
				LOG.error(columnsTypes);
			}// for

		} catch (Exception e) {
			throw new CodegenException(e);
		}
		// LOG.info(tables);
		return tables;
	}// doSomething()

	/**
	 * Checks if is primary key.
	 * 
	 * @param databaseMetaData
	 *            the database meta data
	 * @param tableName
	 *            the table name
	 * @param columnName
	 *            the column name
	 * @return true, if is primary key
	 * @throws SQLException
	 *             the sQL exception
	 */
	private boolean isPrimaryKey(final DatabaseMetaData databaseMetaData, final String tableName, final String columnName) throws SQLException {
		ResultSet pkResultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
		boolean isPk = false;
		while (pkResultSet.next()) {
			final String name = pkResultSet.getString("COLUMN_NAME");
			// LOG.info("COLUMN_NAME " + name);
			if (StringUtils.equalsIgnoreCase(columnName, name)) {
				isPk = true;
				break;
			}// if
		}
		return isPk;
	}

	/**
	 * Generate entity.
	 * 
	 * @param tables
	 *            the tables
	 * @param withBuilder
	 *            the with builder
	 * @param classNameSuffix
	 *            TODO
	 * @throws CodegenException
	 *             the codegen exception
	 */
	void generateEntity(final List<Table> tables, final boolean withBuilder, String classNameSuffix) throws CodegenException {
		if (CollectionUtils.isEmpty(tables)) {
			return;
		}// if
		Template entityTemplate = null;

		if (withBuilder) {
			entityTemplate = Velocity.getTemplate("templates/entity_with_builder.vm");
		} else {
			entityTemplate = Velocity.getTemplate("templates/entity.vm");
		}// if-else

		try {
			VELOCITY_CONTEXT.put("package_name", ENTITY_PACKAGE);
			VELOCITY_CONTEXT.put("class_name_suffix", classNameSuffix);
			for (Table table : tables) {

				VELOCITY_CONTEXT.put("class_name", table.getName());
				VELOCITY_CONTEXT.put("fields", table.getColumns());

				final StringWriter writer = new StringWriter();
				entityTemplate.merge(VELOCITY_CONTEXT, writer);
				final String dir = StringUtils
						.replaceEach((String) VELOCITY_CONTEXT.get("package_name"), new String[] { "." }, new String[] { File.separator });
				final File dirFile = new File(dir);
				if (dirFile.exists()) {
					FileUtils.forceMkdir(dirFile);
				}// if

				FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + classNameSuffix + ".java"),
						writer.toString());
				// LOG.info(writer.toString());
				IOUtils.closeQuietly(writer);
			}// for
		} catch (Exception e) {
			throw new CodegenException(e);
		}// try-catch
	}// generateEnity()

	/**
	 * Generate row mapper.
	 * 
	 * @param tables
	 *            the tables
	 * @throws CodegenException
	 *             the codegen exception
	 */
	void generateRowMapper(final List<Table> tables) throws CodegenException {
		if (CollectionUtils.isEmpty(tables)) {
			return;
		}
		try {
			final Template template = Velocity.getTemplate("templates/rowmapper.vm");
			VELOCITY_CONTEXT.put("package_name", RM_PACKAGE);
			VELOCITY_CONTEXT.put("entity_package_name", ENTITY_PACKAGE);

			for (Table table : tables) {
				VELOCITY_CONTEXT.put("class_name", table.getName());
				VELOCITY_CONTEXT.put("mapper_class_name", table.getName());
				VELOCITY_CONTEXT.put("fields", table.getColumns());

				final StringWriter writer = new StringWriter();
				template.merge(VELOCITY_CONTEXT, writer);
				final String dir = StringUtils
						.replaceEach((String) VELOCITY_CONTEXT.get("package_name"), new String[] { "." }, new String[] { File.separator });
				final File dirFile = new File(dir);
				if (dirFile.exists()) {
					FileUtils.forceMkdir(dirFile);
				}// if

				FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + "RowMapper.java"), writer.toString());
				IOUtils.closeQuietly(writer);

			}// for
		} catch (Exception e) {
			throw new CodegenException(e);
		}
	}

	/**
	 * Generate insert statement creator.
	 * 
	 * @param tables
	 *            the tables
	 * @param statementType
	 *            the statement type
	 * @throws CodegenException
	 *             the codegen exception
	 */
	void generateStatementCreator(List<Table> tables, StatementType statementType) throws CodegenException {
		if (CollectionUtils.isEmpty(tables)) {
			return;
		}// if

		Template template = null;
		String tmpTable = "";
		VELOCITY_CONTEXT.put("package_name", SC_PACKAGE);
		try {
			if (statementType == StatementType.INSERT) {
				template = Velocity.getTemplate("templates/insert_statement_creator.vm");
			} else {
				template = Velocity.getTemplate("templates/update_statement_creator.vm");
			}// if-else

			for (Table table : tables) {
				tmpTable = table.getName();
				VELOCITY_CONTEXT.put("class_name", table.getName());
				VELOCITY_CONTEXT.put("mapper_class_name", table.getName());
				VELOCITY_CONTEXT.put("fields", table.getColumns());

				@SuppressWarnings("unchecked")
				final Collection<Column> pkColumns = CollectionUtils.select(table.getColumns(), new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						return ((Column) object).isPrimaryKey();
					}
				});
				@SuppressWarnings("unchecked")
				final Collection<Column> noPKColumns = CollectionUtils.selectRejected(table.getColumns(), new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						return ((Column) object).isPrimaryKey();
					}
				});

				VELOCITY_CONTEXT.put("pkColumns", pkColumns);
				VELOCITY_CONTEXT.put("noPKColumns", noPKColumns);

				List<String> queryList = new ArrayList<String>(1);

				if (statementType == StatementType.INSERT) {
					VELOCITY_CONTEXT.put("fields", CollectionUtils.selectRejected(table.getColumns(), new Predicate() {

						@Override
						public boolean evaluate(Object object) {
							final Column column = (Column) object;
							return (column.isPrimaryKey() && column.isIdentity());
						}
					}));

					queryList.add(String.format("INSERT INTO %s (", table.getTableName()));

					// for column name
					final List<String> columns = new ArrayList<String>(1);

					for (Column column : table.getColumns()) {
						if (column.isPrimaryKey() && column.isIdentity()) {
							continue;
						}// if
						columns.add(column.getColumnName() + ",");
					}
					columns.set(columns.size() - 1, StringUtils.replace(columns.get(columns.size() - 1), ",", ""));

					for (String col : columns) {
						queryList.add(StringUtils.leftPad(col, (col.length() + 4)));
					}
					final String values = StringUtils.repeat("?", ", ", table.getColumns().size());
					queryList.add(String.format(") VALUES (%s)", values));
				}// if

				if (statementType == StatementType.UPDATE) {
					if (CollectionUtils.isEmpty(noPKColumns)) {
						continue;
					}
					queryList.add(String.format("UPDATE %s SET ", table.getTableName()));

					// for column name
					final List<String> columns = new ArrayList<String>(1);
					final List<String> whereColumns = new ArrayList<String>(1);

					for (Column column : noPKColumns) {
						columns.add(column.getColumnName() + "=?,");
					}

					for (Column column : pkColumns) {
						if (column.isPrimaryKey()) {
							whereColumns.add(StringUtils.leftPad(column.getColumnName() + "=?", (column.getColumnName().length() + 4)));
						}// if
					}

					columns.set(columns.size() - 1, StringUtils.replace(columns.get(columns.size() - 1), ",", ""));

					for (String col : columns) {
						queryList.add(StringUtils.leftPad(col, (col.length() + 4)));
					}

					if (CollectionUtils.isNotEmpty(whereColumns)) {
						queryList.add("WHERE");
						queryList.add(StringUtils.join(whereColumns, " and"));
					}
				}// if

				VELOCITY_CONTEXT.put("queryList", queryList);

				final StringWriter writer = new StringWriter();
				template.merge(VELOCITY_CONTEXT, writer);
				final String dir = StringUtils
						.replaceEach((String) VELOCITY_CONTEXT.get("package_name"), new String[] { "." }, new String[] { File.separator });
				final File dirFile = new File(dir);

				if (dirFile.exists()) {
					FileUtils.forceMkdir(dirFile);
				}// if

				if (statementType == StatementType.INSERT) {
					FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + "InsertStatementCreator.java"),
							writer.toString());
				} else {
					FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + "UpdateStatementCreator.java"),
							writer.toString());
				}// if-else

				IOUtils.closeQuietly(writer);
			}// for
		} catch (Exception e) {
			throw new CodegenException("error occurred while porcessing table " + tmpTable, e);
		}// try-catch
	}//

	/**
	 * Generate dao.
	 * 
	 * @param tables
	 *            the tables
	 * @param classNameSuffix
	 *            the class name suffix
	 * @throws CodegenException
	 *             the codegen exception
	 */
	void generateDAO(final List<Table> tables, String classNameSuffix) throws CodegenException {
		if (CollectionUtils.isEmpty(tables)) {
			return;
		}// if
		Template entityTemplate = Velocity.getTemplate("templates/dao.vm");

		try {
			VELOCITY_CONTEXT.put("package_name", DAO_PACKAGE);
			VELOCITY_CONTEXT.put("class_name_suffix", classNameSuffix);
			VELOCITY_CONTEXT.put("entity_package_name", ENTITY_PACKAGE);
			for (Table table : tables) {

				@SuppressWarnings("unchecked")
				final Collection<Column> pkColumns = CollectionUtils.select(table.getColumns(), new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						return ((Column) object).isPrimaryKey();
					}
				});

				if (CollectionUtils.isNotEmpty(pkColumns)) {
					VELOCITY_CONTEXT.put("pkColumns", pkColumns);

					VELOCITY_CONTEXT.put("find_one_method_agrs", CollectionUtils.collect(pkColumns, new Transformer() {

						@Override
						public Object transform(Object input) {
							final Column column = (Column) input;
							return String.format("%s %s", column.getDataType(), StringUtils.uncapitalize(column.getName()));
						}
					}));
				}
				VELOCITY_CONTEXT.put("class_name", table.getName());
				VELOCITY_CONTEXT.put("fields", table.getColumns());

				// for column name enum
				final List<String> names = new ArrayList<String>(1);

				for (Column column : table.getColumns()) {
					names.add(column.getColumnName());
				}

				final String enumDef = StringUtils.join(names.iterator(), ",");
				VELOCITY_CONTEXT.put("enum", enumDef);

				final StringWriter writer = new StringWriter();
				entityTemplate.merge(VELOCITY_CONTEXT, writer);
				final String dir = StringUtils
						.replaceEach((String) VELOCITY_CONTEXT.get("package_name"), new String[] { "." }, new String[] { File.separator });
				final File dirFile = new File(dir);
				if (dirFile.exists()) {
					FileUtils.forceMkdir(dirFile);
				}// if

				FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + classNameSuffix + ".java"),
						writer.toString());
				// LOG.info(writer.toString());
				IOUtils.closeQuietly(writer);
			}// for
		} catch (Exception e) {
			throw new CodegenException(e);
		}// try-catch
	}// generateEnity()

	/**
	 * Generate dao impl.
	 * 
	 * @param tables
	 *            the tables
	 * @param classNameSuffix
	 *            the class name suffix
	 * @throws CodegenException
	 *             the codegen exception
	 */
	void generateDAOImpl(final List<Table> tables, String classNameSuffix) throws CodegenException {
		if (CollectionUtils.isEmpty(tables)) {
			return;
		}// if
		final Template entityTemplate = Velocity.getTemplate("templates/dao_impl.vm");

		try {
			VELOCITY_CONTEXT.put("package_name", DAO_IMPL_PACKAGE);
			VELOCITY_CONTEXT.put("class_name_suffix", classNameSuffix);
			VELOCITY_CONTEXT.put("entity_package_name", ENTITY_PACKAGE);
			VELOCITY_CONTEXT.put("dao_package_name", DAO_PACKAGE);
			VELOCITY_CONTEXT.put("sc_package_name", SC_PACKAGE);
			VELOCITY_CONTEXT.put("dao_class_name_suffix", "DAO");

			for (Table table : tables) {

				@SuppressWarnings("unchecked")
				final Collection<Column> pkColumns = CollectionUtils.select(table.getColumns(), new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						return ((Column) object).isPrimaryKey();
					}
				});

				if (CollectionUtils.isNotEmpty(pkColumns)) {
					VELOCITY_CONTEXT.put("pkColumns", pkColumns);
				}

				@SuppressWarnings("unchecked")
				final Collection<Column> noPKColumns = CollectionUtils.selectRejected(table.getColumns(), new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						return ((Column) object).isPrimaryKey();
					}
				});

				if (CollectionUtils.isNotEmpty(pkColumns)) {
					VELOCITY_CONTEXT.put("noPKColumns", noPKColumns);
				}

				final List<String> whereColumns = new ArrayList<String>(1);
				for (Column column : pkColumns) {
					if (column.isPrimaryKey()) {
						whereColumns.add(column.getColumnName() + "=?");
					}// if
				}
				if (CollectionUtils.isNotEmpty(whereColumns)) {
					VELOCITY_CONTEXT.put("whereColumns", whereColumns);
				}

				VELOCITY_CONTEXT.put("class_name", table.getName());
				VELOCITY_CONTEXT.put("fields", table.getColumns());
				VELOCITY_CONTEXT.put("table_name", table.getTableName());
				VELOCITY_CONTEXT.put("columns", CollectionUtils.collect(table.getColumns(), new Transformer() {

					@Override
					public Object transform(Object input) {
						return ((Column) input).getColumnName();
					}
				}));

				VELOCITY_CONTEXT.put("find_one_method_agrs", CollectionUtils.collect(pkColumns, new Transformer() {

					@Override
					public Object transform(Object input) {
						final Column column = (Column) input;
						return String.format("%s %s", column.getDataType(), StringUtils.uncapitalize(column.getName()));
					}
				}));

				VELOCITY_CONTEXT.put("find_one_query_agrs", CollectionUtils.collect(pkColumns, new Transformer() {

					@Override
					public Object transform(Object input) {
						final Column column = (Column) input;
						return column.getColumnName();
					}
				}));

				final StringWriter writer = new StringWriter();
				entityTemplate.merge(VELOCITY_CONTEXT, writer);
				final String dir = StringUtils
						.replaceEach((String) VELOCITY_CONTEXT.get("package_name"), new String[] { "." }, new String[] { File.separator });
				final File dirFile = new File(dir);
				if (dirFile.exists()) {
					FileUtils.forceMkdir(dirFile);
				}// if

				FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + classNameSuffix + ".java"),
						writer.toString());
				// LOG.info(writer.toString());
				IOUtils.closeQuietly(writer);
			}// for
		} catch (Exception e) {
			throw new CodegenException(e);
		}// try-catch
	}// generateEnity()

	/**
	 * List tables.
	 * 
	 * @return the list
	 */
	public List<Table> listTables() {
		List<Table> tables;
		try {
			final JdbcTemplate jdbcTemplate = new JdbcTemplate(DATA_SOURCE);
			tables = jdbcTemplate.execute(new ConnectionCallback<List<Table>>() {

				@Override
				public List<Table> doInConnection(Connection con) throws SQLException, DataAccessException {
					List<Table> tableList = null;
					try {
						tableList = discoverTables(CONFIGURATION.getString("codegen.table.pattern"), con);
					} catch (CodegenException e) {
						LOG.error("error occurred while ", e);
					}// try-catch
					return tableList;
				}// doInConnection()
			});
		} finally {
			closeDataSource();
		}// finally
		return tables;
	}// listTables

	/**
	 * Generate code.
	 * 
	 * @throws CodegenException
	 *             the codegen exception
	 */
	public void generateCode() throws CodegenException {

		final StopWatch stopWatch = new StopWatch("Code Generation");
		try {
			stopWatch.start("List Tables");
			final List<Table> tables = listTables();
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}

			stopWatch.start("Generate Entity");
			generateEntity(tables, true, "");
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}

			stopWatch.start("Generate Row Mapper");
			generateRowMapper(tables);
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}

			stopWatch.start("Generate Insert Statement Creator");
			generateStatementCreator(tables, StatementType.INSERT);
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}

			stopWatch.start("Generate Update Statement Creator");
			generateStatementCreator(tables, StatementType.UPDATE);
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}

			stopWatch.start("Generate DAO");
			generateDAO(tables, "DAO");
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}
			stopWatch.start("Generate DAO Impl");
			generateDAOImpl(tables, "DAOImpl");
			if (stopWatch.isRunning()) {
				stopWatch.stop();
			}
		} finally {
			LOG.info(stopWatch.prettyPrint());
		}// finally
	}

	/**
	 * 
	 * @param tableName
	 * @return
	 */
	private static String formatTableName(String tableName) {
		return StringUtils.deleteWhitespace(
					WordUtils.capitalize(
							WordUtils.uncapitalize(
									StringUtils.lowerCase(
											StringUtils.replaceEach(
													StringUtils.upperCase(tableName), new String[] { "_" }, new String[] { " " }
											)
									)
							)
					)
				);
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws CodegenException
	 *             the codegen exception
	 */
	public static void main(String[] args) throws CodegenException {
		 new Main().generateCode();
	}// main

}
