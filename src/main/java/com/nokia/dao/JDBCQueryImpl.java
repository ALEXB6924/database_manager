package com.nokia.dao;

import com.nokia.model.JDBCQuery;

import java.sql.SQLException;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public class JDBCQueryImpl implements JDBCQueryDao {
    @Override
    public JDBCQuery run(String query, String username, String password, String database, String hostname) throws ClassNotFoundException, SQLException {
        return null;
    }

    @Override
    public JDBCQuery getConnection(String username, String password, String database, String hostname) throws ClassNotFoundException, SQLException {
        return null;
    }
}
