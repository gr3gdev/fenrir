package fenrir.gradle.plugin;

import fenrir.gradle.plugin.ext.FenrirExtension;
import fenrir.gradle.plugin.task.DockerImageTask;
import fenrir.gradle.plugin.task.PrepareSourcesTask;
import fenrir.gradle.plugin.task.ListJavaDependenciesTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Fenrir Gradle plugin.
 */
public class FenrirGradlePluginPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getExtensions().create(FenrirExtension.NAME, FenrirExtension.class);
        // Register tasks
        project.getTasks().register(PrepareSourcesTask.TASK_NAME, PrepareSourcesTask.class);
        project.getTasks().register(ListJavaDependenciesTask.TASK_NAME, ListJavaDependenciesTask.class);
        project.getTasks().register(DockerImageTask.TASK_NAME, DockerImageTask.class);
    }
}
