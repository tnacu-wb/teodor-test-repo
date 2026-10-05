//
//  CiolUpsellInteractor.swift
//  PremierInn
//
//  Created by Florin Velesca on 26.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol CiolUpsellDataProvider: ReservationProvider, RegCardProvider {
    func amendCiolPackages(amendInfo: CiolAmendInfo, completion: @escaping (_ response: Bool?, _ error: Error?) -> Void)
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool,
        completion: @escaping (_ response: ConfirmPreCheckInOut?, _ error: Error?) -> Void
    )
    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool,
        completion: @escaping (_ response: ([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)?, _ error: Error?)
        -> Void
    )
}

protocol CiolUpsellPayDelegate: AnyObject {
    func refreshForFailedPayment()
}

extension RequestsManager: CiolUpsellDataProvider {}
protocol PrestayDelegate: AnyObject {
    func didUpdateBalance(with newBalance: Cost)
}
class CiolUpsellInteractor: CiolUpsellInteractorProtocol, CanGetReservation {
    typealias UpsellsAnalytics = (
        extrasDescription: String,
        extrasIds: String,
        foodRevenueChange: Double,
        wifiRevenueChange: Double
    )

    let inputParams: CiolUpsellInputParams
    let dataProvider: CiolUpsellDataProvider

    var upsellOutput: RoomsUpsellOutput
    weak var prestayDelegate: PrestayDelegate?
    weak var output: CiolUpsellInteractorOutputProtocol?
    lazy var ciolUpsellViewModel: CiolUpsellViewModelProtocol = getUpsellViewModel(input: inputParams)

    init(inputParams: CiolUpsellInputParams, dataProvider: CiolUpsellDataProvider = RequestsManager()) {
        self.inputParams = inputParams
        self.upsellOutput = .init(rooms: inputParams.rooms, nights: inputParams.nights ?? 0)
        self.dataProvider = dataProvider
        upsellOutput.fillPrebooked(prebookedUpsells: ciolUpsellViewModel.prebookedUpsells ?? [])
        ciolUpsellViewModel.updateSelected(statuses: upsellOutput.selectedStatuses)
        ciolUpsellViewModel.availableUpsells?.forEach({ upsell in
            let cellSetup = upsellOutput.getCellSetup(id: upsell.id, isSelected: upsell.selected)
            ciolUpsellViewModel.updateEnablement(id: upsell.id, enabled: cellSetup.enabled ?? false)
        })
        ciolUpsellViewModel.updateEnablement(
            isMealDealDisabled: upsellOutput.isMealDealDisabled,
            isBreakfastDisabled: upsellOutput.isBreakfastDisabled
        )
    }

    var customAnalyticsParameters: PIDictionary? { inputParams.analyticsParams }

    var bookingReference: String? { inputParams.bookingReference }

    var upsellAmendInfo: CiolAmendInfo {
        var roomSelections: [CiolUpsellRoomSelection] = []
        var previousRoomSelections: [CiolUpsellRoomSelection] = []
        upsellOutput.rooms.forEach { room in
            var selections: [CiolUpsellSelection] = []
            var previousSelections: [CiolUpsellSelection] = []
            var newlyAddedModels = [CiolUpsellSubitemViewModel]()
            var prebookedModels = [CiolUpsellSubitemViewModel]()
            room.addedUpsells.forEach { upsell in
                let isPrebooked = upsell.isPrebooked
                upsell.subitems.forEach { subitem in
                    guard subitem.quantity > 0 else {
                        return
                    }

                    if isPrebooked {
                        prebookedModels.append(subitem)
                    } else {
                        newlyAddedModels.append(subitem)
                    }
                }
            }
            newlyAddedModels.forEach({ newUpsell in
                var quantity = newUpsell.quantity
                if let itemPrebooked = prebookedModels.first(where: { $0.id == newUpsell.id }),
                   quantity == 1, itemPrebooked.quantity == 1 {
                    quantity += itemPrebooked.quantity
                }
                let selection = CiolUpsellSelection(id: newUpsell.id, noOfSelections: quantity)
                selections.append(selection)
            })
            previousSelections = prebookedModels.map { CiolUpsellSelection(id: $0.id, noOfSelections: $0.quantity) }
            let roomSelection = CiolUpsellRoomSelection(reservationId: room.id, packagesSelection: selections)
            let previousRoomSelection = CiolUpsellRoomSelection(
                reservationId: room.id,
                packagesSelection: previousSelections
            )
            roomSelections.append(roomSelection)
            previousRoomSelections.append(previousRoomSelection)
        }
        return CiolAmendInfo(
            basketReferenceId: inputParams.reservationID ?? "",
            hotelId: inputParams.hotelID,
            arrivalDate: inputParams.arrivalDate ?? Date(),
            departureDate: inputParams.departureDate ?? Date(),
            roomsSelections: roomSelections,
            previousRoomsSelections: previousRoomSelections
        )
    }

