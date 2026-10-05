//
//  CiolUpsellModule.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

enum CiolUpsellModule {
    static func build(inputParams: CiolUpsellInputParams, prestayDelegate: PrestayDelegate) -> UIViewController {
        let viewController = CiolUpsellViewController()
        let presenter = CiolUpsellPresenter()
        let interactor = CiolUpsellInteractor(inputParams: inputParams)
        interactor.prestayDelegate = prestayDelegate
        let router = CiolUpsellRouter()

        viewController.eventHandler = presenter

        presenter.view = viewController
        presenter.interactor = interactor
        presenter.router = router

        interactor.output = presenter

        router.viewController = viewController

        return viewController
    }
}

protocol CiolUpsellItemViewModelProtocol {
    var legendTitle: String { get }
    var imageURL: URL? { get }
    var title: String { get }
    var subtitle: String { get }
    var costSummary: String { get }
    var itemDescription: String? { get }
    var isFoodUpsell: Bool { get }
    var isBooked: Bool { get set }
    var menuUrls: [RestaurantMenuItem] { get }
    var allergensUrls: [AllergenInformation] { get }
    var hasChildren: Bool { get }
    var isMultiRoom: Bool { get }
    var subitems: [CiolUpsellSubitemViewModel] { get set }
    var room: UpsellRoom? { get set }
    var id: String { get }
    var enabled: Bool { get set }
    var addToAllRooms: Bool { get }
    var removeAllItems: Bool { get }
    var selected: Bool { get set }
    var isPrebooked: Bool { get set }
    var isWifi: Bool { get set }
    var nights: Int { get set }
}

extension CiolUpsellItemViewModelProtocol {
    var isBreakfast: Bool {
        [
            UpsellItemOperaId.continentalBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue
        ].contains(id)
    }

    var addToAllRooms: Bool {
        [UpsellItemOperaId.earlyCheckIn.rawValue, UpsellItemOperaId.lateCheckOut.rawValue].contains(id)
    }

    var removeAllItems: Bool {
        isFoodUpsell
    }
}

protocol UpsellPriceUpdateable {
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol? { get set }
    var displayedPriceBreakdownModel: CIOLPriceBreakdownViewModelProtocol? { get }
    var addedPriceBreakdownViewModels: [CIOLPriceBreakdownItemViewModelProtocol] { get set }
    mutating func updateAddedBreakdown(addedItems: [CIOLPriceBreakdownItemViewModelProtocol])
    mutating func updateBalance(newBalance: CIOLPriceBreakdownViewModelProtocol)
}

extension UpsellPriceUpdateable {
    var displayedPriceBreakdownModel: CIOLPriceBreakdownViewModelProtocol? {
        guard let priceBreakdownViewModel else {
            return nil
        }
        guard addedPriceBreakdownViewModels.isNotEmpty else {
            return priceBreakdownViewModel
        }
        var updatedModel = priceBreakdownViewModel
        updatedModel.items.append(contentsOf: addedPriceBreakdownViewModels)
        let totalValue = updatedModel.items.reduce(NSDecimalNumber(value: 0.0)) { $0.adding($1.value.amount) }
        let cost = Cost(
            amount: Double(truncating: totalValue),
            currencyCode: updatedModel.items.first?.value.currencyCode ?? ""
        )
        updatedModel.totalValue = cost.localizedValue
        return updatedModel
    }

    mutating func updateAddedBreakdown(addedItems: [CIOLPriceBreakdownItemViewModelProtocol]) {
        addedPriceBreakdownViewModels = addedItems
    }

    mutating func updateBalance(newBalance: CIOLPriceBreakdownViewModelProtocol) {
        self.priceBreakdownViewModel = newBalance
    }
}

protocol CiolUpsellViewModelProtocol: UpsellPriceUpdateable {
    var availableUpsells: [CiolUpsellItemViewModelProtocol]? { get set }
    var prebookedUpsells: [CiolUpsellItemViewModelProtocol]? { get set }
    var rooms: [UpsellRoom] { get }
    var hasChildren: Bool { get }
    var isMultiRoom: Bool { get }
    var nights: Int? { get }
    var upsellsAddOnEnabled: Bool { get }
    var selectedUpsells: [CiolUpsellItemViewModelProtocol] { get }
    var unselectedUpsells: [CiolUpsellItemViewModelProtocol] { get }
    var shouldShowCloseoutMessage: Bool { get }

    mutating func updateEnablement(isMealDealDisabled: Bool, isBreakfastDisabled: Bool)
    mutating func updateSelected(statuses: [String: Bool])
    mutating func resetUpsells(available: [CiolUpsellItemViewModelProtocol], booked: [CiolUpsellItemViewModelProtocol])
    mutating func updateEnablement(id: String, enabled: Bool)
}

extension CiolUpsellViewModelProtocol {
    var selectedUpsells: [CiolUpsellItemViewModelProtocol] {
        let prebooked = prebookedUpsells ?? []
        let prebookedIds = Set(prebooked.map { $0.id })
        let selectedAvailable = availableUpsells?.filter { $0.selected && !prebookedIds.contains($0.id) } ?? []

        // Combine prebooked items (e.g. PIBA CNP, or pre-existing add-ons like Early check-in)
        // with any newly selected available upsells, so both show up in the Selected section.
        return prebooked + selectedAvailable
    }
    var unselectedUpsells: [CiolUpsellItemViewModelProtocol] {
        availableUpsells?.filter { !$0.selected } ?? []
    }

