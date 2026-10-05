//
//  HomeRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import CoreLocation
import SimpleCalendar
import MessageUI
import SwiftUI

protocol HomeRouterInput: AnyObject {
	func selectedLocation(sender: UIView)
	func selectedNights(sender: UIView)
	func selectedGuests(sender: UIView)
	// func search(with suggestion: Suggestion, and criteria: Criteria, completion: @escaping (UIViewController?) -> Void)
    func presentResultController(_ controller: UIViewController)
	func selectedLocation()
	func selectedNights()
    func showCalendarAndHotel(with dashboardHotelDetails: DashboardHotelDetails)
	func selectedGuests()
	func showGDPRInfo()
	func searchNearMe()
    func resetNavigation()
    func showHotelDetails(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        availabilityResponse: HotelAvailabilityResponse,
        and suggestion: Suggestion?
    )
    func showDatelessHotelDetails(dashboardHotelDetails: DashboardHotelDetails)
    func showDatelessHotelDetailsDeepLink(hotelCode: String, hotelBrand: HotelBrand)
    func showHome()
    func showBookingDetails(identifier: String)
    func showBBSplash()
    func showBBCentralCardExpired(with message: String, completion: @escaping () -> Void?)
    func showAnnouncementMessage(with message: NotificationsMessage)
    func showAmendBooking(identifier: String)
    func showFindReservation(arrivalDate: String, reservationNumber: String, lastName: String?)
    func showPromotionPopover()
    func startCheckInOnline(preStayInputParams: PreStayInputParams, completion: @escaping () -> Void?)
}

class HomeRouter: NSObject {
	var view: UIViewController?
	var presenter: HomePresenter?

	private let locationManager = LocationManager()

    static func build() -> UIViewController {
        let router = HomeRouter()
        let presenter = HomePresenter()
        let interactor = HomeInteractor()
        router.presenter = presenter

        let controller = HomeView(nibName: String(describing: HomeView.self), bundle: nil)
        controller.eventHandler = presenter

        router.view = controller

        interactor.dataProvider = RequestsManager()

        presenter.interactor = interactor
        presenter.view = controller
        presenter.router = router

        return controller
    }

    static func build(with controller: HomeView) {
        let router = HomeRouter()
        let presenter = HomePresenter()

        router.presenter = presenter

        controller.eventHandler = presenter

        router.view = controller.navigationController

        presenter.interactor = HomeInteractor()
        presenter.view = controller
        presenter.router = router
    }

    private func showSuggestions() {
        let controller = SuggestionsRouter.build()
        controller.delegate = self

        view?.present(UINavigationController(rootViewController: controller), animated: true)
    }

    private func showGuestsRooms() {
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
        view?.present(navigationController, animated: true, completion: nil)
    }

    private func showCalendar() {
        let arrivalDate = BookingDetails.sharedInstance.criteria.arrivalDate
        let departureDate = arrivalDate.dateByAddingUnit(
            unitType: .day,
            number: BookingDetails.sharedInstance.criteria.nights
        )

        let controller = AlternateCalendarViewController(arrivalDate: arrivalDate, departureDate: departureDate)
        controller.calendarDelegate = self

        let navController = UINavigationController(rootViewController: controller)
        navController.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navController.navigationBar.shadowImage = UIImage()
        navController.navigationBar.tintColor = .BaseWhite
        navController.navigationBar.barTintColor = .Tint1

        view?.present(navController, animated: true)
    }

	private var navigationController: UINavigationController? {
        (view as? UINavigationController) ?? view?.navigationController
	}

    override init() {
        super.init()

        if UserDefaults.standard.bool(forKey: Constants.hasAcceptedGDPRChanges) == true {
            DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(3)) {
                self.showBBSplash()
            }
        }
    }

    private func showNoEmailError() {
        let alertController = UIAlertController(
            title: PILocalizedString("Error"),
            message: PILocalizedString("Unfortunately we cannot open your email app right now"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(title: PILocalizedString("Close"), style: .cancel))

        view?.present(alertController, animated: true)
    }
}

