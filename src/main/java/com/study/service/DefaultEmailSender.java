package com.study.service;

import lombok.AllArgsConstructor;

import static java.text.MessageFormat.format;

@AllArgsConstructor
public class DefaultEmailSender implements EmailSender {
    private int port;
    private int protocol;

    @Override
    public void sendEmail(String email, String message) {
        System.out.print(format("port:{}, protocol{} || ", port, protocol));
        System.out.println(format("Sending to {} message:{}", email, message));
    }
}
