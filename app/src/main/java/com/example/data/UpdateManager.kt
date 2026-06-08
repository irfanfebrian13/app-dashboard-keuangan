package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class UpdateInfo(
    val hasUpdate: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String
)

object UpdateManager {
    private const val TAG = "UpdateManager"
    private val client = OkHttpClient()

    // Default GitHub repository owner & repo - can be customized in settings
    private const val DEFAULT_OWNER = "irfanfebrian13"
    private const val DEFAULT_REPO = "app-dashboard-keuangan"

    fun getSavedRepoOwner(context: Context): String {
        val prefs = context.getSharedPreferences("HEMATKU_UPDATE_SETTINGS", Context.MODE_PRIVATE)
        return prefs.getString("github_owner", DEFAULT_OWNER) ?: DEFAULT_OWNER
    }

    fun getSavedRepoName(context: Context): String {
        val prefs = context.getSharedPreferences("HEMATKU_UPDATE_SETTINGS", Context.MODE_PRIVATE)
        return prefs.getString("github_repo", DEFAULT_REPO) ?: DEFAULT_REPO
    }

    fun saveRepoDetails(context: Context, owner: String, repo: String) {
        val prefs = context.getSharedPreferences("HEMATKU_UPDATE_SETTINGS", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("github_owner", owner.trim())
            .putString("github_repo", repo.trim())
            .apply()
    }

    fun getCurrentVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    // Function to check updates by hitting Github API
    suspend fun checkUpdate(context: Context): UpdateInfo = withContext(Dispatchers.IO) {
        val currentVersion = getCurrentVersionName(context)
        val owner = getSavedRepoOwner(context)
        val repo = getSavedRepoName(context)

        val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
        
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "HematKu-Update-Checker")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext UpdateInfo(
                        hasUpdate = false,
                        currentVersion = currentVersion,
                        latestVersion = currentVersion,
                        releaseNotes = "Gagal memindai (HTTP rilis belum tersedia atau repo privat / salah)",
                        downloadUrl = ""
                    )
                }

                val bodyString = response.body?.string() ?: return@withContext UpdateInfo(
                    hasUpdate = false,
                    currentVersion = currentVersion,
                    latestVersion = currentVersion,
                    releaseNotes = "Response kosong",
                    downloadUrl = ""
                )

                val json = JSONObject(bodyString)
                val rawTagName = json.optString("tag_name", "")
                // Standardize by removing 'v' prefix
                val latestVersionClean = rawTagName.removePrefix("v").trim()
                val currentVersionClean = currentVersion.removePrefix("v").trim()

                val htmlUrl = json.optString("html_url", "")
                val releaseNotes = json.optString("body", "Tidak ada catatan rilis.")

                // Try to see if there is an APK asset, otherwise fallback to the HTML release url
                val assetsArray = json.optJSONArray("assets")
                var downloadUrl = htmlUrl
                if (assetsArray != null && assetsArray.length() > 0) {
                    for (i in 0 until assetsArray.length()) {
                        val assetObj = assetsArray.getJSONObject(i)
                        val name = assetObj.optString("name", "")
                        if (name.endsWith(".apk")) {
                            downloadUrl = assetObj.optString("browser_download_url", htmlUrl)
                            break
                        }
                    }
                }

                // Simple version semantic check or direct comparison
                val hasUpdate = isNewerVersion(currentVersionClean, latestVersionClean)

                UpdateInfo(
                    hasUpdate = hasUpdate,
                    currentVersion = currentVersion,
                    latestVersion = rawTagName,
                    releaseNotes = releaseNotes,
                    downloadUrl = downloadUrl
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking update", e)
            UpdateInfo(
                hasUpdate = false,
                currentVersion = currentVersion,
                latestVersion = currentVersion,
                releaseNotes = "Error koneksi internet: ${e.localizedMessage}",
                downloadUrl = ""
            )
        }
    }

    // Semi-semantic version compare
    private fun isNewerVersion(current: String, latest: String): Boolean {
        if (current == latest) return false
        try {
            val currentParts = current.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val latestParts = latest.split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }

            val maxLength = maxOf(currentParts.size, latestParts.size)
            for (i in 0 until maxLength) {
                val currVal = currentParts.getOrElse(i) { 0 }
                val lateVal = latestParts.getOrElse(i) { 0 }
                if (lateVal > currVal) return true
                if (lateVal < currVal) return false
            }
        } catch (e: Exception) {
            // Fallback to basic string inequality
            return latest.compareTo(current, ignoreCase = true) > 0
        }
        return false
    }

    fun openDownloadLink(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening URL", e)
        }
    }

    fun downloadAndInstallApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Int) -> Unit = {},
        onError: (String) -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        Thread {
            try {
                val request = Request.Builder()
                    .url(downloadUrl)
                    .header("User-Agent", "HematKu-Updater")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        onError("Gagal mengunduh APK (HTTP ${response.code})")
                        return@use
                    }

                    val body = response.body ?: run {
                        onError("Response body kosong")
                        return@use
                    }

                    val apkDir = File(context.cacheDir, "apk_updates")
                    apkDir.mkdirs()
                    val apkFile = File(apkDir, "HematKu_update.apk")

                    val contentLength = body.contentLength()
                    var bytesRead = 0L

                    body.byteStream().use { input ->
                        FileOutputStream(apkFile).use { output ->
                            val buffer = ByteArray(8192)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                bytesRead += read
                                if (contentLength > 0) {
                                    val progress = ((bytesRead * 100) / contentLength).toInt()
                                    onProgress(progress)
                                }
                            }
                        }
                    }

                    val apkUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        apkFile
                    )

                    val installIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(apkUri, "application/vnd.android.package-archive")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(installIntent)
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading APK", e)
                onError("Gagal mengunduh: ${e.localizedMessage}")
            }
        }.start()
    }
}
