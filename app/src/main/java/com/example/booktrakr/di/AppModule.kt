package com.example.booktrakr.di

import com.example.booktrakr.network.Booksapi
import com.example.booktrakr.repository.FireRepository
import com.example.booktrakr.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideFirebookRepository() =
        FireRepository(queryBook = FirebaseFirestore.getInstance().collection("books"))


    @Singleton
    @Provides
    fun provideBookApi(): Booksapi {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Booksapi::class.java)
    }
}