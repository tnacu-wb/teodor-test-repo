package com.whitbread.premierinn.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.InputStream

fun replaceJSONTemplate(javaClassInstance: Class<*>, filePath: String, rateType: String,
                        hotelCode: String, confirmationNumber: String, totalPrice: String): String {
    return javaClassInstance.resourceToString(filePath)
            .replace("<HotelCode>", hotelCode)
            .replace("<RateType>", rateType)
            .replace("<ConfirmationNumber>", confirmationNumber)
            .replace("<TotalPrice>", totalPrice.substring(1))
}

fun replaceJSONTemplate(javaClassInstance: Class<*>, filePath: String, yearsFromNow: Long): String {
    val yearYY = LocalDate.now().plusYears(yearsFromNow).format(DateTimeFormatter.ofPattern("YY"))
    return javaClassInstance.resourceToString(filePath)
            .replace("<YY>", yearYY)
}

fun Class<*>.resourceToString(filePath: String) : String {
    val inputStream: InputStream = getResourceAsStream(filePath) as InputStream
    return inputStream.bufferedReader().use { it.readText() }
}