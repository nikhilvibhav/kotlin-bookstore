package com.capgemini.rmg.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import java.io.Serializable

/**
 * @author Nikhil Vibhav
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Book(@Id val _id: String? = null,
                val name: String = "",
                val author: String = "",
                val isbn: String = "",
                var rating: Float = 0F,
                var quantity: Int = 0
) : Serializable