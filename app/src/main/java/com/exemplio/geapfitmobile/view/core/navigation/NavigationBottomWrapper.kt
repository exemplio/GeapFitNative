package com.exemplio.geapfitmobile.view.core.navigation

import AgendaScreen
import BusinessScrenn
import ChatsScreen
import ClientScreen
import LibraryScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.*
import com.exemplio.geapfitmobile.view.screens.singleChat.SingleChatScreen

@Composable
fun NavigationBottomWrapper(modifier: Modifier = Modifier, principalNavHost:NavHostController ,navHostController: NavHostController) {
    NavHost(modifier = modifier, navController = navHostController, startDestination = TabHome) {
        composable<TabHome> {
            ClientScreen(principalNavHost = principalNavHost)
        }
        composable<TabAgenda> {
            AgendaScreen(principalNavHost = principalNavHost)
        }
        composable<TabLibrary> {
            LibraryScreen(principalNavHost = principalNavHost)
        }
        composable<TabBusiness> {
            BusinessScrenn(principalNavHost = principalNavHost)
        }
        composable<TabProfile> {
            ChatsScreen(principalNavHost = principalNavHost, navController = navHostController)
        }
    }
}
