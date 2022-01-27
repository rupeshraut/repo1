package com.dev.codegen.main;

import com.dev.codegen.exception.CodegenException;
import lombok.extern.apachecommons.CommonsLog;
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
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.MathTool;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * The type Main.
 */
@CommonsLog
public class Main {


    /**
     * The constant PACKAGE_NAME.
     */
    public static final String PACKAGE_NAME = "package_name";
    /**
     * The constant CLASS_NAME_SUFFIX.
     */
    public static final String CLASS_NAME_SUFFIX = "class_name_suffix";
    /**
     * The constant CLASS_NAME.
     */
    public static final String CLASS_NAME = "class_name";
    /**
     * The constant FIELDS.
     */
    public static final String FIELDS = "fields";
    /**
     * The constant JAVA.
     */
    public static final String JAVA = ".java";
    /**
     * The constant ENTITY_PACKAGE_NAME.
     */
    public static final String ENTITY_PACKAGE_NAME = "entity_package_name";
    /**
     * The constant PK_COLUMNS.
     */
    public static final String PK_COLUMNS = "pkColumns";
    /**
     * The constant NO_PK_COLUMNS.
     */
    public static final String NO_PK_COLUMNS = "noPKColumns";
    public static final String S_S = "%s.%s";
    public static final String JAVA_LANG_STRING = "java.lang.String";
    public static final String JAVA_UTIL_DATE = "java.util.Date";
    public static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    private static final Map<String, String> DATA_TYPE_MAP = new HashMap<>(1);
    private static final VelocityContext VELOCITY_CONTEXT = new VelocityContext();
    private static String BASE_PACKAGE;
    private static String ENTITY_PACKAGE;
    private static String SC_PACKAGE;
    private static String DAO_PACKAGE;
    private static String DAO_IMPL_PACKAGE;
    private static String RM_PACKAGE;
    private static final String[] IGNORE_TABLES = new String[]{"Zudit", "TEMP_", "ZTEMP_", "ZZTEMP_"};
    private static final BasicDataSource DATA_SOURCE = new BasicDataSource();
    private static final CompositeConfiguration CONFIGURATION = new CompositeConfiguration();

    static {

        DATA_TYPE_MAP.put("varchar", JAVA_LANG_STRING);
        DATA_TYPE_MAP.put("nvarchar", JAVA_LANG_STRING);
        DATA_TYPE_MAP.put("text", JAVA_LANG_STRING);
        DATA_TYPE_MAP.put("char", JAVA_LANG_STRING);
        DATA_TYPE_MAP.put("nchar", JAVA_LANG_STRING);
        DATA_TYPE_MAP.put("bit", "java.lang.Boolean");
        DATA_TYPE_MAP.put("datetime", JAVA_UTIL_DATE);
        DATA_TYPE_MAP.put("smalldatetime", JAVA_UTIL_DATE);
        DATA_TYPE_MAP.put("datetime2", JAVA_UTIL_DATE);
        DATA_TYPE_MAP.put("date", JAVA_UTIL_DATE);
        DATA_TYPE_MAP.put("int", JAVA_LANG_INTEGER);
        DATA_TYPE_MAP.put("int identity", JAVA_LANG_INTEGER);
        DATA_TYPE_MAP.put("smallint", JAVA_LANG_INTEGER);
        DATA_TYPE_MAP.put("smallint identity", JAVA_LANG_INTEGER);
        DATA_TYPE_MAP.put("tinyint", JAVA_LANG_INTEGER);
        DATA_TYPE_MAP.put("tinyint identity", JAVA_LANG_INTEGER);
        DATA_TYPE_MAP.put("bigint", "java.lang.Long");
        DATA_TYPE_MAP.put("bigint identity", "java.lang.Long");
        DATA_TYPE_MAP.put("float", "java.lang.Float");
        DATA_TYPE_MAP.put("decimal", "java.lang.Double");
        DATA_TYPE_MAP.put("numeric", "java.math.BigDecimal");
        DATA_TYPE_MAP.put("image", "java.lang.Byte[]");
        DATA_TYPE_MAP.put("varbinary", "java.lang.Byte[]");
        DATA_TYPE_MAP.put("xml", "org.w3c.dom.Document");
        DATA_TYPE_MAP.put("sysname", JAVA_LANG_STRING);

        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        log.info("Velocity Intialized");
        Velocity.init();
        VELOCITY_CONTEXT.put("STRING_UTILS", StringUtils.class);
        VELOCITY_CONTEXT.put("math", new MathTool());

        try {
            init();
        } catch (Exception e) {
            log.error("error occurred while initializing", e);
        }

    }// static

    /**
     * Init.
     *
     * @throws ConfigurationException the configuration exception
     */
    static void init() throws ConfigurationException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("codegen.properties");
        CONFIGURATION.addConfiguration(new PropertiesConfiguration(url));