    var paymentInputParams: CiolReviewAndPayInputParams? {
        CiolReviewAndPayInputParams(
            ciolFlow: inputParams.flow,
            bookerFirstName: inputParams.leadBookerFirstName ?? "",
            bookingReference: inputParams.bookingReference ?? "",
            hotelBrand: inputParams.hotelBrand ?? .premierInn,
            bookingSummaryViewModel: inputParams.bookingSummaryViewModel,
            priceBreakdownViewModel: ciolUpsellViewModel.displayedPriceBreakdownModel ?? inputParams
                                    .priceBreakdownViewModel,
            address: inputParams.address,
            defaultAnalyticsParams: inputParams.analyticsParams,
            additionalAnalyticsParams: additionalAnalyticsParams(),
            stay: inputParams.stay,
            upsellAmendInfo: upsellAmendInfo,
            regCardFlow: inputParams.regCardFlow,
            regCardInput: regCardInput(transactionID: ""),
            showBannerMessage: leadGuestIsNotGerman && inputParams.hotelBrand == .premierInnGermany
        )
    }

    func confirmPreCheckIn(basketReference: String, completion: @escaping (Error?) -> Void) {
        let isPIBACNP = inputParams.stay.paymentOption == .pibaCardNotPresent
        dataProvider.confirmPreCheckInOut(basketReference: basketReference, type: .checkIn, isCiol: isPIBACNP) { _, error in
            let ciolError = error != nil ? CIOLError.performPrestayChecks : error
            completion(ciolError)
        }
    }

    private func refreshUpsells() {
        Task {
            do {
                let reservationDetails = ReservationDetails(
                    reservationId: inputParams.reservationID ?? "",
                    surname: inputParams.stay.surname ?? "",
                    arrivalDate: inputParams.arrivalDate ?? Date(),
                    business: false,
                    token: nil
                )
                let reservation = try await getReservation(reservationDetails: reservationDetails, provider: dataProvider)
                let refreshedUpsells = try await getUpsells()

                await refreshState(
                    refreshedHotelPackages: refreshedUpsells.hotelPackages,
                    refreshedBookedPackages: refreshedUpsells.bookedPackages,
                    updatedBalance: reservation.outstandingAmount
                )
            } catch {
                await self.output?.stopLoadingUI(error: error)
            }
        }
    }

    func goToNextStep() {
        guard let paymentInputParams else { return }
        let hasPaymentItems = paymentInputParams.priceBreakdownViewModel.items.isNotEmpty || upsellOutput
            .didAddUpsellDuringCheckin
        // Check if the current price breakdown has a non-zero total (handles added upsells)
        let currentTotal = paymentInputParams.priceBreakdownViewModel.items
            .reduce(0.0) { $0 + ($1.value.amount.doubleValue * Double($1.quantity)) }
        let hasOutstandingBalance = currentTotal > 0
        // PIBA CNP bookings skip payment (paid at front desk)
        let isPIBACNP = inputParams.stay.paymentOption == .pibaCardNotPresent
        let shouldGoToPayment = hasPaymentItems && hasOutstandingBalance && !isPIBACNP
        if shouldGoToPayment {
            self.output?.goToPayment(inputParams: paymentInputParams)
            return
        }
        switch inputParams.regCardFlow {
        case .general:
            checkIn()
        case .regCard:
            guard let leadGuest = inputParams.guests.first(where: { $0.type == .lead }) else {
                return
            }
            // For non-German guests at German hotels, always show authorization
            // This includes £0.00 authorization for legal compliance
            if leadGuest.isNotGerman,
               let output = output {
                showAuthorize(provider: dataProvider, output: output)
            } else {
                Task { @MainActor in
                    let updatePreCheckinInput = UpdatePrecheckInParams(
                        hotelId: inputParams.hotelID,
                        reservationId: inputParams.rooms.first?.id ?? "",
                        arrivalTime: inputParams.arrivalDate?
                                                                       .parameterString ?? ""
                    )
                    do {
                        let regCardCheckinStatus = try await updatePreCheckInStatus(
                            provider: dataProvider,
                            params: updatePreCheckinInput
                        )
                        guard let regCardCheckinStatus,
                              regCardCheckinStatus == .success else { throw CIOLError.performPrestayChecks }
                        checkIn()
                    } catch {
                        output?.stopLoadingUI(error: error)
                    }
                }
            }
        }
    }

