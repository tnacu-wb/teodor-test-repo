//
//  AccountModule.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

enum AccountModule {
    static func build() -> UIViewController {
        let controller = AccountViewController()
        controller.eventHandler = {
            let interactor = AccountInteractor()

            let router = AccountRouter()
            router.viewController = controller

            let presenter = AccountPresenter()
            presenter.interactor = interactor
            presenter.view = controller
            presenter.router = router

            return presenter
        }()

        return controller
    }
}

protocol CustomAccountLinkViewModel {
    var title: String { get }
    var url: URL { get }
}

protocol AccountViewModel {
    var username: String? { get }
    var email: String? { get }
    var business: (isBusiness: Bool, companyName: String?) { get }
    var userLoggedIn: Bool { get }
    var customLinks: [CustomAccountLinkViewModel] { get }
    var shouldShowPaymentMethods: Bool { get }
}

protocol AccountViewProtocol: AnyObject {
    func reloadData(with accountViewModel: AccountViewModel)
    func scrollToTop()
    func showSuccessConfirmation(withMessage message: String)
}

protocol AccountPresenterProtocol: UserDetailsRouterDelegate, ChangePasswordCompletionInput, RegisterCompletionInput,
    PaymentMethodsRouterDelegate, CardDetailsRouterDelegate {}

protocol AccountRouterProtocol {
    func openMyDetails(withCompletionDelegate: UserDetailsRouterDelegate?, deleteAccountDelegate: DeleteAccountDelegate?)
    func openPaymentMethods(
        paymentMethodDelegate: PaymentMethodsRouterDelegate,
        cardDetailsDelegate: CardDetailsRouterDelegate,
        addNewCardRouterDelegate: AddNewCardRouterDelegate
    )
    func openChangePassword(withCompletionDelegate completionDelegate: ChangePasswordCompletionInput?)
    func openBookingPreferences()
    func openNewsletterPreferences(with emailAddress: String)
    func openLogin()
    func openRegister(withCompletionDelegate completionDelegate: RegisterCompletionInput?)
}

protocol AccountInteractorProtocol {
    var accountViewModel: AccountViewModel? { get }
    var accountCreatedMessage: String { get }
    var detailsChangedMessage: String { get }
    var deletedCardMessage: String { get }
    var savedCardMessage: String { get }
    var updatedCardMessage: String { get }
    var passwordChangedMessage: String { get }

    func updateUser(shouldAttemptLogin: Bool, completion: @escaping () -> Void)
    func userLoggedOut()
}
