package com.example.CloudNativeBatchApplication.processor

import com.example.CloudNativeBatchApplication.domain.Foo
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.CircuitBreaker
import org.springframework.retry.annotation.Recover
import org.springframework.web.client.RestTemplate

open class EnrichmentProcessor: ItemProcessor<Foo, Foo> {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Recover
    fun fallback(foo: Foo): Foo {
        return foo.apply {
            message = "error"
        }
    }

    @CircuitBreaker(maxAttempts = 4)
    override fun process(foo: Foo): Foo {
        val responseEntity: ResponseEntity<String> = this.restTemplate.exchange(
            "http://cloud-native-batch-application/enrich",
            HttpMethod.GET,
            null,
            String::class.java
        )

        return foo.apply {
            message = responseEntity.body.toString()
        }
    }
}