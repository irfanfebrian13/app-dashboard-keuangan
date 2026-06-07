package com.example.data.repository

import com.example.data.local.CategoryDao
import com.example.data.local.SavingDao
import com.example.data.local.TransactionDao
import com.example.data.model.Category
import com.example.data.model.Saving
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val savingDao: SavingDao,
    private val categoryDao: CategoryDao
) {
    // Transactions
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteById(id: Int) {
        transactionDao.deleteById(id)
    }

    // Savings
    val allSavings: Flow<List<Saving>> = savingDao.getAllSavings()

    suspend fun insertSaving(saving: Saving) {
        savingDao.insertSaving(saving)
    }

    suspend fun updateSaving(saving: Saving) {
        savingDao.updateSaving(saving)
    }

    suspend fun deleteSaving(saving: Saving) {
        savingDao.deleteSaving(saving)
    }

    suspend fun deleteSavingById(id: Int) {
        savingDao.deleteById(id)
    }

    // Categories
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    suspend fun countCategories(): Int {
        return categoryDao.countCategories()
    }

    suspend fun deleteAllTransactions() = transactionDao.deleteAllTransactions()

    suspend fun deleteAllSavings() = savingDao.deleteAllSavings()

    suspend fun deleteAllCategories() = categoryDao.deleteAllCategories()
}
