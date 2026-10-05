//
//  HotelDetailsModule.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import MapKit

enum HotelDetailsModule {
    static func build(
        withCode hotelCode: String,
        hotelBrand: HotelBrand?,
        existingAvailability: HotelAvailabilityResponse?,
        bookingAllowed: Bool,
        andSuggestion suggestion: Suggestion?,
        shouldShowCheckAvailability: Bool = false,
        arrivedViaMap: Bool = false,
        distanceToSearch: Double? = nil,
        delegate: HotelDetailsRouterDelegate? = nil,
        shouldDisplayCalendarFirst: Bool = false
    ) -> HotelDetailsViewController {
        BookingDetails.sharedInstance.userEnteredPromoCode = nil
        BookingDetails.sharedInstance.promoKind = nil
        BookingDetails.sharedInstance.promoRateTags = nil

        let interactor = HotelDetailsInteractor(
            withCode: hotelCode,
            hotelBrand: hotelBrand,
            bookingAllowed: bookingAllowed,
            existingAvailability: existingAvailability,
            suggestion: suggestion,
            shouldShowCheckAvailability: shouldShowCheckAvailability,
            arrivedViaMap: arrivedViaMap,
            distanceToSearch: distanceToSearch
        )

        let router = HotelDetailsRouter(with: delegate)
        let presenter = HotelDetailsPresenter(
            with: interactor,
            andRouter: router,
            showCalendarFirst: shouldDisplayCalendarFirst
        )

        interactor.presenter = presenter

        let controller = HotelDetailsViewController(presenter: presenter, and: presenter)

        presenter.view = controller

        router.viewController = controller
        router.presenter = presenter

        return controller
    }
}


// 👀*=*=*=*=*=*=* View *=*=*=*=*=*=*👀
// MARK: View Protocols
protocol HotelDetailsViewProtocol {
    func updateRoomImage(withDesiredRoomIndex desiredRoomIndex: Int)
    func updateHotelAndAvailability(toAvailable available: Bool)
    func updateViewModelTo(_ viewModel: HotelDetailsViewModel?)

    func startDisplayingLoadingElements()
    func stopDisplayingLoadingElements()
    func hotelUpdateFailed(with error: Error)

    func showDirectionsScreen(with viewModel: DirectionsViewModel)
    func openPrivacyPolicy()
    func callHotel(withPhoneNumber phoneNumber: String)
    func dismissCurrentOverlay()
    func emailCustomerService()

    func scrollTo(rateSection: RateSection)
    func provideHapticFeedback()
    func scrollToTripAdvisorSection()
    func present(_ viewController: UIViewController)
    func push(_ viewController: UIViewController)

    func showNotAllowedToBookPrompt()
    func showAlert(
        with title: String,
        message: String,
        confirmTitle: String,
        cancelTitle: String,
        confirmAction: @escaping (() -> Void)
    )
}

struct AccessibilityInfoViewModel {
    let hotelInfo: [String]
    let callInfo: String
    let callButtonTitle: String
    let phoneNumber: String
    let emailButtonTitle: String
}

protocol DirectionsAlertConfig {
    var placeMark: MKPlacemark? { get }
    var coordinate: CLLocationCoordinate2D { get }
}

extension Hotel: DirectionsAlertConfig {}

typealias HotelDetailsCreateParams = (
    hotel: Hotel,
    criteria: Criteria,
    suggestion: Suggestion?,
    discountCodeViewModel: HotelDetailsDiscountCodeViewModel?,
    shouldShowCheckAvailability: Bool,
    bookingAllowed: Bool,
    dismissedCoronavirusMessaging: Bool
)

struct DirectionsViewModel {
    let googleMapsURL: URL
    let appleMapsPlacemark: MKPlacemark?
    let coordinates: CLLocationCoordinate2D

    let optionsTitle: String
    let optionsMessage: String
    let cancelButtonTitle: String
    let appleMapsButtonTitle: String
    let googleMapsButtonTitle: String

    static func create(from config: DirectionsAlertConfig) -> DirectionsViewModel {
        let googleMapsURL = Constants.googleMapsURL
        let appleMapsPlacemark = config.placeMark
        let coordinates = config.coordinate

        let optionsTitle = PILocalizedString("directionsOptionsAlertTitle", comment: "Directions options alert title")
        let optionsMessage = PILocalizedString("directionsOptionsAlertMessage", comment: "Directions options alert message")
        let cancelButtonTitle = PILocalizedString(
            "directionsOptionsAlertActionCancel",
            comment: "Directions options alert action: cancel"
        )
        let appleMapsButtonTitle = PILocalizedString(
            "directionsOptionsAlertActionAppleMaps",
            comment: "Directions options alert action: Apple Maps"
        )
        let googleMapsButtonTitle = PILocalizedString(
            "directionsOptionsAlertActionGoogleMaps",
            comment: "Directions options alert action: Google Maps"
        )

        return DirectionsViewModel(
            googleMapsURL: googleMapsURL,
            appleMapsPlacemark: appleMapsPlacemark,
            coordinates: coordinates,
            optionsTitle: optionsTitle,
            optionsMessage: optionsMessage,
            cancelButtonTitle: cancelButtonTitle,
            appleMapsButtonTitle: appleMapsButtonTitle,
            googleMapsButtonTitle: googleMapsButtonTitle
        )
    }
}

struct ImportantInformationViewModel {
    let importantInfoShortSummary: String
    let leadingIconImage: UIImage?
}

enum FoodContentSectionIndex: Int {
    case food = 0
    case restaurant
}

struct FoodContentViewModel {
    let foodContentSectionIndex: FoodContentSectionIndex
    var title: String?
    var description: String
    var restaurantImageURL: URL?
    var imageShouldBeNormalHeight: Bool
    var imageShouldBeHidden: Bool?
}

struct HotelDetailsViewModel {
    let carouselViewModel: CarouselViewModel
    var titleAndSummaryViewModel: TitleAndSummaryViewModel
    let accessibilityInfoViewModel: AccessibilityInfoViewModel?
    let additionalInfoAndFacilities: AdditionalInfoAndFacilitiesViewModel
    var discountCodeViewModel: HotelDetailsDiscountCodeViewModel?
    var rateSectionViewModels: [RateSectionViewModel]
    let mapSectionViewModel: MapSectionViewModel
    let callSectionViewModel: CallSectionViewModel
    var locationSectionViewModel: LocationSectionViewModel?
    var roomSectionViewModel: RoomSectionViewModel
    var foodAndRestaurantViewModel: RestaurantAndFoodViewModel
    var parkingSectionViewModel: ParkingSectionViewModel
    var importantInformationViewModel: ImportantInformationViewModel?
    var tripAdvisorViewModel: TripAdvisorViewModel?
    var companyName: String?
    // Section visibility definitions
    let substitutionRowText: NSAttributedString?
    let shouldShowAddADiscountCodeSection: Bool
    let shouldShowRatesSection: Bool
    let shouldShowErrorSection: Bool
    let shouldShowFoodSection: Bool
    let shouldShowParkingSection: Bool
    let shouldShowRestaurantInformation: Bool
    let shouldShowRoomSection: Bool
    let shouldShowBBNotAvailableSection: Bool
    let shouldShowCompanyDetails: Bool
    let shouldShowCotNotAvailableMessageSection: Bool

