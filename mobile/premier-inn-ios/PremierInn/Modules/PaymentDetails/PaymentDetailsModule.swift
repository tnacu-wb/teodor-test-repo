//
//  PaymentDetailsModule.swift
//  PremierInn
//
//  Created Freddie Parks on 07/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//
//

import UIKit
import SimpleNetwork
import Formeka

enum PaymentDetailsScope {
    case ciol
}

enum PaymentDetailsModule {
    static func build(
        with scope: PaymentDetailsScope = .ciol,
        using paymentCard: PaymentCard,
        for totalCost: Cost,
        reservation: Reservation? = nil,
        sessionId: String? = nil,
        cancelableText: String? = nil,
        completion: @escaping (PaymentDetailsPresenter?) -> Void
    ) -> UIViewController {
        let controller = PaymentDetailsView(nibName: String(describing: FormekaViewController.self), bundle: nil)

        controller.eventHandler = {
            let presenter = PaymentDetailsPresenter()

            let router = PaymentDetailsRouter()
            router.view = controller

            let ciolParams: CIOLPaymentParams? = {
                guard scope == .ciol else { return nil }
                guard let confirmationNumber = reservation?.confirmationNumber,
                      let sessionId = sessionId ?? reservation?.sessionId,
                      let email = reservation?.booker?.emailAddress else { return nil }

                return (sessionId, confirmationNumber, email, reservation, reservation?.changeCard ?? false)
            }()

            let interactor = PaymentDetailsInteractor(
                with: scope,
                using: paymentCard,
                for: totalCost,
                ciolPaymentParams: ciolParams,
                cancelableText: cancelableText
            )
            interactor.delegate = presenter

            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            completion(presenter)

            return presenter
        }()

        return controller
    }
}

protocol PaymentDetailsViewProtocol: AnyObject {
    func update(with viewModel: PaymentDetailsViewModel)
    func toggleLoadingIndicator(should show: Bool)
    func showError(title: String, message: String?)
}

protocol PaymentDetailsViewEventHandler {
    func viewIsReady()
    func payAndCheckInDidTap(with cvv: String?)
}

protocol PaymentDetailsInteractorProtocol {
    var viewModel: PaymentDetailsViewModel? { get }
    var ciolParams: CIOLPaymentParams? { get }
    var totalCost: Cost { get }
    var cancelableText: String? { get }

    func makePayment(using cvv: String?, completion: @escaping (_ checkedIn: Bool, _ threeDSRequest: URLRequest?) -> Void)
    func completePayment(
        with pares: String,
        completion: @escaping (_ paymentSuccessful: Bool, _ errorMessage: String?) -> Void
    )
}

protocol PaymentDetailsInteractorDelegate: AnyObject {
    func paymentFailed(with errorMessage: String)
}

struct PaymentDetailsReservationInfo {
    let reservation: Reservation?
    let sessionId: String?
    let cancelableText: String?
    let acceptedCreditCards: [CardType]?
}

protocol PaymentDetailsRouterProtocol {
    func goBack(checkedIn: Bool, alsoPaid: Bool)
}

protocol CheckInOnlinePaymentAnalyticsDelegate: AnyObject {
    func trackCheckInConfirmation(success: Bool, params: CIOLPaymentAnalyticsParams)
}
