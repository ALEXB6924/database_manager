package com.nokia.controller.helper;

import com.nokia.model.JDBCQuery;
import com.nokia.model.User;
import com.nokia.service.LogService;
import com.nokia.controller.helper.beans.FrontendDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexandru_bobernac on 5/12/17.
 */
@Component
public class ManagerUtil {

    @Autowired
    private LogService logService;

    @Autowired
    private FrontendDataHolder frontendDataHolder;

    public Map<String, List<List<String>>> getJSONQueryResults(@RequestParam String statement, User user, JDBCQuery jdbcQuery) {
        List<String> columnHeader;
        List<List<String>> columnHeaders = new ArrayList<>();
        Map<String, List<List<String>>> json = new HashMap<>();

        if (jdbcQuery.getRows() > 0) {
            String date = logService.getDate();
            logService.saveLog(user.getUsername(), frontendDataHolder.getDbusername(), frontendDataHolder.getConnectedDatabase(), statement, date);

            System.out.println(jdbcQuery.getMessage());

            List<String> success = new ArrayList<>();
            List<List<String>> successMod = new ArrayList<>();

            success.add("Execution succesful. " + jdbcQuery.getRows() + " rows affected.");
            successMod.add(success);
            json.put("success", successMod);

        } else if (jdbcQuery.getMessage() == null) {
            for (String col : jdbcQuery.getColumns()) {
                columnHeader = new ArrayList<>();
                columnHeader.add(col);
                columnHeaders.add(columnHeader);
            }

            json.put("columns", columnHeaders);
            json.put("data", jdbcQuery.getValues());

        } else {
            List<String> error = new ArrayList<>();
            List<List<String>> errors = new ArrayList<>();
            error.add(jdbcQuery.getMessage());
            errors.add(error);
            json.put("error", errors);
        }
        return json;
    }

    public Map<String, List<Map<String, Object>>> getDatabaseTables(Map<String, List<Map<String, Object>>> json, List<Map<String, Object>> list,
                                                             Map<String, Object> columnMap) throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + frontendDataHolder.getHostname() + "/" + frontendDataHolder.getConnectedDatabase() +
                        "?zeroDateTimeBehavior=convertToNull", frontendDataHolder.getDbusername(), frontendDataHolder.getDbpassword());

        PreparedStatement ps = con.prepareStatement("SELECT table_name FROM information_schema.tables WHERE TABLE_SCHEMA=?");
        ps.setString(1, frontendDataHolder.getConnectedDatabase());
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            columnMap = new HashMap<>();
            columnMap.put("table", rs.getString(1));
            list.add(columnMap);

        }
        ps.close();
        rs.close();

        json.put("databaseStructure", list);

        return json;
    }

    public Map<String, List<Map<String, Object>>> getTableColumns(String table) throws SQLException, ClassNotFoundException {

        Map<String, Object> columnMap;
        Map<String, List<Map<String, Object>>> json = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://" + frontendDataHolder.getHostname() + "/" + frontendDataHolder.getConnectedDatabase(),
                frontendDataHolder.getDbusername(), frontendDataHolder.getDbpassword());
        PreparedStatement ps = connection.prepareStatement(
                "SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.columns WHERE TABLE_SCHEMA=? AND table_name = ?");
        ps.setString(1, frontendDataHolder.getConnectedDatabase());
        ps.setString(2, table);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            columnMap = new HashMap<>();
            columnMap.put("column", rs.getString(1));
            columnMap.put("column_type", rs.getString(2));
            list.add(columnMap);

        }

        ps.close();
        rs.close();

        json.put("databaseStructure", list);

        return json;
    }
}
