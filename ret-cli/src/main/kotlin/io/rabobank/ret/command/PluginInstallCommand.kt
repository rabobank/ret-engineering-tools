package io.rabobank.ret.command

import io.rabobank.ret.RetConsole
import io.rabobank.ret.plugins.PluginManager
import io.rabobank.ret.plugins.RetPlugin
import picocli.CommandLine
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.util.zip.ZipInputStream


@CommandLine.Command(
    name = "install",
    description = ["Install plugin"],
)
class PluginInstallCommand(private val pluginManager: PluginManager, private val retConsole: RetConsole) : Runnable {
    @CommandLine.Parameters(
        arity = "1",
        paramLabel = "<plugin>",
        description = ["Plugin to install"],
    )
    lateinit var plugin: String

    override fun run() {
        val pluginInfo = pluginManager.plugins.firstOrNull { it.name == plugin }
            ?: throw IllegalStateException("Plugin does not exist.")

        retConsole.out("Installing ret plugin ${pluginInfo.name}@${pluginInfo.version}")
        retConsole.out("Downloading plugin from ${pluginInfo.osx.amd64}")
        val plugin = downloadPlugin(URL(pluginInfo.osx.amd64))

        retConsole.out("Configuring plugin")
        System.load(plugin.absolutePath)
        val isolateThread = RetPlugin.createIsolate()
        RetPlugin.initialize(isolateThread, plugin.name)
        retConsole.out("Done!")
    }

    private fun downloadPlugin(url: URL): File {
        ZipInputStream(url.openStream()).use { zip ->

            // Get the plugin file entry
            var zipEntry = zip.nextEntry
            while (zipEntry?.isDirectory == true) {
                zip.closeEntry();
                zipEntry = zip.nextEntry
            }

            val pluginFileName = zipEntry?.name?.substringAfterLast("/")
                ?: throw IllegalStateException()

            val newFile = File("/Users/stevenschenk/.ret/plugins/${pluginFileName}")


            FileOutputStream(newFile).use {
                var len: Int
                val buffer = ByteArray(1024)
                while (zip.read(buffer).also { len = it } > 0) {
                    it.write(buffer, 0, len)
                }
            }

            return newFile
        }
    }
}
