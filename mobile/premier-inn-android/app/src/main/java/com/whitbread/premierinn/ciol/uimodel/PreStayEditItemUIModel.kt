package com.whitbread.premierinn.ciol.uimodel

import com.whitbread.premierinn.data.common.EMPTY_STRING

data class PreStayEditItemUIModel(
    val value: String = EMPTY_STRING,
    val isInvalid: Boolean = false,
    val isVisible: Boolean = true
)
