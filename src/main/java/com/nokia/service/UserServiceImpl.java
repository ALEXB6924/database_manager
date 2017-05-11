package com.nokia.service;

import com.nokia.model.User;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
public class UserServiceImpl implements UserService {
    @Override
    public void saveUser(String username, String password, String permission) throws NoSuchAlgorithmException {

    }

    @Override
    public boolean usernameIsAvailable(String username) {
        return false;
    }

    @Override
    public void deleteAccount(String username) {

    }

    @Override
    public List<String> getUsers() {
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public void saveRole(String role) {

    }
}
