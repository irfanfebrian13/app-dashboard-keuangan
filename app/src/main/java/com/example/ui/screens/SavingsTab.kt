package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Saving
import com.example.ui.icons.CatIcon
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun SavingsTab(
    viewModel: FinanceViewModel,
    savings: List<Saving>
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSavingForTx by remember { mutableStateOf<Saving?>(null) }
    var txType by remember { mutableStateOf("TOPUP") } // "TOPUP" or "WITHDRAW"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Savings Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HematKu Tabungan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Kelola goal tabungan Anda dengan rapi",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Target",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Target")
            }
        }

        // Empty state check
        if (savings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = CatIcon.Filled,
                                    contentDescription = "No Savings Logo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Text(
                            text = "Belum Ada Goal Tabungan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Mari mulai menyisihkan uang Anda! Buat target tabungan kustom untuk kebutuhan masa depan (misal: Beli Laptop, Traveling).",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(savings, key = { it.id }) { saving ->
                    SavingItem(
                        saving = saving,
                        onAddMoney = {
                            selectedSavingForTx = saving
                            txType = "TOPUP"
                        },
                        onWithdrawMoney = {
                            selectedSavingForTx = saving
                            txType = "WITHDRAW"
                        },
                        onDeleteGoal = {
                            viewModel.deleteSaving(saving)
                            Toast.makeText(context, "Goal '${saving.title}' berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Modal dialog to Add a New Savings Target
    if (showAddDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var targetAmtStr by remember { mutableStateOf("") }
        var initialAmtStr by remember { mutableStateOf("0") }
        var goalNotes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tambah Goal Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Nama Target Tabungan") },
                        placeholder = { Text("Misal: Liburan ke Bali") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = targetAmtStr,
                        onValueChange = { targetAmtStr = it },
                        label = { Text("Target Nominal (Rp)") },
                        placeholder = { Text("Masukan jumlah target") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = initialAmtStr,
                        onValueChange = { initialAmtStr = it },
                        label = { Text("Tabungan Awal (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = goalNotes,
                        onValueChange = { goalNotes = it },
                        label = { Text("Catatan / Keterangan") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                val targetAmt = targetAmtStr.toDoubleOrNull() ?: 0.0
                                val initialAmt = initialAmtStr.toDoubleOrNull() ?: 0.0
                                if (goalTitle.isEmpty()) {
                                    Toast.makeText(context, "Mohon masukan nama goal!", Toast.LENGTH_SHORT).show()
                                } else if (targetAmt <= 0) {
                                    Toast.makeText(context, "Mohon masukan target nominal yang valid!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.addSaving(goalTitle, targetAmt, initialAmt, goalNotes)
                                    showAddDialog = false
                                    Toast.makeText(context, "Goal tabungan berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Simpan Goal")
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue handles Add/Withdraw Money Action
    selectedSavingForTx?.let { saving ->
        var amountString by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { selectedSavingForTx = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (txType == "TOPUP") "Tambah Tabungan" else "Tarik Tabungan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Goal: ${saving.title} (Saat ini: ${formatCurrency(saving.currentAmount)})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = amountString,
                        onValueChange = { amountString = it },
                        label = { Text("Jumlah Uang (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { selectedSavingForTx = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                val amount = amountString.toDoubleOrNull() ?: 0.0
                                if (amount <= 0) {
                                    Toast.makeText(context, "Silakan isi jumlah yang valid", Toast.LENGTH_SHORT).show()
                                } else {
                                    if (txType == "TOPUP") {
                                        viewModel.topUpSaving(saving, amount)
                                        // Auto log saving as expense for balance adjustment!
                                        viewModel.addTransaction(
                                            title = "Setoran Tabungan: ${saving.title}",
                                            amount = amount,
                                            type = "PENGELUARAN",
                                            category = "Tabungan",
                                            date = System.currentTimeMillis(),
                                            notes = "Dimasukan ke pos goal tabungan '${saving.title}'"
                                        )
                                        Toast.makeText(context, "Tabungan didepositkan!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        if (amount > saving.currentAmount) {
                                            Toast.makeText(context, "Error: Saldo tabungan untuk target ini tidak mencukupi!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.withdrawSaving(saving, amount)
                                            // Auto log withdrawal as income for balance adjustment!
                                            viewModel.addTransaction(
                                                title = "Penarikan Tabungan: ${saving.title}",
                                                amount = amount,
                                                type = "PEMASUKAN",
                                                category = "Tabungan",
                                                date = System.currentTimeMillis(),
                                                notes = "Ditarik dari pos goal tabungan '${saving.title}'"
                                            )
                                            Toast.makeText(context, "Tabungan ditarik kembali!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    selectedSavingForTx = null
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Konfirmasi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavingItem(
    saving: Saving,
    onAddMoney: () -> Unit,
    onWithdrawMoney: () -> Unit,
    onDeleteGoal: () -> Unit
) {
    val progressRatio = if (saving.targetAmount > 0) (saving.currentAmount / saving.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val percentStr = String.format("%.0f%%", progressRatio * 100)

    val progressColor = when {
        progressRatio >= 1.0f -> Color(0xFF10B981) // Complete emerald
        progressRatio >= 0.5f -> MaterialTheme.colorScheme.primary // Blue-600
        else -> Color(0xFFF59E0B) // Amber warning
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = saving.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (saving.notes.isNotEmpty()) {
                        Text(
                            text = saving.notes,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteGoal,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Hapus Goal",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Target Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Terkumpul",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(saving.currentAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = progressColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target Goal",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(saving.targetAmount),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Visual Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = percentStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = progressColor
                    )
                    if (saving.targetAmount > saving.currentAmount) {
                        Text(
                            text = "Kekurangan: ${formatCurrency(saving.targetAmount - saving.currentAmount)}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Target Tercapai! 🎉",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { progressRatio },
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )
            }

            // Control deposit/withdrawal buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onWithdrawMoney,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Tarik Uang", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tarik", fontSize = 12.sp)
                }

                Button(
                    onClick = onAddMoney,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = progressColor)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Simpan Uang", modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
