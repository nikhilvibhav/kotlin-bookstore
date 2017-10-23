package com.capgemini.rmg.routes

import com.capgemini.camel.bean.exceptionhandler.HttpResponseExceptionHandler
import com.capgemini.camel.exception.core.IrrecoverableException
import com.capgemini.camel.exception.core.RecoverableException
import com.capgemini.camel.exception.data.ValidationException
import com.capgemini.camel.response.model.RestClientErrorResponse
import com.capgemini.rmg.processors.KotlinBookstoreServiceProcessor
import com.fasterxml.jackson.databind.DeserializationFeature
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jackson.JacksonDataFormat

/**
 * @author Nikhil Vibhav
 */
abstract class AbstractKotlinBookstoreRoute : RouteBuilder() {
    protected val errorDataFormat = JacksonDataFormat()
    protected val successDataFormat = JacksonDataFormat()

    /**
     * Initializes Jackson data formats used in the subclasses
     */
    protected fun initJsonDataFormat() {
        // Data format to represent error response
        errorDataFormat.disableFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        errorDataFormat.unmarshalType = RestClientErrorResponse::class.java

        //Data format to represent success response
        successDataFormat.disableFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    /**
     * Initializes excepion handlers which are used by the subclasses
     */
    protected fun initExceptionHandlers() {
        onException(ValidationException::class.java)
                .log(LoggingLevel.ERROR, "A Validation Error occurred - \${exception.stacktrace}")
                .handled(true)
                .bean(HttpResponseExceptionHandler::class.java, "handleValidationException")

        onException(RecoverableException::class.java)
                .log(LoggingLevel.ERROR, "Recoverable error occurred - \${exception.stacktrace}")
                .maximumRedeliveries("{{route.onException.maxRedeliveries}}")
                .useExponentialBackOff()
                .backOffMultiplier("{{route.onException.backOffMultiplier}}")
                .maximumRedeliveryDelay("{{route.onException.maximumRedeliveryDelayMillis}}")
                .redeliveryDelay("{{route.onException.redeliveryDelayMillis}}")
                .retryAttemptedLogLevel(LoggingLevel.INFO)
                .handled(true)
                .bean(HttpResponseExceptionHandler::class.java, "handleRecoverableException")

        onException(IrrecoverableException::class.java)
                .log(LoggingLevel.ERROR, "Irrecoverable error occurred - \${exception.stacktrace}")
                .handled(true)
                .bean(HttpResponseExceptionHandler::class.java, "handleIrrecoverableException")

        onException(Exception::class.java)
                .log(LoggingLevel.ERROR, "Irrecoverable error occurred - \${exception.stacktrace}")
                .bean(KotlinBookstoreServiceProcessor::class.java, "customizeExceptionMessage")
                .handled(true)
                .bean(HttpResponseExceptionHandler::class.java, "handleIrrecoverableException")
    }
}