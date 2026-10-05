package com.whitbread.premierinn.alternativeroomselection

import com.whitbread.premierinn.common.ParcelableDailyRate
import com.whitbread.premierinn.domain.common.PriceDomain

data class SelectedTwinRooms(val roomNumber: Int, val lettingType: String, val price: PriceDomain, val dailyRates: List<ParcelableDailyRate>)