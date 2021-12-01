package com.study;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    public static final String ID = "id";
    public static final String CLASS = "class";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String REF = "ref";

    @Override
    public List<BeanDefinition> readBeanDefinitions(String path) {

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

                    String id = element.getAttribute(ID);
                    String clazz = element.getAttribute(CLASS);

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

                            String name = elem.getAttribute(NAME);
                            String value = elem.getAttribute(VALUE);
                            String ref = elem.getAttribute(REF);

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
            log.error("Error when parsing.", e);
            throw new RuntimeException(e);
        }
        return beanDefinitions;
    }
}
