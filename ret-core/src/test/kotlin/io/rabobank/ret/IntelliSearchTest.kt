package io.rabobank.ret

import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@QuarkusTest
internal class IntelliSearchTest {

    companion object {
        @JvmStatic
        fun arguments(): List<Arguments> {
            return listOf(
                Arguments.of("upa", "user profile aggregator", true),
                Arguments.of("upa", "user-profile-aggregator", true),
                Arguments.of("uuuupa", "user-profile-aggregator", false),
                Arguments.of("UPA", "user-profile-aggregator", true),
                Arguments.of("UPA", "uPa", true),
                Arguments.of("uPa", "user-profile-aggregator", true),
                Arguments.of("upa", "userProfileAggregator", true),
                Arguments.of("upa", "user-ProFile-Aggregator", true),
                Arguments.of("upa", "user-ProfileAggregator", false),
                Arguments.of("user-pr", "user-profile-aggregator", true),
                Arguments.of("user-pr", "user profile aggregator", true),
                Arguments.of("rofile", "user profile aggregator", true),
                Arguments.of("userpa", "user profile aggregator", true),
                Arguments.of("uproagg", "user profile aggregator", true),
                Arguments.of("upa", "update user profile aggregator", true),
                Arguments.of("upagg", "upa user profile aggregator", true),
                Arguments.of("upa", "update user profile perforator", false),
                Arguments.of("uepa", "user profile aggregator", false),
                Arguments.of("upat", " user pa test upa", true),
                Arguments.of("upatupat", "user pa test upa user pa test upa", true),
                Arguments.of("upat", "test upa", false)
            )
        }
    }

    private val target = IntelliSearch()

    @ParameterizedTest
    @MethodSource("arguments")
    fun test(filter: String, candidate: String, expectedValue: Boolean) {
        assertThat(target.matches(filter, candidate)).isEqualTo(expectedValue)
    }
}
