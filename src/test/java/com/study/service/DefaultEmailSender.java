package com.study.service;

import lombok.*;

import static java.text.MessageFormat.format;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@EqualsAndHashCode
public class DefaultEmailSender implements EmailSender {
    private Integer port;
    private String protocol;

    @Override
    public void sendEmail(String email, String message) {
        System.out.print(format("port:{}, protocol{} || ", port, protocol));
        System.out.println(format("Sending to {} message:{}", email, message));
    }
}
