package io.rabobank.ret

import picocli.CommandLine.ParseResult
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class RetConsole(parseResult: ParseResult) {

    private val commandLine = parseResult.commandSpec().commandLine()

    fun out(message: String) {
        commandLine.out.println(message)
    }

    fun errorOut(message: String) {
        commandLine.err.println(commandLine.colorScheme.errorText(message))
    }

    fun prompt(message: String, currentValue: String?): String {
        val messageWithDefault = if (currentValue.isNullOrEmpty()) message else "$message [$currentValue]"
        out(messageWithDefault)
        return readln()
    }
}
