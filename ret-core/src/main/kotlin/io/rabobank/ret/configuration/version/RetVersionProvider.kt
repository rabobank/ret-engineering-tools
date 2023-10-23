package io.rabobank.ret.configuration.version

import io.quarkus.arc.Unremovable
import jakarta.enterprise.context.ApplicationScoped
import picocli.CommandLine

/**
 * Version Provider for PicoCLI
 *
 * This class provides the version info for the entry command. This will be displayed when executing `ret -v`.
 * This version flag is only available at the root command `ret` by default,
 * but you could do something similar for your plugin.
 */
@Unremovable
@ApplicationScoped
class RetVersionProvider(private val versionProperties: VersionProperties = VersionProperties()) :
    CommandLine.IVersionProvider {
    override fun getVersion() =
        arrayOf(
            "RET Version: ${versionProperties.getAppVersion()}",
            "Commit: ${versionProperties.getCommitHash()}",
            "OS: ${versionProperties.getOs()}",
        )
}
