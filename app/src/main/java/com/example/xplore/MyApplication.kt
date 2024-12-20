package com.example.xplore

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configure Cloudinary
        val config = HashMap<String, String>()
        config["cloud_name"] = "REDACTED"
        config["api_key"] = "REDACTED"
        config["api_secret"] = "REDACTED"

        MediaManager.init(this, config)

    }
}
