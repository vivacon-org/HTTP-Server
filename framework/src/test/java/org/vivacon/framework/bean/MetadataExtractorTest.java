package org.vivacon.framework.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vivacon.framework.bean.annotations.Autowired;
import org.vivacon.framework.bean.annotations.Qualifier;
import org.vivacon.framework.bean.annotations.Service;
import org.vivacon.framework.web.annotations.Controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class MetadataExtractorTest {
    private MetadataExtractor metadataExtractor;

    @BeforeEach
    public void setUp() {
        metadataExtractor = MetadataExtractor.getInstance();
        Set<String> predefinedBindings = new HashSet<>(Set.of("PredefinedClassName", "PredefinedInterfaceName"));
        Map<Class<?>, Set<String>> predefinedCache = new HashMap<>();
        predefinedCache.put(SchoolInCache.class, predefinedBindings);
        metadataExtractor.setBeanClassToBindingNamesCache(predefinedCache);
    }

    @Test
    public void test_getConstructorToInject_defaultCtor() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_DefaultCtor.class;
        Constructor<?> actualCtor = MetadataExtractor.getInstance().getConstructorToInject(clazz);
        Constructor<?> expectedCtor = clazz.getDeclaredConstructor();
        Assertions.assertEquals(actualCtor, expectedCtor);
    }

    @Test
    public void test_getConstructorToInject_onlyOneCtor() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_OnlyOneCtor.class;
        Constructor<?> actualCtor = MetadataExtractor.getInstance().getConstructorToInject(clazz);
        Constructor<?> expectedCtor = clazz.getDeclaredConstructor(ClazzService.class, StudentService.class);
        Assertions.assertEquals(actualCtor, expectedCtor);
    }

    @Test
    public void test_getConstructorToInject_manyCtorsWithOneAutowired() throws NoSuchMethodException {
        Class<?> clazz = SchoolController_ManyCtorsWithOneAutowired.class;
        Constructor<?> actualCtor = MetadataExtractor.getInstance().getConstructorToInject(clazz);
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
        Set<String> actualBindingName = MetadataExtractor.getInstance().getBeanBindingName(SchoolInCache.class);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_getBeanBindingName_withInterface() {
        Set<String> expectBindingName = new HashSet<>(Set.of("SchoolImpl", "School"));
        Set<String> actualBindingName = MetadataExtractor.getInstance().getBeanBindingName(SchoolImpl.class);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_getBeanBindingName_withQualifiers() {
        // Predefine cache values
        Set<String> expectBindingName = new HashSet<>(Set.of("School Qualifier", "School", "SchoolQualifer"));
        Set<String> actualBindingName = MetadataExtractor.getInstance().getBeanBindingName(SchoolQualifer.class);
        Assertions.assertEquals(expectBindingName, actualBindingName);
    }

    @Test
    public void test_getDependencyToItsBindName_fromCtor() throws NoSuchMethodException {
        //lấy param của constructor và binding name của param
        //ex: class School - construstor School(Clazz clazz, Student student) -> lấy binding name của Clazz, Student
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
        //lấy field của class và binding name của field
        //ex: class School - co field Clazz clazz, Student student -> lấy binding name của Clazz, Student
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


    @Controller
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

    @Controller
    private static class SchoolController_OnlyOneCtor {
        private ClazzService clazzService;
        private StudentService studentService;

        public SchoolController_OnlyOneCtor(ClazzService clazzService, StudentService studentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }

    @Controller
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

    @Controller
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