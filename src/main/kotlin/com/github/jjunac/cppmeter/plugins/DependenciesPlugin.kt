package com.github.jjunac.cppmeter.plugins

import com.github.jjunac.cppmeter.Plugin
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.DisplayEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import io.ktor.freemarker.FreeMarkerContent
import java.io.File

class DependenciesPlugin: Plugin("dependencies") {

    private val patternInclude  = """#include ["<]([^">]*)[">]""".toRegex(RegexOption.MULTILINE)
    private val dependencies    = mutableMapOf<String, MutableList<String>>()
    private val internalFiles   = mutableSetOf<String>()
    private val externalFiles   = mutableSetOf<String>()

    override fun preAnalyse(e: PreAnalyseEvent) {
        println(e)
        dependencies.clear()
        internalFiles.clear()
        externalFiles.clear()
    }

    override fun analyse(e: AnalyseEvent) {
        println(e)
        dependencies[e.filePath] = mutableListOf()
        patternInclude
            .findAll(File(e.projectPath + "/" + e.filePath).readText())
            .forEach { dependencies[e.filePath]!!.add(it.groupValues[1]) }
    }

    override fun postAnalyse(e: PostAnalyseEvent) {
        println(e)
        internalFiles.addAll(dependencies.keys)
        externalFiles.addAll(dependencies.values.flatten().filter { !internalFiles.contains(it) })
    }

    override fun display(e: DisplayEvent): FreeMarkerContent {
        println(dependencies)
        return FreeMarkerContent(
            "plugins/dependencies.ftl",
            mapOf("internalFiles" to internalFiles, "externalFiles" to externalFiles, "deps" to dependencies)
        )
    }
}