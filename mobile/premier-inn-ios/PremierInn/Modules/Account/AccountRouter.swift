//
//  AccountRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class AccountRouter {
    var viewController: UIViewController?
}

extension AccountRouter: AccountRouterProtocol {
    func openPaymentMethods(
        paymentMethodDelegate: PaymentMethodsRouterDelegate,
        cardDetailsDelegate: CardDetailsRouterDelegate,
        addNewCardRouterDelegate: AddNewCardRouterDelegate
    ) {
        let controller: UIViewController = {
            if let user = UserSessionManager.sharedInstance.currentUser,
               user.paymentPreference?.card != nil
               || user.centrallyStoredBusinessCard != nil {
                return PaymentMethodsRouter.buildController(
                    with: user,
                    delegate: paymentMethodDelegate,
                    addNewCardRouterDelegate: addNewCardRouterDelegate
                )
            }

            return AddNewCardModule.build(delegate: addNewCardRouterDelegate)
        }()

        controller.hidesBottomBarWhenPushed = true
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func openRegister(withCompletionDelegate completionDelegate: RegisterCompletionInput?) {
        guard let viewController = viewController else { return }

        RegisterRouter().presentRegisterInterface(from: viewController, with: completionDelegate)
    }

    func openLogin() {
        guard let viewController = viewController else { return }
        let userBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)

        LoginRouter().presentLoginInterface(
            from: viewController,
            asBusinessLogin: userBusiness,
            comingFromSplashScreen: false,
            andIsFromBookingFlow: false
        )
    }

    func openBookingPreferences() {
        let controller = UserPreferencesRouter.buildController()
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func openNewsletterPreferences(with emailAddress: String) {
        let controller = NewsletterModule.build(with: emailAddress)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func openChangePassword(withCompletionDelegate completionDelegate: ChangePasswordCompletionInput?) {
        let userBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)

        let controller = ChangePasswordRouter.buildController(with: completionDelegate, asBusinessLogin: userBusiness)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func openMyDetails(
        withCompletionDelegate completionDelegate: UserDetailsRouterDelegate?,
        deleteAccountDelegate: DeleteAccountDelegate? = nil
    ) {
        let controller = UserDetailsRouter.buildController(
            bookingDetails: nil,
            loggedUser: UserSessionManager.sharedInstance.currentUser,
            scope: .userPreferences,
            delegate: completionDelegate,
            deleteAccountDelegate: deleteAccountDelegate
        )
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }
}
