package com.nokia.service;

import com.nokia.dao.RoleDao;
import com.nokia.dao.UserDao;
import com.nokia.model.Role;
import com.nokia.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Override
    public void saveRole(String role) {
        if(role.toLowerCase().equals("admin"))
            roleDao.save(new Role("ROLE_ADMIN"));
        else
            roleDao.save(new Role("ROLE_USER"));
    }

    @Override
    public void saveUser(String username, String password, String permission) throws NoSuchAlgorithmException {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if(permission.toLowerCase().equals("admin")) {
            userDao.save(new User(username, passwordEncoder.encode(password), roleDao.findById((long) 1)));
        }
        else{
            userDao.save(new User(username, passwordEncoder.encode(password), roleDao.findById((long) 2)));
        }
    }

    @Override
    public boolean usernameIsAvailable(String username) {
        return userDao.findByUsername(username) == null;
    }

    @Override
    public void deleteAccount(String username) {
        userDao.deleteUser(username);
    }

    @Override
    public List<String> getUsers() {

        List<String> users = new ArrayList<>();

        for (User user : userDao.findAll()){
            users.add(user.getUsername());
        }
        return users;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if(user == null){
            System.out.println("............user not found...........");
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
