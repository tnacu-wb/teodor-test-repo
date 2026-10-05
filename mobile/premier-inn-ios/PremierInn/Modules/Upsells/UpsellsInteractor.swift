//
//  UpsellsInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

typealias RoomMealPreference = (roomNumber: Int, numberOfSelections: Int?, upsellCode: String?, extraUpsellsCodes: [String]?)

private extension UpsellItem {
    var note: NSAttributedString? {
        switch (kidsHaveToPay, freeBreakfastTrigger) {
        case (false, true):
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.headIndent = 5
            paragraphStyle.tailIndent = -5

            let attributedString = NSAttributedString(
                string: PILocalizedString("upsellKidsEatFreeNote"),
                attributes: [
                    .font: UIFont.SubtextSmallStrong(),
                    .foregroundColor: UIColor.BaseBlack,
                    .paragraphStyle: paragraphStyle
                ]
            )
            return attributedString
        default:
            return nil
        }
    }

    var attributedItemDescription: NSAttributedString {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 5

        let attributedString = NSAttributedString(
            string: itemDescription,
            attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.TintD1, .paragraphStyle: paragraphStyle]
        )
        return attributedString
    }
}

class UpsellsInteractor {
    // MARK: - Types

    struct SummaryViewModel: UpsellsBookingSummaryViewModel {
        let hotelImageUrl: URL?
        let hotelName: String?
        let stayDetails: String
        let totalCost: String
    }

    struct AllowancesViewModel: UpsellsAllowancesViewModel {
        let title: String
        let message: String
        let allowances: [Allowance]
    }

    struct RestaurantViewModel: UpsellsRestaurantViewModel {
        let restaurantSectionTitle: String
        let restaurantMainImageUrl: URL?
        let restaurantAllowanceMessage: String?
        let restaurantImageUrls: [URL]?
        let menuViewModels: [MenuInfo]?
        let allergenInformationViewModels: [AllergenInformationViewModel]?
    }

    struct ViewModel: UpsellsViewModel {
        let screenName: String
        let summaryViewModel: UpsellsBookingSummaryViewModel?
        let upsellsAllowancesViewModel: UpsellsAllowancesViewModel?
        let restaurantViewModel: UpsellsRestaurantViewModel
        let roomViewModels: [UpsellsRoomViewModel]
        let extraUpsellsModels: [UpsellsRoomViewModel]
        let totalViewModel: UpsellsTotalViewModel?
        var upsellCloseOutViewModel: UpsellCloseOutBannerViewModel?
        let continueButtonTitle: String
        let hasFoodUpsells: Bool
    }

    struct RoomViewModel: UpsellsRoomViewModel {
        let name: NSAttributedString
        let description: String
        let collapsed: Bool
        let upsellsSummary: String?
        let upsellItems: [UpsellItemViewModel]?
    }

    struct ItemViewModel: UpsellItemViewModel {
        let title: String
        let costSummary: NSAttributedString?
        let description: NSAttributedString
        let note: NSAttributedString?
        let infoMessage: String?
        let selected: Bool
        var quantity: Int?
        var maxSelected: Int?
        let isHidden: Bool
    }

    struct TotalViewModel: UpsellsTotalViewModel {
        let showDoneButton: Bool
        let showBreakdownTotalCost: Bool
        let breakdownRows: [(title: String, value: String)]
        let breakdownTotalCostLabel: String
        let breakdownTotalCostValue: String
        let totalCost: Cost?
        let rateText: String
        let paymentCardImageUrls: [URL]?
        let summaryBreakdownLabelSetup: LabelSetup
    }

    // MARK: - Properties

    internal let rooms: [Room]
    private let hotel: Hotel
    private let upsells: [UpsellItem]
    private let extraUpsells: [UpsellItem]?
    private let sessionId: String?
    private let numberOfNights: Int?
    private var initialSelectionMade: Bool = false
    private var activeRoom: Int? = 0
    private var activeRoomForExtraUpsell: Int? = 0
    private var closeOutState: CloseOutState?

    private let reservation: Reservation?
    internal let bookingDetails: BookingDetails?
    private let isAmendFlow: Bool?
    internal let amendOperaDetails: AmendOperaDetails?
    private let requestsManager = RequestsManager()
    internal let analyticsManager: AnalyticsPromotionsTrackable

    // MARK: - Computed Properties

    private var roomMealPreferences: [RoomMealPreference] {
        didSet {
            bookingDetails?.roomMealCombos = roomMealCombos
            bookingDetails?.roomExtraPackages = roomExtraPackages
        }
    }

    private var summaryViewModel: UpsellsBookingSummaryViewModel? {
        guard let bookingDetails = bookingDetails else { return nil }
        guard let totalCost = bookingDetails.roomAndMealCost?.localizedValue else { return nil }

        return SummaryViewModel(
            hotelImageUrl: hotel.primaryImages.first?.sizedImageURL(withSize: .small),
            hotelName: hotel.name,
            stayDetails: "\(bookingDetails.criteria.reviewDatesSummaryShort) • \(bookingDetails.criteria.reviewRoomsSummary)",
            totalCost: totalCost
        )
    }

