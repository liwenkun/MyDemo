package me.liwenkun.buildSrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty

interface MyPluginExtension {
    val readmeFile: RegularFileProperty
    val outputFile: RegularFileProperty
}

class MyPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("MyPlugin", MyPluginExtension::class.java)
        extension.readmeFile.convention(target.rootProject.layout.projectDirectory.file("README.md"))
        extension.outputFile.convention(target.rootProject.layout.buildDirectory.file("processedReadme.md"))
        target.tasks.register("readmeProcess",
            ReadmeProcessTask::class.java, {
                it.input.set(extension.readmeFile)
                it.output.set(extension.outputFile)
            })
    }
}