    func checkIn() {
        var info = self.inputParams.analyticsParams
        info = info.mergeByKeepingAllValues(with: self.additionalAnalyticsParams())
        let confirmationDetails = CiolConfirmationDetails(
            bookerFirstName: self.inputParams.leadBookerFirstName ?? "",
            hotelBrand: self.inputParams.hotelBrand ?? .premierInn,
            ciolStartFlow: self.inputParams.flow,
            hotelImage: self.inputParams.hotelImage,
            analyticsInfo: info,
            stay: self.inputParams.stay
        )
        // PIBA CNP bookings show confirmation popup before check-in
        let isPIBACNP = confirmationDetails.stay.paymentOption == .pibaCardNotPresent
        if isPIBACNP {
            // PIBA CNP bookings: Show pop-up, then call confirmPreCheckIn after user confirms
            self.output?.goToCompletion(error: nil, ciolConfirmationDetails: confirmationDetails)
        } else {
            // All other bookings: Call confirmPreCheckIn immediately, skip pop-up, go directly to completion
            dataProvider.confirmPreCheckInOut(
                basketReference: confirmationDetails.stay.reservationIdentifier ?? "",
                type: .checkIn,
                isCiol: false
            ) { [weak self] _, error in
                self?.output?.goDirectlyToCompletion(error: error, ciolConfirmationDetails: confirmationDetails)
            }
        }
    }

    private func reload() {
        ciolUpsellViewModel.updateSelected(statuses: upsellOutput.selectedStatuses)
        ciolUpsellViewModel.updateEnablement(
            isMealDealDisabled: upsellOutput.isMealDealDisabled,
            isBreakfastDisabled: upsellOutput.isBreakfastDisabled
        )
        ciolUpsellViewModel.availableUpsells?.forEach({ upsell in
            guard !upsell.isFoodUpsell else { return }
            let cellSetup = upsellOutput.getCellSetup(id: upsell.id, isSelected: upsell.selected)
            ciolUpsellViewModel.updateEnablement(id: upsell.id, enabled: cellSetup.enabled ?? false)
        })
        ciolUpsellViewModel.updateAddedBreakdown(addedItems: upsellOutput.priceItems)
        output?.reloadData(model: ciolUpsellViewModel)
    }

    func additionalAnalyticsParams() -> PIDictionary {
        var info = inputParams.analyticsParams
        let outstandingBalance = inputParams.outstandingBalance?.amount.doubleValue ?? 0
        info[PIAnalytics.Keys.checkInOnline] = true
        info[PIAnalytics.Keys.checkInOnlineBookingID] = inputParams.bookingReference
        info[PIAnalytics.Keys.checkInOnlinePrepaid] = inputParams.outstandingBalance == nil || outstandingBalance == 0
        info[PIAnalytics.Keys.checkInOnlineRoomTypeChange] = false

        let upsellsAnalytics = upsellsAnalytics()
        if upsellsAnalytics.foodRevenueChange > 0.0 {
            info[PIAnalytics.Keys.checkInOnlineFoodRevenueChange] = upsellsAnalytics.foodRevenueChange.stringForAnalyticsCost
        }
        if upsellsAnalytics.wifiRevenueChange > 0.0 {
            info[PIAnalytics.Keys.checkInOnlineWifiRevenueChange] = upsellsAnalytics.wifiRevenueChange.stringForAnalyticsCost
        }

        info[PIAnalytics.Keys.checkInOnlineTotalRevenueChange] = (upsellsAnalytics.foodRevenueChange + upsellsAnalytics
            .wifiRevenueChange).stringForAnalyticsCost
        info[PIAnalytics.Keys.checkInOnlineAddedExtras] = upsellsAnalytics.extrasDescription
        info[PIAnalytics.Keys.checkInOnlineExtrasBooked] = upsellsAnalytics.extrasIds

        info[PIAnalytics.Keys.checkInOnlineRevenue] = (outstandingBalance + upsellsAnalytics
            .foodRevenueChange + upsellsAnalytics.wifiRevenueChange).stringForAnalyticsCost
        info[PIAnalytics.Keys.checkInOnlineSpecialOccasion] = inputParams.selectedPreference?.name
        return info
    }

    func getCellSetup(id: String, isSelected: Bool) -> CiolUpsellCellSetup {
        upsellOutput.getCellSetup(id: id, isSelected: isSelected)
    }

    private func upsellsAnalytics() -> UpsellsAnalytics {
        var description = ""
        var codes = ""
        var foodCost: Double = 0.0
        var wifiCost: Double = 0.0
        upsellOutput.rooms.forEach {
            $0.addedUpsells
                .filter { !$0.isPrebooked }
                .forEach { upsell in
                    upsell.subitems
                        .filter { $0.quantity > 0 }
                        .forEach {
                            description.append("\($0.quantity) x \($0.title), ")
                            codes.append("\($0.id), ")
                            let costAmount = $0.cost?.amount.doubleValue ?? 0.0
                            if upsell.isFoodUpsell {
                                foodCost += costAmount * Double($0.quantity) * Double($0.nights)
                            }
                            if upsell.isWifi {
                                wifiCost += costAmount * Double($0.quantity)
                            }
                        }
                }
        }
        return UpsellsAnalytics(
            extrasDescription: description,
            extrasIds: codes,
            foodRevenueChange: foodCost,
            wifiRevenueChange: wifiCost
        )
    }


