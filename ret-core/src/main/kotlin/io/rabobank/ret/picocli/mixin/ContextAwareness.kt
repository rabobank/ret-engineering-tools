package io.rabobank.ret.picocli.mixin

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command
class ContextAwareness {

    @Option(
        names = ["--ignore-context-aware", "-ica"],
        description = ["Ignore context awareness"],
        scope = CommandLine.ScopeType.INHERIT
    )
    var ignoreContextAwareness: Boolean = false
}
