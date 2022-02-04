package com.ok.model

import androidx.lifecycle.MutableLiveData
import com.ok.db.TransactionDao
import com.ok.db.entity.CategoryWithAmount
import com.ok.db.entity.Transaction
import javax.inject.Inject

/**
 * Created by Olga Kuzmina.
 */
class ExpensesRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    val transactions = MutableLiveData<List<Transaction>>()
    val expensesByCategory = MutableLiveData<List<CategoryWithAmount>>()
    val expensesAmount = MutableLiveData<Double>()

    suspend fun getAllTransactions() {
        transactions.postValue(transactionDao.getAllTransactions())
    }

    suspend fun getExpensesByCategory() {
        expensesByCategory.postValue(transactionDao.getExpensesByCategory())
    }

    suspend fun getTotalExpenses() {
        expensesAmount.postValue(transactionDao.getTotalExpenses())
    }
}