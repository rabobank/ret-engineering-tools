function _contains_any_flag() {
    for flag in "$@"; do
        if [[ -v "opt_args[$flag]" ]]; then
            return 0
        fi
    done
    return 1
}

# returns 0 if the first argument starts with - or --
function _matches_flag_syntax() {
    if [[ $1 =~ ^\-{1,2} ]]; then return 0; fi
    return 1
}

function _call_function_if_exists() {
    function=$1
    type "$function" &>/dev/null && "$function";
}

typeset -A RET_COMBINED_OPT_ARGS

# Initialize Zsh autocompletion system
autoload -Uz compinit
compinit

compdef _ret ret
