package io.rabobank.ret.picocli.mixin

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

/**
 * Ignore Context Awareness
 *
 * Command to be mixed in (@Mixin) with other commands that use context awareness.
 * Mixing in this command will automatically add a flag for ignoring the context.
 */
@Command
class ContextAwareness {

    @Option(
        names = ["--ignore-context-aware", "-ica"],
        description = ["Ignore context awareness"],
        scope = CommandLine.ScopeType.INHERIT,
    )
    var ignoreContextAwareness: Boolean = false
}