    func trackPriceBreakdownTapAnalytics() {
        var dictionary = inputParams.analyticsParams
        dictionary[PIAnalytics.Keys.checkInOnlineBtnExpand] = upsellsAnalytics()

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.ciolUpsells, data: dictionary)
    }

    func regCardInput(transactionID: String) -> RegCardInput? {
        guard let leadGuest = inputParams.guests.first(where: { $0.type == .lead }) else { return nil }
        let pdfInput = PDFBookingDetails(
            transactionID: transactionID,
            reservationID: inputParams.reservationID ?? "",
            profileID: leadGuest.profileId ?? "",
            hotelName: inputParams.hotelName ?? "",
            hotelAddress: inputParams.hotelAddress ?? "",
            arrivalDate: inputParams.arrivalDate?.parameterString ?? "",
            departureDate: inputParams.departureDate?.parameterString ?? "",
            guestList: inputParams.guests
        )

        let fileAttachmentInput = AuthorizationFileAttachmentParams(
            fileName: "",
            reservationId: inputParams.rooms.first?.id ?? "",
            hotelId: inputParams.hotelID,
            fileAttachment: ""
        )

        let updatePreCheckinInput = UpdatePrecheckInParams(
            hotelId: inputParams.hotelID,
            reservationId: inputParams.rooms.first?.id ?? "",
            arrivalTime: inputParams.arrivalDate?.parameterString ?? ""
        )

        var info = self.inputParams.analyticsParams
        info = info.mergeByKeepingAllValues(with: self.additionalAnalyticsParams())
        let confirmInput = CiolConfirmationDetails(
            bookerFirstName: inputParams.leadBookerFirstName ?? "",
            hotelBrand: inputParams.hotelBrand ?? .premierInnGermany,
            ciolStartFlow: .bookingConfirmation,
            hotelImage: inputParams.hotelImage,
            analyticsInfo: info,
            stay: inputParams.stay
        )
        return RegCardInput(
            pdfInput: pdfInput,
            fileAttachmentInput: fileAttachmentInput,
            updatePrecheckinInput: updatePreCheckinInput,
            confirmInput: confirmInput,
            shouldAttachPDF: leadGuest.isNotGerman
        )
    }

    var paymentViewLayout: WebViewControllerLayout {
        leadGuestIsNotGerman ? .withBanner(authorizationBannerMessage) : .general
    }

    private var leadGuestIsNotGerman: Bool {
        inputParams.guests.first(where: { $0.type == .lead })?.isNotGerman == true
    }
}

extension CiolUpsellInteractor: CiolUpsellDetailsOutputDelegate {
    func didUpdateUpsell(for item: CiolUpsellItemViewModelProtocol, action: CiolUpsellDetailsOutputAction) {
        switch action {
        case .add:
            upsellOutput.addUpsell(item)
        case .remove:
            upsellOutput.removeUpsell(item)
        }
        reload()
    }
}

extension CiolUpsellInteractor: RoomsUpsellOutputDelegate {
    func updated(with roomOutput: RoomsUpsellOutput?) {
        guard let roomOutput else { return }
        self.upsellOutput = roomOutput
        reload()
    }
}

private extension CiolUpsellInteractor {
    func descriptionFor(upsellItem: UpsellItem, isMultiRoom: Bool, nights: Int) -> String {
        let price = upsellItem.price.localizedValue

        if upsellItem.foodUpsell == true {
            return "\(price) \(PILocalizedString("upsellPer")) \(PILocalizedString("upsellAdultDay", comment: "per adult/day"))"
        } else if upsellItem.wifiUpsell == true {
            let wifiCostPerNight = upsellItem.price.costDivided(by: Double(nights))
            return "\(wifiCostPerNight.localizedValue) \(PILocalizedString("upsellPer")) \(PILocalizedString("ciol24H"))"
        } else if upsellItem.upsellOperaId == .earlyCheckIn || upsellItem.upsellOperaId == .lateCheckOut {
            return "\(price) \(PILocalizedString("upsellTotalForAllRooms"))"
        } else {
            let multiRoomText = "\(price) \(PILocalizedString("upsellPer")) \(PILocalizedString("room", comment: "per room"))"
            return isMultiRoom ? multiRoomText : price
        }
    }

