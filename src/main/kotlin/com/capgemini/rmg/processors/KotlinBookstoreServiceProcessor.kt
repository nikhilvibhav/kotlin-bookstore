package com.capgemini.rmg.processors

import com.capgemini.rmg.exceptions.IrrecoverableKotlinBookstoreException
import com.capgemini.rmg.models.Book
import com.capgemini.rmg.models.GenericResponse
import org.apache.camel.Exchange
import org.springframework.stereotype.Component
import java.lang.Exception

/**
 * @author Nikhil Vibhav
 */
@Component("kotlinBookstoreProcessor")
class KotlinBookstoreServiceProcessor {

    fun sendSellBookResponse(exchange: Exchange): GenericResponse {
        val response = GenericResponse()
        val book = exchange.`in`.getBody(Book::class.java)
        val quantity = exchange.`in`.getHeader("quantity", String::class.java)

        response.id = book._id
        response.isbn = book.isbn
        response.message = "$quantity number of books with ISBN ${book.isbn} sold"

        return response
    }

    fun sendAddBookResponse(exchange: Exchange): GenericResponse {
        val response = GenericResponse()
        val book = exchange.`in`.getBody(Book::class.java)

        response.id = book._id
        response.isbn = book.isbn
        response.message = "Book with ISBN ${book.isbn} added to DB"

        return response
    }

    fun sendDeleteBookResponse(exchange: Exchange): GenericResponse {
        val response = GenericResponse()
        val book = exchange.`in`.getBody(Book::class.java)

        response.id = book._id
        response.isbn = book.isbn
        response.message = "Book with ISBN ${book.isbn} deleted from DB"

        return response
    }

    fun customizeExceptionMessage(exchange: Exchange) {
        val exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception::class) as Throwable

        if (exception.cause != null) {
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT,
                    IrrecoverableKotlinBookstoreException("Irrecoverable Error occurred. " +
                            "Contact the System Administrator for more details"))
        }
    }
}