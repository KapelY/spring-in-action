package com.study.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {
    private String message;
    private EmailSender emailSender;

    public void sendEmail(String email) {
        emailSender.sendEmail(email, message);
    }

}
