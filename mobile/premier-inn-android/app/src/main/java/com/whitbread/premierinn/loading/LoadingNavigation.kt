package com.whitbread.premierinn.loading

sealed class LoadingNavigation {
    object Loading
    object Proceed
    data class Notification(val data: Map<String, String>)
    object ForceUpdate
    object ForceUpdateNotRequired
}
