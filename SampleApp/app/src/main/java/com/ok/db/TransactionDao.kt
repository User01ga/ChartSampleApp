package com.ok.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ok.db.entity.CategoryWithAmount
import com.ok.db.entity.Transaction

/**
 * Created by Olga Kuzmina.
 */
@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(item: Transaction)

    @Query("SELECT * FROM transactions WHERE category = :category")
    suspend fun getTransactions(category: String): List<Transaction>

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT SUM(amount) as total, category FROM transactions WHERE incoming = 0 GROUP BY category")
    suspend fun getExpensesByCategory(): List<CategoryWithAmount>

    @Query("SELECT SUM(amount) FROM transactions WHERE incoming = 0")
    suspend fun getTotalExpenses(): Double
}