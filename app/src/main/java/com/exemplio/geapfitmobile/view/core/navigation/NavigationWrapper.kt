package com.exemplio.geapfitmobile.view.core.navigation

import ContactsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exemplio.geapfitmobile.view.auth.login.LoginScreen
import com.exemplio.geapfitmobile.view.auth.register.RegisterScreen
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.TabSingleChat
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.TabContacts
import com.exemplio.geapfitmobile.view.home.HomeScreen
import com.exemplio.geapfitmobile.view.screens.message.MessageScreen

@Composable
fun NavigationWrapper() {
    val principalNavHost = rememberNavController()

    DisposableEffect(principalNavHost) {
        GlobalNav.setRoot(principalNavHost)
        onDispose {  }
    }

    NavHost(navController = principalNavHost, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                navigateToRegister = { principalNavHost.navigate(Register) },
                navigateToHome = {
                    principalNavHost.navigate(Home) {
                        popUpTo(principalNavHost.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<Register> { RegisterScreen(navigateBack = { principalNavHost.navigateUp() }) }
        composable<Home> {
            HomeScreen(principalNavHost)
        }
        composable<TabSingleChat> {
            MessageScreen()
        }
        composable<TabContacts> {
            ContactsScreen()
        }
    }
}