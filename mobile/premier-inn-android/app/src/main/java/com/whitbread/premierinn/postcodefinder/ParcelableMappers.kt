package com.whitbread.premierinn.postcodefinder

import com.whitbread.premierinn.domain.common.Address

fun Address.toParcelable(): ParcelableAddress {
    return ParcelableAddress(
            line1 = line1,
            line2 = line2,
            line3 = line3,
            line4 = line4,
            line5 = line5,
            postcode = postCode,
            companyName = companyName,
            countryCode = countryCode
    )
}

fun ParcelableAddress.toAddress(): Address {
    return Address(
            line1 = line1,
            line2 = line2,
            line3 = line3,
            line4 = line4,
            line5 = line5,
            postCode = postcode,
            companyName = companyName,
            countryCode = countryCode
    )
}