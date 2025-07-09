//package com.example.detect
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import android.util.Log
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun StorageScreen(
//    onBackPressed: () -> Unit,
//    isDarkMode: Boolean
//) {
//    println("🔥 STORAGE SCREEN")
//    Log.d("StorageScreen", "🔥 STORAGE SCREEN")
//
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val storage = remember { SimpleStorage(context) }
//    var savedItems by remember { mutableStateOf<List<SavedItem>>(emptyList()) }
//    var selectedFilter by remember { mutableStateOf<String?>(null) }
//    var showDeleteDialog by remember { mutableStateOf<SavedItem?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//    var showMessage by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(selectedFilter) {
//        println("🔥 LOADING ITEMS - Filter: $selectedFilter")
//        Log.d("StorageScreen", "🔥 LOADING ITEMS - Filter: $selectedFilter")
//        savedItems = storage.getSavedItems(selectedFilter)
//        isLoading = false
//    }
//
//    val backgroundColor = if (isDarkMode) {
//        Brush.verticalGradient(colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))
//    } else {
//        Brush.verticalGradient(colors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF)))
//    }
//
//    val cardColor = if (isDarkMode) Color(0xFF2D2D44) else Color.White
//    val textColor = if (isDarkMode) Color.White else Color.Black
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(backgroundColor)
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Top Bar
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = CardDefaults.cardColors(containerColor = cardColor)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(onClick = onBackPressed) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = textColor
//                        )
//                    }
//
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = "Saved Items",
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold,
//                            color = textColor
//                        )
//                        Text(
//                            text = "${savedItems.size} items saved",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//
////                    IconButton(
////                        onClick = {
////                            scope.launch {
////                                savedItems = storage.getSavedItems(selectedFilter)
////                            }
////                        }
////                    ) {
////                        Icon(
////                            imageVector = Icons.Default.Refresh,
////                            contentDescription = "Refresh",
////                            tint = textColor
////                        )
////                    }
//                }
//            }
//
//            // Filter Buttons
////            Row(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(horizontal = 16.dp),
////                horizontalArrangement = Arrangement.spacedBy(8.dp)
////            ) {
////                FilterChip(
////                    selected = selectedFilter == null,
////                    onClick = {
////                        selectedFilter = null
////                        scope.launch { savedItems = storage.getSavedItems(null) }
////                    },
////                    label = { Text("All") }
////                )
////
////                FilterChip(
////                    selected = selectedFilter == "face",
////                    onClick = {
////                        selectedFilter = "face"
////                        scope.launch { savedItems = storage.getSavedItems("face") }
////                    },
////                    label = { Text("Faces") }
////                )
////
////                FilterChip(
////                    selected = selectedFilter == "object",
////                    onClick = {
////                        selectedFilter = "object"
////                        scope.launch { savedItems = storage.getSavedItems("object") }
////                    },
////                    label = { Text("Objects") }
////                )
////            }
//
//            //Spacer(modifier = Modifier.height(16.dp))
//
//            // Content
//            when {
//                isLoading -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//
//                savedItems.isEmpty() -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Card(
//                            modifier = Modifier.padding(32.dp),
//                            colors = CardDefaults.cardColors(containerColor = cardColor)
//                        ) {
//                            Column(
//                                modifier = Modifier.padding(32.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Star,
//                                    contentDescription = "No items",
//                                    modifier = Modifier.size(72.dp),
//                                    tint = MaterialTheme.colorScheme.primary
//                                )
//                                Spacer(modifier = Modifier.height(16.dp))
//                                Text(
//                                    text = "No saved items yet",
//                                    style = MaterialTheme.typography.titleLarge,
//                                    fontWeight = FontWeight.Bold,
//                                    color = textColor
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    text = "Use detection modes to save items",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    color = textColor.copy(alpha = 0.7f)
//                                )
//                            }
//                        }
//                    }
//                }
//
//                else -> {
//                    LazyColumn(
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        items(savedItems) { item ->
//                            SavedItemCard(
//                                item = item,
//                                storage = storage,
//                                onDelete = { showDeleteDialog = item },
//                                onDownload = { success ->
//                                    showMessage = if (success) {
//                                        "✅ Downloaded ${item.name} to Downloads folder"
//                                    } else {
//                                        "❌ Failed to download ${item.name}"
//                                    }
//                                },
//                                cardColor = cardColor,
//                                textColor = textColor
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        // Success/Error Message
//        showMessage?.let { message ->
//            Card(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = if (message.contains("✅")) Color.Green else Color.Red
//                )
//            ) {
//                Text(
//                    text = message,
//                    modifier = Modifier.padding(16.dp),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            LaunchedEffect(message) {
//                kotlinx.coroutines.delay(3000)
//                showMessage = null
//            }
//        }
//    }
//
//    // Delete Dialog
//    showDeleteDialog?.let { itemToDelete ->
//        AlertDialog(
//            onDismissRequest = { showDeleteDialog = null },
//            title = {
//                Text(
//                    "🗑️ Delete Item",
//                    color = textColor
//                )
//            },
//            text = {
//                Text(
//                    "Are you sure you want to delete ${itemToDelete.name}?",
//                    color = textColor
//                )
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        scope.launch {
//                            storage.deleteItem(itemToDelete.id)
//                            savedItems = storage.getSavedItems(selectedFilter)
//                            showDeleteDialog = null
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.error
//                    )
//                ) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDeleteDialog = null }) {
//                    Text("Cancel")
//                }
//            },
//            containerColor = cardColor
//        )
//    }
//}
//
//@Composable
//fun SavedItemCard(
//    item: SavedItem,
//    storage: SimpleStorage,
//    onDelete: () -> Unit,
//    onDownload: (Boolean) -> Unit,
//    cardColor: Color,
//    textColor: Color
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = cardColor)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Image
//            val bitmap = remember(item.imagePath) {
//                storage.loadBitmap(item.imagePath)
//            }
//
//            Box(modifier = Modifier.size(60.dp)) {
//                if (bitmap != null) {
//                    Image(
//                        bitmap = bitmap.asImageBitmap(),
//                        contentDescription = item.name,
//                        modifier = Modifier
//                            .size(60.dp)
//                            .clip(if (item.type == "face") CircleShape else RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
//                    Card(
//                        modifier = Modifier.size(60.dp),
//                        shape = if (item.type == "face") CircleShape else RoundedCornerShape(8.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.primaryContainer
//                        )
//                    ) {
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = if (item.type == "face") Icons.Default.Person else Icons.Default.Info,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            // Info
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = item.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = textColor
//                )
//
//                val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
//                Text(
//                    text = "📅 ${dateFormat.format(Date(item.timestamp))}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = textColor.copy(alpha = 0.7f)
//                )
//
//                Text(
//                    text = if (item.type == "face") "🧠 Face" else "🍎 Object",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//
//            // Action buttons
//            Row {
//                // Download button
//                IconButton(
//                    onClick = {
//                        scope.launch {
//                            println("🔥 DOWNLOADING: ${item.name}")
//                            val success = storage.downloadItem(item)
//                            onDownload(success)
//                            if (success) {
//                                println("✅ Downloaded: ${item.name}")
//                            } else {
//                                println("❌ Download failed: ${item.name}")
//                            }
//                        }
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = "Download",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                // Share button
//                IconButton(
//                    onClick = {
//                        println("🔥 SHARING: ${item.name}")
//                        val shareIntent = storage.shareItem(item)
//                        if (shareIntent != null) {
//                            context.startActivity(shareIntent)
//                            println("✅ Share intent launched for: ${item.name}")
//                        } else {
//                            println("❌ Share failed for: ${item.name}")
//                        }
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = "Share",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                // Delete button
//                IconButton(onClick = onDelete) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Delete",
//                        tint = MaterialTheme.colorScheme.error
//                    )
//                }
//            }
//        }
//    }
//}
package com.example.detect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StorageScreen(
    onBackPressed: () -> Unit,
    isDarkMode: Boolean
) {
    println("🔥 STORAGE SCREEN")
    Log.d("StorageScreen", "🔥 STORAGE SCREEN")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storage = remember { SimpleStorage(context) }
    var savedItems by remember { mutableStateOf<List<SavedItem>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<SavedItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedFilter) {
        println("🔥 LOADING ITEMS - Filter: $selectedFilter")
        Log.d("StorageScreen", "🔥 LOADING ITEMS - Filter: $selectedFilter")
        savedItems = storage.getSavedItems(selectedFilter)
        isLoading = false
    }

    val backgroundColor = if (isDarkMode) {
        Brush.verticalGradient(colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))
    } else {
        Brush.verticalGradient(colors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF)))
    }

    val cardColor = if (isDarkMode) Color(0xFF2D2D44) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cyanColor = Color(0xFF00E5FF) // Cyan color for blue text

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar - Half centimeter down (12dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 16.dp), // Moved down by 12dp
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Saved Images", // Changed from "Saved Items" to "Saved Images"
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "${savedItems.size} images saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = cyanColor // Changed to cyan
                        )
                    }

