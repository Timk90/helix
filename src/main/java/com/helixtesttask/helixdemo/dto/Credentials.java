package com.helixtesttask.helixdemo.dto;

import lombok.Data;

import java.util.Set;

@Data
public class Credentials {
    private String username;
    private String password;
    private Set<Role> roles;

    public static enum Role {
        ADMIN,
        USER;
    }
}
