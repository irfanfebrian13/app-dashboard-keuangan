package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {
    private const val PREFS_NAME = "firebase_config_prefs"
    private const val KEY_API_KEY = "firebase_api_key"
    private const val KEY_PROJECT_ID = "firebase_project_id"
    private const val KEY_APP_ID = "firebase_app_id"

    val isInitialized = mutableStateOf(false)
    val initError = mutableStateOf<String?>(null)

    fun getSavedConfig(context: Context): Triple<String, String, String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val apiKey = prefs.getString(KEY_API_KEY, "") ?: ""
        val projectId = prefs.getString(KEY_PROJECT_ID, "") ?: ""
        val appId = prefs.getString(KEY_APP_ID, "") ?: ""
        return Triple(apiKey, projectId, appId)
    }

    fun saveConfig(context: Context, apiKey: String, projectId: String, appId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_API_KEY, apiKey.trim())
            .putString(KEY_PROJECT_ID, projectId.trim())
            .putString(KEY_APP_ID, appId.trim())
            .apply()
    }

    fun initialize(context: Context): Boolean {
        try {
            val existingApps = FirebaseApp.getApps(context)
            if (existingApps.isNotEmpty()) {
                isInitialized.value = true
                initError.value = null
                Log.d("FirebaseManager", "Firebase SDK already initialized automatically via google-services.json!")
                return true
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error checking existing apps", e)
        }

        val (apiKey, projectId, appId) = getSavedConfig(context)
        if (apiKey.isBlank() || projectId.isBlank() || appId.isBlank()) {
            // Try default auto-initialization from google-services.xml resources first
            return try {
                val app = FirebaseApp.initializeApp(context.applicationContext)
                if (app != null) {
                    isInitialized.value = true
                    initError.value = null
                    Log.d("FirebaseManager", "Firebase SDK initialized successfully via google-services.json auto-lookup!")
                    true
                } else {
                    initError.value = "Firebase kustom belum dikonfigurasi dan inisialisasi default null."
                    isInitialized.value = false
                    false
                }
            } catch (e: Exception) {
                initError.value = "Firebase kustom belum dikonfigurasi, dan auto-lookup gagal: ${e.localizedMessage}"
                isInitialized.value = false
                Log.w("FirebaseManager", "Auto-initialization via google-services.json failed", e)
                false
            }
        }

        return try {
            val options = FirebaseOptions.Builder()
                .setApiKey(apiKey)
                .setProjectId(projectId)
                .setApplicationId(appId)
                .build()
            FirebaseApp.initializeApp(context.applicationContext, options)
            isInitialized.value = true
            initError.value = null
            Log.d("FirebaseManager", "Firebase SDK initialized successfully programmatically!")
            true
        } catch (e: Exception) {
            initError.value = "Gagal inisialisasi: ${e.localizedMessage}"
            isInitialized.value = false
            Log.e("FirebaseManager", "Inisialisasi Firebase gagal: ${e.message}", e)
            false
        }
    }

    fun forceReinitialize(context: Context, apiKey: String, projectId: String, appId: String): Boolean {
        saveConfig(context, apiKey, projectId, appId)
        try {
            val existingApps = FirebaseApp.getApps(context)
            for (app in existingApps) {
                // Remove existing FirebaseApps to allow fresh registration
                app.delete()
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error deleting old apps", e)
        }
        isInitialized.value = false
        initError.value = null
        return initialize(context)
    }

    fun getAuth(): FirebaseAuth? {
        if (!isInitialized.value) return null
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun getFirestore(): FirebaseFirestore? {
        if (!isInitialized.value) return null
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }
}
