package com.nokia.service;

import com.nokia.model.Log;

import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public interface LogService {
    void saveLog(String username, String databaseUser, String database, String statement, String date);
    List<Log> getLogs(String startDate, String endDate);
    String getDate();
}
