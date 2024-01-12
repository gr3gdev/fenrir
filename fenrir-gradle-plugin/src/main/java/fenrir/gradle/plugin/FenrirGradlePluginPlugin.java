package fenrir.gradle.plugin;

import fenrir.gradle.plugin.ext.FenrirExtension;
import fenrir.gradle.plugin.task.DockerImageTask;
import fenrir.gradle.plugin.task.ListJavaDependenciesTask;
import fenrir.gradle.plugin.task.PrepareSourcesTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

/**
 * Fenrir Gradle plugin.
 */
public class FenrirGradlePluginPlugin extends JavaPlugin {

    @Inject
    public FenrirGradlePluginPlugin() {
        super();
    }

    public void apply(@NotNull Project project) {
        super.apply(project);
        project.getExtensions().create(FenrirExtension.NAME, FenrirExtension.class);
        // Register tasks
        project.getTasks().register(PrepareSourcesTask.TASK_NAME, PrepareSourcesTask.class);
        project.getTasks().register(ListJavaDependenciesTask.TASK_NAME, ListJavaDependenciesTask.class);
        project.getTasks().register(DockerImageTask.TASK_NAME, DockerImageTask.class);
    }
}
