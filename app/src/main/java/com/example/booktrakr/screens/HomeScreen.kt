package com.example.booktrakr.screens

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController


@Composable
fun Home(navController: NavHostController) {
    Surface(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
        Text(text = "Home")
    }

}