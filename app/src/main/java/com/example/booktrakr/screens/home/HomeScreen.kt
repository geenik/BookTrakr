package com.example.booktrakr.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booktrakr.components.BookView
import com.example.booktrakr.components.FABContent
import com.example.booktrakr.components.ListCard
import com.example.booktrakr.components.ReaderAppBar
import com.example.booktrakr.components.TitleSection
import com.example.booktrakr.model.Mbook
import com.example.booktrakr.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    viewModel: HomeScreenViewModel= hiltViewModel(),
) {
    Scaffold(topBar = {
        ReaderAppBar(title = "BookTrakr", navController = navController)
    },
        floatingActionButton = {
            FABContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }

        }) {
        //content
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            //home content
            HomeContent(navController = navController,viewModel)
        }

    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {
    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = email?.split("@")?.get(0)

    var listOfBooks= emptyList<Mbook>()
    val currentUser=FirebaseAuth.getInstance().currentUser

    if(!viewModel.data.value.data.isNullOrEmpty()){
        listOfBooks= viewModel.data.value.data?.toList()?.filter {
            it.userId==currentUser?.uid.toString()
        }!!
    }
    Log.d("Books", "HomeContent: ${listOfBooks.toString()}")


    Column(
        Modifier
            .padding(2.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Top) {
        Row(
            Modifier
                .align(alignment = Alignment.Start),
        ) {
            TitleSection(label = "Your reading \nActivity right now...")
            Spacer(modifier = Modifier.width(150.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                 ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = currentUserName!!,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip

                )
                Divider()
            }
        }
        ReadingRightNowArea(books = listOfBooks, navController = navController)

        TitleSection(label = "Reading List")

        BooklistArea(listofBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BooklistArea(listofBooks: List<Mbook>, navController: NavController) {
    val addedBooks = listofBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(addedBooks){
        navController.navigate(ReaderScreens.UpdateScreen.name +"/$it")

    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<Mbook>,
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if (listOfBooks.isEmpty()) {
            Surface(modifier = Modifier.padding(23.dp)) {
                Text(
                    text = "No books found. Add a Book",
                    style = TextStyle(
                        color = Color.Red.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
        } else {
            for (book in listOfBooks) {
                BookView(book) {
                    onCardPressed(it)
                }
            }
        }
    }


}


@Composable
fun ReadingRightNowArea(books: List<Mbook>, navController: NavController) {
    val readingNowList = books.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(readingNowList){
        Log.d("TAG", "BoolListArea: $it")
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

