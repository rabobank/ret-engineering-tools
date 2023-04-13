package io.rabobank.ret.util

import org.apache.commons.lang3.SystemUtils.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BrowserUtils {

    fun openUrl(url: String) {
        when {
            IS_OS_WINDOWS -> openUrlWindows(url)
            IS_OS_LINUX -> throw NotImplementedError("Linux support is not available yet.")
            IS_OS_MAC -> openUrlMacOs(url)
        }
    }

    private fun openUrlWindows(url: String) {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $url")
    }

    private fun openUrlMacOs(url: String) {
        Runtime.getRuntime().exec("open $url")
    }
}
