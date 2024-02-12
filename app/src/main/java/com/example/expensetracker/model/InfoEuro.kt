package com.example.expensetracker.model

import kotlinx.serialization.Serializable

@Serializable
data class InfoEuro (
    val country : String,
    val currency : String,
    val isoA3Code : String,
    val isoA2Code : String,
    val value : Double,
    val comment : String?
)