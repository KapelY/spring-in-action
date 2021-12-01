package com.study.test;

import com.study.BeanDefinition;
import com.study.BeanDefinitionReader;
import com.study.XMLBeanDefinitionReader;
import lombok.SneakyThrows;

public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        BeanDefinitionReader beanDefinition = new XMLBeanDefinitionReader("context.xml");
        beanDefinition.readBeanDefinitions();

    }
}
