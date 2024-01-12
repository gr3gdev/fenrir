package fenrir.gradle.plugin.ext;

import lombok.Data;

@Data
public class FenrirExtension {

    public static final String NAME = "fenrir";

    private String mainClass;
    private String imageName;
}
