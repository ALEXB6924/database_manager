package com.nokia.service;

import com.nokia.dao.LogDao;
import com.nokia.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao logDao;

    @Override
    public List<Log> getLogs(String startDate, String endDate) {

        return logDao.findAll(startDate, endDate);
    }

    @Override
    public void saveLog(String username, String databaseUser, String database, String statement, String date) {
        Log log = new Log(username, databaseUser, database, statement, date);
        logDao.save(log);
    }

    @Override
    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}
