package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.upsellavailable.UpsellAttachmentEntity
import java.lang.reflect.Type

class UpsellAttachmentsListConverter {

    @TypeConverter
    fun fromAttachmentList(attachment: List<UpsellAttachmentEntity>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<UpsellAttachmentEntity>>() {}.type
        return gson.toJson(attachment, type)
    }
    @TypeConverter
    fun toAttachmentList(attachment: String): List<UpsellAttachmentEntity> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<UpsellAttachmentEntity>>() {}.type
        return gson.fromJson(attachment, type)
    }
}