package com.whitbread.premierinn.common.analytics

data class GenericAnalyticsData(val extraArguments: MutableMap<String, String>) : AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        return extraArguments
    }
}

fun analyticsDataOf(vararg pairs: Pair<String, String>): AnalyticsData {
    return GenericAnalyticsData(pairs.toMap().toMutableMap())
}
