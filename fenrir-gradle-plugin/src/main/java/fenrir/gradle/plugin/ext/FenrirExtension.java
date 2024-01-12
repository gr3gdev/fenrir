package fenrir.gradle.plugin.ext;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Plugin extension.
 */
@Data
@NoArgsConstructor
public class FenrirExtension {

    /**
     * Name of extension.
     */
    public static final String NAME = "fenrir";

    private String mainClass;
    private String imageName;
}