    mutating func updateEnablement(id: String, enabled: Bool) {
        guard var availableUpsells,
              let valueIndex = availableUpsells.firstIndex(where: { $0.id == id }),
              let value = availableUpsells[safe: valueIndex] else { return }
            var updatedValue = value
            updatedValue.enabled = enabled
            availableUpsells[valueIndex] = updatedValue

        self.availableUpsells = availableUpsells
    }
}

enum RegCardFlow {
    case general
    case regCard
}

struct CiolUpsellInputParams {
    var hotel: Hotel?
    let availableUpsells: [UpsellItem]?
    let bookedUpsells: [UpsellItem]?
    var rooms: [UpsellRoom]
    var hasChildren: Bool
    var isMultiRoom: Bool
    var nights: Int?
    let priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol
    var bookingSummaryViewModel: BookingSummaryCIOLViewModelProtocol
    var flow: CIOLStartFlow
    var leadBookerFirstName: String?
    var hotelBrand: HotelBrand?
    var address: Address?
    var bookingReference: String?
    var stay: Stay
    let reservationID: String?
    let hotelID: String
    let arrivalDate: Date?
    let departureDate: Date?
    var outstandingBalance: Cost?
    var selectedPreference: HotelPreferenceViewModel?
    var hotelImage: URL?
    var analyticsParams: PIDictionary
    var bookingFlowId: String?
    var regCardFlow: RegCardFlow = .general
    var hotelName: String?
    var hotelAddress: String?
    var guests: [Guest] = []
}

protocol CiolUpsellViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }
    func reloadData(viewModel: CiolUpsellViewModelProtocol)
    func showLoadingIndicator()
    func hideLoadingIndicator()
    func showError(title: String, message: String?, shouldDie: Bool)
}

protocol CiolUpsellInteractorProtocol: CiolUpsellDetailsOutputDelegate, CiolUpsellOutputInteractable,
    RoomsUpsellOutputDelegate, CiolUpsellPayDelegate, AuthorizationDelegate, CanCompleteRegCard {
    var ciolUpsellViewModel: CiolUpsellViewModelProtocol { get set }
    var paymentInputParams: CiolReviewAndPayInputParams? { get }
    var customAnalyticsParameters: PIDictionary? { get }
    var bookingReference: String? { get }
    func goToNextStep()
    func getCellSetup(id: String, isSelected: Bool) -> CiolUpsellCellSetup
    func trackPriceBreakdownTapAnalytics()
    var paymentViewLayout: WebViewControllerLayout { get }
    func confirmPreCheckIn(basketReference: String, completion: @escaping (Error?) -> Void)
}

protocol CiolUpsellOutputInteractable {
    var bookingReference: String? { get }
    func addedFoodItems(for roomID: String, withoutUpsellID: String) -> Int?
    func addedKidsItems(for roomID: String, withoutUpsellID: String) -> Int?
    func upsell(from roomID: String, upsellID: String) -> CiolUpsellItemViewModelProtocol?
    func prebookedItems(for upsellID: String, roomID: String) -> [String]?
    var upsellOutput: RoomsUpsellOutput { get set }
}

extension CiolUpsellOutputInteractable {
    func addedFoodItems(for roomID: String, withoutUpsellID: String) -> Int? {
        upsellOutput.addedFoodItems(for: roomID, withoutUpsellID: withoutUpsellID)
    }

    func addedKidsItems(for roomID: String, withoutUpsellID: String) -> Int? {
        upsellOutput.addedKidsItems(for: roomID, withoutUpsellID: withoutUpsellID)
    }

    func upsell(from roomID: String, upsellID: String) -> CiolUpsellItemViewModelProtocol? {
        upsellOutput.upsell(from: roomID, upsellID: upsellID)
    }

    func prebookedItems(for upsellID: String, roomID: String) -> [String]? {
        upsellOutput.prebookedItems(for: upsellID, roomID: roomID)
    }
}

protocol CiolUpsellRouterProtocol {
    func showPayment()
    func showUpsellRooms(_ roomsUpsellInputParams: RoomsUpsellInputParams, roomsOutputDelegate: RoomsUpsellOutputDelegate)
    func showUpsellDetails(input: CiolUpsellDetailsInputParams, outputDelegate: CiolUpsellDetailsOutputDelegate)
    func goToPayment(inputParams: CiolReviewAndPayInputParams, failedPaymentDelegate: CiolUpsellPayDelegate)
    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails)
    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        and webDelegate: WebViewControllerDelegate,
        authorizationDelegate: AuthorizationDelegate,
        webviewLayout: WebViewControllerLayout
    )
}

protocol CiolUpsellViewEventHandler {
    func handleContinueButtonTap()
    func trackPriceBreakdownTapAnalytics()
    func handleUpsellsRowTapped(_ upsell: CiolUpsellItemViewModelProtocol)
    func viewIsReady()
    func reloadViewModel()
    func getCellSetup(id: String, isSelected: Bool) -> CiolUpsellCellSetup?
}

