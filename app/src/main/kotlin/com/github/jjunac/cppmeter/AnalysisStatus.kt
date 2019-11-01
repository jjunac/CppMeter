package com.github.jjunac.cppmeter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

data class AnalysisStatus(var isOngoing: Boolean, var description: String, var progress: Double)


sealed class AnalysisStatusMsg
// Classical getter/setter
class SetAnalysisStatus(val analysisStatus: AnalysisStatus) : AnalysisStatusMsg()
class GetAnalysisStatus(val analysisStatus: CompletableDeferred<AnalysisStatus>) : AnalysisStatusMsg()
class IsAnalysisOngoing(val analysisStatus: CompletableDeferred<Boolean>) : AnalysisStatusMsg()
// Atomic operation to start the analysis if an analysis is not ongoing
class TryStartAnalysis(val hasStartedTheAnalysis: CompletableDeferred<Boolean>) : AnalysisStatusMsg()
class StepAnalysis(val analysisDescription: String) : AnalysisStatusMsg()
// Stop the analysis and reset the description and the progress
object StopAnalysis : AnalysisStatusMsg()

@ObsoleteCoroutinesApi
fun createAnalysisStatusActor() = GlobalScope.actor<AnalysisStatusMsg> {
    var analysisStatus = AnalysisStatus(false, "", 1.0)
    for (msg in channel) {
        when (msg) {
            is SetAnalysisStatus -> analysisStatus = msg.analysisStatus
            is GetAnalysisStatus -> msg.analysisStatus.complete(analysisStatus)
            is IsAnalysisOngoing -> msg.analysisStatus.complete(analysisStatus.isOngoing)
            is TryStartAnalysis -> {
                if (!analysisStatus.isOngoing) {
                    analysisStatus = AnalysisStatus(true, "Starting analysis...", 0.0)
                    msg.hasStartedTheAnalysis.complete(true)
                } else {
                    msg.hasStartedTheAnalysis.complete(false)
                }
            }
            is StepAnalysis -> analysisStatus.description = msg.analysisDescription
            is StopAnalysis -> analysisStatus = AnalysisStatus(false, "Finishing analysis...", 1.0)
        }
    }
}

suspend fun SendChannel<AnalysisStatusMsg>.setAnalysisStatus(analysisStatus: AnalysisStatus) {
    send(SetAnalysisStatus(analysisStatus))
}

suspend fun SendChannel<AnalysisStatusMsg>.getAnalysisStatus(): AnalysisStatus {
    val analysisStatus = CompletableDeferred<AnalysisStatus>()
    send(GetAnalysisStatus(analysisStatus))
    return analysisStatus.await()
}

suspend fun SendChannel<AnalysisStatusMsg>.isAnalysisOngoing(): Boolean {
    val isAnalysisOngoing = CompletableDeferred<Boolean>()
    send(IsAnalysisOngoing(isAnalysisOngoing))
    return isAnalysisOngoing.await()
}

suspend fun SendChannel<AnalysisStatusMsg>.tryStartAnalysis(): Boolean {
    val isAnalysisOngoing = CompletableDeferred<Boolean>()
    send(TryStartAnalysis(isAnalysisOngoing))
    return isAnalysisOngoing.await()
}

suspend fun SendChannel<AnalysisStatusMsg>.stepAnalysis(analysisDescription: String) {
    send(StepAnalysis(analysisDescription))
}

suspend fun SendChannel<AnalysisStatusMsg>.stopAnalysis() {
    send(StopAnalysis)
}
