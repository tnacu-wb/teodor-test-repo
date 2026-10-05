package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description(
    "A company this scenario knows. Presence gates the CDH company-search stub and the " +
        "per-company Opera profile and negotiated-rates stubs.",
)
data class Company(
    val name: String,
    @JsonSchema.Description("Opera corporate ID; numeric.")
    val corpId: String,
    @JsonSchema.Description("CDH company ID.")
    val companyId: String,
    val telephoneNumber: String,
    val profileType: String = "Company",
    @JsonSchema.Description("Opera language code, e.g. E for English.")
    val language: String = "E",
    @JsonSchema.Description("Accounts-receivable number, when the company has one.")
    val arNumber: String? = null,
    val active: Boolean = true,
    val restricted: Boolean = false,
    @JsonSchema.Description("Whether the mocked Opera profile reports negotiated rates for this company.")
    val negotiatedRateEnabled: Boolean = true,
    @JsonSchema.Description(
        "Whether the company's Opera profile carries a CorporateId link. When false, the " +
            "profile-by-id stub omits the CorporateId entry, so profile-by-company-id lookups " +
            "skip the corporate profile read.",
    )
    val corporateIdLinked: Boolean = true,
    val restrictedReason: String = "",
    val address: CompanyAddress,
) {
    init {
        require(name.isNotBlank()) { "company.name must not be blank" }
        require(corpId.isNotBlank()) { "company.corpId must not be blank" }
        require(corpId.toIntOrNull() != null) { "company.corpId must be numeric" }
        require(companyId.isNotBlank()) { "company.companyId must not be blank" }
        require(telephoneNumber.isNotBlank()) { "company.telephoneNumber must not be blank" }
        require(profileType.isNotBlank()) { "company.profileType must not be blank" }
        require(language.isNotBlank()) { "company.language must not be blank" }
        require(arNumber == null || arNumber.isNotBlank()) { "company.arNumber must not be blank when present" }
    }
}

@Serializable
@JsonSchema.Description("Postal address the mocked company profile carries.")
data class CompanyAddress(
    val addressLine1: String,
    val addressLine2: String = "",
    val addressLine3: String = "",
    val addressLine4: String = "",
    val country: String = "GB",
    val postalCode: String,
) {
    init {
        require(addressLine1.isNotBlank()) { "company.address.addressLine1 must not be blank" }
        require(country.isNotBlank()) { "company.address.country must not be blank" }
        require(postalCode.isNotBlank()) { "company.address.postalCode must not be blank" }
    }
}
