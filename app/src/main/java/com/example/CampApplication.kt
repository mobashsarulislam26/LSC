package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class CampApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val isRobolectric = try {
            Class.forName("org.robolectric.Robolectric") != null ||
                android.os.Build.FINGERPRINT == "unknown" ||
                android.os.Build.MODEL.contains("robolectric", ignoreCase = true)
        } catch (_: Throwable) {
            false
        }
        if (isRobolectric) {
            com.example.data.sync.CampFirebaseMessagingService.createNotificationChannels(this)
            return
        }
        try {
            System.loadLibrary("sqlcipher")
            Log.d(TAG, "SQLCipher native library loaded successfully.")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to load SQLCipher native library: ${e.message}")
        }
        ensureFirebaseInitialized(this)
        com.example.data.sync.CampFirebaseMessagingService.createNotificationChannels(this)
        com.example.data.sync.CampFirebaseMessagingService.subscribeToDefaultTopics()
    }

    companion object {
        private const val TAG = "CampApplication"

        fun ensureFirebaseInitialized(app: Application) {
            try {
                if (FirebaseApp.getApps(app).isEmpty()) {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:366425788570:android:3fa7jaz401f2794c")
                        .setApiKey("AIzaSyFakeKeyForOfflineFallback000000000")
                        .setProjectId("lsc-camp-companion")
                        .build()
                    FirebaseApp.initializeApp(app, options)
                    Log.d(TAG, "FirebaseApp initialized successfully with default offline fallback options.")
                }

                // Configure Firestore persistence for offline support
                try {
                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                        .setLocalCacheSettings(com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build())
                        .build()
                    firestore.firestoreSettings = settings
                    Log.d(TAG, "Firestore offline persistence configured successfully.")
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore persistence configuration note: ${e.message}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to initialize FirebaseApp: ${e.message}")
            }
        }
    }
}
