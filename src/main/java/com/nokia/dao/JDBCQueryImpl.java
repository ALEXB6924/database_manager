package com.nokia.dao;

import com.nokia.model.JDBCQuery;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Repository
public class JDBCQueryImpl implements JDBCQueryDao {

    @Override
    public JDBCQuery run(String query, String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException {

        Connection connection = null;
        List<String> columns = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        int rows = 0;
        String message = null;
        Class.forName("com.mysql.jdbc.Driver");

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostname + "/" + database + "?zeroDateTimeBehavior=convertToNull", username,
                    password);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            if (query.toLowerCase().startsWith("select") || query.toLowerCase().startsWith("explain")
                    || query.toLowerCase().startsWith("show")) {
                ResultSet rs = statement.executeQuery(query);
                ResultSetMetaData column = rs.getMetaData();

                for (int i = 1; i <= column.getColumnCount(); i++)
                    columns.add(column.getColumnName(i));

                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (String col : columns) {
                        row.add(rs.getString(col));
                    }
                    values.add(row);
                }
                statement.close();
                rs.close();
                rows = -2;
            } else {
                rows = statement.executeUpdate(query);
                statement.close();
            }
        } catch (SQLException e) {
            message = e.getMessage();
            rows = -1;
        }

        finally {
            if (connection != null) {
                connection.commit();
                connection.close();
            }
        }

        return new JDBCQuery(rows, message, columns, values);
    }

    @Override
    public JDBCQuery getConnection(String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException {

        Connection connection = null;
        List<String> columns = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        int rows = -3;
        String message = "Connection successful!";
        Class.forName("com.mysql.jdbc.Driver");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + database, username, password);
            connection.setAutoCommit(false);
        }

        catch (SQLException e) {
            message = e.getMessage();
            rows = -4;

        }

        finally {
            if (connection != null) {
                connection.close();
            }
        }

        return new JDBCQuery(rows, message, columns, values);
    }
}
