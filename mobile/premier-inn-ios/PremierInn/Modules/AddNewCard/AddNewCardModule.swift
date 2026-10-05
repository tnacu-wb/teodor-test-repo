//
//  AddNewCardModule.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 03/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import SimpleNetwork
import UIKit

enum AddNewCardModule {
    static func build(delegate: AddNewCardRouterDelegate?) -> UIViewController {
        let controller = AddNewCardView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.eventHandler = {
            let interactor = AddNewCardInteractor()

            let router = AddNewCardRouter()
            router.view = controller
            router.delegate = delegate

            let presenter = AddNewCardPresenter()
            presenter.interactor = interactor
            presenter.view = controller
            presenter.router = router

            return presenter
        }()

        return controller
    }
}

protocol AddNewCardViewProtocol: AnyObject {
    func loadViewModel(addNewCardViewModel: AddNewCardViewModel)
    func updatePaymentMethods(with viewModel: AddNewCardPaymentMethodsViewModel)
    func finishedLoading()
    func showError(title: String, message: String)
}

protocol AddNewCardViewEventHandler {
    func viewIsReady()
    func selectedPaymentMethod(type: String)
    func toggledCNP(toggle: Bool)
    func addCardDidTap(values: PIDictionary, memorableWord: String?)
}

protocol AddNewCardRouterDelegate: AnyObject {
    func cardUpdated()
}

protocol AddNewCardRouterProtocol {
    func showAddCardWebView(cccpiPageParams: ThreeCiPageParams, threeCiPageDelegate: ThreeCiPageDelegate)
    func goBackToMyAccount()
}

protocol AddNewCardInteractorProtocol {
    var addNewCardViewModel: AddNewCardViewModel? { get }

    func selected(paymentType: String)
    func setCnpRequired(toggle: Bool)
    func initiateAddNewCard(values: PIDictionary, memorableWord: String?, completion: @escaping (ThreeCiPageParams?) -> Void)
}

// ViewModel protocols

protocol AddNewCardViewModel {
    var billingAddressViewModel: AddNewCardBillingAddressViewModel { get }
    var paymentMethodsViewModel: AddNewCardPaymentMethodsViewModel? { get }
}

protocol AddNewCardBillingAddressViewModel {
    var addressRequirements: AddressSectionRequirements { get }
}

protocol AddNewCardPaymentMethodsViewModel {
    var title: String { get }
    var paymentMethods: [AddNewCardPaymentMethodViewModel] { get }
    var shouldShowCNP: Bool { get }
    var cnpEnabled: Bool { get }
}

protocol AddNewCardPaymentMethodViewModel {
    var name: String { get }
    var type: CCCPPaymentType { get }
    var imageUrls: [URL]? { get }
    var isCNPAvailable: Bool { get }
    var selected: Bool { get }
}
