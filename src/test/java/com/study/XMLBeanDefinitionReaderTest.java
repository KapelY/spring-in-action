package com.study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XMLBeanDefinitionReaderTest {

    @DisplayName("When read xml config correct size is returned")
    @Test
    void readBeanDefinitions1() {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader();
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions("context.xml");

        assertEquals(3, beanDefinitions.size());
    }

    @DisplayName("When read xml config check id & class")
    @Test
    void readBeanDefinitions2() {
/*
    <bean id="popEmailSender" class="com.study.service.DefaultEmailSender">
        <property name="protocol" value="POP3" />
        <property name="port" value="899" />
    </bean>
*/
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader();
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions("context.xml");

        String expectedId = "popEmailSender";
        Optional<BeanDefinition> actualSender = beanDefinitions.stream()
                .filter(beanDefinition -> beanDefinition.getId().equals("popEmailSender")).findAny();
        assertEquals(expectedId, actualSender.get().getId());

        String expectedClass="com.study.service.DefaultEmailSender";
        assertEquals(expectedClass, actualSender.get().getClassName());
    }

    @DisplayName("When read xml config check properties")
    @Test
    void readBeanDefinitions3() {
/*
    <bean id="popEmailSender" class="com.study.service.DefaultEmailSender">
        <property name="protocol" value="POP3" />
        <property name="port" value="899" />
    </bean>
*/
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader();
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions("context.xml");

        String expectedProtocol = "POP3";
        Optional<BeanDefinition> actualSender = beanDefinitions.stream()
                .filter(beanDefinition -> beanDefinition.getId().equals("popEmailSender")).findAny();
        assertEquals(expectedProtocol, actualSender.get().getValueDependencies().get("protocol"));

        String expectedPort="899";
        assertEquals(expectedPort, actualSender.get().getValueDependencies().get("port"));
    }

    @DisplayName("When read xml config check reference value")
    @Test
    void readBeanDefinitions4() {
/*
    <bean id="helloUserService" class="com.study.service.UserService">
        <property name="message" value="Hello!" />
        <property name="emailSender" ref="popEmailSender" />
    </bean>
*/
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader();
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions("context.xml");

        System.out.println(beanDefinitions);
        String expectedSender = "popEmailSender";
        Optional<BeanDefinition> actualSender = beanDefinitions.stream()
                .filter(beanDefinition -> beanDefinition.getId().equals("helloUserService")).findAny();
        assertEquals(expectedSender, actualSender.get().getRefDependencies().get("emailSender"));


        String expectedMessage="Hello!";
        assertEquals(expectedMessage, actualSender.get().getValueDependencies().get("message"));
    }
}