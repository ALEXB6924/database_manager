package com.nokia.controller;

import com.nokia.model.Log;
import com.nokia.model.User;
import com.nokia.service.LogService;
import com.nokia.service.UserService;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Controller
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @RequestMapping(value = "/getLogs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_USER')")
    public String getLogs(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {

        Map<String, Object> columnMap;
        Map<String, List<Map<String, Object>>> json = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        List<Log> logList = logService.getLogs(startDate + " 00:00:00", endDate + " 23:59:57");

        for (Log log : logList) {
            columnMap = new HashMap<>();
            columnMap.put("ID", log.getId());
            columnMap.put("Username", log.getUsername());
            columnMap.put("DatabaseUser", log.getDatabaseUser());
            columnMap.put("Database", log.getDatabase());
            columnMap.put("Statement", log.getStatement());
            columnMap.put("CreateDate", log.getCreateDate().substring(0, 19));
            list.add(columnMap);
        }
        json.put("logs", list);

        return JSONValue.toJSONString(json);
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String users(){

        List<String> usernameList = Arrays.asList("Username");
        List<List<String>> columns = new ArrayList<>();
        columns.add(usernameList);
        List<List<String>> userList = new ArrayList<>();
        for(String str : userService.getUsers()){
            userList.add(Arrays.asList(str));
        }

        Map<String, List<List<String>>> json = new HashMap<>();
        json.put("data", userList);
        json.put("columns", columns);

        return JSONValue.toJSONString(json);
    }

    // create user for application
    @RequestMapping(value = "/saveUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveUser(@RequestParam(value = "newUsername") String username,
                           @RequestParam(value = "passwd") String password, @RequestParam(value = "permission") String permission,
                           RedirectAttributes redirectAttributes, Principal principal) throws NoSuchAlgorithmException {

        User user = (User) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        boolean condition = userService.usernameIsAvailable(username)
                && (user.getRole().getId() == 1) && !username.isEmpty() && !password.isEmpty()
                && !permission.isEmpty();

        if (condition) {
            userService.saveUser(username, password, permission);
            redirectAttributes.addFlashAttribute("manageUserSuccess", "User " + username + " was successfully created!");

            return "redirect:/sqlTransaction";
        }
        redirectAttributes.addFlashAttribute("manageUserError", "User " + username + " already exists!");

        return "redirect:/sqlTransaction";
    }

    // delete user from application
    @RequestMapping(value = "/deleteUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteUser(@RequestParam(value = "userForDeletion") String username,
                             RedirectAttributes redirectAttributes, Principal principal) throws IOException {

        User user = (User) ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

        if (!userService.usernameIsAvailable(username) && !user.getUsername().equals(username)) {
            System.out.println("DELETING..........");
            userService.deleteAccount(username);
            redirectAttributes.addFlashAttribute("manageUserSuccess", "User " + username + " was successfully deleted");

            return "redirect:/sqlTransaction";
        }
        redirectAttributes.addFlashAttribute("manageUserError", "User " + username + " does not exist/is already deleted!");

        return "redirect:/sqlTransaction";
    }
}
