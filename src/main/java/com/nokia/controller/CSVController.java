package com.nokia.controller;

import com.nokia.controller.helper.beans.CSVDataHolder;
import com.nokia.controller.helper.beans.FrontendDataHolder;
import com.nokia.model.JDBCQuery;
import com.nokia.service.JDBCQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 13.05.2017.
 */
@Controller
public class CSVController {

    @Autowired
    private FrontendDataHolder frontendDataHolder;
    @Autowired
    private JDBCQueryService jdbcQueryService;
    @Autowired
    private CSVDataHolder csvDataHolder;

    @RequestMapping("/uploadCSV")
    public String csvTable(@RequestParam MultipartFile multipartFile, @RequestParam String tableName) throws IOException, SQLException, ClassNotFoundException {

        InputStream inputStream = multipartFile.getInputStream();
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String[]> csvLines = new ArrayList<>();
        String line;
        String prefix;
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter printWriter = new PrintWriter("/home/alex/" + multipartFile.getOriginalFilename());
        while ((line = csvReader.readLine()) != null){
            csvLines.add(line.replaceAll("[^A-Za-z0-9, ]", "").split(","));
        }

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

        String script = "LOAD DATA LOCAL INFILE '/home/alex/" + tableName + ".csv' " +
                "INTO TABLE datatest " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\n' ";

        JDBCQuery jdbcQuery = jdbcQueryService.run(script, frontendDataHolder.getDbusername(), frontendDataHolder.getDbpassword(),
                frontendDataHolder.getConnectedDatabase(), frontendDataHolder.getHostname());
        if (frontendDataHolder.getConnectedDatabase() == null || frontendDataHolder.getConnectedDatabase().isEmpty()){
            jdbcQuery.setMessage("Could not establish connection!");
        }

        return "redirect:/sqlTransaction";
    }

    @RequestMapping("/exportCSV")
    public String exportToCSV(HttpServletResponse httpServletResponse) throws IOException {
        String test = "";
        test = frontendDataHolder.getConnectedDatabase();

        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter printWriter = new PrintWriter("/home/alexandru_bobernac/Documents/export.csv");
        String prefix = "";
        for (String column : csvDataHolder.getColumns()){
            stringBuilder.append(prefix);
            prefix = ",";
            stringBuilder.append(column);
        }
        stringBuilder.append("\n");

        for (List<String> row : csvDataHolder.getValues()){
            prefix = "";
            for (String entry : row){
                stringBuilder.append(prefix);
                prefix = ",";
                stringBuilder.append(entry);
            }
            stringBuilder.append("\n");
        }
        printWriter.write(stringBuilder.toString());
        printWriter.close();

        File fileToDownload = new File("/home/alexandru_bobernac/Documents/export.csv");

        String mimeType = URLConnection.guessContentTypeFromName(fileToDownload.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "export.csv"));
        httpServletResponse.setContentLength((int) fileToDownload.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileToDownload));
        FileCopyUtils.copy(inputStream, httpServletResponse.getOutputStream());

        return "redirect:/sqlTransaction";
    }
}
