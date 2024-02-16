package com.example.booktrakr.screens.details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.booktrakr.components.ReaderAppBar
import com.example.booktrakr.components.RoundedButton
import com.example.booktrakr.data.Resource
import com.example.booktrakr.model.Item
import com.example.booktrakr.model.Mbook
import com.example.booktrakr.model.VolumeInfo
import com.example.booktrakr.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String, viewModel: DetailsViewModel = hiltViewModel()
) {

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Details",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) {
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId)
                }.value
                if (bookInfo.data == null) {
                    Row() {
                        LinearProgressIndicator()
                    }

                } else {
                    BookDetail(bookInfo, navController)
                }
            }
        }
    }
}

@Composable
fun ShowBookDetails(
    bookInfo: Resource<Item>,
    navController: NavController
) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id


    Card(
        modifier = Modifier.padding(34.dp),
        shape = CircleShape, elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = bookData!!.imageLinks.thumbnail),
            contentDescription = "Book Image",
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp)
        )


    }
    Text(
        text = bookData?.title.toString(),
        style = MaterialTheme.typography.headlineLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 19
    )

    Text(text = "Authors: ${bookData?.authors.toString()}")
    Text(text = "Page Count: ${bookData?.pageCount.toString()}")
    Text(
        text = "Categories: ${bookData?.categories.toString()}",
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Published: ${bookData?.publishedDate.toString()}",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(5.dp))

    val cleanDescription = HtmlCompat.fromHtml(
        bookData!!.description,
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString()
    val localDims = LocalContext.current.resources.displayMetrics
    Surface(
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.09f))
            .padding(4.dp),
        shape = RectangleShape,
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {

        LazyColumn(modifier = Modifier.padding(3.dp)) {
            item {

                Text(text = cleanDescription)
            }

        }
    }

    //Buttons
    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RoundedButton(label = "Save") {
            //save this book to the firestore database
            val book = Mbook(
                title = bookData.title,
                authors = bookData.authors.toString(),
                description = bookData.description,
                categories = bookData.categories.toString(),
                notes = "",
                photoUrl = bookData.imageLinks.thumbnail,
                publishedDate = bookData.publishedDate,
                pageCount = bookData.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )

            saveToFirebase(book, navController = navController)

        }
        Spacer(modifier = Modifier.width(25.dp))
        RoundedButton(label = "Cancel") {
            navController.popBackStack()
        }

    }


}

fun saveToFirebase(book: Mbook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.popBackStack()
                        }


                    }.addOnFailureListener {
                        Log.w("Error", "SaveToFirebase:  Error updating doc", it)
                    }

            }


    } else {
    }

}

@Composable
fun BookDetail(
    bookInfo: Resource<Item>,
    navController: NavController
) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id
    if (bookData != null)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(34.dp),
                shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = bookData.imageLinks.thumbnail),
                    contentDescription = "Book Image",
                    modifier = Modifier
                        .width(100.dp)
                        .height(180.dp)
                        .padding(1.dp)
                    , contentScale = ContentScale.FillBounds
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                text = bookData.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = bookData.authors[0],
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontSize = 22.sp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(bookInfo.data.saleInfo.retailPrice!=null){
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "₹ ${bookInfo.data.saleInfo.retailPrice.amount}",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }else{
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Price Not Available",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
//                Log.d(
//                    "Currency",
//                    bookInfo.data.saleInfo.retailPrice.currencyCode + " " + bookInfo.data.saleInfo.retailPrice.amount.toString()
//                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "${bookData.pageCount} Pages",
                    style = TextStyle(fontSize = 18.sp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(modifier = Modifier.clickable {
                    saveBook(bookData, googleBookId, navController)
                }, shape = RoundedCornerShape(5.dp), border = BorderStroke(1.dp, Color.Black)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = "Save",
                        style = TextStyle(fontSize = 22.sp)
                    )
                }
                Surface(modifier = Modifier.clickable {
                    navController.popBackStack()
                }, shape = RoundedCornerShape(5.dp), border = BorderStroke(1.dp, Color.Black)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = "Cancel",
                        style = TextStyle(fontSize = 22.sp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    text = "Detail",
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
                detailRow(title = "Author", detail = bookData.authors[0])
                detailRow(title = "Publisher", detail = bookData.publisher)
                detailRow(title = "Published Date", detail = bookData.publishedDate)
                detailRow(title = "Category", detail = bookData.categories.joinToString(","))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 24.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = "Description",
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
                val cleanDescription = HtmlCompat.fromHtml(
                    bookData.description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString()
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 12.dp),
                    text = cleanDescription,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontSize = 18.sp, color = Color.Gray)
                )
            }
        }
}

private fun saveBook(
    bookData: VolumeInfo,
    googleBookId: String?,
    navController: NavController
) {
    val book = Mbook(
        title = bookData.title,
        authors = bookData.authors.toString(),
        description = bookData.description,
        categories = bookData.categories.toString(),
        notes = "",
        photoUrl = bookData.imageLinks.thumbnail,
        publishedDate = bookData.publishedDate,
        pageCount = bookData.pageCount.toString(),
        rating = 0.0,
        googleBookId = googleBookId,
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    )
    saveToFirebase(book, navController = navController)
}

@Composable
private fun detailRow(title: String, detail: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(top = 5.dp, bottom = 2.dp),
            text = title,
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .padding(top = 5.dp, bottom = 2.dp)
                .align(
                    Alignment.CenterEnd
                ),
            text = detail,
            textAlign = TextAlign.Start,
            style = TextStyle(fontSize = 18.sp, color = Color.Gray)
        )
    }
}
