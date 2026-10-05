package com.whitbread.premierinn.domain.common

import org.threeten.bp.LocalDate


data class Upsell(val quantity: Int,
                  val category: Category,
                  val legend: String,
                  val postingDate: LocalDate,
                  val unitCost: PriceDomain,
                  val code: String,
                  val roomId: String
) {
    //TODO Separate OTHER into GOSH and OTHER (Wifi)
    //Gosh category code is D
    enum class Category {
        BREAKFAST, OTHER
    }
}