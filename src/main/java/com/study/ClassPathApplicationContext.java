package com.study;

import java.util.List;

public class ClassPathApplicationContext implements ApplicationContext{
    private BeanDefinitionReader beanDefinitionReader;
    private List<Bean> beans;
    private String[] paths;

    public ClassPathApplicationContext(String...paths) {
        this.paths = paths;
    }

    @Override
    public Object getBean(String id) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        return null;
    }
}
