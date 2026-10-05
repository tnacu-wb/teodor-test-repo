//
//  UpsellsModule.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

struct AmendRoomsModel {
    let reservation: Reservation
    let existingUpsellItems: [UpsellItem]?
    let rooms: [Room]
    let hotel: Hotel?
    let bookingDetails: BookingDetails?
    let rate: Rate?
    let isForcedUpsellChange: Bool
}

enum CloseOutState {
    case noUpsellsAvailable
    case upsellsFilteredAmend
    case upsellsFilteredAndSelectionExists
    case isForcedUpsellChange
    case isForcedNoUpsellsAvailable
}

enum UpsellsModule {
    // Amend Flow

    /// Build UpsellsModule for Amend flow for multiple rooms
    static func build(
        for roomsModel: AmendRoomsModel,
        delegate: UpsellsRouterDelegate?,
        amendOperaDetails: AmendOperaDetails?
    ) -> UIViewController {
        guard let hotel = roomsModel.hotel else { return UIViewController() }
        guard var upsellOptionsAvailable = roomsModel.reservation.availableFoodUpsells else { return UIViewController() }
        guard let wifiUpsellsOptionsAvailable = roomsModel.reservation.availableWifiUpsells
            else { return UIViewController() }

        // we don't have the rooms class to know if we need to hide WiFi in Amend

        /* ECI/LCO out of scope for Amend
        var extraUpsells: [UpsellItem]?
        if SettingsManager.sharedInstance.featureAllowEciLco {
            extraUpsells = hotel.upsellsAvailable(
                arrivalDate: roomsModel.bookingDetails?.criteria.arrivalDate,
                departureDate: roomsModel.bookingDetails?.criteria.checkOutDate,
                upsells: roomsModel.reservation.availableExtraUpsells ?? []
            )
        } */

        upsellOptionsAvailable = hotel.upsellsAvailable(
            arrivalDate: roomsModel.bookingDetails?.criteria.arrivalDate,
            departureDate: roomsModel.bookingDetails?.criteria.checkOutDate,
            upsells: upsellOptionsAvailable
        )
        // If all the upsells have been filtered from the closeout dates we want to show a specific UI in this instance. This Bool will give us this scenario
        let closeOutState = closeOutState(
            originalUpsells: roomsModel.reservation.availableFoodUpsells,
            filteredUpsells: upsellOptionsAvailable,
            selectedUpsells: roomsModel.existingUpsellItems,
            isAmend: true,
            isForced: roomsModel.isForcedUpsellChange
        )

        // Amend flow
        let scope: UserDetailScope = .amendFlow

        // Pre-Existing Upsell Items
        var roomMealPreferences = [RoomMealPreference]()

        if roomsModel.existingUpsellItems?
           .isEmpty == true || (closeOutState != nil && closeOutState != .upsellsFilteredAndSelectionExists) {
            roomMealPreferences = [RoomMealPreference(
                roomNumber: 0,
                numberOfSelections: 0,
                upsellCode: nil,
                extraUpsellsCodes: nil
            )]
        } else {
            roomsModel.existingUpsellItems?.forEach { item in
                guard let index = roomsModel.rooms.firstIndex(where: { $0.uniqueID == item.roomUniqueID }) else { return }

                // if a meal has already been added, continue - otherwise we need to add the extra
                if let existingPreferenceIndex = roomMealPreferences
                   .firstIndex(where: { $0.upsellCode == item.id && $0.roomNumber == index }) {
                    var existingPreference = roomMealPreferences.remove(at: existingPreferenceIndex)
                    guard item.isExtraUpsell else { return }

                    let extraUpsellsCodes = (existingPreference.extraUpsellsCodes ?? []) + [item.id]
                    existingPreference.extraUpsellsCodes = extraUpsellsCodes

                    roomMealPreferences.append(existingPreference)
                } else {
                    let extraUpsellsCodes = item.isExtraUpsell ? [item.id] : nil
                    roomMealPreferences.append(RoomMealPreference(
                        roomNumber: index,
                        numberOfSelections: item.quantity,
                        upsellCode: item.id,
                        extraUpsellsCodes: extraUpsellsCodes
                    ))
                }
            }
        }

        // Controller
        let controller = UpsellsViewController()
        controller.eventHandler = {
            let router = UpsellsRouter()
            router.viewController = controller
            router.delegate = delegate

            let interactor = UpsellsInteractor(
                with: roomsModel.rooms,
                roomMealPreferences: roomMealPreferences,
                hotel: hotel,
                upsellOptions: upsellOptionsAvailable,
                extraUpsells: nil, // not sending any extras as we do not support it in amend currently
                and: roomsModel.bookingDetails,
                reservation: roomsModel.reservation,
                isAmendFlow: scope == .amendFlow,
                amendOperaDetails: amendOperaDetails,
                upsellCloseOutState: closeOutState
            )

            let presenter = UpsellsPresenter(scope: scope)
            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            return presenter
        }()

        return controller
    }

