//package com.example.detect
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.util.Log
//import java.io.File
//import java.io.FileOutputStream
//import java.util.*
//
//data class SavedItem(
//    val id: String,
//    val name: String,
//    val imagePath: String,
//    val timestamp: Long,
//    val type: String // "face" or "object"
//)
//
//class SimpleStorage(private val context: Context) {
//
//    private val imagesDir = File(context.filesDir, "saved_images")
//    private val prefs = context.getSharedPreferences("saved_items", Context.MODE_PRIVATE)
//
//    init {
//        println("🔥 SIMPLE STORAGE INITIALIZED")
//        Log.d("SimpleStorage", "🔥 SIMPLE STORAGE INITIALIZED")
//
//        try {
//            if (!imagesDir.exists()) {
//                val created = imagesDir.mkdirs()
//                println("📁 Images directory created: $created")
//                Log.d("SimpleStorage", "📁 Images directory created: $created")
//            }
//
//            println("✅ Storage ready at: ${imagesDir.absolutePath}")
//            Log.d("SimpleStorage", "✅ Storage ready")
//
//        } catch (e: Exception) {
//            println("❌ Storage init error: ${e.message}")
//            Log.e("SimpleStorage", "❌ Storage init error", e)
//        }
//    }
//
//    fun saveItem(bitmap: Bitmap, name: String, type: String): String? {
//        println("🔥 SAVE ITEM CALLED - Name: $name, Type: $type")
//        Log.d("SimpleStorage", "🔥 SAVE ITEM - Name: $name, Type: $type")
//
//        return try {
//            val itemId = UUID.randomUUID().toString()
//            val fileName = "${itemId}.jpg"
//            val file = File(imagesDir, fileName)
//
//            println("💾 Saving to: ${file.absolutePath}")
//            Log.d("SimpleStorage", "💾 Saving to: ${file.absolutePath}")
//
//            // Save image
//            FileOutputStream(file).use { out ->
//                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//                println("📷 Image compression success: $success")
//                Log.d("SimpleStorage", "📷 Image compression success: $success")
//            }
//
//            // Verify file saved
//            if (file.exists() && file.length() > 0) {
//                println("✅ Image saved: ${file.length()} bytes")
//                Log.d("SimpleStorage", "✅ Image saved: ${file.length()} bytes")
//
//                // Save metadata in SharedPreferences
//                val editor = prefs.edit()
//                editor.putString("${itemId}_name", name)
//                editor.putString("${itemId}_path", file.absolutePath)
//                editor.putString("${itemId}_type", type)
//                editor.putLong("${itemId}_timestamp", System.currentTimeMillis())
//                val saved = editor.commit()
//
//                println("💾 Metadata saved: $saved")
//                Log.d("SimpleStorage", "💾 Metadata saved: $saved")
//
//                if (saved) {
//                    println("🎉 Item saved successfully: $itemId")
//                    Log.d("SimpleStorage", "🎉 Item saved successfully: $itemId")
//                    return itemId
//                }
//            }
//
//            println("❌ Save failed")
//            Log.e("SimpleStorage", "❌ Save failed")
//            return null
//
//        } catch (e: Exception) {
//            println("❌ Save error: ${e.message}")
//            Log.e("SimpleStorage", "❌ Save error", e)
//            return null
//        }
//    }
//
//    fun getSavedItems(type: String? = null): List<SavedItem> {
//        println("🔥 GET SAVED ITEMS - Type: $type")
//        Log.d("SimpleStorage", "🔥 GET SAVED ITEMS - Type: $type")
//
//        return try {
//            val items = mutableListOf<SavedItem>()
//            val allPrefs = prefs.all
//
//            // Group by item ID
//            val itemIds = allPrefs.keys
//                .filter { it.endsWith("_name") }
//                .map { it.removeSuffix("_name") }
//
//            println("📊 Found ${itemIds.size} items in storage")
//            Log.d("SimpleStorage", "📊 Found ${itemIds.size} items")
//
//            itemIds.forEach { itemId ->
//                try {
//                    val name = prefs.getString("${itemId}_name", "") ?: ""
//                    val path = prefs.getString("${itemId}_path", "") ?: ""
//                    val itemType = prefs.getString("${itemId}_type", "") ?: ""
//                    val timestamp = prefs.getLong("${itemId}_timestamp", 0)
//
//                    // Filter by type if specified
//                    if (type == null || itemType == type) {
//                        // Verify file exists
//                        val file = File(path)
//                        if (file.exists()) {
//                            items.add(
//                                SavedItem(
//                                    id = itemId,
//                                    name = name,
//                                    imagePath = path,
//                                    timestamp = timestamp,
//                                    type = itemType
//                                )
//                            )
//                            println("✅ Loaded item: $name ($itemType)")
//                            Log.d("SimpleStorage", "✅ Loaded item: $name")
//                        } else {
//                            println("⚠️ File missing for: $name")
//                            Log.w("SimpleStorage", "⚠️ File missing for: $name")
//                        }
//                    }
//                } catch (e: Exception) {
//                    println("❌ Error loading item $itemId: ${e.message}")
//                    Log.e("SimpleStorage", "❌ Error loading item $itemId", e)
//                }
//            }
//
//            val sortedItems = items.sortedByDescending { it.timestamp }
//            println("📊 Returning ${sortedItems.size} items")
//            Log.d("SimpleStorage", "📊 Returning ${sortedItems.size} items")
//
//            return sortedItems
//
//        } catch (e: Exception) {
//            println("❌ Get items error: ${e.message}")
//            Log.e("SimpleStorage", "❌ Get items error", e)
//            return emptyList()
//        }
//    }
//
//    fun deleteItem(itemId: String) {
//        println("🔥 DELETE ITEM: $itemId")
//        Log.d("SimpleStorage", "🔥 DELETE ITEM: $itemId")
//
//        try {
//            // Delete image file
//            val path = prefs.getString("${itemId}_path", "")
//            if (!path.isNullOrEmpty()) {
//                val file = File(path)
//                if (file.exists()) {
//                    val deleted = file.delete()
//                    println("🗑️ Image deleted: $deleted")
//                    Log.d("SimpleStorage", "🗑️ Image deleted: $deleted")
//                }
//            }
//
//            // Delete metadata
//            val editor = prefs.edit()
//            editor.remove("${itemId}_name")
//            editor.remove("${itemId}_path")
//            editor.remove("${itemId}_type")
//            editor.remove("${itemId}_timestamp")
//            val removed = editor.commit()
//
//            println("✅ Item deleted: $removed")
//            Log.d("SimpleStorage", "✅ Item deleted: $removed")
//
//        } catch (e: Exception) {
//            println("❌ Delete error: ${e.message}")
//            Log.e("SimpleStorage", "❌ Delete error", e)
//        }
//    }
//
//    fun debugStorage() {
//        println("🔥 DEBUG STORAGE")
//        Log.d("SimpleStorage", "🔥 DEBUG STORAGE")
//
//        println("📁 Images dir exists: ${imagesDir.exists()}")
//        println("📁 Images dir writable: ${imagesDir.canWrite()}")
//        println("📂 Files count: ${imagesDir.listFiles()?.size ?: 0}")
//
//        val allPrefs = prefs.all
//        println("💾 SharedPrefs entries: ${allPrefs.size}")
//
//        Log.d("SimpleStorage", "📁 Dir exists: ${imagesDir.exists()}")
//        Log.d("SimpleStorage", "📂 Files: ${imagesDir.listFiles()?.size ?: 0}")
//        Log.d("SimpleStorage", "💾 Prefs: ${allPrefs.size}")
//    }
//
//    fun loadBitmap(imagePath: String): Bitmap? {
//        return try {
//            val file = File(imagePath)
//            if (file.exists()) {
//                BitmapFactory.decodeFile(imagePath)
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            println("❌ Bitmap load error: ${e.message}")
//            Log.e("SimpleStorage", "❌ Bitmap load error", e)
//            null
//        }
//    }
//}
package com.example.detect

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class SavedItem(
    val id: String,
    val name: String,
    val imagePath: String,
    val timestamp: Long,
    val type: String
)

