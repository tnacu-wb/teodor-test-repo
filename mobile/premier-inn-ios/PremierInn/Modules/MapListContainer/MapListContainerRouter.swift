//
//  MapListContainerRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol MapListContainerRouterProtocol {
    func showMapModule()
    func showListModule()
    func showCardsModule()
    func navigateTo(hotel: Hotel, at indexPath: IndexPath, suggestion: Suggestion?, isMapVisible: Bool)
    func selectedLocation()
    func selectedNights()
    func selectedGuests()
    func presentCriteriaViewController(with suggestion: Suggestion)
    func updateHotelDetailsIfVisible()
    func showHotelDetailsPage(_ hotelController: HotelDetailsViewController)
    func hideHotelDetailsIfVisible()
}

class MapListContainerRouter {
    private weak var controller: MapListContainerViewController?
    private var mapController: Map2ViewController?
    private var listController: VenuesListViewController?
    private var cardsController: VenuesCardsViewController?

    static func buildMapListContainerView(
        with availabilityResponse: AvailabilitiesResponse,
        unavailableHotelCode: String?,
        suggestion: Suggestion
    ) -> UIViewController {
        let controller = MapListContainerViewController()
        controller.hidesBottomBarWhenPushed = true

        let mapController = Map2ViewController()
        let listController = VenuesListViewController()
        let cardsController = VenuesCardsViewController()

        let router = MapListContainerRouter()
        router.controller = controller
        router.mapController = mapController
        router.listController = listController
        router.cardsController = cardsController

        let presenter = MapListContainerPresenter()

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

    static func buildMapListContainerView(with controller: MapListContainerViewController) {
        let mapController = Map2ViewController()
        let listController = VenuesListViewController()
        let cardsController = VenuesCardsViewController()

        let router = MapListContainerRouter()
        router.controller = controller
        router.mapController = mapController
        router.listController = listController
        router.cardsController = cardsController

        let presenter = MapListContainerPresenter()

        let interactor = VenuesInteractor(hotels: [], suggestion: nil, unavailableHotelCode: nil)

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
    }
}

extension MapListContainerRouter: MapListContainerRouterProtocol {
    func presentCriteriaViewController(with suggestion: Suggestion) {
//        let criteria = BookingDetails.sharedInstance.criteria
//        let criteriaController = FlowController.sharedInstance.criteriaViewController(with: criteria, suggestion: suggestion, delegate: controller)
//        let navController = UINavigationController(rootViewController: criteriaController)
//
//        controller?.present(navController, animated: true)
    }

    func updateHotelDetailsIfVisible() {
        guard UIDevice.current.userInterfaceIdiom == .pad else { return }
        guard let hotelDetailsViewController = controller?.children
              .first(where: { $0 is HotelDetailsViewController }) as? HotelDetailsViewController else { return }

        hotelDetailsViewController.updateHotelAndAvailability()
    }

    func showHotelDetailsPage(_ hotelController: HotelDetailsViewController) {
        if UIDevice.current.userInterfaceIdiom == .pad {
            hideHotelDetailsIfVisible()

            guard let controller = controller else { return }

            controller.addChild(hotelController)
            controller.listContainerView.addSubview(hotelController.view)
            hotelController.view.frame = controller.listContainerView.bounds
            hotelController.didMove(toParent: controller)

            UIView.animate(withDuration: .ocd, animations: {
                controller.listContainerView.frame.origin.y = 0
                controller.listContainerView.alpha = 1
            })
        } else {
            controller?.navigationController?.pushViewController(hotelController, animated: true)
        }
    }

    func selectedLocation() {
        let suggestionController = SuggestionsRouter.build()
        suggestionController.delegate = self

        controller?.present(UINavigationController(rootViewController: suggestionController), animated: true)
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
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .Tint1
        appearance.shadowColor = .clear
        navController.navigationBar.standardAppearance = appearance
        navController.navigationBar.scrollEdgeAppearance = appearance
		controller?.present(navController, animated: true)
    }

    func selectedGuests() {
        let roomGuestController = RoomsGuestsCriteriaView()
        roomGuestController.eventHandler = self
        roomGuestController.presenter = {
            let interactor = RoomsGuestsCriteriaViewModel(criteria: BookingDetails.sharedInstance.criteria)

            let presenter = RoomsGuestsCriteriaPresenter(interactor: interactor)
            presenter.view = roomGuestController

            return presenter
        }()

        controller?.present(UINavigationController(rootViewController: roomGuestController), animated: true)
    }

