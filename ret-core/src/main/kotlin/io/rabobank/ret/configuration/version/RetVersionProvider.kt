package io.rabobank.ret.configuration.version

import io.quarkus.arc.Unremovable
import picocli.CommandLine
import javax.enterprise.context.ApplicationScoped

@Unremovable
@ApplicationScoped
class RetVersionProvider(private val versionProperties: VersionProperties = VersionProperties()) :
    CommandLine.IVersionProvider {

    override fun getVersion(): Array<String> {
        return arrayOf(
            "RET Version: ${versionProperties.getAppVersion()}",
            "Commit: ${versionProperties.getCommitHash()}",
            "OS: ${versionProperties.getOs()}"
        )
    }
}
