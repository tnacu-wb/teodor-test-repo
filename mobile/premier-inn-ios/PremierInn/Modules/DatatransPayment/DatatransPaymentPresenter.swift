//
//  DatatransPaymentPresenter.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import Datatrans
import SimpleNetwork

class DatatransPaymentPresenter {
    weak var view: DatatransPaymentViewProtocol?
    var interactor: DatatransPaymentInteractorProtocol?
    var router: DatatransPaymentRouterProtocol?

    private var isPaymentInProgress = false
}

// MARK: - DatatransPaymentPresenterProtocol

extension DatatransPaymentPresenter: DatatransPaymentPresenterProtocol {
    func viewDidLoad() {
        guard !isPaymentInProgress else { return }
        isPaymentInProgress = true

        view?.showLoading()
        interactor?.initiatePaymentSession { [weak self] result in
            DispatchQueue.main.async {
                self?.handleSessionResult(result)
            }
        }
    }
}

// MARK: - Private

private extension DatatransPaymentPresenter {
    func handleSessionResult(
        _ result: Result<DatatransPaymentSessionResponse>
    ) {
        switch result {
        case .success(let session):
            guard let viewController = view as? UIViewController else { return }
            router?.presentSDK(
                transactionId: session.transactionId,
                from: viewController
            )

        case .failure(let error):
            view?.hideLoading()
            isPaymentInProgress = false
            if let flowError = error as? DatatransPaymentFlowError {
                router?.showErrorDialog(message: flowError.localizedMessage)
            } else {
                router?.showErrorDialog(message: DatatransPaymentFlowError.unknown.localizedMessage)
            }
        }
    }
}

// MARK: - TransactionDelegate

extension DatatransPaymentPresenter: TransactionDelegate {
    func transactionDidFinish(_ transaction: Transaction, result: TransactionSuccess) {
        view?.hideLoading()
        isPaymentInProgress = false
        router?.showSuccessDialog()
    }

    func transactionDidFail(_ transaction: Transaction, error: TransactionError) {
        view?.hideLoading()
        isPaymentInProgress = false
        router?.showErrorDialog(message: error.message ?? DatatransPaymentFlowError.unknown.localizedMessage)
    }

    func transactionDidCancel(_ transaction: Transaction) {
        view?.hideLoading()
        isPaymentInProgress = false
        router?.dismiss()
    }
}
