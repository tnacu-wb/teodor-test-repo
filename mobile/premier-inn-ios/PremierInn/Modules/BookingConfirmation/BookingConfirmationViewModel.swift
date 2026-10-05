//
//  BookingConfirmationViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import PassKit
import MapKit

enum BookingConfirmationRowType: String {
    case info = "BookingConfirmationInfoCell"
    case success = "BookingConfirmationSuccessCell"
    case error = "BookingConfirmationErrorCell"
    case status = "BookingConfirmationStatusCell"
    case hotel = "BookingConfirmationHotelInfoCell"
    case event = "BookingConfirmationEventCell"
    case passExists = "BookingConfirmationPassExistsCell"
    case keyExists = "BookingConfirmationKeyExistsCell"
    case howYourKeyWorks = "BookingConfirmationHowYourKeyWorksCell"
    case tripSummary = "BookingConfirmationTripSummaryCell"
    case priceTotal = "BookingConfirmationTotalPriceCell"
    case priceBreakdown = "BookingConfirmationPriceBreakdownCell"
    case amend = "ManagingBookingActionCell"
    case accessibilityInformation = "BookingConfirmationAccessibilityCell"
    case location = "LocationDetailsCell"
    case resendBanner = "BookingConfirmationResendBannerCell"

    case faq = "FAQActionCell"
    case checkInButton
    case checkOutButton
    case callHotel = "CallHotelCell"
    case checkInOut = "CheckInOutCell"
    case wallet = "AddToWalletCell"
    case addKeyToWallet = "AddToKeyToWalletCell"

    case hotelInfoAlert = "hotelInfoAlertCell"
    case employeeOfferWarning = "employeeOfferWarningCell"
    case checkInOutExtrasInfo = "checkInOutExtrasInfoCell"
    case parkingInformation = "BookingConfirmationParkingCell"
    case instructions = "BookingConfirmationInstructionsCell"
    case keyLimitMessage = "keyLimitMessageCell"
}

enum AppleWalletState {
    case passSaved
    case passCanBeAdded
    case passHidden
}

struct BookingConfirmationRow {
    var rowType: BookingConfirmationRowType
    var title: String?
    var subtitle: String?
}

enum BookingCheckStatus {
    case checkIn
    case checkOut
}

extension BookingConfirmationRow {
    init(rowType: BookingConfirmationRowType) {
        self.rowType = rowType
    }
}

struct BookingConfirmationViewModel {
    let bookingSuccessInfoModel: BookingSuccessInfoViewModel?
    let infoViewModel: InfoViewModel
    let hotelAndReferenceViewModel: HotelAndReferenceViewModel
    let bookingDatesViewModel: BookingDatesViewModel
    let checkInOutExtrasInfoViewModel: CheckInOutExtrasInfoViewModel?
    let tripSummaryViewModel: TripSummaryViewModel
    let employeeOfferWarningViewModel: EmployeeOfferWarningViewModel
    let locationViewModel: LocationViewModel
    let accessibilityViewModel: AccessibilityViewModel?
    let upsellsViewModel: BookedUpsellsViewModel?
    let priceTotalViewModel: PriceTotalViewModel
    let priceBreakdownViewModel: PriceBreakdownViewModel
    let amendViewModel: AmendViewModel
    let callHotelViewModel: CallHotelViewModel
    let hotelInformationAlertViewModel: HotelInformationAlertViewModel
    let errorBannerViewModel: ErrorBannerViewModel?
    let resendInvoiceInfoViewModel: InfoViewModel?

    let shouldShowOutOfDateBanner: Bool
    let shouldShowBannerInfo: Bool
    let shouldShowInfo: Bool
    let bookingCheckStatus: BookingCheckStatus?
    let isCheckInOnlineAvailable: Bool
    let isCheckOutOnlineAvailable: Bool
    let eventExistsInCalendar: Bool
    let shouldShowAppleWallet: Bool
    let shouldShowPassExistsButton: Bool
    let shouldShowAddToCalendar: Bool
    let shouldShowEmployeeOfferBanner: Bool
    let shouldShowParkingInformation: Bool
    let shouldShowKeyLimitMessage: Bool

