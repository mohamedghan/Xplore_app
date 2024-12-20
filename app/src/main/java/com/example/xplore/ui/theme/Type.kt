package com.example.xplore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.xplore.R

val gilroy = FontFamily(Font(R.font.gilroy, FontWeight.Normal), Font(R.font.gilroy_bold, FontWeight.Bold))



val Typography.buttonText: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W700,
        fontSize = 20.sp,
    )

val Typography.captionDefault: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
    )

val Typography.H1: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp,
    )


val Typography.body: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
    )

val Typography.bodyBold: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W700,
        fontSize = 14.sp,
    )

val Typography.H2: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
    )

val Typography.H3: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
    )

val Typography.price: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W500,
        fontSize = 30.sp,
    )

val Typography.currency: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
    )


val Typography.search: TextStyle
    get() = TextStyle(
        fontFamily = gilroy,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
    )

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    )
)