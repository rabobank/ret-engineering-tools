package io.rabobank.ret.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.rabobank.ret.RetConsole
import io.rabobank.ret.config.ExceptionMessageHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import picocli.CommandLine
import picocli.CommandLine.Model.CommandSpec

class ExceptionMessageHandlerTest {
    private val retConsole = mockk<RetConsole>(relaxed = true)
    private val exceptionHandler = ExceptionMessageHandler(retConsole)

    @Test
    fun `another exception results in exit code 1`() {
        val ex = IllegalStateException("Boom!")
        val commandLine =
            mockk<CommandLine> {
                every { usageMessage } returns "Use this correctly"
                every { commandSpec } returns CommandSpec.create()
            }

        val exitCode = exceptionHandler.handleExecutionException(ex, commandLine, mockk())

        verify { retConsole.errorOut("Boom!") }
        assertThat(exitCode).isEqualTo(1)
    }

    @Test
    fun `illegal argument results in exit code 2`() {
        val ex = IllegalArgumentException("Boom!")
        val commandLine =
            mockk<CommandLine> {
                every { usageMessage } returns "Use this correctly"
                every { commandSpec } returns CommandSpec.create()
            }

        val exitCode = exceptionHandler.handleExecutionException(ex, commandLine, mockk())

        verify { retConsole.errorOut("Boom!") }
        verify { retConsole.errorOut("Use this correctly") }
        assertThat(exitCode).isEqualTo(2)
    }
}
