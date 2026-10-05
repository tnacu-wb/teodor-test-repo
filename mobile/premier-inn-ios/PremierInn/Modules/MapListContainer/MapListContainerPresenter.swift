//
//  MapListContainerPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import MapKit
import SimpleNetwork

enum VenuesListStyle {
    case card
    case regular
}

protocol MapListContainerViewProtocol: AnyObject {
	func makeMapFullScreen()
	func makeMapSmall()
	func popViewController()
	func updateMapConstraints(optionsViewTop: CGFloat)
    func slideOptionsViewIn()
    func slideOptionsViewInWithoutAnimation()
	func slideOptionsViewOut()
    func getCurrentOptionsViewTopHandleOffset() -> CGFloat
    func hideCards()
	func showCards()
    func revertSegmentedControlToPreviousSetting()
    func provideHapticFeedback()
    func toggleOptionsView(visible: Bool)
    func hotelsWereReloaded()

    func userDidScroll()
    func updateCriteria(to criteria: Criteria)
    func updatedSuggestion(to suggestion: Suggestion)

    func refreshCriteriaElements()
    func errorDidOccur()
    func showError(alertController: UIAlertController)
    func showFallBack()
    func showLoadingBox()
    func hideLoadingBox()
}

protocol MapViewProtocol: AnyObject {
    var centerCoordinate: CLLocationCoordinate2D { get }

    func reload()
    func mapWillGoFullScreen()
    func mapWillGoSmall()
    func deselectAnnotation(_ annotation: MKAnnotation)
    func selectAnnotation(_ annotation: MKAnnotation)
    func centerAnnotation(_ annotation: MKAnnotation)
    func zoomToAnnotation(_ annotation: MKAnnotation)
    func fitAnnotationsInView(style: VenuesListStyle, animated: Bool)
    func showListButton()
    func hideListButton()
    func showResetButton()
    func hideResetButton()
    func showSearchHereButton()
    func hideSearchHereButton()
}

protocol ListViewProtocol: AnyObject {
    func reload()
    func mapWillGoFullScreen()
    func mapDidGoFullScreen()
    func scrollToItemAt(indexPath: IndexPath, andSelect selectCell: Bool?)
    func willReturnToListView()
    func selectCellAt(indexPath: IndexPath)
    func showErrorMessage(message: String?, bottomConstraint: CGFloat)
    func removeErrorMessage()
    func selectedListCell() -> VenueCell?
}


private enum ScrollDirection {
    case up
    case down
    case none
}

class MapListContainerPresenter {
    weak var containerView: MapListContainerViewProtocol?
	weak var mapView: MapViewProtocol?
	weak var listView: ListViewProtocol?
	weak var cardsView: ListViewProtocol?

    private var listShowing = false
    private var currentOffset: CGFloat = 0
    private var optionsSectionIsVisible = true
    private var preselectedHotelCode: String?

    var sortType: AvailabilitiesSorting = .distance
	var router: MapListContainerRouterProtocol?
	var interactor: VenueInteractorProtocol?

    var offsetAtLastDirectionChange: CGFloat = 0

    private var scrollDirection: ScrollDirection = .none

    var currentListStyle = VenuesListStyle.regular
    private var selectedHotel: Hotel?
	private func indexPath(forHotel hotel: Hotel) -> IndexPath? {
		guard let venues = interactor?.venues as? [Hotel] else { return nil }

		for (index, venue) in venues.enumerated() where venue == hotel {
			return IndexPath(row: index, section: 0)
		}

		return nil
	}

	private func analyticsCall() {
		interactor?.trackAvailability(style: currentListStyle)
		interactor?.logToFirebase()
	}