    let shouldShowAmend: Bool
    let shouldShowHotelInfoAlert: Bool
    let isAmended: Bool

    // Digital Key
    let shouldShowDigitalKey: Bool
    let digitalKeyInWallet: Bool
    let shouldShowDigitalKeyFAQ: Bool
    let faqUrl: URL?
    let digitalKeyFaqUrl: URL?

    // swiftlint:disable:next function_body_length
    static func createFrom(_ bookingConfirmationViewModelParams: BookingConfirmationViewModelParams)
        -> BookingConfirmationViewModel? {
        let hotel = bookingConfirmationViewModelParams.hotel
        let summary = bookingConfirmationViewModelParams.summary
        let passSaved = bookingConfirmationViewModelParams.passSaved
        let isAmended = bookingConfirmationViewModelParams.isAmended

        guard let hotel = hotel else { return nil }

        let bookingCheckStatus = determineBookingCheckStatus(summary: summary)
        let isCheckInOnlineAvailable = bookingConfirmationViewModelParams.isCheckInOnlineAvailable
            && bookingCheckStatus == nil
        let isCheckOutOnlineAvailable = bookingConfirmationViewModelParams.isCheckOutOnlineAvailable
        let shouldShowHotelInfoAlert = summary.amendable && SettingsManager.sharedInstance.shouldRedirectForAmendOpera
        let shouldShowParkingSection = hotel.parkings.isNotEmpty || hotel.parkingDescription != nil

        let bookingSuccessInfoModel = BookingSuccessInfoViewModel.createFrom(stay: summary, isAmended: isAmended)
        let infoViewModel = InfoViewModel.createFrom(summary, isAmended: isAmended)
        let hotelAndReferenceViewModel = createHotelAndReferenceViewModel(
            hotel: hotel,
            summary: summary,
            reservation: bookingConfirmationViewModelParams.reservation
        )
        let bookingDatesViewModel = BookingDatesViewModel.createFrom(summary, hotel: hotel)
        let checkInOutExtrasInfoViewModel = createCheckInOutExtrasInfoViewModel(summary: summary, hotel: hotel)
        let tripSummaryViewModel = createTripSummaryViewModel(summary: summary)
        let employeeOfferWarningViewModel = createEmployeeOfferWarningViewModel()
        let locationViewModel = getLocationViewModel(hotel: hotel)
        let accessibilityViewModel = getAccessibilityViewModel(summary: summary, hotel: hotel)
        let upsellsViewModel = createUpsellsViewModel(bookedUpsells: bookingConfirmationViewModelParams.bookedUpsells)
        let priceTotalViewModel = createPriceTotalViewModel(summary: summary)
        let priceBreakdownViewModel = PriceBreakdownViewModel(
            title: PILocalizedString("bookingConfirmationPriceBreakDown")
        )
        let amendViewModel = AmendViewModel(
            title: PILocalizedString("bookingConfirmationManageBookingTitle"),
            subtitle: PILocalizedString("bookingConfirmationManageBookingSubtitle")
        )
        let callHotelViewModel = createCallHotelViewModel(hotel: hotel)
        let hotelInformationAlertViewModel = createHotelInformationAlertViewModel()
        let eventExistsInCalendar = eventExistsInCalendar(summary: summary)
        let isReservationEditable = isReservationEditable(summary: summary)
        let shouldShowAppleWallet = shouldShowAppleWallet(summary: summary, passSaved: passSaved)
        let shouldShowPassExistsButton = shouldShowPassExistsButton(summary: summary, passSaved: passSaved)
        let shouldShowAddToCalendar = shouldShowAddToCalendar(summary: summary)
        let errorBannerViewModel = createErrorBannerViewModel(isOutOfDate: bookingConfirmationViewModelParams.isOutOfDate)
        let shouldShowEmployeeOfferBanner = shouldShowEmployeeOfferBanner(summary: summary)
        let resendInvoiceModel = createResendInvoiceModel(
            resendInvoiceStatus: bookingConfirmationViewModelParams.resendInvoiceStatus,
            reservation: bookingConfirmationViewModelParams.reservation,
            summary: summary
        )
        let shouldShowDigitalKey = shouldShowDigitalKey(
            summary: summary,
            reservation: bookingConfirmationViewModelParams.reservation
        )
        let shouldShowKeyLimitMessage = shouldShowKeyLimitMessage(summary: summary, hotel: hotel)
        let digitalKeyIsInWallet = summary.userHasPassInWallet == true

        return BookingConfirmationViewModel(
            bookingSuccessInfoModel: bookingSuccessInfoModel,
            infoViewModel: infoViewModel,
            hotelAndReferenceViewModel: hotelAndReferenceViewModel,
            bookingDatesViewModel: bookingDatesViewModel,
            checkInOutExtrasInfoViewModel: checkInOutExtrasInfoViewModel,
            tripSummaryViewModel: tripSummaryViewModel,
            employeeOfferWarningViewModel: employeeOfferWarningViewModel,
            locationViewModel: locationViewModel,
            accessibilityViewModel: accessibilityViewModel,
            upsellsViewModel: upsellsViewModel,
            priceTotalViewModel: priceTotalViewModel,
            priceBreakdownViewModel: priceBreakdownViewModel,
            amendViewModel: amendViewModel,
            callHotelViewModel: callHotelViewModel,
            hotelInformationAlertViewModel: hotelInformationAlertViewModel,
            errorBannerViewModel: errorBannerViewModel,
            resendInvoiceInfoViewModel: resendInvoiceModel,
            shouldShowOutOfDateBanner: bookingConfirmationViewModelParams.isOutOfDate,
            shouldShowBannerInfo: bookingConfirmationViewModelParams.shouldShowBannerInfo,
            shouldShowInfo: bookingConfirmationViewModelParams.shouldShowInfo,
            bookingCheckStatus: bookingCheckStatus,
            isCheckInOnlineAvailable: isCheckInOnlineAvailable,
            isCheckOutOnlineAvailable: isCheckOutOnlineAvailable,
            eventExistsInCalendar: eventExistsInCalendar,
            shouldShowAppleWallet: shouldShowAppleWallet,
            shouldShowPassExistsButton: shouldShowPassExistsButton,
            shouldShowAddToCalendar: shouldShowAddToCalendar,
            shouldShowEmployeeOfferBanner: shouldShowEmployeeOfferBanner,
            shouldShowParkingInformation: shouldShowParkingSection,
            shouldShowKeyLimitMessage: shouldShowKeyLimitMessage,
            shouldShowAmend: isReservationEditable,
            shouldShowHotelInfoAlert: shouldShowHotelInfoAlert,
            isAmended: isAmended,
            shouldShowDigitalKey: shouldShowDigitalKey,
            digitalKeyInWallet: digitalKeyIsInWallet,
            shouldShowDigitalKeyFAQ: summary.isDigitalKeyEnabled,
            faqUrl: Constants.faqUrl,
            digitalKeyFaqUrl: Constants.digitalKeyFaqUrl
        )
    }

