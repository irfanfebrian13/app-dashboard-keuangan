package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Category
import com.example.data.model.Saving
import com.example.data.model.Transaction
import com.example.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.mutableStateOf

data class FinanceStats(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)

data class MonthlyTrend(
    val monthLabel: String,
    val amount: Double
)

class FinanceViewModel(private val repository: TransactionRepository) : ViewModel() {

    // Cache states
    val isSyncing = mutableStateOf(false)
    val syncError = mutableStateOf<String?>(null)
    val lastSyncTime = mutableStateOf<String?>(null)
    val cloudDataPreview = mutableStateOf<String?>(null)

    // Update Checker States
    val latestUpdateInfo = mutableStateOf<com.example.data.UpdateInfo?>(null)
    val isCheckingUpdate = mutableStateOf(false)

    fun checkAppUpdate(context: android.content.Context, onFinished: (com.example.data.UpdateInfo) -> Unit = {}) {
        isCheckingUpdate.value = true
        viewModelScope.launch {
            try {
                val info = com.example.data.UpdateManager.checkUpdate(context)
                latestUpdateInfo.value = info
                onFinished(info)
            } catch (e: Exception) {
                // Ignore
            } finally {
                isCheckingUpdate.value = false
            }
        }
    }

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSavings: StateFlow<List<Saving>> = repository.allSavings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val statsState: StateFlow<FinanceStats> = allTransactions.map { list ->
        val income = list.filter { it.type == "PEMASUKAN" }.sumOf { it.amount }
        val expenses = list.filter { it.type == "PENGELUARAN" }.sumOf { it.amount }
        FinanceStats(
            totalIncome = income,
            totalExpenses = expenses,
            balance = income - expenses
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinanceStats(0.0, 0.0, 0.0)
    )

    val monthlyTrends: StateFlow<List<MonthlyTrend>> = allTransactions.map { list ->
        val expenses = list.filter { it.type == "PENGELUARAN" }
        val sdfKey = SimpleDateFormat("yyyy-MM", Locale.US)
        val sdfLabel = SimpleDateFormat("MMM yy", Locale("id", "ID"))

        val grouped = expenses.groupBy {
            sdfKey.format(Date(it.date))
        }

        grouped.entries
            .sortedBy { it.key }
            .map { entry ->
                val firstDate = entry.value.first().date
                val label = sdfLabel.format(Date(firstDate))
                val totalAmount = entry.value.sumOf { it.amount }
                MonthlyTrend(label, totalAmount)
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        prepopulateDefaultCategoriesIfEmpty()
    }

    private fun prepopulateSampleDataIfEmpty() {
        viewModelScope.launch {
            val currentData = repository.allTransactions.first()
            if (currentData.isEmpty()) {
                val samples = listOf(
                    Transaction(title = "Gaji Maret", amount = 5000000.0, type = "PEMASUKAN", category = "Gaji", date = 1773619200000),
                    Transaction(title = "Belanja Bulanan maret", amount = 1200000.0, type = "PENGELUARAN", category = "Belanja", date = 1773878400000),
                    Transaction(title = "Makan & Restoran", amount = 750000.0, type = "PENGELUARAN", category = "Makanan", date = 1774224000000),
                    
                    Transaction(title = "Gaji April", amount = 5000000.0, type = "PEMASUKAN", category = "Gaji", date = 1775865600000),
                    Transaction(title = "Sewa Kamar", amount = 1500000.0, type = "PENGELUARAN", category = "Sewa", date = 1776038400000),
                    Transaction(title = "Peralatan Rumah", amount = 1100000.0, type = "PENGELUARAN", category = "Belanja", date = 1776556800000),
                    Transaction(title = "Makan & Snack", amount = 400000.0, type = "PENGELUARAN", category = "Makanan", date = 1777161600000),
                    
                    Transaction(title = "Gaji Mei", amount = 5500000.0, type = "PEMASUKAN", category = "Gaji", date = 1778457600000),
                    Transaction(title = "Belanja Baju", amount = 850000.0, type = "PENGELUARAN", category = "Belanja", date = 1778803200000),
                    Transaction(title = "Bensin & Transport", amount = 350000.0, type = "PENGELUARAN", category = "Transportasi", date = 1779148800000),
                    Transaction(title = "Makan Malam Bersama", amount = 1300000.0, type = "PENGELUARAN", category = "Makanan", date = 1779494400000),
                    Transaction(title = "Buku Pemrograman", amount = 200000.0, type = "PENGELUARAN", category = "Edukasi", date = 1780012800000),
                    
                    Transaction(title = "Gaji Juni", amount = 5500000.0, type = "PEMASUKAN", category = "Gaji", date = 1780444800000),
                    Transaction(title = "Belanja Mingguan", amount = 1000000.0, type = "PENGELUARAN", category = "Belanja", date = 1780531200000),
                    Transaction(title = "Café & Coffee", amount = 350000.0, type = "PENGELUARAN", category = "Makanan", date = 1780617600000),
                    Transaction(title = "Langganan Netflix", amount = 186000.0, type = "PENGELUARAN", category = "Hiburan", date = 1780790400000)
                )
                for (sample in samples) {
                    repository.insert(sample)
                }
            }
        }
    }

    private fun prepopulateDefaultCategoriesIfEmpty() {
        viewModelScope.launch {
            val count = repository.countCategories()
            if (count == 0) {
                val defaults = listOf(
                    Category(name = "Makanan", type = "PENGELUARAN", icon = "restaurant"),
                    Category(name = "Transportasi", type = "PENGELUARAN", icon = "directions_car"),
                    Category(name = "Belanja", type = "PENGELUARAN", icon = "shopping_bag"),
                    Category(name = "Hiburan", type = "PENGELUARAN", icon = "sports_esports"),
                    Category(name = "Sewa", type = "PENGELUARAN", icon = "home"),
                    Category(name = "Edukasi", type = "PENGELUARAN", icon = "school"),
                    Category(name = "Gaji", type = "PEMASUKAN", icon = "payments"),
                    Category(name = "Hadiah", type = "PEMASUKAN", icon = "featured_play_list"),
                    Category(name = "Investasi", type = "PEMASUKAN", icon = "trending_up"),
                    Category(name = "Lain-lain", type = "PENGELUARAN", icon = "category")
                )
                for (cat in defaults) {
                    repository.insertCategory(cat)
                }
            }
        }
    }

    private fun prepopulateSampleSavingsIfEmpty() {
        viewModelScope.launch {
            repository.allSavings.first().let { currentSavings ->
                if (currentSavings.isEmpty()) {
                    val sampleSavings = listOf(
                        Saving(title = "Beli Laptop ROG", targetAmount = 15000000.0, currentAmount = 5500000.0, notes = "Laptop development", holderName = "Irfani"),
                        Saving(title = "Dana Liburan Bali", targetAmount = 5000000.0, currentAmount = 3500000.0, notes = "Tiket & akomodasi", holderName = "Irfani"),
                        Saving(title = "Dana Darurat 6 Bulan", targetAmount = 12000000.0, currentAmount = 8000000.0, notes = "Simpanan darurat", holderName = "Irfani")
                    )
                    for (saving in sampleSavings) {
                        repository.insertSaving(saving)
                    }
                }
            }
        }
    }

    fun addTransaction(title: String, amount: Double, type: String, category: String, date: Long, notes: String, holderName: String = "") {
        viewModelScope.launch {
            repository.insert(
                Transaction(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    date = date,
                    notes = notes,
                    holderName = holderName
                )
            )
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    // Savings Goals operations
    fun addSaving(title: String, targetAmount: Double, currentAmount: Double, notes: String, holderName: String) {
        viewModelScope.launch {
            repository.insertSaving(
                Saving(
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount,
                    notes = notes,
                    holderName = holderName
                )
            )
        }
    }

    fun updateSaving(saving: Saving) {
        viewModelScope.launch {
            repository.updateSaving(saving)
        }
    }

    fun deleteSaving(saving: Saving) {
        viewModelScope.launch {
            repository.deleteSaving(saving)
        }
    }

    fun topUpSaving(saving: Saving, amount: Double) {
        viewModelScope.launch {
            val updated = saving.copy(currentAmount = saving.currentAmount + amount)
            repository.updateSaving(updated)
        }
    }

    fun withdrawSaving(saving: Saving, amount: Double) {
        viewModelScope.launch {
            val updated = saving.copy(currentAmount = (saving.currentAmount - amount).coerceAtLeast(0.0))
            repository.updateSaving(updated)
        }
    }

    // Custom Categories operations
    fun addCategory(name: String, type: String, icon: String) {
        viewModelScope.launch {
            repository.insertCategory(
                Category(
                    name = name,
                    type = type,
                    icon = icon
                )
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Cloud Encrypted Sync operations
    val firebaseUserEmail = mutableStateOf<String?>(null)
    val firebaseUserId = mutableStateOf<String?>(null)

    fun checkUserLoggedIn() {
        val auth = FirebaseManager.getAuth()
        if (auth != null) {
            val user = auth.currentUser
            firebaseUserEmail.value = user?.email
            firebaseUserId.value = user?.uid
        } else {
            firebaseUserEmail.value = null
            firebaseUserId.value = null
        }
    }

    fun initializeFirebaseOnStart(context: android.content.Context) {
        FirebaseManager.initialize(context)
        checkUserLoggedIn()
    }

    fun registerFirebaseUser(
        email: String,
        word: String,
        context: android.content.Context,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            if (!FirebaseManager.isInitialized.value) {
                FirebaseManager.initialize(context)
            }
            if (!FirebaseManager.isInitialized.value) {
                onComplete(false, FirebaseManager.initError.value ?: "Firebase belum dikonfigurasi")
                return@launch
            }
            val auth = FirebaseManager.getAuth()
            if (auth == null) {
                onComplete(false, "Sistem Firebase Auth tidak tersedia.")
                return@launch
            }
            try {
                isSyncing.value = true
                auth.createUserWithEmailAndPassword(email, word)
                    .awaitWithTimeout(10000)
                isSyncing.value = false
                checkUserLoggedIn()
                onComplete(true, null)
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                isSyncing.value = false
                onComplete(false, "Koneksi pendaftaran timeout (10s). Silakan periksa internet Anda.")
            } catch (e: Exception) {
                isSyncing.value = false
                onComplete(false, e.localizedMessage ?: "Pendaftaran gagal.")
            }
        }
    }

    fun loginFirebaseUser(
        email: String,
        word: String,
        context: android.content.Context,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            if (!FirebaseManager.isInitialized.value) {
                FirebaseManager.initialize(context)
            }
            if (!FirebaseManager.isInitialized.value) {
                onComplete(false, FirebaseManager.initError.value ?: "Firebase belum dikonfigurasi")
                return@launch
            }
            val auth = FirebaseManager.getAuth()
            if (auth == null) {
                onComplete(false, "Sistem Firebase Auth tidak tersedia.")
                return@launch
            }
            try {
                isSyncing.value = true
                auth.signInWithEmailAndPassword(email, word)
                    .awaitWithTimeout(10000)
                isSyncing.value = false
                checkUserLoggedIn()
                onComplete(true, null)
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                isSyncing.value = false
                onComplete(false, "Koneksi masuk timeout (10s). Silakan periksa internet Anda.")
            } catch (e: Exception) {
                isSyncing.value = false
                onComplete(false, e.localizedMessage ?: "Login gagal.")
            }
        }
    }

    fun logoutFirebaseUser() {
        try {
            FirebaseManager.getAuth()?.signOut()
        } catch (e: Exception) {
            // suppress
        }
        firebaseUserEmail.value = null
        firebaseUserId.value = null
    }

    fun performCloudPush(context: android.content.Context, syncId: String, passphrase: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            isSyncing.value = true
            syncError.value = null
            try {
                // Fetch local Room state
                val txsList = repository.allTransactions.first()
                val savingsList = repository.allSavings.first()
                val categoriesList = repository.allCategories.first()

                // Compile into serializable Map payload
                val txsPayload = txsList.map { 
                    mapOf("title" to it.title, "amount" to it.amount, "type" to it.type, "category" to it.category, "date" to it.date, "notes" to it.notes, "holderName" to it.holderName) 
                }
                val savingsPayload = savingsList.map {
                    mapOf("title" to it.title, "targetAmount" to it.targetAmount, "currentAmount" to it.currentAmount, "notes" to it.notes, "date" to it.date, "holderName" to it.holderName)
                }
                val categoriesPayload = categoriesList.map {
                    mapOf("name" to it.name, "type" to it.type, "icon" to it.icon)
                }

                val fullPayloadMap = mapOf(
                    "transactions" to txsPayload,
                    "savings" to savingsPayload,
                    "categories" to categoriesPayload
                )

                // Serialize using Moshi
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(Map::class.java)
                val jsonString = adapter.toJson(fullPayloadMap) ?: ""

                // Encrypt payload with AES-256 (PBKDF2 SHA-256 key-derive)
                val encryptedBase64 = CryptoUtils.encrypt(jsonString, passphrase)

                // Store securely in persistent local SharedPreferences cache
                val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
                val syncTimeStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
                prefs.edit().apply {
                    putString("encrypted_backup", encryptedBase64)
                    putString("last_sync_time", syncTimeStr)
                    putString("cloud_email", syncId)
                    apply()
                }

                cloudDataPreview.value = encryptedBase64
                lastSyncTime.value = syncTimeStr

                // If real Firebase is initialized, push to Firestore
                val firestore = FirebaseManager.getFirestore()
                if (firestore != null && syncId.isNotBlank()) {
                    val normalizedDocId = syncId.trim().lowercase().replace(Regex("[^a-z0-9@._-]"), "_")
                    val backupData = mapOf(
                        "encrypted_backup" to encryptedBase64,
                        "last_sync_time" to syncTimeStr,
                        "updated_at" to System.currentTimeMillis(),
                        "sync_id" to syncId
                    )
                    try {
                        firestore.collection("backups").document(normalizedDocId)
                            .set(backupData)
                            .awaitWithTimeout(10000)
                        isSyncing.value = false
                        onComplete(true, null)
                    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                        isSyncing.value = false
                        val errMsg = "Koneksi cloud timeout. Silakan periksa koneksi internet Anda."
                        syncError.value = errMsg
                        onComplete(false, errMsg)
                    } catch (e: Exception) {
                        isSyncing.value = false
                        val errMsg = e.localizedMessage ?: "Gagal mengunggah data ke cloud"
                        syncError.value = errMsg
                        onComplete(false, errMsg)
                    }
                } else {
                    // Falls back cleanly to local-cache sync emulator
                    kotlinx.coroutines.delay(1000)
                    isSyncing.value = false
                    onComplete(true, null)
                }
            } catch (e: Exception) {
                isSyncing.value = false
                val errMsg = e.localizedMessage ?: "Gagal sinkronisasi"
                syncError.value = errMsg
                onComplete(false, errMsg)
            }
        }
    }

    fun performCloudPull(context: android.content.Context, syncId: String, passphrase: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            isSyncing.value = true
            syncError.value = null

            val firestore = FirebaseManager.getFirestore()

            if (firestore != null && syncId.isNotBlank()) {
                val normalizedDocId = syncId.trim().lowercase().replace(Regex("[^a-z0-9@._-]"), "_")
                // Real Firestore Pull
                try {
                    val document = firestore.collection("backups").document(normalizedDocId)
                        .get()
                        .awaitWithTimeout(10000)
                    
                    val encryptedBase64 = document?.getString("encrypted_backup")
                    if (encryptedBase64 == null) {
                        isSyncing.value = false
                        onComplete(false, "Firestore: Tidak ada data sinkronisasi awan yang ditemukan untuk ID ini!")
                        return@launch
                    }

                    decryptAndRestorePayload(encryptedBase64, passphrase, context) { success, errMsg ->
                        isSyncing.value = false
                        onComplete(success, errMsg)
                    }
                } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                    isSyncing.value = false
                    syncError.value = "Koneksi cloud timeout saat mengunduh data."
                    onComplete(false, "Koneksi timeout. Silakan periksa jaringan internet Anda.")
                } catch (e: Exception) {
                    isSyncing.value = false
                    syncError.value = e.localizedMessage ?: "Gagal mengunduh dari Firestore"
                    onComplete(false, e.localizedMessage ?: "Gagal mengunduh dari Firestore")
                }
            } else {
                // Standard local SharedPreferences emulator
                try {
                    kotlinx.coroutines.delay(1000)
                    val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
                    val encryptedBase64 = prefs.getString("encrypted_backup", null)

                    if (encryptedBase64 == null) {
                        isSyncing.value = false
                        onComplete(false, "Tidak ada data sinkronisasi awan local-cache!")
                        return@launch
                    }

                    decryptAndRestorePayload(encryptedBase64, passphrase, context) { success, errMsg ->
                        isSyncing.value = false
                        onComplete(success, errMsg)
                    }
                } catch (e: Exception) {
                    isSyncing.value = false
                    syncError.value = e.localizedMessage ?: "Sync error"
                    onComplete(false, e.localizedMessage)
                }
            }
        }
    }

    private fun decryptAndRestorePayload(
        encryptedBase64: String,
        passphrase: String,
        context: android.content.Context,
        onDone: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Decrypt client-side using passphrase
                val decryptedJson: String
                try {
                    decryptedJson = CryptoUtils.decrypt(encryptedBase64, passphrase)
                } catch (e: java.security.GeneralSecurityException) {
                    onDone(false, "Gagal dekripsi: Sandi Pembuka Kunci/Sandi Enkripsi salah!")
                    return@launch
                } catch (e: Exception) {
                    onDone(false, "Sandi salah atau data backup rusak!")
                    return@launch
                }

                // Parse restored payload
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(Map::class.java)
                val rawMap = adapter.fromJson(decryptedJson)

                if (rawMap != null) {
                    @Suppress("UNCHECKED_CAST")
                    val txs = rawMap["transactions"] as? List<Map<String, Any>>
                    @Suppress("UNCHECKED_CAST")
                    val svs = rawMap["savings"] as? List<Map<String, Any>>
                    @Suppress("UNCHECKED_CAST")
                    val cats = rawMap["categories"] as? List<Map<String, Any>>

                    // Restore custom categories count safely
                    if (cats != null) {
                        repository.deleteAllCategories()
                        for (catMap in cats) {
                            val name = catMap["name"] as? String ?: continue
                            val type = catMap["type"] as? String ?: "PENGELUARAN"
                            val icon = catMap["icon"] as? String ?: "category"
                            repository.insertCategory(Category(name = name, type = type, icon = icon))
                        }
                    }

                    // Restore transactions safely
                    if (txs != null) {
                        repository.deleteAllTransactions()
                        for (txMap in txs) {
                            val title = txMap["title"] as? String ?: continue
                            val amount = (txMap["amount"] as? Number)?.toDouble() ?: 0.0
                            val type = txMap["type"] as? String ?: "PENGELUARAN"
                            val category = txMap["category"] as? String ?: "Lain-lain"
                            val date = (txMap["date"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            val notes = txMap["notes"] as? String ?: ""
                            val holderName = txMap["holderName"] as? String ?: ""
                            repository.insert(Transaction(title = title, amount = amount, type = type, category = category, date = date, notes = notes, holderName = holderName))
                        }
                    }

                    // Restore savings goals safely
                    if (svs != null) {
                        repository.deleteAllSavings()
                        for (svMap in svs) {
                            val title = svMap["title"] as? String ?: continue
                            val targetAmount = (svMap["targetAmount"] as? Number)?.toDouble() ?: 0.0
                            val currentAmount = (svMap["currentAmount"] as? Number)?.toDouble() ?: 0.0
                            val notes = svMap["notes"] as? String ?: ""
                            val date = (svMap["date"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            val holderName = svMap["holderName"] as? String ?: ""
                            repository.insertSaving(Saving(title = title, targetAmount = targetAmount, currentAmount = currentAmount, notes = notes, date = date, holderName = holderName))
                        }
                    }
                }

                // Cache locally
                val prefs = context.getSharedPreferences("HEMATKU_CLOUD_SYNC", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("encrypted_backup", encryptedBase64).apply()
                cloudDataPreview.value = encryptedBase64

                onDone(true, null)
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Gagal memproses data pemulihan")
            }
        }
    }

    fun exportBackupToDownloads(
        context: android.content.Context,
        passphrase: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Compile into serializable Map payload
                val txsList = repository.allTransactions.first()
                val savingsList = repository.allSavings.first()
                val categoriesList = repository.allCategories.first()

                val txsPayload = txsList.map { 
                    mapOf("title" to it.title, "amount" to it.amount, "type" to it.type, "category" to it.category, "date" to it.date, "notes" to it.notes, "holderName" to it.holderName) 
                }
                val savingsPayload = savingsList.map {
                    mapOf("title" to it.title, "targetAmount" to it.targetAmount, "currentAmount" to it.currentAmount, "notes" to it.notes, "date" to it.date, "holderName" to it.holderName)
                }
                val categoriesPayload = categoriesList.map {
                    mapOf("name" to it.name, "type" to it.type, "icon" to it.icon)
                }

                val fullPayloadMap = mapOf(
                    "transactions" to txsPayload,
                    "savings" to savingsPayload,
                    "categories" to categoriesPayload
                )

                // Serialize using Moshi
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(Map::class.java)
                val jsonString = adapter.toJson(fullPayloadMap) ?: ""

                // Encrypt payload with AES-256
                val encryptedBase64 = CryptoUtils.encrypt(jsonString, passphrase)

                // Outer structure to hold encrypted backup
                val backupOuterMap = mapOf(
                    "encrypted_backup" to encryptedBase64,
                    "app" to "HematKu",
                    "backup_type" to "local_file_backup",
                    "version" to 1,
                    "timestamp" to System.currentTimeMillis()
                )
                val outerAdapter = moshi.adapter(Map::class.java)
                val finalFileContent = outerAdapter.toJson(backupOuterMap) ?: ""

                val filename = "Cadangan_HematKu_${System.currentTimeMillis()}.json"

                var success = false
                var savedPathMsg = ""

                // Save to Downloads using MediaStore if Android Q+ and standard java.io on older
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/json")
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS + "/HematKu_Backups")
                    }
                    val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { os ->
                            os.write(finalFileContent.toByteArray(Charsets.UTF_8))
                            success = true
                            savedPathMsg = "Penyimpanan sukses! Disimpan ke folder 'Downloads/HematKu_Backups/$filename'"
                        }
                    }
                } else {
                    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    val backupFolder = java.io.File(downloadsDir, "HematKu_Backups")
                    if (!backupFolder.exists()) {
                        backupFolder.mkdirs()
                    }
                    val targetFile = java.io.File(backupFolder, filename)
                    java.io.FileOutputStream(targetFile).use { fos ->
                        fos.write(finalFileContent.toByteArray(Charsets.UTF_8))
                        success = true
                        savedPathMsg = "Penyimpanan sukses! Disimpan ke ${targetFile.absolutePath}"
                    }
                }

                if (success) {
                    onComplete(true, savedPathMsg)
                } else {
                    onComplete(false, "Sistem Android menolak pembuatan file di folder Download.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, e.localizedMessage ?: "Gagal mengekspor data cadangan.")
            }
        }
    }

    fun importBackupFromFileContent(
        context: android.content.Context,
        fileContent: String,
        passphrase: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Parse JSON file content
                val moshi = com.squareup.moshi.Moshi.Builder()
                    .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                    .build()
                val adapter = moshi.adapter(Map::class.java)
                val map = adapter.fromJson(fileContent)
                
                if (map == null) {
                    onComplete(false, "Format file tidak valid atau kosong!")
                    return@launch
                }

                val encryptedBase64 = map["encrypted_backup"] as? String
                if (encryptedBase64 == null) {
                    onComplete(false, "File tidak mengandung data enkripsi cadangan HematKu!")
                    return@launch
                }

                // 2. Call existing decrypt & restore payload function
                decryptAndRestorePayload(encryptedBase64, passphrase, context) { success, errMsg ->
                    onComplete(success, errMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Gagal mengimpor file: " + (e.localizedMessage ?: "format rusak"))
            }
        }
    }

    fun shareTransactionsAsCSV(context: android.content.Context) {
        val list = allTransactions.value
        if (list.isEmpty()) {
            android.widget.Toast.makeText(context, "Tidak ada catatan transaksi untuk diekspor!", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val csvHeader = "ID,Tanggal,Nama Transaksi,Tipe,Kategori,Jumlah (Rp),Catatan,Nama Penabung\n"
            val csvBody = list.joinToString("\n") { tx ->
                val dateStr = sdf.format(Date(tx.date))
                val titleEscaped = tx.title.replace("\"", "\"\"")
                val notesEscaped = tx.notes.replace("\"", "\"\"")
                val categoryEscaped = tx.category.replace("\"", "\"\"")
                val holderEscaped = tx.holderName.replace("\"", "\"\"")
                "${tx.id},\"$dateStr\",\"$titleEscaped\",\"${tx.type}\",\"$categoryEscaped\",${tx.amount.toLong()},\"$notesEscaped\",\"$holderEscaped\""
            }
            val csvText = csvHeader + csvBody

            val cachePath = java.io.File(context.cacheDir, "exports")
            if (!cachePath.exists()) {
                cachePath.mkdirs()
            }
            val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val dateStr = formatter.format(Date())
            val csvFile = java.io.File(cachePath, "Laporan_Transaksi_HematKu_$dateStr.csv")
            
            cachePath.listFiles()?.forEach { file ->
                if (file.name.startsWith("Laporan_Transaksi_") && file.name.endsWith(".csv")) {
                    file.delete()
                }
            }

            java.io.FileWriter(csvFile).use { writer ->
                writer.write(csvText)
            }

            val authority = "${context.packageName}.fileprovider"
            val uri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(context, authority, csvFile)

            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Laporan Catatan Transaksi HematKu")
                putExtra(android.content.Intent.EXTRA_TEXT, "Berikut adalah lampiran ekspor dokumen CSV catatan transaksi keuangan saya dari aplikasi HematKu.")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Ekspor CSV via").apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            })

        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(context, "Gagal mengekspor CSV: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}

private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitWithTimeout(timeoutMs: Long): T {
    return withTimeout(timeoutMs) {
        suspendCancellableCoroutine { cont ->
            this@awaitWithTimeout.addOnCompleteListener { task ->
                if (cont.isActive) {
                    if (task.isSuccessful) {
                        cont.resume(task.result)
                    } else {
                        cont.resumeWithException(task.exception ?: Exception("Task failed with no exception"))
                    }
                }
            }
        }
    }
}

class FinanceViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
