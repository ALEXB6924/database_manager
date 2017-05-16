package com.nokia.controller;

import com.nokia.controller.helper.ManagerUtil;
import com.nokia.controller.helper.beans.CSVDataHolder;
import com.nokia.controller.helper.beans.FrontendDataHolder;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLConnection;
import java.security.Principal;
import java.sql.*;
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
    private JDBCQueryService jdbcQueryService;
    @Autowired
    private DatabaseURLService databaseURLService;
    @Autowired
    private FrontendDataHolder frontendDataHolder;
    @Autowired
    private ManagerUtil managerUtil;

    @RequestMapping(value = "/executeStatement", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String executeStatement(@RequestParam String statement, Principal principal) throws SQLException, ClassNotFoundException {

        User user = (User) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        frontendDataHolder.setQuery(statement);

        JDBCQuery jdbcQuery = jdbcQueryService.run(statement, frontendDataHolder.getDbusername(), frontendDataHolder.getDbpassword(),
                frontendDataHolder.getConnectedDatabase(), frontendDataHolder.getHostname());
        if (frontendDataHolder.getConnectedDatabase() == null || frontendDataHolder.getConnectedDatabase().isEmpty()){
            jdbcQuery.setMessage("Could not establish connection!");
        }


        return JSONValue.toJSONString(managerUtil.getJSONQueryResults(statement, user, jdbcQuery));
    }

    @RequestMapping(value = "/checkConnection")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String checkConnection(@RequestParam String username, @RequestParam String password, @RequestParam String database,
                                  RedirectAttributes redirectAttributes) throws SQLException, ClassNotFoundException {

        JDBCQuery jdbcQuery;
        frontendDataHolder.setHostname(databaseURLService.getDatabaseUrl(database));
        jdbcQuery = jdbcQueryService.getConnection(username, password, database, frontendDataHolder.getHostname());
        if (jdbcQuery.getMessage().equals("Connection successful!")) {
            frontendDataHolder.setDbusername(username);
            frontendDataHolder.setDbpassword(password);
            frontendDataHolder.setConnectedDatabase(database);
            frontendDataHolder.setDbusername(password);
            frontendDataHolder.markSelectedDatabse();
        }

        frontendDataHolder.setJdbcQuery(jdbcQuery);

        return "redirect:/sqlTransaction";

    }

    @RequestMapping(value = "/stopConnection")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String stopConnection(ModelMap modelMap, RedirectAttributes redirectAttributes) {

        JDBCQuery jdbcQuery = new JDBCQuery();
        if (frontendDataHolder.getConnectedDatabase() != null && !frontendDataHolder.getConnectedDatabase().equals("")) {
            jdbcQuery.setRows(-3);
            jdbcQuery.setMessage("Disconnected!");
            frontendDataHolder.setConnectedDatabase("");
            frontendDataHolder.setDbusername("");
            frontendDataHolder.setDbpassword("");
            frontendDataHolder.setHostname("");
        }

        else {
            jdbcQuery.setRows(-4);
            jdbcQuery.setMessage("No connection!");
        }

        frontendDataHolder.setJdbcQuery(jdbcQuery);

        return "redirect:/sqlTransaction";
    }

    @RequestMapping(value = "/databaseStructureTables.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDatabaseTables()
            throws SQLException, ClassNotFoundException {

        Map<String, Object> columnMap = new HashMap<>();
        Map<String, List<Map<String, Object>>> json = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        if (frontendDataHolder.getConnectedDatabase() != null && !frontendDataHolder.getConnectedDatabase().isEmpty()) {

            return JSONValue.toJSONString(managerUtil.getDatabaseTables(json, list, columnMap));

        } else {

            columnMap = new HashMap<>();
            columnMap.put("table", "Not connected to database!");
            list.add(columnMap);
            json.put("databaseStructure", list);

        }

        return JSONValue.toJSONString(json);

    }


    @RequestMapping(value = "/databaseStructureTableColumns.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDatabaseTableColumns(@RequestParam String table)
            throws ClassNotFoundException, SQLException {

        return JSONValue.toJSONString(managerUtil.getTableColumns(table));
    }

    @RequestMapping(value = "/customScript", method = RequestMethod.POST)
    public String customScript(@RequestParam MultipartFile multipartFile, RedirectAttributes redirectAttributes)
            throws SQLException, ClassNotFoundException, IOException {

        frontendDataHolder.setJdbcQuery(managerUtil.runCustomScript(multipartFile));

        return "redirect:/sqlTransaction";
    }

    @RequestMapping(value = "/dumpDatabase",  method = RequestMethod.POST)
    public String dumpDatabase(HttpServletResponse httpServletResponse) throws IOException, InterruptedException {

        File fileToDownload = managerUtil.dumpDatabaseTofile();

        String mimeType = URLConnection.guessContentTypeFromName(fileToDownload.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "dump.txt"));
        httpServletResponse.setContentLength((int) fileToDownload.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileToDownload));
        FileCopyUtils.copy(inputStream, httpServletResponse.getOutputStream());

        return "redirect:/sqlTransaction";
    }
}
