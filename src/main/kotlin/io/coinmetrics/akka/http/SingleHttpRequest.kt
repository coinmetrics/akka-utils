package io.coinmetrics.akka.http

import akka.actor.ActorSystem
import akka.http.javadsl.Http
import akka.http.javadsl.model.HttpEntity
import akka.http.javadsl.model.HttpRequest
import akka.stream.Materializer
import java.util.concurrent.CompletionStage


interface SingleHttpRequest {
    val MaxResponseTime: Long get() = 0x4000L
    val MaxResponseSize: Long get() = 0x40000L
    fun executeRequest(system: ActorSystem, mat: Materializer, request: HttpRequest): CompletionStage<HttpEntity.Strict> {
        val responseFuture = Http.get(system).singleRequest(request)
        return responseFuture.thenCompose { it.entity().toStrict(MaxResponseTime, MaxResponseSize, mat) }
    }
}
