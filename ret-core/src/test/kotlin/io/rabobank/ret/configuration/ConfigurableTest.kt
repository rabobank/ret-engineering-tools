package io.rabobank.ret.configuration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.rabobank.ret.RetConsole
import io.rabobank.ret.util.OsUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createDirectories

@ExtendWith(MockKExtension::class)
class ConfigurableTest {
    @SpyK
    private var osUtils = OsUtils()

    @MockK
    private lateinit var retConfig: RetConfig

    @MockK
    private lateinit var retConsole: RetConsole

    private val pluginName = "test"
    private val objectMapper = jacksonObjectMapper()

    @TempDir
    lateinit var mockUserHomeDirectory: Path

    @BeforeEach
    fun setUp() {
        mockUserHomeDirectory.createDirectories()

        every { osUtils.getRetHomeDirectory() } returns mockUserHomeDirectory
    }

    @Test
    fun `migrate configuration with key rename automatically`() {
        val testConfig = TestConfig()
        testConfig.pluginName = pluginName
        testConfig.osUtils = osUtils
        testConfig.objectMapper = objectMapper
        testConfig.retConfig = retConfig
        testConfig.retConsole = retConsole

        every { retConfig["key_1"] } returns "value 1"
        every { retConfig["old_key_2"] } returns "value 2"
        justRun { retConfig.remove(any()) }
        justRun { retConfig.save() }

        assertThat(testConfig.pluginConfig.get<String>("key_1")).isEqualTo("value 1")
        assertThat(testConfig.pluginConfig.get<String>("new_key_2")).isEqualTo("value 2")
    }

    inner class TestConfig : Configurable() {
        override fun properties() = listOf(ConfigurationProperty("key_1", "Question 1"))

        override fun keysToMigrate() = listOf("old_key_2" to "new_key_2")
    }
}
