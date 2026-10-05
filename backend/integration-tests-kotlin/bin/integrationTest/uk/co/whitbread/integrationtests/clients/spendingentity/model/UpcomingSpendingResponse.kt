package uk.co.whitbread.integrationtests.clients.spendingentity.model

import kotlinx.serialization.Serializable

@Serializable
data class UpcomingSpendingResponse(
    val expectedSpendTodayDate: String? = null,
    val expectedSpendToday: Double? = null,
    val expectedNextBillingStartDate: String? = null,
    val expectedNextBillingEndDate: String? = null,
    val expectedNextBilling: Double? = null,
    val expectedNextPeriodStartDate: String? = null,
    val expectedNextPeriodEndDate: String? = null,
    val expectedNextPeriod: Double? = null,
    val currency: String? = null,
    val accountStatus: String? = null,
)
