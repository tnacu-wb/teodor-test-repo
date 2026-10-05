//
//  PaymentDetailsPresenter.swift
//  PremierInn
//
//
//  Created Freddie Parks on 07/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//
//

import Foundation

class PaymentDetailsPresenter {
    weak var view: PaymentDetailsViewProtocol?
    weak var delegate: CheckInOnlinePaymentAnalyticsDelegate?

    var interactor: PaymentDetailsInteractorProtocol?
    var router: PaymentDetailsRouterProtocol?
}

extension PaymentDetailsPresenter: PaymentDetailsViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.update(with: viewModel)
    }

    func payAndCheckInDidTap(with cvv: String?) {
        view?.toggleLoadingIndicator(should: true)

        interactor?.makePayment(using: cvv) { completed, _ in
            if completed {
                self.router?.goBack(checkedIn: true, alsoPaid: true)
                self.delegate?.trackCheckInConfirmation(
                    success: true,
                    params: CIOLPaymentAnalyticsParams(
                        prepaid: true,
                        cardType: self.interactor?.viewModel?.cardViewModel.cardType ?? "",
                        total: self.interactor?.viewModel?.confirmationViewModel.total ?? ""
                    )
                )

                return
            }
        }
    }
}

extension PaymentDetailsPresenter: PaymentDetailsInteractorDelegate {
    func paymentCompleted() {
        DispatchQueue.main.async {
            self.router?.goBack(checkedIn: true, alsoPaid: true)
            self.delegate?.trackCheckInConfirmation(
                success: true,
                params: CIOLPaymentAnalyticsParams(
                    prepaid: true,
                    cardType: self.interactor?.viewModel?.cardViewModel.cardType ?? "",
                    total: self.interactor?.viewModel?.confirmationViewModel.total ?? ""
                )
            )
        }
    }

    func paymentFailed(with errorMessage: String) {
        DispatchQueue.main.async {
            self.view?.toggleLoadingIndicator(should: false)
            self.view?.showError(title: PILocalizedString("Something went wrong"), message: errorMessage)
        }
    }
}

extension PaymentDetailsPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true, completion: {
            self.view?.toggleLoadingIndicator(should: false)
        })
    }
}