    func presentErrorMessageOnListViewIfNeeded() {
        listView?.removeErrorMessage()

        guard let error = interactor?.error as? VenuesInteractorError else { return }
        guard currentListStyle == .regular else { return }

        var message: String = error.localizedDescription
        let bottomMargin: CGFloat = interactor?.venues.isEmpty == true ? 16 : 0

        if BookingDetails.sharedInstance.criteria.childrenCount > 0 && interactor?.suggestion?.brand == .hub {
            message = PILocalizedString("hubFamilyRoomNotAvailableMessage", comment: "Hub hotels do not offer family rooms")
        }

        listView?.showErrorMessage(message: message, bottomConstraint: bottomMargin)
    }

    private func reloadHotels(shouldHideHotelDetails: Bool = false) {
        router?.updateHotelDetailsIfVisible()

        guard let interactor = self.interactor else { return }

        containerView?.showLoadingBox()

        interactor.fetchHotels(hotelDetailsRouterDelegate: (router as? MapListContainerRouter)) { [weak self] _, _ in
            self?.containerView?.hideLoadingBox()

            self?.mapView?.reload()
            self?.mapView?.fitAnnotationsInView(style: self?.currentListStyle ?? .regular, animated: true)

            self?.listView?.reload()
            self?.cardsView?.reload()
            self?.containerView?.hotelsWereReloaded()

            if UIDevice.current.userInterfaceIdiom == .pad {
                if let suggestion = self?.interactor?.suggestion, suggestion.isHotel == true {
                    guard let index = self?.interactor?.indexOfHotel(with: suggestion.identifier ?? "") else { return }
                    guard let hotel = self?.interactor?.venues[index] as? Hotel else { return }
                    if self?.interactor?.shouldShowFallBack() == true {
                        self?.containerView?.showFallBack()
                    } else {
                        self?.router?.navigateTo(
                            hotel: hotel,
                            at: IndexPath(row: index, section: 0),
                            suggestion: suggestion,
                            isMapVisible: true
                        )
                    }
                } else if shouldHideHotelDetails {
                    self?.router?.hideHotelDetailsIfVisible()
                }
            }
        }
    }

    private func popoverNavController(
        with source: UIView,
        and rootViewController: UIViewController
    ) -> UINavigationController {
        let navController = UINavigationController(rootViewController: rootViewController)
        navController.modalPresentationStyle = .popover
        navController.popoverPresentationController?.permittedArrowDirections = .right
        navController.preferredContentSize = CGSize(width: 350, height: 430)
        navController.popoverPresentationController?.sourceView = source
        navController.popoverPresentationController?.sourceRect = source.bounds

        return navController
    }

    private func reloadNewHotels() {
        containerView?.showLoadingBox()

        interactor?.fetchHotels(hotelDetailsRouterDelegate: nil) { [weak self] success, nextScreen in
            self?.containerView?.hideLoadingBox()

            guard success else {
                self?.containerView?.errorDidOccur()
                return
            }

            if let alertView = nextScreen as? UIAlertController {
                self?.containerView?.showError(alertController: alertView)
                return
            }

            self?.analyticsCall()

            self?.containerView?.refreshCriteriaElements()
            self?.listViewIsReady()

            self?.mapView?.reload()
            self?.mapView?.fitAnnotationsInView(style: self?.currentListStyle ?? VenuesListStyle.card, animated: true)

            if let hotelScreen = nextScreen as? HotelDetailsViewController {
                self?.router?.showHotelDetailsPage(hotelScreen)
                return
            }
        }
    }

    private func performSearch(with suggestion: Suggestion) {
        if let currentlySelectedHotel = selectedHotel {
            mapView?.deselectAnnotation(currentlySelectedHotel)
        }

        mapView?.hideResetButton()
        mapView?.hideSearchHereButton()
        containerView?.hideCards()
        listShowing = false

        containerView?.updatedSuggestion(to: suggestion)
    }
}

extension MapListContainerPresenter: MapListContainerPresenterProtocol {
    func editButtonDidTap() {
        guard let suggestion = interactor?.suggestion else { return }

        router?.presentCriteriaViewController(with: suggestion)
    }

