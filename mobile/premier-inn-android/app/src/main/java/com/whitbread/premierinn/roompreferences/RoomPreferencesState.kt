package com.whitbread.premierinn.roompreferences

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.customer.entity.Customer

data class RoomPreferencesState(
        val customer: AsyncResult<Customer>? = null,
        private val roomCriteriaUpdateResult: AsyncResult<RoomCriteria>) {

    val isLoading = customer is AsyncResult.Loading

    val roomConfiguration: RoomCriteria?
        get() = if (customer is AsyncResult.Success && customer.data != null) {
            customer.data.bookingPreferences?.roomCriteriaPreference
        } else null

    val getCustomer: Customer?
        get() = if (customer is AsyncResult.Success && customer.data != null) {
            customer.data
        } else null
}
