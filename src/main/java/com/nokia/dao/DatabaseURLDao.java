package com.nokia.dao;

import com.nokia.model.DatabaseURL;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@SuppressWarnings("unchecked")
@Repository
public interface DatabaseURLDao extends CrudRepository<DatabaseURL, Long> {
    List<DatabaseURL> findAll();
    DatabaseURL save(DatabaseURL databaseConnections);
    DatabaseURL findByName(String name);
}
