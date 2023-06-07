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

#### Install RET

```shell
brew tap rabobank/tap # Add Rabobank brew tap
brew install ret      # Install RET
```

#### Setup autocomplete

```shell
ret configure autocomplete zsh # This will output a command you have to run
```

#### Install Plugins
We have a [plugin repository](https://github.com/rabobank/ret-plugins) where your can download and install the plugins we developed.
Further on, you can read how to [create you own plugins](#create-your-own-plugin) for RET.

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
    name = "my-plugin", // (1)
    description = ["My custom plugin"],
    subcommands = [
        PluginInitializeCommand::class, // (2)
        MySubCommand::class,
        MyOtherSubCommand::class
    ]
)
@RegisterForReflection(targets = [RetContext::class]) // (3)
class MyPluginEntryCommand {
}
```

- (1): Enter the name of your plugin. This is the command that will be exposed to RET. For example:
  `ret my-plugin my-sub-command`
- (2): Make sure to add the `PluginInitializeCommand::class`. This exposes a command which is necessary for RET to
  initialize the plugin.
- (3): Register the `RetContext::class` for reflection. Otherwise, it will not be available in your binary.

### Step 4 - Implement your commands

From here on, it comes basically down to implementing your commands, using the [picocli framework](https://picocli.info/), with some help from utilities provided by RET.
In the example above, we have two sub commands. The implementation of a command could look like this:

```kotlin
@Command(name = "my-sub-command", description = ["Open Google homepage"]) // (1)
class MySubCommand(private val browserUtils: BrowserUtils) : Runnable { // (2)

    override fun run() {
        browserUtils.openUrl("https://www.google.com/") // (3)
    }
}
```

- (1): Again, you can specify the name, in the same way as for the entry command
- (2): Your command has to implement `Runnable` (or `Callable`)
- (3): The `BrowserUtils` let you open a URL, independent of which OS you are running it on

Apart from `BrowserUtils`, you could also inject `RetContext`, which gives you information about e.g. the environment and the Git context.
There are more utility/helper classes available, for which the [docs can be found here](https://javadoc.io/doc/io.github.rabobank/ret-core/latest/index.html).

**Note**: The entry command is also a `Command`, so you could also implement `Runnable` there, if you want to be able to execute it.
In that case, you have to [customize the test setup below a bit](#test-setup-for-entry-command).

### Step 5 - Test your plugin

You can use the Quarkus interactive shell to test your plugin:

```shell
mvn quarkus:dev
```

If you want to write unit tests, you can use a `CommandLine` object to invoke your command, as if it were ran from the terminal. An example:

```kotlin
class MySubCommandTest {

    val browserUtils = mock<BrowserUtils>()
    private val commandLine = CommandLine(MySubCommand(browserUtils))

    @Test
    fun `should open google`() {
        commandLine.execute() // you can specify command line arguments here if used by your command, like execute("how", "to", "walk")
        verify(browserUtils).openUrl("https://www.google.com")
    }
}
```

#### Test Setup for Entry Command
On top of what is written above, when you are testing the implementation of your entry command, you have to provide a custom picocli `IFacotry`, as the `PluginInitializeCommand` cannot be created automatically.
To provide a simple mock, you can do the following:
```kotlin
// ...
private val commandLine = CommandLine(MyPluginEntryCommand(), CustomInitializationFactory())
// ...

class CustomInitializationFactory : IFactory {
    private val pluginInitializeCommand: PluginInitializeCommand = mock()
    override fun <K : Any?> create(cls: Class<K>?): K {
        return if (cls?.isInstance(pluginInitializeCommand) == true) {
            cls.cast(pluginInitializeCommand)
        } else {
            CommandLine.defaultFactory().create(cls)
        }
    }
}
```

### Step 6 - Native build

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

### Step 7 - Install your own plugin

Copy your plugin to the ret plugins folder, located at `~/.ret/plugins`. If the folder
does not exist, you have to create it manually.

Initialize the plugin with RET:

```shell
# !Note: You have to provide the absolute path, otherwise it won't work.
ret plugin initialize /path/to/.ret/plugins/my-plugin.(dll|so|dylib)
```

Your plugin should now be available in RET, so try it out by calling `ret my-plugin my-sub-command`!

If you have [enabled autocompletion](#setup-autocomplete), ret should now automatically pick up your new commands in there,
and your descriptions will be shown as help texts. It is possible that you need to restart your terminal for this to work..
