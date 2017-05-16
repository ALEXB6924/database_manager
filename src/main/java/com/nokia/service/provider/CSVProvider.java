package com.nokia.service.provider;

import com.nokia.controller.helper.beans.CSVDataHolder;
import com.nokia.controller.helper.beans.FrontendDataHolder;
import com.nokia.model.JDBCQuery;
import com.nokia.service.JDBCQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.*;

import static com.nokia.configuration.constants.ApplicationConstants.ROOT_FILE_PATH;

/**
 * Created by alexandru_bobernac on 5/16/17.
 */
@Component
public class CSVProvider {

    @Autowired
    private FrontendDataHolder frontendDataHolder;
    @Autowired
    private JDBCQueryService jdbcQueryService;
    @Autowired
    private CSVDataHolder csvDataHolder;

    public JDBCQuery uploadToDatabase(MultipartFile multipartFile, String tableName) throws IOException, SQLException, ClassNotFoundException {

        int fileName = new Random().nextInt() + new Random().nextInt();
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
        List<String[]> csvLines = new ArrayList<>();
        String line;
        String prefix;
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter printWriter = new PrintWriter(ROOT_FILE_PATH + fileName + ".csv");
        while ((line = csvReader.readLine()) != null){
            csvLines.add(line.replaceAll("[^A-Za-z0-9, ]", "").split(","));
        }

        Iterator it = csvLines.iterator();
        while(it.hasNext()) {
            String[] value= (String[]) it.next();

            if (value == null || value.length == 0 || value[0].equals("")) {
                it.remove();
            }
        }

        for (int i = 1; i < csvLines.size(); i++){
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

        String script = "LOAD DATA LOCAL INFILE '" + ROOT_FILE_PATH + fileName + ".csv' " +
                "INTO TABLE " + tableName + " " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\n' ";

        JDBCQuery jdbcQuery = jdbcQueryService.runCSVUploadQuery(script, frontendDataHolder.getDbusername(), frontendDataHolder.getDbpassword(),
                frontendDataHolder.getConnectedDatabase(), frontendDataHolder.getHostname());
        if (frontendDataHolder.getConnectedDatabase() == null || frontendDataHolder.getConnectedDatabase().isEmpty()){
            jdbcQuery.setMessage("Could not establish connection!");
        }

        return jdbcQuery;
    }

    public File tableToCSV(int fileName) throws FileNotFoundException {

        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter printWriter = new PrintWriter(ROOT_FILE_PATH + fileName + ".csv");
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

        File fileToDownload = new File(ROOT_FILE_PATH + fileName + ".csv");

        return fileToDownload;
    }
}
