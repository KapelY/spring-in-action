package com.study;

import lombok.Data;

import java.util.Map;

@Data
public class BeanDefinition {
    private String id;
    private String className;
    private Map<String, String> valueDependencies;
    private Map<String, String> refDependencies;
}
