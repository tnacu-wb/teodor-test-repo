package com.whitbread.premierinn.domain.common

data class UpsellAttachment(val path: String?,
                            val label: String?) {

    companion object {
        const val ALLERGY_INFO_LABEL: String = "Allergy info"
    }
}