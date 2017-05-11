package com.nokia.service;

import com.nokia.dao.JDBCQueryDao;
import com.nokia.model.JDBCQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Service
public class JDBCQueryServiceImpl implements JDBCQueryService {

    @Autowired
    private JDBCQueryDao statementDao;

    @Override
    public JDBCQuery run(String query, String username, String password, String database, String hostname) throws ClassNotFoundException, SQLException {
        return statementDao.run(query, username, password, database, hostname);
    }

    @Override
    public JDBCQuery getConnection(String username, String password, String database, String hostname) throws ClassNotFoundException, SQLException {
        return statementDao.getConnection(username, password, database, hostname);
    }
}
