package io.rabobank.ret.util

/**
 * Regex Utilities
 *
 * Object with some constant regex patterns
 * @property DIGITS_PATTERN regex pattern for matching numbers of any length
 */
object RegexUtils {
    val DIGITS_PATTERN: Regex = "^\\d*$".toRegex()
}
