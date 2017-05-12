package com.nokia.controller;

import com.nokia.controller.helper.ManagerUtil;
import com.nokia.service.provider.FrontendDataProvider;
import com.nokia.model.JDBCQuery;
import com.nokia.model.User;
import com.nokia.service.DatabaseURLService;
import com.nokia.service.JDBCQueryService;
import com.nokia.service.LogService;
import com.nokia.service.UserService;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */

@Controller
public class DatabaseManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private JDBCQueryService jdbcQueryService;

    @Autowired
    private LogService logService;

    @Autowired
    private DatabaseURLService databaseURLService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private FrontendDataProvider frontendDataProvider;

    @Autowired
    private ManagerUtil managerUtil;

    @RequestMapping(value = "/executeStatement", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String executeStatement(@RequestParam String statement, Principal principal)
            throws SQLException, ClassNotFoundException {

        long start = System.currentTimeMillis();

        User user = (User) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        frontendDataProvider.setQuery(statement);

        List<String> columnHeader;
        List<List<String>> columnHeaders = new ArrayList<>();
        Map<String, List<List<String>>> json = new HashMap<>();

        JDBCQuery jdbcQuery = jdbcQueryService.run(statement, frontendDataProvider.getDbusername(), frontendDataProvider.getDbpassword(),
                frontendDataProvider.getConnectedDatabase(), frontendDataProvider.getHostname());

        managerUtil.getJSONQueryResults(statement, user, columnHeaders, json, jdbcQuery);

        long end = System.currentTimeMillis();
        System.out.println("Executed in" + (end - start) / 1000 + "seconds!");

        return JSONValue.toJSONString(json);
    }

    @RequestMapping(value = "/checkConnection")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String checkConnection(@RequestParam String username, @RequestParam String password,
                                  @RequestParam String database, RedirectAttributes redirectAttributes)
            throws SQLException, ClassNotFoundException {

        JDBCQuery jdbcQuery;
        if(!database.equals("junit") || !database.equals("junit_do")) {
            frontendDataProvider.setHostname(databaseURLService.getDatabaseUrl(database));
        }

        else {
            frontendDataProvider.setHostname(httpServletRequest.getRemoteAddr() + ":3306");
        }
        jdbcQuery = jdbcQueryService.getConnection(username, password, database, frontendDataProvider.getHostname());
        if (jdbcQuery.getMessage().equals("Connection successful!")) {
            frontendDataProvider.setDbusername(username);
            frontendDataProvider.setDbpassword(password);
            frontendDataProvider.setConnectedDatabase(database);
            frontendDataProvider.setDbusername(password);
        }
        // }
        frontendDataProvider.markSelectedDatabse();

        redirectAttributes.addFlashAttribute("statement", frontendDataProvider.getQuery());
        redirectAttributes.addFlashAttribute("databases", frontendDataProvider.getAvailableDatabases());
        redirectAttributes.addFlashAttribute("sqlStatement", jdbcQuery);

        System.out.println(jdbcQuery.getMessage());
        System.out.println(jdbcQuery.getRows());

        return "redirect:/sqlTransaction";

    }

    @RequestMapping(value = "/stopConnection")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String stopConnection(ModelMap modelMap, RedirectAttributes redirectAttributes) {

        JDBCQuery jdbcQuery = new JDBCQuery();
        if (frontendDataProvider.getConnectedDatabase() != null && !frontendDataProvider.getConnectedDatabase().equals("")) {
            jdbcQuery.setRows(-3);
            jdbcQuery.setMessage("Disconnected!");
        }

        else {
            jdbcQuery.setRows(-4);
            jdbcQuery.setMessage("No connection!");
        }

        redirectAttributes.addFlashAttribute("statement", frontendDataProvider.getQuery());
        redirectAttributes.addFlashAttribute("databases", frontendDataProvider.getAvailableDatabases());
        redirectAttributes.addFlashAttribute("sqlStatement", jdbcQuery);

        return "redirect:/sqlTransaction";
    }
}