    // MARK: - Helper Methods

    private static func determineBookingCheckStatus(summary: Stay) -> BookingCheckStatus? {
        if summary.isPreCheckedIn {
            return .checkIn
        } else if summary.basketStatus == .preCheckedOut {
            return .checkOut
        }
        return nil
    }

    private static func createHotelAndReferenceViewModel(
        hotel: Hotel,
        summary: Stay,
        reservation: Reservation?
    ) -> HotelAndReferenceViewModel {
        HotelAndReferenceViewModel(
            hotelImage: hotel.primaryImages.first,
            hotelName: hotel.name,
            reference: summary.identifier,
            shouldShowResendInvoice: summary.isPast && reservation != nil
        )
    }

    private static func createCheckInOutExtrasInfoViewModel(
        summary: Stay,
        hotel: Hotel
    ) -> CheckInOutExtrasInfoViewModel? {
        guard summary.shouldShowCheckInOutExtrasInfo else { return nil }
        guard let message = checkInOutExtrasInfoMessage(summary: summary, hotel: hotel) else { return nil }

        return CheckInOutExtrasInfoViewModel(
            tag: BookingConfirmationRowType.checkInOutExtrasInfo.rawValue,
            message: message,
            topPadding: ViewConstants.LegacyPadding.small,
            bottomPadding: ViewConstants.LegacyPadding.small,
            style: .info
        )
    }

