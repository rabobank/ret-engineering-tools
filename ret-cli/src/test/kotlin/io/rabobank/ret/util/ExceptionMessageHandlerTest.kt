package nl.rabobank.ret.util

import io.quarkus.test.junit.QuarkusTest
import io.rabobank.ret.RetConsole
import io.rabobank.ret.config.ExceptionMessageHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import picocli.CommandLine
import picocli.CommandLine.Model.CommandSpec

@QuarkusTest
class ExceptionMessageHandlerTest {

    private val retConsole: RetConsole = mock()
    private val exceptionHandler: ExceptionMessageHandler = ExceptionMessageHandler(retConsole)

    @Test
    fun `another exception results in exit code 1`() {
        val ex = IllegalStateException("Boom!")
        val commandLine = mock<CommandLine>()
        whenever(commandLine.usageMessage).thenReturn("Use this correctly")
        whenever(commandLine.commandSpec).thenReturn(CommandSpec.create())

        val exitCode = exceptionHandler.handleExecutionException(ex, commandLine, mock())

        verify(retConsole).errorOut("Boom!")
        assertThat(exitCode).isEqualTo(1)
    }

    @Test
    fun `illegal argument results in exit code 2`() {
        val ex = IllegalArgumentException("Boom!")
        val commandLine = mock<CommandLine>()
        whenever(commandLine.usageMessage).thenReturn("Use this correctly")
        whenever(commandLine.commandSpec).thenReturn(CommandSpec.create())

        val exitCode = exceptionHandler.handleExecutionException(ex, commandLine, mock())

        verify(retConsole).errorOut("Boom!")
        verify(retConsole).errorOut("Use this correctly")
        assertThat(exitCode).isEqualTo(2)
    }
}
