package com.exemplio.geapfitmobile.view.core.navigation

import AgendaScreen
import BusinessScrenn
import ChatsScreen
import ClientScreen
import LibraryScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.*

@Composable
fun NavigationBottomWrapper(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    DisposableEffect(navHostController) {
        GlobalNav.setBottom(navHostController)
        onDispose {  }
    }

    NavHost(modifier = modifier, navController = navHostController, startDestination = TabHome) {
        composable<TabHome> { ClientScreen() }
        composable<TabAgenda> { AgendaScreen() }
        composable<TabLibrary> { LibraryScreen() }
        composable<TabBusiness> { BusinessScrenn() }
        composable<TabChats> { ChatsScreen() }
    }
}