    // Upsell Flow

    static func build(
        with bookingDetails: BookingDetails,
        scope: UserDetailScope,
        delegate: UpsellsRouterDelegate?
    ) -> UIViewController {
        guard let hotel = bookingDetails.hotel,
              let rooms = bookingDetails.rate?.rooms else { return UIViewController() }

        // food upsells
        let upsells = hotel.upsellsAvailable(
            arrivalDate: bookingDetails.criteria.arrivalDate,
            departureDate: bookingDetails.criteria.checkOutDate,
            upsells: bookingDetails.rate?.foodUpsells ?? []
        )

        // extra upsells
        let extraUpsells: [UpsellItem]? = {
            let extras = {
                let wifiUpsells = bookingDetails.isUltimateWifiIncluded ? bookingDetails.rate?.wifiUpsells : []

                if SettingsManager.sharedInstance.featureAllowEciLco {
                    let availableExtraUpsells = bookingDetails.rate?.extraUpsells?
                        .filter { $0.availableCount ?? 0 >= rooms.count }
                    return (availableExtraUpsells ?? []) + (wifiUpsells ?? [])
                } else {
                    return (bookingDetails.rate?.extraUpsellsExcludingECiLco ?? []) + (wifiUpsells ?? [])
                }
            }()

            return hotel.upsellsAvailable(
                arrivalDate: bookingDetails.criteria.arrivalDate,
                departureDate: bookingDetails.criteria.checkOutDate,
                upsells: extras
            )
        }()

        var roomMealPreferences: [RoomMealPreference]

        if let roomMealCombos = bookingDetails.roomMealCombos, roomMealCombos.isNotEmpty {
            roomMealPreferences = roomMealCombos.compactMap {
                RoomMealPreference(
                    roomNumber: $0.roomNumber,
                    numberOfSelections: $0.quantity,
                    upsellCode: $0.meal.id,
                    extraUpsellsCodes: nil
                )
            }
        } else {
            roomMealPreferences = {
				guard let mealPreference = UserSessionManager.sharedInstance.currentUser?.bookingPreference?.foodPreference
				    else { return [] }
                var roomMealPreference = [RoomMealPreference]()

                for (index, room) in rooms.enumerated() {
                    roomMealPreference.append(RoomMealPreference((index, room.adults, mealPreference.id, nil)))
                }
                return roomMealPreference
            }()
        }

        if let roomExtraPackages = bookingDetails.roomExtraPackages {
            let groupedByRoom = Dictionary(grouping: roomExtraPackages) { $0.roomNumber }

            for (roomNumber, combos) in groupedByRoom {
                let extraUpsellsCodes = combos.compactMap { $0.meal.id }
                roomMealPreferences.append(RoomMealPreference(
                    roomNumber: roomNumber,
                    numberOfSelections: nil,
                    upsellCode: nil,
                    extraUpsellsCodes: extraUpsellsCodes
                ))
            }
        }

        // If all the upsells have been filtered from the closeout dates we want to show a specific UI in this instance. This Bool will give us this scenario
        let noUpsellsAvailableForDates = closeOutState(
            originalUpsells: bookingDetails.rate?.foodUpsells,
            filteredUpsells: upsells,
            isAmend: false
        )

        let controller = UpsellsViewController()
        controller.eventHandler = {
            let router = UpsellsRouter()
            router.viewController = controller
            router.delegate = delegate

            let interactor = UpsellsInteractor(
                with: rooms,
                roomMealPreferences: roomMealPreferences,
                hotel: hotel,
                upsellOptions: upsells,
                extraUpsells: extraUpsells,
                and: bookingDetails,
                upsellCloseOutState: noUpsellsAvailableForDates
            )

            let presenter = UpsellsPresenter(scope: scope)
            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router
            presenter.reviewBookDelegate = delegate

            return presenter
        }()

        return controller
    }

