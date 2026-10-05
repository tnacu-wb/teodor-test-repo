//
//  HotelSearchQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let headerInformationQuery =
    """
    query headerInformation ($language: String!, $country: String!) {
        headerInformation (language: $language, country: $country) {
            content {
                global {
                    addRoom,
                    done,
                    room,
                    roomLabel,
                    single,
                    double,
                    twin,
                    accessible,
                    family,
                    adult,
                    adults,
                    child,
                    children,
                    night,
                    rooms,
                    adultsLabel,
                    childrenLabel,
                    today,
                    tomorrow
                },
                menu {
                    mobileMenuButton,
                    language,
                    business,
                    languageButton,
                    tick,
                    logIn,
                    discoverPI,
                    findBooking,
                    bookHotel,
                    guestAccount,
                    changeLogs,
                    agentMemo
                }
            },
            form {
                childrenHelperText,
                adultsHelperText,
                includeCot,
                cotLimit,
                removeRoom,
                checkout,
                roomType,
                whereEmailLandingPage,
                where
            },
            datePicker {
                reset
            },
            results {
                notifications {
                    groupBookingHeader,
                    groupBookingMessage
                }
            },
            config {
                roomCodes {
                    double,
                    family,
                    accessible,
                    single,
                    twin
                }
            }
        }
    }
    """

    static let roomTypeInformationQueryStandalone =
    """
    query roomTypeInformation ($brand: String!, $language: String!, $country: String!) {
        roomTypeInformation (brand: $brand, language: $language, country: $country) {
            roomTypes {
              roomTypeCode
              roomCategory
              roomLabel
              roomDescription
              roomImage
            }
        }
    }
    """

    static let roomTypeInformationQuery =
    """
        roomTypeInformation (brand: $brand, language: $language, country: $country) {
            roomTypes {
              roomTypeCode
              roomCategory
              roomLabel
              roomDescription
              roomImage
            }
        }
    """

    static func maxRoomsLimitationQuery(channel: Channel) -> String {
        let query = """
            roomsLimitation\(channel.rawValue): globalConfig (channel: \(channel.rawValue)) {
                maxRoomsLim {
                    maxRooms
                    maxRoomsAmend
                }
            }
        """
        return query
    }

    static func maxNightsLimitationQuery(channel: Channel) -> String {
        let query = """
            nightsLimitation\(channel.rawValue): maxNightsLimitation (channel: \(channel.rawValue)) {
                maxNights
            }
        """
        return query
    }

    static func maxArrivalDateLimitationQuery(channel: Channel) -> String {
        let query = """
            arrivalDateLimitation\(channel.rawValue): maxArrivalDateLimitation (channel: \(channel.rawValue)) {
                            maxArrivalDate
            }
        """
        return query
    }

    static let maxRestrictionsCombinedQuery =
    "query businessRestrictionsCombined {" +
    "\n" + restrictionsForChannel() +
    "}"

    static func restrictionsForChannel() -> String {
        let query = Channel.channelsWithRestrictions.reduce("") { result, channel in
            let string = "\n" +
                "\n" + maxRoomsLimitationQuery(channel: channel) +
                "\n" + maxArrivalDateLimitationQuery(channel: channel) +
                "\n" + maxNightsLimitationQuery(channel: channel)
            return result + string
        }

        return query
    }

    static let countriesQuery =
    """
    query countries($language: String!, $country: String!, $site: String!) {
        countries(language: $language, country: $country, site: $site) {
            countries {
                countryCode
                countryCodeLegacy
                countryName
                passportRequired
                nationality
                dialingCode
                flagSrc
            }
        }
    }
    """

    static let hotelInfoBySlugQuery =
     """
     query hotelInformationBySlug($slug: String!, $country: String!, $language: String!) {
         hotelInformationBySlug(slug: $slug, country: $country, language: $language) {
             brand
             name
             hotelId
         }
     }
     """

    static let getCategoryLabelsQuery =
     """
     query categoryLabels ($country: String!, $language: String!, $category: String!, $labels: [String!]) {
       categoryLabels(country: $country, language: $language, category: $category, labels: $labels) {
         labels
       }
     }
     """

    static let getHotelPreferences =
    """
    query getHotelPreferences($hotelId: String!,$preferenceGroupsCodes: String!,$language: String) {
        getHotelPreferences(hotelId: $hotelId,preferenceGroupsCodes:$preferenceGroupsCodes,language:$language) {
            hotelPreferences {
                description
                code
                preferenceGroup
                housekeeping
                orderSequence
                hotelId
                label
            }
        }
    }
    """

    static let resendInvoiceEmailMutation =
    """
    mutation resendInvoiceEmail($resendInvoiceRequest: ResendInvoiceRequest!) {
        resendInvoiceEmail(resendInvoiceRequest: $resendInvoiceRequest)
    }
    """

    static let homepageAppsContent =
    """
    query homepageAppsContent($channel: Channel!, $subchannel: String!, $language: String!, $country: String!) {
        homepageAppsContent(channel: $channel, subchannel: $subchannel, language: $language, country: $country) {
            logo
            heading
            destinationCards {
                imagePath
                imageTag
                title
                subtitle
                linkPath
                openLinkInApp
                order
                trackingId
                latitude
                longitude
            }
            contentCards {
                imagePath
                imageTag
                title
                subtitle
                linkPath
                openLinkInApp
                order
                trackingId
            }
            promoCards {
                imagePath
                imageTag
                title
                subtitle
                linkPath
                openLinkInApp
                order
                trackingId
            }
            notification {
                type
                title
                message
                linkLabel
                linkPath
                openLinkInApp
                dismissible
            }
        }
    }
    """

    static let combinedHotelInfoAndCategoryLabelsQuery =
    """
    query hotelInformation(
        $hotelId: String!,
        $country: String!,
        $language: String!,
        $category: String!,
        $labels: [String!]
    ) {
        hotelInformation(hotelId: $hotelId, country: $country, language: $language) {
            name
            brand
            hotelId
            parkingDescription
            hotelDescription
            directions
            county
            topSectionImages {
                imageSrc
                tags
            }
            headline
            hotelFacilities {
                description
                name
                code
            }
            coordinates {
                latitude
                longitude
            }
            messagingFlag {
                color
                text
            }
            address {
                addressLine1
                addressLine2
                addressLine3
                country
                postalCode
            }
            roomConfiguration {
                tabItems {
                    roomType
                    roomName
                    roomDescription
                    facilities {
                        code
                        name
                        description
                        weight
                        icon
                        isVisible
                    }
                    images {
                        imageSrc
                        thumbnailSrc
                        alt
                        caption
                        iconSrc
                    }
                }
                tabGroups {
                    groupId
                    groupName
                }
            }
            restaurant {
                name
                description
                logoSrc
                menus {
                    name
                    description
                    disclaimer
                    menuSrc
                }
            }
            contactDetails {
                phone
                hotelNationalPhone
                email
            }
            announcement {
                showAnnouncement
                startDate
                endDate
                text
            }
            importantInfo {
                infoItems {
                    text
                    priority
                    startDate
                    endDate
                }
            }
            paymentCodeTypes {
                code
                name
            }
            ancillaryCloseout {
                items {
                    startDate
                    endDate
                    serviceCode
                    upsellCodes
                }
            }
            tripAdvisorReviews {
                awards {
                    awardType
                    year
                    image
                }
                rating
                subRatings {
                    ratingImageUrl
                    value
                    localisedName
                }
                numberOfReviews
            }
        }
        categoryLabels(country: $country, language: $language, category: $category, labels: $labels) {
            labels
        }
    }
    """
}