    func viewIsReady() {
        router?.showMapModule()
        router?.showListModule()
        router?.showCardsModule()

        analyticsCall()
    }

    func selectedListCell() -> VenueCell? {
        listView?.selectedListCell()
    }


    var screenTitle: String? { interactor?.screenTitle }
    var screenSubtitle: String? { interactor?.screenSubtitle }

    func updatedSuggestion(to suggestion: Suggestion) {
        interactor?.updateSuggestion(to: suggestion)
        reloadNewHotels()
    }

    func criteriaUpdated(to criteria: Criteria) {
        reloadNewHotels()
    }

    func selectedLocation() {
        router?.selectedLocation()
    }

    func selectedNights() {
        router?.selectedNights()
    }

    func selectedGuests() {
        router?.selectedGuests()
    }

    var condensedCriteriaTitle: NSAttributedString {
        let components = [
            dateTextForCurrentCriteriaSummary ?? "",
            roomsAndGuestsTextForCriteria ?? ""
        ]

        let fullyFormedCriteriaTitleWithSeperators = components.joined(separator: "  ")
        var attributedString = NSAttributedString(string: fullyFormedCriteriaTitleWithSeperators)

        attributedString = attributedString.attributedStringByColoringBullets(with: .whiteThree)

        return attributedString
    }

    var nameOfLocationTextForCurrentCriteria: String? {
        interactor?.suggestion?.title
    }

    var dateTextForCurrentCriteriaSummary: String? {
        guard let criteria = interactor?.criteria else { return "NA nights" }

        let checkOutDateString: String = {
            criteria.checkOutDate?.localizedShortDayMonthStringFormat ?? ""
        }()

        let checkInDateString: String = {
            criteria.arrivalDate.localizedShortDayMonthStringFormat
        }()

        return "\(checkInDateString) - \(checkOutDateString)"
    }

    var roomsAndGuestsTextForCriteria: String? {
        guard let guestsCountDescription = interactor?.criteria.guestsCountDescription else { return nil }
        guard let roomsCountDescription = interactor?.criteria.roomsCountDescription else { return nil }

        return guestsCountDescription + ", " + roomsCountDescription
    }

    func viewIsReady(shouldFetchNearbyHotels: Bool) {
        router?.showMapModule()

        // if UIDevice.current.userInterfaceIdiom != .pad {
            router?.showListModule()
        // }

        router?.showCardsModule()

        if UIDevice.current.userInterfaceIdiom == .pad {
            containerView?.toggleOptionsView(visible: false)
            containerView?.makeMapFullScreen()
        }

        if shouldFetchNearbyHotels {
            interactor?.fetchHotelsNearby(with: .distance) { success in
                guard success else { return }

                self.mapView?.reload()
                self.mapView?.fitAnnotationsInView(style: self.currentListStyle, animated: true)
                self.cardsView?.reload()

                self.presentErrorMessageOnListViewIfNeeded()

                self.analyticsCall()

                if UIDevice.current.userInterfaceIdiom == .pad {
                    guard let preselectedHotelCode = self.preselectedHotelCode.remove() else { return }
                    guard let index = self.interactor?.indexOfHotel(with: preselectedHotelCode) else { return }

                    self.didSelectItem(at: IndexPath(item: index, section: 0), mapVisible: true)
                }
            }
        } else if UIDevice.current.userInterfaceIdiom == .pad {
            guard let preselectedHotelCode = preselectedHotelCode.remove() else { return }
            guard let index = interactor?.indexOfHotel(with: preselectedHotelCode) else { return }

            didSelectItem(at: IndexPath(item: index, section: 0), mapVisible: true)
        }

		analyticsCall()
    }

	func mapButtonDidTap() {
        containerView?.toggleOptionsView(visible: false)
		containerView?.makeMapFullScreen()
	}

	func backButtonDidTap() {
		if currentListStyle == .card {
			containerView?.makeMapSmall()
		} else {
			containerView?.popViewController()
		}
	}