    func descriptionFor(lowestCost: Cost?, highestCost: Cost?) -> String {
        if lowestCost?.amount.doubleValue == highestCost?.amount.doubleValue {
            return "\(lowestCost?.localizedValue ?? "0.0") \(PILocalizedString("upsellPer")) \(PILocalizedString("upsellAdultDay", comment: "per adult/day"))"
        } else {
            return "\(lowestCost?.localizedValue ?? "0.0") - \(highestCost?.localizedValue ?? "0.0") \(PILocalizedString("upsellPer")) \(PILocalizedString("upsellAdultDay", comment: "per adult/day"))"
        }
    }

    func getMealDealUpsellItem(
        upsellItem: UpsellItem,
        hasChildren: Bool,
        isMultiRoom: Bool,
        costSummary: String,
        rooms: [UpsellRoom],
        isPrebooked: Bool
    ) -> CiolUpsellItemViewModel {
        var upsellSubItems: [CiolUpsellSubitemViewModel] = []
        var mealDescriptions: [CiolUpsellItemDescription] = [CiolUpsellItemDescription.generic(upsellItem.itemDescription)]
        if hasChildren {
            let kidsBadgeDescription = CiolUpsellItemDescription.kidsLabel(PILocalizedString("ciolUpsellKidsEatFreeItem"))
            mealDescriptions.append(kidsBadgeDescription)
        }
        if isMultiRoom {
            let priceDescription = CiolUpsellItemDescription.generic(costSummary)
            mealDescriptions.append(priceDescription)
        }
        let mainItem = CiolUpsellSubitemViewModel(
            title: upsellItem.legend,
            bookingHasKids: hasChildren,
            canUpdateQuantity: true,
            id: upsellItem.id,
            quantity: upsellItem.quantity ?? 0,
            descriptions: mealDescriptions,
            cost: upsellItem.individualCost,
            isFoodUpsell: true,
            nights: inputParams.nights ?? 0
        )
        upsellSubItems.append(mainItem)
        if hasChildren, !isPrebooked {
            let freeBreakFastDescriptions = [CiolUpsellItemDescription.generic(PILocalizedString("ciolKidsFreeUpsellTitle"))]
            let freeKidsBreakfast = CiolUpsellSubitemViewModel(
                title: PILocalizedString("ciolKidsBreakfastUpsellTitle"),
                bookingHasKids: hasChildren,
                canUpdateQuantity: true,
                id: UpsellItemOperaId.freeChildBreakfast.rawValue,
                enabled: false,
                descriptions: freeBreakFastDescriptions,
                isFoodUpsell: false,
                nights: inputParams.nights ?? 0
            )
            upsellSubItems.append(freeKidsBreakfast)
        }

        return getUpsellItemViewModel(
            hasChildren: hasChildren,
            isMultiRoom: isMultiRoom,
            costSummary: costSummary,
            upsellItem: upsellItem,
            subitems: upsellSubItems,
            rooms: rooms
        )
    }

    func getBreakFastUpsellItem(
        upsellItem: UpsellItem,
        isMultiRoom: Bool,
        hasChildren: Bool,
        subitems: [CiolUpsellSubitemViewModel]?,
        costSummary: String,
        rooms: [UpsellRoom]
    ) -> CiolUpsellItemViewModel {
        var breakFastSubItems: [CiolUpsellSubitemViewModel] = subitems ?? []
        var breakFastDescriptions: [CiolUpsellItemDescription] = [
            CiolUpsellItemDescription.title(.init(
                title: upsellItem.legend,
                subtitle: costSummary
            )),
            CiolUpsellItemDescription.generic(upsellItem.itemDescription)
        ]
        if hasChildren, upsellItem.upsellOperaId == .premierInnBreakfast {
            let kidsBadgeDescription = CiolUpsellItemDescription.kidsLabel(PILocalizedString("ciolUpsellKidsEatFreeItem"))
            breakFastDescriptions.append(kidsBadgeDescription)
        }
        let mainItem = CiolUpsellSubitemViewModel(
            title: upsellItem.legend,
            bookingHasKids: hasChildren,
            canUpdateQuantity: true,
            id: upsellItem.id,
            quantity: upsellItem.quantity ?? 0,
            descriptions: breakFastDescriptions,
            cost: upsellItem.individualCost,
            isFoodUpsell: true,
            nights: inputParams.nights ?? 0
        )
        breakFastSubItems.append(mainItem)

        let sortedSubitems = breakFastSubItems.sorted {
            ($0.cost?.amount as? Double ?? 0.0) < ($1.cost?.amount as? Double ?? 0.0)
        }
        let description = "\(PILocalizedString("ciolFrom")) \((sortedSubitems.first?.cost?.localizedValue ?? "0.0"))"
            .ciolPriceRangeSubfixed

        return getUpsellItemViewModel(
            title: PILocalizedString("Breakfast"),
            hasChildren: hasChildren,
            isMultiRoom: isMultiRoom,
            costSummary: description,
            upsellItem: upsellItem,
            subitems: breakFastSubItems,
            rooms: rooms
        )
    }