    static func build(with bookingDetails: BookingDetails) -> UIViewController {
        UpsellsModule.build(with: bookingDetails, scope: .bookingFlow, delegate: nil)
    }

    static func build(with checkInOnlineModel: CheckInOnlineModel) -> UIViewController {
        // this is the real way
        // guard let upsells = (checkInOnlineModel.reservation.availableUpsells?.filter { $0.foodUpsell }) else { return UIViewController() }

        // this is the workaround until MS reintroduce to foodUpsell flag to reservation upsells
        let upsells = UpsellsModule.filthyFoodUpsellsTemporaryFunctionality(in: checkInOnlineModel)
        guard upsells.isNotEmpty else { return UIViewController() }

        let controller = UpsellsViewController()
        controller.eventHandler = {
            let router = UpsellsRouter()
            router.viewController = controller
            router.delegate = nil

            let interactor = UpsellsInteractor(
                with: checkInOnlineModel.reservation.rooms,
                roomMealPreferences: [],
                hotel: checkInOnlineModel.hotel,
                upsellOptions: upsells,
                extraUpsells: nil, // TODO: Need to check when looking at Checkin online journey
                sessionId: checkInOnlineModel.sessionId,
                reservation: checkInOnlineModel.reservation
            )

            let presenter = UpsellsPresenter(scope: .bookingFlow)
            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            return presenter
        }()

        return controller
    }

    private static func filthyFoodUpsellsTemporaryFunctionality(in checkInOnlineModel: CheckInOnlineModel) -> [UpsellItem] {
        checkInOnlineModel.reservation.availableUpsells?.filter { [11, 12, 17, 18].contains($0.code) } ?? []
    }

    private static func closeOutState(
        originalUpsells: [UpsellItem]?,
        filteredUpsells: [UpsellItem]?,
        selectedUpsells: [UpsellItem]? = nil,
        isAmend: Bool,
        isForced: Bool = false
    ) -> CloseOutState? {
        if isForced {
            guard filteredUpsells?.isNotEmpty == true else {
                return .isForcedNoUpsellsAvailable
            }
            return .isForcedUpsellChange
        }

        if originalUpsells?.isNotEmpty == true &&
           filteredUpsells?.isEmpty == true {
            return .noUpsellsAvailable
        } else if originalUpsells?.count != filteredUpsells?.count {
            guard isAmend == true else { return nil }
            let allUpsellsExistAfterFilter = selectedUpsells?.allSatisfy({ upsell in
                filteredUpsells?.contains(where: { $0.code == upsell.code }) == true
            })

            return allUpsellsExistAfterFilter == true ? .upsellsFilteredAndSelectionExists : .upsellsFilteredAmend
        }
        return nil
    }
}

typealias Allowance = (allowanceDescription: NSAttributedString, accessibilityIdentifier: String)

protocol UpsellsAllowancesViewModel {
    var title: String { get }
    var message: String { get }
    var allowances: [Allowance] { get }
}

protocol UpsellsBookingSummaryViewModel {
    var hotelImageUrl: URL? { get }
    var hotelName: String? { get }
    var stayDetails: String { get }
    var totalCost: String { get }
}

typealias MenuInfo = (name: String, url: URL)
typealias AllergenInformationViewModel = (name: String, url: URL)

protocol UpsellsRestaurantViewModel {
    var restaurantSectionTitle: String { get }
    var restaurantMainImageUrl: URL? { get }
    var restaurantAllowanceMessage: String? { get }
    var restaurantImageUrls: [URL]? { get }
    var menuViewModels: [MenuInfo]? { get }
    var allergenInformationViewModels: [AllergenInformationViewModel]? { get }
}

protocol UpsellsAmendViewModel {
    var isAmendFlow: Bool { get }
    var amendRoomNumber: Int? { get }
    var amendRoom: Room? { get }
    var amendRate: Rate? { get }
}

protocol UpsellsRoomViewModel {
    var name: NSAttributedString { get }
    var description: String { get }
    var collapsed: Bool { get }
    var upsellsSummary: String? { get }
    var upsellItems: [UpsellItemViewModel]? { get }
}

protocol UpsellItemViewModel {
    var title: String { get }
    var costSummary: NSAttributedString? { get }
    var description: NSAttributedString { get }
    var note: NSAttributedString? { get }
    var infoMessage: String? { get }
    var selected: Bool { get }
    var quantity: Int? { get }
    var maxSelected: Int? { get }
    var isHidden: Bool { get }
}

