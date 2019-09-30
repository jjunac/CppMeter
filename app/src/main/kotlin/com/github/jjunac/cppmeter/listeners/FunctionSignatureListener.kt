package com.github.jjunac.cppmeter.listeners

import com.github.jjunac.cppmeter.grammars.CPP14BaseListener
import com.github.jjunac.cppmeter.grammars.CPP14Parser
import java.util.*

open class FunctionSignatureListener : CPP14BaseListener() {

    protected var namespaceStack = ArrayDeque<String>()
    protected var functionName = ""

    fun getNamespace() = if(namespaceStack.isEmpty()) "" else namespaceStack.joinToString("::", "", "::")

    fun getFullyQualifiedFunctionName() = getNamespace() + functionName

    override fun enterOriginalnamespacedefinition(ctx: CPP14Parser.OriginalnamespacedefinitionContext?) {
        super.enterOriginalnamespacedefinition(ctx)
        ctx?.getChild(1)?.let {
            namespaceStack.add(it.text)
        }
    }

    override fun exitOriginalnamespacedefinition(ctx: CPP14Parser.OriginalnamespacedefinitionContext?) {
        super.exitOriginalnamespacedefinition(ctx)
        namespaceStack.pop()
    }

    override fun enterFunctiondefinition(ctx: CPP14Parser.FunctiondefinitionContext?) {
        super.enterFunctiondefinition(ctx)
        functionName = ""
        // Fix constructor: Add classname
        if (ctx?.declarator()?.ptrdeclarator()?.noptrdeclarator()?.noptrdeclarator()?.declaratorid()?.idexpression()?.qualifiedid()?.nestednamespecifier()?.text == "::") {
            functionName += ctx.declspecifierseq()?.declspecifier()?.text!!
        }
        // Add the actual function name
        // TODO: log if null
        functionName += ctx?.declarator()?.ptrdeclarator()?.noptrdeclarator()?.noptrdeclarator()?.text
            ?: ctx?.declarator()?.ptrdeclarator()?.ptrdeclarator()?.noptrdeclarator()?.noptrdeclarator()?.text
    }

}