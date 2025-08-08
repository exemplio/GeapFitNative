package com.exemplio.geapfitmobile.view.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class TabScreens(val route: String){

    @Serializable
    data object TabHome : TabScreens("tab_home?query={query}") {
        fun createRoute(query: String): String = "tab_home?query=$query"
    }

    @Serializable
    data object TabAgenda: TabScreens("tab_home?query={query}")

    @Serializable
    data object TabBusiness: TabScreens("tab_home?query={query}")

    @Serializable
    data object TabLibrary: TabScreens("tab_home?query={query}")

    @Serializable
    data object TabProfile: TabScreens("tab_home?query={query}")

    @Serializable
    data object TabSingleChat: TabScreens("tab_home?query={query}")
}