package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.FormattedAddressGraphQLContract
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.FormattedAddressDomain

fun FormattedAddressGraphQLContract.FormattedAddressData.mapToFormattedAddressGQL(): FormattedAddressDomain {
    return FormattedAddressDomain(
        formattedAddress = this.data.formattedAddress.toAddressDomain()
    )
}

private fun FormattedAddressGraphQLContract.FormattedAddress.toAddressDomain(): Address {
    return Address(
        line1 = this.addressLine1,
        line2 = this.addressLine2 ,
        line3 = this.addressLine3,
        line4 = this.addressLine4,
        line5 = this.addressLine5,
        companyName = this.companyName,
        countryCode = this.country,
        postCode = this.postalCode,
    )
}