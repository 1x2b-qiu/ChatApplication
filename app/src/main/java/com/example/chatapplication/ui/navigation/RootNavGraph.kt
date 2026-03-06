package com.example.chatapplication.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun RootNavGraph(
    navController: NavHostController = rememberNavController(), //导航控制器
    pendingChat: Triple<String, String, String>? = null,
    onPendingChat: () -> Unit = {}
) {

    LaunchedEffect(pendingChat) {
        val chat = pendingChat ?: return@LaunchedEffect
        val encodeName = Uri.encode(chat.second)
        navController.navigate("chat/${chat.first}/${encodeName}/${chat.third}") {
            launchSingleTop = true
        }
        onPendingChat()
    }


    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.background(Color.Black)
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
        settingsNavGraph(navController)
    }
}