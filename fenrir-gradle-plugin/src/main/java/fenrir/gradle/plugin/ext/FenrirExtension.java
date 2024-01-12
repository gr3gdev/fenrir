package fenrir.gradle.plugin.ext;

import lombok.Data;

@Data
public class FenrirExtension {

    public static final String NAME = "fenrir";

    private String mainClass;
    private JavaVersion javaVersion = JavaVersion.VERSION_21;
    private String imageName;
}
