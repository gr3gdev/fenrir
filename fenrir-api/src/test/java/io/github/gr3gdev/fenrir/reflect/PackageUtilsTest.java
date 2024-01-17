package io.github.gr3gdev.fenrir.reflect;

import io.github.gr3gdev.fenrir.MainForTests;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.route.TestRoute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class PackageUtilsTest {

    @Test
    void findAnnotatedClasses() {
        final List<Class<?>> classes = PackageUtils.findAnnotatedClasses(MainForTests.class, Route.class);
        Assertions.assertEquals(1, classes.size());
        Assertions.assertEquals(TestRoute.class, classes.getFirst());
    }
}