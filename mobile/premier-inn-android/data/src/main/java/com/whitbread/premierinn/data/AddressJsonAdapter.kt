package com.whitbread.premierinn.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.whitbread.premierinn.data.remote.ApiCommon
import java.lang.reflect.Type

class AddressJsonAdapter : JsonSerializer<ApiCommon.Address> {

    override fun serialize(src: ApiCommon.Address, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            addProperty("line1", src.line1)
            addProperty("line2", src.line2)
            addProperty("line3", src.line3)
            addProperty("line4", src.line4)
            addProperty("line5", src.line5)
            addProperty("postcode", src.postcode)
            addProperty("postCode", src.postcode)
            addProperty("type", src.type)
            addProperty("companyName", src.companyName)
            addProperty("countryCode", src.countryCode)
        }
    }
}