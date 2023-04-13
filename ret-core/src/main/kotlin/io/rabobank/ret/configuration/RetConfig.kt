package io.rabobank.ret.configuration

import io.rabobank.ret.util.OsUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.nio.file.Path
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance

private const val RET_VERSION = "ret_config_version"

@ApplicationScoped
class RetConfig(
    osUtils: OsUtils,
    private val configurables: Instance<Configurable>,
    @ConfigProperty(name = "quarkus.application.version") private val retVersion: String
) : Config {
    private final val configFile = Path.of(osUtils.getHomeDirectory(), ".ret", "ret.config").toFile()
    private final val properties = Properties()

    init {
        loadExistingProperties()
    }

    private fun loadExistingProperties() {
        if (configFile.exists()) {
            properties.load(configFile.inputStream())
        }
    }

    override operator fun get(key: String) = properties[key] as String?

    override operator fun set(key: String, value: String) {
        properties[key] = value
    }

    override fun configure(function: (ConfigurationProperty) -> Unit) {
        configurables.flatMap { it.properties() }.forEach(function)
        save()
    }

    private fun save() {
        properties[RET_VERSION] = retVersion
        properties.store(configFile.outputStream(), "")
    }

    override fun configFile(): Path = Path.of(configFile.toURI())

}
