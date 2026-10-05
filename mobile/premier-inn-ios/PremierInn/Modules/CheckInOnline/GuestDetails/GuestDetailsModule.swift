//
//  GuestDetailsModule.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 21.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

enum GuestDetailsModule {
    static func build(input: GuestDetailsInputBlueprint, prestayDelegate: PrestayDelegate) -> UIViewController {
        let viewController = GuestDetailsVC()

        let presenter = GuestDetailsPresenter()
        let interactor = GuestDetailsInteractor(input: input)
        interactor.prestayDelegate = prestayDelegate
        let router = GuestDetailsRouter()

        viewController.presenter = presenter

        presenter.view = viewController
        presenter.interactor = interactor
        presenter.router = router

        interactor.output = presenter
        router.viewController = viewController
        return viewController
    }
}

protocol GuestDetailsInputBlueprint {
    var room: Room { get set }
    var prestayInputParams: PreStayInputParams { get set }
    var upsellInput: CiolUpsellInputParams? { get set }
    var defaultAnalytics: PIDictionary { get }
}

protocol EditedGuestsModelBlueprint {
    var editedUsers: [User]? { get }
}

struct GuestDetailsInput: GuestDetailsInputBlueprint {
    var upsellInput: CiolUpsellInputParams?
    var room: Room
    var prestayInputParams: PreStayInputParams
    var defaultAnalytics: PIDictionary
}

struct EditedGuests: EditedGuestsModelBlueprint {
    var editedUsers: [User]?
}

protocol GuestDetailsInteractorBlueprint: EditDetailsViewDelegate, AuthorizationDelegate, CanCompleteRegCard {
    var output: GuestDetailsInteractorOutput? { get set }
    var guests: [Guest] { get set }
    var defaultAnalytics: PIDictionary { get }
    var priceVM: GuestDetailsInteractor.GuestDetailsPriceBreakdownViewModel { get }
    var prestayDelegate: PrestayDelegate? { get set }
    func getEditModel(for index: Int) -> EditDetailsInputParams?
    func validate()
    func handleContinueButtonTap()
    var paymentViewLayout: WebViewControllerLayout { get }
}

protocol GuestDetailsInteractorOutput: AnyObject, RegCardOutput {
    func reload()
    func goToUpsells(upsellInput: CiolUpsellInputParams)
    func goToPayment(with inputParams: CiolReviewAndPayInputParams)
    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails)
    func didFinish(error: CIOLError?)
    func didStart()
    func reloadBalance()
    func showCityTaxDisclaimer()
}

protocol GuestDetailsPresenterBlueprint {
    var customAnalyticsParameters: PIDictionary? { get }
    func viewIsReady()
    func edit(with index: Int)
    func handleContinueButtonTap()
}

protocol GuestDetailsRouterBlueprint {
    func showEdit(with input: EditDetailsInputParams, delegate: EditDetailsViewDelegate)
    func goToUpsell(ciolUpsellInputParams: CiolUpsellInputParams, prestayDelegate: PrestayDelegate)
    func goToPayment(inputParams: CiolReviewAndPayInputParams)
    func goToCompletion(ciolConfirmationDetails: CiolConfirmationDetails)
    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        and webDelegate: WebViewControllerDelegate,
        authorizationDelegate: AuthorizationDelegate,
        webviewLayout: WebViewControllerLayout
    )
}

protocol GuestDetailsViewBlueprint: AnyObject {
    var presenter: GuestDetailsPresenterBlueprint? { get set }
    func update(with: [Guest], priceVM: CIOLPriceBreakdownViewModelProtocol?)
    func showLoadingIndicator()
    func hideLoadingIndicator()
    func showError(title: String, message: String?, shouldDie: Bool)
    func updateBalance(priceVM: CIOLPriceBreakdownViewModelProtocol)
}
