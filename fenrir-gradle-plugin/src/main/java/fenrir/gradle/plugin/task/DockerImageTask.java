package fenrir.gradle.plugin.task;

import fenrir.gradle.plugin.ext.DockerImageExtension;
import fenrir.gradle.plugin.ext.FenrirExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@DisableCachingByDefault(because = "Not worth caching")
public abstract class DockerImageTask extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerImageTask.class);

    public DockerImageTask() {
        super();
        setDescription("Build a docker image");
        setGroup("Fenrir");
        dependsOn("build");
    }

    @TaskAction
    public void exec() throws IOException {
        final String version = (String) getProject().getVersion();
        String libName = getProject().getName();
        if (!version.equals("unspecified")) {
            libName += "-" + version;
        }
        final DockerImageExtension dockerImageExtension = getProject().getExtensions()
                .getByType(DockerImageExtension.class);
        final File dir = getProject().getProjectDir();
        final File dockerDir = new File(dir, "build/docker");
        if (!dockerDir.exists()) {
            Files.createDirectories(dockerDir.toPath());
        }
        Files.copy(new File(dir, "build/libs/" + libName + ".jar").toPath(), new File(dockerDir, libName + ".jar").toPath());
        final Configuration conf = getProject().getConfigurations().findByName("runtimeClasspath");
        if (conf != null) {
            conf.getFiles().forEach(file -> {
                try {
                    Files.copy(file.toPath(), new File(dockerDir, file.getName()).toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        generateDockerfile(dir, dockerImageExtension);
        getProject().exec(it -> {
            it.setWorkingDir(dir);
            it.commandLine("docker", "build", ".", "-t", dockerImageExtension.getImageName());
        });
    }

    private void generateDockerfile(File temporaryDir, DockerImageExtension ext) {
        try {
            final FenrirExtension fenrirExtension = getProject().getExtensions()
                    .getByType(FenrirExtension.class);
            final String dockerfileContent = """
                    # JDK image
                    FROM alpine as base
                    RUN apk add --update openjdk[javaVersion] binutils

                    # Optimize JRE
                    FROM base as customjre
                    RUN mkdir /work
                    COPY ./build/docker/*.jar /work/
                    WORKDIR /work
                    RUN for f in *.jar; do jar xf "$f"; done

                    # find modules dependencies
                    RUN jdeps --ignore-missing-deps -q \\
                        --recursive \\
                        --multi-release [javaVersion] \\
                        --print-module-deps \\
                        --class-path . \\
                        . | sed 's/^$/ALL-MODULE-PATH/' > deps.info

                    # create a custom jre
                    RUN jlink \\
                        --add-modules $(cat deps.info) \\
                        --strip-debug \\
                        --compress 2 \\
                        --no-header-files \\
                        --no-man-pages \\
                        --output customjre

                    # Slim image
                    FROM alpine
                    ENV JAVA_HOME /jre
                    ENV PATH $JAVA_HOME/bin:$PATH
                    COPY --from=customjre /work/customjre $JAVA_HOME
                    RUN mkdir /libs
                    COPY --from=customjre /work /libs
                    WORKDIR /libs
                    ENTRYPOINT java -cp . [mainClass]
                    """
                    .replace("[javaVersion]", ext.getJavaVersion().getVersion())
                    .replace("[mainClass]", fenrirExtension.getMainClass());
            Files.writeString(new File(temporaryDir, "Dockerfile").toPath(),
                    dockerfileContent);
        } catch (IOException e) {
            LOGGER.error("Error when buildDockerImage", e);
        }
    }
}
