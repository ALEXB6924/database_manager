package com.nokia.service;

import com.nokia.model.DatabaseURL;

import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public interface DatabaseURLService {
    List<DatabaseURL> getDatabaseConnections();
    void saveDatabaseConnection(String name, String path);
    String getDatabaseUrl(String database);
}
