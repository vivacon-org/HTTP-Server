package org.vivacon.framework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vivacon.framework.bean.annotations.Qualifier;
import org.vivacon.framework.bean.annotations.Service;
import org.vivacon.framework.web.annotations.Controller;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ClassScannerTest {
    ClassScanner clazzScanner;
    Set<Class<? extends Annotation>> managedAnnotations;

    @BeforeEach
    void setUp() {
        clazzScanner = ClassScanner.getInstance();
        managedAnnotations = new HashSet<>();
        managedAnnotations.add(Controller.class);
        managedAnnotations.add(Service.class);
    }

    @Test
    void test_scanClassesAnnotatedBy() {
        Path scanningPath = Paths.get("C:\\AiNgoc\\HTTP-Server\\demo\\build\\classes\\java\\test");
        List<Class<?>> exceptClazzes = Arrays.asList(SchoolController.class, ClazzService.class);
        List<Class<?>> actualClazzes = clazzScanner.scanClassesAnnotatedBy(scanningPath, managedAnnotations);
        Assertions.assertEquals(exceptClazzes, actualClazzes);
    }

    @Service
    private static class ClazzService {
    }


    @Service
    @Qualifier(name = "Student Service")
    private static class StudentService {
    }

    @Controller
    private static class SchoolController {
        private ClassScannerTest.ClazzService clazzService;
        private ClassScannerTest.StudentService studentService;


        public SchoolController(@Qualifier(name = "Class Service") ClassScannerTest.ClazzService clazzService, ClassScannerTest.StudentService studentService) {
            this.clazzService = clazzService;
            this.studentService = studentService;
        }
    }

}