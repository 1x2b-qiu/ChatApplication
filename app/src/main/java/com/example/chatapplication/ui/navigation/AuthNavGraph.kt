package com.example.chatapplication.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.chatapplication.ui.screen.login.Login
import com.example.chatapplication.ui.screen.complete.Complete
import com.example.chatapplication.ui.screen.register.Register
import com.example.chatapplication.ui.screen.splash.Splash

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
) {
    composable("splash") {
        Splash(
            onSuccess = {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            },
            onFail = {
                navController.navigate("login")
            }
        )
    }

    composable("login") {
        Login(
            onRegisterClick = {
                navController.navigate("register")
            },
            onLoginSuccess = {
                navController.navigate("main") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
    composable("register") {
        Register(
            onBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            onRegisterSuccess = {
                navController.navigate("complete") {
                    // 注册成功同样清理掉 splash 和中间的所有验证页面
                    popUpTo("splash") { inclusive = true }
                }
            }
        )
    }
    composable("complete") {
        Complete(
            onCompleteSuccess = {
                navController.navigate("main") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
}
