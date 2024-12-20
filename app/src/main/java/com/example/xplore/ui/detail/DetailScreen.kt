package com.example.xplore.ui.detail


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.xplore.R
import com.example.xplore.data.JourneyResponse
import com.example.xplore.ui.LayoutScaffold
import com.example.xplore.ui.theme.*

@Composable
fun DetailScreen(
    onAction: (actions: DetailScreenActions) -> Unit,
    journey: JourneyResponse?
) {
    if(journey is JourneyResponse) LayoutScaffold(
        name = journey.caption,
        customAction = {
            Button(
                onClick = {
                    onAction(DetailScreenActions.Back)
                },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Logout"
                    )
                }
            )
        }
    ) { innerPadding ->
        DetailScreenContent(modifier = Modifier.padding(innerPadding), onAction = onAction, journey)
    }
}

@Composable
private fun DetailScreenContent(
    modifier: Modifier,
    onAction: (actions: DetailScreenActions) -> Unit,
    journey: JourneyResponse
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 56.dp)
            .fillMaxHeight()
            .semantics { contentDescription = "Detail Screen" }
    ) {
        AsyncImage(
            model = journey.figure,
            contentDescription = null,
            modifier = Modifier.clip(
                RoundedCornerShape(10.dp)
            )
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = journey.user.name, style = MaterialTheme.typography.H2)
            Spacer(modifier = Modifier.weight(1F))
            Image(
                painter = painterResource(id = R.drawable.star),
                contentDescription = null,
                Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "(4.8)", style = MaterialTheme.typography.captionDefault)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = journey.caption,
            style = MaterialTheme.typography.body,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        if(journey.locations.isNotEmpty()) MoreImages(journey)
        Spacer(modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth()) {
            Column {
                Text(text = "Total Price", style = MaterialTheme.typography.H3)
                Spacer(modifier = Modifier.size(12.dp))
                Row {
                    Text(text = "TND", style = MaterialTheme.typography.currency)
                    Text(text = "3435", style = MaterialTheme.typography.price)
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            Button(
                onClick = {},
                modifier = Modifier.padding(bottom = 56.dp).size(170.dp, 56.dp),
                shape = RoundedCornerShape(72.dp)
            ) {
                Text(text = "Book Now", style = MaterialTheme.typography.buttonText)
            }
        }
    }
}

@Composable
private fun MoreImages(journey: JourneyResponse) {
    Spacer(modifier = Modifier.size(24.dp))
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)) {
        Text(text = "More Images", style = MaterialTheme.typography.bodyBold)
        Spacer(modifier = Modifier.size(16.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            journey.locations.take(4).forEachIndexed { i, location ->
                AsyncImage(
                    model = location.href?: "",
                    contentDescription = "",
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}


sealed class DetailScreenActions {
    object Back : DetailScreenActions()
}