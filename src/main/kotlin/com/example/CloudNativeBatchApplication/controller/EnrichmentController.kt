package com.example.CloudNativeBatchApplication.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException

@RestController
class EnrichmentController {
    private var count = 0

    @GetMapping("/enrich")
    fun enrich(): String {
        if (Math.random() > .5) {
            throw RuntimeException("I screwed up")
        }
        else {
            return "Enriched ${++this.count}"
        }
    }
}