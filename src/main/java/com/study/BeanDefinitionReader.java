package com.study;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions() throws ParserConfigurationException;
}