    func getGenericUpsell(
        upsellItem: UpsellItem,
        isMultiRoom: Bool,
        hasChildren: Bool,
        costSummary: String,
        rooms: [UpsellRoom]
    ) -> CiolUpsellItemViewModel {
        var upsellSubItems: [CiolUpsellSubitemViewModel] = []
        var otherDescriptions: [CiolUpsellItemDescription] = [CiolUpsellItemDescription.generic(upsellItem.itemDescription)]
        if isMultiRoom, upsellItem.wifiUpsell {
            let priceDescription = CiolUpsellItemDescription.generic(costSummary)
            otherDescriptions.append(priceDescription)
        }
        let mainItem = CiolUpsellSubitemViewModel(
            title: upsellItem.legend,
            bookingHasKids: hasChildren,
            canUpdateQuantity: false,
            id: upsellItem.id,
            quantity: upsellItem.quantity ?? 0,
            descriptions: otherDescriptions,
            cost: upsellItem.individualCost,
            isFoodUpsell: false,
            nights: inputParams.nights ?? 0,
            isWifi: upsellItem.wifiUpsell
        )
        upsellSubItems.append(mainItem)
        return getUpsellItemViewModel(
            hasChildren: hasChildren,
            isMultiRoom: isMultiRoom,
            costSummary: costSummary,
            upsellItem: upsellItem,
            subitems: upsellSubItems,
            rooms: rooms
        )
    }

    private func getUpsellViewModel(
        input: CiolUpsellInputParams,
        refreshedAvailableUpsells: [UpsellItem]? = nil,
        refreshedBookedUpsells: [UpsellItem]? = nil
    ) -> CiolUpsellViewModelProtocol {
        var availables: [CiolUpsellItemViewModelProtocol] = []
        var prebooked: [CiolUpsellItemViewModelProtocol] = []
        let processed = getProcessedUpsells(input: input)
        availables = processed.available
        prebooked = processed.booked

        let closeoutUpsells = input.hotel?.closeoutUpsells(
            arrivalDate: input.arrivalDate,
            departureDate: input.departureDate,
            upsells: input.availableUpsells ?? []
        )
        return CiolUpsellViewModel(
            shouldShowCloseoutMessage: closeoutUpsells?.isNotEmpty ?? false,
            availableUpsells: availables,
            prebookedUpsells: prebooked,
            rooms: input.rooms,
            hasChildren: input.hasChildren,
            isMultiRoom: input.isMultiRoom,
            nights: input.nights,
            upsellsAddOnEnabled: input.stay.upsellsAddOnEnabled,
            priceBreakdownViewModel: input.priceBreakdownViewModel
        )
    }

