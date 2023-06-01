package io.rabobank.ret.configuration.version

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.ConfigProvider
import java.util.Properties

private const val GIT_PROPERTIES = "git.properties"

/**
 * Version information (to be injected)
 *
 * This class provides version information about the current RET instance and OS of the user.
 */
@ApplicationScoped
class VersionProperties {

    private val properties: Properties = Properties()
    private val config = ConfigProvider.getConfig()

    init {
        loadGitProperties()
    }

    /**
     * Get the latest commit hash of this ret release
     * @return the latest commit hash
     */
    fun getCommitHash(): String = properties.getProperty("git.commit.id.full", "unknown")

    /**
     * Get the version number of this ret release
     * @return the current installed version number
     */
    fun getAppVersion(): String = config.getOptionalValue("quarkus.application.version", String::class.java).orElse("unknown")

    /**
     * Get info about the current operating system.
     * @return concatenated string of the current OS, version and architecture
     */
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
