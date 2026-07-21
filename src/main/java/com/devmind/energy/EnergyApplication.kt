package com.devmind.energy

import java.time.Clock
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestClient

@SpringBootApplication
@EnableConfigurationProperties(EnergyProperties::class)
open class EnergyApplication {
    @Bean
    fun restClientBuilder(): RestClient.Builder =
        RestClient.builder()


    @Bean
    fun restClient(builder: RestClient.Builder, properties: EnergyProperties): RestClient =
        builder.baseUrl(properties.baseUrl).build()


    @Bean
    fun clock(): Clock? =
        Clock.systemUTC()
}

fun main(args: Array<String>) {
    SpringApplication.run(EnergyApplication::class.java, *args)
}