    // swiftlint:disable:next function_body_length
    static func createFrom(hotelDetailsCreateParams: HotelDetailsCreateParams) -> HotelDetailsViewModel? {
        let hotel = hotelDetailsCreateParams.hotel
        let carouselViewModel = CarouselViewModel.createFrom(hotel)
        let substitutions: NSAttributedString? = substitutionText(withHotel: hotel)
        let rateSectionViewModels = HotelDetailsViewModel.roomRatesViewModels(
            for: hotel.rates.ratesSeparatedByTieredRooms,
            roomConfiguration: hotel.hotelRoomConfiguration,
            brand: hotel.brand,
            and: hotelDetailsCreateParams.criteria
        )
        let shouldShowParkingSection = hotel.parkings.isNotEmpty || hotel.parkingDescription != nil
        let stays = SimpleStorageManager<Stay>.init(dataSource: UserDefaults.standard).items
        let shouldShowFreePhone: Bool = hotelDetailsCreateParams.criteria.rooms
            .contains(where: { $0.type == .accessible }) || stays.activeStays.contains(where: { $0.hotelCode == hotel.code })
        let callSectionViewModel: CallSectionViewModel = CallSectionViewModel(
            callDescription: shouldShowFreePhone && hotel.nationalPhoneNumber != nil ?
                PILocalizedString("callHotelButtonTitle", comment: "Call hotel button title") :
                PILocalizedString("callUsButtonTitle", comment: "Call us button title"),
            costDescription: hotel.brand == .premierInnGermany ?
                PILocalizedString("telephoneDE", comment: "Call cost message") :
                PILocalizedString("telephoneUK", comment: "Call cost message")
        )

        let accessibilityInfoViewModel: AccessibilityInfoViewModel? = {
            guard hotelDetailsCreateParams.criteria.rooms.contains(where: { $0.type == .accessible }) else { return nil }

            let hotelInfo: [String] = {
                var info = [PILocalizedString("accessInfoHotel", comment: "")]

                let containsLoweredBaths = hotel.facilities?.contains(where: { $0.code == "LWB" }) ?? false
                let containsWetRooms = hotel.facilities?.contains(where: { $0.code == "WET" }) ?? false

                switch (containsLoweredBaths, containsWetRooms) {
                case (true, true):
                    info.insert(PILocalizedString("accessLoweredBathsAndWetRooms"), at: 0)
                case (true, false):
                    info.insert(PILocalizedString("accessLoweredBaths"), at: 0)
                case (false, true):
                    info.insert(PILocalizedString("accessWetRooms"), at: 0)
                case (false, false):
                    break
                }

                return info
            }()

            let callInfo = hotel
                .nationalPhoneNumber != nil ? PILocalizedString("accessInfoCallHotel", comment: "") : PILocalizedString(
                    "accessInfoCallGeneric",
                    comment: ""
                )
            let phoneNumber = hotel.nationalPhoneNumber ?? Constants.PhoneNumbers.nationalRateFallBack

            return AccessibilityInfoViewModel(
                hotelInfo: hotelInfo,
                callInfo: callInfo,
                callButtonTitle: callSectionViewModel.callDescription,
                phoneNumber: phoneNumber,
                emailButtonTitle: PILocalizedString("emailCustomerService", comment: "Email customer servuce button title")
            )
        }()

        let importantInfoViewModel: ImportantInformationViewModel? = {
            guard let notes = hotel.notes, notes.isNotEmpty else { return nil }
            guard let notesDateFiltered = hotel.notesToShow(
                arrivalDate: BookingDetails.sharedInstance.criteria.arrivalDate,
                departureDate: BookingDetails.sharedInstance.departureDate
            ) else { return nil }
            guard notesDateFiltered.isNotEmpty else { return nil }

            return ImportantInformationViewModel(
                importantInfoShortSummary: "\(PILocalizedString("hotelDetailsImportantInfo")) (\(notesDateFiltered.count))",
                leadingIconImage: UIImage(named: "infoIcon")
            )
        }()

        let shouldShowErrorSection: Bool = {
            // If this is the HDP page where no availability call has been made then do not show the "sold-out" error
            guard hotelDetailsCreateParams.shouldShowCheckAvailability == false else { return false }

            return hotel.isAvailableAndHasRates == false
        }()

        let shouldShowBBNotAvailableSection: Bool = UserSessionManager.sharedInstance.currentUser?
            .isBusiness == true && SettingsManager.sharedInstance.shouldOperaShowFallBackForBB

        let titleAndSummaryViewModel = TitleAndSummaryViewModel.createFrom(
            hotel,
            shouldShowCheckAvailability: hotelDetailsCreateParams
                                                                           .shouldShowCheckAvailability,
            bookingAllowed: hotelDetailsCreateParams
                                                                           .bookingAllowed,
            dismissedCoronavirusMessaging: hotelDetailsCreateParams
                                                                           .dismissedCoronavirusMessaging,
            isBBHotelInfoAlertBeingShown: shouldShowBBNotAvailableSection,
            shouldShowErrorSection: shouldShowErrorSection
        )

        // Check for cot availability and if any cot is requested but not available
        // we will show the cot not available message section
        let shouldShowCotNotAvailableMessageSection: Bool = {
            var isCotAvailable = false
            let rates = hotel.rates
            let rooms = rates.compactMap { $0.rooms }.flatMap { $0 }
            let cotValues = rooms.compactMap { $0.cotRequired }
            let isCotRequested = cotValues.contains(true)

            guard isCotRequested else { return false }

            let options = rooms.compactMap { $0.options }.flatMap { $0 }
            let availableCots = options.compactMap { $0.cotAvailable }
            isCotAvailable = availableCots.contains(true)

            return !isCotAvailable
        }()

        return HotelDetailsViewModel(
            carouselViewModel: carouselViewModel,
            titleAndSummaryViewModel: titleAndSummaryViewModel,
            accessibilityInfoViewModel: accessibilityInfoViewModel,
            additionalInfoAndFacilities: AdditionalInfoAndFacilitiesViewModel.create(from: hotel),
            discountCodeViewModel: hotelDetailsCreateParams.discountCodeViewModel,
            rateSectionViewModels: rateSectionViewModels,
            mapSectionViewModel: MapSectionViewModel.create(from: hotel, and: hotelDetailsCreateParams.suggestion),
            callSectionViewModel: callSectionViewModel,
            locationSectionViewModel: LocationSectionViewModel.create(from: hotel),
            roomSectionViewModel: RoomSectionViewModel.create(from: hotel),
            foodAndRestaurantViewModel: RestaurantAndFoodViewModel.create(from: hotel),
            parkingSectionViewModel: ParkingSectionViewModel.create(from: hotel),
            importantInformationViewModel: importantInfoViewModel,
            tripAdvisorViewModel: TripAdvisorViewModel.create(from: hotel.tripAdvisorDetails),
            companyName: UserSessionManager.sharedInstance.currentUser?.company?.companyDetails?.companyName,
            substitutionRowText: substitutions,
            shouldShowAddADiscountCodeSection: SettingsManager.sharedInstance
            .shouldShowHdpDiscountCodeCell(for: hotel.brand),
            shouldShowRatesSection: hotel.rates.isNotEmpty,
            shouldShowErrorSection: shouldShowErrorSection && !shouldShowBBNotAvailableSection,
            shouldShowFoodSection: hotel.foodOptionContentSections != nil,
            shouldShowParkingSection: shouldShowParkingSection,
            shouldShowRestaurantInformation: hotel.restaurant != nil,
            shouldShowRoomSection: hotel.brand == .zip,
            shouldShowBBNotAvailableSection: shouldShowBBNotAvailableSection,
            shouldShowCompanyDetails: UserSessionManager.sharedInstance.currentUser?.isBusiness == true,
            shouldShowCotNotAvailableMessageSection: shouldShowCotNotAvailableMessageSection
        )
    }

