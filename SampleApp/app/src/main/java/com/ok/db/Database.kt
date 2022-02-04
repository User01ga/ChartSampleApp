package com.ok.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ok.db.entity.Transaction

/**
 * Created by Olga Kuzmina.
 */
@Database(entities = [Transaction::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}