protocol UpsellRoom {
    var adults: [String] { get set }
    var hasChildren: Bool { get set }
    var numberOfChildren: Int { get set }
    var id: String { get set }
    var title: String { get set }
    var addedUpsells: [CiolUpsellItemViewModelProtocol] { get set }
    var addedDescriptions: [String] { get }
}

extension UpsellRoom {
    var addedDescriptions: [String] {
        addedUpsells
            .filter { $0.isBooked || $0.isPrebooked }
            .flatMap { $0.subitems }
            .filter { $0.quantity > 0 }
            .map { subitem in
                var itemTitle: String = subitem.title
                if [
                    UpsellItemOperaId.continentalBreakfast.rawValue,
                    UpsellItemOperaId.premierInnBreakfast.rawValue,
                    UpsellItemOperaId.premierInnBreakfastDE.rawValue
                ].contains(subitem.id) {
                    var updatedTitleSplit = itemTitle.split(separator: " ").map { String($0) }
                    var breakfast = updatedTitleSplit.removeLast().lowercased()
                    if subitem.quantity > 1 {
                        breakfast += "s"
                    }
                    updatedTitleSplit.append(breakfast)
                    itemTitle = updatedTitleSplit.joined(separator: " ")
                }
                var description = itemTitle
                if subitem.quantity > 1 {
                    let quantity = subitem.quantity > 1 ? "\(subitem.quantity)x" : ""
                    description = "\(quantity) \(itemTitle)"
                }
                return description
            }
    }

    func upsellPrebookState(for id: String) -> UpsellItemState.Prebooked {
        let prebooked = addedUpsells.filter { ($0.isPrebooked) && $0.id == id }.flatMap { $0.subitems }
            .filter { $0.quantity > 0 && $0.id != UpsellItemOperaId.freeChildBreakfast.rawValue }.reduce(
                0,
                { $0 + $1.quantity }
            )
        var prebookState: UpsellItemState.Prebooked = .none
        if prebooked == 0 {
            prebookState = .none
        } else if prebooked < adults.count {
            prebookState = .some
        } else if prebooked == adults.count {
            prebookState = .full
        }
        return prebookState
    }

    func foodPrebookState() -> UpsellItemState.Prebooked {
        let prebooked = addedUpsells.filter { $0.isPrebooked && $0.isFoodUpsell }.flatMap { $0.subitems }
            .filter { $0.quantity > 0 && $0.id != UpsellItemOperaId.freeChildBreakfast.rawValue }.reduce(
                0,
                { $0 + $1.quantity }
            )
        var prebookState: UpsellItemState.Prebooked = .none
        if prebooked == 0 {
            prebookState = .none
        } else if prebooked < adults.count {
            prebookState = .some
        } else if prebooked == adults.count {
            prebookState = .full
        }
        return prebookState
    }

    func uniquePrebookState(upsellID: String) -> UpsellItemState.Prebooked {
        let prebooked = addedUpsells.first(where: { $0.isPrebooked && $0.id == upsellID })
        return prebooked == nil ? .none : .full
    }

    func upsellState(for id: String) -> UpsellItemState {
        let addedItems = addedUpsells
            .filter { ($0.isBooked || $0.isPrebooked) && $0.isFoodUpsell && $0.id == id }
        let prebookState = upsellPrebookState(for: id)
        let foodLimitReached = addedUpsells
            .filter { ($0.isBooked || $0.isPrebooked) && $0.isFoodUpsell }.flatMap { $0.subitems }.reduce(
                0,
                { $0 + $1.quantity }
            ) == adults.count
        let descriptions = addedItems.flatMap({ $0.subitems })
            .filter { $0.quantity > 0 }
            .compactMap { subitem in
                var itemTitle: String = subitem.title
                if [
                    UpsellItemOperaId.continentalBreakfast.rawValue,
                    UpsellItemOperaId.premierInnBreakfast.rawValue,
                    UpsellItemOperaId.premierInnBreakfastDE.rawValue
                ].contains(subitem.id) {
                    var updatedTitleSplit = itemTitle.split(separator: " ").map { String($0) }
                    var breakfast = updatedTitleSplit.removeLast().lowercased()
                    if subitem.quantity > 1 {
                        breakfast += "s"
                    }
                    updatedTitleSplit.append(breakfast)
                    itemTitle = updatedTitleSplit.joined(separator: " ")
                }

                let quantity = subitem.quantity > 1 ? "\(subitem.quantity)x" : "1x"
                itemTitle = "\(quantity) \(itemTitle)"

                return itemTitle
            }
        return .init(roomFoodLimitReached: foodLimitReached, prebookState: prebookState, items: descriptions)
    }
}

struct CiolUpsellRoom: UpsellRoom {
    var hasChildren: Bool
    var numberOfChildren: Int
    var id: String
    var adults: [String]
    var title: String
    var addedUpsells: [CiolUpsellItemViewModelProtocol] = []
}

struct UpsellItemState {
    enum Prebooked {
        case none
        case some
        case full
    }

    let roomFoodLimitReached: Bool
    let prebookState: Prebooked
    let items: [String]
}

