package com.example.xplore.ui.addjourney

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.xplore.UploadState
import com.example.xplore.data.Location
import com.example.xplore.data.LocationService
import com.example.xplore.data.Point
import com.example.xplore.ui.LayoutScaffold
import com.example.xplore.ui.LocationPicker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddJourneyScreen(
    onAction: (actions: AddJourneyScreenActions) -> Unit, locationService: LocationService, journeyViewModel: JourneyViewModel =  hiltViewModel()
) {
    val pagerState = rememberPagerState(
        pageCount = {
            10
        }
    )
    val coroutineScope = rememberCoroutineScope()
    var fromLocation by rememberSaveable { mutableStateOf<Pair<Double, Double>?>(null) }
    var toLocation by rememberSaveable { mutableStateOf<Pair<Double, Double>?>(null) }
    var figure by remember { mutableStateOf<String?>(null) }
    var intermediateLocations by remember { mutableStateOf(listOf<Location>()) }
    var caption by rememberSaveable { mutableStateOf("") }


    LayoutScaffold(
        fab = {

        },
        bottom = {
        },
        name = "Mohamed G.", customAction = { // TODO: FIX show correct name
            Button(
                onClick = {
                    onAction(AddJourneyScreenActions.Back)
                },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Logout"
                    )
                }
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // Pager content
            HorizontalPager(

                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> FromPage(
                        locationService,
                        onSelected = { from ->
                            fromLocation = from.latitude to from.longitude
                        }
                    )
                    1 -> ToPage(
                        locationService,
                        onSelected = { to ->
                            toLocation = to.latitude to to.longitude
                        }
                    )
                    2 -> FigurePage(
                        onSelected = { fig ->
                            figure = fig
                        }
                    )
                    3 -> LocationsPage(
                        onSelected = { locations, capt ->
                            intermediateLocations = locations
                            caption = capt
                        }
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button (hidden on first page) TODO: implement Back button
                /*if (pagerState.currentPage > 0) {
                   Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Text("Back")
                    }
                }*/

                // Next/Complete button
                Button(
                    onClick = {

                        coroutineScope.launch {
                            if (pagerState.currentPage < 3) {
                                when(pagerState.currentPage) {
                                    0 -> {
                                        if(fromLocation == null) {
                                            return@launch
                                        }
                                    }
                                    1 -> {
                                        if(toLocation == null) {
                                            return@launch
                                        }
                                    }
                                    2 -> {
                                        if(figure == null) {
                                            return@launch
                                        }
                                    }
                                }
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                journeyViewModel.addJourney(caption, fromLocation, toLocation, intermediateLocations, figure)
                                onAction(AddJourneyScreenActions.Back)
                                //  TODO: submit journey data
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (pagerState.currentPage < 3) "Next" else "Create Journey")
                }
            }

            // Page indicator
            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(8.dp)
                    )
                }
            }
        }
    }


}

// Example page implementations
@Composable
fun FromPage(locationService: LocationService, onSelected: (LatLng) -> Unit) {
    val initial = LatLng(36.829706818905336,10.162145644426346)
    var isVisible by remember { mutableStateOf(false) }
    val markerState = rememberMarkerState(position = initial)
    var locationname by remember { mutableStateOf<String?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initial, 10f)
    }

    Column {
        val start = if(locationname != null && isVisible) locationname else "....."
        Text("My journey started in $start", fontSize = 30.sp)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {a ->
                markerState.position = a
                onSelected(a)
                isVisible = true
            }
        ) {
            if(isVisible) Marker(
                state = markerState,
                title = "Location",
                snippet = "the location i visited"
            )
        }
    }


    LaunchedEffect(markerState.position) {
        locationname = locationService.getCountryString(markerState.position.latitude , markerState.position.longitude)
    }
}

@Composable
fun ToPage(locationService: LocationService, onSelected: (LatLng) -> Unit) {
    val initial = LatLng(36.829706818905336,10.162145644426346)
    var isVisible by remember { mutableStateOf(false) }
    val markerState = rememberMarkerState(position = initial)
    var locationname by remember { mutableStateOf<String?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initial, 10f)
    }
    Column {
        val start = if(locationname != null && isVisible) locationname else "....."
        Text("My journey ends in $start", fontSize = 30.sp)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {a ->
                markerState.position = a
                onSelected(a)
                isVisible = true
            }
        ) {
            if(isVisible) Marker(
                state = markerState,
                title = "Location",
                snippet = "the destination"
            )
        }
    }



    LaunchedEffect(markerState.position) {
        locationname = locationService.getCountryString(markerState.position.latitude , markerState.position.longitude)
    }
}

