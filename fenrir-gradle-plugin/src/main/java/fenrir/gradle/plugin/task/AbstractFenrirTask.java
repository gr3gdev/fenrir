package fenrir.gradle.plugin.task;

import fenrir.gradle.plugin.ext.FenrirExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Internal;

/**
 * Parent task for Fenrir gradle plugin.
 */
public abstract class AbstractFenrirTask extends DefaultTask {

    /**
     * Constructor : add the group by default.
     */
    public AbstractFenrirTask() {
        setGroup("Fenrir");
    }

    /**
     * Return the {@link FenrirExtension}.
     *
     * @return {@link FenrirExtension}
     */
    @Internal
    protected FenrirExtension getFenrirExtension() {
        return getProject().getExtensions().getByType(FenrirExtension.class);
    }

    /**
     * Return the {@link JavaPluginExtension}.
     *
     * @return {@link JavaPluginExtension}
     */
    @Internal
    protected JavaPluginExtension getJavaPluginExtension() {
        return getProject().getExtensions().getByType(JavaPluginExtension.class);
    }

    /**
     * Return the Java version use by the project.
     *
     * @return String
     */
    @Internal
    protected String getJavaVersion() {
        return getJavaPluginExtension().getTargetCompatibility().name().substring("VERSION_".length());
    }

    /**
     * Return the name of the project's library : projectName-projectVersion.jar.
     *
     * @return String
     */
    @Internal
    protected String getLibraryFullName() {
        final String version = (String) getProject().getVersion();
        String libName = getProject().getName();
        if (!version.equals("unspecified")) {
            libName += "-" + version;
        }
        return libName + ".jar";
    }
}
