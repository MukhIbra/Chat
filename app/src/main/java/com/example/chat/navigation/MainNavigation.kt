package com.example.chat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat.screens.ChatScreen

import com.example.chat.screens.HomeScreen
import com.example.chat.screens.LoginScreen
import com.example.chat.screens.OTPVerificationScreen
import com.example.chat.screens.ProfileScreen
import com.example.chat.screens.SignupScreen
import com.example.chat.screens.StartScreen

@Composable
fun MainNavigation() {

    val navHostController = rememberNavController()

    NavHost(navController = navHostController, startDestination = Screen.Start.route){
        composable(Screen.Start.route){
            StartScreen(navHostController)
        }
        composable(Screen.Home.route){
            HomeScreen(navHostController)
        }
        composable(Screen.OTPVerification.route){
            OTPVerificationScreen(navHostController)
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("key") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val key = navBackStackEntry.arguments?.getString("key")

            ChatScreen(navHostController, key = key!!)
        }
        composable(Screen.Login.route){
            LoginScreen(navHostController)
        }
        composable(Screen.Signup.route){
            SignupScreen(navHostController)
        }
        composable(Screen.Profile.route){
            ProfileScreen(navHostController)
        }
    }

}