package org.example

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebClientTest {

    @JvmRecord
    data class TestClass(val f1: String, val f2: String)

    private val client = WebClient.create()

    private val server = MockWebServer()

    @AfterAll
    fun destroy() {
        server.close()
    }

    @Test
    fun test200() {
        server.enqueue(MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody("""{"f1":"test1","f2":"test2"}"""))

        val req = client.get()
            .uri("http://localhost:${server.port}")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({it.is4xxClientError}, { Mono.empty()})
            .bodyToMono(TestClass::class.java)


        val resp = assertNotNull(req.block())
        assertEquals("test1", resp.f1)
        assertEquals("test2", resp.f2)
    }

    @Test
    fun test400() {
        server.enqueue(MockResponse()
            .setResponseCode(400)
            .setHeader("Content-Type", "application/json")
            .setBody("""{"f1":"test1","f2":"test2"}"""))

        val req = client.get()
            .uri("http://localhost:${server.port}")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({it.is4xxClientError}, { Mono.empty()})
            .bodyToMono(TestClass::class.java)


        val resp = assertNotNull(req.block())
        assertEquals("test1", resp.f1)
        assertEquals("test2", resp.f2)
    }
}