    // swiftlint:disable:next function_body_length
    private func getProcessedUpsells(
        input: CiolUpsellInputParams,
        refreshedAvailableUpsells: [UpsellItem]? = nil,
        refreshedBookedUpsells: [UpsellItem]? = nil
    ) -> (
        available: [CiolUpsellItemViewModelProtocol],
        booked: [CiolUpsellItemViewModelProtocol]
    ) {
        var availables: [CiolUpsellItemViewModelProtocol] = []
        var prebooked: [CiolUpsellItemViewModelProtocol] = []
        let upsellsExcludingCloseoutUpsells = input.hotel?.upsellsAvailable(
            arrivalDate: input.arrivalDate,
            departureDate: input.departureDate,
            upsells: input.availableUpsells ?? []
        )
        let availableUpsells = refreshedAvailableUpsells ?? upsellsExcludingCloseoutUpsells ?? []
        let bookedUpsells = refreshedBookedUpsells ?? input.bookedUpsells ?? []
        let isMultiRoom = input.isMultiRoom
        let hasChildren = input.hasChildren
        var breakFastUpsell: CiolUpsellItemViewModel?
        var bookedBreakfasts: [CiolUpsellItemViewModel] = []

        for upsellItem in availableUpsells {
            let upsellOperaId = upsellItem.upsellOperaId
            let costSummary = descriptionFor(
                upsellItem: upsellItem,
                isMultiRoom: isMultiRoom,
                nights: inputParams.nights ?? 0
            )
            switch upsellOperaId {
            case .mealDeal:
                let mealUpsell = getMealDealUpsellItem(
                    upsellItem: upsellItem,
                    hasChildren: hasChildren,
                    isMultiRoom: isMultiRoom,
                    costSummary: costSummary,
                    rooms: input.rooms,
                    isPrebooked: false
                )
                availables.append(mealUpsell)

            case .premierInnBreakfast, .premierInnBreakfastDE, .continentalBreakfast:
                breakFastUpsell = getBreakFastUpsellItem(
                    upsellItem: upsellItem,
                    isMultiRoom: isMultiRoom,
                    hasChildren: hasChildren,
                    subitems: breakFastUpsell?.subitems,
                    costSummary: costSummary,
                    rooms: input.rooms
                )
            case .freeChildBreakfast: break

            default:
                let newUpsell = getGenericUpsell(
                    upsellItem: upsellItem,
                    isMultiRoom: isMultiRoom,
                    hasChildren: hasChildren,
                    costSummary: costSummary,
                    rooms: input.rooms
                )
                availables.append(newUpsell)
            }
        }

        for upsellItem in bookedUpsells {
            let upsellOperaId = upsellItem.upsellOperaId
            let costSummary = descriptionFor(
                upsellItem: upsellItem,
                isMultiRoom: isMultiRoom,
                nights: inputParams.nights ?? 0
            )
            switch upsellOperaId {
            case .mealDeal:
                let mealUpsell = getMealDealUpsellItem(
                    upsellItem: upsellItem,
                    hasChildren: hasChildren,
                    isMultiRoom: isMultiRoom,
                    costSummary: costSummary,
                    rooms: input.rooms,
                    isPrebooked: true
                )
                prebooked.append(mealUpsell)

            case .premierInnBreakfast, .premierInnBreakfastDE, .continentalBreakfast:
                if let existingRoomBreakfastIndex = bookedBreakfasts.firstIndex(where: { $0.room?.id == upsellItem.roomId }),
                   var existingRoomBreakfast = bookedBreakfasts[safe: existingRoomBreakfastIndex] {
                    existingRoomBreakfast = getBreakFastUpsellItem(
                        upsellItem: upsellItem,
                        isMultiRoom: isMultiRoom,
                        hasChildren: hasChildren,
                        subitems: existingRoomBreakfast.subitems,
                        costSummary: costSummary,
                        rooms: input.rooms
                    )
                    bookedBreakfasts[existingRoomBreakfastIndex] = existingRoomBreakfast
                } else {
                    bookedBreakfasts.append(getBreakFastUpsellItem(
                        upsellItem: upsellItem,
                        isMultiRoom: isMultiRoom,
                        hasChildren: hasChildren,
                        subitems: nil,
                        costSummary: costSummary,
                        rooms: input.rooms
                    ))
                }

            case .freeChildBreakfast:
                let freeKidsBreakfast = CiolUpsellSubitemViewModel(
                    title: PILocalizedString("ciolKidsBreakfastUpsellTitle"),
                    bookingHasKids: hasChildren,
                    canUpdateQuantity: true,
                    id: UpsellItemOperaId.freeChildBreakfast.rawValue,
                    quantity: upsellItem.quantity ?? 0,
                    enabled: false,
                    descriptions: [],
                    isFoodUpsell: false,
                    nights: inputParams.nights ?? 0
                )
                let kidsItem = getUpsellItemViewModel(
                    hasChildren: hasChildren,
                    isMultiRoom: isMultiRoom,
                    costSummary: costSummary,
                    upsellItem: upsellItem,
                    subitems: [freeKidsBreakfast],
                    rooms: input.rooms
                )

                prebooked.append(kidsItem)

            default:
                let newUpsell = getGenericUpsell(
                    upsellItem: upsellItem,
                    isMultiRoom: isMultiRoom,
                    hasChildren: hasChildren,
                    costSummary: costSummary,
                    rooms: input.rooms
                )
                prebooked.append(newUpsell)
            }
        }
        prebooked.append(contentsOf: bookedBreakfasts)

        if let breakFastUpsell {
            var breakFastSubItems: [CiolUpsellSubitemViewModel] = breakFastUpsell.subitems
            if hasChildren {
                let freeBreakFastDescriptions = [CiolUpsellItemDescription.title(.init(
                    title: PILocalizedString("ciolKidsBreakfastUpsellTitle"),
                    subtitle: PILocalizedString("ciolFree")
                ))]
                let freeKidsBreakfast = CiolUpsellSubitemViewModel(
                    title: PILocalizedString("ciolKidsBreakfastUpsellTitle"),
                    bookingHasKids: hasChildren,
                    canUpdateQuantity: true,
                    id: UpsellItemOperaId.freeChildBreakfast.rawValue,
                    enabled: false,
                    descriptions: freeBreakFastDescriptions,
                    isFoodUpsell: false,
                    nights: inputParams.nights ?? 0
                )
                breakFastSubItems.append(freeKidsBreakfast)
            }
            var breakfastWithFreeOption = breakFastUpsell
            breakfastWithFreeOption.subitems = breakFastSubItems
            availables.insert(breakfastWithFreeOption, at: 0)
        }

        return (availables, prebooked)
    }

