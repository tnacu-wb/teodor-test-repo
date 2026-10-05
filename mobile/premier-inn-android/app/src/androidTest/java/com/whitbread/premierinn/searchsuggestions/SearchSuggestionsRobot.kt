package com.whitbread.premierinn.searchsuggestions

import com.whitbread.premierinn.R
import com.whitbread.premierinn.utils.BaseRobot

class SearchSuggestionsRobot : BaseRobot() {

    fun select(option: String) {
        selectItemFromListAt(R.id.search_search_list, 7)
    }
}