package com.exemplio.geapfitmobile.utils

class Path(val path: String)

object StaticNamesPath {
    val passwordGrant = Path("${MyUtils.typeAuth}passwordGrant")
    val register = Path("${MyUtils.typeAuth}register")
    val getClients = Path("${MyUtils.type}get-clients")
    val getMessages = Path("${MyUtils.type}get-messages")
    val getChats = Path("${MyUtils.type}get-chats")
}