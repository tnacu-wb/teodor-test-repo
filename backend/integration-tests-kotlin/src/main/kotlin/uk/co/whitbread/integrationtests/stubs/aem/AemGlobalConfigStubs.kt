package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.AemAllowedRoomTypesByOccupancy
import uk.co.whitbread.integrationtests.testkit.model.AemBookingWidgetConfig
import uk.co.whitbread.integrationtests.testkit.model.AemGlobalConfig
import uk.co.whitbread.integrationtests.testkit.model.AemGlobalConfigOffer
import uk.co.whitbread.integrationtests.testkit.model.AemMetaPromoRateMapping
import uk.co.whitbread.integrationtests.testkit.model.AemPriceFinderConfig
import uk.co.whitbread.integrationtests.testkit.model.AemPriceFinderView
import uk.co.whitbread.integrationtests.testkit.model.AemPromoBox
import uk.co.whitbread.integrationtests.testkit.model.AemPromotionsConfig
import uk.co.whitbread.integrationtests.testkit.model.AemRoomClassOrder
import uk.co.whitbread.integrationtests.testkit.model.AemRoomUpgrade
import uk.co.whitbread.integrationtests.testkit.model.AemRoomUpgradeOptions
import uk.co.whitbread.integrationtests.testkit.model.AemUpsellItemExtra

const val AEM_GLOBAL_CONFIG_STUB_ID = "booking.aem.global-config"

fun globalConfig(globalConfig: AemGlobalConfig): PlannedStub =
    PlannedStub(
        id = AEM_GLOBAL_CONFIG_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(globalConfigMapping(globalConfig)),
    )

private fun globalConfigMapping(globalConfig: AemGlobalConfig): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url =
                    "/${globalConfig.country.lowercase()}/${globalConfig.language.lowercase()}" +
                        "/content-service.global-config.detail/site/${globalConfig.site.value}" +
                        "/brand/${globalConfig.brand.lowercase()}.json",
            ),
        response =
            jsonResponse(
                body = globalConfigBody(globalConfig),
            ),
    )

private fun globalConfigBody(globalConfig: AemGlobalConfig): String {
    val body =
        buildMap<String, Any?> {
            put("roomClassConfig", globalConfig.roomClassConfig.map(::roomClassOrderBody))
            put("bookingWidgetConfig", bookingWidgetConfigBody(globalConfig.bookingWidgetConfig))
            put("offers", globalConfig.offers.map(::offerBody))
            put("brand", globalConfig.brand)
            put("hotelsWithCityTax", globalConfig.hotelsWithCityTax)
            put("upsellItemsExtras", globalConfig.upsellItemsExtras.map(::upsellItemExtraBody))
            globalConfig.roomUpgradeOptions?.let { put("roomUpgradeOptions", roomUpgradeOptionsBody(it)) }
            globalConfig.promotionsConfig?.let { put("promotionsConfig", promotionsConfigBody(it)) }
            globalConfig.priceFinderConfig?.let { put("priceFinderConfig", priceFinderConfigBody(it)) }
        }

    return Json.encodeToString(JsonElement.serializer(), toJsonElement(body))
}

private fun roomClassOrderBody(roomClassOrder: AemRoomClassOrder): Map<String, Any?> =
    mapOf(
        "code" to roomClassOrder.code,
        "order" to roomClassOrder.order,
        "availableUpgrades" to roomClassOrder.availableUpgrades,
    )

private fun bookingWidgetConfigBody(bookingWidgetConfig: AemBookingWidgetConfig): Map<String, Any?> =
    mapOf(
        "maxRooms" to bookingWidgetConfig.maxRooms,
        "maxRoomsAmend" to bookingWidgetConfig.maxRoomsAmend,
        "numberOfNights" to bookingWidgetConfig.numberOfNights,
        "maxArrivalDate" to bookingWidgetConfig.maxArrivalDate,
        "allowedRoomTypesByOccupancy" to bookingWidgetConfig.allowedRoomTypesByOccupancy.map(::occupancyBody),
    )

