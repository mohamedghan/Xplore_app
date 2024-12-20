package com.example.xplore.ui.addjourney

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.xplore.UploadState
import com.example.xplore.data.JourneyRequest
import com.example.xplore.data.Location
import com.example.xplore.data.LocationService
import com.example.xplore.data.MyFeedRepository
import com.example.xplore.data.Point
import com.example.xplore.ui.LocationPicker
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val myFeedRepository: MyFeedRepository,
) : ViewModel() {


    fun addJourney(
        caption: String,
        fromLocation: Pair<Double, Double>?,
        toLocation: Pair<Double, Double>?,
        intermediateLocations: List<Location>,
        figure: String?,
    ) {
        viewModelScope.launch {
            if(fromLocation is Pair && toLocation is Pair && figure is String) {
                myFeedRepository.addJourney(JourneyRequest(
                    from = Point(
                        coordinates = listOf(fromLocation.first, fromLocation.second),
                    ),
                    to = Point(
                        coordinates = listOf(toLocation.first, toLocation.second),
                    ),
                    caption = caption,
                    figure = figure,
                    locations = intermediateLocations,
                ))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJourneyScreenOld(onAction: (actions: AddJourneyScreenActions) -> Unit, locationService: LocationService, journeyViewModel: JourneyViewModel =  hiltViewModel()) {

    val context = LocalContext.current
    var caption by rememberSaveable { mutableStateOf("") }
    var fromLocation by rememberSaveable { mutableStateOf<Pair<Double, Double>?>(null) }
    var toLocation by rememberSaveable { mutableStateOf<Pair<Double, Double>?>(null) }
    var intermediateLocations by remember { mutableStateOf(listOf<Location>()) }
    var figure by remember { mutableStateOf<String?>(null) }
    var uploadState by remember { mutableStateOf(UploadState()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Journey") },
                navigationIcon = {
                    IconButton(onClick = { onAction(AddJourneyScreenActions.Back) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(enabled = !uploadState.isUploading,onClick = {
                        journeyViewModel.addJourney(caption, fromLocation, toLocation, intermediateLocations, figure)
                        onAction(AddJourneyScreenActions.Back)
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uploadState.isUploading) {
                LinearProgressIndicator(
                    progress = { uploadState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .zIndex(1f),
                   // color = MaterialTheme.colors.primary,
                    //trackColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Caption Input
                item {
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text("Journey Caption") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Describe your amazing trip!") },
                        maxLines = 3
                    )
                }
                // From Location Picker
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var locationname by rememberSaveable { mutableStateOf<String>("") }
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult()
                        ) { result ->
                            if (result.resultCode == RESULT_OK) {
                                val data = result.data?.getParcelableExtra<LatLng>("resultKey")
                                if(data is LatLng) {
                                    fromLocation = data.latitude to data.longitude
                                }
                            }
                        }
                        LaunchedEffect(fromLocation) {
                            CoroutineScope(Dispatchers.Default).launch {
                                if(fromLocation is Pair)
                                    locationname = locationService.getCountryString(fromLocation!!.first , fromLocation!!.second) ?: ""
                            }
                        }
                        OutlinedTextField(
                            value = locationname,
                            readOnly = true,
                            onValueChange = {},
                            label = { Text("From") },
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = {
                                    val intent = Intent(context, LocationPicker::class.java)
                                    launcher.launch(intent)
                                }) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = "Pick Location")
                                }
                            }
                        )
                    }
                }

                // To Location Picker
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var locationname by rememberSaveable { mutableStateOf<String>("") }
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult()
                        ) { result ->
                            if (result.resultCode == RESULT_OK) {
                                val data = result.data?.getParcelableExtra<LatLng>("resultKey")
                                if(data is LatLng) {
                                    toLocation = data.latitude to data.longitude
                                }
                            }
                        }
                        LaunchedEffect(toLocation) {
                            CoroutineScope(Dispatchers.Default).launch {
                                if(toLocation is Pair)
                                    locationname = locationService.getCountryString(toLocation!!.first , toLocation!!.second) ?: ""
                            }
                        }
                        OutlinedTextField(
                            value = locationname,
                            readOnly = true,
                            onValueChange = {},
                            label = { Text("To") },
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                IconButton(onClick = {
                                    val intent = Intent(context, LocationPicker::class.java)
                                    launcher.launch(intent)
                                }) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = "Pick Location")
                                }
                            }
                        )
                    }
                }


                item(key=3) {
                    Column {
                        var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
                        val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                            val contentResolver: ContentResolver = context.contentResolver
                            if (uri is Uri) {
                                selectedImageUri = uri
                                uploadImage(
                                    context = context,
                                    uri = uri,
                                    onStateChange = {
                                        state -> uploadState = state
                                    },
                                    onComplete = { link ->
                                        figure = link
                                    },
                                    onError = {

                                    },
                                )

                            }
                        }
                        if (selectedImageUri != null) Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "",
                            modifier = Modifier.fillMaxWidth().aspectRatio(9f/16f),
                            contentScale = ContentScale.Crop
                        )
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
                }


                // Intermediate Locations Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Intermediate Locations", style = MaterialTheme.typography.titleMedium)
                        FilledTonalButton(
                            onClick = {
                                intermediateLocations = intermediateLocations + Location()
                            }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Location")
                            Text("Add Location")
                        }
                    }
                }

                // Intermediate Locations List
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
                                        Toast.makeText(context, "Received: $data", Toast.LENGTH_SHORT).show()
                                        intermediateLocations = intermediateLocations.toMutableList().apply {
                                            this[index] = location.copy(coordinates = Point(coordinates = listOf(data.latitude, data.longitude)))
                                        }
                                    }
                                }
                            }
                            LaunchedEffect(location) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    if(location.coordinates is Point) {
                                        locationname = locationService.getCountryString(location.coordinates.coordinates[0], location.coordinates.coordinates[1]) ?: ""
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = locationname,
                                readOnly = true,
                                onValueChange = {},
                                label = { Text("Coordinates") },
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
                                    val contentResolver: ContentResolver = context.contentResolver
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
                                            },
                                            onError = {

                                            }
                                        )

                                    }
                                }
                                //HERE
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

sealed class AddJourneyScreenActions {
    object Back : AddJourneyScreenActions()
    object AddJourney : AddJourneyScreenActions()
}