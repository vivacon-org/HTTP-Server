package org.vivacon.framework.core;

import org.gradle.internal.impldep.org.junit.runner.RunWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


//@RunWith(PowerMockRunner.class)
//@PrepareForTest({JarFile.class, JarEntry.class})
class ClassScannerTest {

    @Test
    void getInstance() {
    }

    @Test
    void scanClassesAnnotatedBy() {
    }

    @Test
    void getAllClassesInClassPath() {
    }

    @InjectMocks
    private ClassScanner classScanner;

    @Mock
    private File mockDir;
    @Mock
    private File mockClassFile;
    @Mock
    private File mockJarFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        classScanner = Mockito.spy(new ClassScanner());
    }

    @Test
    public void testScanClassesAnnotatedBy() {
        Class<?> clazzWithAnnotation = TestClassWithAnnotation.class;
        Class<?> clazzWithoutAnnotation = TestClassWithoutAnnotation.class;
        Class<?> interfaceClass = TestInterface.class;

        List<Class<?>> allClassesInClassPath = Arrays.asList(clazzWithAnnotation, clazzWithoutAnnotation, interfaceClass);

        Mockito.when(classScanner.getAllClassesInClassPath()).thenReturn(allClassesInClassPath);

        Set<Class<? extends Annotation>> annotations = new HashSet<>();
        annotations.add(TestAnnotation.class);

        List<Class<?>> result = classScanner.scanClassesAnnotatedBy(annotations);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(clazzWithAnnotation, result.get(0));
    }
//    @Test
//    public void testScanJarFile() throws Exception {
//        // Mock JarFile and JarEntry
//        JarFile mockJarFile = PowerMockito.mock(JarFile.class);
//        JarEntry mockJarEntry = PowerMockito.mock(JarEntry.class);
//
//        // Mock the entries in the JarFile
//        Enumeration<JarEntry> mockEntries = Collections.enumeration(
//                List.of(mockJarEntry, mockJarEntry));
//        PowerMockito.when(mockJarFile.entries()).thenReturn(mockEntries);
//
//        // Mock the behavior of JarEntry
//        Mockito.when(mockJarEntry.isDirectory()).thenReturn(false);
//        Mockito.when(mockJarEntry.getName()).thenReturn("TestClass.class");
//
//        // Mock the JarFile constructor
//        PowerMockito.whenNew(JarFile.class).withAnyArguments().thenReturn(mockJarFile);
//
//        // Call the method to test
//        File jarFile = new File("mock.jar");
//        ClassScanner classScanner = new ClassScanner();
//        List<File> result = classScanner.scanJarFile(jarFile);
//
//        // Verify the result
//        Assertions.assertEquals(2, result.size());
//        Assertions.assertEquals("TestClass.class", result.get(0).getName());
//    }

    @Test
    public void testGetAllClassesInClassPath() {
        String classpath = "mockDir";
        Mockito.when(System.getProperty("java.class.path")).thenReturn(classpath);
        Mockito.when(mockDir.isDirectory()).thenReturn(true);
        Mockito.when(mockClassFile.isFile()).thenReturn(true);
        Mockito.when(mockClassFile.getName()).thenReturn("TestClass.class");
        Mockito.when(mockJarFile.isFile()).thenReturn(true);
        Mockito.when(mockJarFile.getName()).thenReturn("test.jar");

        List<File> mockFiles = Arrays.asList(mockClassFile, mockJarFile);
        Mockito.when(classScanner.scanDirectory(mockDir)).thenReturn(mockFiles);

        List<Class<?>> classes = classScanner.getAllClassesInClassPath();

        Assertions.assertEquals(1, classes.size());
    }


    @TestAnnotation
    class TestClassWithAnnotation {}

    class TestClassWithoutAnnotation {}

    interface TestInterface {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {}
}