package com.exemplio.geapfitmobile.view.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import com.exemplio.geapfitmobile.R

sealed class BottomNavigation(
    @StringRes val label: Int, val icon: Int, val tabScreens: TabScreens
) {

    companion object {
        val tabBottomItemsList = listOf(TabHome, TabAgenda, TabBusiness, TabLibrary, TabProfile)
    }

    data object TabHome : BottomNavigation(
        label = R.string.tab_clients, icon = R.drawable.ic_people, tabScreens = TabScreens.TabHome
    )

    data object TabProfile : BottomNavigation(
        label = R.string.tab_chat,
        icon = R.drawable.ic_chats,
        tabScreens = TabScreens.TabProfile
    )

    data object TabLibrary : BottomNavigation(
        label = R.string.tab_library,
        icon = R.drawable.ic_library,
        tabScreens = TabScreens.TabLibrary
    )

    data object TabBusiness : BottomNavigation(
        label = R.string.tab_business, icon = R.drawable.ic_wallet, tabScreens = TabScreens.TabBusiness
    )

    data object TabAgenda : BottomNavigation(
        label = R.string.tab_agenda, icon = R.drawable.ic_calendar, tabScreens = TabScreens.TabAgenda
    )
}