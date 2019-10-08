package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.DisplayEvent
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.features.statusFile
import io.ktor.freemarker.FreeMarker
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.locations.*
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.main() {

    install(Locations)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this.javaClass.classLoader, "templates")
    }
    install(StatusPages) {
        exception<NotFoundException> { call.respond(PageDisplayer("404.ftl").display(DisplayEvent())) }
        exception<Throwable> { cause ->
            call.respond(PageDisplayer("500.ftl").display(DisplayEvent()))
            throw cause
        }
    }


    val core = Core("C:/Users/jerem/Xshared/MQLite/src")
    core.analyseProject()


    routing {
        get("{view?}") {
            call.respond(core.displayView(call.parameters["view"]))
        }
        static("static") {
            resources("static")
        }
    }
}

@KtorExperimentalLocationsAPI
fun main() {
    embeddedServer(Netty, 8080, module = Application::main).start()
}

