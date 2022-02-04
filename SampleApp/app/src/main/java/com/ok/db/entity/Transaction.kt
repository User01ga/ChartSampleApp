package com.ok.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Olga Kuzmina.
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val amount: Double, // TODO use BigDecimal
    val currency: String,
    val date: Long,
    val description: String,
    val category: String,
    val incoming: Boolean,
    val icon: String?
)

data class CategoryWithAmount(val total: Double, val category: String)