    private static func roomRatesViewModels(
        for lettingOptionRates: [LettingOptionRates],
        roomConfiguration: HotelRoomConfiguration?,
        brand: HotelBrand,
        and criteria: Criteria
    ) -> [RateSectionViewModel] {
        lettingOptionRates.enumerated().map { (index, element) in
            RateSectionViewModel.createFrom(
                element,
                roomConfiguration: roomConfiguration,
                hotelBrand: brand,
                andCriteria: criteria,
                and: index
            )
        }
    }

    private static func substitutionText(withHotel hotel: Hotel) -> NSAttributedString? {
        guard let rate = hotel.rates.first else { return nil }

        let substitutions = BookingDetails.sharedInstance.substitutions(forRate: rate)

        if substitutions.isEmpty { return nil }

        let boldAttributes: [NSAttributedString.Key: Any] = [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]

        let subText = NSMutableAttributedString(string: PILocalizedString(
            "hotelDetailsSubstitutionTitle",
            comment: "Hotel details: substitution title"
        ))
        subText.style(
            text: PILocalizedString(
                "hotelDetailsSubstitutionTitleBold",
                comment: "Hotel details: substitution title bold text"
            ),
            withAttributes: boldAttributes
        )

        switch substitutions.count {
        case 1:
            guard let original = substitutions.first?.desired?.localizedName.capitalized,
                  let replacement = substitutions.first?.substitutedRoomsConcatenated?.capitalized else { break }

            let message = String(
                format: PILocalizedString(
                    "hotelDetailsSubstitutionMessage",
                    comment: "Hotel details: substitution message format (don't remove placeholders)"
                ),
                original,
                replacement
            )
            let attributedMessage = NSMutableAttributedString(string: message)

            subText.append(attributedMessage)

            subText.style(text: original, withAttributes: boldAttributes)
            subText.style(text: replacement, withAttributes: boldAttributes)

            return NSAttributedString(attributedString: subText)

        default:
            for room in substitutions {
                guard let roomName = room.roomName?.capitalized,
                      let desiredRoomType = room.desired?.localizedName.capitalized,
                      let replacementRoomType = room.substitutedRoomsConcatenated?.capitalized else { continue }

                let message = String(
                    format: PILocalizedString(
                        "hotelDetailsSubstitutionMessageMultipleRooms",
                        comment: "Hotel details: substitution message format for multiple rooms (don't remove placeholders)"
                    ),
                    roomName,
                    desiredRoomType,
                    replacementRoomType
                )
                subText.append(NSAttributedString(string: "\(message)\n"))
                subText.style(text: roomName, withAttributes: boldAttributes)
                subText.style(text: desiredRoomType, withAttributes: boldAttributes)
                subText.style(text: replacementRoomType, withAttributes: boldAttributes)
            }
        }

        let paragraphStyle = NSMutableParagraphStyle()

        subText.style(text: subText.string, withAttributes: [NSAttributedString.Key.paragraphStyle: paragraphStyle])

        return NSAttributedString(attributedString: subText)
    }
}

struct CallSectionViewModel {
    let callDescription: String
    let costDescription: String
}

struct CarouselViewModel {
    // TODO: roundels for GraphQL https://whitbreadis.atlassian.net/browse/DNRQ-35020
    private static let standardRoom = "standard"
    private static let businessRoom = "extra"
    private static let ultimateRoom = "ultimate"
    private static let hubStandardRoom = "hub-standard-room"
    private static let hubBiggerRoom = "hub-bigger-room"

    let carouselRoundelDesigns: [RoundelDesign]

    let shouldShowBannerRubber: Bool
    let carouselMessageFlagText: String?
    let carouselMessageFlagTextColor: UIColor?
    let carouselMessageFlagBackgroundColor: UIColor?
    let carouselBannerRubberBackgroundColour: UIColor?
    let carouselBannerRubberImage: UIImage?

    static func createFrom(_ hotel: Hotel) -> CarouselViewModel {
        let roundelDesigns: [RoundelDesign] = {
            guard hotel.brand != .zip else {
                return hotel.primaryImagesWithTags.compactMap({ RoundelDesign(
                    url: $0.url,
                    backgroundColor: nil,
                    foregroundColor: nil,
                    text: nil,
                    shouldEmbolden: false
                ) })
            }

            var designs = [RoundelDesign]()

            var shouldShowRoundels: Bool = false

            var differentRoomTypes = [String]()

            for imagesAndTags in hotel.primaryImagesWithTags {
                for tag in imagesAndTags.tags {
                    if [businessRoom, ultimateRoom, hubStandardRoom, hubBiggerRoom].contains(tag) || hotel.facilities?
                       .contains(where: { [
                           "PRR",
                           "STE"
                       ].contains($0.code) }) ?? false {
                        shouldShowRoundels = true
                        break
                    }

                    guard [standardRoom, ultimateRoom, hubStandardRoom, hubBiggerRoom].contains(tag) else { continue }

                    if differentRoomTypes.contains(tag) == false {
                        differentRoomTypes.append(tag)
                    }
                }
            }

            shouldShowRoundels = shouldShowRoundels ? true : differentRoomTypes.count > 1

            for imageWithTag in hotel.primaryImagesWithTags {
                guard shouldShowRoundels else {
                    designs.append(RoundelDesign(
                        url: imageWithTag.url.sizedImageURL(withSize: .large),
                        backgroundColor: nil,
                        foregroundColor: nil,
                        text: nil,
                        shouldEmbolden: false
                    ))
                    continue
                }

                let backgroundColour: UIColor = getBackgroundColor(hotel: hotel, imageWithTag: imageWithTag)

                let foregroundColour: UIColor = .BaseWhite

                let text: String? = getText(hotel: hotel, imageWithTag: imageWithTag)

                designs.append(RoundelDesign(
                    url: imageWithTag.url.sizedImageURL(withSize: .large),
                    backgroundColor: backgroundColour,
                    foregroundColor: foregroundColour,
                    text: text,
                    shouldEmbolden: true
                ))
            }

            return designs
        }()

        if hotel.brand == .premierInn || hotel.brand == .premierInnGermany {
            return CarouselViewModel(
                carouselRoundelDesigns: roundelDesigns,
                shouldShowBannerRubber: false,
                carouselMessageFlagText: nil,
                carouselMessageFlagTextColor: nil,
                carouselMessageFlagBackgroundColor: nil,
                carouselBannerRubberBackgroundColour: nil,
                carouselBannerRubberImage: nil
            )
        }

        let flagText = hotel.messagingFlag?.text
        let flagTextColor = hotel.messagingFlag?.textColor
        let flagBackgroundColor = hotel.messagingFlag?.color
        let bannerBackgroundColor: UIColor = hotel.brand == .hub ? .hubGreen : .zipRed
        // what color
        let bannerImage = hotel.brand == .hub ? UIImage(named: "hubLogoBanner") : UIImage(named: "zipLogoBanner")

        return CarouselViewModel(
            carouselRoundelDesigns: roundelDesigns,
            shouldShowBannerRubber: true,
            carouselMessageFlagText: flagText,
            carouselMessageFlagTextColor: flagTextColor,
            carouselMessageFlagBackgroundColor: flagBackgroundColor,
            carouselBannerRubberBackgroundColour: bannerBackgroundColor,
            carouselBannerRubberImage: bannerImage
        )
    }

