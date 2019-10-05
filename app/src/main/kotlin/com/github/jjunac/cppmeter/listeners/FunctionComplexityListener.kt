package com.github.jjunac.cppmeter.listeners

import com.github.jjunac.cppmeter.grammars.CPP14Parser
import mu.KotlinLogging
import java.util.*

class FunctionComplexityListener : FunctionSignatureListener() {

    data class Function(val fullyQualifiedName: String, val complexity: Int)

    val functions = mutableListOf<Function>()

    private val logger = KotlinLogging.logger {}

    override fun enterFunctiondefinition(ctx: CPP14Parser.FunctiondefinitionContext?) {
        super.enterFunctiondefinition(ctx)
        logger.debug { "Parsing file: ${getFullyQualifiedFunctionName()}" }
    }

    override fun exitFunctiondefinition(ctx: CPP14Parser.FunctiondefinitionContext?) {
        super.exitFunctiondefinition(ctx)
        functions.add(Function(getFullyQualifiedFunctionName(), Random().nextInt(41)+1))
    }

}

