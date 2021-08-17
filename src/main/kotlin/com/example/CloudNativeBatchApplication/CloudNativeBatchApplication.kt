package com.example.CloudNativeBatchApplication

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.retry.annotation.EnableRetry

@EnableBatchProcessing
@EnableRetry
@EnableDiscoveryClient
@SpringBootApplication
class CloudNativeBatchApplication

fun main(args: Array<String>) {
	runApplication<CloudNativeBatchApplication>(*args)
}
