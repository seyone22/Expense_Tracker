package com.example.expensetracker.data

import android.content.Context
import android.util.Log
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.repository.category.CategoriesRepository
import com.example.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import java.io.BufferedReader
import java.io.InputStreamReader

suspend fun prepopulate(
    context: Context,
    categRepo: CategoriesRepository,
    currencyRepo: CurrencyFormatsRepository
) {
    insertCategories(context, categRepo)
    insertCurrencies(context, currencyRepo)
}


suspend fun insertCategories(context: Context, repository: CategoriesRepository) {
    val list = readCsvCategory(context)

    list.forEach { category ->
        repository.insertIfNotExists(category)
    }
}

fun readCsvCategory(context: Context): List<Category> {
    val inputStream = context.assets.open("prepopulate/CATEGORY_V1.csv")
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val list = mutableListOf<Category>()
    bufferedReader.forEachLine { line ->
        val tokens = line.split(",")
        if (tokens.isNotEmpty()) {
            val entity = Category(
                tokens[0].toInt(),
                tokens[1],
                tokens[2].toInt(),
                tokens[3].toInt()
            )
            list.add(entity)
        }
    }
    return list
}

suspend fun insertCurrencies(context: Context, repository: CurrencyFormatsRepository) {
    val list = readCsvCurrency(context)
    Log.d("TAG", "insertCategories: $list")

    list.forEach { currencyFormat ->
        Log.d("TAG", "insertCategories: $currencyFormat")
        repository.insertCurrencyFormat(currencyFormat)
    }
}

fun readCsvCurrency(context: Context): List<CurrencyFormat> {
    val inputStream = context.assets.open("prepopulate/CURRENCYFORMATS_V1.csv")
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val list = mutableListOf<CurrencyFormat>()
    bufferedReader.forEachLine { line ->
        Log.d("TAG", "insertCategories: $line")
        val tokens = parseLine(line)
        val entity = CurrencyFormat(
            tokens[0].toInt(),
            tokens[1],
            tokens[2],
            tokens[3],
            tokens[4],
            tokens[5],
            tokens[6],
            tokens[7],
            tokens[8].toInt(),
            tokens[9].toDouble(),
            tokens[10],
            tokens[11],
        )
        list.add(entity)
    }
    return list
}


fun parseLine(line: String): List<String> {
    val regex = """,(?=(?:[^"]*"[^"]*")*[^"]*$)""".toRegex()
    return line.split(regex)
}
