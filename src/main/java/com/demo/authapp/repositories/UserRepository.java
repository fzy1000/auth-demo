package com.demo.authapp.repositories;

import com.demo.authapp.models.Role;
import com.demo.authapp.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final ConcurrentHashMap<String, User> userCache;


    UserRepository() {
        userCache = new ConcurrentHashMap<>();
    }

    public String create(User user) {
        if (userCache.containsKey(user.getName())) {
            return "User Already Exist";
        }
        synchronized (UserRepository.class) {
            //not sure DigestUtils is available or not . could use hashcode() instead.
            user.setPassword(
                    DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            user.setRoleSet(new HashSet<>());
            if (userCache.putIfAbsent(user.getName(), user) != null) {
                return "User Already Exist";
            }
        }
        return "Success";
    }

    public String delete(String userName) {
        User user = userCache.remove(userName);
        if (user != null) {
            HashSet<Role> roleSet = user.getRoleSet();
            roleSet.forEach(role -> role.getUserNameSet().remove(userName));
            return "Success";
        }
        return "User Not Exist";
    }

    public User getUser(String userName) {
        return userCache.get(userName);
    }

    public User setUser(String userName,User user) {
        return userCache.put(userName,user);
    }
}
