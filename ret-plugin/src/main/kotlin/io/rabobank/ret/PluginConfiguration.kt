package io.rabobank.ret

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

@ApplicationScoped
class PluginConfiguration {

    companion object {
        @JvmField
        var RET_CONTEXT_INSTANCE: RetContext = RetContext()
    }

    @Produces
    private fun retContext(): RetContext {
        this.javaClass.typeName
        return RET_CONTEXT_INSTANCE
    }
}