    func comingBackFromHotelDetails() {
        analyticsCall()
    }

	func sortDidChange(with sortingRule: AvailabilitiesSorting) {
        sortType = sortingRule

        interactor?.fetchHotels(with: sortingRule) { [weak self] success in
            guard success else {
                self?.containerView?.revertSegmentedControlToPreviousSetting()
                return
            }

			self?.mapView?.reload()
            if let style = self?.currentListStyle {
                self?.mapView?.fitAnnotationsInView(style: style, animated: true)
            }
			self?.listView?.reload()

            self?.containerView?.slideOptionsViewInWithoutAnimation()

            self?.presentErrorMessageOnListViewIfNeeded()

            self?.analyticsCall()
        }
    }

    func mapWillGoFullScreen() {
		currentListStyle = .card

        listView?.removeErrorMessage()
		listView?.mapWillGoFullScreen()
		cardsView?.mapWillGoFullScreen()
		mapView?.mapWillGoFullScreen()
    }

    func mapDidGoFullScreen() {
		listView?.mapDidGoFullScreen()
		cardsView?.mapDidGoFullScreen()

        if UIDevice.current.userInterfaceIdiom == .pad {
            mapView?.hideListButton()
        } else {
            mapView?.showListButton()
        }

        mapView?.fitAnnotationsInView(style: currentListStyle, animated: true)
		analyticsCall()
    }

	func mapWillGoSmall() {
		currentListStyle = .regular

		listView?.willReturnToListView()
		cardsView?.willReturnToListView()
		mapView?.mapWillGoSmall()

        presentErrorMessageOnListViewIfNeeded()
	}

    func mapDidGoSmall() {
		mapView?.hideListButton()
		mapView?.hideResetButton()
        mapView?.hideSearchHereButton()
		mapView?.fitAnnotationsInView(style: currentListStyle, animated: true)
		analyticsCall()
    }

    func editGuestsButtonDidTap(sender: UIView) {
        guard let mapController = mapView as? UIViewController else { return }

        let controller = RoomsGuestsCriteriaView()
        controller.eventHandler = self
        controller.presenter = {
            let interactor = RoomsGuestsCriteriaViewModel(criteria: BookingDetails.sharedInstance.criteria)

            let presenter = RoomsGuestsCriteriaPresenter(interactor: interactor)
            presenter.view = controller

            return presenter
        }()

        let navController = popoverNavController(with: sender, and: controller)
        mapController.present(navController, animated: true)
    }

    func editDatesButtonDidTap(sender: UIView) {
        guard let mapController = mapView as? UIViewController else { return }

        let arrivalDate = BookingDetails.sharedInstance.criteria.arrivalDate
        let departureDate = arrivalDate.dateByAddingUnit(
            unitType: .day,
            number: BookingDetails.sharedInstance.criteria.nights
        )

        let controller = AlternateCalendarViewController(arrivalDate: arrivalDate, departureDate: departureDate)
        controller.calendarDelegate = self

        let navController = popoverNavController(with: sender, and: controller)
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .Tint1
        appearance.shadowColor = .clear
        navController.navigationBar.standardAppearance = appearance
        navController.navigationBar.scrollEdgeAppearance = appearance
        mapController.present(navController, animated: true)
    }

    func editSuggestionButtonDidTap(sender: UIView) {
        guard let mapController = mapView as? UIViewController else { return }

        let controller = SuggestionsRouter.build()
        controller.delegate = self

        let navController = popoverNavController(with: sender, and: controller)
        mapController.present(navController, animated: true)
    }

    func setPreselectedHotel(with code: String) {
        preselectedHotelCode = code
    }
}

extension MapListContainerPresenter: RoomsGuestsCriteriaViewEventHandler {
    func criteriaControllerDidCancel(_ sender: RoomsGuestsCriteriaView) {
        sender.dismiss(animated: true)
    }

