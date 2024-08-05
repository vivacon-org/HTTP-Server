package org.vivacon.framework.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vivacon.framework.web.Component;
import org.vivacon.framework.web.Controller;
import org.vivacon.framework.web.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class BeansInitiationOrderResolverTest {

    @Test
    void resolveOrder_allBeansDependenciesAreValid() {
        List<Class<?>> classes = Arrays.asList(SchoolController.class, ClazzService.class, TeacherService.class,
                StudentService.class,  DepartmentService.class);
        Map<Class<?>, BeanDefinition> beanDefinitionMap = MetadataExtractor.getInstance().buildBeanDefinitions(classes);

        List<Class<?>> actualOrder = BeansInitiationOrderResolver.getInstance().resolveOrder(beanDefinitionMap);

        Assertions.assertEquals(classes.size(), actualOrder.size());

        List<Class<?>> pureBeans = Arrays.asList(ClazzService.class, StudentService.class);
        int[] expectedPureBeanIndexes = new int[]{0, 1};
        for (int index : expectedPureBeanIndexes){
            Assertions.assertTrue(pureBeans.contains(actualOrder.get(index)));
        }

        List<Class<?>> expectedOrder = Arrays.asList(TeacherService.class, DepartmentService.class, SchoolController.class);
        int runner = 2;
        for (Class<?> expectedClazz : expectedOrder){
            Assertions.assertEquals(expectedClazz, actualOrder.get(runner));
            runner++;
        }
    }

    @Test
    void resolveOrder_containCircularDependencies() {
        List<Class<?>> classes = Arrays.asList(SchoolController.class, ClazzService.class, CircularDependenciesTeacherService.class,
                StudentService.class,  CircularDependenciesDepartmentService.class);
        Map<Class<?>, BeanDefinition> beanDefinitionMap = MetadataExtractor.getInstance().buildBeanDefinitions(classes);

        Assertions.assertThrows(Exception.class, () -> {
            BeansInitiationOrderResolver.getInstance().resolveOrder(beanDefinitionMap);
        });
    }

    @Controller
    private static class SchoolController {
        private DepartmentService departmentService;

        @Autowired
        public SchoolController(DepartmentService departmentService){
            this.departmentService = departmentService;
        }
    }

    @Service
    private static class DepartmentService {

        private TeacherService teacherService;

        private ClazzService clazzService;

        private StudentService studentService;

        public DepartmentService(TeacherService teacherService,
                                 ClazzService clazzService,
                                 StudentService studentService){
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
                              StudentService studentService){
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


    @Service
    private static class CircularDependenciesDepartmentService {

        private CircularDependenciesTeacherService teacherService;

        private ClazzService clazzService;

        private StudentService studentService;

        public CircularDependenciesDepartmentService(CircularDependenciesTeacherService teacherService,
                                 ClazzService clazzService,
                                 StudentService studentService){
            this.teacherService = teacherService;
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }

    @Component
    private static class CircularDependenciesTeacherService {
        private CircularDependenciesDepartmentService departmentService;
        private ClazzService clazzService;
        private StudentService studentService;

        public CircularDependenciesTeacherService(CircularDependenciesDepartmentService departmentService,
                                                  ClazzService clazzService,
                                                  StudentService studentService){
            this.departmentService = departmentService;
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }
}