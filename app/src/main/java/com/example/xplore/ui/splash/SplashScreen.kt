package com.example.xplore.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.xplore.ui.theme.buttonText

@Composable
fun SplashScreen(
    onAction: () -> Unit
) {
    Scaffold(
    ) { innerPadding ->
        SplashScreenContent(modifier = Modifier.padding(innerPadding), onAction = onAction)
    }
}

@Composable
private fun SplashScreenContent(
    modifier: Modifier,
    onAction: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = com.example.xplore.R.drawable.img_splash),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Button(
            onClick = { onAction() },
            modifier = Modifier.padding(bottom = 56.dp).size(220.dp, 76.dp).align(Alignment.BottomCenter),
            shape = RoundedCornerShape(72.dp),
        ) {
            Text(text = "Let's Start", style = MaterialTheme.typography.buttonText)
            Spacer(modifier = Modifier.size(16.dp))
            Icon(
                painter = painterResource(id = com.example.xplore.R.drawable.arrow_right),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

sealed class SplashScreenActions {
    object LoadHome : SplashScreenActions()
}