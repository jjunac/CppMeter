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
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.nio.file.Paths


@ObsoleteCoroutinesApi
class ProjectAnalyser(private val projectPath: String) {

    private val logger = KotlinLogging.logger {}

    private val viewInstances = mutableMapOf<String, View>()

    private val statusActor = createAnalysisStatusActor()


    @KtorExperimentalAPI
    suspend fun handle(request: ApplicationRequest): Any {
        if (request.path() == "/analysis/status") {
            return statusActor.getAnalysisStatus()
        }

        val activeProject = request.queryParameters["p"]!!
        val e = DisplayEvent(activeProject, viewInstances.mapValues { it.value.displayableName })

        if (viewInstances.isEmpty()) {
            tryStartAnalysis()
        }

        if (statusActor.isAnalysisOngoing()) {
            logger.debug { request.path() }
            return PageDisplayer("analyse.ftl").display(e)
        }

        return when(request.path()) {
            "/" -> PageDisplayer("overview.ftl").display(e)
            else -> {
                val view = viewInstances[request.document()] ?: throw NotFoundException()
                view.displayer!!.display(e)
            }
        }

    }

    suspend fun tryStartAnalysis(): Boolean {
        if (statusActor.tryStartAnalysis()) {
            GlobalScope.launch { analyseProject() }
            return true
        }
        return false
    }

    private suspend fun analyseProject() {
        val analyserInstances = computeNeededAnalyser()
        runAnalysers(analyserInstances.values)
        buildViews(analyserInstances)
        statusActor.stopAnalysis()
    }

    private suspend fun computeNeededAnalyser(): MutableMap<Class<out Analyser>, Analyser> {
        statusActor.stepAnalysis("Computing needed analysers...")
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

    private suspend fun runAnalysers(analysers: Collection<Analyser>) {
        statusActor.stepAnalysis("Performing pre-analysis...")
        analysers.forEach { it.preAnalyse(PreAnalyseEvent(projectPath)) }

        val files = File(projectPath).walk().filter { it.isFile }
        val fileCount = files.count()
        files.forEachIndexed { i, f ->
            statusActor.stepAnalysis("Analysing file (${i+1}/$fileCount)...")
            val filePath = Paths.get(projectPath).relativize(f.toPath()).toString().replace(File.separator, "/")
            val parser = CPP14Parser(CommonTokenStream(CPP14Lexer(ANTLRFileStream(f.toPath().toString()))))
            AnalyseEvent(projectPath, filePath, parser.translationunit()).let {e ->
                analysers.forEach { it.analyse(e) }
            }
        }

        statusActor.stepAnalysis("Performing post-analysis...")
        analysers.forEach { it.postAnalyse(PostAnalyseEvent(projectPath)) }
    }

    private suspend fun buildViews(analyserInstances: Map<Class<out Analyser>, Analyser>) {
        statusActor.stepAnalysis("Building views...")
        Registry.viewMap.forEach { (key, value) ->
            // Checks have been done earlier in computeNeededAnalyser()
            val constructor = value.constructors[0]
            val params = constructor.parameterTypes.map { analyserInstances[it]!! }
            viewInstances[key] = (constructor.newInstance(*params.toTypedArray()) as View).apply {
                buildDisplayer()
            }
        }
    }

}
