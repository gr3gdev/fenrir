package fenrir.gradle.plugin.ext;

import lombok.Data;

/**
 * Plugin extension.
 */
@Data
public class FenrirExtension {

    /**
     * Name of extension.
     */
    public static final String NAME = "fenrir";

    private String mainClass;
    private String imageName;
}
