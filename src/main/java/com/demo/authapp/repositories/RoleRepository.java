package com.demo.authapp.repositories;

import com.demo.authapp.models.Role;
import com.demo.authapp.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class RoleRepository {
    private final ConcurrentHashMap<String, Role> roleCache;

    private final UserRepository userRepository;

    RoleRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        roleCache = new ConcurrentHashMap<>();
    }

    public String create(Role role) {
        if (roleCache.containsKey(role.getRoleName())) {
            return "Role Already Exists";
        }
        synchronized (RoleRepository.class) {
            role.setUserNameSet(new HashSet<>());
            if (roleCache.putIfAbsent(role.getRoleName(), role) != null) {
                return "Role Already Exists";
            }
        }
        return "Success";
    }

    public String delete(String roleName) {
        Role role = roleCache.remove(roleName);
        if (role != null) {
            role.getUserNameSet().forEach(userName -> {
                User user = userRepository.getUser(userName);
                user.getRoleSet().remove(role);
                userRepository.setUser(userName, user);
            });
            return "Success";
        }
        return "Role Not Exist";
    }

    public String addUser(String roleName, User user) {
        Role role = roleCache.get(roleName);
        if (role == null) {
            return "Role Not Exist";
        }
        role.getUserNameSet().add(user.getName());
        List<String> list = user.getRoleSet().stream().map(Role::getRoleName).collect(Collectors.toList());
        if (list.contains(role.getRoleName())) {
            return "Success";
        }
        user.getRoleSet().add(role);
        return "Success";
    }

    public Boolean checkRole(String userName, String roleName) {
        Role role = roleCache.get(roleName);
        if (null == role) {
            return false;
        }
        return role.getUserNameSet().contains(userName);
    }

    public List<String> findAll(String userName) {
        User user = userRepository.getUser(userName);
        if (null == user) {
            return null;
        }
        return user.getRoleSet().stream().map(Role::getRoleName).collect(Collectors.toList());
    }
}
