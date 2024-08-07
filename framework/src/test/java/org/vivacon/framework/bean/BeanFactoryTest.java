package org.vivacon.framework.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.vivacon.framework.core.ClassScanner;

import java.lang.annotation.Annotation;
import java.util.*;


class BeanFactoryTest {
    private BeanDefinition beanDefinition;
    private Map<Class<?>, Object> clazzToBean;
    private BeanFactory beanFactory;
    private Map<String, Set<Object>> bindNameToBeans;
    private ClassScanner classScanner;
    private MetadataExtractor metadataExtractor;
    private Set<Class<? extends Annotation>> managedAnnotations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        beanFactory = Mockito.spy(BeanFactory.getInstance());
    }

    @Test
    void getInstance() {
    }

    @Test
    void createBean() {
    }

    @Test
    public void testFindBeansViaBindingNames_Found() throws Exception {
        Set<String> bindingNames = new HashSet<>(Arrays.asList("name"));
        Map<String, Set<Object>> bindNameToBeans = new HashMap<>();
        Set<Object> beans = new HashSet<>(Arrays.asList(new Object()));
        bindNameToBeans.put("name", beans);
        Optional<Object> result = BeanFactory.getInstance().findBeansViaBindingNames(bindingNames, bindNameToBeans);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(beans.iterator().next(), result.get());
    }

    @Test
    public void testfindBeansViaBindingNames_NotFound() {
    }
}
