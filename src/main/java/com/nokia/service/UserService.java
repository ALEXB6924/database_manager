package com.nokia.service;

import com.nokia.model.User;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public interface UserService {
    void saveUser(String username, String password, String permission) throws NoSuchAlgorithmException;
    boolean usernameIsAvailable(String username);
    void deleteAccount(String username);
    List<String> getUsers();
    User findByUsername(String username);
    void saveRole(String role);
}
