#!/bin/zsh
function _ret() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1: ret subcommands:->subcommands_overview" \
        "*::arg:->call_subcommand" \
        '(-h --help)'{-h,--help}'[Show help information]' \
        '(-v --version)'{-v,--version}'[print version information]'
    RET_COMBINED_OPT_ARGS=()
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        subcommands_overview) _values "autocompletion candidates" 'configure[Initialize or update RET configuration file]' 'plugin[Initialize or update RET plugins]' 'splunk[Plugin to interact with Splunk]';;
        call_subcommand) function="_ret_${line[1]}"; _call_function_if_exists "$function";;
    esac
}
function _ret_configure() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1: ret configure subcommands:->subcommands_overview" \
        "*::arg:->call_subcommand" \
        '(-h --help)'{-h,--help}'[Show help information]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        subcommands_overview) _values "autocompletion candidates" 'autocomplete[Prints the command to install autocomplete. Supported shells are: zsh]' 'autocomplete-zsh[Prints the autocompletion script for ZSH]';;
        call_subcommand) function="_ret_configure_${line[1]}"; _call_function_if_exists "$function";;
    esac
}
function _ret_configure_autocomplete() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1:shells:->parameter0" \
        '(-h --help)'{-h,--help}'[Show help information]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        parameter0) _values "autocompletion candidates" 'zsh';;
    esac
}
function _ret_configure_autocomplete-zsh() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        '(-h --help)'{-h,--help}'[Show help information]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        
    esac
}
function _ret_plugin() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1: ret plugin subcommands:->subcommands_overview" \
        "*::arg:->call_subcommand" \
        '(-h --help)'{-h,--help}'[Show help information]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        subcommands_overview) _values "autocompletion candidates" 'initialize[Initialize plugin]';;
        call_subcommand) function="_ret_plugin_${line[1]}"; _call_function_if_exists "$function";;
    esac
}
function _ret_plugin_initialize() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1: : " \
        '(-h --help)'{-h,--help}'[Show help information]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        
    esac
}
function _ret_splunk() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1: ret splunk subcommands:->subcommands_overview" \
        "*::arg:->call_subcommand" \
        '(-h --help)'{-h,--help}'[Show help information]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        subcommands_overview) _values "autocompletion candidates" 'autocomplete[]' 'search[Open Splunk search results]';;
        call_subcommand) function="_ret_splunk_${line[1]}"; _call_function_if_exists "$function";;
    esac
}
function _ret_splunk_autocomplete() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        "1: ret splunk autocomplete subcommands:->subcommands_overview" \
        "*::arg:->call_subcommand" \
        '(-h --help)'{-h,--help}'[Show help information]' \
        '(-ica --ignore-context-aware)'{-ica,--ignore-context-aware}'[]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        subcommands_overview) _values "autocompletion candidates" 'splunk-app-name[]' 'splunk-index[]';;
        call_subcommand) function="_ret_splunk_autocomplete_${line[1]}"; _call_function_if_exists "$function";;
    esac
}
function _ret_splunk_autocomplete_splunk-app-name() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        '(-h --help)'{-h,--help}'[Show help information]' \
        '(-ica --ignore-context-aware)'{-ica,--ignore-context-aware}'[]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        
    esac
}
function _ret_splunk_autocomplete_splunk-index() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        '(-a --app)'{-a,--app}'=[]:option:->' \
        '(-h --help)'{-h,--help}'[Show help information]' \
        '(-ica --ignore-context-aware)'{-ica,--ignore-context-aware}'[]' \
        '(-w --word)'{-w,--word}'=[]:option:->'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        
    esac
}
function _ret_splunk_search() {
    local context state state_descr line
    local curcontext="$curcontext"
    typeset -A opt_args
    _arguments -C \
        '(-a --app)'{-a,--app}'=[]:option:->option0' \
        '(-h --help)'{-h,--help}'[Show help information]' \
        '(-i --index)'{-i,--index}'=[]:option:->option2' \
        '(-ica --ignore-context-aware)'{-ica,--ignore-context-aware}'[]'
    
    # merge args from earlier functions with this one
    for key in "${(@k)opt_args}"; do; RET_COMBINED_OPT_ARGS+=( [$key]=${opt_args[$key]} ); done
    case "$state" in
        option0) _autocomplete_splunk_app:;;
        option2) _autocomplete_splunk_index:;;
    esac
}

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

function _autocomplete_splunk_app() {
    # Get the possible prefix for the values. For example "-a=" or "" when -a is followed by a space rather than "="
    local flag_word_prefix=$([[ ${words[$CURRENT]} =~ ^.*= ]] && echo -n "$MATCH")
    # Get the value from the option -a or else --app
    local app_flag_value=${RET_COMBINED_OPT_ARGS[-a]-${RET_COMBINED_OPT_ARGS[--app]}}
    local index_flag_value=${RET_COMBINED_OPT_ARGS[-i]-${RET_COMBINED_OPT_ARGS[--index]}}
    desc=("${(@f)$(RET_ENV=ZSH ret splunk autocomplete splunk-app-name --index="${index_flag_value}" --word="${app_flag_value}")}")
    vals=( ${desc%%:*} )
    compadd -p "$flag_word_prefix" -d desc -aQU vals
    compstate[insert]=menu # no expand
}

function _autocomplete_splunk_index() {
    # Get the possible prefix for the values. For example "-i=" or "" when -i is followed by a space rather than "="
    local flag_word_prefix=$([[ ${words[$CURRENT]} =~ ^.*= ]] && echo -n "$MATCH")
    # Get the value from the option -i or else --index
    local index_flag_value=${RET_COMBINED_OPT_ARGS[-i]-${RET_COMBINED_OPT_ARGS[--index]}}
    local app_flag_value=${RET_COMBINED_OPT_ARGS[-a]-${RET_COMBINED_OPT_ARGS[--app]}}
    desc=("${(@f)$(RET_ENV=ZSH ret splunk autocomplete splunk-index --app="${app_flag_value}" --word="${index_flag_value}")}")
    vals=( ${desc%%:*} )
    compadd -p "$flag_word_prefix" -d desc -aQU vals
    compstate[insert]=menu # no expand
}
