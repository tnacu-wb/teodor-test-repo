//
//  AddNewCardPresenter.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 03/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

class AddNewCardPresenter {
    weak var view: AddNewCardViewProtocol?

    var router: AddNewCardRouterProtocol?
    var interactor: AddNewCardInteractorProtocol?
}

extension AddNewCardPresenter: AddNewCardViewEventHandler {
    func viewIsReady() {
        guard let viewModel = interactor?.addNewCardViewModel else { return }

        view?.loadViewModel(addNewCardViewModel: viewModel)
    }

    func selectedPaymentMethod(type: String) {
        interactor?.selected(paymentType: type)

        guard let viewModel = interactor?.addNewCardViewModel?.paymentMethodsViewModel else { return }

        view?.updatePaymentMethods(with: viewModel)
    }

    func toggledCNP(toggle: Bool) {
        interactor?.setCnpRequired(toggle: toggle)

        guard let viewModel = interactor?.addNewCardViewModel?.paymentMethodsViewModel else { return }

        view?.updatePaymentMethods(with: viewModel)
    }

    func addCardDidTap(values: PIDictionary, memorableWord: String?) {
        interactor?.initiateAddNewCard(values: values, memorableWord: memorableWord) { threeCiPageParams in
            self.view?.finishedLoading()

            guard let threeCiPageParams else {
                // track?

                self.view?.showError(
                    title: PILocalizedString("Something went wrong"),
                    message: PILocalizedString("initiateSaveCardGenericError")
                )

                return
            }

            self.router?.showAddCardWebView(cccpiPageParams: threeCiPageParams, threeCiPageDelegate: self)
        }
    }
}

extension AddNewCardPresenter: ThreeCiPageDelegate {
    func finishedAuth(cardType: String?) {
    }

    func startPolling(sender: ThreeCiPageViewController?, transactionID: String) {
        sender?.dismiss()
        router?.goBackToMyAccount()
    }
}
