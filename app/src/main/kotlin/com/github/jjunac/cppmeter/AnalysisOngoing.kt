package com.github.jjunac.cppmeter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


sealed class AnalysisOngoingMsg
object StartAnalysis : AnalysisOngoingMsg()
object StopAnalysis : AnalysisOngoingMsg()
class IsAnalysisOngoing(val isAnalysisOngoing: CompletableDeferred<Boolean>) : AnalysisOngoingMsg()

@ObsoleteCoroutinesApi
fun createAnalysisOngoingActor() = GlobalScope.actor<AnalysisOngoingMsg> {
    var isAnalysisOngoing = false
    for (msg in channel) {
        when (msg) {
            is StartAnalysis -> isAnalysisOngoing = true
            is StopAnalysis -> isAnalysisOngoing = false
            is IsAnalysisOngoing -> msg.isAnalysisOngoing.complete(isAnalysisOngoing)
        }
    }
}

suspend fun SendChannel<AnalysisOngoingMsg>.startAnalysis() {
    send(StartAnalysis)
}

suspend fun SendChannel<AnalysisOngoingMsg>.stopAnalysis() {
    send(StopAnalysis)
}

suspend fun SendChannel<AnalysisOngoingMsg>.isAnalysisOngoing(): Boolean {
    val isAnalysisOngoing = CompletableDeferred<Boolean>()
    send(IsAnalysisOngoing(isAnalysisOngoing))
    return isAnalysisOngoing.await()
}
