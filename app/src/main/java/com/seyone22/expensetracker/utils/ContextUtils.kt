package com.seyone22.expensetracker.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun Context.getFragmentActivity(): FragmentActivity? {
    return when (this) {
        is FragmentActivity -> this
        is ContextWrapper -> baseContext.getFragmentActivity()
        else -> null
    }
}