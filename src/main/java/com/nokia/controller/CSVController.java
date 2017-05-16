package com.nokia.controller;

import com.nokia.controller.helper.beans.CSVDataHolder;
import com.nokia.controller.helper.beans.FrontendDataHolder;
import com.nokia.model.JDBCQuery;
import com.nokia.service.JDBCQueryService;
import com.nokia.service.provider.CSVProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    @Autowired
    private CSVProvider csvProvider;

    @RequestMapping("/uploadCSV")
    public String uploadCSVTable(@RequestParam MultipartFile multipartFile, @RequestParam String tableName, RedirectAttributes redirectAttributes) throws IOException, SQLException, ClassNotFoundException {

        frontendDataHolder.setJdbcQuery(csvProvider.uploadToDatabase(multipartFile, tableName));
        redirectAttributes.addFlashAttribute("uploadMessage", frontendDataHolder.getJdbcQuery().getMessage());

        return "redirect:/sqlTransaction";
    }

    @RequestMapping("/exportCSV")
    public String exportToCSV(HttpServletResponse httpServletResponse) throws IOException {

        int fileName = new Random().nextInt() + new Random().nextInt();
        File fileToDownload = csvProvider.tableToCSV(fileName);

        String mimeType = URLConnection.guessContentTypeFromName(fileToDownload.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName + ".csv"));
        httpServletResponse.setContentLength((int) fileToDownload.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileToDownload));
        FileCopyUtils.copy(inputStream, httpServletResponse.getOutputStream());

        return "redirect:/sqlTransaction";
    }
}
