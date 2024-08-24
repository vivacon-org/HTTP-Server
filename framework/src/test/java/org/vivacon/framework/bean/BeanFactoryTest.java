package org.vivacon.framework.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vivacon.framework.bean.annotation.Component;
import org.vivacon.framework.bean.annotation.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class BeanFactoryTest {

    @Test
    void test_createBean() throws Exception {
        Constructor<?> departmentServiceConstructor = DepartmentService.class.getDeclaredConstructor(
                TeacherService.class, ClazzService.class, StudentService.class);

        LinkedHashMap<Parameter, Set<String>> parameterToBindingNames = new LinkedHashMap<>();
        parameterToBindingNames.put(departmentServiceConstructor.getParameters()[0], Set.of("TeacherService"));
        parameterToBindingNames.put(departmentServiceConstructor.getParameters()[1], Set.of("ClazzService"));
        parameterToBindingNames.put(departmentServiceConstructor.getParameters()[2], Set.of("StudentService"));

        BeanDefinition beanDefinition = new BeanDefinition.BeanDefinitionBuilder()
                .setBeanClass(DepartmentService.class)
                .setBindNames(new HashSet<>(Set.of("DepartmentService")))
                .setInjectedConstructor(departmentServiceConstructor)
                .setParameterToBindingNames(parameterToBindingNames)
                .build();

        DepartmentService departmentService = new DepartmentService(new TeacherService(new ClazzService(), new StudentService()), new ClazzService(), new StudentService());

        Map<Class<?>, Object> clazzToBean = new HashMap<>();
        clazzToBean.put(DepartmentService.class, departmentService);

        Map<String, Set<Object>> bindNameToBeans = new HashMap<>();
        clazzToBean.forEach((clazz, bean) -> bindNameToBeans.put(clazz.getSimpleName(), Set.of(bean)));
        bindNameToBeans.put("DepartmentService", Set.of(departmentService));

        // Execute
        Object actualResult = BeanFactory.getInstance().createBean(beanDefinition, clazzToBean, bindNameToBeans);

        Assertions.assertTrue(actualResult instanceof DepartmentService);
        Assertions.assertEquals(departmentService, actualResult);
    }

    @Test
    void test_populateDependencies() throws NoSuchFieldException {
        Field teacherServiceField = DepartmentService.class.getDeclaredField("teacherService");
        Field clazzServiceFiled = DepartmentService.class.getDeclaredField("clazzService");
        Field studentServiceField = DepartmentService.class.getDeclaredField("studentService");


        LinkedHashMap<Field, Set<String>> dependencyToItsBindNames = new LinkedHashMap<>();
        dependencyToItsBindNames.put(teacherServiceField, Set.of("TeacherService"));
        dependencyToItsBindNames.put(clazzServiceFiled, Set.of("ClazzService"));
        dependencyToItsBindNames.put(studentServiceField, Set.of("StudentService"));


        Map<String, Set<Object>> bindNameToBeans = new LinkedHashMap<>();
        bindNameToBeans.put("TeacherService", Set.of(TeacherService.class));
        bindNameToBeans.put("ClazzService", Set.of(ClazzService.class));
        bindNameToBeans.put("StudentService", Set.of(StudentService.class));

        Object[] actualResult = BeanFactory.getInstance().populateDependencies(dependencyToItsBindNames, bindNameToBeans);

        Assertions.assertTrue(Arrays.asList(actualResult).contains(TeacherService.class));
        Assertions.assertTrue(Arrays.asList(actualResult).contains(ClazzService.class));
        Assertions.assertTrue(Arrays.asList(actualResult).contains(StudentService.class));

    }


    @Service
    private static class DepartmentService {

        private TeacherService teacherService;

        private ClazzService clazzService;

        private StudentService studentService;

        public DepartmentService(TeacherService teacherService,
                                 ClazzService clazzService,
                                 StudentService studentService) {
            this.teacherService = teacherService;
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }

    @Component
    private static class TeacherService {

        private ClazzService clazzService;
        private StudentService studentService;

        public TeacherService(ClazzService clazzService,
                              StudentService studentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }

    @Component
    private static class ClazzService {
    }

    @Component
    private static class StudentService {
    }
}