//
//  UpsellsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class UpsellsRouter {
    weak var viewController: UIViewController?
    weak var delegate: UpsellsRouterDelegate?
}

extension UpsellsRouter: UpsellsRouterProtocol {
    func goBackToUpsellsScreen() {
        guard let viewController = self.viewController else { return }

        if let upsellsVC = viewController.navigationController?.viewControllers
           .first(where: { $0 is UpsellsViewController }) {
            viewController.navigationController?.popToViewController(upsellsVC, animated: true)
            return
        }
        viewController.navigationController?.popToRootViewController(animated: true)
    }

    func showSummary(with bookingDetails: BookingDetails) {
        let controller = BookingSummaryRouter.build(hotel: nil, bookingDetails: bookingDetails, reservation: nil)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func continueToNextStep(with bookingDetails: BookingDetails) {
        if delegate != nil, let viewController = viewController {
            delegate?.upsellsDidFinish(sender: viewController)
            return
        }

        let controllerToPresent = nextViewController(with: bookingDetails)

        viewController?.navigationController?.pushViewController(controllerToPresent, animated: true)
    }

    func nextViewController(with bookingDetails: BookingDetails) -> UIViewController {
        let isBBFlow = bookingDetails.bookingMode == .business

        if isBBFlow {
            guard bookingDetails.shouldShowEmployeeQuestionsForOpera
                else { return ReviewAndBookRouter.build(with: BookingDetails.sharedInstance) }

            guard let questions = UserSessionManager.sharedInstance.currentUser?.company?.businessCardQuestions,
                  questions.isNotEmpty else {
                return ReviewAndBookRouter.build(with: BookingDetails.sharedInstance)
            }
            let controller = BusinessCardQuestionsRouter.build(with: questions, isModal: false)
            controller.delegate = self
            return controller
        } else {
            return UserDetailsRouter.buildController(
                bookingDetails: bookingDetails,
                loggedUser: UserSessionManager.sharedInstance.currentUser,
                scope: .bookingFlow,
                delegate: nil
            )
        }
    }

    func continueToNextAmendStep() {
        delegate?.upsellsDidFinishAmend(sender: viewController!)
    }
}

extension UpsellsRouter: BusinessCardQuestionsDelegate {
    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer]) {
        BookingDetails.sharedInstance.businessCardQuestionsAndAnswers = questions

        let controller: UIViewController = {
            if BookingDetails.sharedInstance.bookingMode == .business {
                return ReviewAndBookRouter.build(with: BookingDetails.sharedInstance)
            }
            return UserDetailsRouter.buildController(
                bookingDetails: BookingDetails.sharedInstance,
                loggedUser: UserSessionManager.sharedInstance.currentUser,
                scope: .bookingFlow,
                delegate: nil
            )
        }()

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }
}
