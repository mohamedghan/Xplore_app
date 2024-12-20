package com.example.xplore.ui.home

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.xplore.R
import com.example.xplore.ui.LayoutScaffold
import com.example.xplore.ui.theme.*

@Composable
fun HomeScreen(
    onAction: (actions: HomeScreenActions) -> Unit,
    navController: NavHostController
) {
LayoutScaffold(name = "Mohamed G.") {
    HomeScreenContent(modifier = Modifier, onAction = onAction)
}

}

@Composable
private fun HomeScreenContent(
    modifier: Modifier,
    onAction: (actions: HomeScreenActions) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 0.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Home Screen" }
    ) {
        Spacer(modifier = Modifier.size(20.dp))
        ToolbarHome()
        Header()
        Categories {
            onAction(HomeScreenActions.Details)
        }
        Spacer(modifier = Modifier.size(56.dp))
    }
}

@Composable
fun Header() {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.home_main),
                contentDescription = null,
                modifier = Modifier
                    .size(342.dp, 285.dp)
                    .align(Alignment.Center),
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { }
                    .size(296.dp, 80.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Where to go", style = MaterialTheme.typography.search)
                Spacer(modifier = Modifier.weight(1F))
                Image(painter = painterResource(id = R.drawable.search), contentDescription = null)
            }
        }
}

@Composable
fun NavBar(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minWidth = 327.dp, minHeight = 67.dp)
                .border(shape = RoundedCornerShape(12.dp), width = 1.dp, color = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minWidth = 370.dp, minHeight = 67.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .offset(y = (-8).dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(16.dp, 2.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ticket),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.size(104.dp))
                Icon(
                    painter = painterResource(id = R.drawable.saved),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.size(16.dp))

            }
        }

        Image(
            painter = painterResource(id = R.drawable.boat),
            contentDescription = null,
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .offset(y = (-24).dp)
        )
    }
}

@Composable
fun ToolbarHome() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.home_profile),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = "Welcome Home", style = MaterialTheme.typography.captionDefault)
            Text(text = "Mohamed G.", style = MaterialTheme.typography.H1)
        }
        Spacer(modifier = Modifier.weight(1F))
        Image(painter = painterResource(id = R.drawable.notification), contentDescription = null)
    }
}

@Composable
private fun Categories(onClick: () -> Unit) {
    Spacer(modifier = Modifier.size(24.dp))
    Column(Modifier.fillMaxWidth()) {
        Text(text = "Popular Categories", style = MaterialTheme.typography.H2)
        Spacer(modifier = Modifier.size(16.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Category(paint = R.drawable.cat_1, text = "Mountain")
            Category(paint = R.drawable.cat_2, text = "Adventure")
            Category(paint = R.drawable.cat_3, text = "Beach")
            Category(paint = R.drawable.cat_4, text = "City")
        }
        HorizontalPagerWithOffsetTransition(onClick = onClick)
    }
}

@Composable
fun Category(@DrawableRes paint: Int, text: String) {
    Column(
        modifier = Modifier.defaultMinSize(minWidth = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = paint),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = text, style = MaterialTheme.typography.captionDefault)
    }
}

@Composable
fun HorizontalPagerWithOffsetTransition(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = {
        mapOfPage.size
    })

    Spacer(modifier = Modifier.size(16.dp))
    Text(text = "Recommended", style = MaterialTheme.typography.H2)
    HorizontalPager(
        state = pagerState,
        // Add 32.dp horizontal padding to 'center' the pages
        contentPadding = PaddingValues(horizontal = 32.dp),
        modifier = modifier.fillMaxWidth()
    ) { page ->
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            HorizontalPage(page = page, onClick = onClick)
        }
    }
}

@Composable
fun HorizontalPage(page: Int, onClick: () -> Unit) {
    Box(modifier = Modifier.clickable { onClick() }) {
        Image(
            painter = painterResource(id = mapOfPage[page].first),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .align(
                    Alignment.BottomStart
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mapOfPage[page].second,
                style = MaterialTheme.typography.H1,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.size(8.dp))
            Image(painter = painterResource(id = R.drawable.star), contentDescription = null)
            Text(text = "(4.9)", style = MaterialTheme.typography.captionDefault)
            Spacer(modifier = Modifier.weight(1F))
            Text(text = "1942 TND", style = MaterialTheme.typography.H2)
        }

    }
}

val mapOfPage = listOf(
    Pair(R.drawable.img_1, "Ain Draham"),
    Pair(R.drawable.img_2, "Ain Draham"),
    Pair(R.drawable.img_3, "Ain Draham"),
)

sealed class HomeScreenActions {
    object Details : HomeScreenActions()
}