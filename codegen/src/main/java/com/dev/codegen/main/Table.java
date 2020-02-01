package com.dev.codegen.main;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The type Table.
 */
@Data
public class Table {

    private String tableName;

    private String name;

    private Set<Column> columns = new LinkedHashSet<>(1);

    /**
     * Instantiates a new Table.
     */
    public Table() {
    }

    /**
     * Instantiates a new Table.
     *
     * @param tableName the table name
     */
    public Table(String tableName) {
        super();
        this.tableName = tableName;
    }

    /**
     * Instantiates a new Table.
     *
     * @param tableName the table name
     * @param name      the name
     */
    public Table(String tableName, String name) {
        super();
        this.tableName = tableName;
        this.name = name;

    }

    /**
     * Instantiates a new Table.
     *
     * @param name    the name
     * @param columns the columns
     */
    public Table(String name, Set<Column> columns) {
        super();
        this.name = name;
        this.columns = columns;
    }

}
