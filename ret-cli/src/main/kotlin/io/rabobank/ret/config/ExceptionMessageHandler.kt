package io.rabobank.ret.config

import io.quarkus.logging.Log
import io.rabobank.ret.RetConsole
import picocli.CommandLine
import picocli.CommandLine.IExecutionExceptionHandler
import picocli.CommandLine.ParseResult

class ExceptionMessageHandler(private val retConsole: RetConsole) : IExecutionExceptionHandler {

    override fun handleExecutionException(ex: Exception, commandLine: CommandLine, parseResult: ParseResult) =
        if (ex is IllegalArgumentException) {
            Log.warn("Input error occurred", ex)
            ex.message?.let { retConsole.errorOut(it) }
            retConsole.errorOut(commandLine.usageMessage)
            commandLine.commandSpec.exitCodeOnInvalidInput()
        } else {
            ex.message?.let { retConsole.errorOut(it) }
            Log.error("An error occurred", ex)
            commandLine.commandSpec.exitCodeOnExecutionException()
        }
}