class SimpleStorage(private val context: Context) {

    private val imagesDir = File(context.filesDir, "saved_images")
    private val prefs = context.getSharedPreferences("saved_items", Context.MODE_PRIVATE)

    init {
        println("🔥 SIMPLE STORAGE INITIALIZED")
        Log.d("SimpleStorage", "🔥 SIMPLE STORAGE INITIALIZED")

        try {
            if (!imagesDir.exists()) {
                val created = imagesDir.mkdirs()
                println("📁 Images directory created: $created")
                Log.d("SimpleStorage", "📁 Images directory created: $created")
            }
            println("✅ Storage ready at: ${imagesDir.absolutePath}")
            Log.d("SimpleStorage", "✅ Storage ready")
        } catch (e: Exception) {
            println("❌ Storage init error: ${e.message}")
            Log.e("SimpleStorage", "❌ Storage init error", e)
        }
    }

    fun saveItem(bitmap: Bitmap, name: String, type: String): String? {
        println("🔥 SAVE ITEM CALLED - Name: $name, Type: $type")
        Log.d("SimpleStorage", "🔥 SAVE ITEM - Name: $name, Type: $type")

        return try {
            val itemId = UUID.randomUUID().toString()
            val fileName = "${itemId}.jpg"
            val file = File(imagesDir, fileName)

            println("💾 Saving to: ${file.absolutePath}")
            Log.d("SimpleStorage", "💾 Saving to: ${file.absolutePath}")

            FileOutputStream(file).use { out ->
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                println("📷 Image compression success: $success")
                Log.d("SimpleStorage", "📷 Image compression success: $success")
            }

            if (file.exists() && file.length() > 0) {
                println("✅ Image saved: ${file.length()} bytes")
                Log.d("SimpleStorage", "✅ Image saved: ${file.length()} bytes")

                val editor = prefs.edit()
                editor.putString("${itemId}_name", name)
                editor.putString("${itemId}_path", file.absolutePath)
                editor.putString("${itemId}_type", type)
                editor.putLong("${itemId}_timestamp", System.currentTimeMillis())
                val saved = editor.commit()

                println("💾 Metadata saved: $saved")
                Log.d("SimpleStorage", "💾 Metadata saved: $saved")

                if (saved) {
                    println("🎉 Item saved successfully: $itemId")
                    Log.d("SimpleStorage", "🎉 Item saved successfully: $itemId")
                    return itemId
                }
            }

            println("❌ Save failed")
            Log.e("SimpleStorage", "❌ Save failed")
            return null

        } catch (e: Exception) {
            println("❌ Save error: ${e.message}")
            Log.e("SimpleStorage", "❌ Save error", e)
            return null
        }
    }

