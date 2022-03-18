package io.coinmetrics.akka.http

import akka.actor.ActorSystem
import akka.http.javadsl.model.HttpRequest
import akka.stream.Materializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Optional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
class ExternalHttpReaderTest : Assertions() {

    val mapper = ObjectMapper().registerKotlinModule()
    val system = ActorSystem.create()

    @Test
    fun `single request`() {
        val request = HttpRequest.GET("https://reqres.in/api/users")
        val reader = ExternalHttpReader(request, mapper)
        val obj =
            reader.getDataObject<JsonNode>(system, Materializer.matFromSystem(system)).toCompletableFuture().join()
        println(obj)
    }

    @Test
    fun `request stream`() {
        val request = HttpRequest.GET("https://reqres.in/api/users")
        val result = HttpRequestStream(system).executeRequest(request)
        val maybe = result.handle() { s, t -> Optional.ofNullable(s) }.toCompletableFuture().join()
        assertTrue(maybe.isPresent)
        println(maybe.get())
    }

    @AfterAll
    fun done() {
        system.terminate()
    }
}
