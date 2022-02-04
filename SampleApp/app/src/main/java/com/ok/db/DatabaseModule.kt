package com.ok.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Created by Olga Kuzmina.
 */

private const val DB_FILE_NAME = "sampleapp.db"
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): Database {
        return Room.databaseBuilder(appContext, Database::class.java, DB_FILE_NAME)
            .createFromAsset(DB_FILE_NAME)
            .build()
    }

    @Provides
    fun provideTransactionDao(appDatabase: Database): TransactionDao {
        return appDatabase.transactionDao()
    }
}