    // MARK: - Lifecycle

    init(
        with rooms: [Room],
        roomMealPreferences: [RoomMealPreference] = [],
        hotel: Hotel,
        upsellOptions: [UpsellItem],
        extraUpsells: [UpsellItem]?,
        sessionId: String? = nil,
        and bookingDetails: BookingDetails? = nil,
        reservation: Reservation? = nil,
        isAmendFlow: Bool? = nil,
        amendOperaDetails: AmendOperaDetails? = nil,
        upsellCloseOutState: CloseOutState? = nil,
        analyticsManager: AnalyticsPromotionsTrackable = AnalyticsManager.shared
    ) {
        // defer ensures that the didSet will be called, otherwise it doesn't call from the init
        defer {
            self.roomMealPreferences = roomMealPreferences
        }

        self.rooms = rooms
        self.roomMealPreferences = roomMealPreferences
        self.hotel = hotel
        self.upsells = upsellOptions.filter { $0 != UpsellItem.freeChildrensBreakfast }
        self.extraUpsells = extraUpsells
        self.sessionId = sessionId
        self.numberOfNights = reservation?.nights
        self.bookingDetails = bookingDetails
        self.reservation = reservation
        self.isAmendFlow = isAmendFlow
        self.closeOutState = upsellCloseOutState
        if roomMealPreferences.isNotEmpty && rooms.count > 1 {
            activeRoom = nil
            activeRoomForExtraUpsell = nil
        }
        self.amendOperaDetails = amendOperaDetails
        self.analyticsManager = analyticsManager
    }

    // MARK: - Private Methods

    private func roomName(for roomIndex: Int) -> NSAttributedString {
        let roomName = NSMutableAttributedString(
            string: "\(PILocalizedString("Room")) \(roomIndex + 1)",
            attributes: [.foregroundColor: UIColor.BaseBlack, .font: UIFont.Heading1_Semibold()]
        )

        if let leadGuest = rooms[safe: roomIndex]?.leadGuest {
            roomName.append(NSAttributedString(
                string: " (\(leadGuest.displayName))",
                attributes: [.font: UIFont.Heading3_Semibold()]
            ))
        }

        return roomName
    }

    private func upsellsSummary(for roomIndex: Int) -> String? {
        var upsellSummary: String?

        guard let room = rooms[safe: roomIndex] else { return nil }

        let roomPreferences = roomMealPreferences.filter({
            $0.roomNumber == roomIndex &&
            $0.extraUpsellsCodes == nil
        })

        var roomHasFreeBreakfast = false
        for preference in roomPreferences {
            guard let meal = upsells.first(where: { $0.id == preference.upsellCode }) else { continue }
            guard let quantity = preference.numberOfSelections else { continue }

            if upsellSummary == nil {
                upsellSummary = "\(quantity) x \(meal.title.capitalized)"
            } else {
                upsellSummary?.append("\n\(quantity) x \(meal.title.capitalized)")
            }

            if meal.freeBreakfastTrigger == true && room.children > 0 {
                roomHasFreeBreakfast = true
            }
        }
        if roomHasFreeBreakfast {
            upsellSummary?.append(PILocalizedString("upsellKidsEatSummaryNote", comment: ""))
        }
        return upsellSummary
    }

    private func extraUpsellsSummary(for roomIndex: Int) -> String? {
        guard rooms.indices.contains(roomIndex) else { return nil }

        guard let extraUpsellsCodes = roomMealPreferences.first(where: {
            $0.roomNumber == roomIndex &&
            $0.extraUpsellsCodes != nil
        })?.extraUpsellsCodes else { return nil }

        let upsellSummary = extraUpsellsCodes.reduce("", { summary, upsellCode in
            guard let userSelectedUpsell = extraUpsells?.first(where: { upsellCode == $0.id }) else { return summary }

            return summary + "\(userSelectedUpsell.title.capitalized)\n"
        })

        return upsellSummary
    }

    private func extraUpsellsViewModels(for roomIndex: Int) -> [UpsellItemViewModel]? {
        guard let extraUpsells = extraUpsells, extraUpsells.isNotEmpty else { return nil }

        let roomMealPreferences = self.getPreferencesFromRoomMealCombos(combos: bookingDetails?.roomExtraPackages ?? [])

        return extraUpsells.compactMap { upsell in
            ItemViewModel(
                title: upsell.legend,
                costSummary: costSummary(for: roomIndex, upsell: upsell),
                description: upsell.attributedItemDescription,
                note: nil,
                infoMessage: nil,
                selected: roomMealPreferences
                .first(where: { $0.roomNumber == roomIndex && (($0.extraUpsellsCodes?.contains(upsell.id)) == true) }) !=
                nil,
                quantity: nil,
                maxSelected: nil,
                isHidden: false
            )
        }
    }

