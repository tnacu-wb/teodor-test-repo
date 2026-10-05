package com.whitbread.premierinn.searchresults

import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot

class SearchResultsRobot : BaseRobot() {

    fun chooseHotelResult(pos: Int) {
        selectItemFromListAt(R.id.hotelsRecyclerView, pos)
    }
}