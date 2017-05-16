package com.nokia.controller.helper.beans;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandru_bobernac on 5/15/17.
 */
@Component
public class CSVDataHolder {

    private List<String> columns;
    private List<List<String>> values;

    public CSVDataHolder() {
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }
}