    private func upsellItemViewModels(for roomIndex: Int) -> [UpsellItemViewModel]? {
        guard roomIndex == activeRoom || rooms.count == 1 else { return nil }
        guard let room = rooms[safe: roomIndex] else { return nil }

        let roomMealPreferences = self.getPreferencesFromRoomMealCombos(combos: bookingDetails?.roomMealCombos ?? [])

        return upsells.compactMap { upsell in
            let upsellQuantity = roomMealPreferences
                .first(where: { $0.roomNumber == roomIndex && $0.upsellCode == upsell.id })?.numberOfSelections
            let isFreeBreakfastPromotion = upsell.id == UpsellItemOperaId.freeBreakfastPromotion.rawValue

            return ItemViewModel(
                title: upsell.legend,
                costSummary: costSummary(for: roomIndex, upsell: upsell),
                description: upsell.attributedItemDescription,
                note: room.children > 0 ? upsell.note : nil,
                infoMessage: upsell.id == UpsellItemOperaId.mealDeal
                .rawValue && mealDealActivated ?
                PILocalizedString(
                    "If you choose a Meal Deal you'll receive a 2-course dinner and drink instead of an allowance"
                ) :
                nil,
                selected: roomMealPreferences
                .first(where: { $0.roomNumber == roomIndex && $0.upsellCode == upsell.id }) != nil,
                quantity: upsellQuantity ?? 0,
                maxSelected: maxUpsellStepperCount(
                    adults: room.adults,
                    roomId: roomIndex,
                    currentValue: upsellQuantity ?? 0
                ),
                isHidden: isFreeBreakfastPromotion
            )
        }
    }

    func costSummary(for roomIndex: Int, upsell: UpsellItem) -> NSAttributedString {
        let textAndRanges = priceTextAndRanges(upsell: upsell)

        guard let range = textAndRanges.ranges.first else { return NSAttributedString(string: "NA") }

        let attributedCostSummary = NSMutableAttributedString(
            string: textAndRanges.text,
            attributes: [.foregroundColor: UIColor.TintD2, .font: UIFont.Body()]
        )

        attributedCostSummary.addAttribute(.font, value: UIFont.Heading4_Semibold(), range: range)

        return NSAttributedString(attributedString: attributedCostSummary)
    }

    private var roomViewModels: [UpsellsRoomViewModel] {
        var roomViewModels = [UpsellsRoomViewModel]()

        for (index, room) in rooms.enumerated() {
            var titleDescription = room.bookingSummaryRoomDescription
            let titleDescriptionComponents = titleDescription.components(separatedBy: ", ")

            if let roomLetting = bookingDetails?.roomLettings?[safe: index],
               let selectedRoom = roomLetting.options?.first(where: { $0.lettingType == room.lettingType }) ?? roomLetting
               .options?.first,
               selectedRoom.silentSubstitution == false,
               let guestSummary = titleDescriptionComponents[safe: 0] {
                let nonSilentRoomLabel = SettingsManager.sharedInstance
                    .roomLabelFor(lettingType: selectedRoom.lettingType ?? "")
                titleDescription = guestSummary + ", " + nonSilentRoomLabel
            }

            let roomViewModel = RoomViewModel(
                name: roomName(for: index),
                description: titleDescription,
                collapsed: index != activeRoom && rooms.isNotEmpty,
                upsellsSummary: upsellsSummary(for: index),
                upsellItems: upsellItemViewModels(for: index)
            )
            roomViewModels.append(roomViewModel)
        }

        return roomViewModels
    }

    private var extraUpsellsModels: [UpsellsRoomViewModel] {
        guard extraUpsells?.count ?? 0 > 0 else { return [] }

        let extraItemsModels = rooms.enumerated().compactMap { index, room in
            RoomViewModel(
                name: roomName(for: index),
                description: room.bookingSummaryRoomDescription.lowercased(),
                collapsed: index != activeRoomForExtraUpsell && rooms.isNotEmpty,
                upsellsSummary: extraUpsellsSummary(for: index),
                upsellItems: extraUpsellsViewModels(for: index)
            )}

        return extraItemsModels
    }
}

extension UpsellsInteractor: UpsellsInteractorProtocol {
    // MARK: - Computed Properties

    var viewModel: UpsellsViewModel? {
        ViewModel(
            screenName: PILocalizedString("customiseYourStay", comment: ""),
            summaryViewModel: summaryViewModel,
            upsellsAllowancesViewModel: upsellsAllowancesViewModel,
            restaurantViewModel: restaurantViewModel,
            roomViewModels: roomViewModels,
            extraUpsellsModels: extraUpsellsModels,
            totalViewModel: totalViewModel,
            upsellCloseOutViewModel: upsellsCloseOutViewModel,
            continueButtonTitle: PILocalizedString("Continue"),
            hasFoodUpsells: bookingDetails?.rate?.foodUpsells?.isNotEmpty ?? false
        )
    }

