package io.rabobank.ret.util

import jakarta.enterprise.context.ApplicationScoped
import org.apache.commons.lang3.SystemUtils.IS_OS_LINUX
import org.apache.commons.lang3.SystemUtils.IS_OS_MAC
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import java.net.URL

/**
 * Utils to open a Browser
 *
 * Class used to open urls in an internet browser, independent of underlying operating system.
 */
@ApplicationScoped
class BrowserUtils {

    /**
     * Open the provided [url] in a browser.
     */
    fun openUrl(url: String) {
        when {
            IS_OS_WINDOWS -> openUrlWindows(url)
            IS_OS_LINUX -> throw NotImplementedError("Linux support is not available yet.")
            IS_OS_MAC -> openUrlMacOs(url)
        }
    }

    /**
     * Open the provided [url] in a browser.
     */
    fun openUrl(url: URL) {
        openUrl(url.toString())
    }

    private fun openUrlWindows(url: String) {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $url")
    }

    private fun openUrlMacOs(url: String) {
        Runtime.getRuntime().exec("open $url")
    }
}
