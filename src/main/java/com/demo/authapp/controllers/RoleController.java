package com.demo.authapp.controllers;


import com.demo.authapp.models.Role;
import com.demo.authapp.models.User;
import com.demo.authapp.repositories.RoleRepository;
import com.demo.authapp.repositories.SessionRepository;
import com.demo.authapp.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public RoleController(RoleRepository roleRepository, UserRepository userRepository, SessionRepository sessionRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }


    @PostMapping
    public String create(@RequestBody final Role role) {
        return roleRepository.create(role);
    }

    @RequestMapping(value = "{roleName}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String roleName) {
        return roleRepository.delete(roleName);
    }

    @RequestMapping(value = "{roleName}/{userName}", method = RequestMethod.PUT)
    public String update(@PathVariable String roleName, @PathVariable String userName) {
        User user = userRepository.getUser(userName);
        if (user == null) {
            return "User Not Exist";
        }
        return roleRepository.addUser(roleName, user);
    }


    @RequestMapping(value = "list", method = RequestMethod.POST)
    public List<String> list(String sessionValue) {
        String userName = sessionRepository.checkAuth(sessionValue);
        if (null == userName) {
            return null;
        }
        return roleRepository.findAll(userName);
    }

    @RequestMapping(value = "{roleName}", method = RequestMethod.POST)
    public Boolean checkRole(@PathVariable String roleName, String sessionValue) {
        String userName = sessionRepository.checkAuth(sessionValue);
        if (null == userName) {
            return false;
        }
        return roleRepository.checkRole(userName, roleName);
    }


}
