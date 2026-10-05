//
//  PaymentMethodsPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 23/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol PaymentMethodsViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func setTitle(_ title: String?)
    func loadViewModel(sections: [PaymentMethodsCardSection], ctaTitle: String?, infoFooterMessage: String?)
	func askForConfirmation(
	    title: String,
	    message: String,
	    cancelButtonTitle: String,
	    confirmButtonTitle: String,
	    completion: @escaping (Bool) -> Void
	)
	func dismiss()
    func handle(error: Error)
    func showCancelButton()
}

protocol PaymentMethodsInteractorProtocol {
    var paymentMethodsTracking: PaymentMethodsTracking { get }
    var sections: [PaymentMethodsCardSection] { get }
    var scope: PaymentMethodsScope { get }
    var ctaTitle: String? { get }
    var infoFooterMessage: String? { get }
    var customAnalyticsParameters: PIDictionary? { get }

	func delete(section: PaymentMethodsCardSection, completion: @escaping (Bool, Error?) -> Void)
	func cardForSection(section: PaymentMethodsCardSection) -> PaymentCard?
    func shouldCheckWithUserBeforeChanging(to cardAtIndex: Int) -> Bool
    func selectedCard(at index: Int)
}

class PaymentMethodsPresenter {
    weak var view: PaymentMethodsViewProtocol?
    var interactor: PaymentMethodsInteractorProtocol?
    var router: PaymentMethodsRouterProtocol?
}

extension PaymentMethodsPresenter: PaymentMethodsPresenterProtocol {
    var paymentMethodsTracking: PaymentMethodsTracking {
        interactor?.paymentMethodsTracking ?? ("", "")
    }

    func viewIsReady() {
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters

        view?.setTitle(interactor?.scope.navigationTitle)
        if interactor?.scope == .bookingFlow {
            view?.showCancelButton()
        }

        view?.loadViewModel(
            sections: interactor?.sections ?? [],
            ctaTitle: interactor?.ctaTitle,
            infoFooterMessage: interactor?.infoFooterMessage
        )
    }

	func linkCellDidSelect(with action: PaymentMethodsLinkAction, section: PaymentMethodsCardSection) {
		switch action {
		case .delete:
			view?.askForConfirmation(
			    title: PILocalizedString(
			        "paymentMethodsDeleteCardQuestionTitle",
			        comment: "Payment methods, delete card question title"
			    ),
			    message: PILocalizedString(
			        "paymentMethodsDeleteCardQuestionMessage",
			        comment: "Payment methods, delete card question message"
			    ),
			    cancelButtonTitle: PILocalizedString("Cancel", comment: "Title for alert cancel button"),
			    confirmButtonTitle: PILocalizedString(
			        "paymentMethodsDeleteButtonTitle",
			        comment: "Payment methods, delete button title"
			    )
			) { (confirmed) in
				if confirmed {
					self.interactor?.delete(section: section) { (success, error) in
                        DispatchQueue.main.async {
                            if success {
                                self.router?.deleteCard()
                            } else if let error = error {
                                self.view?.handle(error: error)
                            } else {
                                self.view?.dismiss()
                            }
                        }
					}
				}
			}

		case .edit:
			if let card = interactor?.cardForSection(section: section) {
				router?.edit(card: card)
			}
		}
	}

    func selectedPaymentCard(at index: Int) {
        func selectCard(at index: Int) {
            interactor?.selectedCard(at: index)
            view?.loadViewModel(
                sections: interactor?.sections ?? [],
                ctaTitle: interactor?.ctaTitle,
                infoFooterMessage: interactor?.infoFooterMessage
            )
            router?.selectedCard()
        }

        guard interactor?.shouldCheckWithUserBeforeChanging(to: index) == true else {
            selectCard(at: index)
            return
        }

        router?.confirmUserSelection(
            with: PILocalizedString("Do you want to continue?"),
            message: PILocalizedString(
                "Using your personal card to make this booking will remove your dinner and parking allowance"
            )
        ) {
            selectCard(at: index)
        }
    }

    func ctaDidTap() {
        guard let scope = interactor?.scope else { return }

        switch scope {
        case .myPI:
            router?.addNewCard()
        case .bookingFlow:
            break
        }
    }
}

extension PaymentMethodsPresenter: PaymentMethodsInteractorDelegate {
    func cardDelete() {
    }

	func cardDidUpdate() {
        view?.loadViewModel(
            sections: interactor?.sections ?? [],
            ctaTitle: interactor?.ctaTitle,
            infoFooterMessage: interactor?.infoFooterMessage
        )
	}
}
