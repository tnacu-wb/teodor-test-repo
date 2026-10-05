package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class BookingChannelDetails(
        val channel: String,
        val subchannel: String,
        val language: String
)

fun BookingChannelDetails.toChannelEnum(): Channel {
        return when (channel.uppercase()) {
                Channel.BB.name -> Channel.BB
                Channel.PI.name -> Channel.PI
                else -> Channel.PI
        }
}