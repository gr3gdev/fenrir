package fenrir.gradle.plugin;

import fenrir.gradle.plugin.ext.FenrirExtension;
import fenrir.gradle.plugin.task.DockerImageTask;
import fenrir.gradle.plugin.task.ListJavaDependenciesTask;
import fenrir.gradle.plugin.task.PrepareSourcesTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Fenrir Gradle plugin.
 */
public abstract class FenrirGradlePluginPlugin extends JavaPlugin {

    /**
     * Apply the plugin to the project (include {@link JavaPlugin}).
     *
     * @param project The target object
     */
    public void apply(@NotNull Project project) {
        super.apply(project);
        project.getExtensions().create(FenrirExtension.NAME, FenrirExtension.class);
        // Register tasks
        project.getTasks().register(PrepareSourcesTask.TASK_NAME, PrepareSourcesTask.class);
        project.getTasks().register(ListJavaDependenciesTask.TASK_NAME, ListJavaDependenciesTask.class);
        project.getTasks().register(DockerImageTask.TASK_NAME, DockerImageTask.class);
    }
}
