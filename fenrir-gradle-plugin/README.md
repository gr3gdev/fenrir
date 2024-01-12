# fenrir.gradle.plugin

The Gradle plugin for develop a project with Fenrir.
This plugin extends of the JavaPlugin.

## Usage

Add plugin :

```groovy
plugins {
    id('fenrir.gradle.plugin')
}
```

Configure the plugin :

```groovy
fenrir {
    mainClass = "my.package.MyMainClass"
    imageName = "my-docker-image"
}
```

## Tasks

### prepareSources

Depends on the task "build".

This task prepare sources files with all dependencies for construct an optimized docker image.

### listJavaDependencies

Depends on the task "prepareSources".

This task list all java module require by the project.

### buildDockerImage

Depends on the task "listJavaDependencies".

This task make an optimized docker image with a small JRE for execute the project.