typealias LabelSetup = (colour: UIColor, text: String)

protocol UpsellsTotalViewModel {
    var showDoneButton: Bool { get }
    var showBreakdownTotalCost: Bool { get }
    var breakdownRows: [(title: String, value: String)] { get }
    var totalCost: Cost? { get }
    var breakdownTotalCostLabel: String { get }
    var breakdownTotalCostValue: String { get }
    var rateText: String { get }
    var paymentCardImageUrls: [URL]? { get }
    var summaryBreakdownLabelSetup: LabelSetup { get }
}

protocol UpsellsViewModel {
    var screenName: String { get }
    var summaryViewModel: UpsellsBookingSummaryViewModel? { get }
    var upsellsAllowancesViewModel: UpsellsAllowancesViewModel? { get }
    var restaurantViewModel: UpsellsRestaurantViewModel { get }
    var roomViewModels: [UpsellsRoomViewModel] { get }
    var extraUpsellsModels: [UpsellsRoomViewModel] { get }
    var totalViewModel: UpsellsTotalViewModel? { get }
    var upsellCloseOutViewModel: UpsellCloseOutBannerViewModel? { get }
    var continueButtonTitle: String { get }
    var hasFoodUpsells: Bool { get }
}

struct UpsellCloseOutBannerViewModel {
    var state: CloseOutState?
    var title: String?
    var message: String?
}

protocol UpsellsViewProtocol: AnyObject {
    func showCancelButton()
    func update(with viewModel: UpsellsViewModel)
    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?)
    func showSpinnerAndLockScreen()
    func hideSpinnerAndUnLockScreen()
    func lockFullScreen(shouldLock: Bool)
}

protocol UpsellsViewEventHandler {
    var promotionsAnalytics: PIDictionary? { get }
    var analyticsScope: String { get }
    var analyticsCustomParams: [String: Any]? { get }

    func viewIsReady()
    func toggledExtraUpsell(toggle: Bool, upsellIndex: Int, in roomIndex: Int)
    func numberOfAdultsDidChange(value: Int, at roomIndex: Int, mealCode: Int)
    func selected(change roomIndex: Int)
    func selectedExtraUpsell(change roomIndex: Int)
    func summaryButtonDidTap()
    func continueButtonDidTap()
    func cancelButtonDidTap()
}

typealias AmendAnalyticsValues = (bookingId: String, upsellsLegends: String, upsellsCodes: String)

protocol UpsellsInteractorProtocol {
    var viewModel: UpsellsViewModel? { get }
    var bookingDetails: BookingDetails? { get }
    var roomMealCombos: [RoomMealCombo]? { get }
    var amendOperaDetails: AmendOperaDetails? { get }
    // Amend flow properties
    var amendedRooms: [Room] { get }

    // Amend analytics
    var amendAnalytics: AmendAnalyticsValues? { get }

    var promotionsAnalytics: PIDictionary? { get }

    func selected(upsellAt upsellIndex: Int, in roomIndex: Int, numberOfSelection: Int)
    func toggledExtraUpsell(toggle: Bool, upsellIndex: Int, in roomIndex: Int)
    func selected(change roomIndex: Int)
    func selectedExtraUpsell(change roomIndex: Int)
    func resetUsersMealSelection()
    func saveAncillaries(completion: @escaping (Bool, Error?) -> Void)
    func amendPackages(completion: @escaping (_ isSuccess: Bool?, _ error: Error?) -> Void)
}

protocol UpsellsRouterProtocol {
    func goBackToUpsellsScreen()
    func showSummary(with bookingDetails: BookingDetails)
    func continueToNextStep(with bookingDetails: BookingDetails)
    func continueToNextAmendStep()
}

protocol UpsellsRouterDelegate: AnyObject {
    func goBackToUpsellsScreen()
    func showErrorMessage(title: String, message: String, handler: @escaping (UIAlertAction) -> Void)
    func upsellsDidFinish(sender: UIViewController)
    func upsellsDidFinishAmend(sender: UIViewController)
}

extension UpsellsRouterDelegate {
    func goBackToUpsellsScreen() { }

    func showErrorMessage(title: String, message: String, handler: @escaping (UIAlertAction) -> Void) { }
}
