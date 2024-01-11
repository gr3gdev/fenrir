package fenrir.gradle.plugin;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A simple functional test for the 'fenrir.gradle.plugin' plugin.
 */
class FenrirGradlePluginPluginFunctionalTest {
    @TempDir(cleanup = CleanupMode.NEVER)
    File projectDir;

    private File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    private File getSettingsFile() {
        return new File(projectDir, "settings.gradle");
    }

    private File getMainSourceFile(File packageDir) {
        return new File(packageDir, "Test.java");
    }

    private File getRouteSourceFile(File packageDir) {
        return new File(packageDir, "TestRoute.java");
    }

    private File getResourceFile(File resourcesDir) {
        return new File(resourcesDir, "index.html");
    }

    @Test
    void canRunTask() throws IOException {
        writeString(getSettingsFile(), "");
        writeString(getBuildFile(), String.format("""
                plugins {
                    id('fenrir.gradle.plugin')
                    id 'java'
                }

                group = "com.example"
                version = "0.0.1-SNAPSHOT"

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation('org.slf4j:slf4j-api:2.0.11')
                    implementation('ch.qos.logback:logback-classic:1.4.14')
                    implementation files('%1$s/fenrir-api/build/libs/fenrir-api-0.1.0.jar')
                    implementation files('%1$s/fenrir-http/build/libs/fenrir-http-0.1.0.jar')
                }

                java {
                    toolchain {
                        languageVersion = JavaLanguageVersion.of(21)
                    }
                }

                dockerImage {
                    imageName = "functionnal-test"
                }

                fenrir {
                    mainClass = "io.github.gr3gdev.fenrir.gradle.plugin.Test"
                }
                """, new File(Objects.requireNonNull(FenrirGradlePluginPluginFunctionalTest.class.getClassLoader().getResource(".")).getPath(),
                "../../../../..").getAbsolutePath()));

        final File resourcesDir = new File(projectDir, "src/main/resources");
        if (!resourcesDir.exists()) {
            assertTrue(resourcesDir.mkdirs());
        }
        writeString(getResourceFile(resourcesDir), """
                <!DOCTYPE HTML>
                <html>
                <head>
                    <title>Test</title>
                </head>
                <body>
                    <h1>It works !</h1>
                </body>
                </html>
                """);

        final File packageDir = new File(projectDir, "src/main/java/io/github/gr3gdev/fenrir/gradle/plugin");
        if (!packageDir.exists()) {
            assertTrue(packageDir.mkdirs());
        }
        writeString(getMainSourceFile(packageDir), """
                package io.github.gr3gdev.fenrir.gradle.plugin;

                import io.github.gr3gdev.fenrir.FenrirApplication;
                import io.github.gr3gdev.fenrir.FenrirConfiguration;
                import io.github.gr3gdev.fenrir.runtime.HttpMode;

                @FenrirConfiguration(modes = { HttpMode.class })
                public class Test {
                    public static void main(String[] args) {
                        FenrirApplication.run(Test.class);
                    }
                }
                """);
        writeString(getRouteSourceFile(packageDir), """
                package io.github.gr3gdev.fenrir.gradle.plugin;

                import io.github.gr3gdev.fenrir.annotation.Listener;
                import io.github.gr3gdev.fenrir.annotation.Route;
                import io.github.gr3gdev.fenrir.plugin.impl.FileLoaderPlugin;

                @Route(plugin = FileLoaderPlugin.class)
                public class TestRoute {

                    @Listener(path = "/")
                    public String index() {
                        return "/index.html";
                    }
                }
                """);

        // Run the build
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("buildDockerImage");
        runner.withProjectDir(projectDir);
        BuildResult result = runner.build();

        // Verify the result
        assertTrue(result.getOutput().contains("Task :buildDockerImage"));
        assertTrue(result.getOutput().contains("BUILD SUCCESSFUL"));
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
