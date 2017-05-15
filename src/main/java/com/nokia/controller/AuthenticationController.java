package com.nokia.controller;

import com.nokia.controller.helper.beans.FrontendDataHolder;
import com.nokia.model.JDBCQuery;
import com.nokia.model.User;
import com.nokia.service.DatabaseURLService;
import com.nokia.service.JDBCQueryService;
import com.nokia.service.LogService;
import com.nokia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Controller
public class AuthenticationController {

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
    private FrontendDataHolder frontendDataHolder;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap modelMap) throws NoSuchAlgorithmException {

        frontendDataHolder.setHostname("");
        frontendDataHolder.setDbpassword("");
        frontendDataHolder.setDbusername("");
        frontendDataHolder.setConnectedDatabase("");
        frontendDataHolder.setQuery("");

        Object flash = httpServletRequest.getSession().getAttribute("flash");

        if(flash != null)
            modelMap.put("flash", flash);

        modelMap.put("user", new User());

//		userService.saveRole("admin");
//		userService.saveRole("user");
//
//		userService.saveUser("admin", "4dm1n", "admin");
//		userService.saveUser("user", "user", "user");
//        databaseURLService.saveDatabaseConnection("junit", "localhost:3306");
//
//		logService.saveLog("admin",	"dwms",	"twms",	"update StockUnit s set s.amount = 90",	"2016-08-31 10:25:54");
//		logService.saveLog("admin",	"dwms",	"twms",	"INSERT INTO sublager VALUES ('test', 55 )", "2016-08-31 10:34:41");
//		logService.saveLog("admin",	"dwms",	"twms",	"update StockUnit set availableAmount = amount", "2016-09-02 12:06:08");
//		logService.saveLog("admin",	"dwms",	"twms",	"UPDATE unitloadcapaArticle SET capa=90 WHERE articleId='R-PKW9'", "2016-09-13 12:20:25");

        return "login";
    }

    @RequestMapping(value = "/sqlTransaction")
    public String sqlTransaction(ModelMap modelMap) {

        frontendDataHolder.setQuery("");
        frontendDataHolder.setAvailableDatabases();

        if(!modelMap.containsAttribute("sqlStatement")) {
            modelMap.put("statement", frontendDataHolder.getQuery());
            modelMap.put("databases", frontendDataHolder.getAvailableDatabases());
            modelMap.put("sqlStatement", new JDBCQuery());
        }
        modelMap.put("connectedDatabase", frontendDataHolder.getConnectedDatabase());

        return "editor";
    }
}
