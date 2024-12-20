package com.example.xplore.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.xplore.UploadState
import com.example.xplore.ui.theme.AppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


class LocationPicker : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val initial = LatLng(36.829706818905336,10.162145644426346)
            var isVisible by remember { mutableStateOf(false) }

            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val markerState = rememberMarkerState(position = initial)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(initial, 10f)
                    }
                    Box(modifier = Modifier.padding(innerPadding)) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = {a ->
                                markerState.position = a
                                isVisible = true
                            }
                        ) {
                            if(isVisible) Marker(
                                    state = markerState,
                                    title = "Location",
                                    snippet = "the location i visited"
                            )
                        }
                        if(isVisible) FloatingActionButton(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                            content = {
                                Row {
                                    Icon(
                                        Icons.Filled.Timer,
                                        contentDescription = "ok button"
                                    )
                                }
                            },
                            onClick = {
                                val returnIntent = Intent()
                                returnIntent.putExtra("resultKey", markerState.position)
                                setResult(RESULT_OK, returnIntent)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}