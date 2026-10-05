//
//  DatatransPaymentRouter.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit
import Datatrans

class DatatransPaymentRouter {
    weak var view: UIViewController?
    weak var delegate: DatatransPaymentDelegate?
}

// MARK: - DatatransPaymentRouterProtocol

extension DatatransPaymentRouter: DatatransPaymentRouterProtocol {
    func presentSDK(transactionId: String, from controller: UIViewController) {
        let transaction = Transaction(transactionId: transactionId)

        // Set callback URL for 3-D Secure bank app return
        transaction.options.appCallbackURL = "https://www.premierinn.com"

        // Set accent colour to PI brand
        transaction.theme.accentColor = UIColor.BasePurple

        // Presenter is the TransactionDelegate
        if let presenter = (controller as? DatatransPaymentView)?.presenter as? TransactionDelegate {
            transaction.delegate = presenter
        }

        transaction.start(presentingController: controller)
    }

    func showSuccessDialog() {
        let alert = UIAlertController(
            title: PILocalizedString("datatransPaymentSuccessTitle"),
            message: PILocalizedString("datatransPaymentSuccessMessage"),
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(
            title: PILocalizedString("OK"),
            style: .default
        ) { [weak self] _ in
            self?.delegate?.paymentDidComplete()
            self?.dismiss()
        })
        view?.present(alert, animated: true)
    }

    func showErrorDialog(message: String) {
        let alert = UIAlertController(
            title: PILocalizedString("datatransPaymentFailedTitle"),
            message: message,
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(
            title: PILocalizedString("OK"),
            style: .default
        ) { [weak self] _ in
            self?.delegate?.paymentDidFail()
            self?.dismiss()
        })
        view?.present(alert, animated: true)
    }

    func dismiss() {
        view?.dismiss(animated: true)
    }
}
