package io.rabobank.ret.util

import org.apache.commons.lang3.SystemUtils.IS_OS_LINUX
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OsUtils {
    fun getHomeDirectory(): String =
        when {
            IS_OS_WINDOWS -> throw NotImplementedError("Windows is not supported yet.")
            IS_OS_LINUX || IS_OS_MAC -> System.getenv("HOME")
            else -> throw IllegalStateException("Unsupported operating system")
        }

    fun getRetHomeDirectory(): String = "${getHomeDirectory()}/.ret"
}
