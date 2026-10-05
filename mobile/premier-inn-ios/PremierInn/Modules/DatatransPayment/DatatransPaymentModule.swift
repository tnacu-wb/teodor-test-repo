//
//  DatatransPaymentModule.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

// MARK: - Protocols

protocol DatatransPaymentViewProtocol: AnyObject {
    func showLoading()
    func hideLoading()
}

protocol DatatransPaymentPresenterProtocol {
    func viewDidLoad()
}

protocol DatatransPaymentInteractorProtocol {
    func initiatePaymentSession(
        completion: @escaping (Result<DatatransPaymentSessionResponse>) -> Void
    )
}

protocol DatatransPaymentRouterProtocol {
    func presentSDK(transactionId: String, from controller: UIViewController)
    func showSuccessDialog()
    func showErrorDialog(message: String)
    func dismiss()
}

protocol DatatransPaymentDelegate: AnyObject {
    func paymentDidComplete()
    func paymentDidFail()
    func paymentDidCancel()
}

// MARK: - Data Provider

protocol DatatransPaymentDataProvider {
    func initMobileSDKPayment(
        basketId: String,
        completion: @escaping (_ response: DatatransPaymentSessionResponse?, _ error: Error?) -> Void
    )
}

extension RequestsManager: DatatransPaymentDataProvider {}

// MARK: - Module Factory

enum DatatransPaymentModule {
    static func build(basketId: String, delegate: DatatransPaymentDelegate?) -> UIViewController {
        let controller = DatatransPaymentView()

        let router = DatatransPaymentRouter()
        router.view = controller
        router.delegate = delegate

        let dataProvider: DatatransPaymentDataProvider = RequestsManager()

        let interactor = DatatransPaymentInteractor(basketId: basketId, dataProvider: dataProvider)

        let presenter = DatatransPaymentPresenter()
        presenter.view = controller
        presenter.interactor = interactor
        presenter.router = router

        controller.presenter = presenter

        return controller
    }
}