    var roomMealCombos: [RoomMealCombo]? {
        bookingDetails?.roomMealCombos = roomMealPreferences.compactMap { roomMealPreference in
            guard let upsell = upsells.first(where: { $0.id == roomMealPreference.upsellCode }) else { return nil }
            guard let mealUpsellQuantity = roomMealPreference.numberOfSelections else { return nil }

            return (roomMealPreference.roomNumber, upsell, mealUpsellQuantity)
        }

        return bookingDetails?.roomMealCombos
    }

    var roomExtraPackages: [RoomMealCombo]? {
        var packages: [RoomMealCombo] = []

        for roomMealPreference in roomMealPreferences {
            for upsellCode in roomMealPreference.extraUpsellsCodes ?? [] {
                guard let upsell = extraUpsells?.first(where: { $0.id == upsellCode }) else { continue }
                let extraUpsellQuantity = UpsellQuantity(upsellItem: upsell).quantityPerUpsell

                packages.append((roomMealPreference.roomNumber, upsell, extraUpsellQuantity))
            }
        }

        bookingDetails?.roomExtraPackages = packages

        return packages
    }


    // Amend flow properties
    var amendedRooms: [Room] { self.rooms }

    var amendAnalytics: AmendAnalyticsValues? {
        guard let reservation = reservation else { return nil }

        let combinedUpsells = upsells + (extraUpsells ?? [])

        return (
            reservation.confirmationNumber,
            combinedUpsells.compactMap { $0.legend }.joined(separator: ", "),
            combinedUpsells.compactMap { $0.id }.joined(separator: ", ")
        )
    }

    var promotionsAnalytics: PIDictionary? {
        analyticsManager.getPromotionsAnalyticsDict(with: bookingDetails)
    }

    // MARK: - Public Methods

    func selected(upsellAt upsellIndex: Int, in roomIndex: Int, numberOfSelection: Int) {
        guard let upsell = upsells[safe: upsellIndex] else { return }

        roomMealPreferences.removeAll(where: {
            $0.roomNumber == roomIndex &&
            $0.upsellCode == upsell.id &&
            $0.extraUpsellsCodes == nil
        })

        guard numberOfSelection > 0 else { return }

        roomMealPreferences.append((roomIndex, numberOfSelection, upsell.id, nil))
    }

    func toggledExtraUpsell(toggle: Bool, upsellIndex: Int, in roomIndex: Int) {
        guard let upsell = extraUpsells?[safe: upsellIndex] else { return }

        var existingExtraUpsellCodes = roomMealPreferences
            .filter { $0.roomNumber == roomIndex }
            .compactMap { $0.extraUpsellsCodes }
            .flatMap { $0 }

        roomMealPreferences.removeAll(where: {
            $0.roomNumber == roomIndex &&
            $0.extraUpsellsCodes != nil
        })

        if toggle {
            existingExtraUpsellCodes.append(upsell.id)
            roomMealPreferences.append((roomIndex, nil, nil, existingExtraUpsellCodes))
        } else {
            if let index = existingExtraUpsellCodes.firstIndex(of: upsell.id) {
                existingExtraUpsellCodes.remove(at: index)
                roomMealPreferences.append((roomIndex, nil, nil, existingExtraUpsellCodes))
            }
        }
    }

    func selected(change roomIndex: Int) {
        activeRoom = roomIndex
    }

    func selectedExtraUpsell(change roomIndex: Int) {
        activeRoomForExtraUpsell = roomIndex
    }

    func saveAncillaries(completion: @escaping (Bool, Error?) -> Void) {
        guard let bookingDetails = bookingDetails else {
            completion(false, nil)
            return
        }

        requestsManager.saveUpsellsToBookingFlow(bookingDetails: bookingDetails) { [weak self] isSuccess, error in
            guard let self else { return completion(false, nil) }

            if isSuccess {
                bookingDetails.previousRoomMealCombos = roomMealCombos
                bookingDetails.previousRoomExtraPackages = roomExtraPackages
            } else {
                let allCombos = (bookingDetails.previousRoomMealCombos ?? []) +
                    (bookingDetails.previousRoomExtraPackages ?? [])
                roomMealPreferences = getPreferencesFromRoomMealCombos(combos: allCombos)
            }
            completion(isSuccess, error)
        }
    }

    func resetUsersMealSelection() {
        let allCombos = (bookingDetails?.previousRoomMealCombos ?? []) + (bookingDetails?.previousRoomExtraPackages ?? [])
        roomMealPreferences = getPreferencesFromRoomMealCombos(combos: allCombos)
    }

