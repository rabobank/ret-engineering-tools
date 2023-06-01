package io.rabobank.ret.configuration.version

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.MDC

/**
 * MDC configuration
 *
 * Sets the version properties into the MDC, to enrich the log messages.
 * This is done automatically, so you don't need to use this class in your plugin.
 */
@ApplicationScoped
class MdcConfig(private val versionProperties: VersionProperties) {

    fun onStart(@Observes ev: StartupEvent) {
        MDC.put("os", versionProperties.getOs())
        MDC.put("version", versionProperties.getAppVersion())
        MDC.put("commit", versionProperties.getCommitHash())
    }
}