    private static func getBackgroundColor(hotel: Hotel, imageWithTag: (url: URL, tags: [String])) -> UIColor {
        if isBrandPremierInn(hotel: hotel) {
            guard imageWithTag.tags.contains(businessRoom) || imageWithTag.tags.contains(ultimateRoom)
                else { return .BasePurple }
            return .paleTeal
            // what color
        }

        return .TintD1
    }

    private static func getText(hotel: Hotel, imageWithTag: (url: URL, tags: [String])) -> String? {
        if isBrandPremierInn(hotel: hotel) {
            guard imageWithTag.tags.contains(businessRoom) || imageWithTag.tags.contains(ultimateRoom) else {
                return imageWithTag.tags.contains(standardRoom) ? PILocalizedString("hotelDetailsStandardRoomRoundel") : nil
            }

            if imageWithTag.tags.contains(ultimateRoom) {
                return PILocalizedString("hotelDetailsPremiumRoomRoundel")
            } else if imageWithTag.tags.contains(businessRoom) {
                return PILocalizedString("hotelDetailsBusinessRoomRoundel")
            } else {
                return nil
            }
        }

        return imageWithTag.tags
            .contains(hubStandardRoom) ? PILocalizedString("hotelDetailsHubStandardRoomRoundel") : imageWithTag.tags
            .contains(hubBiggerRoom) ? PILocalizedString("hotelDetailsHubBiggerRoomRoundel") : nil
    }

    private static func isBrandPremierInn(hotel: Hotel) -> Bool {
        hotel.brand == .premierInn || hotel.brand == .premierInnGermany
    }
}

struct TitleAndSummaryViewModel {
    let hotelName: String
    let distanceDescription: NSAttributedString?

    let tripAdvisorViewModel: TripAdvisorViewModel?

    let criteriaDatesButtonTitle: String
    let guestsAndRoomsButtonTitle: String

    var limitedOrNoAvailabilityInfo: (text: String, color: UIColor)?

    let shouldShowCriteriaSummaryRow: Bool
    let shouldShowCheckAvailabilityRow: Bool
    let isHotelNotAvailable: Bool

    let announcementText: String?
    var shouldShowCoronavirusMessaging: Bool
    var hasPremierPlus: Bool?
    var messagingFlag: MessagingFlag?
    var importantInfoViewModel: ImportantInformationViewModel?


    static func createFrom(
        _ hotel: Hotel,
        shouldShowCheckAvailability: Bool = false,
        bookingAllowed: Bool,
        dismissedCoronavirusMessaging: Bool,
        isBBHotelInfoAlertBeingShown: Bool = false,
        shouldShowErrorSection: Bool
    ) -> TitleAndSummaryViewModel {
        // Announcement message should come from hotel details, if not fallback to remote config message (if any)
        let announcementText: String? = {
            if let announcementText = hotel.announcement?.announcementToShow(
                arrivalDate: BookingDetails.sharedInstance.criteria.arrivalDate,
                departureDate: BookingDetails.sharedInstance.criteria.checkOutDate
            ) {
                return announcementText
            }
            guard SettingsManager.sharedInstance.coronavirusMessagingSRPAndHDP else { return nil }
            return SettingsManager.sharedInstance.coronavirusBannerMessage
        }()
        let announcementTextProvided = announcementText?.isNotEmpty ?? false

        return TitleAndSummaryViewModel(
            hotelName: hotel.name,
            distanceDescription: hotel.distance > 0 ? LengthFormatter.distanceFormatter.attributedString(
                distance: hotel.distance,
                unit: hotel.distanceUnit,
                suffix: PILocalizedString(
                    "hotelDetailsDistanceSuffix",
                    comment: "Hotel details: distance description suffix"
                ),
                boldFont: UIFont.Heading4_Semibold(),
                regularFont: .BodySmall(),
                boldColor: .TintD1
            ) : NSAttributedString(
                string: hotel.address?.description ?? "",
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Body()]
            ),
            tripAdvisorViewModel: TripAdvisorViewModel.create(from: hotel.tripAdvisorDetails),
            criteriaDatesButtonTitle: BookingDetails.sharedInstance.criteria.reviewDatesSummary,
            guestsAndRoomsButtonTitle: BookingDetails.sharedInstance.criteria.criteriaRoomsGuestSummary,
            limitedOrNoAvailabilityInfo: configureLimitedOrNoAvailabilityInfo(
                hotel.limitedAvailability,
                shouldShowErrorSection
            ),
            shouldShowCriteriaSummaryRow: hotel.isAvailableAndHasRates && UIDevice.current.userInterfaceIdiom != .pad,
            shouldShowCheckAvailabilityRow: shouldShowCheckAvailability && !isBBHotelInfoAlertBeingShown,
            isHotelNotAvailable: hotel.isAvailableAndHasRates == false && bookingAllowed == true,
            announcementText: announcementText,
            shouldShowCoronavirusMessaging: !dismissedCoronavirusMessaging && announcementTextProvided,
            hasPremierPlus: hotel.offersPremierPlus,
            messagingFlag: hotel.messagingFlag,
            importantInfoViewModel: configImportantInfoViewModel(hotel: hotel)
        )
    }

    static func configureLimitedOrNoAvailabilityInfo(
        _ limitedAvailability: Bool,
        _ showErrorSection: Bool
    ) -> (String, UIColor)? {
        if limitedAvailability {
            return (
                PILocalizedString(
                    "hotelDetailsLastFewRooms",
                    comment: "Hotel details: last few rooms title"
                ),
                .Tint6
            )
        }
        if showErrorSection {
            return (PILocalizedString("hotelDetailsSoldOut"), .Tint8)
        }
        return nil
    }

    static func configImportantInfoViewModel(hotel: Hotel) -> ImportantInformationViewModel? {
        guard let notes = hotel.notes, notes.isNotEmpty else { return nil }
        guard let notesDateFiltered = hotel.notesToShow(
            arrivalDate: BookingDetails.sharedInstance.criteria.arrivalDate,
            departureDate: BookingDetails.sharedInstance.departureDate
        ) else { return nil }
        guard notesDateFiltered.isNotEmpty else { return nil }

        return ImportantInformationViewModel(
            importantInfoShortSummary: "\(PILocalizedString("hotelDetailsImportantInfo")) (\(notesDateFiltered.count))",
            leadingIconImage: UIImage(named: "infoIcon")
        )
    }
}