struct RoomsUpsellOutput {
    var rooms: [UpsellRoom] = []
    let nights: Int

    init(rooms: [UpsellRoom], nights: Int) {
        self.rooms = rooms
        self.nights = nights
    }

    enum Constants {
        static let needToAmend = PILocalizedString("ciolNeedAmend")
        static let notAllGuests = PILocalizedString("ciolNotAllGuests")
    }

    let foodUpsells = [
        UpsellItemOperaId.premierInnBreakfast.rawValue,
        UpsellItemOperaId.premierInnBreakfastDE.rawValue,
        UpsellItemOperaId.continentalBreakfast.rawValue,
        UpsellItemOperaId.mealDeal.rawValue
    ]
    let uniqueUpsells = [
        UpsellItemOperaId.ultimateWifi7Days.rawValue,
        UpsellItemOperaId.ultimateWifi24Hours.rawValue,
        UpsellItemOperaId.lateCheckOut.rawValue,
        UpsellItemOperaId.earlyCheckIn.rawValue
    ]

    // swiftlint:disable:next cyclomatic_complexity
    func getCellSetup(id: String, isSelected: Bool) -> CiolUpsellCellSetup {
        let isMultiRoom = rooms.count > 1

        if isSelected {
            // seems like the multi room logic could get the same results as the one room logic but too risky to change
            if rooms.count == 1 {
                if foodUpsells.contains(id) {
                    guard let upsellState = rooms.first?.upsellState(for: id) else { return .init(isMultiRoom: isMultiRoom)}
                    switch upsellState.prebookState {
                    case .none:
                        return .init(isMultiRoom: isMultiRoom, items: upsellState.items, enabled: true)
                    case .some:
                        let text = upsellState.roomFoodLimitReached ? Constants.needToAmend : Constants.notAllGuests
                        return .init(
                            hasBackground: .warning,
                            text: text,
                            isMultiRoom: isMultiRoom,
                            items: upsellState.items,
                            enabled: true
                        )
                    case .full:
                        return .init(
                            hasBackground: .warning,
                            text: Constants.needToAmend,
                            isMultiRoom: isMultiRoom,
                            items: upsellState.items,
                            enabled: false
                        )
                    }
                } else if uniqueUpsells.contains(id) {
                    guard let roomID = rooms.first?.id else {
                        return .init(isMultiRoom: isMultiRoom, enabled: false)
                    }
                    let uniquePrebookState = uniquePrebokState(roomID: roomID, upsellID: id)
                    var text: String?
                    var enabled = true
                    var messageType = CiolUpsellCellSetup.MessageType.info
                    if uniquePrebookState == .full {
                        text = Constants.needToAmend
                        messageType = .warning
                        enabled = false
                    }
                    return .init(hasBackground: messageType, text: text, isMultiRoom: isMultiRoom, enabled: enabled)
                } else {
                    return .init(isMultiRoom: isMultiRoom, enabled: false)
                }
            } else {
                var items = [String]()
                let prebookState = hasPrebooked(id: id)
                let message: String?
                var enabled = true
                switch prebookState {
                case .none:
                    message = nil
                case .some:
                    message = Constants.notAllGuests
                case .full:
                    enabled = false
                    message = Constants.needToAmend
                }

                switch hasUpsellInMultipleRooms(id: id) {
                case .none:
                    items = []
                case .oneRoom:
                    items = [PILocalizedString("ciolAddedOneRoom")]
                case .multipleRooms:
                    items = [PILocalizedString("ciolAddedMultipleRooms")]
                }
                return .init(
                    hasBackground: .warning,
                    text: message,
                    isMultiRoom: isMultiRoom,
                    items: items,
                    enabled: enabled
                )
            }
        } else {
            var text: String?
            if UpsellItemOperaId.mealDeal.rawValue == id {
                text = isMealDealDisabled ? PILocalizedString("ciolRoomWithBreakfast") : nil
            } else if isBreakFast(id: id) {
                text = isBreakfastDisabled ? PILocalizedString("ciolRoomWithMeal") : nil
            }
            return .init(hasBackground: .info, text: text, isMultiRoom: isMultiRoom, enabled: true)
        }
    }

    func getRoomCellConfig(roomID: String, editedUpsellID: String) -> CiolUpsellCellSetup {
        let prebookedState = roomFoodPrebookState(roomID: roomID)

        if editedUpsellID == UpsellItemOperaId.mealDeal.rawValue {
            let isMealDealDisabled = isMealDealDisabled(by: roomID)
            var text: String? = isMealDealDisabled ? PILocalizedString("ciolRoomWithBreakfast") : nil
            let enabled = prebookedState == .full ? false : !isMealDealDisabled
            var messageType = CiolUpsellCellSetup.MessageType.info
            if prebookedState == .full {
                text = Constants.needToAmend
                messageType = .warning
            }
            return .init(hasBackground: messageType, text: text, isMultiRoom: true, enabled: enabled)
        } else if isBreakFast(id: editedUpsellID) {
            let isBreakFastDisabled = isBreakfastDisabled(by: roomID)
            var text: String? = isBreakFastDisabled ? PILocalizedString("ciolRoomWithMeal") : nil
            let enabled = prebookedState == .full ? false : !isBreakFastDisabled
            var messageType = CiolUpsellCellSetup.MessageType.info
            if prebookedState == .full {
                text = Constants.needToAmend
                messageType = .warning
            }
            return .init(hasBackground: messageType, text: text, isMultiRoom: true, enabled: enabled)
        } else {
            let uniquePrebookState = uniquePrebokState(roomID: roomID, upsellID: editedUpsellID)
            var text: String?
            var enabled = true
            var messageType = CiolUpsellCellSetup.MessageType.info
            if uniquePrebookState == .full {
                text = Constants.needToAmend
                messageType = .warning
                enabled = false
            }
            return .init(hasBackground: messageType, text: text, isMultiRoom: true, enabled: enabled)
        }
    }

