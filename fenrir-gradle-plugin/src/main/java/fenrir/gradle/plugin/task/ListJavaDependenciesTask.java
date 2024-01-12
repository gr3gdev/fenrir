package fenrir.gradle.plugin.task;

import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ListJavaDependenciesTask extends AbstractFenrirTask {

    public static final String TASK_NAME = "listJavaDependencies";

    public ListJavaDependenciesTask() {
        super();
        setDescription("List module dependencies for the JVM");
        dependsOn(PrepareSourcesTask.TASK_NAME);
    }

    @TaskAction
    public void exec() throws IOException {
        final String javaVersion = getExtention().getJavaVersion().getVersion();
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