extension HomeRouter: HomeRouterInput {
    func startCheckInOnline(preStayInputParams: PreStayInputParams, completion: @escaping () -> Void?) {
        guard let navigationController else {
            completion()
            return
        }

        let preStayViewController = PreStayModule.build(preStayInputParams: preStayInputParams)
        preStayViewController.hidesBottomBarWhenPushed = true

        let showPreStayFlow: () -> Void = { [weak self] in
            guard let self else {
                completion()
                return
            }

            // Make sure PreStay always shows the header/back button when pushed.
            navigationController.setNavigationBarHidden(false, animated: false)

            if navigationController.viewControllers.isEmpty {
                // Fallback: when there is no push stack, present in a MyBookings-like nav container
                // with an explicit Close action so the user is never stuck in full screen.
                preStayViewController.navigationItem.leftBarButtonItem = UIBarButtonItem(
                    title: PILocalizedString("Close"),
                    style: .plain,
                    target: self,
                    action: #selector(closePresentedCiolFlow)
                )

                let modalNavigationController = MyBookingsNavigationController(rootViewController: preStayViewController)
                modalNavigationController.modalPresentationStyle = .fullScreen
                view?.present(modalNavigationController, animated: true) {
                    completion()
                }
                return
            }

            navigationController.pushViewController(preStayViewController, animated: true)
            completion()
        }

        if let presentedViewController = view?.presentedViewController {
            presentedViewController.dismiss(animated: true) {
                showPreStayFlow()
            }
            return
        }

        showPreStayFlow()
    }

    @objc private func closePresentedCiolFlow() {
        view?.presentedViewController?.dismiss(animated: true)
    }

    func showFindReservation(arrivalDate: String, reservationNumber: String, lastName: String?) {
        let findReservationController = FindReservationRouter.build(
            arrivalDate: arrivalDate,
            reservationNumber: reservationNumber,
            lastName: lastName
        )
        findReservationController.hidesBottomBarWhenPushed = true

        guard let bookingsNavigationController = navigationController?.tabBarController?
              .viewControllers?[1] as? MyBookingsNavigationController else { return }

        bookingsNavigationController.popToRootViewController(animated: true)
        bookingsNavigationController.pushViewController(findReservationController, animated: true)
        navigationController?.visibleViewController?.tabBarController?.selectedIndex = 1
    }

    func showBookingDetails(identifier: String) {
        let manager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        guard let summary = manager.items.first(where: { $0.identifier == identifier }) else { return }

        let bookingConfirmationController = BookingConfirmationModule.build(summary: summary, isBookingFlowEnd: false)
        bookingConfirmationController.hidesBottomBarWhenPushed = true

        navigationController?.pushViewController(bookingConfirmationController, animated: true)
    }

    func showAmendBooking(identifier: String) {
        let manager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        guard let summary = manager.items.first(where: { $0.identifier == identifier }) else { return }

        let amendBookingController = AmendBookingRouter.build(with: summary, and: nil)

        amendBookingController.hidesBottomBarWhenPushed = true
        navigationController?.setNavigationBarHidden(false, animated: false)
        navigationController?.pushViewController(amendBookingController, animated: true)
    }

    func showHome() {
        navigationController?.visibleViewController?.tabBarController?.selectedIndex = 0
    }

    func showHotelDetails(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        availabilityResponse: HotelAvailabilityResponse,
        and suggestion: Suggestion?
    ) {
        let hotelController = HotelDetailsModule.build(
            withCode: hotelCode,
            hotelBrand: hotelBrand ?? suggestion?.brand,
            existingAvailability: availabilityResponse,
            bookingAllowed: true,
            andSuggestion: suggestion
        )

        navigationController?.pushViewController(hotelController, animated: true)
    }

    func showDatelessHotelDetailsDeepLink(hotelCode: String, hotelBrand: HotelBrand) {
        let controller = HotelDetailsModule.build(
            withCode: hotelCode,
            hotelBrand: hotelBrand,
            existingAvailability: nil,
            bookingAllowed: true,
            andSuggestion: nil,
            shouldShowCheckAvailability: true
        )

        navigationController?.pushViewController(controller, animated: true)
    }

    func showDatelessHotelDetails(dashboardHotelDetails: DashboardHotelDetails) {
        let controller = HotelDetailsModule.build(
            withCode: dashboardHotelDetails.code,
            hotelBrand: dashboardHotelDetails.brand,
            existingAvailability: nil,
            bookingAllowed: true,
            andSuggestion: nil,
            shouldShowCheckAvailability: true
        )

        navigationController?.pushViewController(controller, animated: true)
    }

    func resetNavigation() {
        navigationController?.visibleViewController?.presentedViewController?.dismiss(animated: false)

        navigationController?.visibleViewController?.tabBarController?.selectedIndex = 0
        _ = navigationController?.popToRootViewController(animated: false)
    }

