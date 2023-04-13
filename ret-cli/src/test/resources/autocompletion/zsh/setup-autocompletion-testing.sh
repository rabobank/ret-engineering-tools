setopt interactivecomments

#### -----------------------------------------
#### Inspired by https://stackoverflow.com/questions/65386043/unit-testing-zsh-completion-script/69164362#69164362
#### -----------------------------------------
# Define our test function.
comptest () {
    # Gather all matching completions in this array.
    # -U discards duplicates.
    typeset -aU completions=()

    # Override the builtin compadd command.
    compadd () {
        # Gather all matching completions for this call in $reply.
        # Note that this call overwrites the specified array.
        # Therefore we cannot use $completions directly.
        builtin compadd -O REPLY "$@"

        completions+=("${REPLY[@]}") # Collect them.

        # Martin: This was done in the original script and works fine locally, but not when ran via the automated container script.
        # It does not seem to be needed for our cases..
        #builtin compadd "$@"       # Run the actual command.
    }

    # Bind a custom widget to TAB.
    bindkey "^I" complete-word
    zle -C complete-word complete-word complete-word

    complete-word () {
        # Make the completion system believe we're on a normal
        # command line, not in vared.
        unset 'compstate[vared]'

        _main_complete "$@"

        # Print out our completions.
        # Use of ^B and ^C as delimiters here is arbitrary.
        # Just use something that won't normally be printed.
        print -n $'\C-B'
        print -nlr -- "${completions[@]}"  # Print one per line.
        print -n $'\C-C'
        exit
    }

    vared -c tmp
}
#### -----------------------------------------
#### -----------------------------------------
#### -----------------------------------------

# Mock the ret binary service
ret () {
    local command=$*

    typeset -A mock
    source /tmp/ret-stub-data

    for key value_string in "${(@kv)mock}"; do
        local regex_pattern="${key//\'/}"
        if [[ $command =~ $regex_pattern ]]; then
            local values=("${(@f)$(echo $value_string)}") # split string on newlines
            for value in "${values[@]}"; do echo "$value"; done # echo every candidate separately
            echo $command >> /tmp/matched-ret-stub-calls
            return
        fi
    done

    echo $command >> /tmp/unmatched-ret-stub-calls
}

zmodload zsh/zpty  # Load the pseudo terminal module.
source /tmp/_ret # Load the autocompletion script.
