package io.rabobank.ret.util

object RegexUtils {
    /**
     * Regex pattern for matching numbers of any length.
     */
    val DIGITS_PATTERN: Regex = "^\\d*$".toRegex()
}
