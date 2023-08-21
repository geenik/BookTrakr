package com.example.booktrakr.network

import com.example.booktrakr.model.Book
import com.example.booktrakr.model.Item
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface Booksapi{

    @GET("volumes")
    suspend fun getAllBooks(@Query("q")query: String ): Book

    @GET("volumes/{bookId}")
    suspend fun getBookInfo(@Path("bookId")bookId:String): Item
}