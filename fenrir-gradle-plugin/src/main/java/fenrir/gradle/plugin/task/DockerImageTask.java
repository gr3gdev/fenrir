package fenrir.gradle.plugin.task;

import fenrir.gradle.plugin.ext.FenrirExtension;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@DisableCachingByDefault(because = "Not worth caching")
public abstract class DockerImageTask extends AbstractFenrirTask {

    public static final String TASK_NAME = "buildDockerImage";

    public DockerImageTask() {
        super();
        setDescription("Build a docker image");
        dependsOn(ListJavaDependenciesTask.TASK_NAME);
    }

    @TaskAction
    public void exec() throws IOException {
        final File dir = getTemporaryDir();
        Files.copy(new File(dir.getParentFile(), ListJavaDependenciesTask.TASK_NAME + "/deps.info").toPath(),
                new File(dir, "deps.info").toPath(), StandardCopyOption.REPLACE_EXISTING);
        copyDirectory(new File(dir.getParentFile(), PrepareSourcesTask.TASK_NAME).toPath(),
                new File(dir, "libs").getAbsolutePath());
        final FenrirExtension fenrirExtension = getExtention();
        generateEntrypoint(dir, fenrirExtension);
        generateDockerfile(dir, fenrirExtension);
        getProject().exec(it -> {
            it.setWorkingDir(dir);
            it.commandLine("docker", "build", ".", "-q", "-t", fenrirExtension.getImageName());
        });
    }

    private void copyDirectory(Path sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        try (final Stream<Path> walk = Files.walk(sourceDirectoryLocation)) {
            walk.forEach(source -> {
                final Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                        .substring(sourceDirectoryLocation.toString().length()));
                try {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void generateEntrypoint(File dir, FenrirExtension ext) throws IOException {
        final String entrypointContent = new String(Objects.requireNonNull(DockerImageTask.class
                .getResourceAsStream("/entrypoint.sh")).readAllBytes(),
                StandardCharsets.UTF_8)
                .replace("[mainClass]", ext.getMainClass());
        Files.writeString(new File(dir, "entrypoint.sh").toPath(),
                entrypointContent);
    }

    private void generateDockerfile(File dir, FenrirExtension ext) throws IOException {
        final String dockerfileContent = new String(Objects.requireNonNull(DockerImageTask.class
                .getResourceAsStream("/Dockerfile-template")).readAllBytes(),
                StandardCharsets.UTF_8)
                .replace("[javaVersion]", ext.getJavaVersion().getVersion());
        Files.writeString(new File(dir, "Dockerfile").toPath(),
                dockerfileContent);
    }
}
