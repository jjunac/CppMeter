package com.github.jjunac.cppmeter.views

import com.github.jjunac.cppmeter.Registry
import com.github.jjunac.cppmeter.analysers.ComplexityAnalyser
import com.github.jjunac.cppmeter.annotations.RegisterView

@RegisterView("complexity")
class FunctionComplexityView : PageView("complexity") {

    val complexityAnalyser: ComplexityAnalyser = Registry.analysers.getInstance(ComplexityAnalyser::class.java)

    override fun buildDataModel(): Map<String, Any> {
        val ids = mutableListOf("project-root")
        val labels = mutableListOf("Project")
        val parents = mutableListOf("")
        val values = mutableListOf(complexityAnalyser.totalComplexity)
        val colors = mutableListOf("#fff")

        val cumulativeComplexities = mutableMapOf("" to 0)


        fun computeColor(complexityData: ComplexityAnalyser.ComplexityData): String {
            val parentCumulativeComplexity = cumulativeComplexities[complexityData.parentId]!!
            cumulativeComplexities[complexityData.id] = parentCumulativeComplexity
            cumulativeComplexities[complexityData.parentId] = parentCumulativeComplexity + complexityData.complexity
            val hue = cumulativeComplexityToHue(parentCumulativeComplexity)
            return "hsl($hue, ${100-complexityData.depth*6}%, 63%)"
        }

        complexityAnalyser.complexityDatas
            .values
            .sortedWith(compareBy({ it.parentId }, { -it.complexity }, { it.name }))
            .forEach {
                println(it)
                ids.add(it.id)
                labels.add(it.name)
                parents.add(if (it.parentId.isEmpty()) "project-root" else it.parentId)
                values.add(it.complexity)
                colors.add(computeColor(it))
            }

        return mapOf("ids" to ids, "labels" to labels, "parents" to parents, "values" to values, "colors" to colors)

    }

    private fun cumulativeComplexityToHue(cumulativeComplexity: Int) =
        ((cumulativeComplexity.toDouble() / complexityAnalyser.totalComplexity) * 360).toInt()

}