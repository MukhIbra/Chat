package com.example.chat.navigation

sealed class Screen(val route: String){
    object Start : Screen("start")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object OTPVerification : Screen("otp")
    object Home : Screen("home")
    object Chat : Screen("chat" + "/{key}")
    object Search : Screen("search" + "/{focused}")
    object Profile : Screen("profile")
}