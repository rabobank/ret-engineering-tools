package io.rabobank.ret.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.enterprise.context.ApplicationScoped

private val repoConfig = """
    {
      "name": "ret official",
      "url": "https://raw.githubusercontent.com/stevenschenk/ret-repository/main/plugins.json"
    }
    """

@ApplicationScoped
class PluginManager(objectMapper: ObjectMapper) {
    private val repository = objectMapper.readValue<Repository>(repoConfig)
    val plugins: List<Plugin> = listOf(
        Plugin(
            "git-plugin",
            "0.0.5",
            PluginFile(
                "https://github.com/rabobank/ret-plugins/releases/download/0.0.5/git-plugin-0.0.5-osx-x86_64.zip",
                "https://github.com/rabobank/ret-plugins/releases/download/0.0.5/git-plugin-0.0.5-osx-x86_64.zip"
            ),
            PluginFile("https://github.com/rabobank/ret-plugins/releases/download/0.0.5/git-plugin-0.0.5-linux-x86_64.zip", null)
        )
    )
}
