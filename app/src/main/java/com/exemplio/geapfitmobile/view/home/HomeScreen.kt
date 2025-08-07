@file:OptIn(ExperimentalMaterial3Api::class)

package com.exemplio.geapfitmobile.view.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.exemplio.geapfitmobile.R
import com.exemplio.geapfitmobile.view.core.components.ErrorBanner
import com.exemplio.geapfitmobile.view.core.navigation.BottomNavigation
import com.exemplio.geapfitmobile.view.core.navigation.BottomNavigation.Companion.tabBottomItemsList
import com.exemplio.geapfitmobile.view.core.navigation.NavigationBottomWrapper

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(principalNavigaton: NavHostController,homeViewModel: HomeViewModel = hiltViewModel()) {

    val tabNavController = rememberNavController()
    val navStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navStackEntry?.destination?.route
    val selectedTab = tabBottomItemsList.firstOrNull { tab ->
        tab.tabScreens::class.qualifiedName == currentRoute?.substringBefore("?")
    } ?: BottomNavigation.TabHome

    Scaffold(
        bottomBar = {
            GeapFitBottomBar(
                selectedTab = selectedTab,
                navHost = tabNavController,
            )
        }) {
        val bottomPadding = tenPercentPadding()
        NavigationBottomWrapper(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = bottomPadding), navHostController = tabNavController,
                principalNavHost = principalNavigaton
            )
        }
}

@Composable
fun GeapFitBottomBar(selectedTab: BottomNavigation, navHost: NavHostController) {
    BottomAppBar() {
        tabBottomItemsList.forEach { tabItem ->
            val isSelected = tabItem == selectedTab
            NavigationBarItem(
                selected = isSelected,
                icon = {
                    Icon(painter = painterResource(id=tabItem.icon), contentDescription = null)
                },
                label = { Text(stringResource(tabItem.label)) },
                onClick = {
                    if (!isSelected) {
                        navHost.navigate(tabItem.tabScreens) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                })
        }
    }

}

@Composable
fun tenPercentPadding(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeightPx = configuration.screenHeightDp
    return (screenHeightPx * 0.10f).dp
}