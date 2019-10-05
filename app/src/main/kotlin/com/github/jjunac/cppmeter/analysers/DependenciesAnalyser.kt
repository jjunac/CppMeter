package com.github.jjunac.cppmeter.analysers

import com.github.jjunac.cppmeter.annotations.RegisterAnalyser
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import java.io.File

@RegisterAnalyser
class DependenciesAnalyser : Analyser {

    private val patternInclude  = """#include ["<]([^">]*)[">]""".toRegex(RegexOption.MULTILINE)
    val dependencies    = mutableMapOf<String, MutableList<String>>()
    val internalFiles   = mutableSetOf<String>()
    val externalFiles   = mutableSetOf<String>()

    override fun preAnalyse(e: PreAnalyseEvent) {
        dependencies.clear()
        internalFiles.clear()
        externalFiles.clear()
    }

    override fun analyse(e: AnalyseEvent) {
        dependencies[e.filePath] = mutableListOf()
        patternInclude
            .findAll(File(e.projectPath + "/" + e.filePath).readText())
            .forEach { dependencies[e.filePath]!!.add(it.groupValues[1]) }
    }

    override fun postAnalyse(e: PostAnalyseEvent) {
        internalFiles.addAll(dependencies.keys)
        externalFiles.addAll(dependencies.values.flatten().filter { !internalFiles.contains(it) })
    }

}