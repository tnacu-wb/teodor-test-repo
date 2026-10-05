//
//  BookingInfoQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let bookingConfirmationOnlyQuery =
    """
    query bookingConfirmation($basketReference: String!, $country: String!, $language: String!, $bookingChannel: String) {
    \(bookingConfirmation)
    }
    """

    static let bookingConfirmationWithManageBookingQuery =
    """
    query bookingConfirmation($basketReference: String!, $country: String!, $language: String!, $bookingChannel: String, $cancelInformationCriteria: CancelInformationCriteria!) {
    \(bookingConfirmation)
    \(manageBooking)
    }
    """

    static let bookingConfirmation =
    """
        bookingConfirmation(basketReference: $basketReference, country: $country, language: $language, bookingChannel: $bookingChannel) {
            balanceOutstanding
            totalCost
            hotelId
            currencyCode
            bookingFlowId
            bookingReference
            basketStatus
            isThirdPartyBooking
            paymentOption
            upsellsAddonsEnabled
            reservationByIdList {
                reservationId
                reservationStatus
                preCheckInStatus
                deRegCardCompleted
                reservationGuestList {
                    nameTitle
                    givenName
                    surName
                    additionalDetails {
                        dob
                        passportNumber
                        nationality
                    }
                    isAccompanyingGuest
                    profileId
                    address {
                        addressType
                        addressLine1
                        addressLine2
                        addressLine3
                        addressLine4
                        postalCode
                        cityName
                        countryCode
                        addressId
                    }
                }
                roomStay {
                    adultsNumber
                    childrenNumber
                    roomNumber
                    cot
                    roomType
                    ratePlanCode
                    arrivalDate
                    departureDate
                    roomPrice
                    rateExtraInfo {
                        rateName
                        rateDescription
                    }
                    roomExtraInfo {
                        roomName
                        groupId
                    }
                }
                billing {
                    address {
                        addressLine1
                        addressLine2
                        addressLine3
                        addressLine4
                        country
                        postalCode
                    }
                    email
                    telephone
                    firstName
                    lastName
                    title
                }
                reservationPackageList {
                    packageCode
                    description
                    unitPrice
                    totalQuantity
                    computedPrice
                }
                preferences {
                    code
                    preferenceType
              }
            }
        }
    """

    static let manageBooking =
    """
    manageBooking(cancelInformationCriteria: $cancelInformationCriteria){
        isCancellable
        isAmendable
        isCheckInOnlineAvailable
        isCheckOutOnlineAvailable
        isDigitalKey
        ciolErrorLabelKey
    }

    """

    static let cancelReservationMutation =
    """
    mutation cancelReservation($cancellationCriteria: CancellationCriteria!){
        cancelReservation(cancellationCriteria: $cancellationCriteria){
            basketReference
        }
    }

    """

    static let findBookingSourceQuery: String =
    """
    query findBookingSource($findBookingCriteria: FindBookingCriteria!) {
        findBooking(findBookingCriteria: $findBookingCriteria) {
            sourcePms
            ref
            token
            basketReference
            hotelId
            isThirdPartyBooking
        }
    }
    """

    static let getStaysQuery: String =
	"""
	query bookingHistory($bookingHistoryRequest: BookingHistoryRequest!) {
		bookingHistory(bookingHistoryRequest: $bookingHistoryRequest) {
			bookings {
				hotelName
				arrivalDate
				bookingStatus
				bookingReference
				hotelCode
				sourceSystem
				departureDate
				noOfRooms
				customerReference
				historyRecordNumber
				purchaseOrder
				cancelled
				checkInOnline
				checkedIn
				amendable
				guestHistoryNumber
				cancelable
				leadGuest
				leadGuestSurname
				prePaidAmount {
					amount
					currency
				}
				totalCost {
					amount
					currency
				}
				cityTax {
					amount
					currency
				}
				isCheckInOnlineAvailable
				isDigitalKeyEligible
				basketStatus
				hotelCountry
			}
		}
	}
	"""

    static let bookingInformationOnlyQuery =
    """
    query bookingInformation($basketReference: String!, $country: String!, $language: String!, $upgradeToEmployeeRate: Boolean, $bookingChannelCriteria: BookingChannelCriteria!) {
        bookingInformation(basketReference: $basketReference, country: $country, language: $language, upgradeToEmployeeRate: $upgradeToEmployeeRate, bookingChannelCriteria: $bookingChannelCriteria) {
            bookingFlowId
        }
    }
    """

    static let updateReservationPreferencesMutation =
    """
    mutation updateReservationPreferences(
        $hotelId: String!
        $reservationsIds: [String!]
        $preferencesCollections: [PreferencesCollection!]
    ) {
        updateReservationPreferences(
            updateReservationPreferencesRequest: {
                hotelId: $hotelId
                reservationsIds: $reservationsIds
                preferencesCollections: $preferencesCollections
            }
        )
    }
    """
}