    private func getUpsellItemViewModel(
        title: String? = nil,
        hasChildren: Bool,
        isMultiRoom: Bool,
        costSummary: String,
        upsellItem: UpsellItem,
        subitems: [CiolUpsellSubitemViewModel],
        rooms: [UpsellRoom]
    ) -> CiolUpsellItemViewModel {
        let id = isBreakfast(for: upsellItem.id) ? UpsellItemOperaId.premierInnBreakfast.rawValue : upsellItem.id
        var upsellModel = CiolUpsellItemViewModel(
            legendTitle: upsellItem.legend,
            imageURL: upsellItem.image,
            costSummary: costSummary,
            itemDescription: upsellItem.itemDescription,
            isFoodUpsell: upsellItem.foodUpsell,
            isBooked: false,
            menuUrls: upsellItem.menu != nil ? [upsellItem.menu!] : [],
            allergensUrls: upsellItem
                                                  .allergenInformation != nil ? [upsellItem.allergenInformation!] : [],
            hasChildren: hasChildren,
            isMultiRoom: isMultiRoom,
            isWifi: upsellItem.wifiUpsell,
            nights: inputParams.nights ?? 0,
            subitems: subitems,
            id: id,
            selected: upsellItem.roomId != nil,
            isPrebooked: upsellItem.roomId != nil
        )
        let prebookedRoom = rooms.first(where: { $0.id == upsellItem.roomId })
        upsellModel.room = prebookedRoom
        return upsellModel
    }

    private func isBreakfast(for id: String) -> Bool {
        [
            UpsellItemOperaId.continentalBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue
        ].contains(id)
    }
}


extension CiolUpsellInteractor {
    func getUpsells() async throws -> (hotelPackages: [UpsellItem], bookedPackages: [UpsellItem]) {
        guard let reservationId = inputParams.reservationID,
              let bookingFlowId = inputParams.bookingFlowId,
              let arrivalDate = inputParams.arrivalDate,
              let checkOutDate = inputParams.departureDate else { throw CIOLError.performPrestayChecks }

        let bookingDetails = BookingDetails()
        var criteria = Criteria()
        criteria.arrivalDate = arrivalDate
        criteria.nights = arrivalDate.numberOfNights(to: checkOutDate)
        bookingDetails.criteria = criteria
        return try await withCheckedThrowingContinuation { continuation in
            dataProvider.getPackages(
                reservationId: reservationId,
                bookingDetails: bookingDetails,
                hotelCode: inputParams.hotelID,
                bookingFlowId: bookingFlowId,
                showMealInclusiveRate: false
            ) { [weak self] response, error in
                guard let self = self,
                      error == nil,
                      let hotelPackages = response?.0,
                      let bookedPackages = response?.1 else {
                    continuation.resume(throwing: CIOLError.performPrestayChecks)
                    return
                }

                var ciolUpsellPackages: [UpsellItem] = []

                ciolUpsellPackages.append(
                    contentsOf: CiolUpsellConfigurator.availableFoodUpsells(
                        hotelPackages: hotelPackages,
                        bookedPackages: bookedPackages,
                        numberOfAdults: inputParams.rooms.map { $0.adults.count
                        }.reduce(0, +)
                    ).0
                )

                ciolUpsellPackages.append(
                    contentsOf: CiolUpsellConfigurator.availableEciLcoUpsells(
                        hotelPackages: hotelPackages,
                        bookedPackages: bookedPackages
                    ).0
                )

                ciolUpsellPackages.append(
                    contentsOf: CiolUpsellConfigurator.availableWifiUpsells(
                        hotelPackages: hotelPackages,
                        bookedPackages: bookedPackages,
                        roomCount: inputParams.rooms.count
                    ).0
                )

                continuation.resume(with: .success((ciolUpsellPackages, bookedPackages)))
            }
        }
    }

    @MainActor
    private func refreshState(
        refreshedHotelPackages: [UpsellItem],
        refreshedBookedPackages: [UpsellItem],
        updatedBalance: Cost?
    ) {
        if let updatedBalance {
            let updatedBalanceModel = priceBreakdown(with: updatedBalance)
            ciolUpsellViewModel.updateBalance(newBalance: updatedBalanceModel)
            prestayDelegate?.didUpdateBalance(with: updatedBalance)
        }
        self.upsellOutput = .init(rooms: inputParams.rooms, nights: inputParams.nights ?? 0)
        let processed = getProcessedUpsells(
            input: inputParams,
            refreshedAvailableUpsells: refreshedHotelPackages,
            refreshedBookedUpsells: refreshedBookedPackages
        )
        ciolUpsellViewModel.resetUpsells(available: processed.available, booked: processed.booked)
        upsellOutput.fillPrebooked(prebookedUpsells: ciolUpsellViewModel.prebookedUpsells ?? [])
        reload()
        self.output?.stopLoadingUI(error: nil)
    }
}


extension CiolUpsellInteractor: CiolUpsellPayDelegate {
    func refreshForFailedPayment() {
        self.output?.startLoadingUI()
        refreshUpsells()
    }
}