    mutating func fillPrebooked(prebookedUpsells: [CiolUpsellItemViewModelProtocol] = []) {
        prebookedUpsells.forEach { prebooked in
            addSingleRoom(prebooked)
        }
    }
    mutating func addUpsell(_ upsell: CiolUpsellItemViewModelProtocol) {
        if upsell.addToAllRooms {
            addAllRooms(upsell)
        } else {
            addSingleRoom(upsell)
        }
    }

    private mutating func addSingleRoom(_ upsell: CiolUpsellItemViewModelProtocol) {
        guard let updatedIndex = rooms.firstIndex(where: { $0.id == upsell.room?.id}) else {
            return
        }
        var updatedRoom = rooms[updatedIndex]
        if let existingUpsellIndex = updatedRoom.addedUpsells
           .firstIndex(where: {$0.id == upsell.id && $0.isPrebooked == false }) {
            var addedUpsells = updatedRoom.addedUpsells
            addedUpsells[existingUpsellIndex] = upsell
            updatedRoom.addedUpsells = addedUpsells
        } else {
            updatedRoom.addedUpsells.append(upsell)
        }
        rooms[updatedIndex] = updatedRoom
    }

    private mutating func addAllRooms(_ upsell: CiolUpsellItemViewModelProtocol) {
        rooms.indices.forEach { index in
            var updatedRoom = rooms[index]
            if let existingUpsellIndex = updatedRoom.addedUpsells
               .firstIndex(where: { $0.id == upsell.id && $0.isPrebooked == false }) {
                var addedUpsells = updatedRoom.addedUpsells
                addedUpsells[existingUpsellIndex] = upsell
                updatedRoom.addedUpsells = addedUpsells
            } else {
                updatedRoom.addedUpsells.append(upsell)
            }
            rooms[index] = updatedRoom
        }
    }

    mutating func removeUpsell(_ upsell: CiolUpsellItemViewModelProtocol) {
        if upsell.addToAllRooms {
            removeAllRooms(upsell)
        } else {
            removeSingleRoom(upsell)
        }
    }

    mutating func removeAllRooms(_ upsell: CiolUpsellItemViewModelProtocol) {
        rooms.indices.forEach { index in
            var updatedRoom = rooms[index]
            var updatedUpsells = updatedRoom.addedUpsells
            updatedUpsells.removeAll(where: { $0.id == upsell.id && $0.isPrebooked == false })
            updatedRoom.addedUpsells = updatedUpsells
            rooms[index] = updatedRoom
        }
    }

    mutating func removeSingleRoom(_ upsell: CiolUpsellItemViewModelProtocol) {
        guard let updatedIndex = rooms.firstIndex(where: { $0.id == upsell.room?.id}) else {
            return
        }
        var updatedRoom = rooms[updatedIndex]
        var updatedUpsells = updatedRoom.addedUpsells
        updatedUpsells.removeAll(where: { $0.id == upsell.id && $0.isPrebooked == false })
        updatedRoom.addedUpsells = updatedUpsells
        rooms[updatedIndex] = updatedRoom
    }

    var totalAdults: Int {
        rooms
            .map { $0.adults.count }
            .reduce(0) { $0 + $1 }
    }

    var addedFoodUpsellsCount: Int {
        rooms
            .flatMap { $0.addedUpsells }
            .filter { $0.isFoodUpsell }
            .flatMap { $0.subitems }
            .reduce(0) {$0 + $1.quantity}
    }

    var foodUpsellsLeft: Int {
        totalAdults - addedFoodUpsellsCount
    }

    func addedFoodItems(for roomID: String, withoutUpsellID: String) -> Int? {
        guard let room = rooms
              .first(where: { $0.id == roomID }) else { return nil }
        let prebookedUpsells = room.addedUpsells
            .filter { $0.isPrebooked && $0.isFoodUpsell }
            .flatMap { $0.subitems }
            .reduce(0) { $0 + $1.quantity }
        let otherAddedUpsells = room.addedUpsells
            .filter { $0.isBooked && $0.id != withoutUpsellID && $0.isFoodUpsell == true }
            .flatMap { $0.subitems }
            .reduce(0) { $0 + $1.quantity }
        return prebookedUpsells + otherAddedUpsells
    }