        DATA_SOURCE.setUrl(CONFIGURATION.getString("codegen.jdbc.url"));
        DATA_SOURCE.setDriverClassName(CONFIGURATION.getString("codegen.jdbc.driver"));
        DATA_SOURCE.setUsername(CONFIGURATION.getString("codegen.jdbc.username"));
        DATA_SOURCE.setPassword(CONFIGURATION.getString("codegen.jdbc.password"));

        BASE_PACKAGE = CONFIGURATION.getString("codegen.base.package.name");
        ENTITY_PACKAGE = String.format(S_S, BASE_PACKAGE, CONFIGURATION.getString("codegen.entity.package.name"));
        RM_PACKAGE = String.format(S_S, BASE_PACKAGE, CONFIGURATION.getString("codegen.rm.package.name"));
        SC_PACKAGE = String.format(S_S, BASE_PACKAGE, CONFIGURATION.getString("codegen.sc.package.name"));
        DAO_PACKAGE = String.format(S_S, BASE_PACKAGE, CONFIGURATION.getString("codegen.dao.package.name"));
        DAO_IMPL_PACKAGE = String.format(S_S, BASE_PACKAGE, CONFIGURATION.getString("codegen.dao.impl.package.name"));

        VELOCITY_CONTEXT.put("entity_suffix", CONFIGURATION.getString("codegen.entity.suffix"));
        VELOCITY_CONTEXT.put("rm_suffix", CONFIGURATION.getString("codegen.rm.suffix"));
        VELOCITY_CONTEXT.put("dao_suffix", CONFIGURATION.getString("codegen.dao.suffix"));
        VELOCITY_CONTEXT.put("dao_impl_suffix", CONFIGURATION.getString("codegen.dao.impl.suffix"));
    }// init()

    private static String formatTableName(String tableName) {
        return StringUtils.deleteWhitespace(WordUtils.capitalize(WordUtils.uncapitalize(StringUtils.lowerCase(StringUtils.replaceEach(
                StringUtils.upperCase(tableName), new String[]{"_"}, new String[]{" "})))));
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws CodegenException the codegen exception
     */
    public static void main(String[] args) throws CodegenException {
        new Main().generateCode();
    }// main∫

    /**
     * Close data source.
     */
    void closeDataSource() {
        try {
            if (Objects.nonNull(DATA_SOURCE)) {
                DATA_SOURCE.close();
            }// if
        } catch (SQLException e) {
            log.error("error occurred while closing data source", e);
        }// try-catch
    }

    /**
     * Discover tables list.
     *
     * @param tablePattern the table pattern
     * @param connection   the connection
     * @return the list
     * @throws CodegenException the codegen exception
     */
    public List<Table> discoverTables(final String tablePattern, final Connection connection) throws CodegenException {
        final List<Table> tables = new ArrayList<>(1);
        final Set<String> missingColumnList = new HashSet<>(1);

        try {

            final DatabaseMetaData databaseMetaData = connection.getMetaData();

            final ResultSet tableResultSet = databaseMetaData.getTables(null, null, tablePattern, new String[]{"TABLE"});

            while (tableResultSet.next()) {
                final String tableName = tableResultSet.getString(3);

                final Table table = new Table(tableName, formatTableName(tableName));
                if (StringUtils.startsWithAny(tableName, IGNORE_TABLES)) {
                    continue;
                }// if

                log.info(tableName);

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
                log.error(columnsTypes);
            }// for

        } catch (Exception e) {
            throw new CodegenException(e);
        }
        return tables;
    }// doSomething()

    private boolean isPrimaryKey(final DatabaseMetaData databaseMetaData, final String tableName, final String columnName) throws SQLException {
        ResultSet pkResultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
        boolean isPk = false;
        while (pkResultSet.next()) {
            final String name = pkResultSet.getString("COLUMN_NAME");
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
     * @param tables          the tables
     * @param withBuilder     the with builder
     * @param classNameSuffix the class name suffix
     * @throws CodegenException the codegen exception
     */
    void generateEntity(final List<Table> tables, final boolean withBuilder, String classNameSuffix) throws CodegenException {
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }// if
        Template entityTemplate;

        if (withBuilder) {
            entityTemplate = Velocity.getTemplate("templates/entity_with_builder.vm");
        } else {
            entityTemplate = Velocity.getTemplate("templates/entity.vm");
        }// if-else

        try {
            VELOCITY_CONTEXT.put(PACKAGE_NAME, ENTITY_PACKAGE);
            VELOCITY_CONTEXT.put(CLASS_NAME_SUFFIX, classNameSuffix);
            for (Table table : tables) {

                VELOCITY_CONTEXT.put(CLASS_NAME, table.getName());
                VELOCITY_CONTEXT.put(FIELDS, table.getColumns());

                final StringWriter writer = new StringWriter();
                entityTemplate.merge(VELOCITY_CONTEXT, writer);
                final String dir = StringUtils
                        .replaceEach((String) VELOCITY_CONTEXT.get(PACKAGE_NAME), new String[]{"."}, new String[]{File.separator});
                final File dirFile = new File(dir);
                if (dirFile.exists()) {
                    FileUtils.forceMkdir(dirFile);
                }// if

                FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + classNameSuffix + JAVA),
                        writer.toString());
                IOUtils.closeQuietly(writer);
            }// for
        } catch (Exception e) {
            throw new CodegenException(e);
        }// try-catch
    }// generateEnity()

    /**
     * Generate row mapper.
     *
     * @param tables the tables
     * @throws CodegenException the codegen exception
     */
    void generateRowMapper(final List<Table> tables) throws CodegenException {
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }
        try {
            final Template template = Velocity.getTemplate("templates/rowmapper.vm");
            VELOCITY_CONTEXT.put(PACKAGE_NAME, RM_PACKAGE);
            VELOCITY_CONTEXT.put(ENTITY_PACKAGE_NAME, ENTITY_PACKAGE);

            for (Table table : tables) {
                VELOCITY_CONTEXT.put(CLASS_NAME, table.getName());
                VELOCITY_CONTEXT.put("mapper_class_name", table.getName());
                VELOCITY_CONTEXT.put(FIELDS, table.getColumns());

                final StringWriter writer = new StringWriter();
                template.merge(VELOCITY_CONTEXT, writer);
                final String dir = StringUtils
                        .replaceEach((String) VELOCITY_CONTEXT.get(PACKAGE_NAME), new String[]{"."}, new String[]{File.separator});
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
     * Generate statement creator.
     *
     * @param tables        the tables
     * @param statementType the statement type
     * @throws CodegenException the codegen exception
     */
    void generateStatementCreator(List<Table> tables, StatementType statementType) throws CodegenException {
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }// if

        Template template = null;
        String tmpTable = "";
        VELOCITY_CONTEXT.put(PACKAGE_NAME, SC_PACKAGE);
        try {
            if (statementType == StatementType.INSERT) {
                template = Velocity.getTemplate("templates/insert_statement_creator.vm");
            } else {
                template = Velocity.getTemplate("templates/update_statement_creator.vm");
            }// if-else

            for (Table table : tables) {
                tmpTable = table.getName();
                VELOCITY_CONTEXT.put(CLASS_NAME, table.getName());
                VELOCITY_CONTEXT.put("mapper_class_name", table.getName());
                VELOCITY_CONTEXT.put(FIELDS, table.getColumns());

                @SuppressWarnings("unchecked") final Collection<Column> pkColumns = CollectionUtils.select(table.getColumns(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Column) object).isPrimaryKey();
                    }
                });
                @SuppressWarnings("unchecked") final Collection<Column> noPKColumns = CollectionUtils.selectRejected(table.getColumns(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Column) object).isPrimaryKey();
                    }
                });

                VELOCITY_CONTEXT.put(PK_COLUMNS, pkColumns);
                VELOCITY_CONTEXT.put(NO_PK_COLUMNS, noPKColumns);

                List<String> queryList = new ArrayList<>(1);

                if (statementType == StatementType.INSERT) {
                    VELOCITY_CONTEXT.put(FIELDS, CollectionUtils.selectRejected(table.getColumns(), new Predicate() {

                        @Override
                        public boolean evaluate(Object object) {
                            final Column column = (Column) object;
                            return (column.isPrimaryKey() && column.isIdentity());
                        }
                    }));

                    queryList.add(String.format("INSERT INTO %s (", table.getTableName()));

                    // for column name
                    final List<String> columns = new ArrayList<>(1);

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
                        .replaceEach((String) VELOCITY_CONTEXT.get(PACKAGE_NAME), new String[]{"."}, new String[]{File.separator});
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
     * @param tables          the tables
     * @param classNameSuffix the class name suffix
     * @throws CodegenException the codegen exception
     */
    void generateDAO(final List<Table> tables, String classNameSuffix) throws CodegenException {
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }// if
        Template entityTemplate = Velocity.getTemplate("templates/dao.vm");

        try {
            VELOCITY_CONTEXT.put(PACKAGE_NAME, DAO_PACKAGE);
            VELOCITY_CONTEXT.put(CLASS_NAME_SUFFIX, classNameSuffix);
            VELOCITY_CONTEXT.put(ENTITY_PACKAGE_NAME, ENTITY_PACKAGE);
            for (Table table : tables) {

                @SuppressWarnings("unchecked") final Collection<Column> pkColumns = CollectionUtils.select(table.getColumns(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Column) object).isPrimaryKey();
                    }
                });

                if (CollectionUtils.isNotEmpty(pkColumns)) {
                    VELOCITY_CONTEXT.put(PK_COLUMNS, pkColumns);

                    VELOCITY_CONTEXT.put("find_one_method_agrs", CollectionUtils.collect(pkColumns, new Transformer() {

                        @Override
                        public Object transform(Object input) {
                            final Column column = (Column) input;
                            return String.format("%s %s", column.getDataType(), StringUtils.uncapitalize(column.getName()));
                        }
                    }));
                }
                VELOCITY_CONTEXT.put(CLASS_NAME, table.getName());
                VELOCITY_CONTEXT.put(FIELDS, table.getColumns());

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
                        .replaceEach((String) VELOCITY_CONTEXT.get(PACKAGE_NAME), new String[]{"."}, new String[]{File.separator});
                final File dirFile = new File(dir);
                if (dirFile.exists()) {
                    FileUtils.forceMkdir(dirFile);
                }// if

                FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + classNameSuffix + JAVA),
                        writer.toString());
                IOUtils.closeQuietly(writer);
            }// for
        } catch (Exception e) {
            throw new CodegenException(e);
        }// try-catch
    }// generateEnity()

    /**
     * Generate dao.
     *
     * @param tables          the tables
     * @param classNameSuffix the class name suffix
     * @throws CodegenException the codegen exception
     */
    void generateDAOImpl(final List<Table> tables, String classNameSuffix) throws CodegenException {
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }// if
        final Template entityTemplate = Velocity.getTemplate("templates/dao_impl.vm");

        try {
            VELOCITY_CONTEXT.put(PACKAGE_NAME, DAO_IMPL_PACKAGE);
            VELOCITY_CONTEXT.put(CLASS_NAME_SUFFIX, classNameSuffix);
            VELOCITY_CONTEXT.put(ENTITY_PACKAGE_NAME, ENTITY_PACKAGE);
            VELOCITY_CONTEXT.put("dao_package_name", DAO_PACKAGE);
            VELOCITY_CONTEXT.put("sc_package_name", SC_PACKAGE);
            VELOCITY_CONTEXT.put("dao_class_name_suffix", "DAO");

            for (Table table : tables) {

                @SuppressWarnings("unchecked") final Collection<Column> pkColumns = CollectionUtils.select(table.getColumns(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Column) object).isPrimaryKey();
                    }
                });

                if (CollectionUtils.isNotEmpty(pkColumns)) {
                    VELOCITY_CONTEXT.put(PK_COLUMNS, pkColumns);
                }

                @SuppressWarnings("unchecked") final Collection<Column> noPKColumns = CollectionUtils.selectRejected(table.getColumns(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Column) object).isPrimaryKey();
                    }
                });

                if (CollectionUtils.isNotEmpty(pkColumns)) {
                    VELOCITY_CONTEXT.put(NO_PK_COLUMNS, noPKColumns);
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

                VELOCITY_CONTEXT.put(CLASS_NAME, table.getName());
                VELOCITY_CONTEXT.put(FIELDS, table.getColumns());
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
                        .replaceEach((String) VELOCITY_CONTEXT.get(PACKAGE_NAME), new String[]{"."}, new String[]{File.separator});
                final File dirFile = new File(dir);
                if (dirFile.exists()) {
                    FileUtils.forceMkdir(dirFile);
                }// if

                FileUtils.writeStringToFile(new File(dirFile.getAbsolutePath(), StringUtils.capitalize(table.getName()) + classNameSuffix + JAVA),
                        writer.toString());
                IOUtils.closeQuietly(writer);
            }// for
        } catch (Exception e) {
            throw new CodegenException(e);
        }// try-catch
    }// generateEnity()

    /**
     * List tables list.
     *
     * @return the list
     */
    public List<Table> listTables() {
        List<Table> tables;
        try {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(DATA_SOURCE);
            tables = jdbcTemplate.execute(new ConnectionCallback<List<Table>>() {

                @Override
                public List<Table> doInConnection(Connection con) {
                    List<Table> tableList = null;
                    try {
                        tableList = discoverTables(CONFIGURATION.getString("codegen.table.pattern"), con);
                    } catch (CodegenException e) {
                        log.error("error occurred while ", e);
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
     * @throws CodegenException the codegen exception
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
            log.info(stopWatch.prettyPrint());
        }// finally
    }

    /**
     * The enum Statement type.
     */
    public enum StatementType {

        /**
         * Insert statement type.
         */
        INSERT,
        /**
         * Update statement type.
         */
        UPDATE
    }

}
