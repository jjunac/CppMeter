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
//    private val plugins: MutableMap<String, BaseAnalyser> = mutableMapOf()
//    private val reflections = Reflections("com.github.jjunac.cppmeter", SubTypesScanner(), TypeAnnotationsScanner())


    init {
        Registry.register()
//        reflections.getTypesAnnotatedWith(RegisterAnalyser::class.java).forEach {
//            if (!Analyser::class.java.isAssignableFrom(it))
//                error("${it.simpleName} doesn't extend Analyser")
//            @Suppress("UNCHECKED_CAST") val clazz = it as Class<Analyser>
//            Registry.analysers.register(clazz, clazz.getConstructor().newInstance())
//        }
//        logger.info { "${Registry.analysers.size} Analysers registered" }
//
//        reflections.getTypesAnnotatedWith(RegisterView::class.java).forEach {
//            if (!View::class.java.isAssignableFrom(it))
//                error("${it.simpleName} doesn't extend View")
//            @Suppress("UNCHECKED_CAST") val clazz = it as Class<View>
//            Registry.views.register(clazz.getAnnotation(RegisterView::class.java).path, clazz.getConstructor().newInstance())
//        }
//        logger.info { "${Registry.analysers.size} Analysers registered" }

//        registerPlugin(DependenciesAnalyser(this))
//        registerPlugin(ComplexityAnalyser(this))
    }

//    private fun registerPlugin(baseAnalyser: BaseAnalyser) {
//        plugins[baseAnalyser.name] = baseAnalyser
//    }

    fun analyseProject() {
        runAnalysers()
        buildViews()
    }

    fun displayOverview(): Any =
        PageDisplayer("overview.ftl", mapOf()).display(DisplayEvent())

    fun displayView(pluginName: String): Any =
        Registry.views[pluginName]!!.displayer!!.display(DisplayEvent())

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
