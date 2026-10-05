package com.whitbread.premierinn.alternativeroomselection

import com.whitbread.premierinn.domain.common.PriceDomain

data class TwinRoomOption(val lettingType: String,
                          val price: PriceDomain,
                          val isTwinRoomOption: Boolean)