    fun getSavedItems(type: String? = null): List<SavedItem> {
        println("🔥 GET SAVED ITEMS - Type: $type")
        Log.d("SimpleStorage", "🔥 GET SAVED ITEMS - Type: $type")

        return try {
            val items = mutableListOf<SavedItem>()
            val allPrefs = prefs.all

            val itemIds = allPrefs.keys
                .filter { it.endsWith("_name") }
                .map { it.removeSuffix("_name") }

            println("📊 Found ${itemIds.size} items in storage")
            Log.d("SimpleStorage", "📊 Found ${itemIds.size} items")

            itemIds.forEach { itemId ->
                try {
                    val name = prefs.getString("${itemId}_name", "") ?: ""
                    val path = prefs.getString("${itemId}_path", "") ?: ""
                    val itemType = prefs.getString("${itemId}_type", "") ?: ""
                    val timestamp = prefs.getLong("${itemId}_timestamp", 0)

                    if (type == null || itemType == type) {
                        val file = File(path)
                        if (file.exists()) {
                            items.add(
                                SavedItem(
                                    id = itemId,
                                    name = name,
                                    imagePath = path,
                                    timestamp = timestamp,
                                    type = itemType
                                )
                            )
                            println("✅ Loaded item: $name ($itemType)")
                            Log.d("SimpleStorage", "✅ Loaded item: $name")
                        }
                    }
                } catch (e: Exception) {
                    println("❌ Error loading item $itemId: ${e.message}")
                    Log.e("SimpleStorage", "❌ Error loading item $itemId", e)
                }
            }

            val sortedItems = items.sortedByDescending { it.timestamp }
            println("📊 Returning ${sortedItems.size} items")
            Log.d("SimpleStorage", "📊 Returning ${sortedItems.size} items")

            return sortedItems

        } catch (e: Exception) {
            println("❌ Get items error: ${e.message}")
            Log.e("SimpleStorage", "❌ Get items error", e)
            return emptyList()
        }
    }

