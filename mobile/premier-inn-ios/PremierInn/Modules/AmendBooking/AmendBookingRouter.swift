//
//  AmendBookingRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol AmendBookingRouterProtocol {
    func showCalendar(with stayDetails: AmendStayDatesDetails)
    func editUpsells(isForced: Bool)
    func addRoom(
        asRoomNumber roomNumber: Int,
        availabilityRequirements: AddRoomAvailabilityRequirements,
        amendOperaDetails: AmendOperaDetails?
    )
    func editRoom(
        _ number: Int,
        isOnlyRoom: Bool,
        amendRoomRestrictions: AmendRoomRestrictions,
        withExistingRoomValues roomValues: Room,
        andAvailabilityRequirements availabilityRequirements: AddRoomAvailabilityRequirements,
        amendOperaDetails: AmendOperaDetails?
    )
    func showReview(amendReviewModel: AmendReservationReviewModel)
    func goBackToResultsScreenIfPossible()
    func popController(animated: Bool)
}

protocol AmendBookingRouterDelegate: AnyObject {
    func bookingWasAmended()
}

class AmendBookingRouter {
    // MARK: - Properties

    private weak var delegate: AmendBookingRouterDelegate?
    private weak var viewController: UIViewController?
    /// modal ViewController that shows Upsells
    var addViewController: UINavigationController?

    private var interactor: AmendBookingInteractorProtocol?

    // MARK: - Lifecycle

    deinit {
        print("DEINIT: \(self)")
    }

    static func build(
        with stay: Stay,
        hotel: Hotel? = nil,
        and amendRouterDelegate: AmendBookingRouterDelegate?
    ) -> UIViewController {
        let controller = AmendBookingView(nibName: "BookingManagementViewController", bundle: nil)
        controller.eventHandler = {
            let presenter = AmendBookingPresenter()

            let interactor = AmendBookingInteractor(stay: stay, hotel: hotel, delegate: presenter)

            let router = AmendBookingRouter()
            router.delegate = amendRouterDelegate
            router.viewController = controller
            router.interactor = interactor

            presenter.router = router
            presenter.view = controller
            presenter.interactor = interactor

            return presenter
        }()

        return controller
    }
}

extension AmendBookingRouter: AmendBookingRouterProtocol {
    func addRoom(
        asRoomNumber roomNumber: Int,
        availabilityRequirements: AddRoomAvailabilityRequirements,
        amendOperaDetails: AmendOperaDetails?
    ) {
        interactor?.trackState(of: PIAnalytics.StateNames.amendAddRoom)

        let controller = AddRoomRouter.build(
            routerDelegate: self,
            asNewRoom: true,
            existingRoom: nil,
            roomNumber: roomNumber,
            availabilityRequirements: availabilityRequirements,
            amendOperaDetails: amendOperaDetails
        )
        controller.eventHandler = self

        addViewController = UINavigationController(rootViewController: controller)
        if let addViewController = self.addViewController {
            viewController?.present(addViewController, animated: true)
        }
    }

    func editRoom(
        _ roomNumber: Int,
        isOnlyRoom: Bool,
        amendRoomRestrictions: AmendRoomRestrictions,
        withExistingRoomValues roomValues: Room,
        andAvailabilityRequirements availabilityRequirements: AddRoomAvailabilityRequirements,
        amendOperaDetails: AmendOperaDetails?
    ) {
        interactor?.trackState(of: PIAnalytics.StateNames.amendRoom)

        let controller = AddRoomRouter.build(
            routerDelegate: self,
            asNewRoom: false,
            existingRoom: roomValues,
            isOnlyRoom: isOnlyRoom,
            isAmendableRoom: amendRoomRestrictions.isAmendableRoom,
            isCancellableRoom: amendRoomRestrictions.isCancellableRoom,
            canAmendGuests: amendRoomRestrictions.canAmendGuests,
            roomNumber: roomNumber,
            availabilityRequirements: availabilityRequirements,
            amendOperaDetails: amendOperaDetails
        )
        controller.eventHandler = self

        addViewController = UINavigationController(rootViewController: controller)
        if let addViewController = self.addViewController {
            viewController?.present(addViewController, animated: true)
        }
    }

