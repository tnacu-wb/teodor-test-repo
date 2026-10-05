package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface PackagesGraphQLContract {

    data class PackagesData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data(
        @SerializedName("packages") val packages: Packages?
    )

    data class Packages(
        @SerializedName("restaurant") val restaurant: Restaurant,
        @SerializedName("packages") val packages: PackagesPackages?,
        @SerializedName("privacyPolicy") val privacyPolicy: PrivacyPolicy,
        @SerializedName("hotelHasCityTaxForLeisure") val hotelHasCityTaxForLeisure: Boolean,
        @SerializedName("hotelHasCityTaxForBusiness") val hotelHasCityTaxForBusiness: Boolean
    )

    data class Restaurant (
        @SerializedName("logoSrc") val logoSrc: String?,
        @SerializedName("messageHeader") val messageHeader: String? = null,
        @SerializedName("noMealsFound") val noMealsFound: Boolean,
        @SerializedName("messageDescription") val messageDescription: String? = null,
        @SerializedName("restaurantNotFound") val restaurantNotFound: Boolean,
        @SerializedName("menus") val menus: List<Menu>? = null
    )

    data class PackagesPackages (
        @SerializedName("meals") val meals: List<Meal>?,
        @SerializedName("mealsKids") val mealsKids: List<Meal>?,
        @SerializedName("roomSelection") val roomSelection: List<RoomSelection>?,
        @SerializedName("extrasItems") val extrasItems: List<ExtrasItem>?,
    )

    data class Meal (
        @SerializedName("name") val name: String? = null,
        @SerializedName("id") val id: String,
        @SerializedName("bartId") val bartId: String? = null,
        @SerializedName("price") val price: Double? = null,
        @SerializedName("totalPrice") val totalPrice: Double? = null,
        @SerializedName("freeBreakfastOption") val freeBreakfastOption: Boolean? = null,
        @SerializedName("freeBreakfastCode") val freeBreakfastCode: String? = null,
        @SerializedName("freeBreakfastMaxPerMeal") val freeBreakfastMaxPerMeal: Int? = null,
        @SerializedName("imageSrc") val imageSrc: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("shortDescription") val shortDescription: String? = null,
        @SerializedName("allergyInfoSrc") val allergyInfoSrc: String? = null,
        @SerializedName("allergyInfoLabel") val allergyInfoLabel: String? = null,
        @SerializedName("currency") val currency: String? = null,
        @SerializedName("order") val order: Int? = null,
        @SerializedName("menu") val menu: Menu? = null,
    )

    data class ExtrasItem (
        @SerializedName("name") val name: String? = null,
        @SerializedName("id") val id: String,
        @SerializedName("price") val price: Double? = null,
        @SerializedName("imageSrc") val imageSrc: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("currency") val currency: String? = null,
        @SerializedName("order") val order: Int? = null,
        @SerializedName("available") val available: Int? = null)

    data class Menu (
        @SerializedName("name") val name: String,
        @SerializedName("menuSrc") val menuSrc: String,
        @SerializedName("description") val description: String? = null,
        @SerializedName("imageSrc") val imageSrc: String? = null,
        @SerializedName("menuLabel") val menuLabel: String? = null,
        @SerializedName("disclaimer") val disclaimer: String? = null)

    data class RoomSelection (
        @SerializedName("reservationId") val reservationId: String?,
        @SerializedName("packagesSelection") val packagesSelection: List<PackagesSelection>,
    )

    data class PackagesSelection (
        @SerializedName("id") val id: String,
        @SerializedName("noOfSelections") val noOfSelections: Int,
    )

    data class PrivacyPolicy (
        @SerializedName("description") val description: String,
        @SerializedName("linkLabel") val linkLabel: String,
        @SerializedName("moreInfoLabel") val moreInfoLabel: String,
        @SerializedName("moreInfo") val moreInfo: List<MoreInfo>,
        @SerializedName("name") val name: String,
        @SerializedName("linkSrc") val linkSrc: String)

   data class MoreInfo (
        @SerializedName("image") val image: String,
        @SerializedName("description") val description: String)
}