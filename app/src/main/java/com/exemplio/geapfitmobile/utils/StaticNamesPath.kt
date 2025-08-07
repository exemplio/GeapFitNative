package com.geapfit.utils

import com.exemplio.geapfitmobile.utils.MyUtils


class Path(val path: String)

object StaticNamesPath {
    val passwordGrant = Path("${MyUtils.typeAuth}:signInWithPassword")
    val getClients = Path("${MyUtils.type}/clients")
}