package com.example.xplore.ui.myfeed

import android.view.animation.Animation
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.xplore.data.JourneyResponse
import com.example.xplore.ui.BottomNavItem
import com.example.xplore.ui.LayoutScaffold

@Composable
fun MyFeedScreen(modifier: Modifier = Modifier, viewModel: MyFeedViewModel = hiltViewModel(), onActions: (FeedScreenActions) -> Unit) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val buttons = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Chats
    )
    LayoutScaffold(
        fab = {

        },
        bottom = {

            BottomAppBar(
                actions = {},
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { onActions(FeedScreenActions.AddJourney) },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
/*
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                buttons.forEachIndexed {i, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(text = item.label) },
                        selected = i == 1,
                        onClick = {
                            when(item) {
                                BottomNavItem.Home -> {

                                }
                                else -> {

                                }
                            }
                           /* navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when navigating back to a previously selected item
                                restoreState = true
                            }*/
                        }
                    )
                }
            }
*/
        },
        name = "Mohamed G.", customAction = { // TODO: FIX show correct name
        Button(
            onClick = {
                viewModel.logout()
                onActions(FeedScreenActions.SignOut)
            },
            content = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Logout"
                )
            }
        )
    }) { innerPadding ->
        when (items) {
            is MyFeedUiState.Success -> {
                MyFeedScreen(
                    posts = (items as MyFeedUiState.Success).data,
                    onLike = { i ->
                        val likedByMe = i.likedBy.contains((items as MyFeedUiState.Success).myid)
                        if(likedByMe) {
                            viewModel.dislikeMyFeed(i)
                        } else {
                            viewModel.likeMyFeed(i)
                        }
                        viewModel.reload()
                    },
                    myid = (items as MyFeedUiState.Success).myid,
                    modifier = modifier.padding(innerPadding),
                    onActions = onActions
                )
            }

            is MyFeedUiState.Error -> {
                ErrorPage(onRetry = {})
            }

            else -> {
                LoadingPage()
            }
        }

        }
}

@Composable
internal fun MyFeedScreen(myid: String, posts: List<JourneyResponse>, onLike: (post: JourneyResponse) -> Unit, modifier: Modifier, onActions: (FeedScreenActions) -> Unit) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(posts.reversed()) { i, post ->
                FadeInAnimation(i) {
                    PostItem(post, myid, onActions = onActions, onLike = {
                        onLike(post)
                    })
                }
            }
        }

}

@Composable
private fun FadeInAnimation(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * 100,
                easing = LinearEasing
            )
        )
    ) {
        content()
    }
}

@Composable
fun PostItem(post: JourneyResponse, myid:String, onLike: () -> Unit, onActions: (FeedScreenActions) -> Unit) {
    var isLiked by remember { mutableStateOf(post.likedBy.contains(myid)) }
    var likeCount by remember { mutableIntStateOf(post.likedBy.size) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User Profile Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = post.user.avatar,
                    contentDescription = "${post.user.name}'s profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = post.user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            IconButton(onClick = { /* Handle more options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }


        AsyncImage(
            model = post.figure,
            contentDescription = "Post by ${post.user.name}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable {
                    onActions(FeedScreenActions.ViewPost(post))
                }, // Square aspect ratio
            contentScale = ContentScale.Crop

        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Like, Comment, Share Icons
            Row {
                IconButton(onClick = {
                    if(isLiked) {
                        likeCount--
                    } else {
                        likeCount++
                    }
                    isLiked = !isLiked
                    onLike()
                }) {
                    Icon(
                        imageVector = if(isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if(isLiked) Color.Red else Color.Black
                    )
                }
                // Add comment and share icons similarly
            }
        }

        // Likes and Caption
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = "$likeCount likes",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "${post.user.name} ${post.caption}",
                fontSize = 14.sp
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

sealed class FeedScreenActions {
    data object SignOut : FeedScreenActions()
    data class ViewPost(val p:JourneyResponse) : FeedScreenActions()
    data object AddJourney: FeedScreenActions()
}


@Composable
fun ErrorPage(
    message: String = "Failed to load content",
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error Icon",
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Retry")
        }
    }
}

@Composable
fun LoadingPage(
    message: String = "Loading content..."
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main loading indicator
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        // Loading message
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Shimmer loading placeholders
        Spacer(modifier = Modifier.height(32.dp))
        LoadingPlaceholders()
    }
}

@Composable
private fun LoadingPlaceholders() {
    val shimmerEffect = rememberShimmerAnimationSpec()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        repeat(3) {
            ShimmerItem(shimmerEffect)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ShimmerItem(shimmerEffect: ShimmerAnimationSpec) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .shimmer(shimmerEffect)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Placeholder for image
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .weight(1f)
        ) {
            // Title placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(12.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
private fun rememberShimmerAnimationSpec(): ShimmerAnimationSpec {
    return remember {
        ShimmerAnimationSpec(
            durationMillis = 1000,
            delay = 300,
            iterations = Animation.INFINITE
        )
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun LoadingPagePreview() {
    MaterialTheme {
        LoadingPage()
    }
}

// Shimmer effect animation spec
data class ShimmerAnimationSpec(
    val durationMillis: Int,
    val delay: Int,
    val iterations: Int
)

// Shimmer modifier extension
fun Modifier.shimmer(spec: ShimmerAnimationSpec): Modifier = composed {
    var position by remember { mutableFloatStateOf(0f) }
    val transition = rememberInfiniteTransition(label = "shimmer")

    position = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(spec.durationMillis, spec.delay),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer position"
    ).value

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            start = Offset(position * 1000f, 0f),
            end = Offset(position * 1000f + 100f, 100f)
        )
    )
}