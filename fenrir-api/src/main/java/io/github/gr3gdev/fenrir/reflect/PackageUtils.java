package io.github.gr3gdev.fenrir.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utils for package manipulations.
 */
public class PackageUtils {

    private PackageUtils() {
        // None
    }

    private static String getPackagePath(Class<?> mainClass) {
        final String packageName = mainClass.getPackageName();
        return packageName.replaceAll("[.]", File.separator);
    }

    private static List<Class<?>> findClasses(Class<?> mainClass, Function<Class<?>, Boolean> filter, String errorMessage) {
        final String packagePath = getPackagePath(mainClass);
        try (final Stream<Path> stream = Files.walk(Paths
                .get(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
                        .getResource(packagePath)).getPath()))) {
            return stream
                    .filter(PackageUtils::isClass)
                    .map(p -> PackageUtils.pathToClass(packagePath, p))
                    .filter(filter::apply)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Find all classes with an annotation in the package (and children) of the main class (by reflection).
     *
     * @param mainClass       the main class
     * @param annotationClass the annotation class
     * @return List of Class
     */
    public static List<Class<?>> findAnnotatedClasses(Class<?> mainClass, Class<? extends Annotation> annotationClass) {
        return findClasses(mainClass,
                c -> c.isAnnotationPresent(annotationClass),
                "Error when searching annotated classes : " + annotationClass.getCanonicalName());
    }

    private static boolean isClass(Path path) {
        return path.toFile().isFile() && path.toString().endsWith(".class");
    }

    private static Class<?> pathToClass(String packagePath, Path path) {
        try {
            final String classPath = path.toString().substring(path.toString().indexOf(packagePath),
                    path.toString().lastIndexOf('.'));
            return Class.forName(classPath.replace(File.separator, "."));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
