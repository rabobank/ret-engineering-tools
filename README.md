# RET Engineering Tools

RET is _the_ utility for all developers!
It allows you to interact easily with all tools you use from day to day, like Azure DevOps, Splunk, SignalFX and more.
RET can be invoked from the command line, but also from integrations, such as Alfred (more integrations are planned).
To ensure RET is easy to use, it has smart autocompletion and is context aware.

Directly navigate to one of your repositories?
Or create a PR from the terminal where you just pushed your latest changes?
Or directly query Splunk?
Use RET!

## Getting started

### OS Support

| OS      | Status          | Autocompletion |
|---------|-----------------|----------------|
| MacOS   | amd64 & aarch64 | ZSH            |
| Linux   | pending         | -              |
| Windows | pending         | -              |

### MacOS Install

**Homebrew**

Install RET

```shell
brew tap rabobank/tap # Add Rabobank brew tap
brew install ret      # Install RET
```

Setup autocomplete
```shell
ret configure autocomplete zsh # This will output a command you have to run
```

## What makes RET so special?

To make RET intuitive and fast, we have three major aspects that are used throughout all RET commands.

### Context-awareness

RET tries to be aware of the project that you're executing commands from.
This ensures that RET will only show you the most relevant results.

For example, if you're in the folder of a Git repository, RET will, by default, only show the result of pull requests for that particular repository.
To disable the context-awareness, add `-ica` or `--ignore-context-aware` to the command.

### CLI Autocompletion

RET auto-completes commands, flags and arguments when you press the `TAB` key (see animation above).
This brings productivity to a next level.

The autocompletion system in combination with Context-awareness and IntelliSearch, enables you to quickly execute the right command.
In the background, RET will do API calls to ensure that the autocompletion results are relevant and up-to-date.

### IntelliSearch

If you're searching for an open pull request from a repository that contains the word user, you simply type `ret pr open user<TAB>`. You can also search for letters, i.e. `ret pr open mds<TAB>`
results in showing pull requests for the repository called `my-demo-service`.

The search is based on how other smart searches and filters are built, such like the one in IntelliJ IDEA.
Roughly that means that you can search on a part of the word, or on (some of) the first letters of parts of the words, case-insensitively.

For example, `RET` resolves to Ret-Engineering-Tools and RET-plugins.

