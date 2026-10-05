package com.whitbread.premierinn.domain.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import java.util.Base64
import java.util.logging.Logger

fun Reservation.nights() = nightCount(this.arrival, this.departure)

fun Pair<LocalDate, LocalDate>.nightsCount() = nightCount(this.first, this.second)

private fun nightCount(arrivalDate: LocalDate,
                       departureDate: LocalDate): Int = Period.between(arrivalDate, departureDate).days

fun latLongConcatenated(latitude: String, longitude: String): String {
    return latitude.plus(",").plus(longitude)
}

fun String.getLastNChars(numberOfChars: Int): String {
    return this.takeLast(numberOfChars)
}

// Extension method to decode JWT token and extract payload using Gson
fun String.decodeJwt(): Map<String, Any> {
    val log = Logger.getLogger("JWTDecoder")
    return try {
        val parts = this.split(".")
        require(parts.size == 3) { "Invalid JWT structure" }

        val payload = parts[1].takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("Empty payload")

        val decodedBytes = Base64.getUrlDecoder().decode(payload)
        val payloadJson = decodedBytes.toString(Charsets.UTF_8)

        val type = object : TypeToken<Map<String, Any>>() {}.type
        Gson().fromJson(payloadJson, type) ?: emptyMap()

    } catch (e: IllegalArgumentException) {
        log.warning("Invalid JWT: ${e.message}")
        emptyMap()
    } catch (e: Exception) {
        log.warning("Unexpected error: ${e.javaClass.simpleName} - ${e.message}")
        emptyMap()
    }
}