struct AdditionalInfoAndFacilitiesViewModel {
    var title: String
    var hotelDescription: String?
    var checkInDescription: String?
    var checkOutDescription: String?
    var facilitiesViewModel: HotelFacilitiesViewModel?
    var shouldShowSeeMoreInfoButton: Bool
    var offersBreakfast: Bool
    var rates: [Rate]

    let lettingTypeToShowInRoomInfo: String

    static func create(from hotel: Hotel) -> AdditionalInfoAndFacilitiesViewModel {
        var facilitiesViewModel: HotelFacilitiesViewModel?
        if let facilities = hotel.facilities {
            facilitiesViewModel = HotelFacilitiesViewModel(facilities: facilities)
        }

        var offersBreakfast: Bool {
            guard let meals = hotel.rates.first?.foodUpsells else { return false }

            return meals
                .first(where: { $0.legend.lowercased().contains(PILocalizedString("Breakfast").lowercased()) }) != nil ?
                true : false
        }

        // we need to set a letting code here, if we want to change the default tab in the room types overlay
        let lettingTypeForRoomInfo = ""

        return AdditionalInfoAndFacilitiesViewModel(
            title: PILocalizedString("aboutThisHotel"),
            hotelDescription: hotel.hotelDescription,
            checkInDescription: hotel
            .brand == .premierInnGermany ? PILocalizedString("hotelDetailsGermanCheckInTime") :
            PILocalizedString("hotelDetailsCheckInTime"),
            checkOutDescription: hotel
            .brand == .premierInnGermany ? PILocalizedString("hotelDetailsGermanCheckOutTime") :
            PILocalizedString("hotelDetailsCheckOutTime"),
            facilitiesViewModel: facilitiesViewModel,
            shouldShowSeeMoreInfoButton: hotel.notes != nil,
            offersBreakfast: offersBreakfast,
            rates: hotel.rates,
            lettingTypeToShowInRoomInfo: lettingTypeForRoomInfo
        )
    }
}

// Overarching Rate Section View Model
struct RateSectionViewModel {
    let index: Int
    let title: String
    let rowViewModels: [RateCellViewModel]
    let shouldShowCityTaxRow: Bool
    let hotelBrand: HotelBrand
    let rateRoomSectionLettingType: String?
    let criteriaDescription: String

    let shortDescription: String?
    let roomImages: [String]?

    var urgencyMessagingRoomsAvailable: Int?

    static func createFrom(
        _ roomRates: LettingOptionRates,
        roomConfiguration: HotelRoomConfiguration?,
        hotelBrand: HotelBrand,
        andCriteria criteria: Criteria,
        and index: Int
    ) -> RateSectionViewModel {
        let lettingType = roomRates.roomClassOptions.first?.lettingType
        let title = SettingsManager.sharedInstance.roomsSectionTitle(
            for: lettingType,
            roomClass: roomRates.roomClassOptions.first?.roomClass,
            and: hotelBrand
        )
        let criteriaDescription = criteria.rateCriteriaSummary
        let cityTaxRequired = roomRates.rates.contains(where: { $0.cityTaxRequired })
        let showSelectButtonTitle = roomRates.rates.first?.rooms?
            .firstIndex(where: { $0.isAccessibleRoom || $0.isNewTwinRoom }) != nil

        let shortDesc = roomConfiguration?.tabItems?[safe: index]?.roomDescriptionTextShort
        let roomImages = roomConfiguration?.tabItems?[safe: index]?.renditions

        let urgencyMessagingRoomsAvailable = calculateUrgencyMessaging(
            roomRates: roomRates,
            lettingType: lettingType
        )

        let rowViewModels = roomRates.rates.map { RateCellViewModel.createFrom(
            $0,
            lettingType: lettingType,
            hotelBrand: hotelBrand,
            criteria: criteria,
            isSelectTitle: showSelectButtonTitle
        ) }

        return RateSectionViewModel(
            index: index,
            title: title,
            rowViewModels: rowViewModels,
            shouldShowCityTaxRow: cityTaxRequired,
            hotelBrand: hotelBrand,
            rateRoomSectionLettingType: lettingType,
            criteriaDescription: criteriaDescription,
            shortDescription: shortDesc,
            roomImages: roomImages,
            urgencyMessagingRoomsAvailable: urgencyMessagingRoomsAvailable
        )
    }

    // MARK: - Private Helpers

    private enum UrgencyMessaging {
        static let minRooms: Int = 1
        static let maxRooms: Int = 9
    }

    private static func calculateUrgencyMessaging(
        roomRates: LettingOptionRates,
        lettingType: String?
    ) -> Int? {
        guard let lettingType else { return nil }

        let availableForLettingType = roomRates.roomClassOptions
            .filter { $0.lettingType == lettingType }
            .compactMap(\.numberAvailable)
            .min()

        guard let availableForLettingType,
              (UrgencyMessaging.minRooms...UrgencyMessaging.maxRooms).contains(availableForLettingType) else {
            return nil
        }

        return availableForLettingType
    }
}

struct LocationSectionViewModel {
    var hotelDescription: String

    static func create(from hotel: Hotel) -> LocationSectionViewModel? {
        guard let hotelDescription = hotel.hotelDescription else { return nil }

        return LocationSectionViewModel(hotelDescription: hotelDescription)
    }
}

struct RateCellViewModel {
    let uniqueRateID: UUID
    let lettingType: String?
    let price: Cost?
    let priceText: NSAttributedString
    let rateName: NSAttributedString?
    let rateNameLabelBackgroundColor: UIColor
    let rateNameLabelBorderColor: CGColor
    let rateDescription: String?
    let criteriaDescription: String
    let rateButtonTitle: String
    let nonDiscountedRate: NSAttributedString?
    let promotionTag: NSAttributedString?
    let rateCellAccessibilityHelper: RateCellAccessibilityHelper?

