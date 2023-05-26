package io.rabobank.ret

import jakarta.enterprise.context.ApplicationScoped
import picocli.CommandLine.ParseResult

/**
 * The Ret Console (to be injected)
 *
 * This class is used to interact with the user by outputting messages, errors and prompting for responses.
 */
@ApplicationScoped
class RetConsole(parseResult: ParseResult) {

    private val commandLine = parseResult.commandSpec().commandLine()

    /**
     * Prints the [message] to the user
     */
    fun out(message: String) {
        commandLine.out.println(message)
    }

    /**
     * Prints the [message], formatted as error
     */
    fun errorOut(message: String) {
        commandLine.err.println(commandLine.colorScheme.errorText(message))
    }

    /**
     * Prompts the user with [message] and mentions the [currentValue], if provided
     * @return the answer, as entered by the user
     */
    fun prompt(message: String, currentValue: String?): String {
        val messageWithDefault = if (currentValue.isNullOrEmpty()) message else "$message [$currentValue]"
        out(messageWithDefault)
        return readln()
    }
}
