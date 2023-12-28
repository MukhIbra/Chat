package com.example.chat.data

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.example.chat.R
import kotlinx.parcelize.Parcelize

data class User(
    var username: String? = null,
    var password: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var image: String? = null,
    var key: String? = null
)