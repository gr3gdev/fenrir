package fenrir.gradle.plugin.task;

import fenrir.gradle.plugin.ext.FenrirExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Internal;

public abstract class AbstractFenrirTask extends DefaultTask {
    public AbstractFenrirTask() {
        setGroup("Fenrir");
    }

    @Internal
    protected FenrirExtension getFenrirExtension() {
        return getProject().getExtensions().getByType(FenrirExtension.class);
    }

    @Internal
    protected JavaPluginExtension getJavaPluginExtension() {
        return getProject().getExtensions().getByType(JavaPluginExtension.class);
    }

    @Internal
    protected String getJavaVersion() {
        return getJavaPluginExtension().getTargetCompatibility().name().substring("VERSION_".length());
    }

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