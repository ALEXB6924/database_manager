package com.nokia.dao;

import com.nokia.model.Log;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@SuppressWarnings("unchecked")
@Repository
public interface LogDao extends CrudRepository<Log, Long> {

    @Override
    Log save(Log log);

    @Query("SELECT l FROM Log l WHERE l.createDate BETWEEN :startDate AND :endDate")
    List<Log> findAll(@Param("startDate") String startDate, @Param("endDate") String endDate);
}