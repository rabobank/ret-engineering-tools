package io.rabobank.ret.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import io.rabobank.ret.plugin.Plugin
import io.rabobank.ret.plugin.PluginDefinition
import io.rabobank.ret.util.OsUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.walk

@ApplicationScoped
class PluginConfiguration {

    companion object {
        const val PLUGIN_EXTENSION = "plugin"
    }

    @OptIn(ExperimentalPathApi::class)
    @Produces
    @ApplicationScoped
    fun plugins(osUtils: OsUtils, objectMapper: ObjectMapper): List<Plugin> {
        val pluginPath = Path.of(osUtils.getHomeDirectory(), ".ret", "plugins")

        return pluginPath
            .walk()
            .map { it.toFile() }
            .filter { it.extension == PLUGIN_EXTENSION }
            .map { objectMapper.readValue(it.readText(), PluginDefinition::class.java) }
            .map { Plugin(it, pluginPath.resolve(it.libName)) }
            .toList()
    }
}
