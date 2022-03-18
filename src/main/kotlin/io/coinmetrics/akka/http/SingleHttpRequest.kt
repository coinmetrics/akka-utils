package io.coinmetrics.akka.http

import akka.actor.ActorSystem
import akka.http.javadsl.Http
import akka.http.javadsl.model.HttpEntity
import akka.http.javadsl.model.HttpRequest
import akka.stream.Materializer
import java.util.concurrent.CompletionStage


abstract class SingleHttpRequest(val maxResponseTime: Long = 60 * 1000, val maxResponseSize: Long = 256 * 1024) {
    fun executeRequest(system: ActorSystem, mat: Materializer, request: HttpRequest): CompletionStage<HttpEntity.Strict> {
        val responseFuture = Http.get(system).singleRequest(request)
        return responseFuture.thenCompose { it.entity().toStrict(maxResponseTime, maxResponseSize, mat) }
    }
}
