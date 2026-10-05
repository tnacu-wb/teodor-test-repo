package com.whitbread.premierinn.domain.graphql.guestDetails.entity

import com.whitbread.premierinn.domain.common.AddressShort

data class PartialAddressDomain(
    val partialAddress: List<AddressShort>
)
