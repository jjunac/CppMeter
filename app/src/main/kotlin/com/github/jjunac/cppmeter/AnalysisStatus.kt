package com.github.jjunac.cppmeter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


sealed class AnalysisStatusMsg
class SetAnalysisStatus(val analysisStatus: String) : AnalysisStatusMsg()
class GetAnalysisStatus(val analysisStatus: CompletableDeferred<String>) : AnalysisStatusMsg()

@ObsoleteCoroutinesApi
fun createAnalysisStatusActor() = GlobalScope.actor<AnalysisStatusMsg> {
    var analysisStatus = "Analysis ongoing..."
    for (msg in channel) {
        when (msg) {
            is SetAnalysisStatus -> {
                logger.debug(msg.analysisStatus)
                analysisStatus = msg.analysisStatus
            }
            is GetAnalysisStatus -> msg.analysisStatus.complete(analysisStatus)
        }
    }
}

suspend fun SendChannel<AnalysisStatusMsg>.getAnalysisStatus(): String {
    val status = CompletableDeferred<String>()
    send(GetAnalysisStatus(status))
    return status.await()
}

suspend fun SendChannel<AnalysisStatusMsg>.setAnalysisStatus(status: String) {
    send(SetAnalysisStatus(status))
}
