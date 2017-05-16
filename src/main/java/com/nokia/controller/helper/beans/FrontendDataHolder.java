package com.nokia.controller.helper.beans;

import com.nokia.model.DatabaseURL;
import com.nokia.model.JDBCQuery;
import com.nokia.service.DatabaseURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandru_bobernac on 5/12/17.
 */
@Component
public class FrontendDataHolder {

    @Autowired
    private DatabaseURLService databaseURLService;

    String query;
    Map<String, Boolean> availableDatabases = new HashMap<>();
    String connectedDatabase;
    String hostname;
    String dbusername;
    String dbpassword;
    JDBCQuery jdbcQuery;

    public JDBCQuery getJdbcQuery() {
        return jdbcQuery;
    }

    public void setJdbcQuery(JDBCQuery jdbcQuery) {
        this.jdbcQuery = jdbcQuery;
    }

    public FrontendDataHolder() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Boolean> getAvailableDatabases() {
        return availableDatabases;
    }

    public void setAvailableDatabases() {
        for (DatabaseURL databaseURL : databaseURLService.getDatabaseConnections()){
            availableDatabases.put(databaseURL.getName(), false);
        }
    }

    public void markSelectedDatabse(){
        availableDatabases.put(connectedDatabase, true);
    }

    public String getConnectedDatabase() {
        return connectedDatabase;
    }

    public void setConnectedDatabase(String connectedDatabase) {
        this.connectedDatabase = connectedDatabase;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDbusername() {
        return dbusername;
    }

    public void setDbusername(String dbusername) {
        this.dbusername = dbusername;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public void setDbpassword(String dbpassword) {
        this.dbpassword = dbpassword;
    }
}
