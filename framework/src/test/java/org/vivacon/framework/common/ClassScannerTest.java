package org.vivacon.framework.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.vivacon.framework.bean.annotation.Autowired;
import org.vivacon.framework.bean.annotation.Component;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.web.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


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
        ClassScanner clazzScanner = Mockito.mock(ClassScanner.class);


        Set<Class<? extends Annotation>> managedAnnotations = new HashSet<>();
        managedAnnotations.add(RestController.class);
        managedAnnotations.add(Service.class);
        managedAnnotations.add(Component.class);

        List<Class<?>> expectedResult = Arrays.asList(SchoolController.class, DepartmentService.class, TeacherService.class, ClazzService.class, StudentService.class);

        List<Class<?>> actualResult = clazzScanner.scanClassesAnnotatedBy(Paths.get("."), managedAnnotations);

        for (Class<?> result : expectedResult) {
            Assertions.assertTrue(actualResult.contains(result));
        }

    }

    @Test
    void test_getAllClassesInClassPath() throws MalformedURLException {

        ClassScanner clazzScanner = Mockito.mock(ClassScanner.class);

        List<Class<?>> expectedResult = Arrays.asList(SchoolController.class, DepartmentService.class, TeacherService.class, ClazzService.class);

        Mockito.when(clazzScanner.getAllClassesInClassPath(Mockito.any())).thenReturn(expectedResult);

        List<Class<?>> actualResult = clazzScanner.getAllClassesInClassPath(Paths.get(""));
        for (Class<?> result : expectedResult) {
            Assertions.assertTrue(actualResult.contains(result));
        }
    }

    @Test
    void test_scanDirectory(@TempDir Path tempDir) throws IOException {

        Path controllerDirectory = tempDir.resolve("org/vivacon/demo/controller");
        Files.createDirectories(controllerDirectory);
        Files.createFile(controllerDirectory.resolve("CompanyController.class"));

        Path serviceDirectory = tempDir.resolve("org/vivacon/demo/service");
        Files.createDirectories(serviceDirectory);
        Files.createFile(serviceDirectory.resolve("BroadCastService.class"));
        Files.createFile(serviceDirectory.resolve("EmailNotificationService.class"));
        Files.createFile(serviceDirectory.resolve("SMSNotificationService.class"));

        List<File> classFileExpected = new ArrayList<>();
        classFileExpected.add(controllerDirectory.resolve("CompanyController.class").toFile());
        classFileExpected.add(serviceDirectory.resolve("BroadCastService.class").toFile());
        classFileExpected.add(serviceDirectory.resolve("EmailNotificationService.class").toFile());
        classFileExpected.add(serviceDirectory.resolve("SMSNotificationService.class").toFile());

        ClassScanner clazzScanner = new ClassScanner(ClassLoaderFactory.getInstance().create(tempDir.toUri().toURL()));
        List<File> classFileActual = clazzScanner.scanDirectory(tempDir.toFile());

        for (File expectedFile : classFileExpected) {
            Assertions.assertTrue(classFileActual.contains(expectedFile), "Expected file not found: " + expectedFile.getPath());
        }
    }

    @Test
    void test_scanJarFile() throws Exception {
        JarFile jarFile = Mockito.mock(JarFile.class);

        JarEntry schoolEntry = Mockito.mock(JarEntry.class);
        Mockito.when(schoolEntry.getName()).thenReturn("org.vivacon.framework.common.ClassScannerTest$SchoolController.class");
        Mockito.when(schoolEntry.isDirectory()).thenReturn(false);

        JarEntry deparmentEntry = Mockito.mock(JarEntry.class);
        Mockito.when(deparmentEntry.getName()).thenReturn("org.vivacon.framework.common.ClassScannerTest$DepartmentService.class");
        Mockito.when(deparmentEntry.isDirectory()).thenReturn(false);

        List<JarEntry> jarEntries = Arrays.asList(schoolEntry, deparmentEntry);
        Enumeration<JarEntry> enumeration = new Vector<>(jarEntries).elements();
        Mockito.when(jarFile.entries()).thenReturn(enumeration);

        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        Mockito.doReturn(SchoolController.class)
                .when(classLoader)
                .loadClass("org.vivacon.framework.common.ClassScannerTest$SchoolController");
        Mockito.doReturn(DepartmentService.class)
                .when(classLoader)
                .loadClass("org.vivacon.framework.common.ClassScannerTest$DepartmentService");

        ClassScanner clazzScanner = new ClassScanner(classLoader);

        List<Class<?>> actualClasses = clazzScanner.scanJarFile(jarFile);

        List<Class<?>> expectedClasses = new ArrayList<>(Arrays.asList(SchoolController.class, DepartmentService.class));

        Assertions.assertEquals(expectedClasses, actualClasses);
    }

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