package io.github.gr3gdev.fenrir.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SystemStubsExtension.class)
class FenrirPropertiesTest {
    @SystemStub
    private EnvironmentVariables environmentVariables;

    FenrirProperties load() {
        final FenrirProperties fenrirProperties = new FenrirProperties();
        try {
            fenrirProperties.load(FenrirPropertiesTest.class.getResourceAsStream("/fenrir.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Configuration file 'fenrir.properties' is not found !", e);
        }
        return fenrirProperties;
    }

    @BeforeEach
    void before() {
        System.setProperties(new Properties());
        environmentVariables.remove("VAR1");
        environmentVariables.remove("VAR2");
        environmentVariables.remove("JDBC_URL");
    }

    @Test
    void defaultProperties() {
        final FenrirProperties fenrirProperties = load();
        assertEquals("jdbc://default", fenrirProperties.getProperty("test.jdbcUrl"), "Error when reading property");
        assertEquals("", fenrirProperties.getProperty("test.key1"), "Default value when variable not set");
        assertEquals("value2", fenrirProperties.getProperty("test.key2"), "Default value invalid");
        assertEquals("value3", fenrirProperties.getProperty("test.key3"), "Error when reading property");
    }

    @Test
    void replacePlaceholderWithProperty() {
        System.setProperty("VAR1", "value1");
        System.setProperty("VAR2", "another value2");
        System.setProperty("JDBC_URL", "jdbc://real");
        final FenrirProperties fenrirProperties = load();
        assertEquals("value1", fenrirProperties.getProperty("test.key1"), "Variable not replace");
        assertEquals("another value2", fenrirProperties.getProperty("test.key2"), "Variable not replace");
        assertEquals("value3", fenrirProperties.getProperty("test.key3"), "Error when reading property");
        assertEquals("jdbc://real", fenrirProperties.getProperty("test.jdbcUrl"), "Error when reading property");
    }

    @Test
    void replacePlaceholderWithEnv() {
        environmentVariables.set("VAR1", "env value1");
        environmentVariables.set("VAR2", "env value2");
        environmentVariables.set("JDBC_URL", "jdbc://real");
        final FenrirProperties fenrirProperties = load();
        assertEquals("env value1", fenrirProperties.getProperty("test.key1"), "Variable not replace");
        assertEquals("env value2", fenrirProperties.getProperty("test.key2"), "Variable not replace");
        assertEquals("jdbc://real", fenrirProperties.getProperty("test.jdbcUrl"), "Error when reading property");
    }
}