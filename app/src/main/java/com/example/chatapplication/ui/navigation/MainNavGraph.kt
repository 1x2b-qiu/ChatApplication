package com.example.chatapplication.ui.navigation

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.chatapplication.ui.screen.apply.Apply
import com.example.chatapplication.ui.screen.chat.Chat
import com.example.chatapplication.ui.screen.contactProfile.ContactProfile
import com.example.chatapplication.ui.screen.main.Main

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    composable("main") {
        Main(
            onNavigateToChat = { id, name, targetId ->
                navController.navigate("chat/$id/$name/$targetId")
            },
            onNavigateToApply = {
                navController.navigate("apply")
            },
            onNavigateToProfile = {
                navController.navigate("profile")
            },
            onNavigateToContactProfile = { contactId ->
                navController.navigate("contactProfile/${contactId}")
            }
        )
    }
    composable("chat/{conversationId}/{targetName}/{targetId}") {
        Chat(
            onBackClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        )
    }
    composable("apply") {
        Apply(
            onBackClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        )
    }
    composable("contactProfile/{contactId}") {
        ContactProfile(
            onBackClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            onConversationClick = { id, name, targetId ->
                navController.navigate("chat/$id/$name/$targetId")
            }
        )
    }
}