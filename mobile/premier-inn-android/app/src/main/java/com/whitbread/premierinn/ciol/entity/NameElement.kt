package com.whitbread.premierinn.ciol.entity

import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType
import com.whitbread.premierinn.ciol.viewmodel.state.utils.InputState
import java.io.Serializable

data class NameElement(
    val map: HashMap<Serializable, String?> = hashMapOf(
        Pair(
            FieldType.UNKNOWN,
            null
        )
    ),
    val dataValidity: InputState? = null
)