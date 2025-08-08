package com.exemplio.geapfitmobile.view.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemplio.geapfitmobile.view.auth.login.LoginScreen
import com.exemplio.geapfitmobile.view.auth.register.RegisterScreen
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.TabSingleChat
import com.exemplio.geapfitmobile.view.home.HomeScreen
import com.exemplio.geapfitmobile.view.screens.singleChat.SingleChatScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                navigateToRegister = { navController.navigate(Register) },
                navigateToHome = { navController.navigate(Home){
                    popUpTo(0)
                }})
        }

        composable<Register> {
            RegisterScreen(navigateBack = { navController.popBackStack() })
        }

        composable<Home> {
            HomeScreen(navController)
        }

        composable<TabSingleChat> {
            SingleChatScreen()
        }
    }
}