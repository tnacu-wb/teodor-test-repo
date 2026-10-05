//
//  CiolUpsellDetailsInteractor.swift
//  PremierInn
//
//  Created by Oltean Vasile Bogdan on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol CiolUpsellDetailsOutputDelegate: AnyObject {
    func didUpdateUpsell(for item: CiolUpsellItemViewModelProtocol, action: CiolUpsellDetailsOutputAction)
}

class CiolUpsellDetailsInteractor: CiolUpsellDetailsInteractorProtocol {
    class CiolUpsellDetailsViewModel: CiolUpsellDetailsViewModelProtocol {
        var prebookedItems: [String]?

        var upsell: CiolUpsellItemViewModelProtocol
        var butonConfig: CiolUpsellDetailsButton.ButtonState
        var removeButtonConfig: CiolUpsellDetailsButton.ButtonState?
        var addedFoodItems: Int
        var addedKidsItems: Int

        init(
            upsell: CiolUpsellItemViewModelProtocol,
            butonConfig: CiolUpsellDetailsButton.ButtonState,
            removeButtonConfig: CiolUpsellDetailsButton.ButtonState?,
            addedFoodItems: Int,
            addedKidsItems: Int,
            prebookedItems: [String]? = nil
        ) {
            self.upsell = upsell
            self.butonConfig = butonConfig
            self.addedFoodItems = addedFoodItems
            self.addedKidsItems = addedKidsItems
            self.removeButtonConfig = removeButtonConfig
            self.prebookedItems = prebookedItems
        }
    }

    var output: CiolUpsellDetailsOutput
    weak var outputDelegate: CiolUpsellDetailsOutputDelegate?
    private var inputParams: CiolUpsellDetailsInputParams

    var customAnalyticsParameters: PIDictionary? {
        var data = inputParams.analyticsParams
        data[PIAnalytics.Keys.checkInOnlineExtrasShownDescription] = inputParams.upsell.title
        return data
    }

    private func shouldDisableKidsSelection(upsellSubitem: CiolUpsellSubitemViewModel) -> Bool {
        [UpsellItemOperaId.premierInnBreakfast.rawValue, UpsellItemOperaId.mealDeal.rawValue].contains(upsellSubitem.id) &&
        inputParams.upsell.hasChildren &&
        upsellSubitem.quantity == 0
    }

    lazy var ciolUpsellDetailsViewModel: CiolUpsellDetailsViewModelProtocol = {
        CiolUpsellDetailsViewModel(
            upsell: inputParams.upsell,
            butonConfig: addButtonConfig,
            removeButtonConfig: inputParams.upsell
                                          .isBooked ? .booked(inputParams.upsell.removeAllItems) : nil,
            addedFoodItems: inputParams.addedFoodItems,
            addedKidsItems: inputParams.addedKidsItems,
            prebookedItems: inputParams.prebookedItems
        )
    }()

    var availableItems: Int {
        ciolUpsellDetailsViewModel.adults - (output.addedItems + ciolUpsellDetailsViewModel.addedFoodItems)
    }

    var availableKidsItems: Int {
        ciolUpsellDetailsViewModel.kids - (output.addedKidsItems + ciolUpsellDetailsViewModel.addedKidsItems)
    }

    var maxValue: Int {
        ciolUpsellDetailsViewModel.adults - ciolUpsellDetailsViewModel.addedFoodItems
    }

    var maxKidsValue: Int {
        ciolUpsellDetailsViewModel.kids - ciolUpsellDetailsViewModel.addedKidsItems
    }

    var addButtonConfig: CiolUpsellDetailsButton.ButtonState {
        let buttonConfig: CiolUpsellDetailsButton.ButtonState
        if output.mainItem.isFoodUpsell {
            if output.mainItem.isBooked {
                buttonConfig = .hidden
            } else if output.addedItems == 0 {
                buttonConfig = .disabled
            } else {
                var currencyCode = ""
                let totalCost = output.upsells
                    .map { ($0.cost, $0.quantity) }
                    .reduce(NSDecimalNumber(value: 0.0)) { partialResult, nextValue in
                        currencyCode = nextValue.0?.currencyCode ?? "£"
                        let costAmount = nextValue.0?.amount ?? 0.0
                        return partialResult.adding(costAmount.multiplying(by: .init(integerLiteral: nextValue.1)))
                    }
                let cost = Cost(
                    amount: totalCost.multiplying(by: NSDecimalNumber(value: inputParams.nights ?? 0)) as? Double ?? 0.0,
                    currencyCode: currencyCode
                )
                buttonConfig = .enabled(.addPeriod(cost.localizedValue, inputParams.nights ?? 1))
            }
        } else {
            if output.mainItem.isBooked {
                buttonConfig = .hidden
            } else {
                buttonConfig = inputParams.upsell.addToAllRooms ? .enabled(.addToAll) : .enabled(.add)
            }
        }
        return buttonConfig
    }

