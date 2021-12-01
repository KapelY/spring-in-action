package com.study;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private String path;

    @Override
    public List<BeanDefinition> readBeanDefinitions() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document;
        List<BeanDefinition> beanDefinitions = new ArrayList<>();

        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(XMLBeanDefinitionReader.class.getClassLoader().getResourceAsStream(path));
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
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return beanDefinitions;
    }
}
