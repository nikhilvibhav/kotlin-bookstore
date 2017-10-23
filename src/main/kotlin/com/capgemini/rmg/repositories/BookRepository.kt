package com.capgemini.rmg.repositories

import com.capgemini.rmg.models.Book
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author Nikhil Vibhav
 */
interface BookRepository : MongoRepository<Book, String> {

    fun findByIsbn(isbn: String): Book
}