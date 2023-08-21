package com.example.booktrakr.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
            HomeContent(navController = navController)
        }

    }
}

@Composable
fun HomeContent(navController: NavController) {
    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = email?.split("@")?.get(0)

    val listOfBooks = listOf(
        Mbook(id = "dadfa", title = "Hello Again", authors = "All of us", notes = null),
        Mbook(id = "dadfa", title = " Again", authors = "All of us", notes = null),
        Mbook(id = "dadfa", title = "Hello ", authors = "The world us", notes = null),
        Mbook(id = "dadfa", title = "Hello Again", authors = "All of us", notes = null),
        Mbook(id = "dadfa", title = "Hello Again", authors = "All of us", notes = null)
    )

    Column(
        Modifier
            .padding(2.dp).fillMaxHeight()
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Top) {
        Row(
            Modifier
                .align(alignment = Alignment.Start),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TitleSection(label = "Your reading \nActivity right now...")

            Column(horizontalAlignment = Alignment.End,
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
        ReadingRightNowArea(books = listOf(), navController = navController)

        TitleSection(label = "Reading List")

        BooklistArea(listofBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BooklistArea(listofBooks: List<Mbook>, navController: NavController) {
    HorizontalScrollableComponent(listOfBooks = listofBooks) {

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
        if (listOfBooks.isNullOrEmpty()) {
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
                ListCard(book) {
                    onCardPressed(it)
                }
            }
        }


    }


}


@Composable
fun ReadingRightNowArea(books: List<Mbook>, navController: NavController) {
    ListCard()

}

