package fenrir.gradle.plugin.task;

import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Task for list module dependencies.
 */
public class ListJavaDependenciesTask extends AbstractFenrirTask {

    /**
     * Task name.
     */
    public static final String TASK_NAME = "listJavaDependencies";

    /**
     * Constructor.
     */
    public ListJavaDependenciesTask() {
        super();
        setDescription("List module dependencies for the JVM");
        dependsOn(PrepareSourcesTask.TASK_NAME);
    }

    /**
     * Main action.
     *
     * @throws IOException deps.info not found
     */
    @TaskAction
    public void exec() throws IOException {
        final String javaVersion = getJavaVersion();
        final String classPathDirectory = new File(getTemporaryDir().getParentFile(), PrepareSourcesTask.TASK_NAME).getAbsolutePath();
        final File depsFile = new File(getTemporaryDir(), "deps.info");
        getProject().exec(it -> {
                    try {
                        it.commandLine("jdeps", "--ignore-missing-deps", "-q", "--recursive",
                                "--multi-release", javaVersion,
                                "--print-module-deps",
                                "--class-path", classPathDirectory, ".").setStandardOutput(new FileOutputStream(depsFile));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        if (Files.readString(depsFile.toPath()).isBlank()) {
            Files.writeString(depsFile.toPath(), "ALL-MODULE-PATH");
        }
    }
}
