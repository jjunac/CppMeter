package com.github.jjunac.cppmeter.listeners

import com.github.jjunac.cppmeter.grammars.CPP14Parser
import mu.KotlinLogging
import java.util.*

class FunctionComplexityListener : FunctionSignatureListener() {

    data class Function(val fullyQualifiedName: String, val complexity: Int)

    val functions = mutableListOf<Function>()
    var complexity = 1

    private val logger = KotlinLogging.logger {}

    override fun enterFunctiondefinition(ctx: CPP14Parser.FunctiondefinitionContext?) {
        super.enterFunctiondefinition(ctx)
        complexity = 1
    }

    override fun exitFunctiondefinition(ctx: CPP14Parser.FunctiondefinitionContext?) {
        super.exitFunctiondefinition(ctx)
        functions.add(Function(getFullyQualifiedFunctionName(), complexity))
    }

    override fun enterSelectionstatement(ctx: CPP14Parser.SelectionstatementContext?) {
        super.enterSelectionstatement(ctx)
        if (ctx?.getChild(0)?.text == "if") {
            complexity++
        }
    }

    override fun enterLabeledstatement(ctx: CPP14Parser.LabeledstatementContext?) {
        super.enterLabeledstatement(ctx)
        if (ctx?.getChild(0)?.text == "case" || ctx?.getChild(1)?.text == "default") {
            complexity++
        }
    }




}

