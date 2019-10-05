package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.analysers.Analyser
import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.analysers.DependenciesAnalyser
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import com.github.jjunac.cppmeter.grammars.CPP14Lexer
import com.github.jjunac.cppmeter.grammars.CPP14Parser
import com.github.jjunac.cppmeter.analysers.ComplexityAnalyser
import com.github.jjunac.cppmeter.annotations.RegisterAnalyser
import com.github.jjunac.cppmeter.annotations.RegisterView
import com.github.jjunac.cppmeter.views.View
import io.ktor.freemarker.FreeMarkerContent
import mu.KotlinLogging
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import java.io.File
import java.nio.file.Paths

class Core(private val projectPath: String) {

    private val logger = KotlinLogging.logger {}

    init {
        Registry.register()
    }

    fun analyseProject() {
        runAnalysers()
        buildViews()
    }

    fun displayView(pluginName: String?): Any {
        if (pluginName.isNullOrEmpty())
            return PageDisplayer("overview.ftl").display(DisplayEvent())
        // TODO add 404
        return Registry.views[pluginName]!!.displayer!!.display(DisplayEvent())
    }

    private fun runAnalysers() {
        Registry.analysers.values.forEach { it.preAnalyse(PreAnalyseEvent(projectPath)) }

        File(projectPath).walk().filter { it.isFile }.forEach {
            val filePath = Paths.get(projectPath).relativize(it.toPath()).toString().replace(File.separator, "/")
            val lexer = CPP14Lexer(ANTLRFileStream(it.toPath().toString()))
            val parser = CPP14Parser(CommonTokenStream(lexer))
            with(AnalyseEvent(projectPath, filePath, parser.translationunit())) {
                Registry.analysers.values.forEach { it.analyse(this) }
            }
        }

        Registry.analysers.values.forEach { it.postAnalyse(PostAnalyseEvent(projectPath)) }
    }

    private fun buildViews() {
        Registry.views.values.forEach { it.buildDisplayer() }
    }


}