    func addedKidsItems(for roomID: String, withoutUpsellID: String) -> Int? {
        guard let room = rooms
              .first(where: { $0.id == roomID }) else { return nil }
        let prebookedUpsells = room.addedUpsells
            .filter { $0.isPrebooked && $0.id == UpsellItemOperaId.freeChildBreakfast.rawValue }
            .flatMap { $0.subitems }
            .reduce(0) { $0 + $1.quantity }
        let otherAddedUpsells = room.addedUpsells
            .filter { $0.isBooked && $0.id != withoutUpsellID }
            .flatMap { $0.subitems }
            .filter { $0.id == UpsellItemOperaId.freeChildBreakfast.rawValue }
            .reduce(0) { $0 + $1.quantity }
        return prebookedUpsells + otherAddedUpsells
    }


    func upsell(from roomID: String, upsellID: String) -> CiolUpsellItemViewModelProtocol? {
        guard let room = rooms
              .first(where: { $0.id == roomID }) else { return nil }
        return room.addedUpsells.first(where: { $0.id == upsellID && !$0.isPrebooked})
    }

    func prebookedItems(for upsellID: String, roomID: String) -> [String]? {
        guard let room = rooms.first(where: {$0.id == roomID }) else { return nil }
        return room.addedUpsells
            .filter { $0.isPrebooked && $0.id == upsellID }
            .flatMap { $0.subitems }
            .filter { $0.quantity > 0 }
            .compactMap { "\($0.quantity)x \($0.title)" }
    }

    func isBreakfastDisabled(by roomID: String) -> Bool {
        let maxAdults = rooms
            .first(where: { $0.id == roomID })?
            .adults.count

        let addedMealDeals = rooms
            .first(where: { $0.id == roomID })?
            .addedUpsells
            .flatMap { $0.subitems }
            .filter { $0.id == UpsellItemOperaId.mealDeal.rawValue }
            .reduce(0) { $0 + $1.quantity }
        return maxAdults == addedMealDeals
    }

    var isBreakfastDisabled: Bool {
        let breakfastIds = [
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue,
            UpsellItemOperaId.continentalBreakfast.rawValue
        ]
        let maxAdults = rooms
            .compactMap { $0.adults.count }
            .reduce(0, +)

        let addedUpsells = rooms
            .flatMap { $0.addedUpsells }

        let mealDealQuantity = addedUpsells
            .flatMap { $0.subitems }
            .filter { $0.id == UpsellItemOperaId.mealDeal.rawValue }
            .reduce(0) { $0 + $1.quantity }

        let breakfastDisabled = maxAdults == mealDealQuantity

        var prebookedBreakfastDisabled = false

        let preBookedBreakfasts = addedUpsells
            .filter { $0.isPrebooked && breakfastIds.contains($0.id) }
            .flatMap { $0.subitems }
            .reduce(0) { $0 + $1.quantity }
        let disabled = (mealDealQuantity + preBookedBreakfasts) == maxAdults
        prebookedBreakfastDisabled = preBookedBreakfasts == maxAdults

        return prebookedBreakfastDisabled || breakfastDisabled || disabled
    }

    func isMealDealDisabled(by roomID: String) -> Bool {
        let breakfastIds = [
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue,
            UpsellItemOperaId.continentalBreakfast.rawValue
        ]
        let maxAdults = rooms
            .first(where: { $0.id == roomID })?
            .adults.count

        let addedMealDeals = rooms
            .first(where: { $0.id == roomID })?
            .addedUpsells
            .flatMap {$0.subitems}
            .filter { breakfastIds.contains($0.id) }
            .reduce(0) { $0 + $1.quantity }
        return maxAdults == addedMealDeals
    }

    var isMealDealDisabled: Bool {
        let breakfastIds = [
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue,
            UpsellItemOperaId.continentalBreakfast.rawValue
        ]
        let maxAdults = rooms
            .compactMap { $0.adults.count }
            .reduce(0, +)

        let addedUpsells = rooms
            .flatMap { $0.addedUpsells }

        let breakfastQuantity = addedUpsells
            .flatMap {$0.subitems}
            .filter { breakfastIds.contains($0.id) }
            .reduce(0) { $0 + $1.quantity }
        let mealDisabled = maxAdults == breakfastQuantity
        var prebookedMealDisabled = false

        let preBookedMeals = addedUpsells
            .filter { $0.isPrebooked && $0.id == UpsellItemOperaId.mealDeal.rawValue }
            .flatMap { $0.subitems }
            .reduce(0) { $0 + $1.quantity }
        let disabled = (breakfastQuantity + preBookedMeals) == maxAdults
        prebookedMealDisabled = preBookedMeals == maxAdults

        return mealDisabled || prebookedMealDisabled || disabled
    }

    func hasUpsellInAnyRoom(id: String) -> Bool {
        rooms
            .flatMap { $0.addedUpsells }
            .filter { $0.id == id }
            .isNotEmpty
    }

    // reduce
    func hasUpsellInMultipleRooms(id: String) -> UpsellAvailability {
        var count = 0
        rooms.forEach { room in
            if room.addedUpsells.contains(where: { $0.id == id }) {
                count += 1
            }
        }
        return .init(rawValue: count) ?? .none
    }

