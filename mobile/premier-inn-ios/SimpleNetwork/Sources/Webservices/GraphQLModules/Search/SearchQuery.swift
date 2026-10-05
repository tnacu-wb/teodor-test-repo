//
//  SearchQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let availabilitiesQuery =
    """
    query hotelAvailabilities($availabilitiesSearchCriteria: AvailabilitiesSearchCriteria!) {
        hotelAvailabilities(availabilitiesSearchCriteria: $availabilitiesSearchCriteria) {
            total
            page
            multiHotelAvailabilities {
                hotelId
                name
                hotelAvailability {
                    distance
                    available
                    limitedAvailability
                    pmsSource
                    cellCode
                    lowestRoomRate {
                        netTotal
                        currencyCode
                    }
                }
                hotelInformation {
                    brand
                    thumbnailImages {
                        imageSrc
                        tags
                    }
                    hotelFacilities {
                        code
                        name
                        description
                    }
                    messagingFlag {
                        text
                        color
                    }
                    coordinates {
                        latitude
                        longitude
                    }
                }
            }
        }
    }
    """

    static let availabilityAndPackagesQuery =
    """
    query hotelAvailability ($availabilitySearchCriteria: AvailabilitySearchCriteria!, $language: String!, $country: String!, $hotelId: String!, $brand: String!, $channel: String!, $channelEnum: Channel!, $ratePlans: [String]) {
    \(availabilityQuery)
    \(roomClassConfigQuery)
    \(ratesInformationQuery)
    \(roomTypeInformationQuery)
    }
    """

    static let availabilityQuery =
    """
    hotelAvailability (availabilitySearchCriteria: $availabilitySearchCriteria) {
        hotelId
        startDate
        endDate
        available
        limitedAvailability
        roomRates {
            ratePlanCode
            cellCode
            promotionCode
            roomTypes {
                roomType
                adults
                children
                cotRequested
                rooms {
                    pmsRoomType
                    silentSubstitution
                    roomClass
                    cotAvailable
                    specialRequests
                    numberOfRoomsAvailable
                    roomPriceBreakdown {
                        totalNetAmount
                        baseRateAmount
                        currencyCode
                        packageCode
                        packageAmount
                        dailyPrices {
                            date
                            netPrice
                        }
                    }
                }
            }
        }
    }
    """

    static let getPackagesStandaloneQuery =
    "query packages ($packagesCriteria: PackagesCriteria!, $bookingFlowCriteria: BookingFlowCriteria!) {" +
    "\n" + getPackagesQuery +
    "\n" + donationsQuery +
    "\n" +
    "}"

    static let getPackagesQuery =
    """
    packages (packagesCriteria: $packagesCriteria) {
        packages {
            meals {
                name
                id
                bartId
                price
                freeBreakfastCode
                freeBreakfastMaxPerMeal
                imageSrc
                shortDescription
                allergyInfoSrc
                allergyInfoLabel
                currency
                order
                menu {
                    name
                    description
                    disclaimer
                    menuSrc
                }
            }
            mealsKids {
                name
                id
                imageSrc
                description
                allergyInfoSrc
                allergyInfoLabel
                price
                currency
                order
                menu {
                    name
                    description
                    disclaimer
                    menuSrc
                }
            }
            extrasItems {
                name
                id
                description
                price
                imageSrc
                currency
                order
                available
            }
            roomSelection {
                reservationId
                packagesSelection {
                    id
                    noOfSelections
                }
            }
        }
        hotelHasCityTaxForLeisure
        hotelHasCityTaxForBusiness
    }
    """

    static let donationsQuery =
    """
    donations(bookingFlowCriteria: $bookingFlowCriteria) {
        description
        imageSrc
        name
        donationPackages {
            code
            currency
            unitPrice
        }
    }
    """

    static let roomClassConfigQuery =
    """
        globalConfig (channel: $channelEnum, brand: $brand, country: $country, language: $language) {
            roomClassConfig {
                code
                order
            }
        }
    """

    static let ratesInformationQueryStandalone =
    """
    query ratesInformation($brand: String!, $language: String!, $country: String!, $hotelId: String!, $channel: String, $ratePlans: [String]) {
        ratesInformation: ratesInformationV2(brand: $brand, language: $language, country: $country, hotelId: $hotelId, channel: $channel, ratePlans: $ratePlans) {
            rateClassifications {
                rateClassification
                rateOrder
                rateName
                rateDescription
                rateLongDescription
                rateNotes
                rateTags
            }
        }
    }
    """

    static let ratesInformationQuery =
    """
    ratesInformation: ratesInformationV2(brand: $brand, language: $language, country: $country, hotelId: $hotelId, channel: $channel, ratePlans: $ratePlans) {
        rateClassifications {
            rateClassification
            rateOrder
            rateName
            rateDescription
            rateLongDescription
            rateNotes
            rateTags
        }
    }
    """
}
