package com.demo.authapp.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;

@Data
@EqualsAndHashCode
public class User {
    private String name;
    private String password;
    private HashSet<Role> roleSet;
}
