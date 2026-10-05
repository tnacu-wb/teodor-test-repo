package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description(
    "One rate the hotel offers: a rate plan for a room type and occupancy with its nightly " +
        "price. Presence on the first hotel gates the Opera rate-plans and rate-info stubs.",
)
data class Rate(
    @JsonSchema.Description("Opera rate plan code.")
    val ratePlan: String,
    val ratePlanSet: String? = null,
    @JsonSchema.Description(
        "Rate-plan classification display set Opera reports for this plan, e.g. BMD. " +
            "Overrides the rate-plans stub's default classification when present.",
    )
    val displaySet: String? = null,
    @JsonSchema.Description("Promotion attached to the rate. Each such rate gates an Opera rate-plan-info stub.")
    val promotionCode: String? = null,
    @JsonSchema.Description("Base rate plan a promotional rate derives from; requires promotionCode.")
    val dynamicBaseRatePlan: String? = null,
    @JsonSchema.Description("Opera room type code the rate prices.")
    val roomType: String,
    val adults: Int,
    val children: Int = 0,
    val nightlyRate: Double = 59.0,
    @JsonSchema.Description("Whether Opera permits a discount on this reservation rate.")
    val discountAllowed: Boolean = true,
) {
    init {
        require(promotionCode == null || promotionCode.isNotBlank()) {
            "rate.promotionCode must not be blank"
        }
        require(dynamicBaseRatePlan == null || promotionCode != null) {
            "rate.dynamicBaseRatePlan requires promotionCode"
        }
    }
}

@Serializable
@JsonSchema.Description(
    "Packages and donation packages the mocked Opera package endpoints serve for the hotel. " +
        "packages gate the package-group and packages-list stubs; donationPackages gate the " +
        "donation-packages stub.",
)
data class PackageCatalogue(
    val packages: List<PackageDefinition> = emptyList(),
    val donationPackages: List<PackageDefinition> = emptyList(),
)

@Serializable
@JsonSchema.Description("One sellable package in the hotel's catalogue.")
data class PackageDefinition(
    @JsonSchema.Description("Opera package code, e.g. CARPRK.")
    val code: String,
    val description: String,
    val shortDescription: String = description,
    val price: Double,
    val currency: String = "GBP",
    @JsonSchema.Description("How the price is applied, e.g. FLAT_RATE, PER_ROOM, PER_ADULT, PER_PERSON.")
    val calculationRule: String = "FLAT_RATE",
    @JsonSchema.Description("Which nights the charge posts on, e.g. EVERY_NIGHT, ARRIVAL_NIGHT, LAST_NIGHT.")
    val postingRhythm: String = "EVERY_NIGHT",
    @JsonSchema.Description("Inventory article the package consumes, when it is inventory-limited.")
    val inventoryArticleNumber: String? = null,
    @JsonSchema.Description("Stay purposes a city-tax package applies to, e.g. LEI, BUS.")
    val cityTaxPurposes: Set<String> = emptySet(),
    @JsonSchema.Description("Member packages, for composite packages such as meal deals.")
    val composition: PackageComposition? = null,
) {
    init {
        require(code.isNotBlank()) { "package code must not be blank" }
        require(description.isNotBlank()) { "package description must not be blank" }
        require(currency.isNotBlank()) { "package currency must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("The member packages a composite package bundles.")
data class PackageComposition(
    val description: String? = null,
    val members: List<PackageComponent> = emptyList(),
)

@Serializable
@JsonSchema.Description("One member of a composite package.")
data class PackageComponent(
    val code: String,
    val description: String,
) {
    init {
        require(code.isNotBlank()) { "package component code must not be blank" }
        require(description.isNotBlank()) { "package component description must not be blank" }
    }
}