private fun occupancyBody(occupancy: AemAllowedRoomTypesByOccupancy): Map<String, Any?> =
    mapOf(
        "acceptedRoomTypes" to occupancy.acceptedRoomTypes,
        "childrenNumber" to occupancy.childrenNumber,
        "adultsNumber" to occupancy.adultsNumber,
    )

private fun offerBody(offer: AemGlobalConfigOffer): Map<String, Any?> =
    mapOf(
        "page" to offer.page,
        "maxRooms" to offer.maxRooms,
        "maxRoomsAmend" to offer.maxRoomsAmend,
        "numberOfNights" to offer.numberOfNights,
        "maxArrivalDate" to offer.maxArrivalDate,
        "allowedRoomTypesByOccupancy" to offer.allowedRoomTypesByOccupancy.map(::occupancyBody),
    )

private fun roomUpgradeOptionsBody(roomUpgradeOptions: AemRoomUpgradeOptions): Map<String, Any?> =
    mapOf(
        "priceText" to roomUpgradeOptions.priceText,
        "primaryButtonText" to roomUpgradeOptions.primaryButtonText,
        "secondaryButtonText" to roomUpgradeOptions.secondaryButtonText,
        "roomUpgrades" to roomUpgradeOptions.roomUpgrades.map(::roomUpgradeBody),
    )

private fun roomUpgradeBody(roomUpgrade: AemRoomUpgrade): Map<String, Any?> =
    mapOf(
        "roomClass" to roomUpgrade.roomClass,
        "heading" to roomUpgrade.heading,
        "imageUrl" to roomUpgrade.imageUrl,
        "description" to roomUpgrade.description,
    )

private fun promotionsConfigBody(promotionsConfig: AemPromotionsConfig): Map<String, Any?> =
    buildMap {
        promotionsConfig.promoBox?.let { put("promoBox", promoBoxBody(it)) }
        put("promoItems", emptyList<Any>())
        put("metaPromoRateMapping", promotionsConfig.metaPromoRateMapping.map(::metaPromoRateMappingBody))
    }

private fun promoBoxBody(promoBox: AemPromoBox): Map<String, Any?> =
    mapOf(
        "title" to promoBox.title,
        "button" to promoBox.button,
        "whenInvalid" to promoBox.whenInvalid,
        "whenMultipleRedeem" to promoBox.whenMultipleRedeem,
        "whenSuccess" to promoBox.whenSuccess,
        "whenEmpty" to promoBox.whenEmpty,
        "whenCodeExpired" to promoBox.whenCodeExpired,
        "whenUnavailable" to promoBox.whenUnavailable,
        "whenCodeAlreadyApplied" to promoBox.whenCodeAlreadyApplied,
    )

private fun metaPromoRateMappingBody(metaPromoRateMapping: AemMetaPromoRateMapping): Map<String, Any?> =
    mapOf(
        "rate" to metaPromoRateMapping.rate,
        "code" to metaPromoRateMapping.code,
    )

private fun priceFinderConfigBody(priceFinderConfig: AemPriceFinderConfig): Map<String, Any?> =
    mapOf(
        "priceFinderViews" to priceFinderConfig.priceFinderViews.map(::priceFinderViewBody),
    )

private fun priceFinderViewBody(priceFinderView: AemPriceFinderView): Map<String, Any?> =
    mapOf(
        "path" to priceFinderView.path,
        "bannerHeadline" to priceFinderView.bannerHeadline,
        "locationName" to priceFinderView.locationName,
    )

private fun upsellItemExtraBody(upsellItemExtra: AemUpsellItemExtra): Map<String, Any?> =
    mapOf(
        "promoText" to upsellItemExtra.promoText,
        "packageCode" to upsellItemExtra.packageCode,
        "promoPackageCode" to upsellItemExtra.promoPackageCode,
    )
