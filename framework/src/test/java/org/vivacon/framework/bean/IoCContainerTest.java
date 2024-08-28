package org.vivacon.framework.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vivacon.framework.bean.annotation.Component;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.common.ClassScanner;
import org.vivacon.framework.common.MetadataExtractor;
import org.vivacon.framework.event.EventBroker;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class IoCContainerTest {
    private IoCContainer ioCContainer;
    private ClassScanner classScanner;
    private MetadataExtractor metadataExtractor;
    private BeanFactory beanFactory;
    private BeansInitiationOrderResolver resolver;
    private EventBroker eventBroker;

    @BeforeEach
    void setUp() {
        classScanner = Mockito.mock(ClassScanner.class);
        metadataExtractor = Mockito.mock(MetadataExtractor.class);
        beanFactory = Mockito.mock(BeanFactory.class);
        resolver = Mockito.mock(BeansInitiationOrderResolver.class);
        eventBroker = Mockito.mock(EventBroker.class);

        Path scanningPath = Path.of("C:\\AiNgoc\\HTTP-Server\\framework\\build\\classes\\java\\test");
        Set<Class<? extends Annotation>> managedAnnotations = Set.of(Component.class);

        ioCContainer = new IoCContainer(classScanner, metadataExtractor, beanFactory, resolver, scanningPath, managedAnnotations);

    }

    //TODO
    @Test
    void testLoadBeans() {Map<Class<?>, Object> expectedBeanContainer = new HashMap<>();

        // Create beans with proper dependencies
        ClazzService clazzService = new ClazzService();
        StudentService studentService = new StudentService();
        TeacherService teacherService = new TeacherService(clazzService, studentService);
        DepartmentService departmentService = new DepartmentService(teacherService, clazzService, studentService);

        expectedBeanContainer.put(DepartmentService.class, departmentService);
        expectedBeanContainer.put(TeacherService.class, teacherService);
        expectedBeanContainer.put(ClazzService.class, clazzService);
        expectedBeanContainer.put(StudentService.class, studentService);

        // Load beans using the IoCContainer
        Map<Class<?>, Object> actualBeanContainer = ioCContainer.loadBeans();

        // Verify that the actual container contains the expected beans
        for (Map.Entry<Class<?>, Object> entry : expectedBeanContainer.entrySet()) {
            Assertions.assertTrue(actualBeanContainer.containsKey(entry.getKey()),
                    "Expected bean not found in actual container: " + entry.getKey().getName());
        }
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