package io.coinmetrics.akka.http

import akka.actor.ActorSystem
import akka.actor.ExtendedActorSystem
import akka.http.javadsl.model.HttpRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PooledHttpRequestTest: Assertions() {

    val system = ActorSystem.create() as ExtendedActorSystem
    val request = HttpRequest.GET("/get?foo=bar")

    @Test
    fun `regular pooled request`() {
        val pool = HttpRequestPool("postman-echo.com", 4, system)
        val result = pool.queueRequest(request)
        val response = result.toCompletableFuture().join()
        //println(response.decodeString(Charsets.UTF_8))
        pool.shutdown().toCompletableFuture().join()
    }

    @Test
    fun `secured pooled request`() {
        val pool = HttpRequestPool("postman-echo.com", 4, system, secure = true)
        val result = pool.queueRequest(request)
        val response = result.toCompletableFuture().join()
        //println(response.decodeString(Charsets.UTF_8))
        pool.shutdown().toCompletableFuture().join()
    }
}
