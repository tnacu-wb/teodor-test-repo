package com.whitbread.premierinn.ciol.entity.upsells

import android.os.Parcelable
import com.whitbread.premierinn.domain.graphql.hdp.entity.MenuDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuUiModel(
    val name: String,
    val menuSrc: String
) : Parcelable

fun MenuDomain.convertToUiModel() = MenuUiModel(
    name = name,
    menuSrc = menuSrc,
)
