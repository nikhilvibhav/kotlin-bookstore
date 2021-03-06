package com.capgemini.rmg.configs

import com.capgemini.archaius.spring.ArchaiusBridgePropertyPlaceholderConfigurer
import org.apache.camel.CamelContext
import org.apache.camel.component.properties.PropertiesComponent
import org.apache.camel.spring.boot.CamelContextConfiguration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

/**
 * Configuration class for Kotlin Bookstore Service
 *
 * @author Nikhil Vibhav
 */
@Configuration
@ComponentScan(basePackages = arrayOf("com.capgemini.rmg"))
open class KotlinBookstoreConfig {

    @Autowired
    lateinit var camelContext: CamelContext

    private val LOGGER = LoggerFactory.getLogger(KotlinBookstoreConfig::class.java)

    @Bean
    open fun contextConfiguration(): CamelContextConfiguration {
        return object : CamelContextConfiguration {
            override fun beforeApplicationStart(context: CamelContext) {
                val configLocations = arrayOf("file:config/camel.properties",
                        "file:config/env.properties")
                val propertiesComponent = PropertiesComponent()
                propertiesComponent.setLocations(configLocations)
                context.addComponent("properties", propertiesComponent)
                LOGGER.info("Added properties component to camel context.")

                if (context.isAllowUseOriginalMessage) {
                    context.isAllowUseOriginalMessage = false
                    LOGGER.info("Turned OFF AllowUseOriginalMessage.")
                }

                if (!context.isTracing) {
                    context.isTracing = false
                    LOGGER.info("Turned OFF tracing")
                }
            }

            override fun afterApplicationStart(camelContext: CamelContext?) {
            }
        }
    }

    @Bean
    @Primary
    open fun bridgePropertyPlaceholder(): ArchaiusBridgePropertyPlaceholderConfigurer {
        val configurer = ArchaiusBridgePropertyPlaceholderConfigurer()

        configurer.setIgnoreResourceNotFound(true)
        configurer.setInitialDelayMillis(5000)
        configurer.setDelayMillis(5000)
        configurer.setIgnoreDeletesFromSource(true)
        configurer.setAllowMultiplePlaceholders(true)
        configurer.setLocations(*arrayOf<Resource>(
                FileSystemResource("config/camel.properties"),
                FileSystemResource("config/env.properties"),
                FileSystemResource("config/hystrix.properties"),
                FileSystemResource("config/metrics.properties"),
                FileSystemResource("config/application.properties")))

        LOGGER.info("Camel Archaius Bridge Property Placeholder is configured successfully.")

        return configurer
    }
}