    static func createFrom(
        _ rate: Rate,
        lettingType: String?,
        hotelBrand: HotelBrand,
        criteria: Criteria,
        isSelectTitle: Bool
    ) -> RateCellViewModel {
        let price = rate.totalCost(for: lettingType)
        let priceText = price?
            .fancyMantissaString(baseAttributes: [:], mantissaAttributes: [:]) ?? NSAttributedString(string: "")
        let rateName = rate.businessLogicName(hotelBrand: hotelBrand, biggerRoomAvailable: false)
        let rateNameLabelBackgroundColor = rate.backgroundColor
        let rateNameLabelBorderColor = rate.borderColor.cgColor
        let rateDescription = rate.businessLogicDescription(with: hotelBrand)
        let criteriaDescription = criteria.rateCriteriaSummary

        let rateButtonTitle = isSelectTitle ?
        PILocalizedString("hotelDetailsRateCellSelectButtonTitle") :
        PILocalizedString("hotelDetailsRateCellButtonTitle")

        let accessibilityHelper: RateCellAccessibilityHelper? = {
            guard let rateName = rate.name else { return nil }

            let headerIdentifier = AccessibilityIdentifiers.HotelDetails.bookingTypeHeader.replacingOccurrences(
                of: "%@",
                with: rateName
            )
            let priceIdentifier = rateName + AccessibilityIdentifiers.HotelDetails.BookingPrice
            let detailsIdentifier = AccessibilityIdentifiers.HotelDetails.bookingTypeDetails.replacingOccurrences(
                of: "%@",
                with: rateName
            )
            let daysIdentifier = rateName + AccessibilityIdentifiers.HotelDetails.BookingDays
            let buttonIdentifier = rateName + AccessibilityIdentifiers.HotelDetails.BookingButton

            return RateCellAccessibilityHelper(
                headerIdentifier: headerIdentifier,
                priceIdentifier: priceIdentifier,
                detailsIdentifier: detailsIdentifier,
                daysIdentifier: daysIdentifier,
                buttonIdentifier: buttonIdentifier
            )
        }()


        let nonDiscountedPrice = rate.nonDiscountedCost(for: lettingType)
        let nonDiscountedPriceText = nonDiscountedPrice?.fancyMantissaString(
            baseAttributes: [
                NSAttributedString.Key.strikethroughStyle: NSUnderlineStyle.single.rawValue,
                NSAttributedString.Key.strikethroughColor: UIColor.ColourDL2
            ],
            mantissaAttributes: [:]
        )

        var promotionTagString: NSAttributedString? {
            guard let tag = rate.promotionTag(for: hotelBrand) else { return nil }

            return NSAttributedString.attributedStringAndImageForDiscount(label: tag)
        }

        return RateCellViewModel(
            uniqueRateID: rate.uniqueID,
            lettingType: lettingType,
            price: price,
            priceText: priceText,
            rateName: rateName,
            rateNameLabelBackgroundColor: rateNameLabelBackgroundColor,
            rateNameLabelBorderColor: rateNameLabelBorderColor,
            rateDescription: rateDescription,
            criteriaDescription: criteriaDescription,
            rateButtonTitle: rateButtonTitle,
            nonDiscountedRate: nonDiscountedPriceText,
            promotionTag: promotionTagString,
            rateCellAccessibilityHelper: accessibilityHelper
        )
    }
}

struct MapSectionViewModel: LocationDetailsDisplayable {
    let title: String?
    let address: String?
    let annotations: [MKAnnotation]
    var mapImage: UIImage?

    static func create(from hotel: Hotel, and suggestion: Suggestion?) -> MapSectionViewModel {
        var annotations: [MKAnnotation] = [hotel]
        if let suggestion = suggestion {
            annotations.append(suggestion)
        }

        return MapSectionViewModel(
            title: PILocalizedString("mapScreenTitlePlan"),
            address: hotel.address?.description,
            annotations: annotations
        )
    }
}

struct RateCellAccessibilityHelper {
    var headerIdentifier: String
    var priceIdentifier: String
    var detailsIdentifier: String
    var daysIdentifier: String
    var buttonIdentifier: String
}

enum RateRoomSectionType {
    case StandardRoom
    case BiggerRoom
}

struct RoomSectionViewModel {
    var roomImageURLs: [URL]
    var hotelInformationViewModel: HotelInformationViewModel
    var roomContentViewModels: [RoomContentViewModel]

    static func create(from hotel: Hotel) -> RoomSectionViewModel {
        let roomImageURLs = hotel.roomImages.compactMap { $0.sizedImageURL(withSize: .medium) }
        let hotelInformationViewModel = HotelInformationViewModel(
            backgroundColour: hotel.brand.segmentedControlBackgroundColor,
            selectorColor: hotel.brand.selectorColor,
            normalTextAttributes: hotel.brand.segmentedTextNormalAttributes,
            selectedTextAttributes: hotel.brand.segmentedTextSelectedAttributes,
            informationSegments: hotel.brand.infoSegments
        )

        let roomContentViewModels = RoomSectionViewModel.createAllRoomContentViewModels(from: hotel)

        return RoomSectionViewModel(
            roomImageURLs: roomImageURLs,
            hotelInformationViewModel: hotelInformationViewModel,
            roomContentViewModels: roomContentViewModels
        )
    }

    private static func createAllRoomContentViewModels(from hotel: Hotel) -> [RoomContentViewModel] {
        var contentViewModels = [RoomContentViewModel]()

        for index in hotel.brand.infoSegments.indices {
            guard let roomContent = loadContentFor(index: index, hotel: hotel) else { continue }

            contentViewModels.append(roomContent)
        }

        return contentViewModels
    }

    private static func loadContentFor(index: Int, hotel: Hotel) -> RoomContentViewModel? {
        let segments = hotel.brand.infoSegments

        guard index < segments.count else { return nil }

        let hotelInfo = segments[index]

        var contentString: String {
            if index != 2 {
                return hotelInfo.content
            }

            guard let rate = hotel.rates.first(where: { $0.wifiUpsells != nil }), let wifiUpsells = rate.wifiUpsells else {
                return hotelInfo.content
            }


            let paragraphs = wifiUpsells.map({
                $0
                    .legend + ($0.price.localizedValue.isEmpty ? "" : "\n" + $0.price.localizedValue) +
                    ($0.itemDescription.isEmpty ? "" : "\n\n") + $0.itemDescription
            })

            return hotelInfo.content + "\n\n" + paragraphs.joined(separator: "\n\n")
        }

        var attributedContentText: NSAttributedString?
        if let attributedText = NSAttributedString.attributedStringWith(
            text: contentString,
            lineSpacing: 7,
            font: UIFont.Body(),
            textColor: .TintD2,
            textAlignment: .left
        ) {
            attributedContentText = attributedStringForSegment(
                attributedText: attributedText,
                index: index,
                highlights: Constants.CMS.highlightedRoomInformationStrings
            )
        }

        return RoomContentViewModel(
            regularContentDescription: contentString,
            attributedContentDescription: attributedContentText
        )
    }

    private static func attributedStringForSegment(
        attributedText: NSAttributedString,
        index: Int,
        highlights: [String]
    ) -> NSAttributedString {
        let string = NSMutableAttributedString(attributedString: attributedText)

        switch index {
        case 2:
            return string.attributedStringByHighlightingCharacters(highlights: highlights)

        default:
            return string.attributedStringByColoringBullets(with: UIColor.BasePurple)
        }
    }
}

// Room stuff

struct HotelInformationViewModel {
    var backgroundColour: UIColor
    var selectorColor: UIColor
    var normalTextAttributes: [NSAttributedString.Key: Any]
    var selectedTextAttributes: [NSAttributedString.Key: Any]
    var informationSegments: [HotelInformationSegment]
}

struct HotelInformationSegment: Hashable {
    var title: String
    var content: String
}

struct RoomContentViewModel {
    var regularContentDescription: String
    var attributedContentDescription: NSAttributedString?
}

struct RestaurantAndFoodViewModel {
    // MARK: - Properties

    var restaurantAndFoodCellViewModel: RestaurantCellViewModel
    var imageURLs: [URL]
    var foodContentViewModels: [FoodContentViewModel]
    var mealInformation: String?
    var foodInfo: [FoodInfo]

    // MARK: - Lifecycle

