package com.example.booktrakr.repository

import com.example.booktrakr.data.DataOrException
import com.example.booktrakr.model.Mbook
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(private val queryBook:Query){
    suspend fun getAllBooksFromDatabase():DataOrException<List<Mbook>,Boolean,Exception>{
        val dataOrException=DataOrException<List<Mbook>,Boolean,java.lang.Exception>()
        try {
            dataOrException.loading=true
            dataOrException.data = queryBook.get().await().documents.map {documentSnapshot ->
                documentSnapshot.toObject(Mbook::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty())dataOrException.loading=false
        }catch (exception: FirebaseFirestoreException){
            dataOrException.e=exception
        }
        return dataOrException
    }
}