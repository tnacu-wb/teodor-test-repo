package uk.co.whitbread.integrationtests.testkit.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** Serializes [LocalDate] values as strict ISO-8601 `YYYY-MM-DD` strings. */
object IsoLocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.LocalDate", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: LocalDate,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val value = decoder.decodeString()
        return try {
            LocalDate.parse(value)
        } catch (failure: DateTimeParseException) {
            throw SerializationException("Invalid ISO-8601 date: '$value'", failure)
        }
    }
}

/** Strict JSON contract shared by Booking contract tests and the local REST adapter. */
val bookingJson: Json =
    Json {
        ignoreUnknownKeys = false
        encodeDefaults = false
        explicitNulls = false
    }
