package com.whitbread.premierinn.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException


class LocalDateJsonAdapter : TypeAdapter<LocalDate>() {

    companion object {
        val apiDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    override fun write(out: JsonWriter?, value: LocalDate?) {
        out?.value(value?.format(apiDateFormat))
    }

    override fun read(input: JsonReader?): LocalDate? {
        try {

            val token = input?.peek()
            if (token == JsonToken.NULL) {
                input.nextNull()
                return null
            }

            if (token == JsonToken.STRING) {
                return LocalDate.parse(input.nextString(), apiDateFormat)
            }

        } catch (e: IOException) {
            return null
        }
        return null
    }
}