package com.github.jjunac.cppmeter

import com.github.jjunac.cppmeter.displayers.PageDisplayer
import com.github.jjunac.cppmeter.events.DisplayEvent
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.features.statusFile
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
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
import java.text.DateFormat

@KtorExperimentalAPI
fun Application.main() {

    Core.init()

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this.javaClass.classLoader, "templates")
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
        }
    }

    install(StatusPages) {
        exception<NotFoundException> { call.respond(PageDisplayer("404.ftl").display(DisplayEvent())) }
        exception<Throwable> { cause ->
            call.respond(PageDisplayer("500.ftl").display(DisplayEvent()))
            throw cause
        }
    }

    install(Routing) {
        static("static") {
            resources("static")
        }
        api()
    }

}

@KtorExperimentalLocationsAPI
fun main() {
    embeddedServer(Netty, 8080, module = Application::main).start()
}

