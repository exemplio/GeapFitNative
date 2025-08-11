package com.geapfit.utils

import com.exemplio.geapfitmobile.utils.MyUtils


class Path(val path: String)

object StaticNamesPath {
    val passwordGrant = Path("${MyUtils.typeAuth}passwordGrant")
    val getClients = Path("${MyUtils.type}get-clients")
    val getMessages = Path("${MyUtils.type}get-messages")
}