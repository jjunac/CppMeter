package com.github.jjunac.cppmeter.analysers

import com.github.jjunac.cppmeter.annotations.RegisterAnalyser
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import com.github.jjunac.cppmeter.listeners.FunctionComplexityListener
import mu.KotlinLogging
import org.antlr.v4.runtime.tree.ParseTreeWalker

@RegisterAnalyser
class ComplexityAnalyser : Analyser {

    data class ComplexityData(val id: String, val parentId: String, val name: String, var complexity: Int, val depth: Int)

    val logger = KotlinLogging.logger {}
    val functions = mutableListOf<FunctionComplexityListener.Function>()
    val complexityDatas = mutableMapOf<String, ComplexityData>()
    var totalComplexity = 0


    override fun preAnalyse(e: PreAnalyseEvent) {
        functions.clear()
        complexityDatas.clear()
    }

    override fun analyse(e: AnalyseEvent) {
        FunctionComplexityListener().let {
            ParseTreeWalker().walk(it, e.parseTree)
            functions += it.functions
        }
    }

    override fun postAnalyse(e: PostAnalyseEvent) {
        for (function in functions) {
            totalComplexity += function.complexity
            val qualifiers = function.fullyQualifiedName.split("::")
            var parentId = ""
            qualifiers.forEachIndexed { i, it ->
                val currentId = (if (parentId.isEmpty()) "" else "$parentId::") + it
                complexityDatas.putIfAbsent(currentId, ComplexityData(currentId, parentId, it, 0, i))
                complexityDatas[currentId]!!.complexity += function.complexity
                parentId = currentId
                return@forEachIndexed
            }
        }
        logger.debug{complexityDatas}
    }

}

