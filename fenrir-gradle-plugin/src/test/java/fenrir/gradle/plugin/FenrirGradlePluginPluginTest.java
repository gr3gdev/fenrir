package fenrir.gradle.plugin;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple unit test for the 'fenrir.gradle.plugin' plugin.
 */
class FenrirGradlePluginPluginTest {
    @Test void pluginRegistersATask() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("fenrir.gradle.plugin");

        // Verify the result
        assertNotNull(project.getTasks().findByName("buildDockerImage"));
    }
}
