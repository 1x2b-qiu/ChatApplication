package com.example.chatapplication.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.chatapplication.ui.screen.profile.gender.Gender
import com.example.chatapplication.ui.screen.profile.main.Profile
import com.example.chatapplication.ui.screen.profile.name.Name

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController
) {
    composable("settings") {

    }

    composable("profile") {
        Profile(
            onBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            onName = {
                navController.navigate("name")
            },
            onGender = {
                navController.navigate("gender")
            }
        )
    }

    composable("name") {
        Name(
            onBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        )
    }

    composable("gender") {
        Gender(
            onBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        )
    }

}