    private func getPreferencesFromRoomMealCombos(combos: [RoomMealCombo]) -> [RoomMealPreference] {
        var mealPrefs: [RoomMealPreference] = []
        var extraPrefs: [RoomMealPreference] = []

        let groupedByRoom = Dictionary(grouping: combos) { $0.roomNumber }

        for (roomNumber, combos) in groupedByRoom {
            var tempExtraUpsellsCodes: [String] = []

            for combo in combos {
                if combo.meal.foodUpsell {
                    let mealPref: RoomMealPreference = (
                        roomNumber: roomNumber,
                        numberOfSelections: combo.quantity,
                        upsellCode: combo.meal.id,
                        extraUpsellsCodes: nil
                    )
                    mealPrefs.append(mealPref)
                } else if combo.meal.isExtraUpsell {
                    tempExtraUpsellsCodes += [combo.meal.id]
                    extraPrefs.removeAll(where: { $0.roomNumber == roomNumber })

                    let extraUpsellPref: RoomMealPreference = (
                        roomNumber: roomNumber,
                        numberOfSelections: combo.quantity,
                        upsellCode: nil,
                        extraUpsellsCodes: tempExtraUpsellsCodes
                    )
                    extraPrefs.append(extraUpsellPref)
                }
            }
        }

        return mealPrefs + extraPrefs
    }

    func amendPackages(completion: @escaping (Bool?, Error?) -> Void) {
        guard let basketReference = amendOperaDetails?.temporaryBasketReference,
              let arrivalDate = bookingDetails?.arrivalDate,
              let departureDate = bookingDetails?.departureDate,
              let rooms = bookingDetails?.criteria.rooms else {
            completion(nil, AmendReviewUpsellsError.amendPackagesFailed)
            return
        }

        let packagesVariables = AmendPackagesDetail(
            basketReferenceId: basketReference,
            hotelId: hotel.code,
            arrivalDate: arrivalDate,
            departureDate: departureDate,
            roomsSelections: currentlySelectedUpsells,
            previousRoomsSelections: reservation?.upsellItems,
            rooms: rooms
        )

        requestsManager.amendPackages(packagesDetails: packagesVariables) { isSuccess, error in
            if error != nil {
                return completion(nil, AmendReviewUpsellsError.amendPackagesFailed)
            }
            if let success = isSuccess, success == true {
                return completion(true, nil)
            }
            completion(false, nil)
        }
    }

    private var currentlySelectedUpsells: [UpsellItem]? {
        guard let rooms = bookingDetails?.criteria.rooms else { return nil }

        let roomMealPreferences = roomMealPreferences
        let availableUpsells = reservation?.availableUpsells

        let meals: [UpsellItem] = roomMealPreferences.compactMap { roomMealPreference in
            let roomIndex = roomMealPreference.roomNumber
            guard roomMealPreference.extraUpsellsCodes == nil else { return nil } // extras in the next loop
            guard let room = rooms[safe: roomIndex] else { return nil }

            let mealCode = roomMealPreference.upsellCode
            let quantity = roomMealPreference.numberOfSelections

            guard var upsellItem = availableUpsells?.first(where: { $0.id == mealCode }) else { return nil }

            upsellItem.adults = quantity
            upsellItem.children = room.children
            upsellItem.roomId = room.roomId

            return upsellItem
        }

        var extras: [UpsellItem] = []
        roomMealPreferences.forEach { roomMealPreference in
            let roomIndex = roomMealPreference.roomNumber
            guard let room = rooms[safe: roomIndex] else { return }

            for upsellCode in roomMealPreference.extraUpsellsCodes ?? [] {
                guard var extraItem = availableUpsells?.first(where: { $0.id == upsellCode }) else { continue }

                let extraUpsellQuantity = UpsellQuantity(upsellItem: extraItem).quantityPerUpsell

                extraItem.quantity = extraUpsellQuantity
                extraItem.roomId = room.roomId

                extras.append(extraItem)
            }
        }

        return meals + extras
    }
}

// MARK: Allowances View Model
extension UpsellsInteractor {
    private var upsellsAllowancesViewModel: AllowancesViewModel? {
        guard let company = UserSessionManager.sharedInstance.currentUser?.company else { return nil }
        guard let card = bookingDetails?.paymentMethod?.method else { return nil }
        guard card == UserSessionManager.sharedInstance.currentUser?
              .centrallyStoredBusinessCard || card == UserSessionManager.sharedInstance.currentUser?.paymentPreference?.card
        else { return nil }
        guard card.cardNotPresentRequired == true else { return nil }

        // always nil - defaults to true
        let dinnerAllowanceAvailableAtHotel: Bool = {
            card.isBusiness ? bookingDetails?.hotel?.cnpAuthorisation?.dinnerAvailable ?? true : bookingDetails?.hotel?
                .cnpAuthorisation?.dinnerAvailableNonBa ?? true
        }()

        let upsellItemName: String? = {
            guard company.isMealDealAllowed else { return nil }
            guard upsells.first(where: { $0.id == UpsellItemOperaId.mealDeal.rawValue }) != nil else { return nil }
            return PILocalizedString("Meal Deal")
        }()

        var allowanceRows: [Allowance] = []

        if dinnerAllowanceAvailableAtHotel == true, let dinnerAllowance = dinnerAllowance, dinnerAllowance.amount != 0 {
            allowanceRows.append((BBUpsells.mealAllowanceDescription(
                for: upsellItemName,
                and: dinnerAllowance,
                encouraging: company.bookingAllowances?.allowAlcohol ?? false,
                mealDealActivated: mealDealActivated
            ), AccessibilityIdentifiers.Upsells.businessAllowanceDinnerTitle))
        }

        if company.bookingAllowances?.allowCarParking ?? false {
            allowanceRows.append((
                BBUpsells.parkingAllowanceDescription,
                AccessibilityIdentifiers.Upsells.businessAllowanceParkingTitle
            ))
        }

        return allowanceRows.isNotEmpty ? AllowancesViewModel(
            title: PILocalizedString("upsellYourBusinessAllowancesTitle"),
            message: PILocalizedString("upsellYouCanUseAllowanceOnBusinessCard"),
            allowances: allowanceRows
        ) : nil
    }

