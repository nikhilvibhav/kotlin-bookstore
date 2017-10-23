package com.capgemini.rmg.routes

import com.capgemini.rmg.models.Book
import com.capgemini.rmg.models.GenericResponse
import org.apache.camel.model.rest.RestBindingMode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.ws.rs.core.MediaType

/**
 * @author Nikhil Vibhav
 */
@Component("kotlinBookstoreServiceRoute")
class KotlinBookstoreServiceRoute : AbstractKotlinBookstoreRoute() {

    private val LOGGER = LoggerFactory.getLogger(KotlinBookstoreServiceRoute::class.java)

    @Value("\${rest.api.base.url}")
    private lateinit var restApiBaseUrl: String

    @Value("\${rest.api.version}")
    private lateinit var restApiVersion: String

    @Value("\${rest.api.title}")
    private lateinit var restApiTitle: String

    @Value("\${rest.api.description}")
    private lateinit var restApiDesc: String

    @Value("\${rest.api.host}")
    private lateinit var restApiHost: String

    @Value("\${swagger.url:/apidocs}")
    private lateinit var swaggerUrl: String

    @Value("\${info.app.name}")
    private lateinit var serviceInfoName: String

    @Value("\${info.app.description}")
    private lateinit var serviceInfoDescription: String

    @Value("\${info.resource.path:/info}")
    private lateinit var serviceInfoResourcePath: String

    @Value("\${book.resource.path:/book}")
    private lateinit var bookResourcePath: String

    @Value("\${add.book.resource.path:/add}")
    private lateinit var addBookResourcePath: String

    @Value("\${add.book.resource.path:/delete}")
    private lateinit var deleteBookResourcePath: String

    @Value("\${add.book.resource.path:/sell}")
    private lateinit var sellBookResourcePath: String

    override fun configure() {
        val serviceInfoMap = HashMap<String, String>()
        serviceInfoMap.put("name", serviceInfoName)
        serviceInfoMap.put("description", serviceInfoDescription)

        initExceptionHandlers()

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .host(restApiHost)
                .contextPath(restApiBaseUrl)
                .apiContextPath(swaggerUrl)
                .apiProperty("api.description", restApiDesc)
                .apiProperty("api.title", restApiTitle)
                .apiProperty("api.version", restApiVersion)
                .apiProperty("cors", "true")
                .skipBindingOnErrorCode = true

        // Service Information endpoint
        rest(serviceInfoResourcePath)
                .description("Kotlin Bookstore Service's Info Endpoint")
                .produces(MediaType.APPLICATION_JSON)
                .get()
                .route().transform().constant(serviceInfoMap)

        // Get All Books
        rest(bookResourcePath)
                .description("Get all the books in the database")
                .produces(MediaType.APPLICATION_JSON)
                .get()
                .outType(List::class.java)
                .to("direct:getAllBooks")

        // Get Book by ISBN
        rest(bookResourcePath + "/{isbn}")
                .description("Get a particular book from the database")
                .produces(MediaType.APPLICATION_JSON)
                .get()
                .outType(Book::class.java)
                .to("direct:getBook")

        // Add a new Book
        rest(bookResourcePath + addBookResourcePath)
                .description("Add a book in the database")
                .consumes(MediaType.APPLICATION_JSON).produces(MediaType.APPLICATION_JSON)
                .post()
                .type(Book::class.java)
                .outType(GenericResponse::class.java)
                .to("direct:addBook")

        // Sell a Book by ISBN
        rest(bookResourcePath + sellBookResourcePath + "/{isbn}")
                .description("Sell a specified quantity of the book from the database")
                .produces(MediaType.APPLICATION_JSON)
                .put("?quantity={quantity}")
                .outType(GenericResponse::class.java)
                .to("direct:sellBook")

        // Delete a Book by ISBN
        rest(bookResourcePath + deleteBookResourcePath + "/{isbn}")
                .description("Remove a book from the database")
                .produces(MediaType.APPLICATION_JSON)
                .delete()
                .outType(GenericResponse::class.java)
                .to("direct:deleteBook")
    }
}

