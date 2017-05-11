package com.nokia.service;

import com.nokia.dao.DatabaseURLDao;
import com.nokia.model.DatabaseURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Service
public class DatabaseURLServiceImpl implements DatabaseURLService{

    @Autowired
    private DatabaseURLDao databaseConnectionsDao;

    @Override
    public List<DatabaseURL> getDatabaseConnections() {
        return databaseConnectionsDao.findAll();
    }

    @Override
    public void saveDatabaseConnection(String name, String path) {
        DatabaseURL databaseConnections = new DatabaseURL(name, path);
        databaseConnectionsDao.save(databaseConnections);
    }

    @Override
    public String getDatabaseUrl(String database) {
        return databaseConnectionsDao.findByName(database).getHostname();
    }
}