@Composable
fun FigurePage(onSelected: (String) -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var uploadState by remember { mutableStateOf(UploadState()) }

    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri is Uri) {
            selectedImageUri = uri
            uploadImage(
                context = context,
                uri = uri,
                onStateChange = {
                        state -> uploadState = state
                },
                onComplete = { link ->
                    onSelected(link)
                },
                onError = {

                },
            )

        }
    }

    Column(
        modifier = Modifier.verticalScroll(
            state = scrollState
        )
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable { getContent.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Close button to remove selected image
                IconButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }




}

@Composable
fun LocationsPage(onSelected: (List<Location>, String) -> Unit) {
    val context = LocalContext.current
    var intermediateLocations by remember { mutableStateOf(listOf<Location>()) }
    var uploadState by remember { mutableStateOf(UploadState()) }
    var caption by rememberSaveable { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = caption,
            onValueChange = {
                caption = it
                onSelected(intermediateLocations, caption)
                            },
            label = { Text("Journey Caption") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Describe your amazing trip!") },
            maxLines = 3
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Add stops along the way", style = MaterialTheme.typography.titleMedium)
            FilledTonalButton(
                onClick = {
                    intermediateLocations = intermediateLocations + Location()
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Location")
                Text("Add Location")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            itemsIndexed(intermediateLocations) { index, location ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
                        if(selectedImageUri != null) Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio((16/9.0).toFloat())
                        )
                        var locationname by rememberSaveable  { mutableStateOf<String>("") }
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult()
                        ) { result ->
                            if (result.resultCode == RESULT_OK) {
                                val data = result.data?.getParcelableExtra<LatLng>("resultKey")
                                if(data is LatLng) {
                                    intermediateLocations = intermediateLocations.toMutableList().apply {
                                        this[index] = location.copy(coordinates = Point(coordinates = listOf(data.latitude, data.longitude)))
                                    }
                                }
                            }
                        }
                        LaunchedEffect(location) {
                            CoroutineScope(Dispatchers.Default).launch {
                                if(location.coordinates is Point) {
                                    // locationname = locationService.getCountryString(location.coordinates.coordinates[0], location.coordinates.coordinates[1]) ?: "" TODO:
                                }
                            }
                        }
                        OutlinedTextField(
                            value = locationname,
                            readOnly = true,
                            onValueChange = {},
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    val intent = Intent(context, LocationPicker::class.java)
                                    launcher.launch(intent)
                                }) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = "Get Coordinates")
                                }
                            }
                        )

                        // Image Capture
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                                if (uri is Uri) {
                                    selectedImageUri = uri
                                    uploadImage(
                                        context = context,
                                        uri = uri,
                                        onStateChange = {
                                                state -> uploadState = state
                                        },
                                        onComplete = { link ->
                                            intermediateLocations = intermediateLocations.toMutableList().apply {
                                                this[index] = location.copy(href = link)
                                            }
                                            onSelected(intermediateLocations, caption)
                                        },
                                        onError = {

                                        }
                                    )

                                }
                            }
                            //
                            Text("Location Photo")
                            FilledTonalButton(
                                onClick = {
                                    getContent.launch("image/*")
                                }
                            ) {
                                Icon(Icons.Filled.PhotoCamera, contentDescription = "Take Photo")
                                Text("Pick Photo")
                            }
                        }

                        TextButton(
                            onClick = {
                                intermediateLocations = intermediateLocations.filterIndexed { i, _ -> i != index }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove Location")
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}

private fun uploadImage(
    context: Context,
    uri: Uri,
    onStateChange: (UploadState) -> Unit,
    onComplete: (String) -> Unit,
    onError: (String) -> Unit
) {
    onStateChange(UploadState(isUploading = true))

    // Create upload callback
    val callback = object : UploadCallback {
        override fun onStart(requestId: String) {
            onStateChange(UploadState(isUploading = true))
        }

        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
            val progress = bytes.toFloat() / totalBytes.toFloat()
            onStateChange(UploadState(isUploading = true, progress = progress))
        }

        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
            val uploadedUrl = resultData["url"] as? String
            uploadedUrl?.let {
                onStateChange(UploadState(uploadedUrl = it))
                onComplete(it)
            }
        }

        override fun onError(requestId: String, error: ErrorInfo) {
            onStateChange(UploadState(error = error.description))
            onError(error.description)
        }

        override fun onReschedule(requestId: String, error: ErrorInfo) {
            onStateChange(UploadState(error = "Upload rescheduled: ${error.description}"))
        }
    }

    // Start upload
    try {
        MediaManager.get().upload(uri).callback(callback).dispatch()
    } catch (e: Exception) {
        onStateChange(UploadState(error = e.message ?: "Unknown error occurred"))
        onError(e.message ?: "Unknown error occurred")
    }
}