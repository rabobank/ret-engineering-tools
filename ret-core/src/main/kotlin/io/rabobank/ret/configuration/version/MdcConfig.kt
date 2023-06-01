package io.rabobank.ret.configuration.version

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.MDC

/**
 * MDC configuration
 *
 * Sets the version properties into the MDC, to enrich the log messages.
 * Typically, you don't use this explicitly when creating a plugin.
 */
@ApplicationScoped
class MdcConfig(private val versionProperties: VersionProperties) {

    fun onStart(@Observes ev: StartupEvent) {
        MDC.put("os", versionProperties.getOs())
        MDC.put("version", versionProperties.getAppVersion())
        MDC.put("commit", versionProperties.getCommitHash())
    }
}
