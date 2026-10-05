package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Global booking configuration the mocked AEM global-config endpoint serves. Presence gates the global-config stub.")
data class AemGlobalConfig(
    val country: String,
    val language: String,
    val site: AemSite,
    val brand: String,
    val bookingWidgetConfig: AemBookingWidgetConfig,
    val roomClassConfig: List<AemRoomClassOrder> = emptyList(),
    val offers: List<AemGlobalConfigOffer> = emptyList(),
    val roomUpgradeOptions: AemRoomUpgradeOptions? = null,
    val promotionsConfig: AemPromotionsConfig? = null,
    val priceFinderConfig: AemPriceFinderConfig? = null,
    val hotelsWithCityTax: List<String> = emptyList(),
    val upsellItemsExtras: List<AemUpsellItemExtra> = emptyList(),
) {
    init {
        require(country.isNotBlank()) { "globalConfig country must not be blank" }
        require(language.isNotBlank()) { "globalConfig language must not be blank" }
        require(brand.isNotBlank()) { "globalConfig brand must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Booking-widget limits: room counts, night counts, and allowed room types per occupancy.")
data class AemBookingWidgetConfig(
    val maxRooms: Int,
    val maxRoomsAmend: Int,
    val numberOfNights: Int,
    val maxArrivalDate: Int,
    val allowedRoomTypesByOccupancy: List<AemAllowedRoomTypesByOccupancy> = emptyList(),
)

@Serializable
@JsonSchema.Description("Room types the widget accepts for one adults/children combination.")
data class AemAllowedRoomTypesByOccupancy(
    val acceptedRoomTypes: List<String>,
    val adultsNumber: Int,
    val childrenNumber: Int,
) {
    init {
        require(acceptedRoomTypes.isNotEmpty()) {
            "globalConfig.acceptedRoomTypesByOccupancy.acceptedRoomTypes must not be empty"
        }
    }
}

@Serializable
@JsonSchema.Description("Display order and upgrade options for one room class.")
data class AemRoomClassOrder(
    val code: String,
    val order: Int,
    val availableUpgrades: List<String> = emptyList(),
) {
    init {
        require(code.isNotBlank()) { "globalConfig.roomClassConfig.code must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Per-page overrides of the booking-widget limits.")
data class AemGlobalConfigOffer(
    val page: String,
    val maxRooms: Int? = null,
    val maxRoomsAmend: Int? = null,
    val numberOfNights: Int? = null,
    val maxArrivalDate: Int? = null,
    val allowedRoomTypesByOccupancy: List<AemAllowedRoomTypesByOccupancy> = emptyList(),
) {
    init {
        require(page.isNotBlank()) { "globalConfig.offers.page must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Room-upgrade prompt copy and the upgrades on offer.")
data class AemRoomUpgradeOptions(
    val priceText: String,
    val primaryButtonText: String,
    val secondaryButtonText: String,
    val roomUpgrades: List<AemRoomUpgrade> = emptyList(),
) {
    init {
        require(priceText.isNotBlank()) { "globalConfig.roomUpgradeOptions.priceText must not be blank" }
        require(primaryButtonText.isNotBlank()) {
            "globalConfig.roomUpgradeOptions.primaryButtonText must not be blank"
        }
        require(secondaryButtonText.isNotBlank()) {
            "globalConfig.roomUpgradeOptions.secondaryButtonText must not be blank"
        }
    }
}

@Serializable
@JsonSchema.Description("One room upgrade offer with its display content.")
data class AemRoomUpgrade(
    val roomClass: String,
    val heading: String,
    val imageUrl: String,
    val description: String,
) {
    init {
        require(roomClass.isNotBlank()) { "globalConfig.roomUpgrades.roomClass must not be blank" }
        require(heading.isNotBlank()) { "globalConfig.roomUpgrades.heading must not be blank" }
        require(imageUrl.isNotBlank()) { "globalConfig.roomUpgrades.imageUrl must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Promotion-code box copy and meta-promo rate mappings.")
data class AemPromotionsConfig(
    val promoBox: AemPromoBox? = null,
    val metaPromoRateMapping: List<AemMetaPromoRateMapping> = emptyList(),
)

@Serializable
@JsonSchema.Description("Promotion-code box copy for each outcome state.")
data class AemPromoBox(
    val title: String,
    val button: String,
    val whenInvalid: String,
    val whenMultipleRedeem: String,
    val whenSuccess: String,
    val whenEmpty: String,
    val whenCodeExpired: String,
    val whenUnavailable: String,
    val whenCodeAlreadyApplied: String,
)

@Serializable
@JsonSchema.Description("Maps a meta-search rate to its promotion code.")
data class AemMetaPromoRateMapping(
    val rate: String,
    val code: String,
) {
    init {
        require(rate.isNotBlank()) { "globalConfig.metaPromoRateMapping.rate must not be blank" }
        require(code.isNotBlank()) { "globalConfig.metaPromoRateMapping.code must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Price-finder landing page views.")
data class AemPriceFinderConfig(
    val priceFinderViews: List<AemPriceFinderView> = emptyList(),
)

@Serializable
@JsonSchema.Description("One price-finder view identified by its path.")
data class AemPriceFinderView(
    val path: String,
    val bannerHeadline: String? = null,
    val locationName: String? = null,
) {
    init {
        require(path.isNotBlank()) { "globalConfig.priceFinderViews.path must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Upsell copy linking a package to its promotional variant.")
data class AemUpsellItemExtra(
    val promoText: String,
    val packageCode: String,
    val promoPackageCode: String,
) {
    init {
        require(promoText.isNotBlank()) { "globalConfig.upsellItemsExtras.promoText must not be blank" }
        require(packageCode.isNotBlank()) { "globalConfig.upsellItemsExtras.packageCode must not be blank" }
        require(promoPackageCode.isNotBlank()) {
            "globalConfig.upsellItemsExtras.promoPackageCode must not be blank"
        }
    }
}
