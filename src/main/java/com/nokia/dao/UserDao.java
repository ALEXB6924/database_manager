package com.nokia.dao;

import com.nokia.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@SuppressWarnings("unchecked")
@Repository
public interface UserDao extends CrudRepository<User, Long> {
    @Query("SELECT u FROM  User u WHERE u.username = :username AND u.password = :password")
    User getUser(@Param("username") String username, @Param("password") String password);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.username = :username")
    void deleteUser(@Param("username")String username);

    User findByUsername(String username);
}
