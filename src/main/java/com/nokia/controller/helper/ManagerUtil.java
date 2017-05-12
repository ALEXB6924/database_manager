package com.nokia.controller.helper;

import com.nokia.model.JDBCQuery;
import com.nokia.model.User;
import com.nokia.service.DatabaseURLService;
import com.nokia.service.JDBCQueryService;
import com.nokia.service.LogService;
import com.nokia.service.UserService;
import com.nokia.service.provider.FrontendDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
    private FrontendDataProvider frontendDataProvider;

    public void getJSONQueryResults(@RequestParam String statement, User user, List<List<String>> columnHeaders, Map<String, List<List<String>>> json, JDBCQuery jdbcQuery) {
        List<String> columnHeader;
        if (jdbcQuery.getRows() > 0) {
            String date = logService.getDate();
            logService.saveLog(user.getUsername(), frontendDataProvider.getDbusername(), frontendDataProvider.getConnectedDatabase(), statement, date);

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
    }
}