//                    IconButton(
//                        onClick = {
//                            scope.launch {
//                                savedItems = storage.getSavedItems(selectedFilter)
//                            }
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Refresh,
//                            contentDescription = "Refresh",
//                            tint = textColor
//                        )
//                    }
                }
            }

            // Filter Buttons (ONLY Faces and All)


           // Spacer(modifier = Modifier.height(16.dp))

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                savedItems.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(32.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "No faces",
                                    modifier = Modifier.size(72.dp),
                                    tint = cyanColor // Changed to cyan
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No saved faces yet",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Use Face Detection to save faces",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedItems) { item ->
                            SavedItemCard(
                                item = item,
                                storage = storage,
                                onDelete = { showDeleteDialog = item },
                                onDownload = { success ->
                                    showMessage = if (success) {
                                        "✅ Downloaded ${item.name} to Downloads/AI_Detect folder"
                                    } else {
                                        "❌ Failed to download ${item.name}"
                                    }
                                },
                                cardColor = cardColor,
                                textColor = textColor,
                                cyanColor = cyanColor
                            )
                        }
                    }
                }
            }
        }

        // Success/Error Message
        showMessage?.let { message ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.contains("✅")) Color.Green else Color.Red
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                showMessage = null
            }
        }
    }

    // Delete Dialog
    showDeleteDialog?.let { itemToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text(
                    "🗑️ Delete Face",
                    color = textColor
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete ${itemToDelete.name}?",
                    color = textColor
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            storage.deleteItem(itemToDelete.id)
                            savedItems = storage.getSavedItems(selectedFilter)
                            showDeleteDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            },
            containerColor = cardColor
        )
    }
}

