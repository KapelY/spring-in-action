package com.study.entity;

import lombok.Data;

@Data
public class User {
    Integer id;
    String email;
    String passwordHash;
}
