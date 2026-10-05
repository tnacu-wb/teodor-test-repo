//
//  MapListContainerRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol MapListContainerRouterProtocolTwo {
    func showMapModule()
    func showListModule()
    func showCardsModule()
    func navigateTo(hotel: Hotel, at indexPath: IndexPath, suggestion: Suggestion?, isMapVisible: Bool)
    func presentCriteriaViewController(with suggestion: Suggestion)

    func selectedLocation()
    func selectedNights()
    func selectedGuests()

    func showHotelDetailsPage(_ hotelController: HotelDetailViewController)
}

class MapListContainerRouterTwo {
    private weak var controller: MapListContainerViewControllerTwo?
    private var mapController: Map2ViewController?
    private var listController: VenuesListViewController?
    private var cardsController: VenuesCardsViewController?

    static func buildMapListContainerView(
        with availabilityResponse: AvailabilitiesResponse,
        unavailableHotelCode: String?,
        suggestion: Suggestion
    ) -> UIViewController {
        let controller = MapListContainerViewControllerTwo()
        controller.hidesBottomBarWhenPushed = true

        let mapController = Map2ViewController()
        let listController = VenuesListViewController()
        let cardsController = VenuesCardsViewController()

        let router = MapListContainerRouterTwo()
        router.controller = controller
        router.mapController = mapController
        router.listController = listController
        router.cardsController = cardsController

        let presenter = MapListContainerPresenterTwo()

        let interactor = VenuesInteractor(
            hotels: availabilityResponse.hotels,
            suggestion: suggestion,
            unavailableHotelCode: unavailableHotelCode
        )

        presenter.containerView = controller
        presenter.listView = listController
        presenter.cardsView = cardsController
        presenter.mapView = mapController
        presenter.router = router
        presenter.interactor = interactor

        controller.presenter = presenter
        mapController.presenter = presenter
        listController.presenter = presenter
        cardsController.presenter = presenter

        return controller
    }
}

extension MapListContainerRouterTwo: MapListContainerRouterProtocolTwo {
    func showHotelDetailsPage(_ hotelController: HotelDetailViewController) {
        if UIDevice.current.userInterfaceIdiom == .pad {
            listController?.navigationController?.pushViewController(hotelController, animated: true)
        } else {
            controller?.navigationController?.pushViewController(hotelController, animated: true)
        }
    }

    func selectedLocation() {
        let controller = FlowController.sharedInstance.suggestionsViewController(delegate: self)

        self.controller?.present(UINavigationController(rootViewController: controller), animated: true)
    }

    func selectedNights() {
		let arrivalDate = BookingDetails.sharedInstance.criteria.arrivalDate
		let departureDate = arrivalDate.dateByAddingUnit(
		    unitType: .day,
		    number: BookingDetails.sharedInstance.criteria.nights
		)

		let calendarController = AlternateCalendarViewController(arrivalDate: arrivalDate, departureDate: departureDate)
		calendarController.calendarDelegate = self

		let navController = UINavigationController(rootViewController: calendarController)
		navController.navigationBar.setBackgroundImage(UIImage(), for: .default)
		navController.navigationBar.shadowImage = UIImage()
		navController.navigationBar.tintColor = .white
		navController.navigationBar.barTintColor = .sea

		controller?.present(navController, animated: true)
    }

    func selectedGuests() {
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
        self.controller?.present(navigationController, animated: true, completion: nil)
    }

    func showMapModule() {
        guard let controller = controller else { return }
        guard let mapController = mapController else { return }

        let padding: CGFloat = 20

        mapController.smallMapStyleMapInsets = UIEdgeInsets(
            top: controller.optionsView.frame.height + padding,
            left: 0,
            bottom: controller.listContainerView.frame.height - controller.optionsView.frame
            .height - CGFloat(Constants.smallMapHeight) + padding,
            right: 0
        )

        controller.addChild(mapController)
        mapController.view.frame = controller.mapContainerView.bounds
        controller.mapContainerView.addSubview(mapController.view)
        mapController.didMove(toParent: controller)
    }

    func showListModule() {
        guard let controller = controller else { return }
        guard let listController = listController else { return }

        controller.addChild(listController)
        controller.listContainerView.addSubview(listController.view)
        listController.view.frame = controller.listContainerView.bounds
        listController.didMove(toParent: controller)
    }

    func showCardsModule() {
        guard let controller = controller else { return }
        guard let cardsController = cardsController else { return }

        controller.addChild(cardsController)
        controller.cardsContainerView.addSubview(cardsController.view)
        cardsController.view.frame = controller.cardsContainerView.bounds
        cardsController.didMove(toParent: controller)
    }

    func navigateTo(hotel: Hotel, at indexPath: IndexPath, suggestion: Suggestion?, isMapVisible: Bool) {
        let hotelController = HotelDetailViewController(hotel: hotel)
        hotelController.controllerOutput = FlowController.sharedInstance
        hotelController.suggestion = suggestion
        hotelController.arrivedViaMap = isMapVisible
        hotelController.searchIndex = indexPath.row

        controller?.navigationController?.pushViewController(hotelController, animated: true)
    }

    func presentCriteriaViewController(with suggestion: Suggestion) {
        let criteria = BookingDetails.sharedInstance.criteria
        let criteriaController = FlowController.sharedInstance.criteriaViewController(
            with: criteria,
            suggestion: suggestion,
            delegate: controller
        )

        controller?.present(UINavigationController(rootViewController: criteriaController), animated: true)
    }
}

extension MapListContainerRouterTwo: SuggestionsViewControllerDelegate {
    func suggestionsViewControllerDidCancel(_ sender: SuggestionsViewController) {
        sender.dismiss(animated: true)
    }

    func suggestionsViewControllerDidPickSuggestion(_ sender: SuggestionsViewController, suggestion: Suggestion) {
        self.controller?.updatedSuggestion(to: suggestion)

        sender.dismiss(animated: true)
    }
}

extension MapListContainerRouterTwo: AlternateCalendarViewControllerDelegate {
	func calendarDidSelect(arrivalDate: Date, nights: Int) {
		BookingDetails.sharedInstance.criteria.arrivalDate = arrivalDate
		BookingDetails.sharedInstance.criteria.nights = nights

		controller?.dismiss(animated: true) { [weak self] in
			self?.controller?.updateCriteria(to: BookingDetails.sharedInstance.criteria)
		}
	}

    func calendarDidChange(arrivalDate: Date, nights: Int) {}

    func calendarDidInvalidate() {}
}

extension MapListContainerRouterTwo: RoomsGuestsCriteriaViewEventHandler {
    func criteriaController(_ sender: RoomsGuestsCriteriaView, didFinishWith criteria: Criteria) {
        BookingDetails.sharedInstance.criteria = criteria

        controller?.dismiss(animated: true, completion: { [weak self] in
            self?.controller?.updateCriteria(to: criteria)
        })
    }

    func criteriaControllerDidCancel(_ sender: RoomsGuestsCriteriaView) {
        controller?.dismiss(animated: true)
    }
}
