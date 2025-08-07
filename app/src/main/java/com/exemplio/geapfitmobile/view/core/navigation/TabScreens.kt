package com.exemplio.geapfitmobile.view.core.navigation

import kotlinx.serialization.Serializable

sealed class TabScreens{

    @Serializable
    data object TabHome: TabScreens()

    @Serializable
    data object TabAgenda: TabScreens()

    @Serializable
    data object TabBusiness: TabScreens()

    @Serializable
    data object TabLibrary: TabScreens()

    @Serializable
    data object TabProfile: TabScreens()
}