    func selectedLocation(sender: UIView) {
        let controller = SuggestionsRouter.build()
        controller.delegate = self

        let navController = UINavigationController(rootViewController: controller)
        navController.modalPresentationStyle = .popover
        navController.popoverPresentationController?.permittedArrowDirections = .left
        navController.popoverPresentationController?.sourceView = sender
        navController.popoverPresentationController?.sourceRect = sender.bounds

        view?.present(navController, animated: true)
	}

    func showCalendarAndHotel(with dashboardHotelDetails: DashboardHotelDetails) {
        let controller = HotelDetailsModule.build(
            withCode: dashboardHotelDetails.code,
            hotelBrand: dashboardHotelDetails.brand,
            existingAvailability: nil,
            bookingAllowed: true,
            andSuggestion: nil,
            shouldShowCheckAvailability: true,
            shouldDisplayCalendarFirst: true
        )

        navigationController?.pushViewController(controller, animated: true)
    }

	func selectedNights(sender: UIView) {
        let arrivalDate = BookingDetails.sharedInstance.criteria.arrivalDate
        let departureDate = arrivalDate.dateByAddingUnit(
            unitType: .day,
            number: BookingDetails.sharedInstance.criteria.nights
        )

        let controller = AlternateCalendarViewController(arrivalDate: arrivalDate, departureDate: departureDate)
        controller.calendarDelegate = self

        let navController = UINavigationController(rootViewController: controller)
        navController.modalPresentationStyle = .popover
        navController.popoverPresentationController?.permittedArrowDirections = .left
        navController.popoverPresentationController?.sourceView = sender
        navController.popoverPresentationController?.sourceRect = sender.bounds
        navController.preferredContentSize = CGSize(width: 350, height: 430)

        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .Tint1
        appearance.shadowColor = .clear
        navController.navigationBar.standardAppearance = appearance
        navController.navigationBar.scrollEdgeAppearance = appearance
        view?.present(navController, animated: true)
	}

	func selectedGuests(sender: UIView) {
        let controller = RoomsGuestsCriteriaView()
        controller.eventHandler = self
        controller.presenter = {
            let interactor = RoomsGuestsCriteriaViewModel(criteria: BookingDetails.sharedInstance.criteria)

            let presenter = RoomsGuestsCriteriaPresenter(interactor: interactor)
            presenter.view = controller

            return presenter
        }()

        let navController = UINavigationController(rootViewController: controller)
        navController.modalPresentationStyle = .popover
        navController.popoverPresentationController?.permittedArrowDirections = .right
        navController.popoverPresentationController?.sourceView = sender
        navController.popoverPresentationController?.sourceRect = sender.bounds
        navController.preferredContentSize = CGSize(width: 350, height: 430)

        view?.present(navController, animated: true)
	}

    func selectedLocation() {
        showSuggestions()
    }

    func selectedNights() {
        showCalendar()
    }

    func selectedGuests() {
        showGuestsRooms()
    }

	func showGDPRInfo() {
        let gdprView = GDPRView(onDismiss: { [weak self] in
            self?.view?.dismiss(animated: true)
        })
        let hostController = UIHostingController(rootView: gdprView)
        hostController.modalPresentationStyle = .fullScreen
        guard let presentingController = navigationController ?? view else { return }
        presentingController.present(hostController, animated: true, completion: nil)
	}

	func searchNearMe() {
		locationManager.delegate = self
		locationManager.findUserLocation()
	}

    func presentResultController(_ controller: UIViewController) {
        navigationController?.pushViewController(controller, animated: true)
    }

    func showBBSplash() {
        guard UserDefaults.standard.bool(forKey: Constants.hasAcceptedGDPRChanges) == true else { return }
        guard UserDefaults.standard.bool(forKey: Constants.shownBBIntroKey) != true else { return }

        let bbc = BusinessBookerIntroModule.build(with: self)
        view?.present(bbc, animated: true)
    }

