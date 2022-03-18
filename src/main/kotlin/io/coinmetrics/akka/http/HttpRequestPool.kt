package io.coinmetrics.akka.http

import akka.Done
import akka.actor.ExtendedActorSystem
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.model.HttpResponse
import akka.stream.Materializer
import akka.stream.OverflowStrategy
import akka.stream.QueueOfferResult
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import akka.util.ByteString
import io.coinmetrics.akka.utils.toPair
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import akka.japi.Pair as AkkaPair

class HttpRequestPool(host: String, queueSize: Int, system: ExtendedActorSystem, val secure: Boolean = false, val maxResponseTime: Long = 60 * 1000, val maxResponseSize: Long = 256 * 1024) {

    private val poolClientFlow = if (secure) Http(system).cachedHostConnectionPoolHttps<CompletableFuture<ByteString>>(ConnectHttp.toHostHttps(host))
        else Http(system).cachedHostConnectionPool<CompletableFuture<ByteString>>(ConnectHttp.toHost(host))

    private val mat = Materializer.matFromSystem(system)

    private val resolve: (AkkaPair<Try<HttpResponse>, CompletableFuture<ByteString>>) -> Unit = { pair ->
        val (resp, future) = pair.toPair()
        when(resp) {
            is Success -> resp.value().entity().toStrict(maxResponseTime, maxResponseSize, mat).handle { entity, error ->
                if (error == null) future.complete(entity.data)
                else future.completeExceptionally(error)
            }
            is Failure -> future.completeExceptionally(resp.exception())
            else -> throw UnknownError()
        }
    }

    private val queue = Source.queue<AkkaPair<HttpRequest, CompletableFuture<ByteString>>>(queueSize, OverflowStrategy.dropNew())

    private val running = queue.via(poolClientFlow).to(Sink.foreach(resolve)).run(mat)

    fun queueRequest(request: HttpRequest): CompletionStage<ByteString> {
        val pair = AkkaPair(request, CompletableFuture<ByteString>())
        val result = running.offer(pair).thenCompose {
            when(it) {
                QueueOfferResult.enqueued() -> pair.second()
                QueueOfferResult.dropped() -> CompletableFuture.failedFuture(RuntimeException("Queue overflowed. Try again later."))
                is QueueOfferResult.Failure -> CompletableFuture.failedFuture(it.cause())
                else -> CompletableFuture.failedFuture(RuntimeException("Queue error $it while running the request."))
            }
        }
        return result.toCompletableFuture()
    }

    fun shutdown(): CompletionStage<Done> {
        running.complete()
        return running.watchCompletion()
    }


}