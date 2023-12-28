package com.example.chat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.chat.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chat.data.Message
import com.example.chat.data.User
import com.example.chat.navigation.Screen
import com.example.chat.ui.theme.DarkGray
import com.example.chat.ui.theme.Gray
import com.example.chat.ui.theme.Gray400
import com.example.chat.ui.theme.InterBold
import com.example.chat.ui.theme.InterMedium
import com.example.chat.ui.theme.InterRegular
import com.example.chat.ui.theme.InterSemibold
import com.example.chat.ui.theme.Line
import com.example.chat.ui.theme.Yellow
import com.example.chat.util.Helper
import com.example.chat.util.SharedHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val currentUserKey = SharedHelper.getInstance(context).getKey()!!
    val contacts = remember { mutableStateListOf<User>() }
    val lastMessages = remember { mutableStateListOf<Message>() }
    val explore = remember { mutableStateOf(false) }
    val dataFetched = remember { mutableStateOf(false) }


    Helper.getChats(currentUserKey) { c, lm ->
        contacts.clear()
        contacts.addAll(c)
        Log.d("TAG", "HomeScreen: ${contacts.joinToString()}")
        lastMessages.clear()
        lastMessages.addAll(lm)
        Log.d("TAG", "HomeScreen: ${lastMessages.joinToString()}")
        explore.value = c.isEmpty()
        dataFetched.value = true
    }


    val userList = remember {
        mutableStateListOf<User>()
    }

//    val curruser = remember { mutableStateOf(User("", "", "", "", "", "")) }
//    Helper.getUser(currentUserKey) {
//        curruser.value = it
//    }


    val reference = Firebase.database.reference.child("users")
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val children = snapshot.children
            userList.clear()
            children.forEach {
                Log.d("tag", "onDataChange: ${it.getValue<User>()?.username}")
                val user = it.getValue<User>()
                if (user != null && user.key != currentUserKey){
                    userList.add(user)
                }

//                if(user?.key == currentUserKey){
//                    thisUser = user!!
//                }
            }

        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("TAG", "onCancelled: ${error.message}")
        }

    })


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header()
            LazyRow(modifier = Modifier.padding(vertical = 20.dp)) {
                item {
                    AddStoryLayout(modifier = Modifier.padding(start = 23.dp)){
                        navHostController.navigate(Screen.Profile.route)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                items(contacts) {
                    UserStory(user = it) {
                        navHostController.navigate("chat/${it.key}")
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp, topEnd = 30.dp
                        )
                    )
                    .background(Color.White)
            ) {
                RoundedCorner(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 15.dp)
                )
                LazyColumn(
                    modifier = Modifier.padding(bottom = 15.dp, top = 30.dp)
                ) {
                    items(contacts) {
                        var index = 0
                        for (i in 0 .. lastMessages.size - 1){
                            if (lastMessages[i].to == it.key){
                                index = i
                                break
                            }
                        }

                        UserEachRow(user = it, lastMessages[index]) {

                            navHostController.navigate("chat/${it.key}")
                        }
                    }
                }
            }
        }

    }

}


@Composable
fun RoundedCorner(
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .width(90.dp)
            .height(5.dp)
            .clip(RoundedCornerShape(90.dp))
            .background(
                Gray400
            )
    )
}


@Composable
fun UserEachRow(
    user: User,
    lastMessages : Message,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .noRippleEffect { onClick() }
            .padding(horizontal = 20.dp, vertical = 5.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "",
                        modifier = Modifier.size(60.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                    ) {
                        Text(
                            text = user.firstName.toString(), style = TextStyle(
                                color = Color.Black, fontSize = 15.sp, fontFamily = InterBold
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = lastMessages.text.toString(), style = TextStyle(
                                color = Gray, fontSize = 14.sp, fontFamily = InterMedium
                            )
                        )
                    }

                }
                Text(
                    text = lastMessages.date.toString(), style = TextStyle(
                        color = Gray, fontSize = 12.sp, fontFamily = InterMedium
                    )
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Line)
        }
    }

}

@Composable
fun UserStory(
    user: User, modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(end = 10.dp)
            .noRippleEffect { onClick() }
    ) {

        Card(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, Yellow),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(Yellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "",
                    modifier = Modifier.size(60.dp),
                    tint = Color.Unspecified
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.firstName.toString(), style = TextStyle(
                color = Color.White, fontSize = 13.sp, fontFamily = InterMedium
            ), modifier = Modifier.align(Alignment.CenterHorizontally)
        )

    }
}


@Composable
fun AddStoryLayout(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier,
    ) {

        Card(
            modifier = Modifier.size(70.dp)
                .noRippleEffect { onClick() },
            shape = CircleShape,
            border = BorderStroke(2.dp, DarkGray),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(Yellow),
                contentAlignment = Alignment.Center,

            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "",
                        modifier = Modifier.size(12.dp),
                        tint = Yellow
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.add_story), style = TextStyle(
                color = Color.White, fontSize = 13.sp, fontFamily = InterMedium
            ), modifier = Modifier.align(Alignment.CenterHorizontally)
        )

    }

}

@Composable
fun Header() {

    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.White,
                fontFamily = InterRegular,
                fontSize = 20.sp,
                fontWeight = FontWeight.W300
            )
        ) {
            append("Welcome back, ")
        }
        withStyle(
            style = SpanStyle(
                color = Color.White,
                fontFamily = InterSemibold,
                fontSize = 20.sp,
            )
        ) {
            append("Jayant!")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 60.dp)
    ) {
        Text(text = annotatedString)
    }

}

@SuppressLint("UnnecessaryComposedModifier", "UnrememberedMutableInteractionSource")
fun Modifier.noRippleEffect(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}