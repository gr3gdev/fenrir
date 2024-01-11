package fenrir.gradle.plugin.ext;

import lombok.Data;

@Data
public class DockerImageExtension {
    private String imageName;
    private JavaVersion javaVersion = JavaVersion.VERSION_21;
}