    private var dinnerAllowance: Cost? {
        UserSessionManager.sharedInstance.currentUser?.company?.allowance(for: hotel)
    }

    private var mealDealActivated: Bool {
        guard dinnerAllowance?.amount.doubleValue ?? 0 > 0 else { return false }
        guard UserSessionManager.sharedInstance.currentUser?.company?.isMealDealAllowed == true else { return false }
        return roomMealPreferences.contains(where: { $0.upsellCode == UpsellItemOperaId.mealDeal.rawValue })
    }
}

// MARK: - Restaurant View Model
extension UpsellsInteractor {
    private var restaurantViewModel: RestaurantViewModel {
        RestaurantViewModel(
            restaurantSectionTitle: restaurantSectionTitle,
            restaurantMainImageUrl: hotel.restaurant?.imageURL,
            restaurantAllowanceMessage: restaurantAllowanceMessage,
            restaurantImageUrls: (bookingDetails?.rate?.foodUpsellsImages ?? []) + hotel.extrasImages,
            menuViewModels: menuViewModels,
            allergenInformationViewModels: allergenInfoViewModels
        )
    }

    private var restaurantSectionTitle: String {
        upsells.compactMap { $0.id }.contains(UpsellItemOperaId.mealDeal.rawValue) ? PILocalizedString(
            "upsellsRestaurantInfoHeaderTitle",
            comment: "Upsells info header title label"
        ) : PILocalizedString("Add breakfast")
    }

    private var restaurantMainImageUrl: URL? {
        UserSessionManager.sharedInstance.currentUser?.company == nil ? hotel.restaurant?.imageURL : nil
    }

    private var restaurantAllowanceMessage: String? {
        guard UserSessionManager.sharedInstance.currentUser?.company != nil else { return nil }

        let allowanceExists = dinnerAllowance?.amount.doubleValue ?? 0 > 0

        return allowanceExists ?
        PILocalizedString("travelManagerApprovedMealsExcludedFromAllowanceNotice") :
        PILocalizedString("travelManagerApprovedMealsNotice")
    }

    private var menuViewModels: [MenuInfo]? {
        hotel.restaurant?.menus?.compactMap {
            guard let path = $0.path else { return nil }
            guard let baseUrl = URL(string: "https://premierinn.com") else { return nil }

            return ($0.title, baseUrl.appendingPathComponent(path))
        }
    }

    private var allergenInfoViewModels: [AllergenInformationViewModel]? {
        upsells.compactMap {
            guard let allergenInfo = $0.allergenInformation else { return nil }
            guard let allergenURL = URL(string: "https://premierinn.com" + allergenInfo.path) else { return nil }

            return (allergenInfo.name, allergenURL)
        }
    }

    private func maxUpsellStepperCount(adults: Int, roomId: Int, currentValue: Int) -> Int {
        // Get how many upsells the user has selected for their room
        var howManyUpsellsSelectedForRoom: Int {
            bookingDetails?.roomMealCombos?.filter({
                $0.roomNumber == roomId
            }).compactMap { $0.quantity }.reduce(0, +) ?? 0
        }
        // Finally using the current value for that stepper work out its max range for that specific upsell
       return abs((adults - howManyUpsellsSelectedForRoom) + currentValue)
    }
}

// MARK: - Total View Model
extension UpsellsInteractor {
    private var totalViewModel: UpsellsTotalViewModel? {
        TotalViewModel(
            showDoneButton: isAmendFlow ?? false,
            showBreakdownTotalCost: !(isAmendFlow ?? false),
            breakdownRows: breakdownRows,
            breakdownTotalCostLabel: breakdownTotalCostLabel,
            breakdownTotalCostValue: breakdownTotalCostValue,
            totalCost: totalCost,
            rateText: rateText,
            paymentCardImageUrls: paymentCardImageUrls,
            summaryBreakdownLabelSetup: summaryBreakdownLabelSetup
        )
    }

