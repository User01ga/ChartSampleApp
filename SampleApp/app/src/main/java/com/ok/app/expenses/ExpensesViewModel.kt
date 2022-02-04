package com.ok.app.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ok.model.ExpensesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Olga Kuzmina.
 */
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val repository: ExpensesRepository
) : ViewModel() {

    val expensesAmount = repository.expensesAmount
    val expensesByCategory = repository.expensesByCategory
    val transactions = repository.transactions

    fun loadTransactions() {
        viewModelScope.launch { repository.getAllTransactions() }
    }

    fun loadExpenses() {
        viewModelScope.launch {
            repository.getTotalExpenses()
            repository.getExpensesByCategory()
        }
    }
}