    func editUpsells(isForced: Bool) {
        guard let reservation = interactor?.amendReservationModel?.reservation else { return }
        guard let rooms = interactor?.amendReservationModel?.criteria.rooms else { return }
        guard let hotel = interactor?.hotel else { return }
        guard let rate = interactor?.existingReservation?.rate else { return }
        let upsellItems = reservation.upsellItems

        let bookingDetails = BookingDetails()
        bookingDetails.rate = rate
        bookingDetails.roomLettings = rooms
        bookingDetails.criteria = interactor?.amendReservationModel?.criteria ?? Criteria()
        bookingDetails.criteria.rooms = rooms
        // we don't have this anymore so we won't be able to show Upsells allowances
//        bookingDetails.paymentMethod = PaymentMethod(type: .stored, method: reservation.payment)

        let amendRoomsModel = AmendRoomsModel(
            reservation: reservation,
            existingUpsellItems: upsellItems,
            rooms: rooms,
            hotel: hotel,
            bookingDetails: bookingDetails,
            rate: rate,
            isForcedUpsellChange: isForced
        )

        let controller = UpsellsModule.build(
            for: amendRoomsModel,
            delegate: self,
            amendOperaDetails: interactor?.amendOperaDetails
        )
        viewController?.present(UINavigationController(rootViewController: controller), animated: true)
    }

    func popController(animated: Bool) {
        viewController?.navigationController?.popViewController(animated: animated)
    }

    func showCalendar(with stayDetails: AmendStayDatesDetails) {
        let controller = AmendDatesRouter.buildController(with: stayDetails, and: self)

        let navController = UINavigationController(rootViewController: controller)
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .Tint1
        appearance.shadowColor = .clear
        navController.navigationBar.standardAppearance = appearance
        navController.navigationBar.scrollEdgeAppearance = appearance

        interactor?.trackState(of: PIAnalytics.StateNames.amendCalendar)

        viewController?.present(navController, animated: true)
    }

    func showReview(amendReviewModel: AmendReservationReviewModel) {
        let controller = AmendReviewRouter.buildController(with: amendReviewModel, and: self)

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func goBackToResultsScreenIfPossible() {
        guard let viewController = self.viewController else { return }

        // If theres booking details(always should be) go back to that if not pop to root as a fail safe.
        if let resultsController = viewController.navigationController?.viewControllers
           .first(where: { $0 is BookingConfirmationViewController }) {
            viewController.navigationController?.popToViewController(resultsController, animated: true)
            return
        }

        // Otherwise pop to the root
        viewController.navigationController?.popToRootViewController(animated: true)
    }
}

extension AmendBookingRouter: AmendReviewRouterDelegate {
    func amendCompleted() {
        viewController?.navigationController?.popViewController(animated: true)
        delegate?.bookingWasAmended()
    }

    func amendFailed() {
        viewController?.navigationController?.popViewController(animated: true)
    }
}

// MARK: - Amend Dates Delegate
extension AmendBookingRouter: AmendDatesRouterDelegate {
    func finishedAmend() {
        // go back and refresh the temporary basket
        interactor?.refreshTemporaryBasket(
            hasAdultsDecreased: false,
            isNewRoomAdded: false
        )
        viewController?.dismiss(animated: true)
    }
}

// MARK: - Amend Room Delegate
extension AmendBookingRouter: AmendRoomRouterDelegate {
	func finishedEditingRoom(hasAdultsDecreased: Bool, isNewRoomAdded: Bool) {
        // go back and refresh the temporary basket
        interactor?.refreshTemporaryBasket(
            hasAdultsDecreased: hasAdultsDecreased,
            isNewRoomAdded: isNewRoomAdded
        )
        addViewController?.dismiss(animated: false)
    }
}

// MARK: - Add Room Delegate
extension AmendBookingRouter: AddRoomViewEventHandler {
    func addRoomControllerDidCancel(_ sender: AddRoomView) {
        viewController?.dismiss(animated: true)
    }

    func addRoomController(_ sender: AddRoomView, didFinishWith criteria: Criteria) {
    }
}

// MARK: - Upsells Delegate
extension AmendBookingRouter: UpsellsRouterDelegate {
    func upsellsDidFinish(sender: UIViewController) {
        sender.dismiss(animated: true) {
            self.addViewController?.dismiss(animated: false)
        }
    }

    func upsellsDidFinishAmend(sender: UIViewController) {
        sender.dismiss(animated: true) {
            self.interactor?.refreshTemporaryBasket(
                hasAdultsDecreased: false,
                isNewRoomAdded: false
            )
            self.addViewController?.dismiss(animated: false)
        }
    }
}
