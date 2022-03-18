package io.coinmetrics.akka.http

import akka.actor.ActorSystem
import akka.http.javadsl.Http
import akka.http.javadsl.model.HttpRequest
import akka.stream.Materializer
import akka.stream.RestartSettings
import akka.stream.javadsl.RestartSource
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit

data class Response(val code: Int, val data: String)

class HttpRequestStream(val system: ActorSystem) {
    val minDelay = FiniteDuration.apply(100, TimeUnit.MILLISECONDS)
    val maxDelay = FiniteDuration.apply(1, TimeUnit.SECONDS)
    val totalDelay = FiniteDuration.apply(5, TimeUnit.SECONDS)
    val http = Http.get(system)
    fun executeRequest(request: HttpRequest, maxRestarts: Int = 1): CompletionStage<Response> {
        val logSettings = RestartSettings.`LogSettings$`.`MODULE$`.defaultSettings()
        val restartSettings = RestartSettings(minDelay, maxDelay, 0.1, maxRestarts, totalDelay, logSettings)
        val mat = Materializer.matFromSystem(system)
        val src = Source.single(request)
            .mapAsync(1) { req -> http.singleRequest(req) }
            .mapAsync(1) { resp ->
                resp.entity().toStrict(1000, mat).thenApply { strict ->
                    Response(resp.status().intValue(), strict.data.utf8String())
                }
            }
        val restart = RestartSource.withBackoff(restartSettings) { src }
        return restart.runWith(Sink.head(), mat)
    }
}
