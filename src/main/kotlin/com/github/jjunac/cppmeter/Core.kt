package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.plugins.DependenciesPlugin
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import io.ktor.freemarker.FreeMarkerContent
import java.io.File
import java.nio.file.Paths

class Core(private val projectPath: String) {

    private val plugins: MutableMap<String, Plugin> = mutableMapOf<String, Plugin>()

    init {
        registerPlugin(DependenciesPlugin())
    }

    private fun registerPlugin(plugin: Plugin) {
        plugins[plugin.name] = plugin
    }

    fun analyse() {
        plugins.values.forEach { it.preAnalyse(PreAnalyseEvent(projectPath))}

        File(projectPath).walk().filter { it.isFile }.forEach {
            val filePath = Paths.get(projectPath).relativize(it.toPath()).toString().replace(File.separator, "/")
            plugins.values.forEach { it.analyse(AnalyseEvent(projectPath, filePath))}
        }

        plugins.values.forEach { it.postAnalyse(PostAnalyseEvent(projectPath))}
    }

    fun display(pluginName: String): FreeMarkerContent {
        return plugins[pluginName]!!.display(DisplayEvent())
    }

}