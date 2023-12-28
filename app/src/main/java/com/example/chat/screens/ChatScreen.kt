package com.example.chat.screens

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chat.R
import com.example.chat.data.Message
import com.example.chat.data.User
import com.example.chat.ui.theme.Gray
import com.example.chat.ui.theme.Gray400
import com.example.chat.ui.theme.InterBold
import com.example.chat.ui.theme.InterRegular
import com.example.chat.ui.theme.LightRed
import com.example.chat.ui.theme.LightYellow
import com.example.chat.ui.theme.Yellow
import com.example.chat.util.Helper
import com.example.chat.util.SharedHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ChatScreen(
    navHostController: NavHostController, key: String
) {
    val context = LocalContext.current
    val deleteDialogOpen = remember { mutableStateOf(false) }
    val deleteIndex = remember { mutableStateOf(-1) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val name = remember { mutableStateOf("") }
    val gotUser = remember { mutableStateOf(false) }
    val user = remember { mutableStateOf(User("", "", "", "", "", "")) }

    Helper.getUser(key) {
        user.value = it
        name.value = user.value.firstName + " " + user.value.lastName
        gotUser.value = true
    }
    val messages = remember { mutableStateListOf<Message>() }
    Helper.getMessages(LocalContext.current, key) { m ->
        messages.clear()
        messages.addAll(m)
        coroutineScope.launch {
            delay(300)
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
        }

    }

    var message by remember { mutableStateOf("") }
    val data =
        navHostController.previousBackStackEntry?.savedStateHandle?.get<User>("data") ?: User()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UserNameRow(
                person = user.value,
                modifier = Modifier.padding(top = 60.dp, start = 20.dp, end = 20.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp, topEnd = 30.dp
                        )
                    )
                    .background(Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = 15.dp,
                        top = 25.dp,
                        end = 15.dp,
                        bottom = 50.dp
                    )
                ) {
                    items(messages) {
                        MessageRow(message = it)
                    }
                }
            }
        }

        CustomTextField(
            text = message, onValueChange = { message = it },
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .align(Alignment.BottomCenter),

            ) {
            Helper.writeMessage(message.trim(), context, user.value.key!!)
            message = ""

        }
    }

}

@Composable
fun MessageRow(
    message: Message
) {
    val currentUserKey = SharedHelper.getInstance(LocalContext.current).getKey()
    val fromMe = message.from == currentUserKey

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (!fromMe) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(
                    if (!fromMe) LightRed else LightYellow
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.text.toString(), style = TextStyle(
                    color = Color.Black,
                    fontFamily = InterRegular,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = message.date.toString(),
            style = TextStyle(
                color = Gray,
                fontFamily = InterRegular,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(164.dp),
        border = BorderStroke(1.dp, Gray400)
    ) {
        TextField(
            value = text, onValueChange = { onValueChange(it) },
            placeholder = {
                Text(
                    text = stringResource(R.string.type_message),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = InterRegular,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = { CommonIconButton(imageVector = Icons.Default.Add, { onClick() }) },
            trailingIcon = { CommonIconButtonDrawable(R.drawable.mic) }

        )
    }

}

@Composable
fun CommonIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(33.dp)
            .clip(CircleShape)
            .background(Yellow), contentAlignment = Alignment.Center

    ) {
        Icon(
            imageVector = imageVector, contentDescription = "",
            tint = Color.Black,
            modifier = Modifier
                .size(15.dp)
                .clickable {
                    onClick()

                }
        )
    }

}

@Composable
fun CommonIconButtonDrawable(
    @DrawableRes icon: Int
) {

    Box(
        modifier = Modifier
            .size(33.dp)
            .clip(CircleShape)
            .background(Yellow), contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon), contentDescription = "",
            tint = Color.Black,
            modifier = Modifier.size(15.dp)
        )
    }

}

@Composable
fun UserNameRow(
    modifier: Modifier = Modifier,
    person: User
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "",
                modifier = Modifier.size(42.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = person.firstName.toString(), style = TextStyle(
                        color = Color.White,
                        fontFamily = InterBold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = stringResource(R.string.online), style = TextStyle(
                        color = Color.White,
                        fontFamily = InterRegular,
                        fontSize = 14.sp
                    )
                )
            }
        }
        IconButton(
            onClick = {}, modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "", tint = Color.White)
        }
    }

}

