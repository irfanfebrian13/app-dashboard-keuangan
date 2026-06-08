package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.FirebaseManager
import androidx.compose.foundation.BorderStroke

@Composable
fun SyncAndCategoryTab(
    viewModel: FinanceViewModel,
    categories: List<Category>
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Kategori, 1: Sinkronisasi Awan, 2: Info Update

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Header Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSubTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kategori",
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSubTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cloud Sync",
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSubTab == 2) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = 2 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cek Update",
                    fontWeight = FontWeight.Bold,
                    color = if (activeSubTab == 2) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        // Sub Tab Contents
        when (activeSubTab) {
            0 -> CategoryManagerView(viewModel = viewModel, categories = categories)
            1 -> CloudSyncView(viewModel = viewModel)
            2 -> AppUpdateSettingsView(viewModel = viewModel)
        }
    }
}

@Composable
fun CategoryManagerView(
    viewModel: FinanceViewModel,
    categories: List<Category>
) {
    val context = LocalContext.current
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryType by remember { mutableStateOf("PENGELUARAN") } // "PENGELUARAN" or "PEMASUKAN"
    
    // Available preset icons
    val iconsList = listOf("restaurant", "directions_car", "shopping_bag", "sports_esports", "home", "school", "payments", "redeem", "trending_up", "category")
    var selectedIcon by remember { mutableStateOf("category") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick insert category Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Buat Kategori Kustom Baru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Input Name
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nama Kategori") },
                    placeholder = { Text("Contoh: Hobi, Kesehatan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Segmented Type Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (newCategoryType == "PENGELUARAN") Color(0xFFEF4444) else Color.Transparent)
                            .clickable { newCategoryType = "PENGELUARAN" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pengeluaran",
                            fontWeight = FontWeight.Bold,
                            color = if (newCategoryType == "PENGELUARAN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (newCategoryType == "PEMASUKAN") Color(0xFF10B981) else Color.Transparent)
                            .clickable { newCategoryType = "PEMASUKAN" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pemasukan",
                            fontWeight = FontWeight.Bold,
                            color = if (newCategoryType == "PEMASUKAN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }

                // Choose Icon Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pilih Simbol / Ikon", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(iconsList) { iconName ->
                            val isChosen = iconName == selectedIcon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isChosen) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
                                    .clickable { selectedIcon = iconName },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when(iconName) {
                                        "restaurant" -> Icons.Default.Restaurant
                                        "directions_car" -> Icons.Default.DirectionsCar
                                        "shopping_bag" -> Icons.Default.ShoppingBag
                                        "sports_esports" -> Icons.Default.SportsEsports
                                        "home" -> Icons.Default.Home
                                        "school" -> Icons.Default.School
                                        "payments" -> Icons.Default.Payments
                                        "redeem" -> Icons.Default.Redeem
                                        "trending_up" -> Icons.Default.TrendingUp
                                        else -> Icons.Default.Category
                                    },
                                    contentDescription = iconName,
                                    tint = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Submit button
                Button(
                    onClick = {
                        if (newCategoryName.isBlank()) {
                            Toast.makeText(context, "Nama kategori tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addCategory(newCategoryName.trim(), newCategoryType, selectedIcon)
                            Toast.makeText(context, "Kategori '${newCategoryName}' ditambahkan!", Toast.LENGTH_SHORT).show()
                            newCategoryName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tambah Kategori")
                }
            }
        }

        // List categories scroll container
        Text(
            text = "Daftar Kategori Tersedia (${categories.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories, key = { it.id }) { cat ->
                val badgeColor = if (cat.type == "PEMASUKAN") Color(0xFF10B981) else Color(0xFFEF4444)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(badgeColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when(cat.icon) {
                                    "restaurant" -> Icons.Default.Restaurant
                                    "directions_car" -> Icons.Default.DirectionsCar
                                    "shopping_bag" -> Icons.Default.ShoppingBag
                                    "sports_esports" -> Icons.Default.SportsEsports
                                    "home" -> Icons.Default.Home
                                    "school" -> Icons.Default.School
                                    "payments" -> Icons.Default.Payments
                                    "redeem" -> Icons.Default.Redeem
                                    "trending_up" -> Icons.Default.TrendingUp
                                    else -> Icons.Default.Category
                                },
                                contentDescription = cat.name,
                                tint = badgeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(cat.type, fontSize = 9.sp, color = badgeColor, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Delete custom categories (Keep preset ones if you want, or let them delete any)
                    IconButton(
                        onClick = {
                            viewModel.deleteCategory(cat)
                            Toast.makeText(context, "Kategori '${cat.name}' telah dihapus", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Kategori",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloudSyncView(
    viewModel: FinanceViewModel
) {
    val context = LocalContext.current
    val isSyncing by viewModel.isSyncing
    val lastSync by viewModel.lastSyncTime
    val previewEnc by viewModel.cloudDataPreview

    // Firebase Auth States
    val userEmail by viewModel.firebaseUserEmail
    val userId by viewModel.firebaseUserId
    val isFirebaseInitialized by FirebaseManager.isInitialized

    var syncId by remember { mutableStateOf("") }
    var securityPassphrase by remember { mutableStateOf("") }
    
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }

    val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileContent = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                inputStream?.close()
                if (fileContent.isBlank()) {
                    Toast.makeText(context, "File kosong atau tidak terbaca!", Toast.LENGTH_SHORT).show()
                } else if (securityPassphrase.isBlank()) {
                    Toast.makeText(context, "Sandi Enkripsi AES-256 wajib diisi (Sandi yang dipakai saat membuat cadangan)!", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.importBackupFromFileContent(context, fileContent, securityPassphrase) { success, msg ->
                        if (success) {
                            Toast.makeText(context, "Impor Sukses! Seluruh data dipulihkan.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Impor Gagal: ${msg ?: "Sandi salah atau format rusak"}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka berkas: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Init values
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
        securityPassphrase = prefs.getString("security_passphrase", "") ?: ""
        syncId = prefs.getString("cloud_email", "") ?: ""
        viewModel.lastSyncTime.value = prefs.getString("last_sync_time", "-")
        viewModel.cloudDataPreview.value = prefs.getString("encrypted_backup", null)
    }

    // Auto-fill Sync ID based on logged in user's email for seamless convenience
    LaunchedEffect(userEmail) {
        if (userEmail != null && syncId.isBlank()) {
            syncId = userEmail!!
            val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("cloud_email", userEmail).apply()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Section 1: Firebase Auth Session Setup
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Autentikasi Cloud Pengguna",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Surface(
                            color = if (isFirebaseInitialized) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = if (isFirebaseInitialized) "Firebase Aktif ⚡" else "Belum Terhubung",
                                color = if (isFirebaseInitialized) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (userId != null) {
                        // User is securely logged in
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF10B981).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Masuk Terautentikasi:", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                Text(userEmail ?: "Email tidak diketahui", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("UID: ${userId?.take(8)}...", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = {
                                    viewModel.logoutFirebaseUser()
                                    Toast.makeText(context, "Sesi autentikasi ditutup", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Keluar", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    } else {
                        // Authentication Form
                        Text(
                            text = "Gunakan email dan kata sandi Anda untuk mendaftar akun baru atau masuk ke sesi sinkronisasi cloud Anda.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        OutlinedTextField(
                            value = authEmail,
                            onValueChange = { authEmail = it },
                            label = { Text("E-mail Sesi") },
                            placeholder = { Text("akun@gmail.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(18.dp)) }
                        )

                        OutlinedTextField(
                            value = authPassword,
                            onValueChange = { authPassword = it },
                            label = { Text("Kata Sandi") },
                            placeholder = { Text("Minimal 6 karakter") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", modifier = Modifier.size(18.dp)) }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (authEmail.isBlank() || authPassword.isBlank()) {
                                        Toast.makeText(context, "Isi email dan sandi!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.loginFirebaseUser(authEmail.trim(), authPassword, context) { success, err ->
                                            if (success) {
                                                Toast.makeText(context, "Masuk Sukses!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Gagal: $err", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Masuk")
                            }

                            Button(
                                onClick = {
                                    if (authEmail.isBlank() || authPassword.isBlank()) {
                                        Toast.makeText(context, "Isi email dan sandi!", Toast.LENGTH_SHORT).show()
                                    } else if (authPassword.length < 6) {
                                        Toast.makeText(context, "Sandi minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.registerFirebaseUser(authEmail.trim(), authPassword, context) { success, err ->
                                            if (success) {
                                                Toast.makeText(context, "Pendaftaran baru sukses!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Gagal daftar: $err", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Daftar Baru")
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Data Encryption & Direct Sync (Bypasses manual Firebase setup completely)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. Sinergi Enkripsi & Sinkronisasi Awan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Sync Identification Code
                    OutlinedTextField(
                        value = syncId,
                        onValueChange = {
                            syncId = it
                            val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
                            prefs.edit().putString("cloud_email", it).apply()
                        },
                        label = { Text("ID Sinkronisasi / Cloud Backup ID") },
                        placeholder = { Text("contoh: akun@gmail.com atau kode_unik") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Sync ID", modifier = Modifier.size(18.dp)) }
                    )

                    // Passphrase (E2E)
                    OutlinedTextField(
                        value = securityPassphrase,
                        onValueChange = {
                            securityPassphrase = it
                            val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
                            prefs.edit().putString("security_passphrase", it).apply()
                        },
                        label = { Text("Kunci Sandi Enkripsi AES-256") },
                        placeholder = { Text("Min 4 Karakter (Sangat Rahasia)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Security Token", modifier = Modifier.size(18.dp)) }
                    )

                    Text(
                        text = "🔐 Sebelum data diunggah ke server cloud HematKu, data dienkripsi terlebih dahulu di client menggunakan sandi AES-256 bits. Backups Anda dicadangkan langsung ke awan di bawah ID Sinkronisasi Anda secara privat tanpa pendaftaran akun manual (passwordless & direct). Pihak mana pun (bahkan server cloud) tidak dapat membaca data keuangan Anda tanpa kunci sandi di atas.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    if (isSyncing) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            Text("Sedang memproses enkripsi & komunikasi awan...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (syncId.isBlank()) {
                                        Toast.makeText(context, "ID Sinkronisasi wajib diisi!", Toast.LENGTH_SHORT).show()
                                    } else if (securityPassphrase.isBlank()) {
                                        Toast.makeText(context, "Sandi Enkripsi wajib diisi untuk mengamankan data!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.performCloudPull(context, syncId, securityPassphrase) { success, errMsg ->
                                            if (success) {
                                                Toast.makeText(context, "Dekripsi sukses! Database dipulihkan.", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, errMsg ?: "Sinkronisasi gagal", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Unduh", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ambil Data", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (syncId.isBlank()) {
                                        Toast.makeText(context, "ID Sinkronisasi wajib diisi!", Toast.LENGTH_SHORT).show()
                                    } else if (securityPassphrase.isBlank()) {
                                        Toast.makeText(context, "Sandi Enkripsi wajib diisi untuk mengamankan data!", Toast.LENGTH_SHORT).show()
                                    } else if (securityPassphrase.length < 4) {
                                        Toast.makeText(context, "Sandi minimal 4 karakter!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.performCloudPush(context, syncId, securityPassphrase) { success, errMsg ->
                                            if (success) {
                                                Toast.makeText(context, "Enkripsi sukses! Data dicadangkan ke awan HematKu.", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Gagal unggah data: ${errMsg ?: "koneksi bermasalah"}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = "Unggah", modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unggah Data", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Metode Sinkronisasi:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "HEMATKU SECURE CLOUD ✔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Terakhir Sinkronisasi:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = lastSync ?: "-",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Section 3: Extra Offline CSV Backup Tools
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Cadangan Offline (CSV / Excel)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = "Ekspor Lokal 📁",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Ambil dokumen laporan cadangan transaksi offline lengkap Anda dalam format file CSV (.csv). File ini dapat diimpor langsung ke aplikasi spreadsheet seperti Microsoft Excel, Google Sheets, atau aplikasi pencatat keuangan eksternal.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    Button(
                        onClick = {
                            viewModel.shareTransactionsAsCSV(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Simpan CSV", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unduh Catatan Transaksi (CSV)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 4: Local JSON Secure File Backup & Import Tools (Offline Migration)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "4. Migrasi / Ekspor Impor Berkas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = "Migrasi Offline 📲",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Gunakan fitur ini untuk mencadangkan seluruh data transaksi, tabungan, dan kategori Anda ke dalam folder '/Download' perangkat dalam bentuk file terenkripsi AES-256 (.json). Masukkan 'Kunci Sandi Enkripsi AES-256' di bagian (2) terlebih dahulu sebelum melakukan ekspor atau impor data.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (securityPassphrase.isBlank()) {
                                    Toast.makeText(context, "Silakan isi 'Kunci Sandi Enkripsi AES-256' di bagian (2) terlebih dahulu sebelum ekspor!", Toast.LENGTH_LONG).show()
                                } else if (securityPassphrase.length < 4) {
                                    Toast.makeText(context, "Sandi minimal 4 karakter!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.exportBackupToDownloads(context, securityPassphrase) { success, msg ->
                                        if (success) {
                                            Toast.makeText(context, msg ?: "Ekspor Selesai!", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Ekspor Gagal: $msg", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = "Ekspor Berkas", modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ekspor File", fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                if (securityPassphrase.isBlank()) {
                                    Toast.makeText(context, "Isi 'Kunci Sandi Enkripsi AES-256' di bagian (2) yang sesuai dengan file cadangan Anda sebelum impor!", Toast.LENGTH_LONG).show()
                                } else {
                                    importLauncher.launch("application/json")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = "Impor Berkas", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Impor File", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Encrypted Payload Visualization
        if (previewEnc != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Verified Sync", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Text("Paket Enkripsi Aktif (AES-256 Hex Cipher)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF10B981))
                        }
                        Text(
                            text = previewEnc ?: "",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 5,
                            lineHeight = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        )
                        Text(
                            text = "Garis enkripsi di atas dikirim secara aman menggunakan sinkronisasi TLS ke Cloud HematKu Anda. Pihak mana pun (termasuk server) tidak memiliki kunci dekripsi.",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppUpdateSettingsView(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    var ownerInput by remember { mutableStateOf(com.example.data.UpdateManager.getSavedRepoOwner(context)) }
    var repoInput by remember { mutableStateOf(com.example.data.UpdateManager.getSavedRepoName(context)) }
    var tokenInput by remember { mutableStateOf(com.example.data.UpdateManager.getSavedToken(context)) }
    var showToken by remember { mutableStateOf(false) }
    val updateInfo = viewModel.latestUpdateInfo.value
    val isChecking = viewModel.isCheckingUpdate.value

    LaunchedEffect(Unit) {
        if (updateInfo == null) {
            viewModel.checkAppUpdate(context)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Section 1: Konfigurasi Repositori GitHub
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Pengaturan Pembaruan GitHub",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Aplikasi akan memindai versi rilis APK terbaru dari publik repositori GitHub Anda secara otomatis saat dibuka.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = ownerInput,
                        onValueChange = { ownerInput = it },
                        label = { Text("GitHub Owner (Username / Org)") },
                        placeholder = { Text("Contoh: irfani-oppo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Owner") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = repoInput,
                        onValueChange = { repoInput = it },
                        label = { Text("GitHub Repository Name") },
                        placeholder = { Text("Contoh: app-dashboard-keuangan") },
                        leadingIcon = { Icon(Icons.Default.Folder, contentDescription = "Repo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        label = { Text("GitHub Token (Opsional)") },
                        placeholder = { Text("ghp_... atau github_pat_...") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Token") },
                        trailingIcon = {
                            IconButton(onClick = { showToken = !showToken }) {
                                Icon(
                                    imageVector = if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showToken) "Sembunyikan token" else "Tampilkan token"
                                )
                            }
                        },
                        visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        supportingText = { Text("Untuk hindari batas API (60/jam). Token perlu repo:public_repo scope.", fontSize = 10.sp) }
                    )

                    Button(
                        onClick = {
                            if (ownerInput.trim().isEmpty() || repoInput.trim().isEmpty()) {
                                Toast.makeText(context, "Username dan nama repositori wajib diisi!", Toast.LENGTH_SHORT).show()
                            } else {
                                com.example.data.UpdateManager.saveRepoDetails(context, ownerInput, repoInput)
                                com.example.data.UpdateManager.saveToken(context, tokenInput)
                                viewModel.checkAppUpdate(context) { info ->
                                    if (info.hasUpdate) {
                                        Toast.makeText(context, "Pembaruan rilis ditemukan!", Toast.LENGTH_SHORT).show()
                                    } else if (!info.downloadUrl.contains("http")) {
                                        Toast.makeText(context, info.releaseNotes, Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Aplikasi Anda sudah versi terbaru!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memeriksa...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Check")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan & Cek Pembaruan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 2: Info Versi & Rilis Terbaru
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informasi Versi Aplikasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Versi Terpasang:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(com.example.data.UpdateManager.getCurrentVersionName(context), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Version",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    if (isChecking) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memeriksa pembaruan...", fontSize = 12.sp)
                        }
                    } else if (updateInfo != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Versi Rilis GitHub:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = if (updateInfo.hasUpdate) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color(0xFF10B981).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50),
                            ) {
                                Text(
                                    text = updateInfo.latestVersion,
                                    color = if (updateInfo.hasUpdate) MaterialTheme.colorScheme.error else Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Status Badge Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (updateInfo.hasUpdate) MaterialTheme.colorScheme.error.copy(alpha = 0.08f) else Color(0xFF10B981).copy(alpha = 0.08f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (updateInfo.hasUpdate) MaterialTheme.colorScheme.error.copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (updateInfo.hasUpdate) Icons.Default.NewReleases else Icons.Default.CheckCircle,
                                    contentDescription = "Status",
                                    tint = if (updateInfo.hasUpdate) MaterialTheme.colorScheme.error else Color(0xFF10B981)
                                )
                                Column {
                                    Text(
                                        text = if (updateInfo.hasUpdate) "Pembaruan Versi Baru Tersedia! 🚀" else "Aplikasi Sudah Versi Terkini ✨",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (updateInfo.hasUpdate) MaterialTheme.colorScheme.error else Color(0xFF10B981)
                                    )
                                    Text(
                                        text = if (updateInfo.hasUpdate) "Silakan perbarui untuk mendapatkan perbaikan rilis terbaru." else "Tidak memerlukan tindakan tambahan.",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Changelog
                        Text("Catatan Rilis (Changelog):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp, min = 40.dp)
                                .verticalScroll(rememberScrollState()),
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                        ) {
                            MarkdownText(
                                text = updateInfo.releaseNotes,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        if (updateInfo.hasUpdate && updateInfo.downloadUrl.isNotEmpty()) {
                            Button(
                                onClick = {
                                    com.example.data.UpdateManager.openDownloadLink(context, updateInfo.downloadUrl)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Download")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Unduh Pembaruan APK", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(
                            text = "Klik tombol \"Simpan & Cek Pembaruan\" di atas untuk memeriksa versi terbaru.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
