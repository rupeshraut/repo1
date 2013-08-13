package com.dev.codegen.main;

/**
 * The Class Column.
 */
public class Column {

	/** The column name. */
	private String columnName;

	/** The name. */
	private String name;

	/** The data type. */
	private String dataType;

	/** The is null. */
	private boolean isNull;

	/** The is primary key. */
	private boolean isPrimaryKey;

	/** The is identity. */
	private boolean isIdentity;

	/** The size. */
	private int size;

	/**
	 * Instantiates a new column.
	 */
	public Column() {
	}

	/**
	 * Instantiates a new column.
	 * 
	 * @param columnName
	 *            the column name
	 * @param name
	 *            the name
	 */
	public Column(String columnName, String name) {
		super();
		this.columnName = columnName;
		this.name = name;
	}

	/**
	 * Instantiates a new column.
	 * 
	 * @param columnName
	 *            the column name
	 * @param name
	 *            the name
	 * @param dataType
	 *            the data type
	 */
	public Column(String columnName, String name, String dataType) {
		super();
		this.columnName = columnName;
		this.name = name;
		this.dataType = dataType;
	}

	/**
	 * Gets the column name.
	 * 
	 * @return the column name
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Sets the column name.
	 * 
	 * @param columnName
	 *            the new column name
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Gets the name.
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
	 * Gets the data type.
	 * 
	 * @return the data type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets the data type.
	 * 
	 * @param dataType
	 *            the new data type
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Checks if is null.
	 * 
	 * @return true, if is null
	 */
	public boolean isNull() {
		return isNull;
	}

	/**
	 * Sets the null.
	 * 
	 * @param isNull
	 *            the new null
	 */
	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	/**
	 * Checks if is primary key.
	 * 
	 * @return true, if is primary key
	 */
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	/**
	 * Sets the primary key.
	 * 
	 * @param isPrimaryKey
	 *            the new primary key
	 */
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	/**
	 * Checks if is identity.
	 * 
	 * @return true, if is identity
	 */
	public boolean isIdentity() {
		return isIdentity;
	}

	/**
	 * Sets the identity.
	 * 
	 * @param isIdentity
	 *            the new identity
	 */
	public void setIdentity(boolean isIdentity) {
		this.isIdentity = isIdentity;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 * 
	 * @param size
	 *            the new size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Column [name=");
		builder.append(name);
		builder.append(", dataType=");
		builder.append(dataType);
		builder.append(", isNull=");
		builder.append(isNull);
		builder.append(", isPrimaryKey=");
		builder.append(isPrimaryKey);
		builder.append(", isIdentity=");
		builder.append(isIdentity);
		builder.append(", size=");
		builder.append(size);
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
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + (isIdentity ? 1231 : 1237);
		result = prime * result + (isNull ? 1231 : 1237);
		result = prime * result + (isPrimaryKey ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + size;
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
		Column other = (Column) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (isIdentity != other.isIdentity)
			return false;
		if (isNull != other.isNull)
			return false;
		if (isPrimaryKey != other.isPrimaryKey)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

}
