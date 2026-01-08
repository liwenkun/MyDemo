package me.liwenkun.buildSrc

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

internal abstract class ReadmeProcessTask : DefaultTask() {
    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:InputFile
    abstract val input: RegularFileProperty

    @TaskAction
    fun action() {
        // 使用 FileWriter，第二个参数 true 表示追加模式
        output.get().asFile.writeText(input.get().asFile.readText()
                + "\n\nCopyright 2026 liwenkun")
    }
}
