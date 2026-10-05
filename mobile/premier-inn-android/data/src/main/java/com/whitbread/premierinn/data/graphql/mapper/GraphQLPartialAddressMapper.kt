package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.PartialAddressGraphQLContract
import com.whitbread.premierinn.domain.common.AddressShort
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PartialAddressDomain

fun PartialAddressGraphQLContract.PartialAddressData.mapToPartialAddressGQL(postcode: String): PartialAddressDomain {
    return PartialAddressDomain(
        partialAddress = this.data.partialAddress.toAddressDomain(postcode)
    )
}

private fun List<PartialAddressGraphQLContract.PartialAddress>.toAddressDomain(postcode: String): List<AddressShort> {
    val partialAddress = mutableListOf<AddressShort>()
    this.forEach {
        partialAddress.add(
            AddressShort(
                line = it.addressText,
                id = it.id,
                postcode = postcode
            )
        )
    }
    return partialAddress
}