    private static func checkInOutExtrasInfoMessage(summary: Stay, hotel: Hotel) -> String? {
        let hotelBrand = hotel.brand
        if hotelBrand == .premierInn || hotelBrand == .hub || hotelBrand == .zip {
            return PILocalizedString("bookingConfirmationMessageUKHotel")
        } else if hotelBrand == .premierInnGermany {
            return PILocalizedString("bookingConfirmationMessageGermanHotel")
        }
        return nil
    }

    private static func createTripSummaryViewModel(summary: Stay) -> TripSummaryViewModel {
        TripSummaryViewModel(
            title: PILocalizedString("bookingConfirmationTripTitle"),
            guestsRoomsSummary: summary.roomsCountDescription + ", " + summary.nightsCountDescription
        )
    }

    private static func createEmployeeOfferWarningViewModel() -> EmployeeOfferWarningViewModel {
        EmployeeOfferWarningViewModel(
            tag: BookingConfirmationRowType.employeeOfferWarning.rawValue,
            message: PILocalizedString("bookingConfirmationEmployeeOfferWarningMessage"),
            topPadding: ViewConstants.LegacyPadding.small,
            bottomPadding: ViewConstants.LegacyPadding.small,
            style: .alert
        )
    }

    private static func createUpsellsViewModel(bookedUpsells: BookedUpsells?) -> BookedUpsellsViewModel {
        BookedUpsellsViewModel(
            availableUpsells: bookedUpsells?.availableUpsells,
            unavailableUpsells: bookedUpsells?.unavailableUpsells
        )
    }

    private static func createPriceTotalViewModel(summary: Stay) -> PriceTotalViewModel {
        let outstandingAmountEmpty = SimpleNetwork.Constants.outstandingAmountEmptyOperaCode
        let outstandingAmount = summary.balanceOutstanding?.amount == outstandingAmountEmpty ? nil : summary
            .balanceOutstanding?.localizedValue

        return PriceTotalViewModel(
            outstandingAmount: outstandingAmount,
            totalPrice: summary.totalCost?.localizedValue ?? "n/a",
            rate: summary.rateText
        )
    }

    private static func createCallHotelViewModel(hotel: Hotel) -> CallHotelViewModel {
        let callDescriptionText = hotel.nationalPhoneNumber != nil ?
        PILocalizedString("callHotelButtonTitle") :
        PILocalizedString("callUsButtonTitle")
        return CallHotelViewModel(
            callDescription: callDescriptionText,
            callChargeInformation: PILocalizedString("telephoneNoCost")
        )
    }

    private static func createHotelInformationAlertViewModel() -> HotelInformationAlertViewModel {
        HotelInformationAlertViewModel(
            tag: BookingConfirmationRowType.hotelInfoAlert.rawValue,
            text: PILocalizedString("bookingConfirmationHotelInformationAmendUnavailableMessage"),
            attributedString: nil,
            highlightedText: PILocalizedString("bookingConfirmationHotelInformationAmendUnavailableHighlighted"),
            icon: nil,
            contentBackgroundColor: nil,
            topPadding: ViewConstants.LegacyPadding.small,
            bottomPadding: ViewConstants.LegacyPadding.small,
            style: .info
        )
    }

