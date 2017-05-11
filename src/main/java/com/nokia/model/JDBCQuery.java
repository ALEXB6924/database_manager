package com.nokia.model;

import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public class JDBCQuery {

    private int rows;
    private String message;
    private List<String> columns;
    private List<List<String>> values;

    public JDBCQuery() {
    }

    public JDBCQuery(int rows, String message, List<String> columns, List<List<String>> values) {
        this.rows = rows;
        this.message = message;
        this.columns = columns;
        this.values = values;
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SqlStatement{" +
                "rows=" + rows +
                ", message='" + message + '\'' +
                ", columns=" + columns +
                ", values=" + values +
                '}';
    }
}
