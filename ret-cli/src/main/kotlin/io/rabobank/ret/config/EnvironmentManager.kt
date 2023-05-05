package io.rabobank.ret.config

import io.quarkus.logging.Log
import io.rabobank.ret.config.Environment.CLI
import io.rabobank.ret.config.Environment.valueOf
import org.eclipse.microprofile.config.inject.ConfigProperty
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class EnvironmentManager {

    @Produces
    fun environment(@ConfigProperty(name = "ret.env", defaultValue = "CLI") retEnvironment: String): Environment = try {
        Log.debug("ret.env value is $retEnvironment")
        valueOf(retEnvironment)
    } catch (e: Exception) {
        CLI
    }
}

enum class Environment(val recordMetrics: Boolean) {
    CLI(true),
    ZSH_AUTOCOMPLETE(false),
    ALFRED(true),
    ALFRED_AUTOCOMPLETE(false);
}
