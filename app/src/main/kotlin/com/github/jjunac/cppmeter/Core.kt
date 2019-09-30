package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.analysers.DependenciesAnalyser
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import com.github.jjunac.cppmeter.grammars.CPP14Lexer
import com.github.jjunac.cppmeter.grammars.CPP14Parser
import com.github.jjunac.cppmeter.analysers.ComplexityAnalyser
import io.ktor.freemarker.FreeMarkerContent
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.nio.file.Paths

class Core(private val projectPath: String) {

    val plugins: MutableMap<String, Plugin> = mutableMapOf()

    init {
        registerPlugin(DependenciesAnalyser(this))
        registerPlugin(ComplexityAnalyser(this))
    }

    private fun registerPlugin(plugin: Plugin) {
        plugins[plugin.name] = plugin
    }

    fun analyse() {
        plugins.values.forEach { it.preAnalyse(PreAnalyseEvent(projectPath))}

        File(projectPath).walk().filter { it.isFile }.forEach {
            val filePath = Paths.get(projectPath).relativize(it.toPath()).toString().replace(File.separator, "/")
            val lexer = CPP14Lexer(ANTLRFileStream(it.toPath().toString()))
            val parser = CPP14Parser(CommonTokenStream(lexer))
            with(AnalyseEvent(projectPath, filePath, parser.translationunit())) {
                plugins.values.forEach { it.analyse(this)}
            }
        }

        plugins.values.forEach { it.postAnalyse(PostAnalyseEvent(projectPath))}
    }

    fun displayOverview(): FreeMarkerContent {
        return PageDisplayer("overview.ftl", mapOf()).toFreeMarkerContent(plugins.values)
    }

    fun displayPlugin(pluginName: String): FreeMarkerContent {
        return plugins[pluginName]!!.displayPage(DisplayEvent()).toFreeMarkerContent(plugins.values)
    }

}