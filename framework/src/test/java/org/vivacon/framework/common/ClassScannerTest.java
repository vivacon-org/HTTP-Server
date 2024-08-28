package org.vivacon.framework.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vivacon.framework.bean.annotation.Autowired;
import org.vivacon.framework.bean.annotation.Component;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.web.annotation.RestController;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ClassScannerTest {
    Set<Class<? extends Annotation>> managedAnnotations;

    @BeforeEach
    void setUp() {
        managedAnnotations = new HashSet<>();
        managedAnnotations.add(RestController.class);
        managedAnnotations.add(Service.class);
    }

    @Test
    void test_scanClassesAnnotatedBy() {
        Path scanningPath = Path.of("C:\\AiNgoc\\HTTP-Server\\framework\\build\\classes\\java\\test"); //mock jar

        Set<Class<? extends Annotation>> managedAnnotations = new HashSet<>();
        managedAnnotations.add(RestController.class);
        managedAnnotations.add(Service.class);
        managedAnnotations.add(Component.class);

        List<Class<?>> expectedResult = Arrays.asList(SchoolController.class, DepartmentService.class, TeacherService.class, ClazzService.class, StudentService.class);

        /*List<Class<?>> actualResult = ClassScanner.getInstance().scanClassesAnnotatedBy(scanningPath, managedAnnotations);

        for (Class<?> result : expectedResult) {
            Assertions.assertTrue(actualResult.contains(result));
        }*/

    }

    @Test
    void test_getAllClassesInClassPath() throws MalformedURLException {
        Path scanningPath = Path.of("C:\\AiNgoc\\HTTP-Server\\framework\\build\\classes\\java\\test");

        ClassScanner clazzScanner = new ClassScanner(ClassLoaderFactory.getInstance().create(scanningPath.toUri().toURL()));
        List<Class<?>> expectedResult = new ArrayList<>();
        expectedResult.add(SchoolController.class);
        expectedResult.add(DepartmentService.class);
        expectedResult.add(TeacherService.class);
        expectedResult.add(ClazzService.class);

        List<Class<?>> actualResult = clazzScanner.getAllClassesInClassPath(scanningPath);

        for (Class<?> result : expectedResult) {
            Assertions.assertTrue(actualResult.contains(result));
        }
    }

    @Test
    void test_scanDirectory() throws MalformedURLException {
        File rootDirectory = new File("C:\\AiNgoc\\HTTP-Server\\demo\\build\\classes\\java\\main");
        List<File> classFileExpected = new ArrayList<>();
        classFileExpected.add(new File("C:\\AiNgoc\\HTTP-Server\\demo\\build\\classes\\java\\main\\org\\vivacon\\demo\\controller\\CompanyController.class"));
        classFileExpected.add(new File("C:\\AiNgoc\\HTTP-Server\\demo\\build\\classes\\java\\main\\org\\vivacon\\demo\\service\\BroadCastService.class"));
        classFileExpected.add(new File("C:\\AiNgoc\\HTTP-Server\\demo\\build\\classes\\java\\main\\org\\vivacon\\demo\\service\\EmailNotificationService.class"));
        classFileExpected.add(new File("C:\\AiNgoc\\HTTP-Server\\demo\\build\\classes\\java\\main\\org\\vivacon\\demo\\service\\SMSNotificationService.class"));

        ClassScanner clazzScanner = new ClassScanner(ClassLoaderFactory.getInstance().create(rootDirectory.toURI().toURL()));

        List<File> classFileActual = clazzScanner.scanDirectory(rootDirectory);

        for (File expectedFile : classFileExpected) {
            Assertions.assertTrue(classFileActual.contains(expectedFile), "Expected file not found: " + expectedFile.getPath());
        }
    }

    //TODO: test scanJarFile

    @RestController
    private static class SchoolController {
        private DepartmentService departmentService;

        @Autowired
        public SchoolController(DepartmentService departmentService) {
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