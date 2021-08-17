package com.example.CloudNativeBatchApplication.config

import com.example.CloudNativeBatchApplication.domain.Foo
import com.example.CloudNativeBatchApplication.listener.DownloadingJobExecutionListener
import com.example.CloudNativeBatchApplication.processor.EnrichmentProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.client.RestTemplate
import javax.sql.DataSource

@Configuration
class JobConfiguration(
    private val stepBuilderFactory: StepBuilderFactory,
    private val jobBuilderFactory: JobBuilderFactory
) {

    @Bean
    fun downloadStepExecutionListener() = DownloadingJobExecutionListener()

    @Bean
    @StepScope
    fun reader(
        @Value("#{jobExecutionContext['localFiles']}") paths: String?
    ): MultiResourceItemReader<Foo> {
        // 입력된 파일 paths를 문자열로 이은 값
        println(">> parts = $paths")


        val parsedPaths = paths!!.split(",")
        // 입력된 파일 갯수
        println(">> parsedPaths = ${parsedPaths.size}")

        var resources = arrayOf<Resource>()
        parsedPaths.forEach {
            val resource = FileSystemResource(it)
            // 리소스 하나의 경로
            println(">> resource = ${resource.uri}")
            resources = resources.plus(resource)
        }

        return MultiResourceItemReaderBuilder<Foo>()
            .name("multiReader")
            .delegate(delegate())
            .resources(*resources)
            .build()
    }

    @Bean
    @StepScope
    fun delegate(): FlatFileItemReader<Foo> {
        return FlatFileItemReaderBuilder<Foo>()
            .name("fooReader")
            .delimited()
            .names("first", "second", "third")
            .targetType(Foo::class.java)
            .build()
    }

    @Bean
    @StepScope
    fun processor() = EnrichmentProcessor()

    @Bean
    fun writer(dataSource: DataSource?): JdbcBatchItemWriter<Foo> {
        return JdbcBatchItemWriterBuilder<Foo>()
            .dataSource(dataSource!!)
            .beanMapped()
            .sql("INSERT INTO FOO VALUES (:first, :second, :third, :message)")
            .build()
    }

    @Bean
    fun load(): Step {
        return this.stepBuilderFactory.get("load")
            .chunk<Foo, Foo>(20)
            .reader(reader(null))
            .processor(processor())
            .writer(writer(null))
            .build()
    }

    @Bean
    fun job(jobExecutionListener: JobExecutionListener): Job {
        return this.jobBuilderFactory.get("s3jdbc")
            .listener(jobExecutionListener)
            .start(load())
            .build()
    }

    @Bean
    @LoadBalanced
    fun restTemplate() = RestTemplate()
}