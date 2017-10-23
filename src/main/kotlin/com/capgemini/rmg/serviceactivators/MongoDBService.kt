package com.capgemini.rmg.serviceactivators

import com.capgemini.rmg.models.Book
import com.capgemini.rmg.repositories.BookRepository
import org.apache.camel.Exchange
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikhil Vibhav
 */
@Component("mongoDBService")
class MongoDBService {

    @Autowired
    lateinit var repository: BookRepository

    fun saveBook(exchange: Exchange): Book {
        val book = exchange.`in`.getBody(Book::class.java)

        val newBook = repository.save(book)

        return newBook
    }

    fun sellBook(exchange: Exchange): Book {
        val isbn: String = exchange.`in`.getHeader("isbn", String::class.java)
        val quantityToSell: Int = exchange.`in`.getHeader("quantity", Int::class.java)

        val book = repository.findByIsbn(isbn)

        book.quantity = book.quantity - quantityToSell

        return repository.save(book)
    }

    fun findBook(exchange: Exchange): Book {
        val isbn: String = exchange.`in`.getHeader("isbn", String::class.java)

        val book = repository.findByIsbn(isbn)

        return book
    }

    fun findAllBooks(): List<Book> {
        return repository.findAll()
    }

    fun deleteBook(exchange: Exchange): Book {
        val isbn: String = exchange.`in`.getHeader("isbn", String::class.java)

        val book = repository.findByIsbn(isbn)

        repository.delete(book)

        return book
    }
}