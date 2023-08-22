package io.rabobank.ret.util

import jakarta.enterprise.context.ApplicationScoped
import org.apache.commons.lang3.SystemUtils.IS_OS_LINUX
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import java.nio.file.Path

/**
 * Utils for the Operating System
 *
 * Class used to interact with the operating system, independent of underlying operating system.
 */
@ApplicationScoped
class OsUtils {
    /**
     * Get the path of home directory of the user, independent of OS.
     * @return the home directory of the user.
     */
    fun getHomeDirectory(): String =
        when {
            IS_OS_WINDOWS -> throw NotImplementedError("Windows is not supported yet.")
            IS_OS_LINUX || IS_OS_MAC -> System.getenv("HOME")
            else -> error("Unsupported operating system")
        }

    /**
     * Get the path of the directory where RET files and configurations are stored (not configurable).
     * @return the RET directory.
     */
    fun getRetHomeDirectory(): Path = Path.of(getHomeDirectory(), ".ret")

    /**
     * Get the path of the directory where RET plugins and their configurations are stored (not configurable).
     * @return the RET plugins directory.
     */
    fun getRetPluginsDirectory(): Path = getRetHomeDirectory().resolve("plugins")

    /**
     * Get the path of where the plugin configuration is stored (not configurable).
     * @return the RET plugin configuration path.
     */
    fun getPluginConfig(pluginName: String): Path = getRetPluginsDirectory().resolve("$pluginName.json")
}