    fun deleteItem(itemId: String) {
        println("🔥 DELETE ITEM: $itemId")
        Log.d("SimpleStorage", "🔥 DELETE ITEM: $itemId")

        try {
            val path = prefs.getString("${itemId}_path", "")
            if (!path.isNullOrEmpty()) {
                val file = File(path)
                if (file.exists()) {
                    val deleted = file.delete()
                    println("🗑️ Image deleted: $deleted")
                    Log.d("SimpleStorage", "🗑️ Image deleted: $deleted")
                }
            }

            val editor = prefs.edit()
            editor.remove("${itemId}_name")
            editor.remove("${itemId}_path")
            editor.remove("${itemId}_type")
            editor.remove("${itemId}_timestamp")
            val removed = editor.commit()

            println("✅ Item deleted: $removed")
            Log.d("SimpleStorage", "✅ Item deleted: $removed")

        } catch (e: Exception) {
            println("❌ Delete error: ${e.message}")
            Log.e("SimpleStorage", "❌ Delete error", e)
        }
    }

    // NEW: Download feature
    fun downloadItem(item: SavedItem): Boolean {
        return try {
            println("🔥 DOWNLOADING ITEM: ${item.name}")
            Log.d("SimpleStorage", "🔥 DOWNLOADING ITEM: ${item.name}")

            val sourceFile = File(item.imagePath)
            if (!sourceFile.exists()) {
                println("❌ Source file not found")
                return false
            }

            val bitmap = BitmapFactory.decodeFile(item.imagePath)
            if (bitmap == null) {
                println("❌ Failed to decode bitmap")
                return false
            }

            // Save to Downloads folder
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${item.type}_${item.name}_${timestamp}.jpg"
            val downloadFile = File(downloadsDir, fileName)

            FileOutputStream(downloadFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            // Add to MediaStore so it appears in gallery
            val values = android.content.ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            println("✅ Downloaded to: ${downloadFile.absolutePath}")
            Log.d("SimpleStorage", "✅ Downloaded to: ${downloadFile.absolutePath}")

            return true

        } catch (e: Exception) {
            println("❌ Download error: ${e.message}")
            Log.e("SimpleStorage", "❌ Download error", e)
            return false
        }
    }

    // NEW: Share feature
    fun shareItem(item: SavedItem): Intent? {
        return try {
            val file = File(item.imagePath)
            if (!file.exists()) return null

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            Intent.createChooser(shareIntent, "Share ${item.name}")

        } catch (e: Exception) {
            println("❌ Share error: ${e.message}")
            Log.e("SimpleStorage", "❌ Share error", e)
            null
        }
    }

    fun loadBitmap(imagePath: String): Bitmap? {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(imagePath)
            } else {
                null
            }
        } catch (e: Exception) {
            println("❌ Bitmap load error: ${e.message}")
            Log.e("SimpleStorage", "❌ Bitmap load error", e)
            null
        }
    }
}

