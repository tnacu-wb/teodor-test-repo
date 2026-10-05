package com.whitbread.premierinn.domain.graphql.amend.entity

import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain

data class PackagesAndAncillaryCloseoutDomain(
    val packages: DataPackagesDomain,
    val ancillaryCloseOutItems: List<AncillaryCloseOutItem>
)