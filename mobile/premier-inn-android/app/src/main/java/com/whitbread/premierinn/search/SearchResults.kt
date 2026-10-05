package com.whitbread.premierinn.search

import com.whitbread.premierinn.search.adapter.adapteritem.SearchAdapterItem

data class SearchItemsForQuery(val query: String, val results: List<SearchAdapterItem<*>>)
data class SearchItemsWithoutQuery(val results: List<SearchAdapterItem<*>>)