    static func create(from hotel: Hotel) -> RestaurantAndFoodViewModel {
        let restaurantCellViewModel = RestaurantCellViewModel.create(from: hotel)
        let foodContentViewModels = RestaurantAndFoodViewModel.loadFoodContentViewModels(with: hotel)

        let mealInformation = BookingDetails.sharedInstance.mealInformationText(forRate: hotel.rates.first)

        return RestaurantAndFoodViewModel(
            restaurantAndFoodCellViewModel: restaurantCellViewModel,
            imageURLs: hotel.restaurantImages.compactMap { $0.sizedImageURL(withSize: .medium) },
            foodContentViewModels: foodContentViewModels,
            mealInformation: mealInformation,
            foodInfo: FoodInfo.loadContents(from: hotel) ?? [FoodInfo]()
        )
    }

    static func loadFoodContentViewModels(with hotel: Hotel) -> [FoodContentViewModel] {
        var viewModels = [FoodContentViewModel]()

        guard let foodContent = hotel.foodOptionContentSections else { return viewModels }

        // Food section
        foodContent.forEach {
            let titleComponents = $0.title.components(separatedBy: "-")
            let foodContentViewModel = FoodContentViewModel(
                foodContentSectionIndex: .food,
                title: titleComponents.first ?? "",
                description: $0.body,
                restaurantImageURL: nil,
                imageShouldBeNormalHeight: false,
                imageShouldBeHidden: true
            )
            viewModels.append(foodContentViewModel)
        }
        // Restaurant section
        guard let restaurant = hotel.restaurant,
              let restaurantDescription = restaurant.description?.htmlStripped() else {
            return viewModels
        }
        let foodContentViewModelRestaurant = FoodContentViewModel(
            foodContentSectionIndex: .restaurant,
            description: restaurantDescription,
            restaurantImageURL: restaurant.imageURL,
            imageShouldBeNormalHeight: true,
            imageShouldBeHidden: false
        )
        viewModels.append(foodContentViewModelRestaurant)
        return viewModels
    }
}

struct ParkingSectionViewModel {
    var title: String
    var imageURLS: [URL]
    var parkingSummary: NSAttributedString?
    var shouldShowLargeParkingSummaryCellBottomPadding: Bool

    var parkingDescription: NSAttributedString?
    var hotelParkingDescription: String?

    static func create(from hotel: Hotel) -> ParkingSectionViewModel {
        let imageURLs = hotel.parkingImages.compactMap { $0.sizedImageURL(withSize: .medium) }

        var parkingSummary: NSAttributedString?
        if let summary = hotel.parkings.first?.summary {
            parkingSummary = summary.text.attributedString(
                with: summary.ranges,
                attributes: [
                    NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                    NSAttributedString.Key.font: UIFont.Heading4_Semibold()
                ]
            )
        }

        let shouldShowLargeParkingSummaryCellBottomPadding = hotel.parkings.first != nil

        var parkingDescription: NSAttributedString?
        if let parking = hotel.parkings.first {
            let description = parking.description(with: hotel.parkingDescription)
            parkingDescription = description.text.attributedString(
                with: description.ranges,
                attributes: [
                    NSAttributedString.Key.foregroundColor: UIColor.TintD2,
                    NSAttributedString.Key.font: UIFont.Heading4_Semibold()
                ]
            )
        }

        var hotelParkingDescription: String?
        if let parkingDescription = hotel.parkingDescription {
            hotelParkingDescription = parkingDescription.htmlStripped().trimmingCharacters(in: .whitespacesAndNewlines)
        }

        return ParkingSectionViewModel(
            title: PILocalizedString("hotelDetailsParkingSectionTitle", comment: "Hotel details: parking section title"),
            imageURLS: imageURLs,
            parkingSummary: parkingSummary,
            shouldShowLargeParkingSummaryCellBottomPadding: shouldShowLargeParkingSummaryCellBottomPadding,
            parkingDescription: parkingDescription,
            hotelParkingDescription: hotelParkingDescription
        )
    }
}

struct RestaurantCellViewModel {
    var backgroundColour: UIColor
    var selectorColor: UIColor
    var normalTextAttributes: [NSAttributedString.Key: Any]
    var selectedTextAttributes: [NSAttributedString.Key: Any]
    var restaurantTitle: String
    var restaurantFoodContentAvailable: Bool
    var restaurantDisclaimer: String?
    // var restaurantInfo: RestaurantInfo?

    static func create(from hotel: Hotel) -> RestaurantCellViewModel {
        var restaurantTitle: String = ""
        if let restaurant = hotel.restaurant {
            let restaurantName = restaurant.name.isEmpty ? PILocalizedString(
                "hotelDetailsRestaurantPlaceholder",
                comment: "Hotel details: restaurant name placeholder"
            ) : restaurant.name
            let restaurantSegmentTitle = PILocalizedString(
                "hotelDetailsAboutRestaurant",
                comment: "Hotel details: about restaurant title"
            ) + " " + restaurantName
            restaurantTitle = restaurantSegmentTitle
        }

        var restaurantFoodContentAvailable: Bool = false
        if let foodOptionContentSections = hotel.foodOptionContentSections {
            restaurantFoodContentAvailable = foodOptionContentSections.isNotEmpty
        }

        // IMPORTANT: The main restaurant disclaimer is the same Breakfast disclaimer
        let disclaimer = hotel.restaurant?.menus?
            .first { $0.title.lowercased().contains(PILocalizedString("Breakfast").lowercased()) }?.disclaimer

        return RestaurantCellViewModel(
            backgroundColour: .BaseWhite,
            selectorColor: hotel.brand.selectorColor,
            normalTextAttributes: hotel.brand.segmentedTextAttributes,
            selectedTextAttributes: hotel.brand.segmentedTextAttributes,
            restaurantTitle: restaurantTitle,
            restaurantFoodContentAvailable: restaurantFoodContentAvailable,
            restaurantDisclaimer: disclaimer
        )
    }
}

struct FoodInfo {
    var title: String
    var body: String

    static func loadContents(from hotel: Hotel) -> [FoodInfo]? {
        guard let foodOptionContentSections = hotel.foodOptionContentSections else { return nil }

        var foodContents = [FoodInfo]()
        for contentSection in foodOptionContentSections {
            foodContents.append(FoodInfo(title: contentSection.title, body: contentSection.body))
        }

        return foodContents
    }
}

typealias TripAdvisorSubRating = (name: String, value: Double, image: UIImage)
typealias TripAdvisorAward = (name: String, imageURL: URL?)

struct TripAdvisorViewModel {
    var rating: Double
    var ratingDescription: String
    var numberOfReviews: Int
    var image: UIImage
    var subRatings: [TripAdvisorSubRating]
    var awards: [TripAdvisorAward]

    static func create(from tripAdvisorDetails: TripAdvisorDetails?) -> TripAdvisorViewModel? {
        guard let tripAdvisorDetails = tripAdvisorDetails else { return nil }

        return TripAdvisorViewModel(
            rating: tripAdvisorDetails.rating,
            ratingDescription: tripAdvisorDetails.ratingDescription,
            numberOfReviews: tripAdvisorDetails.numberOfReviews,
            image: TripAdvisorDetails.image(with: tripAdvisorDetails.rating),
            subRatings: tripAdvisorDetails.subRatings.compactMap { TripAdvisorSubRating(
                name: $0.localisedName,
                value: $0.value,
                image: TripAdvisorDetails.image(with: $0.value)
            ) },
            awards: tripAdvisorDetails.awards.compactMap { TripAdvisorAward(name: $0.awardType, imageURL: $0.imageURL) }
        )
    }
}

// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*



// 🎪*=*=*=*=*=*=* Presenter *=*=*=*=*=*=*🎪
// MARK: Presenter Protocols
protocol HotelDetailsPresenterProtocol {
    var screenName: String { get }
    var screenType: String { get }
    var shouldTrackScreen: Bool { get }
    var hotelDetailsViewModel: HotelDetailsViewModel? { get }

    var continueViewCanShowAtBottom: Bool { get }

    func hotelUpdated()
    func hotelUpdateFailed(with error: Error)
    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int)
    func criteriaWasUpdated(to newCriteria: Criteria)
    func closeCurrentOverlay()
    func showHoldBookingError()
    func tripAdvisorRatingTapped()
    func showLoadingIndicator()
    func hideLoadingIndicator()
}

// 🥾*=*=*=*=*=*=* Event Handler *=*=*=*=*=*=*🥾
protocol HotelDetailsEventHandler {
    func viewIsAppearing()
    func viewDidFinishLoading()

    func roomSectionIndexDidChange(to imageTag: ImageTag)
    func hotelMoreInformationButtonDidTap()
    func discountCodeButtonDidTap()
    func hotelFacilitiesButtonDidTap()
    func locationInformationButtonDidTap()
    func mapPreviewDidTap()
    func directionsButtonDidTap()
    func backButtonDidTap()
    func openPrivacyPolicy()
    func callHotelDidTap()
    func emailButtonDidTap()
    func showCalendarDidTap()
    func showGuestsAndRoomsDidTap()
    func showCarousel(index: Int, andDelegate delegate: FullScreenImageViewerRouterDelegate)
    func checkAvailabilityButtonTapped()
    func scrollToRateSection(rateSection: RateSection)
    func selectedRate(withRateID rateID: UUID, lettingType: String?)
    func showHotelsNearbyButtonTapped()
    func logoutButtonDidTap()

    func selectedShowDisabledAccess()
    func showOurRoomsTapped(lettingType: String?)
    func parkingInfoDidTap()
    func dismissCoronavirusInformationBannerTapped()
}
// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*



// 🗺*=*=*=*=*=*=* Router *=*=*=*=*=*=*🗺
// MARK: Router Protocols
protocol HotelDetailsRouterProtocol {
    func handleBARTDowntimeError(errorToCheck: Error)
    func goBack(withCriteriaToCheck criteriaToCheck: Criteria)
    func showCalendar(withArrivalDate arrivalDate: Date, andNumberOfNights numberOfNights: Int)
    func goBackToResultsScreenIfPossible()
    func goBackToHomeScreen()
    func showGuestsAndRooms()
    func selectedRate(
        withRooms rooms: [Room]?,
        accessibleRoomImages: [URL],
        twinRoomImages: [URL],
        withUpsells: Bool,
        andShouldShowRoomSelectionScreen shouldShowRoomSelectionScreen: Bool
    )
    func showHoldRateError()
    func showDisabledAccess()
    func showBlockerAlert(withTitle title: String, body: String, andEmailSubject emailSubject: String?)
    func showOurRooms(with hotel: Hotel, lettingType: String?)
}
// MARK: Router Delegate
protocol HotelDetailsRouterDelegate: AnyObject {
    func cancelButtonDidTap(sender: UIViewController?)
}
// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*


// 🤖*=*=*=*=*=*=* Interactor *=*=*=*=*=*=*🤖
// MARK: Interactor Protocols
protocol HotelDetailsInteractorProtocol {
    var viewModel: HotelDetailsViewModel? { get }
    var directionsViewModel: DirectionsViewModel? { get }
    var discountCodeViewModel: HotelDetailsDiscountCodeViewModel? { get set }

    var hotel: Hotel? { get }
    var phoneNumber: String? { get }
    var suggestion: Suggestion? { get }
    var carouselImageURLs: [URL] { get }
    var roundelDesigns: [RoundelDesign]? { get }
    var accessibleRoomImages: [URL]? { get }
    var twinRoomImages: [URL]? { get }
    var criteria: Criteria { get }
    var arrivalDate: Date { get }
    var numberOfNights: Int { get }
    var bookingAllowed: Bool { get }
    var hasUpsells: Bool { get }
    var userIsAllowedToMakeBookings: Bool { get }

    var screenName: String { get }
    var screenType: String { get }
    var shouldTrackScreen: Bool { get }
    var shouldShowYouNeedToObtainCVVInformation: Bool { get }

    func updateBookingAllowed(to bookingAllowed: Bool)
    func updateSuggestion(to suggestion: Suggestion)
    func applyUserEnteredDiscountCodeIfNeeded()
    func loadAvailability(datesChanged: Bool)
    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int)
    func criteriaChanged(to newCriteria: Criteria)

    func roomsForRateID(_ rateID: UUID, and lettingType: String?) -> [Room]
    func setCriteria(withRateID rateID: UUID, and lettingType: String?)
    func shouldShowEmployeeOfferInformation(for rateID: UUID) -> Bool
    func holdBooking(withRateID rateID: UUID, completion: @escaping (Bool) -> Void)

    func customerDismissedCoronavirusInformation()
    func validateUserCanBookRate(with id: UUID, completion: @escaping (Result<Bool>) -> Void)
    func userLoggedOut()
}
// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*


// MARK: Other bits 😷 We have no home 😢

struct SegmentSelectorData {
    let segments: [HotelInformationSegment]
    let selectedIndex: Int
}

typealias TextAndRanges = (text: String, ranges: [NSRange])

enum RateSection: Int {
    case standard
    case biggerRoom
    case accessible

    static func rateSection(for lettingType: LettingType) -> RateSection {
        switch lettingType {
        case .single, .double, .family, .twin:
            return .standard
        case .accessible:
            return .accessible
        case .premierPlus:
            return .biggerRoom
        case .businessRooms:
            return .biggerRoom
        }
    }
}

class AnimationHelper {
    var pointsBeforeTriggeringAnimation: CGFloat = 0
    var pointsBeforeTriggeringButtonsAnimation: CGFloat = 0
    var animationLength: CGFloat = 0
    var cachedTopImageView: UIImageView?
    var cacheTopImageHeight: CGFloat = 0
}

enum HotelDetailRow: String {
    case hotelDetailNameAddressCell
    case hotelDistanceCell
    case hotelFewRoomsCell
    case hotelDetailsCriteriaSummaryCell
    case errorCell
    case hotelDetailUserActionErrorCell
    case rateCell
    case tripAdvisorCell
    case hotelFacilitiesCell
    case hotelMoreInfoCell
    case hotelMapCell
    case hotelLocationInfoCell
    case hotelRoomPhotosCell
    case roomSegmentsCell
    case roomContentCell
    case hotelRestaurantPhotosCell
    case foodOptionsAndRestaurantSegmentsCell
    case foodContentCell
    case foodFreeBreakfastCell
    case hotelParkingPhotosCell
    case callHotelCell
    case hotelDetailCarouselCell
    case availabilityUnchecked
    case discountCodeCell
    case checkInOut
    case tripAdvisorDetails
    case coronavirusMessaging
    case cotNotAvailableMessage
}
