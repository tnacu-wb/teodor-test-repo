//
//  HotelDetailsRouter.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import MessageUI

class HotelDetailsRouter {
    var viewController: UIViewController?
    var presenter: HotelDetailsPresenter?

    private weak var delegate: HotelDetailsRouterDelegate?

    init(with delegate: HotelDetailsRouterDelegate? = nil) {
        self.delegate = delegate
    }

    func goBackToResultsScreenIfPossible() {
        guard let viewController = self.viewController else { return }

        // If there's a results list, pop to that
        if let resultsController = viewController.navigationController?.viewControllers
           .first(where: { $0 is MapListContainerViewController }) {
            // If it is iPad treat it as the cancel button being tapped
            if UIDevice.current.userInterfaceIdiom == .pad {
                delegate?.cancelButtonDidTap(sender: viewController)
                return
            }
            viewController.navigationController?.popToViewController(resultsController, animated: true)
            return
        }
        // Otherwise pop to the root
        viewController.navigationController?.popToRootViewController(animated: true)
    }

    func goBackToHomeScreen() {
        guard let viewController = self.viewController else { return }

        viewController.navigationController?.popToRootViewController(animated: true)
    }

    private func showNoEmailError() {
        let alertController = UIAlertController(
            title: PILocalizedString("Error"),
            message: PILocalizedString("Unfortunately we cannot open your email app right now"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(title: PILocalizedString("Close"), style: .cancel))

        viewController?.present(alertController, animated: true)
    }
}

extension HotelDetailsRouter: HotelDetailsRouterProtocol {
    func handleBARTDowntimeError(errorToCheck: Error) {
        BARTDowntimeHandler.handle(error: errorToCheck, withParentNavigationController: viewController?.navigationController)
    }

    func goBack(withCriteriaToCheck criteriaToCheck: Criteria) {
        if UIDevice.current.userInterfaceIdiom == .pad {
            delegate?.cancelButtonDidTap(sender: viewController)

            return
        }

        guard let viewController = viewController else { return }

        var controller = (viewController.navigationController?.viewControllers
            .last { $0 is CriteriaModifiable }) as? CriteriaModifiable

        if controller != nil, let currentCriteria = controller?.currentCriteria {
            if !(currentCriteria.arrivalDate == criteriaToCheck.arrivalDate && currentCriteria.nights == criteriaToCheck
                .nights) {
                controller?.updatedCriteria = criteriaToCheck
                viewController.navigationController?.popViewController(animated: true)
                return
            }
        }

        _ = viewController.navigationController?.popViewController(animated: true)

        if let resultsController = (viewController.navigationController?.viewControllers
            .first { $0 is MapListContainerViewController }) as? MapListContainerViewController {
            resultsController.presenter?.comingBackFromHotelDetails()
        }
    }

    func showCalendar(withArrivalDate arrivalDate: Date, andNumberOfNights numberOfNights: Int) {
        let departureDate = arrivalDate.dateByAddingUnit(unitType: .day, number: numberOfNights)

        let controller = AlternateCalendarViewController(arrivalDate: arrivalDate, departureDate: departureDate)
        controller.calendarDelegate = self

        let navController = UINavigationController(rootViewController: controller)
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .Tint1
        appearance.shadowColor = .clear
        navController.navigationBar.standardAppearance = appearance
        navController.navigationBar.scrollEdgeAppearance = appearance
        viewController?.present(navController, animated: true)
    }

    func showGuestsAndRooms() {
        let controller = RoomsGuestsCriteriaView()
        controller.eventHandler = self
        controller.presenter = {
            let interactor = RoomsGuestsCriteriaViewModel(criteria: BookingDetails.sharedInstance.criteria)

            let presenter = RoomsGuestsCriteriaPresenter(interactor: interactor)
            presenter.view = controller

            return presenter
        }()

        // present controller
        let navigationController = UINavigationController(rootViewController: controller)
        viewController?.present(navigationController, animated: true)
    }

    func selectedRate(
        withRooms rooms: [Room]?,
        accessibleRoomImages: [URL],
        twinRoomImages: [URL],
        withUpsells: Bool,
        andShouldShowRoomSelectionScreen shouldShowRoomSelectionScreen: Bool
    ) {
        let controllerToPresent = nextController(
            withRooms: rooms,
            accessibleRoomImages: accessibleRoomImages,
            twinRoomImages: twinRoomImages,
            withUpsells: withUpsells,
            andShouldShowRoomSelectionScreen: shouldShowRoomSelectionScreen
        )

        viewController?.navigationController?.pushViewController(controllerToPresent, animated: true)
    }

