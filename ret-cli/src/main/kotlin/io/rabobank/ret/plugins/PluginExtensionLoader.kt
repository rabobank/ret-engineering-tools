package io.rabobank.ret.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.rabobank.ret.plugin.Plugin
import io.rabobank.ret.plugin.PluginDefinition
import io.rabobank.ret.util.OsUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.walk

@ApplicationScoped
class PluginExtensionLoader {

    companion object {
        const val PLUGIN_EXTENSION = "plugin"
    }

    @OptIn(ExperimentalPathApi::class)
    @Produces
    @ApplicationScoped
    fun plugins(osUtils: OsUtils, objectMapper: ObjectMapper): List<Plugin> =
        osUtils.getRetPluginsDirectory().let { pluginPath ->
            pluginPath.walk()
                .map(Path::toFile)
                .filter { it.extension == PLUGIN_EXTENSION }
                .map { objectMapper.readValue<PluginDefinition>(it.readText()) }
                .map { Plugin(it, pluginPath.resolve(it.dylibFile())) }
                .toList()
        }

    private fun PluginDefinition.dylibFile() = libName.takeIf { it.endsWith(".dylib") } ?: "$libName.dylib"
}
