package com.study.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class UserService {
    private String message;
    private EmailSender emailSender;

    public void sendEmail(String email) {
        emailSender.sendEmail(email, message);
    }

}
