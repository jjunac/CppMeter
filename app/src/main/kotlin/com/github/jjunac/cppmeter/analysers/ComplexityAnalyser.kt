package com.github.jjunac.cppmeter.analysers

import com.github.jjunac.cppmeter.annotations.RegisterAnalyser
import com.github.jjunac.cppmeter.events.AnalyseEvent
import com.github.jjunac.cppmeter.events.PostAnalyseEvent
import com.github.jjunac.cppmeter.events.PreAnalyseEvent
import com.github.jjunac.cppmeter.listeners.FunctionComplexityListener
import org.antlr.v4.runtime.tree.ParseTreeWalker

@RegisterAnalyser
class ComplexityAnalyser : Analyser {

    data class ComplexityData(val id: String, val parentId: String, val name: String, var complexity: Int, val depth: Int)

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
        println(complexityDatas)
    }

//    override fun displayPage(e: DisplayEvent): PageDisplayer {
//        return displayPageFromTemplate(constructDataModel())
//    }

//    private fun constructDataModel(): Map<String, Any> {
//        val ids = mutableListOf("project-root")
//        val labels = mutableListOf("Project")
//        val parents = mutableListOf("")
//        val values = mutableListOf(totalComplexity)
//        val colors = mutableListOf("#fff")
//
//        val cumulativeComplexities = mutableMapOf("" to 0)
//        fun computeColor(complexityData: ComplexityData): String {
//            val parentCumulativeComplexity = cumulativeComplexities[complexityData.parentId]!!
//            cumulativeComplexities[complexityData.id] = parentCumulativeComplexity
//            cumulativeComplexities[complexityData.parentId] = parentCumulativeComplexity + complexityData.complexity
//            val hue = ((parentCumulativeComplexity.toDouble()/totalComplexity)*360).toInt()
//            return "hsl($hue,${100-complexityData.depth*6}%,63%)"
//        }
//
//        complexityDatas
//            .values
//            .sortedWith(compareBy({ it.parentId }, { -it.complexity }, { it.name }))
//            .forEach {
//                println(it)
//                ids.add(it.id)
//                labels.add(it.name)
//                parents.add(if (it.parentId.isEmpty()) "project-root" else it.parentId)
//                values.add(it.complexity)
//                colors.add(computeColor(it))
//            }
//
//        return mapOf("ids" to ids, "labels" to labels, "parents" to parents, "values" to values, "colors" to colors)
//    }

}

