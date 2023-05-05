package io.rabobank.ret.configuration.version

import io.quarkus.runtime.StartupEvent
import org.jboss.logging.MDC
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes

@ApplicationScoped
class MdcConfig(private val versionProperties: VersionProperties) {

    fun onStart(@Observes ev: StartupEvent) {
        MDC.put("os", versionProperties.getOs())
        MDC.put("version", versionProperties.getAppVersion())
        MDC.put("commit", versionProperties.getCommitHash())
    }
}
