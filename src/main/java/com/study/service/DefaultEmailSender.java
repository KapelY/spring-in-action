package com.study.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static java.text.MessageFormat.format;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class DefaultEmailSender implements EmailSender {
    private Integer port;
    private String protocol;

    @Override
    public void sendEmail(String email, String message) {
        System.out.print(format("port:{}, protocol{} || ", port, protocol));
        System.out.println(format("Sending to {} message:{}", email, message));
    }
}