    func criteriaController(_ sender: RoomsGuestsCriteriaView, didFinishWith criteria: Criteria) {
        BookingDetails.sharedInstance.criteria = criteria
        sender.dismiss(animated: true, completion: {
            self.reloadHotels()
        })
    }
}

extension MapListContainerPresenter: AlternateCalendarViewControllerDelegate {
    func calendarDidChange(arrivalDate: Date, nights: Int) {}

    func calendarDidInvalidate() {}

    func calendarDidSelect(arrivalDate: Date, nights: Int) {
        BookingDetails.sharedInstance.criteria.nights = nights
        BookingDetails.sharedInstance.criteria.arrivalDate = arrivalDate

        guard let mapController = mapView as? UIViewController else { return }

        mapController.dismiss(animated: true, completion: {
            self.reloadHotels()
        })
    }
}

extension MapListContainerPresenter: SuggestionsViewControllerDelegate {
    func suggestionsViewControllerDidCancel(_ sender: SuggestionsViewController) {
        sender.dismiss(animated: true)
    }

    func suggestionsViewControllerDidPickSuggestion(_ sender: SuggestionsViewController, suggestion: Suggestion) {
        interactor?.updateSuggestion(to: suggestion)

        guard let mapController = mapView as? UIViewController else { return }

        mapController.dismiss(animated: true, completion: {
            self.reloadHotels(shouldHideHotelDetails: true)
        })
    }
}

extension MapListContainerPresenter: MapPresenterProtocol {
	var annotations: [MKAnnotation] {
		guard let interactor = interactor else { return [] }

        return interactor.suggestionAndAllVenues
	}

	var nearbyAnnotations: [MKAnnotation] {
		guard let interactor = interactor else { return [] }

		return interactor.suggestionAndVenuesInReasonableDistance
	}

    var lastSuccessfulSuggestion: Suggestion? {
        interactor?.lastSuccessfulSuggestion
    }

	func mapViewIsReady() {
		mapView?.reload()
        mapView?.fitAnnotationsInView(style: currentListStyle, animated: false)

        if UIDevice.current.userInterfaceIdiom != .pad {
            containerView?.hideCards()
        }
	}

	func mapDidPanByUser() {
        containerView?.userDidScroll()

        if let hotelToDeselect = selectedHotel {
            mapView?.deselectAnnotation(hotelToDeselect)
        }

        containerView?.hideCards()
		mapView?.showResetButton()
        mapView?.showSearchHereButton()
	}

	func didTapAnnotation(_ annotation: MKAnnotation) {
		guard let hotel = annotation as? Hotel else { return }
		guard let indexPath = indexPath(forHotel: hotel) else { return }

		selectedHotel = annotation as? Hotel

		mapView?.centerAnnotation(annotation)
        mapView?.showResetButton()
        mapView?.showSearchHereButton()

        router?.showCardsModule()
		containerView?.showCards()

        listView?.scrollToItemAt(indexPath: indexPath, andSelect: false)
		cardsView?.scrollToItemAt(indexPath: indexPath, andSelect: true)

        if UIDevice.current.userInterfaceIdiom == .pad {
            if interactor?.shouldShowFallBack() == true {
                containerView?.showFallBack()
            } else {
                router?.navigateTo(hotel: hotel, at: indexPath, suggestion: interactor?.suggestion, isMapVisible: true)
            }
        }
	}

	func listButtonDidTap() {
		containerView?.makeMapSmall()
        containerView?.toggleOptionsView(visible: true)
	}

	func resetButtonDidTap() {
        if let currentlySelectedHotel = selectedHotel {
            mapView?.deselectAnnotation(currentlySelectedHotel)
        }

		mapView?.hideResetButton()
        mapView?.hideSearchHereButton()
        mapView?.fitAnnotationsInView(style: currentListStyle, animated: true)
		containerView?.hideCards()
        listShowing = false
	}

