package com.example.booktrakr.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.booktrakr.screens.Home
import com.example.booktrakr.screens.ReaderStatsScreen
import com.example.booktrakr.screens.Search.BooksSearchViewModel
import com.example.booktrakr.screens.Search.SearchScreen
import com.example.booktrakr.screens.login.LoginScreen
import com.example.booktrakr.screens.SplashScreen

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
            Home(navController = navController)
        }
        composable(ReaderScreens.ReaderStatsScreen.name) {
            ReaderStatsScreen(navController = navController)
        }
        composable(ReaderScreens.SearchScreen.name) {
            val viewmodel= hiltViewModel<BooksSearchViewModel>()
            SearchScreen(navController = navController,viewModel=viewmodel)
        }
}
}