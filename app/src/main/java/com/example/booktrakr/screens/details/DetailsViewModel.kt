package com.example.booktrakr.screens.details

import androidx.lifecycle.ViewModel
import com.example.booktrakr.data.Resource
import com.example.booktrakr.model.Item
import com.example.booktrakr.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: BookRepository)
    : ViewModel(){
    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId)
    }
}