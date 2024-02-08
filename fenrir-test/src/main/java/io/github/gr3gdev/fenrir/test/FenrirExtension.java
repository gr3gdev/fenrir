package io.github.gr3gdev.fenrir.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * JUnit's extension for tests with Fenrir framework.
 */
public class FenrirExtension implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback {

    private final Map<String, TestApplication> testApplications = new HashMap<>();

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) {
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(App.class))
                .forEach(f -> {
                    if (f.getType().equals(TestApplication.class)) {
                        try {
                            f.setAccessible(true);
                            f.set(testInstance, testApplications.get(f.getName()));
                            f.setAccessible(false);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        testApplications.values().forEach(TestApplication::stop);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        final Class<?> testClass = extensionContext.getRequiredTestClass();
        Arrays.stream(testClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(BeforeApp.class))
                .forEach(m -> {
                    try {
                        if (Modifier.isStatic(m.getModifiers())) {
                            m.invoke(null);
                        } else {
                            throw new RuntimeException("The method annotated with @beforeApp must be static : " + m.getName());
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
        Arrays.stream(testClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(App.class))
                .forEach(f -> {
                    if (f.getType().equals(TestApplication.class)) {
                        final App app = f.getAnnotation(App.class);
                        final TestApplication testApplication = new TestApplication(app.value());
                        testApplication.waitStarted(app.timeoutInSeconds());
                        testApplications.put(f.getName(), testApplication);
                    } else {
                        throw new RuntimeException("The @App annotation is only allowed for " + TestApplication.class.getCanonicalName());
                    }
                });
    }
}
