package io.rabobank.ret.picocli.mixin

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

/**
 * Command to be mixed in (@Mixin) with other commands that use some type of contaext awareness for autocompletion
 * (e.g. via [io.rabobank.ret.RetContext]).
 * Mixing in this command will automatically add a flag for ignoring the context,
 * which can be read out and used when performing custom autocompletion logic.
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
