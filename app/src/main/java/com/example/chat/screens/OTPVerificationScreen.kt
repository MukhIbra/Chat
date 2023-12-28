package com.example.chat.screens

import android.app.Activity
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chat.R
import com.example.chat.navigation.Screen
import com.example.chat.ui.theme.Black20
import com.example.chat.ui.theme.Primary
import com.example.chat.ui.theme.Secondary
import com.example.chat.ui.theme.Tertiary
import com.example.chat.ui.theme.Text2
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Preview
@Composable
fun OTPVerificationPrev() {
    OTPVerificationScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun OTPVerificationScreen(navController: NavController) {
    val phoneNumber = rememberSaveable {
        mutableStateOf("")
    }

    val otp = rememberSaveable {
        mutableStateOf("")
    }

    val verificationID = rememberSaveable {
        mutableStateOf("")
    }//Need this to get credentials

    val codeSent = rememberSaveable {
        mutableStateOf(false)
    }//Optional- Added just to make consistent UI

    val loading = rememberSaveable {
        mutableStateOf(false)
    }//Optional

    val passwordVisibility = remember { mutableStateOf(false) }

    val context = LocalContext.current

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Primary)
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(42.dp))
        TextField(//import androidx.compose.material3.TextField
            enabled = !codeSent.value && !loading.value,
            value = phoneNumber.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { if (it.length <= 13) phoneNumber.value = it },
            placeholder = { Text(text = "Enter your phone number") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(),
            supportingText = {
                Text(
                    text = "${phoneNumber.value.length} / 13",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
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

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedVisibility(
            visible = !codeSent.value,
            exit = scaleOut(
                targetScale = 0.5f,
                animationSpec = tween(durationMillis = 500, delayMillis = 100)
            ),
            enter = scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(durationMillis = 500, delayMillis = 100)
            )
        ) {
            Button(
                enabled = !loading.value && !codeSent.value,
                onClick = {
                    if (TextUtils.isEmpty(phoneNumber.value) || phoneNumber.value.length < 10) {
                        Toast.makeText(
                            context,
                            "Enter a valid phone number",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        loading.value = true
                        val number = "${phoneNumber.value}"
                        sendVerificationCode(
                            number,
                            mAuth,
                            context as Activity,
                            callbacks
                        )//This is the main method to send the code after verification
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
            ) {
                Text(
                    text = "Generate OTP", color = Text2,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

//    if (loading.value) {
//        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//    }

    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Toast.makeText(context, "Verification successful..", Toast.LENGTH_SHORT).show()
            loading.value = false
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(context, "Verification failed.. ${p0.message}", Toast.LENGTH_LONG)
                .show()
            loading.value = false
        }

        override fun onCodeSent(
            verificationId: String,
            p1: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, p1)
            verificationID.value = verificationId
            codeSent.value = true
            loading.value = false
        }
    }
    AnimatedVisibility(
        visible = codeSent.value,
        // Add same animation here
    ) {
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
                enabled = !loading.value,
                value = otp.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { if (it.length <= 6) otp.value = it },
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text(text = "Enter your otp") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                supportingText = {
                    Text(
                        text = "${otp.value.length} / 6",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
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
                visualTransformation =
                if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility.value) R.drawable.password_toggle_hide
                    else R.drawable.password_toggle

                    val description =
                        if (passwordVisibility.value) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                        Icon(painter = painterResource(id = image), description, tint = Black20)
                    }
                }
            )


                        Spacer (modifier = Modifier.height(10.dp))

                        Button (
                        enabled = !loading.value,
                onClick = {
                    if (TextUtils.isEmpty(otp.value) || otp.value.length < 6) {
                        Toast.makeText(
                            context,
                            "Please enter a valid OTP",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        loading.value = true
                        //This is the main part where we verify the OTP
                        val credential: PhoneAuthCredential =
                            PhoneAuthProvider.getCredential(
                                verificationID.value, otp.value
                            )//Get credential object
                        mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(context as Activity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Your mobile phone is verified successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(Screen.Home.route)
                                } else {
                                    loading.value = false
                                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(
                                            context,
                                            "Verification failed.." + (task.exception as FirebaseAuthInvalidCredentialsException).message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        if ((task.exception as FirebaseAuthInvalidCredentialsException).message?.contains(
                                                "expired"
                                            ) == true
                                        ) {//If code is expired then get a chance to resend the code
                                            codeSent.value = false
                                            otp.value = ""
                                        }
                                    }
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Verify OTP", modifier = Modifier.padding(8.dp))
            }

        }
    }


}

private fun sendVerificationCode(
    number: String,
    auth: FirebaseAuth,
    activity: Activity,
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
) {
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(number)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}