    private var breakdownRows: [(title: String, value: String)] {
        var rows = [(title: String, value: String)]()

        guard let nights = bookingDetails?.criteria.nights ?? numberOfNights else { return rows }

        let nightsDescription = Criteria.nightCountDescription(for: nights)

        if let bookingDetails = bookingDetails {
            let stayDescription = "\(PILocalizedString("bookingSummaryHotelStayLabel", comment: "Booking summary hotel stay label")): \(rateText)\(PILocalizedString("rate", comment: ""))\n(\(bookingDetails.criteria.nightsCountDescription), \(bookingDetails.criteria.roomsCountDescription))"
            let stayCost = bookingDetails.roomLettings?.totalCost?.localizedValue ?? ""

            rows.append((
                stayDescription,
                stayCost
            ))
        }

        if paidBreakfasts.isNotEmpty {
            rows.append(contentsOf: paidBreakfasts.map {
                let upsellTitle = "\($0.upsell.legend)\n(\(String.localizedStringWithFormat(PILocalizedString("%d guest(s)", comment: "Message shown for number of guests"), $0.count)), \(nightsDescription))"
                let upsellCostAmount = ($0.upsell.price.amount.doubleValue * Double($0.count)) * Double(nights)
                let upsellTotalCost = Cost(amount: upsellCostAmount, currencyCode: $0.upsell.price.currencyCode)

                return (upsellTitle, upsellTotalCost.localizedValue)
            })
        }

        if freeBreakfastCount > 0 {
            let freeBreakfastTitle = "\(PILocalizedString("bookingSummaryKidsBreakfastLabel", comment: "Booking summary kids breakfast label"))\n(\(String.localizedStringWithFormat(PILocalizedString("%d child(children)", comment: "Message shown for number of children"), freeBreakfastCount)), \(nightsDescription))"

            rows.append((
                freeBreakfastTitle,
                PILocalizedString("bookingSummaryKidsBreakfastValue", comment: "Booking summary kids breakfast value")
            ))
        }

        if let bookingDetails = bookingDetails {
            if extraItems.isNotEmpty {
                rows.append(contentsOf: extraItems.map {
                    let upsellTitle = bookingDetails.criteria.rooms.count == 1 ? $0.upsell.legend : "\($0.upsell.legend)\n(\(String.localizedStringWithFormat(PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"), $0.count)))"
                    let upsellCostAmount = $0.upsell.price.amount.doubleValue * Double($0.count)
                    let upsellTotalCost = Cost(amount: upsellCostAmount, currencyCode: $0.upsell.price.currencyCode)

                    return (upsellTitle, upsellTotalCost.localizedValue)
                })
            }
        }

        return rows
    }

    private var breakdownTotalCostLabel: String {
        bookingDetails != nil ? PILocalizedString("bookingConfirmationTotalPriceTitle", comment: "") : PILocalizedString(
            "footerViewBreakdownTotal",
            comment: ""
        )
    }

    private var breakdownTotalCostValue: String {
        if bookingDetails != nil {
            return totalCost?.localizedValue ?? ""
        }

        guard let numberOfNights = numberOfNights else { return "" }

        let mealCosts = paidBreakfasts.reduce(0, { runningTotal, breakfast in
            runningTotal + ((breakfast.upsell.price.amount.doubleValue * Double(breakfast.count)) * Double(numberOfNights))
        })

        let extraCosts = extraItems.reduce(0, { runningTotal, extra in
            runningTotal + (extra.upsell.price.amount.doubleValue * Double(extra.count))
        })

        guard let totalCost = reservation?.totalCost else { return "" }

        let prepaidAmount = reservation?.prepaidAmount?.amount.doubleValue ?? 0
        let outstandingAmount = ((totalCost.amount.doubleValue - prepaidAmount) + mealCosts + extraCosts)
        let outstandingCost = Cost(amount: outstandingAmount, currencyCode: totalCost.currencyCode)

        return outstandingCost.localizedValue
    }

    private var rateText: String {
        if let name = bookingDetails?.rate?.name(with: bookingDetails?.hotel?.brand) ?? reservation?.rate?.name {
            return name
        }

        return ""
    }

    private var totalCost: Cost? {
        guard bookingDetails != nil else {
            return reservation?.totalCost
        }

        let shouldShowCityTaxInformation: Bool = bookingDetails?.rate?.cityTaxRequired ?? false

        let totalCost: Cost? = {
            if shouldShowCityTaxInformation { return bookingDetails?.roomAndMealCost }

            guard let fullRoomcost = bookingDetails?.roomAndMealCost else { return nil }
            let extrasCost = bookingDetails?.extrasTotalCost ?? Cost(amount: 0.0, currencyCode: fullRoomcost.currencyCode)

            var total = fullRoomcost
            if let sum = total + extrasCost {
                total = sum
            }
            guard let cityTaxCost = bookingDetails?.rate?.rooms?.first?.options?.first?.cityTax else { return total }

            let totalCostMinusCityTax = Cost(
                amount: fullRoomcost.amount.doubleValue + extrasCost.amount.doubleValue - cityTaxCost.amount.doubleValue,
                currencyCode: fullRoomcost.currencyCode
            )

            return totalCostMinusCityTax
        }()

        return totalCost
    }

    private var summaryBreakdownLabelSetup: LabelSetup {
        if bookingDetails == nil {
            return (UIColor.TintD1, PILocalizedString("footerViewBreakdownTotal", comment: ""))
        }
        return (UIColor.BasePurple, PILocalizedString("footerViewBreakdown", comment: ""))
    }

    private var paymentCardImageUrls: [URL]? {
        guard bookingDetails == nil else { return nil }
        guard let cards = hotel.acceptedCreditCards else { return nil }

        return (cards.sorted { $0.listOrder < $1.listOrder }).compactMap { $0.logoURL }
    }
}

