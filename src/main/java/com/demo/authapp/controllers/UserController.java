package com.demo.authapp.controllers;


import com.demo.authapp.models.User;
import com.demo.authapp.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping
    public String create(@RequestBody final User user) {
        return userRepository.create(user);
    }

    @RequestMapping(value = "{userName}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String userName) {
        return userRepository.delete(userName);
    }

}
