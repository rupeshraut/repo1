package com.dev.codegen.main;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The type Column.
 */
@Data
@AllArgsConstructor
public class Column {

    private String columnName;

    private String name;

    private String dataType;

    private boolean isNull;

    private boolean isPrimaryKey;

    private boolean isIdentity;

    private int size;

    /**
     * Instantiates a new Column.
     */
    public Column() {
    }

    /**
     * Instantiates a new Column.
     *
     * @param columnName the column name
     * @param name       the name
     */
    public Column(String columnName, String name) {
        super();
        this.columnName = columnName;
        this.name = name;
    }

    /**
     * Instantiates a new Column.
     *
     * @param columnName the column name
     * @param name       the name
     * @param dataType   the data type
     */
    public Column(String columnName, String name, String dataType) {
        super();
        this.columnName = columnName;
        this.name = name;
        this.dataType = dataType;
    }
}
