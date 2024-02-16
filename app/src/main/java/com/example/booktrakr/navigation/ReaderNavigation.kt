package com.example.booktrakr.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booktrakr.screens.home.Home
import com.example.booktrakr.screens.ReaderStatsScreen
import com.example.booktrakr.screens.Search.BooksSearchViewModel
import com.example.booktrakr.screens.Search.SearchScreen
import com.example.booktrakr.screens.login.LoginScreen
import com.example.booktrakr.screens.SplashScreen
import com.example.booktrakr.screens.UpdateScreen
import com.example.booktrakr.screens.details.BookDetailsScreen
import com.example.booktrakr.screens.home.HomeScreenViewModel

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController,
    startDestination = ReaderScreens.SplashScreen.name ){
        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeViewModel= hiltViewModel<HomeScreenViewModel>()
            Home(navController = navController,viewModel=homeViewModel)
        }
        composable(ReaderScreens.ReaderStatsScreen.name) {
            val viewModel= hiltViewModel<HomeScreenViewModel>()
            ReaderStatsScreen(navController = navController,viewModel)
        }
        composable(ReaderScreens.SearchScreen.name) {
            val viewmodel= hiltViewModel<BooksSearchViewModel>()
            SearchScreen(navController = navController,viewModel=viewmodel)
        }

        composable(ReaderScreens.DetailScreen.name+"/{bookId}", arguments = listOf(navArgument("bookId"){
            type= NavType.StringType
        })) {
            it.arguments?.getString("bookId").let {
                BookDetailsScreen(navController = navController, bookId = it.toString())
            }
        }
        val updateName = ReaderScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}",
            arguments = listOf(navArgument("bookItemId") {
                type = NavType.StringType
            })) { navBackStackEntry ->

            navBackStackEntry.arguments?.getString("bookItemId").let {
                UpdateScreen(navController = navController, bookItemId = it.toString())
            }

        }
}
}