package io.rabobank.ret.util

import jakarta.enterprise.context.ApplicationScoped
import org.apache.commons.lang3.SystemUtils.IS_OS_LINUX
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS

/**
 * Utils for the Operating System (to be injected)
 *
 * Class used to interact with the operating system, independent of underlying operating system.
 */
@ApplicationScoped
class OsUtils {
    /**
     * Get the path of home directory of the user, independent of OS
     * @return the home directory of the user
     */
    fun getHomeDirectory(): String =
        when {
            IS_OS_WINDOWS -> throw NotImplementedError("Windows is not supported yet.")
            IS_OS_LINUX || IS_OS_MAC -> System.getenv("HOME")
            else -> throw IllegalStateException("Unsupported operating system")
        }

    /**
     * Get the path of the directory where ret files and configurations are stored (not configurable)
     * @return the ret directory
     */
    fun getRetHomeDirectory(): String = "${getHomeDirectory()}/.ret"
}
