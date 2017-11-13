package com.capgemini.rmg

import org.apache.camel.component.servlet.CamelHttpTransportServlet
import org.apache.camel.swagger.servlet.RestSwaggerCorsFilter
import org.apache.camel.swagger.servlet.RestSwaggerServlet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean

/**
 * Main class
 *
 * @author Nikhil Vibhav
 */
@SpringBootApplication
open class Application {

    @Value("\${rest.api.base.url:/capi/rmg/kt/v1}")
    private lateinit var restApiBaseUrl: String

    @Value("\${swagger.url:/apidocs}")
    private lateinit var swaggerUrl: String

    @Value("\${rest.api.version}")
    private lateinit var restApiVersion: String

    @Value("\${rest.api.title}")
    private lateinit var restApiTitle: String

    @Value("\${rest.api.description}")
    private lateinit var restApiDesc: String

    private val LOGGER = LoggerFactory.getLogger(Application::class.java)

    @Bean
    open fun camelHttpServletRegistration(): ServletRegistrationBean {
        val camelHttpServlet = CamelHttpTransportServlet()
        val servletRegistration = ServletRegistrationBean(camelHttpServlet, restApiBaseUrl + "/*")
        servletRegistration.setName("CamelServlet")

        LOGGER.info("Camel HTTP Transport Servlet Registered!")

        return servletRegistration;
    }

    @Bean
    open fun camelSwaggerServletRegistration(): ServletRegistrationBean {
        val camelSwaggerServlet = RestSwaggerServlet()
        val servletRegistration = ServletRegistrationBean(camelSwaggerServlet, swaggerUrl + "/*")
        servletRegistration.addInitParameter("base.path", removeLeadingSlash(restApiBaseUrl))
        servletRegistration.addInitParameter("api.path", swaggerUrl)
        servletRegistration.addInitParameter("api.version", restApiVersion)
        servletRegistration.addInitParameter("api.title", restApiTitle)
        servletRegistration.addInitParameter("api.description", restApiDesc)

        LOGGER.info("Camel Swagger Servlet Registered")

        return servletRegistration
    }

    @Bean
    open fun camelSwaggerCorsFilterRegistration(): FilterRegistrationBean {
        val swaggerCorsFilter = RestSwaggerCorsFilter()
        val filterRegistration = FilterRegistrationBean()
        filterRegistration.filter = swaggerCorsFilter
        filterRegistration.addServletRegistrationBeans(camelSwaggerServletRegistration(), camelHttpServletRegistration())

        LOGGER.info("Camel Swagger CORS Filter Registered")

        return filterRegistration
    }

    fun removeLeadingSlash(url: String): String {
        if (url.startsWith("/")) {
            return url.substring(1)
        }
        return url
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}