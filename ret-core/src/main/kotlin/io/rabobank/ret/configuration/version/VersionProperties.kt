package io.rabobank.ret.configuration.version

import io.quarkus.logging.Log
import org.eclipse.microprofile.config.ConfigProvider
import java.util.*
import javax.enterprise.context.ApplicationScoped

private const val GIT_PROPERTIES = "git.properties"

@ApplicationScoped
class VersionProperties {

    private val properties: Properties = Properties()
    private val config = ConfigProvider.getConfig()

    init {
        loadGitProperties()
    }

    fun getCommitHash(): String = properties.getProperty("git.commit.id.full", "unknown")

    fun getAppVersion(): String = config.getOptionalValue("quarkus.application.version", String::class.java).orElse("unknown")

    fun getOs(): String {
        val osName = config.getOptionalValue("os.name", String::class.java).orElse("unknown")
        val osVersion = config.getOptionalValue("os.version", String::class.java).orElse("unknown")
        val osArch = config.getOptionalValue("os.arch", String::class.java).orElse("unknown")

        return "$osName ($osVersion) $osArch"
    }

    private fun loadGitProperties() {
        val inputStream = VersionProperties::class.java.classLoader.getResourceAsStream(GIT_PROPERTIES)

        if (inputStream != null) {
            properties.load(inputStream)
        } else {
            Log.error("No Git information available: cannot load git.properties file.")
        }
    }
}