    func showMapModule() {
        guard let controller = controller else { return }
        guard let mapController = mapController else { return }

        let padding: CGFloat = 20

        mapController.smallMapStyleMapInsets = UIEdgeInsets(
            top: {
                guard let optionsView = controller.optionsView else { return 0 }

                return optionsView.frame.height + padding
        }(),
            left: 0,
            bottom: {
                guard let listContainer = controller.listContainerView else { return 0 }
                guard let optionsView = controller.optionsView else { return 0 }

                return listContainer.frame.height - optionsView.frame.height - CGFloat(Constants.smallMapHeight) + padding
        }(),
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
        let hotelController = HotelDetailsModule.build(
            withCode: hotel.code,
            hotelBrand: hotel.brand,
            existingAvailability: nil,
            bookingAllowed: true,
            andSuggestion: suggestion,
            arrivedViaMap: isMapVisible,
            distanceToSearch: hotel.distance,
            delegate: self
        )

        if UIDevice.current.userInterfaceIdiom == .pad {
            hideHotelDetailsIfVisible()

            guard let controller = controller else { return }

            controller.addChild(hotelController)
            controller.listContainerView.addSubview(hotelController.view)
            hotelController.view.frame = controller.listContainerView.bounds
            hotelController.didMove(toParent: controller)

            UIView.animate(withDuration: .ocd, animations: {
                controller.listContainerView.frame.origin.y = 0
                controller.listContainerView.alpha = 1
            })
        } else {
            controller?.navigationController?.pushViewController(hotelController, animated: true)
        }
    }

    func hideHotelDetailsIfVisible() {
        guard let controller = controller else { return }

        controller.children.filter { type(of: $0) == HotelDetailsViewController.self }.forEach {
            $0.view.removeFromSuperview()
            $0.removeFromParent()
        }
    }
}

extension MapListContainerRouter: SuggestionsViewControllerDelegate {
    func suggestionsViewControllerDidCancel(_ sender: SuggestionsViewController) {
        sender.dismiss(animated: true)
    }

    func suggestionsViewControllerDidPickSuggestion(_ sender: SuggestionsViewController, suggestion: Suggestion) {
        controller?.updatedSuggestion(to: suggestion)

        sender.dismiss(animated: true)
    }
}

extension MapListContainerRouter: AlternateCalendarViewControllerDelegate {
    func calendarDidChange(arrivalDate: Date, nights: Int) {}

    func calendarDidInvalidate() {}

	func calendarDidSelect(arrivalDate: Date, nights: Int) {
		BookingDetails.sharedInstance.criteria.arrivalDate = arrivalDate
		BookingDetails.sharedInstance.criteria.nights = nights

        controller?.updateCriteria(to: BookingDetails.sharedInstance.criteria)

        controller?.dismiss(animated: true)
	}
}

extension MapListContainerRouter: RoomsGuestsCriteriaViewEventHandler {
    func criteriaController(_ sender: RoomsGuestsCriteriaView, didFinishWith criteria: Criteria) {
        BookingDetails.sharedInstance.criteria = criteria

        controller?.updateCriteria(to: criteria)

        controller?.dismiss(animated: true)
    }

    func criteriaControllerDidCancel(_ sender: RoomsGuestsCriteriaView) {
        controller?.dismiss(animated: true)
    }
}

extension MapListContainerRouter: HotelDetailsRouterDelegate {
    func cancelButtonDidTap(sender: UIViewController?) {
        guard let listContainerView = controller?.listContainerView else { return }
        guard let hdpController = sender else { return }

        UIView.animate(
            withDuration: .ocd,
            animations: {
                listContainerView.alpha = 0.2
                listContainerView.frame.origin.y = UIScreen.main.bounds.size.height
            },
            completion: { _ in
                hdpController.view.removeFromSuperview()
                hdpController.removeFromParent()

                UIView.animate(withDuration: .ocd, animations: {
                    listContainerView.alpha = 1
                    listContainerView.frame.origin.y = 0
                })
        }
        )
    }
}
