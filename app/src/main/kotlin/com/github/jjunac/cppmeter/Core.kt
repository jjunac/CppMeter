package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.daos.Project
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
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Paths
import java.sql.Connection
import java.sql.Connection.TRANSACTION_READ_UNCOMMITTED
import kotlin.coroutines.coroutineContext

object Core {

    val version = "1.0.0"
    val codename = "Aerosmith"
//    private val projectPath = "C:/Users/jerem/Xshared/MQLite/src"

    private val projectAnalyserMap = mutableMapOf<String, ProjectAnalyser>()

    private val logger = KotlinLogging.logger {}

    fun init() {
        Database.connect("jdbc:sqlite:data/data.sqlite3", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create (Projects)
        }

        Registry.discover()
    }

    @ObsoleteCoroutinesApi
    @KtorExperimentalAPI
    fun handle(request: ApplicationRequest): Any {
        return runBlocking {
            val activeProject = request.queryParameters["p"]!!
            // TODO: improve the 2 Map.get
            projectAnalyserMap.computeIfAbsent(activeProject) {
                ProjectAnalyser(transaction { Project.find { Projects.name eq it }.limit(1).first() }.path)
            }
            return@runBlocking projectAnalyserMap[activeProject]!!.handle(request)
        }
    }



}
