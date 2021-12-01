package com.study;

import com.study.service.DefaultEmailSender;
import com.study.service.EmailSender;
import com.study.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathApplicationContextITest {
    private final ApplicationContext context =
            new ClassPathApplicationContext("context2.xml", "context3.xml");

    @Test
    @DisplayName("Check email sender object by id")
    void getBeanById() {
        EmailSender popEmailSenderActual = (EmailSender) context.getBean("popEmailSender");
        EmailSender popEmailSenderExpected = new DefaultEmailSender(899, "POP3");
        assertEquals(popEmailSenderActual, popEmailSenderExpected);

        UserService userServiceExpected = new UserService("Hello!", popEmailSenderActual);
        UserService userServiceActual = (UserService) context.getBean("helloUserService");
        assertEquals(userServiceExpected, userServiceActual);
    }

    @Test
    @DisplayName("Check bean by class name")
    void checkBeanByClassName() {
        EmailSender popEmailSenderActual = (EmailSender) context.getBean("popEmailSender");
        EmailSender popEmailSenderExpected = new DefaultEmailSender(899, "POP3");
        assertEquals(popEmailSenderActual, popEmailSenderExpected);

        UserService userServiceExpected = new UserService("Hello!", popEmailSenderActual);
        UserService userServiceActual = (UserService) context.getBean("helloUserService");
        assertEquals(userServiceExpected, userServiceActual);
    }

    @Test
    @DisplayName("Check bean by class")
    void checkBeanByClass() {
        DefaultEmailSender bean = context.getBean(DefaultEmailSender.class);
        assertNotNull(bean);
        assertEquals(bean.getClass(), DefaultEmailSender.class);
    }

    @Test
    @DisplayName("Check by id & class")
    void checkBeanByClassId() {
        UserService bean = context.getBean("byeUserService1", UserService.class);
        assertNotNull(bean);
        assertEquals(bean.getClass(), UserService.class);
    }

    @Test
    @DisplayName("Check all object id's in the context")
    void getBeanNames() {
        List<String> beanNames = context.getBeanNames();
        assertEquals(5, beanNames.size());
    }
}