    init(inputParams: CiolUpsellDetailsInputParams) {
        self.inputParams = inputParams
        self.output = .init(
            mainItem: inputParams.upsell,
            upsells: inputParams.upsell.subitems,
            maxValue: (inputParams.upsell.room?.adults.count ?? 0) - inputParams.addedFoodItems
        )
        if inputParams.upsell.isFoodUpsell == false {
            updateOutput(id: inputParams.upsell.id, quantity: 1, resetBooked: false)
        }
    }


    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> Void) {
        let freeKidsMealID = UpsellItemOperaId.freeChildBreakfast.rawValue

        switch action {
        case .didAdd(let value):
            let result = value + 1

            if id == freeKidsMealID,
               availableKidsItems > 0 {
                updateOutput(id: id, quantity: result, completion: completion)
            } else if id != freeKidsMealID,
                      availableItems > 0 {
                updateOutput(id: id, quantity: result, completion: completion)
            }
        case .didWithdraw(let value):
            let result = value - 1
            guard result >= 0 else { return }
            updateOutput(id: id, quantity: result, completion: completion)
        }
    }

    func sendUpsellOutput(action: CiolUpsellDetailsOutputAction) {
        switch action {
        case .add:
            setBooked()
        case .remove:
            resetOutput()
        }
        var updatedUpsell = output.mainItem
        updatedUpsell.subitems = output.upsells
        outputDelegate?.didUpdateUpsell(for: updatedUpsell, action: action)
    }

    private func updateOutput(id: String, quantity: Int, resetBooked: Bool = true, completion: (() -> Void)? = nil) {
        output.updateQuantity(id: id, quantity: quantity, resetBooked: resetBooked)
        output.updateEnablement(maxValue: maxValue, maxKidsValue: maxKidsValue, id: id)
        completion?()
    }

    private func resetOutput() {
        output.resetOutput()
    }

    private func setBooked() {
        output.setBooked()
    }

    func trackAllergensScreenAnalytics() {
        trackMenuOrAllergensScreenAnalytics(stateName: PIAnalytics.StateNames.ciolAllergensSelected)
    }

    func trackMenuScreenAnalytics() {
        trackMenuOrAllergensScreenAnalytics(stateName: PIAnalytics.StateNames.ciolMenuSelected)
    }

    private func trackMenuOrAllergensScreenAnalytics(stateName: String) {
        AnalyticsManager.shared.trackState(stateName, data: inputParams.analyticsParams)
    }
}

struct CiolUpsellDetailsOutput {
    var upsells: [CiolUpsellSubitemViewModel] = []
    var mainItem: CiolUpsellItemViewModelProtocol

    init(mainItem: CiolUpsellItemViewModelProtocol, upsells: [CiolUpsellSubitemViewModel], maxValue: Int?) {
        self.mainItem = mainItem
        self.upsells = upsells
        updateEnablement(maxValue: maxValue)
    }

    var addedItems: Int {
        upsells
            .filter { $0.id != UpsellItemOperaId.freeChildBreakfast.rawValue }
            .reduce(0) { $0 + $1.quantity }
    }

    var addedKidsItems: Int {
        upsells
            .filter { $0.id == UpsellItemOperaId.freeChildBreakfast.rawValue }
            .reduce(0) { $0 + $1.quantity }
    }

    mutating func updateQuantity(id: String, quantity: Int, resetBooked: Bool = true) {
        if resetBooked {
            mainItem.isBooked = false
        }
        guard let index = upsells.firstIndex(where: {$0.id == id}),
              var outputItem = upsells[safe: index] else { return }
        outputItem.quantity = quantity
        upsells[index] = outputItem
    }

    mutating func updateEnablement(maxValue: Int?, maxKidsValue: Int? = nil, id: String? = nil) {
        let freeKidsFoodID = UpsellItemOperaId.freeChildBreakfast.rawValue
        var maxCountUpsells: [CiolUpsellSubitemViewModel]?
        if let maxValue, maxValue > 0 {
            maxCountUpsells = upsells.filter({ $0.quantity == maxValue && $0.id != freeKidsFoodID})
        }

        // disable upsells when one has reach max output
        for (index, value) in upsells.enumerated() {
            guard value.id != freeKidsFoodID, id != freeKidsFoodID else { continue }
            if let maxCountUpsells,
               maxCountUpsells.count == 1, let maxCountUpsell = maxCountUpsells.first,
               maxCountUpsell.id != freeKidsFoodID,
               value.id != maxCountUpsell.id {
                upsells[index].enabled = false
            } else {
                upsells[index].enabled = true
            }
        }

        guard let id else { return }
        // enable / disable kids upsell only for PI breakfast and Meal Deal
        if [
            UpsellItemOperaId.premierInnBreakfast.rawValue,
            UpsellItemOperaId.premierInnBreakfastDE.rawValue,
            UpsellItemOperaId.mealDeal.rawValue
        ].contains(id),
           let updatedUpsell = upsells.first(where: {$0.id == id}),
           let kidsUpsellIndex = upsells.firstIndex(where: { $0.id == UpsellItemOperaId.freeChildBreakfast.rawValue }) {
            var maxKidsItemLimitReached = false
            if let maxKidsValue, maxKidsValue == 0 {
                maxKidsItemLimitReached = true
            }
            upsells[kidsUpsellIndex].enabled = updatedUpsell.quantity != 0 && !maxKidsItemLimitReached
            if updatedUpsell.quantity == 0 {
                upsells[kidsUpsellIndex].quantity = 0
            }
        }
    }

    mutating func resetOutput() {
        mainItem.isBooked = false
        upsells.indices.forEach {
            upsells[$0].quantity = 0
            upsells[$0].enabled = upsells[$0].id != UpsellItemOperaId.freeChildBreakfast.rawValue ?
            true : false
        }
    }

    mutating func setBooked() {
        mainItem.isBooked = true
    }
}
