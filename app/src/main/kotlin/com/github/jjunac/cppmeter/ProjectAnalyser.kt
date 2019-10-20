package com.github.jjunac.cppmeter


import com.github.jjunac.cppmeter.analysers.Analyser
import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import com.github.jjunac.cppmeter.views.View
import com.github.jjunac.cppmeter.grammars.CPP14Lexer
import com.github.jjunac.cppmeter.grammars.CPP14Parser
import io.ktor.features.NotFoundException
import io.ktor.request.ApplicationRequest
import io.ktor.request.document
import io.ktor.request.path
import io.ktor.util.KtorExperimentalAPI
import mu.KotlinLogging
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.nio.file.Paths


class ProjectAnalyser(private val projectPath: String) {

    private val logger = KotlinLogging.logger {}

    private val viewInstances = mutableMapOf<String, View>()
    private var isAnalysed = false


    @KtorExperimentalAPI
    fun handle(request: ApplicationRequest): Any {
        val activeProject = request.queryParameters["p"]!!
        val e = DisplayEvent(activeProject, viewInstances.mapValues { it.value.displayableName })
        if (!isAnalysed) {
            analyseProject()
            return PageDisplayer("analyse.ftl").display(e)
        }
        if (request.path() == "/")
            return PageDisplayer("overview.ftl").display(e)
        val view = viewInstances[request.document()] ?: throw NotFoundException()
        return view.displayer!!.display(e)
    }

    private fun analyseProject() {
        val analyserInstances = computeNeededAnalyser()
        runAnalysers(analyserInstances.values)
        buildViews(analyserInstances)
        isAnalysed = true
    }

    private fun computeNeededAnalyser(): MutableMap<Class<out Analyser>, Analyser> {
        val res = mutableMapOf<Class<out Analyser>, Analyser>()
        Registry.viewMap.values.forEach { viewClass ->
            require(viewClass.constructors.size == 1) {
                "View ${viewClass.simpleName} must have only 1 constructor"
            }
            viewClass.constructors[0].parameterTypes.forEach { analyserClass ->
                require (Analyser::class.java.isAssignableFrom(analyserClass)) {
                    "${viewClass.simpleName} constructor parameters must only be subclasses of Analyser"
                }
                @Suppress("UNCHECKED_CAST")
                res.computeIfAbsent(analyserClass as Class<out Analyser>) {
                    analyserClass.getConstructor().newInstance()
                }
            }
        }
        return res
    }

    private fun runAnalysers(analysers: Collection<Analyser>) {
        analysers.forEach { it.preAnalyse(PreAnalyseEvent(projectPath)) }

        File(projectPath).walk().filter { it.isFile }.forEach { f ->
            val filePath = Paths.get(projectPath).relativize(f.toPath()).toString().replace(File.separator, "/")
            val parser = CPP14Parser(CommonTokenStream(CPP14Lexer(ANTLRFileStream(f.toPath().toString()))))
            AnalyseEvent(projectPath, filePath, parser.translationunit()).let {e ->
                analysers.forEach { it.analyse(e) }
            }
        }

        analysers.forEach { it.postAnalyse(PostAnalyseEvent(projectPath)) }
    }

    private fun buildViews(analyserInstances: Map<Class<out Analyser>, Analyser>) {
        Registry.viewMap.forEach { (key, value) ->
            // Checks have been done earlier in computeNeededAnalyser()
            val constructor = value.constructors[0]
            val params = constructor.parameterTypes.map { analyserInstances[it]!! }
            logger.debug { constructor.parameterTypes.map { it.simpleName } }
            logger.debug { params }
            viewInstances[key] = (constructor.newInstance(*params.toTypedArray()) as View).apply {
                buildDisplayer()
            }
        }
    }

}