@Composable
fun SavedItemCard(
    item: SavedItem,
    storage: SimpleStorage,
    onDelete: () -> Unit,
    onDownload: (Boolean) -> Unit,
    cardColor: Color,
    textColor: Color,
    cyanColor: Color
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        // VERTICAL LAYOUT - Changed from Row to Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Image Section
            val bitmap = remember(item.imagePath) {
                storage.loadBitmap(item.imagePath)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Fixed height for vertical display
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = item.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)), // Rounded corners for image
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info Section
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date only (removed calendar icon and face text)
                val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                Text(
                    text = "${dateFormat.format(Date(item.timestamp))}", // Removed calendar emoji
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Download button
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                println("🔥 DOWNLOADING: ${item.name}")

                                val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AI_Detect")
                                if (!downloadsDir.exists()) {
                                    downloadsDir.mkdirs()
                                }

                                val sourceFile = File(item.imagePath)
                                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val fileName = "face_${item.name}_${timestamp}.jpg"
                                val destFile = File(downloadsDir, fileName)

                                sourceFile.copyTo(destFile, overwrite = true)

                                onDownload(true)
                                println("✅ Downloaded: ${destFile.absolutePath}")

                            } catch (e: Exception) {
                                println("❌ Download failed: ${e.message}")
                                onDownload(false)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = cyanColor)
                )
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = Color.White)
                }

                // Share button
                Button(
                    onClick = {
                        val shareIntent = storage.shareItem(item)
                        if (shareIntent != null) {
                            context.startActivity(shareIntent)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = cyanColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", color = Color.White)
                }

                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

