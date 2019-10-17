package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import com.github.jjunac.cppmeter.grammars.CPP14Lexer
import com.github.jjunac.cppmeter.grammars.CPP14Parser
import com.github.jjunac.cppmeter.daos.Projects
import io.ktor.features.NotFoundException
import io.ktor.request.ApplicationRequest
import io.ktor.request.document
import io.ktor.request.path
import io.ktor.util.KtorExperimentalAPI
import mu.KotlinLogging
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Paths
import java.sql.Connection.TRANSACTION_READ_UNCOMMITTED

object Core {

    val version = "1.0.0"
    val codename = "Aerosmith"
    private val projectPath = "C:/Users/jerem/Xshared/MQLite/src"

    private val logger = KotlinLogging.logger {}

    init {
        Registry.register()

        Database.connect("jdbc:sqlite:data/data.sqlite3", "org.sqlite.JDBC")
        transaction(TRANSACTION_READ_UNCOMMITTED, 1) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create (Projects)
        }
    }

    fun analyseProject() {
        runAnalysers()
        buildViews()
    }

    @KtorExperimentalAPI
    fun handle(request: ApplicationRequest): Any {
        val e = DisplayEvent(request.queryParameters["p"])
        if (request.path() == "/")
            return PageDisplayer("overview.ftl").display(e)
        logger.debug { Registry.views }
        val view = Registry.views[request.document()] ?: throw NotFoundException()
        return view.displayer!!.display(e)
    }

    fun displayOverview() = PageDisplayer("overview.ftl").display(DisplayEvent())

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