    private static func eventExistsInCalendar(summary: Stay) -> Bool {
        guard let arrivalDate = summary.arrivalDate else { return false }
        guard let checkoutDate = summary.checkOutDate else { return false }

        let reference = summary.identifier
        return SettingsManager.sharedInstance.calendar(
            containsEventWithBookingRefererence: reference,
            startDate: arrivalDate,
            endDate: checkoutDate
        )
    }

    private static func isReservationEditable(summary: Stay) -> Bool {
        !summary.cancelled && (summary.isAmendAllowed || summary.cancelable == true)
    }

    private static func shouldShowAppleWallet(summary: Stay, passSaved: Bool) -> Bool {
        guard !summary.isDigitalKeyEnabled else { return false }
        guard SettingsManager.sharedInstance.featureAppleWalletPass else { return false }
        guard summary.basketStatus != .preCheckedOut else { return false }
        guard !summary.isPast, !summary.cancelled, !passSaved else { return false }
        return PKAddPassesViewController.canAddPasses()
    }

    private static func shouldShowPassExistsButton(summary: Stay, passSaved: Bool) -> Bool {
        guard !summary.isDigitalKeyEnabled else { return false }
        guard SettingsManager.sharedInstance.featureAppleWalletPass else { return false }
        guard !summary.isPast, !summary.cancelled else { return false }
        return passSaved
    }

    private static func shouldShowAddToCalendar(summary: Stay) -> Bool {
        summary.isBeforeArrivalDate && !summary.cancelled && summary.basketStatus != .preCheckedOut
    }

    private static func createErrorBannerViewModel(isOutOfDate: Bool) -> ErrorBannerViewModel? {
        guard isOutOfDate else { return nil }
        return ErrorBannerViewModel(message: PILocalizedString("bookingConfirmationUnableToLoadLatestInfo"))
    }

    private static func shouldShowEmployeeOfferBanner(summary: Stay) -> Bool {
        summary.rateClassification == SimpleNetwork.Constants.EmployeeOffer.rateClassification
    }

    private static func createResendInvoiceModel(
        resendInvoiceStatus: ResendInvoiceStatus?,
        reservation: Reservation?,
        summary: Stay
    ) -> InfoViewModel? {
        switch resendInvoiceStatus {
        case .notRequested:
            return nil
        case .success:
            return InfoViewModel(
                notificationStyle: .success,
                message: NSAttributedString(string: String(
                    format: PILocalizedString("invoiceResendSuccessful"),
                    reservation?.booker?.emailAddress ?? summary.lastName
                ))
            )
        case .failed:
            return InfoViewModel(
                notificationStyle: .error,
                message: NSAttributedString(string: PILocalizedString("invoiceResendFailed"))
            )
        default:
            return nil
        }
    }

    private static func shouldShowDigitalKey(summary: Stay, reservation: Reservation?) -> Bool {
        summary.isDigitalKeyEnabled && !summary
            .userHasPassInWallet &&
            (summary.isPreCheckedIn || reservation?.rooms.first?.bookingStatus == .checkedIn)
    }

    private static func shouldShowKeyLimitMessage(summary: Stay, hotel: Hotel) -> Bool {
        guard summary.isDigitalKeyEnabled else { return false }
        guard hotel.brand == .premierInnGermany else { return false }
        guard summary.userHasPassInWallet else { return false }
        return true
    }

    private static func getLocationViewModel(hotel: Hotel) -> LocationViewModel {
        LocationViewModel(
            title: PILocalizedString("bookingConfirmationLocationSectionTitle"),
            address: hotel.address?.description,
            parkingImage: hotel.parkings.first?.image,
            parkingInfo: hotel.parkings.first?.title,
            hotelAnnotation: hotel,
            mapImage: nil
        )
    }