    func showBBCentralCardExpired(with message: String, completion: @escaping () -> Void?) {
        let alertController = UIAlertController(
            title: PILocalizedString("hotelDetailsBBCardExpiredTitle"),
            message: PILocalizedString("hotelDetailsBBCardExpiredMessage"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("EmailTravelManager"),
            style: .default,
            handler: { _ in
            guard MFMailComposeViewController.canSendMail() else {
                return self.showNoEmailError()
            }

            let controller = MFMailComposeViewController()
            controller.navigationBar.tintColor = .white
            controller.setSubject(PILocalizedString("hotelDetailsBBCardExpiredEmailContent"))

            self.view?.present(controller, animated: true)
        }
        ))
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("bookingReviewContinueButtonTitle"),
            style: .default,
            handler: { _ in
            completion()
        }
        ))

        DispatchQueue.main.async {
            self.view?.present(alertController, animated: true) {
                alertController.setActionAccessibilityIdentifiers(with: [
                    (
                        PILocalizedString("EmailTravelManager"),
                        AccessibilityIdentifiers.Home.bbCardExpiredEmailTravelManagerAction
                    ),
                    (
                        PILocalizedString("bookingReviewContinueButtonTitle"),
                        AccessibilityIdentifiers.Home.bbCardExpiredContinueAction
                    )
                ])
            }
        }
    }

    func showAnnouncementMessage(with message: NotificationsMessage) {
        let announcementsViewController = ImportantAnnouncementsModule.build(with: message)
        view?.present(announcementsViewController, animated: true)
    }

    func showPromotionPopover() {
        guard let view else { return }

        let promotionPopover = PromotionPopoverView()
        let hostController = UIHostingController(rootView: promotionPopover)
        let sourceView = view.tabBarController?.tabBar ?? view.view

        hostController.modalPresentationStyle = .popover
        hostController.popoverPresentationController?.sourceView = sourceView
        hostController.popoverPresentationController?.sourceRect = sourceView?.bounds ?? CGRect.zero

        let popoverWidth = view.view.bounds.width
        let popoverHeight = max(700, (view.view.bounds.height) - 360)
        hostController.preferredContentSize = CGSize(width: popoverWidth, height: popoverHeight)

        view.present(hostController, animated: true)
    }
}

extension HomeRouter: SuggestionsViewControllerDelegate {
	func suggestionsViewControllerDidCancel(_ sender: SuggestionsViewController) {
		sender.dismiss(animated: true)
	}

	func suggestionsViewControllerDidPickSuggestion(_ sender: SuggestionsViewController, suggestion: Suggestion) {
		presenter?.selected(suggestion: suggestion)

		sender.dismiss(animated: true)
	}
}

extension HomeRouter: LocationManagerDelegate {
	func locationManagerDidFind(userLocation location: CLLocation, locationManager: LocationManager) {
		locationManager.delegate = nil

		let suggestion = PISuggestion(coordinate: location.coordinate)
		suggestion.aCustomTitle = PILocalizedString("CurrentLocation", comment: "")

		presenter?.foundLocation(suggestion: suggestion)
	}

	func locationManagerDidFailWithError(locationManager: LocationManager, error: NSError) {
		locationManager.delegate = nil
	}

	func locationManagerAuthorizationDenied(locationManager: LocationManager, shouldShowAlert: Bool) {
		locationManager.delegate = nil

		presenter?.refreshView()

		let alert = locationManager.authDeniedSettingsAlertController()
		view?.present(alert, animated: true)
	}
}

extension HomeRouter: RoomsGuestsCriteriaViewEventHandler {
	func criteriaController(_ sender: RoomsGuestsCriteriaView, didFinishWith criteria: Criteria) {
		BookingDetails.sharedInstance.criteria = criteria

		view?.dismiss(animated: true, completion: { [weak self] in
			self?.presenter?.selected(criteria: criteria)
		})
	}

	func criteriaControllerDidCancel(_ sender: RoomsGuestsCriteriaView) {
		view?.dismiss(animated: true)
	}
}

extension HomeRouter: AlternateCalendarViewControllerDelegate {
	func calendarDidSelect(arrivalDate: Date, nights: Int) {
		view?.dismiss(animated: true)

		BookingDetails.sharedInstance.criteria.arrivalDate = arrivalDate
		BookingDetails.sharedInstance.criteria.nights = nights

		presenter?.selected(criteria: BookingDetails.sharedInstance.criteria)
	}

    func calendarDidChange(arrivalDate: Date, nights: Int) {}

    func calendarDidInvalidate() {}
}

extension HomeRouter: BusinessBookerIntroRouterDelegate {
    func selectedLogin(isBusiness: Bool, andIsFromSplashScreen isFromSplashScreen: Bool) {
        if UserSessionManager.sharedInstance.currentUser != nil {
            UserSessionManager.sharedInstance.piUserLoggedOut()
        }

        guard let view = view else { return }
        LoginRouter().presentLoginInterface(
            from: view,
            asBusinessLogin: isBusiness,
            comingFromSplashScreen: isFromSplashScreen,
            andIsFromBookingFlow: false
        )
    }
}
