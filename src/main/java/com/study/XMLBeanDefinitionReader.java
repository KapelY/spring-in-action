package com.study;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private String path;

    @SneakyThrows
    @Override
    public List<BeanDefinition> readBeanDefinitions() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(XMLBeanDefinitionReader.class.getClassLoader().getResourceAsStream(path));

        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            BeanDefinition beanDefinition = null;

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getAttribute("id");
                String clazz = element.getAttribute("class");

                beanDefinition = new BeanDefinition();
                beanDefinition.setId(id);
                beanDefinition.setClassName(clazz);

                NodeList propertyList = node.getChildNodes();
                Map<String, String> valueDependencies = new HashMap<>();
                Map<String, String> refDependencies = new HashMap<>();

                for (int j = 0; j < propertyList.getLength(); j++) {
                    Node property = propertyList.item(j);
                    if (property.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) property;

                        String name = elem.getAttribute("name");
                        String value = elem.getAttribute("value");
                        String ref = elem.getAttribute("ref");

                        if (!value.isEmpty() && ref.isEmpty()) {
                            valueDependencies.put(name, value);
                        }
                        if (!ref.isEmpty() && value.isEmpty()) {
                            refDependencies.put(name, ref);
                        }
                    }
                }
                beanDefinition.setRefDependencies(refDependencies);
                beanDefinition.setValueDependencies(valueDependencies);
            }
            if (beanDefinition != null) {
                beanDefinitions.add(beanDefinition);
            }
        }
        return beanDefinitions;
    }
}
