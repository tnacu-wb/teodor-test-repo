//
//  ReviewAndBookRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import Formeka

protocol ReviewAndBookRouterProtocol {
    func showEditUpsells(bookingDetails: BookingDetails)
    func showEditGuest(bookingDetails: BookingDetails)
    func showSummary(bookingDetails: BookingDetails)
    func showEditBusinessCardQuestions(questionsAndAnswers: [BusinessCardQuestionAndAnswer])
    func showBookingConfirmation(with: Stay)
    func popToRoot()
    func goBack()
    func showBartError()
    func goBackToSearchForAvailabilityController(hotel: Hotel?)
    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        walletAnalyticsDelegate: WalletAnalyticsDelegate,
        and webDelegate: WebViewControllerDelegate
    )
    func startDatatransPayment(basketId: String)
}

class ReviewAndBookRouter {
    private weak var viewController: UIViewController?

    static func build(with bookingDetails: BookingDetails) -> UIViewController {
        let controller = ReviewAndBookViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)
        controller.presenter = {
            let router = ReviewAndBookRouter()
            router.viewController = controller

            let interactor = ReviewAndBookInteractor(bookingDetails: bookingDetails)

            let presenter = ReviewAndBookPresenter()
            presenter.view = controller
            presenter.router = router
            presenter.interactor = interactor

            return presenter
        }()

        return controller
    }
}

extension ReviewAndBookRouter: ReviewAndBookRouterProtocol {
    func showEditUpsells(bookingDetails: BookingDetails) {
        let controller = UpsellsModule.build(with: bookingDetails, scope: .bookingFlowEditing, delegate: self)

        viewController?.present(UINavigationController(rootViewController: controller), animated: true)
    }

    func showEditGuest(bookingDetails: BookingDetails) {
        let controller = UserDetailsRouter.buildController(
            bookingDetails: bookingDetails,
            loggedUser: UserSessionManager.sharedInstance.currentUser,
            scope: .bookingFlowEditing,
            delegate: self
        )

        viewController?.present(UINavigationController(rootViewController: controller), animated: true)
    }

