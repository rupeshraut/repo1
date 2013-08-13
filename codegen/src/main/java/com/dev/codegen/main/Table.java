package com.dev.codegen.main;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The Class Table.
 */
public class Table {

	/** The table name. */
	private String tableName;

	/** The name. */
	private String name;

	/** The columns. */
	private Set<Column> columns = new LinkedHashSet<Column>(1);

	/**
	 * Instantiates a new table.
	 */
	public Table() {
	}

	/**
	 * Instantiates a new table.
	 * 
	 * @param tableName
	 *            the table name
	 */
	public Table(String tableName) {
		super();
		this.tableName = tableName;
	}

	/**
	 * Instantiates a new table.
	 * 
	 * @param tableName
	 *            the table name
	 * @param name
	 *            the name
	 */
	public Table(String tableName, String name) {
		super();
		this.tableName = tableName;
		this.name = name;

	}

	/**
	 * Instantiates a new table.
	 * 
	 * @param name
	 *            the name
	 * @param columns
	 *            the columns
	 */
	public Table(String name, Set<Column> columns) {
		super();
		this.name = name;
		this.columns = columns;
	}

	/**
	 * Gets the table name.
	 * 
	 * @return the table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the table name.
	 * 
	 * @param tableName
	 *            the new table name
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * /** Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the columns.
	 * 
	 * @return the columns
	 */
	public Set<Column> getColumns() {
		return columns;
	}

	/**
	 * Sets the columns.
	 * 
	 * @param columns
	 *            the new columns
	 */
	public void setColumns(Set<Column> columns) {
		this.columns = columns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Table [tableName=");
		builder.append(tableName);
		builder.append(", name=");
		builder.append(name);
		builder.append(", columns=");
		builder.append(columns);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

}
