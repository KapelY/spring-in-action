package com.study;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ClassPathApplicationContext implements ApplicationContext {
    @Setter private BeanDefinitionReader beanDefinitionReader;
    private List<Bean> beans;
    private String[] paths;

    public ClassPathApplicationContext(String... paths) {
        this(new XMLBeanDefinitionReader(), paths);
    }

    public ClassPathApplicationContext(BeanDefinitionReader beanDefinitionReader, String... paths) {
        this.paths = paths;
        this.beanDefinitionReader = beanDefinitionReader;
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
            beanDefinitions.addAll(beanDefinitionReader.readBeanDefinitions(path));
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
                    injectValueBySetter(bean, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void injectValueBySetter(Bean bean, String fieldName, String fieldValue) {
        try {
            Field beanValueField = bean.getClass().getDeclaredField("value");
            beanValueField.setAccessible(true);
            Object storedObject = beanValueField.get(bean);
            Field declaredField = storedObject.getClass().getDeclaredField(fieldName);

            for (Method method : storedObject.getClass().getMethods()) {
                if (method.getName().startsWith("set")
                        && method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    Class<?> type = declaredField.getType();
                    if (Integer.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, Integer.valueOf(fieldValue));
                    } else if (Double.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, Double.valueOf(fieldValue));
                    } else if (Boolean.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, Boolean.valueOf(fieldValue));
                    } else if (Short.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, Short.valueOf(fieldValue));
                    } else if (Character.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, fieldValue.charAt(0));
                    } else if (Long.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, Long.valueOf(fieldValue));
                    } else if (Byte.class.isAssignableFrom(type)) {
                        method.invoke(storedObject, Byte.valueOf(fieldValue));
                    } else {
                        method.invoke(storedObject, fieldValue);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            log.error("No setters or any other problem when setting the value, the field {} wasn't set", fieldName, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Method Deprecated because it violates DI principle,
     * We should use setter methods instead! @Tolik T.
     *
     * @param bean - Object to be injected with @fieldValue
     * @param fieldName - String name of the field for injection
     * @param fieldValue - String value for injection
     */
    @Deprecated
    private void injectValueBySetValue(Bean bean, String fieldName, String fieldValue) {
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
            } else if (Byte.class.isAssignableFrom(type)) {
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
                    injectReferenceBySetter(bean, entry.getKey(), beanToInject.getValue());
                }
            }
        }
    }

    private void checkBeanNotNull(BeanDefinition beanDefinition, Bean bean) {
        if (bean == null) {
            log.error("Bean not found for beanDefinition - {}", beanDefinition);
            throw new IllegalStateException("Bean not found for beanDefinition - " + beanDefinition);
        }
    }

    private void injectReferenceBySetter(Bean bean, String fieldName, Object object) {
        try {
            Field beanValueField = bean.getClass().getDeclaredField("value");
            beanValueField.setAccessible(true);
            Object storedObject = beanValueField.get(bean);
            for (Method method : storedObject.getClass().getMethods()) {
                if (method.getName().startsWith("set") && method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    method.invoke(storedObject, object);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            log.warn("Error when injecting references", e);
        }
    }

    /**
     * Method Deprecated because it violates DI principle,
     * We should use setter methods instead! @Tolik T.
     *
     * @param bean - Object to be injected with @fieldValue
     * @param fieldName - String name of the field for injection
     * @param object - Object reference for injection
     */
    @Deprecated
    private void injectReference(Bean bean, String fieldName, Object object) {
        try {
            Field beanValueField = bean.getClass().getDeclaredField("value");
            beanValueField.setAccessible(true);
            Object storedObject = beanValueField.get(bean);
            Field declaredField = storedObject.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(storedObject, object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Error when injecting references", e);
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
        T result = null;
        int matchCounter = 0;

        for (Bean bean : beans) {
            if (bean.getValue().getClass() == clazz) {
                matchCounter++;
                result = clazz.cast(bean.getValue());
            }
        }
        if (matchCounter == 1) {
            return result;
        } else if (matchCounter == 0) {
            return null;
        }
        log.error("There is more than one bean matches class");
        throw new IllegalCallerException("There is more than one bean matches class");
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id) && bean.getValue().getClass().equals(clazz)) {
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
