//
//  CiolUpsellDetailsModule.swift
//  PremierInn
//
//  Created by Oltean Vasile Bogdan on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

enum CiolUpsellDetailsModule {
    static func build(
        inputParams: CiolUpsellDetailsInputParams,
        outputDelegate: CiolUpsellDetailsOutputDelegate
    ) -> UIViewController {
        let viewController = CiolUpsellDetailsViewController()
        viewController.modalPresentationStyle = .overFullScreen
        viewController.modalTransitionStyle = .crossDissolve
        let presenter = CiolUpsellDetailsPresenter()
        let interactor = CiolUpsellDetailsInteractor(inputParams: inputParams)
        interactor.outputDelegate = outputDelegate
        let router = CiolUpsellDetailsRouter()

        viewController.eventHandler = presenter

        presenter.view = viewController
        presenter.interactor = interactor
        presenter.router = router

        router.viewController = viewController

        return viewController
    }
}

protocol CiolUpsellDetailsViewModelProtocol {
    var upsell: CiolUpsellItemViewModelProtocol { get set }
    var butonConfig: CiolUpsellDetailsButton.ButtonState { get }
    var shouldShowBorder: Bool { get }
    var shouldShowMenu: Bool { get }
    var shouldShowAllergens: Bool { get }
    var adults: Int { get }
    var addedFoodItems: Int { get }
    var addedKidsItems: Int { get }
    var maxFoodItems: Int { get }
    var kids: Int { get }
    var removeButtonConfig: CiolUpsellDetailsButton.ButtonState? { get set }
    var prebookedItems: [String]? { get set }

    mutating func updateItem(for id: String, quantity: Int)
}

extension CiolUpsellDetailsViewModelProtocol {
    var shouldShowBorder: Bool {
        upsell.isFoodUpsell
    }

    var shouldShowMenu: Bool {
        upsell.isFoodUpsell && upsell.menuUrls.isNotEmpty
    }

    var shouldShowAllergens: Bool {
        upsell.isFoodUpsell && upsell.allergensUrls.isNotEmpty
    }

    var adults: Int {
        upsell.room?.adults.count ?? 0
    }

    var kids: Int {
        upsell.room?.numberOfChildren ?? 0
    }

    var maxFoodItems: Int {
        adults - addedFoodItems
    }

    mutating func updateItem(for id: String, quantity: Int) {
        guard let index = upsell.subitems.firstIndex(where: { $0.id == id}),
              var toUpdate = upsell.subitems[safe: index] else { return }
        toUpdate.quantity = quantity
        upsell.subitems[index] = toUpdate
    }
}

struct CiolUpsellDetailsInputParams {
    var upsell: CiolUpsellItemViewModelProtocol
    let nights: Int?
    let addedFoodItems: Int
    let addedKidsItems: Int
    let prebookedItems: [String]?
    let bookingReference: String?
    let analyticsParams: PIDictionary
}

protocol CiolUpsellDetailsViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func displayUpsellData(viewModel: CiolUpsellDetailsViewModelProtocol)
    func updateButton(state: CiolUpsellDetailsButton.ButtonState)
    func updateEnablement(models: [CiolUpsellSubitemViewModel])
}

protocol CiolUpsellDetailsInteractorProtocol {
    var customAnalyticsParameters: PIDictionary? { get }
    var ciolUpsellDetailsViewModel: CiolUpsellDetailsViewModelProtocol { get }
    var addButtonConfig: CiolUpsellDetailsButton.ButtonState { get }
    var outputDelegate: CiolUpsellDetailsOutputDelegate? { get set }
    var output: CiolUpsellDetailsOutput { get set }

    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> Void)
    func sendUpsellOutput(action: CiolUpsellDetailsOutputAction)
    func trackMenuScreenAnalytics()
    func trackAllergensScreenAnalytics()
}

protocol CiolUpsellDetailsRouterProtocol {
    func showMenuOrAllergyInfo(url: String)
}

protocol CiolUpsellDetailsViewEventHandler {
    func viewIsReady()
    func reloadViewModel()
    func showAllergyInfo()
    func showMenu()
    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> Void)
    func sendUpsellOutput(action: CiolUpsellDetailsOutputAction)
}

struct CiolUpsellSubitemViewModel {
    let title: String
    let bookingHasKids: Bool
    let canUpdateQuantity: Bool
    let id: String
    var quantity: Int
    var enabled: Bool
    var descriptions: [CiolUpsellItemDescription]
    var cost: Cost?
    let isFoodUpsell: Bool
    let isWifi: Bool
    var nights: Int

    init(
        title: String,
        bookingHasKids: Bool,
        canUpdateQuantity: Bool,
        id: String,
        quantity: Int = 0,
        enabled: Bool = true,
        descriptions: [CiolUpsellItemDescription],
        cost: Cost? = nil,
        isFoodUpsell: Bool,
        nights: Int,
        isWifi: Bool = false
    ) {
        self.title = title
        self.bookingHasKids = bookingHasKids
        self.canUpdateQuantity = canUpdateQuantity
        self.id = id
        self.quantity = quantity
        self.enabled = enabled
        self.descriptions = descriptions
        self.cost = cost
        self.isFoodUpsell = isFoodUpsell
        self.nights = nights
        self.isWifi = isWifi
    }
}

struct CiolUpsellDetailsTitleDescription {
    let title: String
    let subtitle: String
}

enum CiolUpsellItemDescription {
    case generic(String)
    case title(CiolUpsellDetailsTitleDescription)
    case kidsLabel(String)
}

enum CiolUpsellDetailsOutputAction {
    case add, remove
}
