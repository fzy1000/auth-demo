package com.demo.authapp.controllers;

import com.demo.authapp.models.User;
import com.demo.authapp.repositories.RoleRepository;
import com.demo.authapp.repositories.SessionRepository;
import com.demo.authapp.repositories.UserRepository;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionsController {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionsController(SessionRepository sessionRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public String create(@RequestBody final User user) {
        User existUser = userRepository.getUser(user.getName());
        String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        if (null != existUser && existUser.getPassword().equals(pwd)) {
            return sessionRepository.create(user);
        }
        return "Authorization Fail";
    }

    @PostMapping(value = "delete")
    public String update(String sessionValue) {
        return sessionRepository.delete(sessionValue);
    }

}
