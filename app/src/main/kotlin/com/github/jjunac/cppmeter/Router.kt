package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.daos.Project
import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.DisplayEvent
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

@KtorExperimentalAPI
fun Routing.api() {
    trace { application.log.debug(it.buildText()) }
    apiProjects()
    get("/{...}") {
        if (call.request.queryParameters["p"] == null)
            call.respondRedirect("/projects")
        call.respond(Core.handle(call.request))
    }
}

private fun Routing.apiProjects() {
    route("/projects") {
        get {
            call.respond(transaction {
                PageDisplayer("projects/index.ftl").display(DisplayEvent())
            })
        }
        get("new") {
            call.respond(PageDisplayer("projects/new.ftl").display(DisplayEvent()))
        }
        post {
            val params = call.receiveParameters()
            val project = transaction {
                Project.new {
                    name = params["name"]!!
                    path = params["path"]!!
                }
            }
            call.respondRedirect("/projects")
        }
    }
}