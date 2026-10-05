package com.whitbread.premierinn.domain.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

const val ADULT_AGE_INT = 18

fun isAdult(dateOfBirth: String): Boolean {
    return try {
        val dob = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateOfBirth)
        val adultDob = Calendar.getInstance().apply {
            add(Calendar.YEAR, -ADULT_AGE_INT)
        }.time
        dob != null && dob <= adultDob
    } catch (e: Exception) {
        false
    }
}

fun isValidBirthDate(dateOfBirth: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val dob = LocalDate.parse(dateOfBirth, formatter)
        val today = LocalDate.now()
        !dob.isAfter(today)
    } catch (e: Exception) {
        false
    }
}