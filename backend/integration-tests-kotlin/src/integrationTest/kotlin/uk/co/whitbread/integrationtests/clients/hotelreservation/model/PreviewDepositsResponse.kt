package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class PreviewDepositsResponse(
    val depositFolios: List<PreviewDepositFolio> = emptyList(),
)

@Serializable
data class PreviewDepositFolio(
    val hotelId: String? = null,
    val reservationId: String? = null,
    val vatRegion: String? = null,
    val charges: List<PreviewDepositCharge> = emptyList(),
)

@Serializable
data class PreviewDepositCharge(
    val transactionCode: String? = null,
    val quantity: Int? = null,
    val reference: String? = null,
    val currencyAmount: DepositCurrencyAmount? = null,
)
