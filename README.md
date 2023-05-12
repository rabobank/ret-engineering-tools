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

## Create your own plugin
### Requirements
- Java/Kotlin
- Quarkus 3
- Maven (Gradle can be used, but we do not provide an example)
- GraalVM

### Step 1 - Create an empty Quarkus project

Use the [Quarkus initializer](https://code.quarkus.io/) to create a Quarkus project.
- No additional dependencies are required.
- Use Maven as build tool
- Enter your group and artifact names

### Step 2 - Add the ret-plugin dependency

```xml

<dependency>
    <groupId>io.github.rabobank</groupId>
    <artifactId>ret-plugin</artifactId>
    <version>${ret-plugin.version}</version>
</dependency>
```

### Step 3 - Create entry command

```kotlin
@TopCommand
@CommandLine.Command(
    name = "my-plugin", (1)
    description = ["My custom plugin"],
    subcommands = [
        PluginInitializeCommand::class, (2)
        MySubCommand::class,
        MyOtherSubcommand::class
    ]
)
@RegisterForReflection(targets = [RetContext::class]) (3)
class SplunkEntryCommand {
}
```
- (1): Enter the name of your plugin. This is the command that will be exposed to RET. For example:
        `ret my-plugin my-subcommand`
- (2): Make sure to add the `PluginInitializeCommand::class`. This exposes a command which is necessary for RET to
  initialize the plugin.
- (3): Register the `RetContext::class` for reflection. Otherwise, it will not be available in your binary.

### Step 4 - Test your plugin
You can use the Quarkus interactive shell to test your plugin:

```shell
mvn quarkus:dev
```

### Step 5 - Native build

To create your plugin library, it needs to be natively compiled. Add the following profile to your pom.xml

```xml
<profiles>
        <profile>
            <id>native-plugin</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>io.quarkus.platform</groupId>
                        <artifactId>quarkus-maven-plugin</artifactId>
                        <extensions>true</extensions>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>build</goal>
                                    <goal>generate-code</goal>
                                    <goal>generate-code-tests</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-antrun-plugin</artifactId>
                        <version>3.1.0</version>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <configuration>
                                    <target>
                                        <exec failonerror="true" executable="/bin/bash" dir="${project.build.directory}/native-sources">
                                            <arg value="-c"/>
                                            <arg value="native-image $(cat native-image.args)"/>
                                        </exec>
                                        <copy file="${project.build.directory}/native-sources/${project.build.finalName}.${lib.extension}"
                                              tofile="${project.build.directory}/${project.build.finalName}.${lib.extension}"/>
                                    </target>
                                </configuration>
                                <goals>
                                    <goal>run</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
            <properties>
                <quarkus.banner.enabled>false</quarkus.banner.enabled>
                <quarkus.log.level>warn</quarkus.log.level>
                <quarkus.package.type>native-sources</quarkus.package.type>
                <quarkus.package.add-runner-suffix>false</quarkus.package.add-runner-suffix>
                <quarkus.native.additional-build-args>--shared</quarkus.native.additional-build-args>
            </properties>
        </profile>
        <profile>
            <id>unix</id>
            <activation>
                <os>
                    <family>unix</family>
                </os>
            </activation>
            <properties>
                <lib.extension>so</lib.extension>
            </properties>
        </profile>
        <profile>
            <id>windows</id>
            <activation>
                <os>
                    <family>windows</family>
                </os>
            </activation>
            <properties>
                <lib.extension>dll</lib.extension>
            </properties>
        </profile>
        <profile>
            <id>macos</id>
            <activation>
                <os>
                    <family>mac</family>
                </os>
            </activation>
            <properties>
                <lib.extension>dylib</lib.extension>
            </properties>
        </profile>
    </profiles>
```


Invoke the Maven package goal with the `native-plugin` enabled:
```shell
mvn package -Pnative-plugin
```

This will produce your plugin in the target folder: `my-plugin.(dll|so|dylib)`

### Step 6 - Install your own plugin
Copy your plugin to the ret plugins folder. The ret plugin folder is located at `~/.ret/plugins`. If the folder
  does not exist, you have to create manually.

Initialize the plugin with RET:
```shell
# !Note: You have to provide an absolute path, otherwise it won't work.
ret plugin initialize /path/to/.ret/plugins/myplugin.dylib
```



Your plugin should now be available in RET.
