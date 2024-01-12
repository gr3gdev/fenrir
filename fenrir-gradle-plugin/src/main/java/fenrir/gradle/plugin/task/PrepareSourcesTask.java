package fenrir.gradle.plugin.task;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.io.File;

/**
 * Task for prepare sources with all dependencies in a specific directory.
 */
@DisableCachingByDefault(because = "Not worth caching")
public class PrepareSourcesTask extends AbstractFenrirTask {

    /**
     * Task name.
     */
    public static final String TASK_NAME = "prepareSources";

    /**
     * Constructor.
     */
    public PrepareSourcesTask() {
        super();
        setDescription("Prepare sources");
        dependsOn("build");
    }

    /**
     * Main action.
     */
    @TaskAction
    public void exec() {
        final File projectDir = getProject().getProjectDir();
        final File outputDir = getTemporaryDir();
        final Configuration conf = getProject().getConfigurations().findByName("runtimeClasspath");
        if (conf != null) {
            conf.getFiles()
                    .forEach(file ->
                            getProject().copy(it ->
                                    it.from(getProject().zipTree(file)).into(outputDir)
                            ));
        }
        final File jarFile = new File(projectDir, "build/libs/" + getLibraryFullName());
        getProject().copy(it -> it.from(getProject().zipTree(jarFile)).into(outputDir));
    }
}
