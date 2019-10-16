package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.daos.Project
import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.DisplayEvent
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.ContentType
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

private val logger = KotlinLogging.logger {}

@KtorExperimentalAPI
fun Routing.api() {
    trace { application.log.debug(it.buildText()) }
    apiProjects()
    get("/{view}") {
        call.respond(Core.displayView(call.parameters["view"]))
    }
    get("/") {
        call.respond(Core.displayOverview())
    }
}

private fun Routing.apiProjects() {
    route("/projects") {
        get {
            call.respond(sqliteTransaction {
                PageDisplayer("projects/index.ftl", mapOf("projects" to Project.all().map { it.name })).display(DisplayEvent())
            })
        }
        get("new") {
            call.respond(PageDisplayer("projects/new.ftl").display(DisplayEvent()))
        }
        post {
            val params = call.receiveParameters()
            logger.debug { params }
            val project = sqliteTransaction {
                Project.new {
                    name = params["name"]!!
                    path = params["path"]!!
                }
            }
            call.respondRedirect("/projects")
        }
    }
}