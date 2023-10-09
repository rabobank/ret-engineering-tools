package io.rabobank.ret

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces

@ApplicationScoped
class RetContextConfiguration {

    companion object {
        @JvmField
        var RET_CONTEXT_INSTANCE = RetContext()
    }

    @Suppress("unused")
    @Produces
    private fun retContext(): RetContext = RET_CONTEXT_INSTANCE
}
