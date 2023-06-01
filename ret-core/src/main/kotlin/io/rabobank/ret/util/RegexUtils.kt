package io.rabobank.ret.util

object RegexUtils {
    /**
     * regex pattern for matching numbers of any length.
     */
    val DIGITS_PATTERN: Regex = "^\\d*$".toRegex()
}
