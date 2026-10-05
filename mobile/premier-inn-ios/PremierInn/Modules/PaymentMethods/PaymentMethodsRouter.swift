//
//  PaymentMethodsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 23/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum PaymentMethodsScope {
    case bookingFlow
    case myPI

    var navigationTitle: String {
        switch self {
        case .bookingFlow:
            return PILocalizedString("Payment method")
        default:
            return PILocalizedString("Payment methods")
        }
    }
}

protocol PaymentMethodsRouterProtocol {
	func edit(card: PaymentCard)
    func selectedCard()
    func addNewCard()
    func confirmUserSelection(with title: String, message: String, completion: @escaping () -> Void)
    func deleteCard()
}

protocol PaymentMethodsRouterDelegate: AnyObject {
    func selectedCard()
    func cardDelete()
}

typealias PaymentMethodsBookingParameters = (isPrepaymentRequired: Bool, acceptedCardTypes: [CardType], departureDate: Date)

class PaymentMethodsRouter {
    var delegate: PaymentMethodsRouterDelegate?
    var addNewCardRouterDelegate: AddNewCardRouterDelegate?
	var viewController: UIViewController?

    static func buildController(
        with user: User,
        scope: PaymentMethodsScope = .myPI,
        bookingParams: PaymentMethodsBookingParameters? = nil,
        delegate: PaymentMethodsRouterDelegate? = nil,
        addNewCardRouterDelegate: AddNewCardRouterDelegate
    ) -> UIViewController {
        let controller = PaymentMethodsViewController()
        controller.presenter = {
            let presenter = PaymentMethodsPresenter()
            presenter.view = controller

            let interactor = PaymentMethodsInteractor(with: user, scope: scope, bookingParams: bookingParams)
            interactor.delegate = presenter

            presenter.interactor = interactor

            let router = PaymentMethodsRouter()
            router.viewController = controller
            router.delegate = delegate
            router.addNewCardRouterDelegate = addNewCardRouterDelegate

            presenter.router = router

            return presenter
        }()

        return controller
    }
}

extension PaymentMethodsRouter: PaymentMethodsRouterProtocol {
	func edit(card: PaymentCard) {
        let controller = AddNewCardModule.build(delegate: addNewCardRouterDelegate)
		viewController?.navigationController?.pushViewController(controller, animated: true)
	}

    func deleteCard() {
        delegate?.cardDelete()
        viewController?.navigationController?.popViewController(animated: true)
    }

    func selectedCard() {
        if let delegate = delegate {
            viewController?.dismiss(animated: true, completion: {
                delegate.selectedCard()
            })
        }
    }

    func addNewCard() {
        let controller = AddNewCardModule.build(delegate: addNewCardRouterDelegate)

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func confirmUserSelection(with title: String, message: String, completion: @escaping () -> Void) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel))
        alertController.addAction(UIAlertAction(title: PILocalizedString("Continue"), style: .default, handler: { _ in
            completion()
        }))

        viewController?.present(alertController, animated: true)
    }
}
