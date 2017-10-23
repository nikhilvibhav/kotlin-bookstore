package com.capgemini.rmg.routes

import org.apache.camel.LoggingLevel
import org.springframework.stereotype.Component

/**
 * @author Nikhil Vibhav
 */
@Component("crudBookstoreRoute")
class CrudBookstoreRoute : AbstractKotlinBookstoreRoute() {
    override fun configure() {

        // Get all books
        from("direct:getAllBooks")
                .routeId(CrudBookstoreRoute::class.java.simpleName + ".FindAllBooks")
                .log(LoggingLevel.INFO, "Received request to get all books...")
                .bean("mongoDBService", "findAllBooks")

        // Get book by ISBN
        from("direct:getBook")
                .routeId(CrudBookstoreRoute::class.java.simpleName + ".FindBook")
                .log(LoggingLevel.INFO, "Received request to get details of book with ISBN - \${header.isbn}")
                .bean("mongoDBService", "findBook")

        // Sell book by ISBN
        from("direct:sellBook")
                .routeId(CrudBookstoreRoute::class.java.simpleName + ".SellBook")
                .log(LoggingLevel.INFO, "Received request to sell \${header.quantity} quantities of book with ISBN - \${header.isbn}")
                .bean("mongoDBService", "sellBook")
                .bean("kotlinBookstoreProcessor", "sendSellBookResponse")

        // Add a new book
        from("direct:addBook")
                .routeId(CrudBookstoreRoute::class.java.simpleName + ".AddBook")
                .log(LoggingLevel.INFO, "Received request to add book - \${body}")
                .bean("mongoDBService", "saveBook")
                .bean("kotlinBookstoreProcessor", "sendAddBookResponse")

        // Delete a book by ISBN
        from("direct:deleteBook")
                .routeId(CrudBookstoreRoute::class.java.simpleName + ".DeleteBook")
                .log(LoggingLevel.INFO, "Received request to delete book with ISBN - \${header.isbn}")
                .bean("mongoDBService", "deleteBook")
                .bean("kotlinBookstoreProcessor", "sendDeleteBookResponse")
    }
}