    private static func getAccessibilityViewModel(summary: Stay, hotel: Hotel) -> AccessibilityViewModel? {
        guard summary.numberOfAccessibleRooms > 0 else { return nil }

        let callButtonTitle = hotel.phoneNumber == nil ?
        PILocalizedString("callUsButtonTitle") :
        PILocalizedString("callHotelButtonTitle")

        let accessibilityDetails = hotel.nationalPhoneNumber == nil ?
        PILocalizedString("bookingConfirmationAccesibilityRowDetailsHotel") :
        PILocalizedString("bookingConfirmationAccesibilityRowDetailsCustomerContactCentre")

        return AccessibilityViewModel(
            callButtonTitle: callButtonTitle,
            accessibilityDetails: accessibilityDetails
        )
    }
}

struct ErrorBannerViewModel {
    let message: String
}

struct BookingSuccessInfoViewModel {
    let title: String
    let message: NSAttributedString

    static func createFrom(stay: Stay, isAmended: Bool) -> BookingSuccessInfoViewModel? {
        if stay.cancelled || isAmended {
            return nil
        }

        let guestName = stay.leadGuestName ?? PILocalizedString("yourEmailAddress")

        // If already paid during booking flow
        if stay.prePaidAmount == stay.totalCost {
            let message = String(format: PILocalizedString("bookingConfirmationPaidInFullMessage"), guestName)

            return BookingSuccessInfoViewModel(
                title: PILocalizedString("bookingConfirmationInfoCellTitle"),
                message: NSAttributedString(string: message)
            )
        } else { // If to be paid on arrival
            let localizedPrice = if let totalCost = stay.totalCost,
                                    let prePaidAmount = stay.prePaidAmount {
                (totalCost - prePaidAmount)?.localizedValue ?? PILocalizedString("fullAmountFallbackText")
            } else {
                PILocalizedString("fullAmountFallbackText")
            }
            let message = String(
                format: PILocalizedString("bookingConfirmationPayOnArrivalMessage"),
                guestName,
                localizedPrice
            )

            return BookingSuccessInfoViewModel(
                title: PILocalizedString("bookingConfirmationInfoCellTitle"),
                message: NSAttributedString(string: message)
            )
        }
    }
}

struct InfoViewModel {
    let notificationStyle: NotificationStyle
    let message: NSAttributedString?

    static func createFrom(_ summary: Stay, isAmended: Bool) -> InfoViewModel {
        var message: NSAttributedString?

        // If there is an error
        guard summary.cancelled == false else {
            message = NSAttributedString(string: PILocalizedString("bookingConfirmationInfoCancelledTitle"))

            return InfoViewModel(notificationStyle: .error, message: message)
        }

        // If Booking Confirmation has failed then we show a generic message as we do not know how they payed
        if summary.totalCost?.amount == 0 || summary.totalCost == nil {
            let guestName = summary.leadGuestName ?? PILocalizedString("yourEmailAddress")
            message = NSAttributedString(string: PILocalizedString("bookingConfirmationInfoCellTitle") + String(
                format: PILocalizedString("bookingConfirmationGenericMessage"),
                guestName
            ))

            return InfoViewModel(notificationStyle: .success, message: message)
        }

        let guestName = summary.leadGuestName ?? PILocalizedString("yourEmailAddress")

        // Amended booking
        if isAmended {
            if summary.prePaidAmount == summary.totalCost {
                let finalText = PILocalizedString("bookingConfirmationAmendedSuccessful") + "\n" + String(
                    format: PILocalizedString("bookingConfirmationPaidInFullMessage"),
                    guestName
                )
                let ranges = finalText.ranges(of: PILocalizedString("bookingConfirmationPaidInFullBoldText"))
                message = finalText.attributedString(with: ranges, attributes: [.font: UIFont.Heading4_Semibold()])

                return InfoViewModel(notificationStyle: .success, message: message)
            } else {
                var localizedPrice = PILocalizedString("fullAmountFallbackText")

                if let totalCost = summary.totalCost, let prePaidAmount = summary.prePaidAmount {
                    localizedPrice = (totalCost - prePaidAmount)?
                        .localizedValue ?? PILocalizedString("fullAmountFallbackText")
                }
                let finalText = PILocalizedString("bookingConfirmationAmendedSuccessful") + "\n" + String(
                    format: PILocalizedString("bookingConfirmationPayOnArrivalMessage"),
                    guestName,
                    localizedPrice
                )
                let ranges = finalText.ranges(of: PILocalizedString("bookingConfirmationPayOnArrivalBoldText"))
                message = finalText.attributedString(with: ranges, attributes: [.font: UIFont.Heading4_Semibold()])

                return InfoViewModel(notificationStyle: .success, message: message)
            }
        }

        return InfoViewModel(notificationStyle: .info, message: NSAttributedString(string: ""))
    }
}

