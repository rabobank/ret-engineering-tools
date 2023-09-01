package io.rabobank.ret.configuration.version

import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.ConfigProvider
import java.util.Properties

private const val GIT_PROPERTIES = "git.properties"

/**
 * Version information
 *
 * This class provides version information about the current RET instance and OS of the user.
 */
@ApplicationScoped
class VersionProperties {

    private val properties = Properties()
    private val config = ConfigProvider.getConfig()

    init {
        loadGitProperties()
    }

    /**
     * Get the latest commit hash of this ret release.
     * @return the latest commit hash.
     */
    fun getCommitHash(): String = properties.getProperty("git.commit.id.full", "unknown")

    /**
     * Get the version number of this ret release.
     * @return the current installed version number.
     */
    fun getAppVersion(): String =
        config.getOptionalValue("quarkus.application.version", String::class.java).orElse("unknown")

    /**
     * Get info about the current operating system.
     * @return concatenated string of the current OS, version and architecture.
     */
    fun getOs(): String {
        val osName = config.getOptionalValue("os.name", String::class.java).orElse("unknown")
        val osVersion = config.getOptionalValue("os.version", String::class.java).orElse("unknown")
        val osArch = config.getOptionalValue("os.arch", String::class.java).orElse("unknown")

        return "$osName ($osVersion) $osArch"
    }

    private fun loadGitProperties() {
        VersionProperties::class.java.classLoader.getResourceAsStream(GIT_PROPERTIES)
            ?.run { properties.load(this) }
            ?: Log.debug("No Git information available: cannot load git.properties file.")
    }
}
