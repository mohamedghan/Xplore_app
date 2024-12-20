package com.example.xplore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.xplore.R
import com.example.xplore.ui.myfeed.FeedScreenActions
import com.example.xplore.ui.theme.H1
import com.example.xplore.ui.theme.captionDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutScaffold(name: String, bottom: @Composable () -> Unit = {}, fab: @Composable () -> Unit = {}, customAction: @Composable () -> Unit = {}, content: @Composable (v: PaddingValues) -> Unit) {
    Scaffold (
        floatingActionButton = fab,
        topBar = { TopAppBar(
            actions = {
                customAction()
            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.home_profile),
                    contentDescription = null
                )
            },
            title = {
                Column {
                    Text(text = "Welcome Home", style = MaterialTheme.typography.captionDefault)
                    Text(text = name, style = MaterialTheme.typography.H1)
                }
            },
            )
                 },
        bottomBar = bottom,
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(10.dp),
        content=content
    )
}

@Preview
@Composable
fun BottomAppBarExample() {
    Scaffold(
        bottomBar = {

        },
    ) { innerPadding ->
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Example of a scaffold with a bottom app bar."
        )
    }
}