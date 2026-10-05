package com.whitbread.premierinn.domain.common

import org.threeten.bp.LocalDate

data class DailyRate(val date: LocalDate, val price: PriceDomain)