package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Transaction
import com.example.data.model.Category
import com.example.data.model.Saving
import com.example.ui.icons.CatIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.viewmodel.FinanceStats
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MonthlyTrend
import java.text.SimpleDateFormat
import java.util.*

// Helper Currency Formatters
fun formatCurrency(amount: Double): String {
    val formatted = String.format("%,d", amount.toLong()).replace(',', '.')
    return "Rp $formatted"
}

fun formatCompactCurrency(amount: Double): String {
    return when {
        amount >= 1_000_000 -> String.format(Locale("id", "ID"), "%.1f Jt", amount / 1_000_000.0)
        amount >= 1_000 -> String.format(Locale("id", "ID"), "%.0f Rb", amount / 1_000.0)
        else -> String.format(Locale("id", "ID"), "%.0f", amount)
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) }
    var selectedTransactionByEdit by remember { mutableStateOf<Transaction?>(null) }

    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val stats by viewModel.statsState.collectAsStateWithLifecycle()
    val trends by viewModel.monthlyTrends.collectAsStateWithLifecycle()
    val savings by viewModel.allSavings.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()

    var showUpdateDialog by remember { mutableStateOf(false) }
    val updateInfo = viewModel.latestUpdateInfo.value
    val hasPendingUpdate = updateInfo?.hasUpdate == true

    LaunchedEffect(Unit) {
        viewModel.checkAppUpdate(context) { info ->
            if (info.hasUpdate) {
                showUpdateDialog = true
            }
        }
    }

    fun openUpdateDialog() {
        if (updateInfo != null) {
            showUpdateDialog = true
        } else {
            viewModel.checkAppUpdate(context) { info ->
                if (info.hasUpdate) {
                    showUpdateDialog = true
                } else {
                    Toast.makeText(context, "Tidak ada pembaruan tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Wallet Icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "HematKu",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "LINDUNGI KEUANGAN ANDA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        IconButton(
                            onClick = { openUpdateDialog() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (hasPendingUpdate) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(if (activeTab == 0) Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(if (activeTab == 1) Icons.Default.AddCircle else Icons.Outlined.AddCircle, contentDescription = "Catat") },
                    label = { Text("Catat", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(if (activeTab == 2) CatIcon.Filled else CatIcon.Outlined, contentDescription = "Tabungan") },
                    label = { Text("Tabungan", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(if (activeTab == 3) Icons.Default.BarChart else Icons.Outlined.BarChart, contentDescription = "Tren") },
                    label = { Text("Tren", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(if (activeTab == 4) Icons.Default.CloudSync else Icons.Outlined.CloudSync, contentDescription = "Setelan") },
                    label = { Text("Setelan", fontSize = 11.sp) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> BerandaTab(
                    stats = stats,
                    transactions = transactions,
                    categories = categories,
                    onTransactionClick = { selectedTransactionByEdit = it },
                    onExportCsvClick = { viewModel.shareTransactionsAsCSV(context) }
                )
                1 -> CatatTab(
                    categories = categories,
                    onSave = { title, amount, type, category, date, notes ->
                        viewModel.addTransaction(title, amount, type, category, date, notes)
                        Toast.makeText(context, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        activeTab = 0 // back to main dashboard
                    }
                )
                2 -> SavingsTab(
                    viewModel = viewModel,
                    savings = savings
                )
                3 -> TrenTab(trends = trends, stats = stats)
                4 -> SyncAndCategoryTab(
                    viewModel = viewModel,
                    categories = categories
                )
            }

            // Edit Transaction Dialog
            selectedTransactionByEdit?.let { transaction ->
                EditTransactionDialog(
                    transaction = transaction,
                    categories = categories,
                    onDismiss = { selectedTransactionByEdit = null },
                    onConfirmEdit = { updatedTransaction ->
                        viewModel.updateTransaction(updatedTransaction)
                        selectedTransactionByEdit = null
                        Toast.makeText(context, "Transaksi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        viewModel.deleteTransaction(transaction)
                        selectedTransactionByEdit = null
                        Toast.makeText(context, "Transaksi berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // In-App Update Dialog (M3 styled)
            if (showUpdateDialog && updateInfo != null) {
                AlertDialog(
                    onDismissRequest = { showUpdateDialog = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "Pembaruan",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Pembaruan Tersedia! 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Versi Sekarang:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(updateInfo.currentVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Versi Terbaru:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(updateInfo.latestVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
                            
                            Text("Catatan Rilis:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 120.dp, min = 40.dp)
                                    .verticalScroll(rememberScrollState()),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = updateInfo.releaseNotes,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                com.example.data.UpdateManager.openDownloadLink(context, updateInfo.downloadUrl)
                                showUpdateDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Unduh & Instal Sekarang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showUpdateDialog = false }
                        ) {
                            Text("Nanti Saja", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                )
            }
        }
    }
}

// ----------------------------------------------------
// TAB 1: BERANDA (DASHBOARD)
// ----------------------------------------------------
@Composable
fun BerandaTab(
    stats: FinanceStats,
    transactions: List<Transaction>,
    categories: List<Category>,
    onTransactionClick: (Transaction) -> Unit,
    onExportCsvClick: () -> Unit
) {
    var selectedFilterCategory by remember { mutableStateOf("Semua") }

    val filterCategoriesList = remember(categories) {
        listOf("Semua") + categories.map { it.name }.distinct()
    }

    val filteredTransactions = remember(transactions, selectedFilterCategory) {
        if (selectedFilterCategory == "Semua") {
            transactions
        } else {
            transactions.filter { it.category == selectedFilterCategory }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Balances block
        item {
            BalanceCard(stats = stats)
        }

        // Category Quick filter
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Filter Kategori",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterCategoriesList) { category ->
                        val isSelected = category == selectedFilterCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Daftar Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = onExportCsvClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Ekspor CSV",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${filteredTransactions.size} Transaksi",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Empty state check
        if (filteredTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Belum Ada Catatan",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Silakan tambah catatan transaksi baru di tab 'Catat' atau ubah filter pencarian Anda.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { item ->
                TransactionItem(transaction = item, onClick = { onTransactionClick(item) })
            }
        }
    }
}

// ----------------------------------------------------
// BALANCE CARD
// ----------------------------------------------------
@Composable
fun BalanceCard(stats: FinanceStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Total Saldo",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Rp",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                    Text(
                        text = String.format("%,d", stats.balance.toLong()).replace(',', '.'),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pemasukan Box (white/10 background, border)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Pemasukan",
                                tint = Color(0xFF6EE7B7), // Emerald-300
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "PEMASUKAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = formatCurrency(stats.totalIncome),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Pengeluaran Box (white/10 background, border)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Pengeluaran",
                                tint = Color(0xFFFCA5A5), // Red-300
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "PENGELUARAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = formatCurrency(stats.totalExpenses),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TRANSACTION LIST ITEM
// ----------------------------------------------------
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    // Determine category icon and color
    val icon = getCategoryIcon(transaction.category)
    val color = if (transaction.type == "PEMASUKAN") IncomeGreen else ExpenseRed
    val prefix = if (transaction.type == "PEMASUKAN") "+" else "-"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (transaction.notes.isNotEmpty()) {
                    Text(
                        text = transaction.notes,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${transaction.category} • ${formatDate(transaction.date)}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "$prefix${formatCurrency(transaction.amount)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = color
            )
        }
    }
}

// Helper category icons map
fun getCategoryIcon(category: String): ImageVector {
    return when (category.trim()) {
        "Makanan" -> Icons.Default.Restaurant
        "Belanja" -> Icons.Default.ShoppingBag
        "Transportasi" -> Icons.Default.DirectionsCar
        "Sewa" -> Icons.Default.Home
        "Edukasi" -> Icons.Default.School
        "Hiburan" -> Icons.Default.SportsEsports
        "Gaji" -> Icons.Default.Payments
        "Hadiah" -> Icons.Default.Redeem
        "Investasi" -> Icons.Default.TrendingUp
        "Kesehatan" -> Icons.Default.MedicalServices
        "Sosial" -> Icons.Default.Favorite
        "Hobi" -> Icons.Default.SportsBasketball
        "Tabungan" -> CatIcon.Filled
        else -> Icons.Default.Category
    }
}

// ----------------------------------------------------
// TAB 2: CATAT (ADD TRANSACTION FORM)
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatatTab(
    categories: List<Category>,
    onSave: (String, Double, String, String, Long, String) -> Unit
) {
    var type by remember { mutableStateOf("PENGELUARAN") } // "PENGELUARAN" or "PEMASUKAN"
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makanan") }
    var notes by remember { mutableStateOf("") }

    val expenseCategories = remember(categories) {
        val list = categories.filter { it.type == "PENGELUARAN" }.map { it.name }
        if (list.isEmpty()) listOf("Makanan") else list
    }
    val incomeCategories = remember(categories) {
        val list = categories.filter { it.type == "PEMASUKAN" }.map { it.name }
        if (list.isEmpty()) listOf("Gaji") else list
    }

    // Automatically synchronize default category on type change
    LaunchedEffect(type, expenseCategories, incomeCategories) {
        category = if (type == "PEMASUKAN") {
            incomeCategories.firstOrNull() ?: "Gaji"
        } else {
            expenseCategories.firstOrNull() ?: "Makanan"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Catat Transaksi",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Segmented Type Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (type == "PENGELUARAN") ExpenseRed else Color.Transparent)
                        .clickable { type = "PENGELUARAN" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pengeluaran",
                        fontWeight = FontWeight.Bold,
                        color = if (type == "PENGELUARAN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (type == "PEMASUKAN") IncomeGreen else Color.Transparent)
                        .clickable { type = "PEMASUKAN" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pemasukan",
                        fontWeight = FontWeight.Bold,
                        color = if (type == "PEMASUKAN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Value field (IDR Amount)
        item {
            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Jumlah (Rp)") },
                placeholder = { Text("Contoh: 150000") },
                leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp, end = 4.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }

        // Title Field
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nama Transaksi") },
                placeholder = { Text("Contoh: Beli Kopi, Gaji Bulanan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Category Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pilih Kategori",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                val activeList = if (type == "PENGELUARAN") expenseCategories else incomeCategories
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeList.forEach { cat ->
                        val isSelected = cat == category
                        val chipColor = if (type == "PEMASUKAN") IncomeGreen else ExpenseRed

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) chipColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) chipColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { category = cat }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(cat),
                                    contentDescription = cat,
                                    tint = if (isSelected) chipColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = cat,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) chipColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notes optional Field
        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan Tambahan (Opsional)") },
                placeholder = { Text("Contoh: Di kafe Senayan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                maxLines = 3
            )
        }

        // Action Buttons
        item {
            Button(
                onClick = {
                    val finalAmount = amountStr.toDoubleOrNull()
                    if (title.isBlank() || finalAmount == null || finalAmount <= 0) {
                        return@Button
                    }
                    onSave(title, finalAmount, type, category, System.currentTimeMillis(), notes)
                },
                enabled = title.isNotBlank() && amountStr.toDoubleOrNull() != null && amountStr.toDoubleOrNull()!! > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintGreenPrimary
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = "Simpan")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simpan Transaksi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}



// ----------------------------------------------------
// TAB 3: LAPORAN & GRAFIK (AUTO TREND METRIC VISUALIZER)
// ----------------------------------------------------
@Composable
fun TrenTab(trends: List<MonthlyTrend>, stats: FinanceStats) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Tren Pengeluaran",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Statistik tren pengeluaran bulanan Anda secara otomatis",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // High Quality Chart Container
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Grafik Pola Pengeluaran Bulanan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (trends.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Chart Empty",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum Ada Data Tren",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Grafik akan muncul jika ada data transaksi pengeluaran.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        // The actual gorgeous custom Bar Chart
                        MonthlyExpenseBarChart(trends = trends)
                    }
                }
            }
        }

        // Insight summary cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Insight", tint = MintGreenPrimary)
                        Text(text = "Analisis Pengeluaran", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    val maxMonth = trends.maxByOrNull { it.amount }
                    val avgExpense = if (trends.isNotEmpty()) trends.map { it.amount }.average() else 0.0

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Rata-rata Pengeluaran:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = formatCurrency(avgExpense), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (maxMonth != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Konsumsi Tertinggi:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${maxMonth.monthLabel} (${formatCurrency(maxMonth.amount)})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Efisiensi Tabungan:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val efficiency = if (stats.totalIncome > 0) {
                                ((stats.totalIncome - stats.totalExpenses) / stats.totalIncome) * 100
                            } else {
                                0.0
                            }
                            Text(
                                text = String.format(Locale("id", "ID"), "%.1f%%", efficiency),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (efficiency > 20) IncomeGreen else ExpenseRed
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom Draw Chart Component
@Composable
fun MonthlyExpenseBarChart(trends: List<MonthlyTrend>) {
    val maxVal = trends.maxOfOrNull { it.amount } ?: 1.0
    var triggerAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(trends) {
        triggerAnimation = false
        kotlinx.coroutines.delay(40)
        triggerAnimation = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        trends.forEachIndexed { index, trend ->
            // Scale logic
            val ratio = if (maxVal > 0) (trend.amount / maxVal).toFloat() else 0f
            // Safety cap ratio at min 0.05 so bar doesn't disappear completely
            val safeRatio = if (ratio < 0.05f && trend.amount > 0) 0.05f else ratio

            // Stagger animation slightly for each bar
            var animateValue by remember { mutableStateOf(0f) }
            LaunchedEffect(triggerAnimation, safeRatio) {
                if (triggerAnimation) {
                    kotlinx.coroutines.delay(index * 60L) // Staggered delay
                    animateValue = safeRatio
                } else {
                    animateValue = 0f
                }
            }

            val animatedHeightRatio by animateFloatAsState(
                targetValue = animateValue,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "BarHeightAnimation"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Label amount
                Text(
                    text = formatCompactCurrency(trend.amount),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Fixed-height container for the bar to keep the monthly label statically and perfectly aligned
                Box(
                    modifier = Modifier
                        .height(130.dp)
                        .width(30.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val isMaxExpense = trend.amount == maxVal && maxVal > 0
                    val barBrush = if (isMaxExpense) {
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight(animatedHeightRatio)
                            .width(30.dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(brush = barBrush)
                            .border(
                                width = 1.dp,
                                color = if (isMaxExpense) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Month Tag
                Text(
                    text = trend.monthLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ----------------------------------------------------
// EDIT & DELETE INTERACTIVE TRANSACTION DIALOG
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirmEdit: (Transaction) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(transaction.title) }
    var amountStr by remember { mutableStateOf(transaction.amount.toLong().toString()) }
    var type by remember { mutableStateOf(transaction.type) }
    var category by remember { mutableStateOf(transaction.category) }
    var notes by remember { mutableStateOf(transaction.notes) }

    val expenseCategories = remember(categories) {
        val list = categories.filter { it.type == "PENGELUARAN" }.map { it.name }
        if (list.isEmpty()) listOf("Makanan") else list
    }
    val incomeCategories = remember(categories) {
        val list = categories.filter { it.type == "PEMASUKAN" }.map { it.name }
        if (list.isEmpty()) listOf("Gaji") else list
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Catatan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = ExpenseRed)
                    }
                }

                // Type Tab in edit
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "PENGELUARAN") ExpenseRed else Color.Transparent)
                            .clickable { type = "PENGELUARAN"; category = "Makanan" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pengeluaran",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (type == "PENGELUARAN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "PEMASUKAN") IncomeGreen else Color.Transparent)
                            .clickable { type = "PEMASUKAN"; category = "Gaji" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pemasukan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (type == "PEMASUKAN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Jumlah (Rp)") },
                    leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp, end = 4.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Transaksi") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Kategori", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    val activeList = if (type == "PENGELUARAN") expenseCategories else incomeCategories
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeList.forEach { cat ->
                            val isSelected = cat == category
                            val chipColor = if (type == "PEMASUKAN") IncomeGreen else ExpenseRed

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) chipColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) chipColor else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { category = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) chipColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val finalAmount = amountStr.toDoubleOrNull()
                            if (title.isBlank() || finalAmount == null || finalAmount <= 0) return@Button
                            onConfirmEdit(
                                transaction.copy(
                                    title = title,
                                    amount = finalAmount,
                                    type = type,
                                    category = category,
                                    notes = notes
                                )
                            )
                        },
                        enabled = title.isNotBlank() && amountStr.toDoubleOrNull() != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreenPrimary)
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}


