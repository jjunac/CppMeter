package com.github.jjunac.cppmeter.views

import com.github.jjunac.cppmeter.Registry
import com.github.jjunac.cppmeter.analysers.DependenciesAnalyser
import com.github.jjunac.cppmeter.annotations.RegisterView

@RegisterView("dependencies")
class DependenciesView(private val depsAnalyser: DependenciesAnalyser) : PageView("dependencies") {

    override fun buildDataModel(): Map<String, Any> {
        return mapOf(
            "internalFiles" to depsAnalyser.internalFiles,
            "externalFiles" to depsAnalyser.externalFiles,
            "deps" to depsAnalyser.dependencies
        )
    }

}