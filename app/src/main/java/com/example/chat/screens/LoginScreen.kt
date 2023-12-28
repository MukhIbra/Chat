package com.example.chat.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chat.R
import com.example.chat.navigation.Screen
import com.example.chat.ui.theme.*
import com.example.chat.util.Helper
import com.example.chat.util.SharedHelper

@Preview
@Composable
fun PreviewLogin() {
    LoginScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val passwordVisibility = remember { mutableStateOf(false) }

    val username = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Primary)
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(42.dp))
        TextField(
            value = username.value,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            onValueChange = { username.value = it.trim() },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(text = "Username", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = "",
                    tint = Black20
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Text2,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Text2,
                containerColor = Secondary
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password.value,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            onValueChange = { password.value = it.trim() },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(text = "Password", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "",
                    tint = Black20
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Text2,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Text2,
                containerColor = Secondary
            ),
            textStyle = TextStyle(fontSize = 16.sp),
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisibility.value) R.drawable.password_toggle_hide
                else R.drawable.password_toggle

                val description =
                    if (passwordVisibility.value) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                    Icon(painter = painterResource(id = image), description, tint = Black20)
                }
            })
        Spacer(modifier = Modifier.height(42.dp))
        Button(
            onClick = {
                Log.d("TAG", "LoginScreen: ")
                focusManager.clearFocus(true)
                Helper.logIn(username.value, password.value) { key ->
                    Log.d("TAG2", "LoginScreen: ")
                    if (key != null) {
                        Log.d("TAG3", "LoginScreen: ")
                        SharedHelper.getInstance(context).saveKey(key)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                        Toast.makeText(context, "Successfully logged in", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Incorrect username or password",
                            Toast.LENGTH_SHORT
                        ).show()
                        password.value = ""
                    }
                }

            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Tertiary,
                disabledContainerColor = Secondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 62.dp),
            enabled = username.value.isNotEmpty() && password.value.length > 7
        ) {
            Text(
                text = "Log in",
                color = Text2,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                navController.navigate(Screen.Signup.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 62.dp)
        ) {
            Text(
                text = "Sign up",
                color = Text,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

        }

    }
}