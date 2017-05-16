package com.nokia.service;

import com.nokia.model.JDBCQuery;

import java.sql.SQLException;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public interface JDBCQueryService {
    JDBCQuery run(String query, String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException;
    JDBCQuery getConnection(String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException;
    JDBCQuery runCustomScript(String query, String username, String password, String database, String hostname)
            throws SQLException, ClassNotFoundException;
    JDBCQuery runCSVUploadQuery(String query, String username, String password, String database, String hostname)
            throws SQLException, ClassNotFoundException;
}
