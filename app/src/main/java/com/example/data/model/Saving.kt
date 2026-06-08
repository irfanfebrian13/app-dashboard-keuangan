package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings")
data class Saving(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val notes: String = "",
    val date: Long = System.currentTimeMillis(),
    val holderName: String = ""
)