struct CheckInOutExtrasInfoViewModel {
    let tag: String
    let message: String
    let topPadding: CGFloat
    let bottomPadding: CGFloat
    let style: NotificationStyle
}

struct HotelAndReferenceViewModel {
    let hotelImage: URL?
    let hotelName: String
    let reference: String
    let shouldShowResendInvoice: Bool
}

struct BookingDatesViewModel {
    let arrivalDateString: String?
    let checkInValue: String
    let checkOutDateString: String?
    let checkOutValue: String

    static func createFrom(_ summary: Stay, hotel: Hotel) -> BookingDatesViewModel {
        let hotelBrand = hotel.brand
        let checkInValue = summary.checkInTimeText(hotelBrand: hotelBrand)
        let checkOutValue = summary.checkOutTimeText(hotelBrand: hotelBrand)

        let bookingDatesViewModel: BookingDatesViewModel = BookingDatesViewModel(
            arrivalDateString: summary.arrivalDate?.localizedVeryShortStringFormat,
            checkInValue: checkInValue,
            checkOutDateString: summary.checkOutDate?.localizedVeryShortStringFormat,
            checkOutValue: checkOutValue
        )

        return bookingDatesViewModel
    }
}

struct TripSummaryViewModel {
    let title: String
    let guestsRoomsSummary: String
}

struct EmployeeOfferWarningViewModel {
    let tag: String
    let message: String
    let topPadding: CGFloat
    let bottomPadding: CGFloat
    let style: NotificationStyle
}

struct LocationViewModel: LocationDetailsDisplayable {
    let title: String?
    let address: String?
    let parkingImage: UIImage?
    let parkingInfo: String?
    let hotelAnnotation: Hotel
    var mapImage: UIImage?

    var annotations: [MKAnnotation] {
        [hotelAnnotation]
    }
}

struct AccessibilityViewModel {
    let callButtonTitle: String
    let accessibilityDetails: String
}

struct PriceTotalViewModel {
    let outstandingAmount: String?
    let totalPrice: String
    let rate: String?
}

struct BookedUpsellsViewModel {
    let availableUpsells: [BookedUpsellViewModel]?
    let unavailableUpsells: [BookedUpsellViewModel]?
}

struct PriceBreakdownViewModel {
    let title: String
}

struct AmendViewModel {
    let title: String
    let subtitle: String
}

struct CallHotelViewModel {
    let callDescription: String
    let callChargeInformation: String
}

struct HotelInformationAlertViewModel {
    let tag: String
    let text: String?
    let attributedString: NSAttributedString?
    let highlightedText: String?
    let icon: String?
    let contentBackgroundColor: UIColor?
    let topPadding: CGFloat
    let bottomPadding: CGFloat
    let style: NotificationStyle
}