// MARK: - Convenience
extension UpsellsInteractor {
    private func priceTextAndRanges(upsell: UpsellItem) -> TextAndRanges {
        let localizedPrice = upsell.price.localizedValue

        if upsell.isExtraUpsell {
            let ranges = localizedPrice.ranges(of: localizedPrice)
            return (localizedPrice, ranges)
        }

        let text = String.localizedStringWithFormat(PILocalizedString("perDay", comment: ""), localizedPrice)
        let ranges = text.ranges(of: localizedPrice)
        return (text, ranges)
    }

    private var freeBreakfastCount: Int {
        // calculating free kids breakfasts separately for each room so we don't go over the room.children
        rooms.indices.reduce(0, { count, roomIndex in
            let room = rooms[roomIndex]
            guard room.children > 0 else { return count }

            let mealPreferencesForRoom = roomMealPreferences.filter { $0.roomNumber == roomIndex }
            let upsellIds = mealPreferencesForRoom.compactMap { $0.upsellCode }
            let upsellsForRoom = upsells.filter({ upsellIds.contains($0.id) })

            guard upsellsForRoom.first(where: { $0.freeBreakfastTrigger == true }) != nil else { return count }

            return count + room.children
        })
    }

    private var paidBreakfasts: [(upsell: UpsellItem, count: Int)] {
        var tempUpsells: [String: Int] = [:]

        for roomMealPreference in roomMealPreferences {
            guard let upsellCode = roomMealPreference.upsellCode else { continue }
            guard rooms.indices.contains(roomMealPreference.roomNumber) else { continue }
            guard let upsell = upsells.first(where: { $0.id == upsellCode }) else { continue }

            let room = rooms[roomMealPreference.roomNumber]

            var runningTotal = tempUpsells[upsellCode] ?? 0

            runningTotal += roomMealPreference.numberOfSelections ?? 0
            runningTotal += (upsell.kidsHaveToPay == true ? room.children : 0)

            tempUpsells[upsellCode] = runningTotal
        }

        return tempUpsells.keys.compactMap { key in
            guard let upsell = upsells.first(where: { $0.id == key }) else { return nil }
            guard let amount = tempUpsells[key], amount != 0 else { return nil }

            return (upsell, amount)
        }
    }

    private var extraItems: [(upsell: UpsellItem, count: Int)] {
        var tempUpsells: [String: Int] = [:]

        for roomMealPreference in roomMealPreferences {
            guard let extraUpsellsCodes = roomMealPreference.extraUpsellsCodes else { continue }

            for upsellCode in extraUpsellsCodes {
                guard rooms.indices.contains(roomMealPreference.roomNumber),
                      let extraUpsells = extraUpsells,
                      let userSelectedUpsell = extraUpsells.first(where: { upsellCode == $0.id }) else { continue }

                var runningTotal = tempUpsells[userSelectedUpsell.id] ?? 0

                runningTotal += 1 // charged once per room

                tempUpsells[userSelectedUpsell.id] = runningTotal
            }
        }

        return tempUpsells.keys.compactMap { key in
            guard let upsell = extraUpsells?.first(where: { $0.id == key }) else { return nil }
            guard let amount = tempUpsells[key] else { return nil }

            return (upsell, amount)
        }
    }
}

extension UpsellsInteractor {
    private var upsellsCloseOutViewModel: UpsellCloseOutBannerViewModel? {
        guard let closeOutState = closeOutState else { return nil }
        switch closeOutState {
        case .noUpsellsAvailable:
            return UpsellCloseOutBannerViewModel(
                state: closeOutState,
                title: PILocalizedString("restaurantClosedTitle"),
                message: PILocalizedString("restaurantClosedMessage")
            )
        case .isForcedUpsellChange:
            return UpsellCloseOutBannerViewModel(
                state: closeOutState,
                title: nil,
                message: PILocalizedString("forcedCloseOutAmendMessage")
            )
        case .isForcedNoUpsellsAvailable:
            return UpsellCloseOutBannerViewModel(
                state: closeOutState,
                title: PILocalizedString("restaurantClosedTitle"),
                message: PILocalizedString("restaurantClosedAmendMessage")
            )
        case .upsellsFilteredAmend, .upsellsFilteredAndSelectionExists:
            return UpsellCloseOutBannerViewModel(
                state: closeOutState,
                title: nil,
                message: PILocalizedString("upsellsCloseOutFilteredText")
            )
        }
    }
}