    func hasPrebooked(id: String) -> UpsellItemState.Prebooked {
        var prebookStates: [UpsellItemState.Prebooked] = []
        rooms.forEach { room in
            if foodUpsells.contains(id) {
                prebookStates.append(room.upsellPrebookState(for: id))
            } else {
                prebookStates.append(room.uniquePrebookState(upsellID: id))
            }
        }
        if prebookStates.contains(.some) {
            return .some
        } else if prebookStates.filter({ $0 == .full }).count == prebookStates.count {
            return .full
        } else if prebookStates.filter({ $0 == .none }).count == prebookStates.count {
            return .none
        } else if prebookStates.filter({ $0 == .full }).count != prebookStates.count {
            return .some
        }
        return .none
    }

    func roomFoodPrebookState(roomID: String) -> UpsellItemState.Prebooked {
        guard let room = rooms.first(where: { $0.id == roomID }) else { return .none }
        return room.foodPrebookState()
    }

    func uniquePrebokState(roomID: String, upsellID: String) -> UpsellItemState.Prebooked {
        guard let room = rooms.first(where: { $0.id == roomID }) else { return .none }
        return room.uniquePrebookState(upsellID: upsellID)
    }

    enum UpsellAvailability: Int {
        case none
        case oneRoom
        case multipleRooms
    }

    var selectedStatuses: [String: Bool] {
        var statuses = [String: Bool]()
        let allAddedItems = rooms.flatMap { $0.addedUpsells }

        let itemsGrouped = Dictionary(grouping: allAddedItems, by: { $0.id })
        itemsGrouped.forEach({ item in
            var isSelected = false
            item.value.forEach({ upsell in
                if upsell.isBooked || upsell.isPrebooked {
                    isSelected = true
                }
            })
            statuses[item.key] = isSelected
        })
        return statuses
    }

    var priceItems: [PreStayInteractor.CIOLPriceBreakdownItemViewModel] {
        let subitems = rooms
            .flatMap { $0.addedUpsells }
            .filter { !$0.isPrebooked && ($0.isBooked || $0.subitems.contains(where: { $0.quantity > 0 })) }
            .flatMap { $0.subitems }
            .filter { $0.quantity > 0 }
        let grouped = Dictionary(grouping: subitems, by: { $0.id })
        var items: [PreStayInteractor.CIOLPriceBreakdownItemViewModel] = []
        grouped.forEach { dict in
            var item: PreStayInteractor.CIOLPriceBreakdownItemViewModel
            var title: String?
            var quantity: Int = 0
            var nights = 0
            var itemCost: Cost?
            var isFood = false
            var isWifi = false
            dict.value.forEach({ subitem in
                itemCost = subitem.cost
                if subitem.id == UpsellItemOperaId.freeChildBreakfast.rawValue {
                    itemCost = .init(amount: 0.0, currencyCode: NSLocale.current.currencySymbol ?? "£")
                }
                title = subitem.title
                nights = subitem.nights
                isFood = subitem.isFoodUpsell
                isWifi = subitem.isWifi
                quantity += subitem.quantity
                isFood = subitem.isFoodUpsell
            })
            guard let itemCost, var title else { return }
            var amount = itemCost.amount.multiplying(by: NSDecimalNumber(value: quantity))
            if isFood {
                amount = amount.multiplying(by: NSDecimalNumber(value: nights))
            }
            if isWifi {
                let wifiCostPerNight = itemCost.costDivided(by: Double(nights))
                let wifiPerDay = " (\(wifiCostPerNight.localizedValue) \(PILocalizedString("upsellPer")) \(PILocalizedString("ciol24H")))"
                title.append(contentsOf: wifiPerDay)
            }
            item = .init(
                name: title,
                value: .init(amount: Double(truncating: amount), currencyCode: itemCost.currencyCode),
                quantity: quantity
            )
            items.append(item)
        }
        return items
    }

    func isBreakFast(id: String) -> Bool {
        [
            UpsellItemOperaId.continentalBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue
        ].contains(id)
    }

    var didAddUpsellDuringCheckin: Bool {
        rooms
            .flatMap { $0.addedUpsells }
            .filter { $0.isPrebooked == false }
            .isNotEmpty
    }
}

struct CiolUpsellItemViewModel: CiolUpsellItemViewModelProtocol {
    var legendTitle: String
    var imageURL: URL?

    var title: String {
        if isBreakfast {
            guard let room, isMultiRoom else { return PILocalizedString("ciolUpsellBreakfastTitle") }
            var multiRoomTitle = "\(room.title) \(PILocalizedString("ciolUpsellBreakfastTitle"))"
            multiRoomTitle = multiRoomTitle.prefix(1).uppercased() + multiRoomTitle.lowercased().dropFirst()
            return multiRoomTitle
        } else {
            guard let room, isMultiRoom else { return legendTitle }
            var multiRoomTitle = "\(room.title) \(legendTitle)"
            multiRoomTitle = multiRoomTitle.prefix(1).uppercased() + multiRoomTitle.dropFirst()
            return multiRoomTitle
        }
    }

    var costSummary: String
    var itemDescription: String?
    var isFoodUpsell: Bool
    var isBooked: Bool
    var menuUrls: [RestaurantMenuItem]
    var allergensUrls: [AllergenInformation]
    var hasChildren: Bool
    var isMultiRoom: Bool
    var isWifi: Bool
    var nights: Int

