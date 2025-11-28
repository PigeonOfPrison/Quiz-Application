package com.Dolkara.auth_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {

    private String username;

    private String password;

    private String email;
}
