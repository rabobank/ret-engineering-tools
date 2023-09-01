package io.rabobank.ret.commands

import io.quarkus.runtime.annotations.RegisterForReflection
import io.rabobank.ret.RetContext
import io.rabobank.ret.configuration.RetConfig
import io.rabobank.ret.util.Logged
import picocli.CommandLine.Command
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.Spec
import java.io.File

@Command(
    name = "project",
    hidden = true,
)
@RegisterForReflection(targets = [RetContext::class])
@Logged
class ConfigureProjectCommand(
    private val retConfig: RetConfig,
) : Runnable {
    @Spec
    lateinit var commandSpec: CommandSpec

    override fun run() {
        val workingDir = File("").absoluteFile

        if (retConfig["projects"] == null) {
            retConfig["projects"] = listOf(Project(workingDir.name, workingDir.absolutePath))
        } else {
            @Suppress("UNCHECKED_CAST")
            val projectsMap = retConfig["projects"] as MutableList<Project>
            projectsMap += Project(workingDir.name, workingDir.absolutePath)
        }

        retConfig.save()
    }
}

data class Project(
    val name: String,
    val path: String,
)