    var subtitle: String {
        guard isMultiRoom else {
            return configureSubtitle()
        }

        if isCheckin {
            return configureSubtitle()
        }

        let isWifi = [UpsellItemOperaId.ultimateWifi7Days.rawValue, UpsellItemOperaId.ultimateWifi24Hours.rawValue]
            .contains(id)
        if let adultsText = room?.adults.joined(separator: ", ") {
            var multiText = "\(PILocalizedString("ciolUpsellFor")) \(adultsText)"
            if let numberOfKids = room?.numberOfChildren, numberOfKids > 0, !isWifi {
                let kids = String.localizedStringWithFormat(PILocalizedString("%d child(children)"), numberOfKids)
                multiText += " \(PILocalizedString("ciolUpsellAnd")) \(kids)"
            }
            return multiText
        } else {
            return "\(costSummary) \(PILocalizedString("ciolPerRoom"))"
        }
    }

    private var isCheckin: Bool {
        [UpsellItemOperaId.earlyCheckIn.rawValue, UpsellItemOperaId.lateCheckOut.rawValue].contains(id)
    }

    private func configureSubtitle() -> String {
        var mutableSubitems = subitems
        mutableSubitems.removeAll { $0.cost?.amount == nil || $0.cost?.amount as? Double == 0.0 }

        guard !isWifi else {
            guard let wifiCostPerNight = subitems.first?.cost?.costDivided(by: Double(nights)) else { return "" }
            return "\(wifiCostPerNight.localizedValue) \(PILocalizedString("upsellPer")) \(PILocalizedString("ciol24H"))"
        }

        guard !isCheckin else {
            return subitems.first?.cost?.localizedValue ?? ""
        }

        guard mutableSubitems.count > 1 else {
            return "\(subitems.first?.cost?.localizedValue ?? "")".ciolPriceRangeSubfixed
        }

        let hasAllItemsEqual = mutableSubitems.dropFirst().allSatisfy({ $0.cost?.amount == subitems.first?.cost?.amount })
        guard !hasAllItemsEqual else {
            return "\(mutableSubitems.first?.cost?.localizedValue ?? "")".ciolPriceRangeSubfixed
        }

        let sortedSubitems = mutableSubitems.sorted { firstItem, secondItem in
            (firstItem.cost?.amount as? Double ?? 0.0) < (secondItem.cost?.amount as? Double ?? 0.0)
        }
        return "\(PILocalizedString("ciolFrom")) \((sortedSubitems.first?.cost?.localizedValue ?? "0.0"))"
            .ciolPriceRangeSubfixed
    }

    var subitems: [CiolUpsellSubitemViewModel]
    var room: UpsellRoom?
    var id: String
    var enabled: Bool = true
    var bookingReference: String?
    var selected: Bool
    var isPrebooked: Bool
}

struct CiolUpsellViewModel: CiolUpsellViewModelProtocol {
    let shouldShowCloseoutMessage: Bool
    var availableUpsells: [CiolUpsellItemViewModelProtocol]?
    var prebookedUpsells: [CiolUpsellItemViewModelProtocol]?
    var rooms: [UpsellRoom]
    var hasChildren: Bool
    var isMultiRoom: Bool
    var nights: Int?
    var upsellsAddOnEnabled: Bool
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol?
    var addedPriceBreakdownViewModels: [CIOLPriceBreakdownItemViewModelProtocol] = []

    mutating func resetUpsells(available: [CiolUpsellItemViewModelProtocol], booked: [CiolUpsellItemViewModelProtocol]) {
        self.availableUpsells = available
        self.prebookedUpsells = booked
    }

    mutating func updateEnablement(isMealDealDisabled: Bool, isBreakfastDisabled: Bool) {
        guard var availableUpsells else { return }
        let breakfastIds = [
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue,
            UpsellItemOperaId.continentalBreakfast.rawValue
        ]
        for (index, value) in availableUpsells.enumerated() {
            guard value.isFoodUpsell else { continue }
            if value.id == UpsellItemOperaId.mealDeal.rawValue {
                var updatedValue = value
                updatedValue.enabled = !isMealDealDisabled
                availableUpsells[index] = updatedValue
            } else if breakfastIds.contains(where: { $0 == value.id }) {
                var updatedValue = value
                updatedValue.enabled = !isBreakfastDisabled
                availableUpsells[index] = updatedValue
            }
        }
        self.availableUpsells = availableUpsells
    }

    mutating func updateSelected(statuses: [String: Bool]) {
        availableUpsells?.indices.forEach({ index in
            if var upsell = availableUpsells?[safe: index] {
                if let updatedStatus = statuses[upsell.id] {
                    upsell.selected = updatedStatus
                    availableUpsells?[index] = upsell
                } else {
                    upsell.selected = false
                    availableUpsells?[index] = upsell
                }
            }
        })
    }

    func isBreakfast(for id: String) -> Bool {
        [
            UpsellItemOperaId.continentalBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue
        ].contains(id)
    }

    let breakfasts = [
        UpsellItemOperaId.continentalBreakfast.rawValue,
        UpsellItemOperaId.premierInnBreakfast.rawValue,
        UpsellItemOperaId.premierInnBreakfastDE.rawValue
    ]
}