    func nextController(
        withRooms rooms: [Room]?,
        accessibleRoomImages: [URL],
        twinRoomImages: [URL],
        withUpsells: Bool,
        andShouldShowRoomSelectionScreen shouldShowRoomSelectionScreen: Bool
    ) -> UIViewController {
        let isBBFlow = BookingDetails.sharedInstance.bookingMode == .business

        switch (shouldShowRoomSelectionScreen, withUpsells, isBBFlow) {
        case (true, _, _):
            let productString = BookingDetails.sharedInstance.hotel?.roomSelectionProductString() ?? ""
            return BathrooomSelectionModule.buildModule(
                with: rooms ?? [],
                accessibleRoomImages: accessibleRoomImages,
                twinRoomImages: twinRoomImages,
                and: productString
            )
        case (_, true, _):
            return UpsellsModule.build(with: BookingDetails.sharedInstance)
        case (false, false, false):
            return UserDetailsRouter.buildController(
                bookingDetails: BookingDetails.sharedInstance,
                loggedUser: UserSessionManager.sharedInstance.currentUser,
                scope: .bookingFlow,
                delegate: nil
            )
        case (false, false, true):
            guard BookingDetails.sharedInstance.shouldShowEmployeeQuestionsForOpera
                else { return ReviewAndBookRouter.build(with: BookingDetails.sharedInstance) }
            guard let questions = UserSessionManager.sharedInstance.currentUser?.company?.businessCardQuestions,
                  questions.isNotEmpty else { return ReviewAndBookRouter.build(with: BookingDetails.sharedInstance) }
            let businessController = BusinessCardQuestionsRouter.build(with: questions, isModal: false)
            businessController.delegate = self
            return businessController
        default:
            return ReviewAndBookRouter.build(with: BookingDetails.sharedInstance)
        }
    }

    func showHoldRateError() {
        let controller = UIAlertController(
            title: PILocalizedString("holdBookingErrorAlertTitle", comment: "Hold booking error alert title"),
            message: PILocalizedString("holdBookingErrorAlertMessage", comment: "Hold booking error alert message"),
            preferredStyle: .alert
        )
        controller.addAction(UIAlertAction(
            title: PILocalizedString("holdBookingErrorAlertAction", comment: "Hold booking error alert action"),
            style: .cancel
        ) { _ in
            self.goBackToResultsScreenIfPossible()
        })

        viewController?.present(controller, animated: true)
    }

    func showDisabledAccess() {
        guard let url = Constants.disabledAccessUrl else { return }
        viewController?.openURLInSafari(url: url)
    }

    func showOurRooms(with hotel: Hotel, lettingType: String?) {
        let controller = RoomTypesModule.build(with: hotel, lettingType: lettingType)

        if UIDevice.current.userInterfaceIdiom == .pad {
            controller.modalPresentationStyle = .popover
            controller.popoverPresentationController?.sourceView = viewController?.view
            controller.popoverPresentationController?.sourceRect = viewController?.view.bounds ?? CGRect.zero

            let popoverWidth = viewController?.view.bounds.width ?? 375
            let popoverHeight = max(700, (viewController?.view.bounds.height ?? 900) - 360)
            controller.preferredContentSize = CGSize(width: popoverWidth, height: popoverHeight)
            viewController?.present(controller, animated: true)
        } else {
            viewController?.present(UINavigationController(rootViewController: controller), animated: true)
        }
    }

    func showBlockerAlert(withTitle title: String, body: String, andEmailSubject emailSubject: String?) {
        let alertController = UIAlertController(
            title: title,
            message: body,
            preferredStyle: .alert
        )
        if let emailSubject = emailSubject {
            alertController.addAction(UIAlertAction(
                title: PILocalizedString("EmailTravelManager"),
                style: .default,
                handler: { _ in
                guard MFMailComposeViewController.canSendMail() else {
                    return self.showNoEmailError()
                }

                let controller = MFMailComposeViewController()
                controller.navigationBar.tintColor = .BaseWhite
                controller.setSubject(emailSubject)
                controller.navigationController?.updateBarVisuals(withBottomBorder: true, theme: .light)
                self.viewController?.present(controller, animated: true)
            }
            ))
        }
        alertController.addAction(UIAlertAction(title: PILocalizedString("Close"), style: .cancel))

        viewController?.present(alertController, animated: true) {
            alertController.setActionAccessibilityIdentifiers(with: [
                (
                    PILocalizedString("EmailTravelManager"),
                    AccessibilityIdentifiers.HotelDetails.bbCardExpiredEmailTravelManagerAction
                ),
                (PILocalizedString("Close"), AccessibilityIdentifiers.HotelDetails.bbCardExpiredCloseAction)
            ])
        }
    }
}

extension HotelDetailsRouter: AlternateCalendarViewControllerDelegate {
    func calendarDidSelect(arrivalDate: Date, nights: Int) {
        presenter?.datesChanged(withNewArrivalDate: arrivalDate, andNumberOfNights: nights)
    }

    func calendarDidChange(arrivalDate: Date, nights: Int) {}

    func calendarDidInvalidate() {}
}

extension HotelDetailsRouter: RoomsGuestsCriteriaViewEventHandler {
    func criteriaController(_ sender: RoomsGuestsCriteriaView, didFinishWith criteria: Criteria) {
        presenter?.criteriaWasUpdated(to: criteria)
    }

    func criteriaControllerDidCancel(_ sender: RoomsGuestsCriteriaView) {
        presenter?.closeCurrentOverlay()
    }
}

extension HotelDetailsRouter: BusinessCardQuestionsDelegate {
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
