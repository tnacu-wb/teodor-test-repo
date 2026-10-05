package com.whitbread.premierinn.ciol.entity

import com.whitbread.premierinn.data.graphql.mapper.TITLE_MR

data class FullNameElement(
    val title: String = TITLE_MR,
    val firstName: NameElement? = null,
    val lastName: NameElement? = null
)
