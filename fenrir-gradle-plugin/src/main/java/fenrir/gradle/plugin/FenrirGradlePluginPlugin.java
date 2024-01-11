package fenrir.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import fenrir.gradle.plugin.ext.DockerImageExtension;
import fenrir.gradle.plugin.ext.FenrirExtension;
import fenrir.gradle.plugin.task.DockerImageTask;

/**
 * Fenrir Gradle plugin.
 */
public class FenrirGradlePluginPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getExtensions().create("dockerImage", DockerImageExtension.class);
        project.getExtensions().create("fenrir", FenrirExtension.class);
        // Register a task
        project.getTasks().register("buildDockerImage", DockerImageTask.class);
    }
}
