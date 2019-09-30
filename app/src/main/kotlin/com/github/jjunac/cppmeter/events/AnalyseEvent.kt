package com.github.jjunac.cppmeter.events

import org.antlr.v4.runtime.tree.ParseTree

data class AnalyseEvent(val projectPath: String, val filePath: String, val parseTree: ParseTree)