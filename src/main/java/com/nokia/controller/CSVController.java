package com.nokia.controller;

import com.nokia.model.JDBCQuery;
import com.nokia.service.JDBCQueryService;
import com.nokia.service.provider.FrontendDataProvider;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 13.05.2017.
 */
@Controller
public class CSVController {

    @Autowired
    private FrontendDataProvider frontendDataProvider;

    @Autowired
    private JDBCQueryService jdbcQueryService;

    @RequestMapping("/uploadCSV")
    public String csvTable(@RequestParam MultipartFile multipartFile, @RequestParam String tableName) throws IOException, SQLException, ClassNotFoundException {

        String queryStr = "INSERT INTO " + tableName;
        String queryFields = "(";
        String queryValues = "VALUES(";

        InputStream inputStream = multipartFile.getInputStream();
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String[]> csvLines = new ArrayList<>();
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter printWriter = new PrintWriter("/home/alex/" + multipartFile.getOriginalFilename());
        while ((line = csvReader.readLine()) != null){
            csvLines.add(line.replaceAll("[^A-Za-z0-9, ]", "").split(","));
        }

        String prefix = "";

        for (int i = 1; i < csvLines.size() - 1; i++){
            prefix = "";
            for (String row : csvLines.get(i)){
                stringBuilder.append(prefix);
                prefix = ",";
                stringBuilder.append(row);
            }
            stringBuilder.append("\n");
        }
        printWriter.write(stringBuilder.toString());
        printWriter.close();



//        for (int i = 0; i < csvLines.size() - 1; i++){
//            for (int j = 0; j < csvLines.get(i).length; j++){
//                if (i == 0){
//                    queryFields = queryFields + csvLines.get(i)[j];
//                    if (j + 1 != csvLines.size()){
//                        queryFields = queryFields + ",";
//                    }
//                }
//                else {
//                    queryValues = queryValues + csvLines.get(i)[j];
//                    if (j + 1 != csvLines.size()){
//                        queryValues = queryValues + ",";
//                    }
//                }
//            }
//        }
//
//        queryFields = queryFields + ") ";
//        queryValues = queryValues + ")";
//        queryStr = queryStr + queryFields + queryValues;

        String script = "LOAD DATA LOCAL INFILE '/home/alex/" + tableName + ".csv' " +
                "INTO TABLE datatest " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\n' ";

        JDBCQuery jdbcQuery = jdbcQueryService.run(script, frontendDataProvider.getDbusername(), frontendDataProvider.getDbpassword(),
                frontendDataProvider.getConnectedDatabase(), frontendDataProvider.getHostname());
        if (frontendDataProvider.getConnectedDatabase() == null || frontendDataProvider.getConnectedDatabase().isEmpty()){
            jdbcQuery.setMessage("Could not establish connection!");
        }

        return "redirect:/sqlTransaction";
    }
}
