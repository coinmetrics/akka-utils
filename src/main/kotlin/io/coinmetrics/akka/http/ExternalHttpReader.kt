package io.coinmetrics.akka.http

import akka.actor.ActorSystem
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.HttpRequest
import akka.stream.Materializer
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.concurrent.CompletionStage


class ExternalHttpReader(val request: HttpRequest, val mapper: ObjectMapper): SingleHttpRequest {
    inline fun <reified T : Any> getDataObject(system: ActorSystem, mat: Materializer): CompletionStage<T> = executeRequest(system, mat, request)
        .thenCompose { Jackson.unmarshaller(mapper, T::class.java).unmarshal(it, mat) }
        .exceptionally { err ->
            system.log().error("Unable to complete request $request: {}", err.message)
            T::class.java.getDeclaredConstructor().newInstance()
        }
}
