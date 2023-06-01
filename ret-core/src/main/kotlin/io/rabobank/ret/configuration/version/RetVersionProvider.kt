package io.rabobank.ret.configuration.version

import io.quarkus.arc.Unremovable
import jakarta.enterprise.context.ApplicationScoped
import picocli.CommandLine

/**
 * Version Provider for PicoCLI
 *
 * This class provides the version info for the entry command. This will be displayed when executing `ret -v`.
 * Typically, you don't need this when creating a plugin.
 */
@Unremovable
@ApplicationScoped
class RetVersionProvider(private val versionProperties: VersionProperties = VersionProperties()) :
    CommandLine.IVersionProvider {

    override fun getVersion(): Array<String> {
        return arrayOf(
            "RET Version: ${versionProperties.getAppVersion()}",
            "Commit: ${versionProperties.getCommitHash()}",
            "OS: ${versionProperties.getOs()}",
        )
    }
}