    func searchHereButtonDidTap() {
        guard let centerCoordinate = mapView?.centerCoordinate else { return }

        let suggestion = PISuggestion(dictionary: [
            "name": (String(centerCoordinate.latitude).substringToIndex(5) + ", " + String(centerCoordinate.longitude)
                .substringToIndex(5)),
            "lat": centerCoordinate.latitude,
            "long": centerCoordinate.longitude
        ])

        performSearch(with: suggestion)
    }

    func performLastSuccessfulSearch() {
        guard let lastSuccessfulSuggestion = lastSuccessfulSuggestion else { return }

        performSearch(with: lastSuccessfulSuggestion)
    }

    func mapViewDidDeselectAnnotation() {
        selectedHotel = nil
        containerView?.hideCards()
    }
}

extension MapListContainerPresenter: ListPresenterProtocol {
    var shouldShowCoronavirusInformationBanner: Bool { interactor?.shouldShowCoronavirusInformationBanner ?? false }

    func didStartScrolling() {
        containerView?.userDidScroll()
    }

	func listViewIsReady() {
		listView?.reload()
		cardsView?.reload()

        presentErrorMessageOnListViewIfNeeded()
	}

	func numberOfVenues() -> Int {
		interactor?.venues.count ?? 0
	}

	func hotel(for index: IndexPath) -> Hotel? {
		guard let hotels = interactor?.venues as? [Hotel] else { return nil }
		guard index.row < hotels.count else { return nil }

		return hotels[index.row]
	}

	func didSelectItem(at indexPath: IndexPath, mapVisible: Bool) {
		guard let interactor = interactor else { return }
		guard let hotels = interactor.venues as? [Hotel] else { return }
		guard indexPath.row < hotels.count else { return }
        if interactor.shouldShowFallBack() {
            containerView?.showFallBack()
        } else {
            router?.navigateTo(
                hotel: hotels[indexPath.row],
                at: indexPath,
                suggestion: interactor.suggestion,
                isMapVisible: mapVisible
            )
        }
    }

	func listDidScroll(to y: CGFloat) {
        let previousOffset = currentOffset

        guard currentListStyle == .regular else { return }

        if y <= Constants.searchResultsMapListDragToPopAmount {
            containerView?.toggleOptionsView(visible: false)
            containerView?.makeMapFullScreen()
            containerView?.provideHapticFeedback()

            return
        }

        if y < 0 {
            let top = y * 0.5 * 0.75

            containerView?.updateMapConstraints(optionsViewTop: top)
        } else {
            if y < Constants.smallMapHeight + 10 {
                return
            }

            currentOffset = y
            if y > previousOffset {
                if optionsSectionIsVisible == false { return }

                optionsSectionIsVisible = false
                containerView?.toggleOptionsView(visible: false)
                containerView?.slideOptionsViewOut()

                return
            } else {
                // Scrolling up

                if optionsSectionIsVisible { return }

                optionsSectionIsVisible = true
                containerView?.slideOptionsViewIn()
                containerView?.toggleOptionsView(visible: true)

                return
            }
            // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        }
    }

	func listDidScroll(to indexPath: IndexPath) {
		guard let interactor = interactor else { return }
		guard let hotels = interactor.venues as? [Hotel] else { return }
		guard indexPath.item < hotels.count else { return }

		let hotel = hotels[indexPath.item]

		selectedHotel = hotel

		mapView?.centerAnnotation(hotel)
        mapView?.selectAnnotation(hotel)
        containerView?.showCards()
		listView?.selectCellAt(indexPath: indexPath)
		cardsView?.selectCellAt(indexPath: indexPath)
	}

    func didTapMapArea() {
        containerView?.toggleOptionsView(visible: false)
        containerView?.makeMapFullScreen()
    }

    func didTapDismissCoronavirusInformationBanner() {
        interactor?.didTapDismissCoronavirusInformationBanner()
    }
}

extension MapListContainerPresenter: VenueFullyBookedCellDelegate {
    func venueCellDidTapEditButton() {
        selectedNights()
    }
}