    func showSummary(bookingDetails: BookingDetails) {
        let controller = BookingSummaryRouter.build(hotel: nil, bookingDetails: bookingDetails, reservation: nil)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func showEditBusinessCardQuestions(questionsAndAnswers: [BusinessCardQuestionAndAnswer]) {
        let controller: FormBusinessCardQuestionsController = {
            BusinessCardQuestionsRouter.build(with: questionsAndAnswers, isModal: true)
        }()

        controller.delegate = self

        viewController?.present(UINavigationController(rootViewController: controller), animated: true)
    }

    func showBookingConfirmation(with stay: Stay) {
        let controller = BookingConfirmationModule.build(summary: stay, isBookingFlowEnd: true)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func goBack() {
        viewController?.navigationController?.popViewController(animated: true)
    }

    func popToRoot() {
        viewController?.navigationController?.popToRootViewController(animated: true)
    }

    func showBartError() {
        let controller = BARTDowntimeModal()
        controller.parentNavigationController = viewController?.navigationController

        viewController?.present(controller, animated: true)
    }

    func goBackToSearchForAvailabilityController(hotel: Hotel?) {
        let destinationController: UIViewController? = {
            if let controller = (viewController?.navigationController?.viewControllers
                .first { $0 is MapListContainerViewController }) as? MapListContainerViewController {
                controller.presenter?.sortDidChange(with: .distance)

                return controller
                // 😷🧤🛢 T0x1c 5TR4T5 Below 😷🧤🛢
            } else if let controller = (viewController?.navigationController?.viewControllers
                .first { $0 is HotelDetailsViewController }) as? HotelDetailsViewController,
                        let presenter = controller.eventHandler as? HotelDetailsPresenter {
                if let hotel = hotel {
                    presenter.interactor.updateSuggestion(to: PISuggestion(hotel: hotel))
                }

                return controller
            }
            // 😷🧤🛢 End of T0x1c 5TR4T5 😷🧤🛢

            return nil
        }()

        guard let controller = destinationController else {
            viewController?.navigationController?.popToRootViewController(animated: true)
            return
        }

        viewController?.navigationController?.popToViewController(controller, animated: true)
    }

    // Payment

    func processPayment(
        with cccpiPageParams: ThreeCiPageParams,
        using threeCiPageDelegate: ThreeCiPageDelegate,
        walletAnalyticsDelegate: WalletAnalyticsDelegate,
        and webDelegate: WebViewControllerDelegate
    ) {
        let parameters: ThreeCWebPageInitVariables = (
            cccpiPageParams.html,
            PIAnalytics.StateNames.pay3CiPage,
            PIAnalytics.StateTypes.bookingFlow,
            cccpiPageParams.allowedEvents,
            cccpiPageParams.trackingParams ?? [:],
            .general
        )
        let controller = ThreeCiPageViewController(parameters: parameters)
        controller.delegate = webDelegate
        controller.threeCiPageDelegate = threeCiPageDelegate
        controller.walletAnalyticsDelegate = walletAnalyticsDelegate
        let navigationController = UINavigationController(rootViewController: controller)
        navigationController.modalPresentationStyle = .fullScreen

        viewController?.present(navigationController, animated: true)
    }

    func startDatatransPayment(basketId: String) {
        let controller = DatatransPaymentModule.build(basketId: basketId, delegate: self)
        controller.modalPresentationStyle = .fullScreen
        viewController?.present(controller, animated: true)
    }
}

extension ReviewAndBookRouter: UserDetailsRouterDelegate {
    func goBackToUserDetailsScreen() {
        guard let viewController = self.viewController else { return }

        if let userDetailsVC = viewController.navigationController?.viewControllers
           .first(where: { $0 is UserDetailsViewController }) {
            viewController.navigationController?.popToViewController(userDetailsVC, animated: true)
            return
        }
        viewController.navigationController?.popToRootViewController(animated: true)
    }

    func showError(title: String, message: String, handler: @escaping (UIAlertAction) -> Void) {
        viewController?.showErrorAlertWith(title: title, message: message, error: nil, handler: handler)
    }

    func userDetailsWereUpdated() {
        // Here we don't need to react to user's data being saved
    }

    func userDetailsDidFinish(with bookingDetails: BookingDetails, output: FormStep1Output, sender: UIViewController) {
        sender.dismiss(animated: true)

        bookingDetails.booker = output.booker
        bookingDetails.purpose = output.purpose
        bookingDetails.criteria.addGuests(output.guests)

        NotificationCenter.default.post(name: .guestsDidChange, object: nil)
    }
}

extension ReviewAndBookRouter: UpsellsRouterDelegate {
    func goBackToUpsellsScreen() {
        guard let viewController = self.viewController else { return }

        if let upsellsVC = viewController.navigationController?.viewControllers
           .first(where: { $0 is UpsellsViewController }) {
            viewController.navigationController?.popToViewController(upsellsVC, animated: true)
            return
        }
        viewController.navigationController?.popToRootViewController(animated: true)
    }

    func showErrorMessage(title: String, message: String, handler: @escaping (UIAlertAction) -> Void) {
        viewController?.showErrorAlertWith(title: title, message: message, error: nil, handler: handler)
    }

    func upsellsDidFinish(sender: UIViewController) {
        sender.dismiss(animated: true)

        NotificationCenter.default.post(name: .guestsDidChange, object: nil)
    }

    func upsellsDidFinishAmend(sender: UIViewController) {
    }
}

extension ReviewAndBookRouter: BusinessCardQuestionsDelegate {
    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer]) {
        viewController?.dismiss(animated: true)

        BookingDetails.sharedInstance.businessCardQuestionsAndAnswers = questions

        NotificationCenter.default.post(name: .businessQuestionsDidChange, object: questions)
    }
}

extension ReviewAndBookRouter: PaymentMethodsRouterDelegate {
    func cardDelete() {
    }

    func selectedCard() {
        (viewController as? ReviewAndBookViewProtocol)?.loadViewModel(with: BookingDetails.sharedInstance)
    }
}

// MARK: - DatatransPaymentDelegate

extension ReviewAndBookRouter: DatatransPaymentDelegate {
    func paymentDidComplete() {
        // Placeholder: stay on review screen. Future: navigate to confirmation.
    }

    func paymentDidFail() {
        // Guest stays on review screen to retry
    }

    func paymentDidCancel() {
        // Guest stays on review screen
    }
}
