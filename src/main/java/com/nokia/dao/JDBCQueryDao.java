package com.nokia.dao;

import com.nokia.model.JDBCQuery;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public interface JDBCQueryDao {
    JDBCQuery run(String query, String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException;
    JDBCQuery getConnection(String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException;
}
