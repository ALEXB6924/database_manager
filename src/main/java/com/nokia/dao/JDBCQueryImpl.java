package com.nokia.dao;

import com.nokia.controller.helper.beans.CSVDataHolder;
import com.nokia.model.JDBCQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.nokia.configuration.constants.ApplicationConstants.DATABASE_DRIVER;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Repository
public class JDBCQueryImpl implements JDBCQueryDao {

    @Autowired
    private CSVDataHolder csvDataHolder;

    @Override
    public JDBCQuery run(String query, String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException {

        Connection connection = null;
        List<String> columns = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        JDBCQuery jdbcQuery = new JDBCQuery();
        int rows = 0;
        String[] queries;
        String message = null;
        Class.forName(DATABASE_DRIVER);
        System.out.println(query);

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostname + "/" + database + "?zeroDateTimeBehavior=convertToNull", username,
                    password);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            if (checkIfMethodReturnsRows(query)) {
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
                queries = query.split("\n");
                if (queries.length > 1){
                    for (String string : queries){
                        rows = statement.executeUpdate(string);
                    }
                }
                else {
                    rows = statement.executeUpdate(query);
                    statement.close();
                }
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

        if (!columns.isEmpty()){
            csvDataHolder.setColumns(columns);
            csvDataHolder.setValues(values);
        }

        return new JDBCQuery(rows, message, columns, values);
    }

    private boolean checkIfMethodReturnsRows(String query) {
        return query.toLowerCase().startsWith("select") || query.toLowerCase().startsWith("explain")
                || query.toLowerCase().startsWith("show") || query.toLowerCase().startsWith("describe");
    }

    @Override
    public JDBCQuery getConnection(String username, String password, String database, String hostname)
            throws ClassNotFoundException, SQLException {

        Connection connection = null;
        List<String> columns = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        int rows = -3;
        String message = "Connection successful!";
        Class.forName(DATABASE_DRIVER);

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

    @Override
    public JDBCQuery runCustomScript(String query, String username, String password, String database, String hostname) throws SQLException, ClassNotFoundException {

        Connection connection = null;
        List<String> columns = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        String[] queries;
        int rows = 0;
        int allRows[];
        String message = "";
        Class.forName(DATABASE_DRIVER);
        System.out.println(query);

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostname + "/" + database + "?zeroDateTimeBehavior=convertToNull", username,
                    password);
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            queries = query.split("(?<=;)");

            for (String string : queries) {
                statement.addBatch(string);
            }
            allRows = statement.executeBatch();
            statement.close();
            for (int i : allRows) {
                rows = rows + i;
            }

        } catch (SQLException e) {
            message = e.getMessage();
            rows = -1;
        } finally {
            if (connection != null) {
                connection.commit();
                connection.close();
            }
        }

        return new JDBCQuery(rows, message, columns, values);
    }

    @Override
    public JDBCQuery runCSVUploadQuery(String query, String username, String password, String database, String hostname) throws SQLException, ClassNotFoundException {

        Connection connection = null;
        List<String> columns = new ArrayList<>();
        Statement statement;
        List<List<String>> values = new ArrayList<>();
        int rows = 0;
        String message = "";
        Class.forName(DATABASE_DRIVER);
        System.out.println(query);

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostname + "/" + database + "?zeroDateTimeBehavior=convertToNull", username,
                    password);
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            rows = statement.executeUpdate(query);

        } catch (SQLException e) {
            message = e.getMessage();
            rows = -1;
        } finally {
            if (connection != null) {
                connection.commit();
                connection.close();
            }
        }

        return new JDBCQuery(rows, message, columns, values);
    }

}
