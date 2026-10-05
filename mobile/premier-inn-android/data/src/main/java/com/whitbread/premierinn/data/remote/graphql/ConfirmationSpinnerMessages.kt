package com.whitbread.premierinn.data.remote.graphql

import com.google.gson.annotations.SerializedName

data class ConfirmationSpinnerMessages(
        @SerializedName("messages") val messages: List<MessageInfo>) {
    fun getMaxAttempts(): Long {
        var maxattempts = 0L
        messages.forEach { message ->
            maxattempts += message.seconds
        }
        return maxattempts
    }


    data class MessageInfo(
            @SerializedName("order") val order: Int,
            @SerializedName("seconds") val seconds: Long,
            @SerializedName("message") val message: String)
}


