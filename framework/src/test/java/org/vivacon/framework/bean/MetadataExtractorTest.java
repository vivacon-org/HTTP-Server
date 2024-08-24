package org.vivacon.framework.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vivacon.framework.bean.annotation.Autowired;
import org.vivacon.framework.bean.annotation.Qualifier;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.core.event.ClearCacheEvent;
import org.vivacon.framework.core.event.EventBroker;
import org.vivacon.framework.web.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MetadataExtractorTest {
    private MetadataExtractor metadataExtractor;

    @BeforeEach
    public void setUp() {
        metadataExtractor = MetadataExtractor.getInstance();
        ClearCacheEvent clearCacheEvent = new ClearCacheEvent();
        EventBroker.getInstance().publish(clearCacheEvent);
        Set<String> predefinedBindings = new HashSet<>(Set.of("PredefinedClassName", "PredefinedInterfaceName"));
    }

    @Test
    public void test_getConstructorToInject_defaultCtor() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_DefaultCtor.class;
        Constructor<?> actualCtor = metadataExtractor.getConstructorToInject(clazz);
        Constructor<?> expectedCtor = clazz.getDeclaredConstructor();
        Assertions.assertEquals(actualCtor, expectedCtor);
    }

    @Test
    public void test_getConstructorToInject_onlyOneCtor() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_OnlyOneCtor.class;
        Constructor<?> actualCtor = metadataExtractor.getConstructorToInject(clazz);
        Constructor<?> expectedCtor = clazz.getDeclaredConstructor(ClazzService.class, StudentService.class);
        Assertions.assertEquals(actualCtor, expectedCtor);
    }

    @Test
    public void test_getConstructorToInject_manyCtorsWithOneAutowired() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_ManyCtorsWithOneAutowired.class;
        Constructor<?> actualCtor = metadataExtractor.getConstructorToInject(clazz);
        Constructor<?> expectedCtor = clazz.getDeclaredConstructor(ClazzService.class, StudentService.class);
        Assertions.assertEquals(actualCtor, expectedCtor);
    }

    @Test
    public void test_getConstructorToInject_manyCtorsWithNoAutowired() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_ManyCtorsNoAutowired.class;
        Assertions.assertThrows(IllegalArgumentException.class, () -> MetadataExtractor.getInstance().getConstructorToInject(clazz));
    }

    @Test
    public void test_getBeanBindingName_withCache() {
        Set<String> expectBindingName = new HashSet<>(Set.of("PredefinedClassName", "PredefinedInterfaceName"));
        Set<String> actualBindingName = metadataExtractor.getBeanBindingName(SchoolInCache.class);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_getBeanBindingName_withInterface() {
        Set<String> expectBindingName = new HashSet<>(Set.of("SchoolImpl", "School"));
        Set<String> actualBindingName = metadataExtractor.getBeanBindingName(SchoolImpl.class);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_getBeanBindingName_withQualifiers() {
        // Predefine cache values
        Set<String> expectBindingName = new HashSet<>(Set.of("School Qualifier", "School", "SchoolQualifer"));
        Set<String> actualBindingName = metadataExtractor.getBeanBindingName(SchoolQualifer.class);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_getDependencyToItsBindName_fromCtor() throws NoSuchMethodException {
        Constructor<?> constructor = SchoolController.class.getConstructor(ClazzService.class, StudentService.class, DepartmentService.class);
        Parameter paramClazz = constructor.getParameters()[0];
        Parameter paramStudent = constructor.getParameters()[1];
        Parameter paramDepartment = constructor.getParameters()[2];

        LinkedHashMap<Parameter, Set<String>> expectBindingName = new LinkedHashMap<>();
        expectBindingName.put(paramClazz, new HashSet<>(Set.of("Class Service")));
        expectBindingName.put(paramStudent, new HashSet<>(Set.of("StudentService", "Student Service")));
        expectBindingName.put(paramDepartment, new HashSet<>(Set.of("DepartmentService")));

        LinkedHashMap<Parameter, Set<String>> actualBindingName = MetadataExtractor.getInstance().getDependencyToItsBindNames(constructor);
        Assertions.assertEquals(expectBindingName, actualBindingName);

    }

    @Test
    public void test_getDependencyToItsBindName_fromFields() throws NoSuchFieldException {
        Class<?> clazzController = SchoolController.class;
        Field fieldClazz = clazzController.getDeclaredFields()[0];
        Field fieldStudent = clazzController.getDeclaredFields()[1];
        Field fieldDepartment = clazzController.getDeclaredFields()[2];

        LinkedHashMap<Field, Set<String>> expectBindingName = new LinkedHashMap<>();
        expectBindingName.put(fieldClazz, new HashSet<>(Set.of("ClazzService")));
        expectBindingName.put(fieldStudent, new HashSet<>(Set.of("StudentService", "Student Service")));
        expectBindingName.put(fieldDepartment, new HashSet<>(Set.of("DepartmentService")));

        LinkedHashMap<Field, Set<String>> actualBindingName = MetadataExtractor.getInstance().getDependencyToItsBindNames(clazzController);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_buildBeanDefinitions() throws NoSuchMethodException {
        List<Class<?>> componentClasses = Arrays.asList(SchoolController.class);

        Map<Class<?>, BeanDefinition> expectedBeanDefinition = new HashMap<>();

        Constructor<?> constructor = SchoolController.class.getConstructor(ClazzService.class, StudentService.class, DepartmentService.class);
        Parameter paramClazz = constructor.getParameters()[0];
        Parameter paramStudent = constructor.getParameters()[1];
        Parameter paramDepartment = constructor.getParameters()[2];
        LinkedHashMap<Parameter, Set<String>> parameterSetLinkedHashMap = new LinkedHashMap<>();
        parameterSetLinkedHashMap.put(paramClazz, new HashSet<>(Set.of("Class Service")));
        parameterSetLinkedHashMap.put(paramStudent, new HashSet<>(Set.of("StudentService", "Student Service")));
        parameterSetLinkedHashMap.put(paramDepartment, new HashSet<>(Set.of("DepartmentService")));


        Class<?> clazzController = SchoolController.class;
        Field fieldClazz = clazzController.getDeclaredFields()[0];
        Field fieldStudent = clazzController.getDeclaredFields()[1];
        Field fieldDepartment = clazzController.getDeclaredFields()[2];

        LinkedHashMap<Field, Set<String>> fieldBindingName = new LinkedHashMap<>();
        fieldBindingName.put(fieldClazz, new HashSet<>(Set.of("ClazzService")));
        fieldBindingName.put(fieldStudent, new HashSet<>(Set.of("StudentService", "Student Service")));
        fieldBindingName.put(fieldDepartment, new HashSet<>(Set.of("DepartmentService")));

        BeanDefinition beanDefinition = new BeanDefinition.BeanDefinitionBuilder()
                .setBeanClass(SchoolController.class)
                .setBindNames(new HashSet<>(Set.of("SchoolController")))
                .setInjectedConstructor(constructor)
                .setFieldToBindingNames(fieldBindingName).build();

        expectedBeanDefinition.put(SchoolController.class, beanDefinition);

        Map<Class<?>, BeanDefinition> actualBeanDefinition = MetadataExtractor.getInstance().buildBeanDefinitions(componentClasses);
        Assertions.assertEquals(expectedBeanDefinition, actualBeanDefinition);
    }

    @RestController
    private static class SchoolController {
        private ClazzService clazzService;
        private StudentService studentService;

        @Qualifier(name = "DepartmentService")
        private DepartmentService departmentService;


        public SchoolController(@Qualifier(name = "Class Service") ClazzService clazzService, StudentService studentService, DepartmentService departmentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
            this.departmentService = departmentService;
        }
    }

    @RestController
    private static class SchoolController_OnlyOneCtor {
        private ClazzService clazzService;
        private StudentService studentService;

        public SchoolController_OnlyOneCtor(ClazzService clazzService, StudentService studentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }

    @RestController
    private static class SchoolController_ManyCtorsWithOneAutowired {

        private ClazzService clazzService;
        private StudentService studentService;

        @Autowired
        public SchoolController_ManyCtorsWithOneAutowired(ClazzService clazzService, StudentService studentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
        }

        public SchoolController_ManyCtorsWithOneAutowired(ClazzService clazzService) {
            this.clazzService = clazzService;
            this.studentService = null;
        }
    }

    @RestController
    private static class SchoolController_ManyCtorsNoAutowired {
        private ClazzService clazzService;
        private StudentService studentService;

        public SchoolController_ManyCtorsNoAutowired(ClazzService clazzService, StudentService studentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
        }

        public SchoolController_ManyCtorsNoAutowired(ClazzService clazzService) {
            this.clazzService = clazzService;
            this.studentService = null;
        }
    }

    private static class SchoolController_DefaultCtor {
        private ClazzService clazzService;
        private StudentService studentService;
    }

    @Service
    private static class ClazzService {
    }

    @Service
    @Qualifier(name = "Student Service")
    private static class StudentService {
    }

    @Service
    private static class DepartmentService {

    }

    private interface School {
    }

    private class SchoolImpl implements School {
    }

    @Qualifier(name = "School Qualifier")
    private class SchoolQualifer implements School {
    }

    private class SchoolInCache implements School {
    }

}