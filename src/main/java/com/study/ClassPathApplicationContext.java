package com.study;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ClassPathApplicationContext implements ApplicationContext {
    private BeanDefinitionReader beanDefinitionReader;
    private List<Bean> beans;
    private String[] paths;

    public ClassPathApplicationContext(String... paths) {
        this.paths = paths;
        initializeContext();
    }

    private void initializeContext() {
        List<BeanDefinition> beanDefinitions = getBeanDefinitions();
        beans = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beans);
        injectRefDependencies(beanDefinitions, beans);
    }

    private List<BeanDefinition> getBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        for (String path : paths) {
            beanDefinitionReader = new XMLBeanDefinitionReader(path); // fixme ioc from impl
            beanDefinitions.addAll(beanDefinitionReader.readBeanDefinitions());
        }
        return beanDefinitions;
    }

    @SneakyThrows
    private List<Bean> createBeans(List<BeanDefinition> beanDefinitions) {
        List<Bean> beans = new ArrayList<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String id = beanDefinition.getId();
            Object object = null;
            try {
                object = Class.forName(beanDefinition.getClassName())
                        .getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Bean bean = new Bean(id, object);
            beans.add(bean);
        }
        return beans;
    }

    private void injectValueDependencies(List<BeanDefinition> beanDefinitions, List<Bean> beans) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Map<String, String> valueDependencies = beanDefinition.getValueDependencies();
            if (!valueDependencies.isEmpty()) {
                Bean bean = getBeanById(beans, beanDefinition.getId()).orElse(null);

                checkBeanNotNull(beanDefinition, bean);

                for (Map.Entry<String, String> entry : valueDependencies.entrySet()) {
                    injectValue(bean, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void injectValue(Bean bean, String fieldName, String fieldValue) {
        try {
            Field beanValueField = bean.getClass().getDeclaredField("value");
            beanValueField.setAccessible(true);
            Object storedObject = beanValueField.get(bean);
            Field declaredField = storedObject.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);

            Class<?> type = declaredField.getType();
            if (Integer.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, Integer.valueOf(fieldValue));
            } else if (Double.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, Double.valueOf(fieldValue));
            } else if (Boolean.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, Boolean.valueOf(fieldValue));
            } else if (Short.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, Short.valueOf(fieldValue));
            } else if (Character.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, fieldValue.charAt(0));
            } else if (Long.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, Long.valueOf(fieldValue));
            }else if (Byte.class.isAssignableFrom(type)) {
                declaredField.set(storedObject, Byte.valueOf(fieldValue));
            } else {
                declaredField.set(storedObject, fieldValue);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) { // todo log me correctly
            e.printStackTrace();
        }
    }

    private Optional<Bean> getBeanById(List<Bean> beans, String id) {
        return beans.stream().filter(bean -> bean.getId().equals(id)).findFirst();
    }

    private void injectRefDependencies(List<BeanDefinition> beanDefinitions, List<Bean> beans) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();
            if (!refDependencies.isEmpty()) {
                Bean bean = getBeanById(beans, beanDefinition.getId()).orElse(null);

                checkBeanNotNull(beanDefinition, bean);

                for (Map.Entry<String, String> entry : refDependencies.entrySet()) {
                    Bean beanToInject = getBeanById(beans, entry.getValue()).orElse(null);
                    checkBeanNotNull(beanDefinition, beanToInject);
                    injectReference(bean, entry.getKey(), beanToInject.getValue());
                }
            }
        }
    }

    private void checkBeanNotNull(BeanDefinition beanDefinition, Bean bean) {
        if (bean == null) {
            throw new IllegalStateException("No bean found for beanDefinition - " + beanDefinition);
        }
    }

    private void injectReference(Bean bean,  String fieldName, Object object) {
        try {
            Field beanValueField = bean.getClass().getDeclaredField("value");
            beanValueField.setAccessible(true);
            Object storedObject = beanValueField.get(bean);
            Field declaredField = storedObject.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(storedObject, object);
        }catch (NoSuchFieldException | IllegalAccessException e) { // todo log me correctly
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id)) {
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getClass().equals(clazz)) {
                return clazz.cast(bean.getValue());
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id) && bean.getClass().equals(clazz)) {
                return clazz.cast(bean.getValue());
            }
        }
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        return beans.stream().map(Bean::getId).collect(Collectors.toList());
    }
}
