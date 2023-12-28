package com.example.chat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.example.chat.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chat.navigation.Screen
import com.example.chat.ui.theme.Aqua
import com.example.chat.ui.theme.InterBold
import com.example.chat.ui.theme.InterSemibold
import com.example.chat.util.SharedHelper

@Composable
fun StartScreen(
    navHostController: NavHostController
) {

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background), contentDescription = "",
            contentScale = ContentScale.FillWidth
        )

        Box(
            modifier = Modifier
                .padding(top = 220.dp)
                .align(Alignment.Center)
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.stay_with_your_friends),
                    style = TextStyle(
                        fontSize = 36.sp,
                        color = Color.White,
                        fontFamily = InterBold
                    )
                )
                CustomCheckBox()

            }
        }
        Button(
            onClick = {
                if (SharedHelper.getInstance(context).getKey() == "")
                    navHostController.navigate(Screen.Login.route) {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                    }
                else {
                    navHostController.navigate(Screen.Home.route) {
                        popUpTo(navHostController.graph.id) {
                            inclusive = true
                        }
                    }
                }

            }, modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.BottomCenter)
                .height(60.dp),
            shape = RoundedCornerShape(100.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = stringResource(R.string.get_started), style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = InterBold
                )
            )
        }
    }
}

@Composable
fun CustomCheckBox() {

    Row(
        modifier = Modifier.padding(vertical = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 10.dp, topEnd = 10.dp, bottomStart = 80.dp, bottomEnd = 80.dp
                    )
                )
                .background(Aqua), contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "",
                modifier = Modifier.size(15.dp),
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = stringResource(R.string.secure_private_messaging), style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = InterSemibold
            )
        )
    }

}