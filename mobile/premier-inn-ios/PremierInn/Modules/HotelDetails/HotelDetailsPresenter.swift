//
//  HotelDetailsPresenter.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

class HotelDetailsPresenter {
    var view: HotelDetailsViewProtocol?
    var interactor: HotelDetailsInteractorProtocol
    var router: HotelDetailsRouterProtocol
    private var showCalendar: Bool

    init(
        with interactor: HotelDetailsInteractorProtocol,
        andRouter router: HotelDetailsRouterProtocol,
        showCalendarFirst: Bool
    ) {
        self.interactor = interactor
        self.router = router
        self.showCalendar = showCalendarFirst
    }
}

extension HotelDetailsPresenter: HotelDetailsPresenterProtocol {
    var screenName: String {
        interactor.screenName
    }

    var screenType: String {
        interactor.screenType
    }

    var shouldTrackScreen: Bool {
        interactor.shouldTrackScreen
    }

    var hotelDetailsViewModel: HotelDetailsViewModel? {
        nil
    }

    var continueViewCanShowAtBottom: Bool {
        interactor.bookingAllowed
    }

    func hotelUpdated() {
        view?.stopDisplayingLoadingElements()
        view?.updateViewModelTo(interactor.viewModel)
    }

    func hotelUpdateFailed(with error: Error) {
        view?.stopDisplayingLoadingElements()
        view?.hotelUpdateFailed(with: error)
    }

    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int) {
        interactor.datesChanged(withNewArrivalDate: newArrivalDate, andNumberOfNights: numberOfNights)
        view?.dismissCurrentOverlay()
        view?.startDisplayingLoadingElements()
        interactor.loadAvailability(datesChanged: true)
    }

    func criteriaWasUpdated(to newCriteria: Criteria) {
        interactor.criteriaChanged(to: newCriteria)
        view?.dismissCurrentOverlay()
        view?.startDisplayingLoadingElements()
        interactor.loadAvailability(datesChanged: false)
    }

    func showLoadingIndicator() {
        view?.startDisplayingLoadingElements()
    }

    func hideLoadingIndicator() {
        view?.stopDisplayingLoadingElements()
    }

    func closeCurrentOverlay() {
        view?.dismissCurrentOverlay()
    }
}

extension HotelDetailsPresenter: HotelDetailsEventHandler {
    func viewIsAppearing() {
        view?.startDisplayingLoadingElements()
        interactor.loadAvailability(datesChanged: false)

        if showCalendar {
            showCalendarDidTap()
        }
    }

    func viewDidFinishLoading() { }

    func roomSectionIndexDidChange(to imageTag: ImageTag) {
        guard interactor.hotel?.brand == .hub else { return }
        guard let desiredRoomImageIndex = interactor.hotel?.indexOfRoomImage(with: imageTag.rawValue) else { return }

        view?.updateRoomImage(withDesiredRoomIndex: desiredRoomImageIndex)
    }

    func hotelMoreInformationButtonDidTap() {
        guard let hotel = interactor.hotel else { return }

        let controller = AdditionalInfoModule.build(with: hotel, and: .hotelNotes)

        DispatchQueue.main.async { [weak self] in
            self?.view?.present(UINavigationController(rootViewController: controller))
        }
    }

    func hotelFacilitiesButtonDidTap() {
        guard let hotel = interactor.hotel else { return }

        let controller = AdditionalInfoModule.build(with: hotel, and: .facilities)

        view?.present(UINavigationController(rootViewController: controller))
    }

    func locationInformationButtonDidTap() {
        guard let hotel = interactor.hotel else { return }

        let controller = AdditionalInfoModule.build(with: hotel, and: .hotelLocation)

        view?.present(UINavigationController(rootViewController: controller))
    }

    func mapPreviewDidTap() {
        guard let hotel = interactor.hotel else { return }

        let controller = MapDetailModule.build(with: hotel, hotelCode: nil, referencePoint: interactor.suggestion)

        view?.push(controller)
    }

    func directionsButtonDidTap() {
        guard let viewModel = interactor.directionsViewModel else { return }

        view?.showDirectionsScreen(with: viewModel)
    }

    func backButtonDidTap() {
        router.goBack(withCriteriaToCheck: interactor.criteria)
    }

    func openPrivacyPolicy() {
        view?.openPrivacyPolicy()
    }

    func callHotelDidTap() {
        guard let phoneNumber = interactor.phoneNumber else { return }
        view?.callHotel(withPhoneNumber: phoneNumber)
    }

    func emailButtonDidTap() {
        view?.emailCustomerService()
    }

    func showCalendarDidTap() {
        router.showCalendar(
            withArrivalDate: interactor.arrivalDate,
            andNumberOfNights: interactor.numberOfNights
        )
    }

    func showGuestsAndRoomsDidTap() {
        router.showGuestsAndRooms()
    }

    func showCarousel(index: Int, andDelegate delegate: FullScreenImageViewerRouterDelegate) {
        guard let roundelDesigns = interactor.roundelDesigns else { return }

        guard let controller = FullScreenImageViewerModule.build(
            roundelDesigns: roundelDesigns,
            startIndex: index,
            and: delegate
        ) else { return }

        let navigationController = UINavigationController(rootViewController: controller)
        navigationController.setNavigationBarHidden(true, animated: false)

        view?.present(navigationController)
    }

    func checkAvailabilityButtonTapped() {
        interactor.updateBookingAllowed(to: true)
    }

    func scrollToRateSection(rateSection: RateSection) {
        view?.scrollTo(rateSection: rateSection)
    }

    func selectedRate(withRateID rateID: UUID, lettingType: String?) {
        interactor.validateUserCanBookRate(with: rateID) { [weak self] result in
            switch result {
            case .success:
                self?.callExistingSelectedRate(rateID: rateID, lettingType: lettingType)

            case .failure(let error as SelectRateError):
                let alertContent = error.alertContent
                self?.router.showBlockerAlert(
                    withTitle: alertContent.title,
                    body: alertContent.message,
                    andEmailSubject: alertContent.emailSubject
                )

            case .failure(let error):
                self?.router.showBlockerAlert(
                    withTitle: PILocalizedString("paymentMethodsGenericError"),
                    body: error.localizedDescription,
                    andEmailSubject: nil
                )
            }
        }
    }

    func holdBooking(rateId: UUID, lettingType: String?) {
        let rooms = interactor.roomsForRateID(rateId, and: lettingType)
        let shouldShowCotWarning = self.interactor.viewModel?.shouldShowCotNotAvailableMessageSection ?? false
        BookingDetails.sharedInstance.shouldRequestCot = !shouldShowCotWarning

        let shouldShowRoomSelection = rooms.contains(where: { $0.isAccessibleRoom || $0.isNewTwinRoom })

        interactor.setCriteria(withRateID: rateId, and: lettingType)

        if !shouldShowRoomSelection {
            self.showLoadingIndicator()
            interactor.holdBooking(withRateID: rateId) { response in
                self.hideLoadingIndicator()
                guard response == true else {
                    self.showHoldBookingError()
                    return
                }
                self.router.selectedRate(
                    withRooms: rooms,
                    accessibleRoomImages: self.interactor.accessibleRoomImages ?? [],
                    twinRoomImages: self.interactor.twinRoomImages ?? [],
                    withUpsells: self.interactor.hasUpsells,
                    andShouldShowRoomSelectionScreen: shouldShowRoomSelection
                )
            }
        } else {
            self.router.selectedRate(
                withRooms: rooms,
                accessibleRoomImages: self.interactor.accessibleRoomImages ?? [],
                twinRoomImages: self.interactor.twinRoomImages ?? [],
                withUpsells: self.interactor.hasUpsells,
                andShouldShowRoomSelectionScreen: shouldShowRoomSelection
            )
        }
    }

    private func callExistingSelectedRate(rateID: UUID, lettingType: String?) {
        // add auth get cvv check here
        if interactor.shouldShowYouNeedToObtainCVVInformation {
            // show message then do the thing
            DispatchGroupManager.sharedInstance.availabilityDispatchGroup.notify(queue: .main) {
                self.view?.showAlert(
                    with: PILocalizedString("hotelDetailsCVVRequiredBBTitle"),
                    message: PILocalizedString("hotelDetailsCVVRequiredBBMessage"),
                    confirmTitle: PILocalizedString("hotelDetailsCVVRequiredBBConfirmTitle"),
                    cancelTitle: PILocalizedString("hotelDetailsCVVRequiredBBCancelTitle"),
                    confirmAction: {
                        // do the thing
                        self.holdBooking(rateId: rateID, lettingType: lettingType)
                    }
                )
            }
        } else if interactor.shouldShowEmployeeOfferInformation(for: rateID) {
            DispatchGroupManager.sharedInstance.availabilityDispatchGroup.notify(queue: .main) {
                self.view?.showAlert(
                    with: PILocalizedString("hotelDetailsEmployeeOfferTitle"),
                    message: PILocalizedString("hotelDetailsEmployeeOfferMessage"),
                    confirmTitle: PILocalizedString("hotelDetailsEmployeeOfferConfirmTitle"),
                    cancelTitle: PILocalizedString("hotelDetailsEmployeeOfferCancelTitle"),
                    confirmAction: {
                        // do the thing
                        self.holdBooking(rateId: rateID, lettingType: lettingType)
                    }
                )
            }
        } else {
            // do the thing
            DispatchGroupManager.sharedInstance.availabilityDispatchGroup.notify(queue: .main) {
                self.holdBooking(rateId: rateID, lettingType: lettingType)
            }
        }
    }

    func showHotelsNearbyButtonTapped() {
        router.goBackToResultsScreenIfPossible()
    }

    func showHoldBookingError() {
        router.showHoldRateError()
    }

    func selectedShowDisabledAccess() {
        router.showDisabledAccess()
    }

    func showOurRoomsTapped(lettingType: String?) {
        guard let hotel = interactor.hotel else { return }

        if hotel.brand != .zip {
            router.showOurRooms(with: hotel, lettingType: lettingType)
        } else {
            // let rateSection = RateSection.rateSection(for: lettingType)

            // view?.scrollTo(rateSection: rateSection)
        }
    }

    func parkingInfoDidTap() {
        guard let hotel = interactor.hotel else { return }

        let controller = AdditionalInfoModule.build(with: hotel, and: .hotelParking)

        view?.present(UINavigationController(rootViewController: controller))
    }

    func tripAdvisorRatingTapped() {
        view?.scrollToTripAdvisorSection()
    }

    func dismissCoronavirusInformationBannerTapped() {
        interactor.customerDismissedCoronavirusInformation()
    }

    func logoutButtonDidTap() {
        interactor.userLoggedOut()
        router.goBackToHomeScreen()
    }
}

// MARK: - Discount Codes

extension HotelDetailsPresenter {
    func discountCodeButtonDidTap() {
        let viewController: UIViewController

        if let viewModel = interactor.discountCodeViewModel {
            viewController = HotelDetailsDiscountCodeModule.build(viewModel: viewModel)
        } else {
            let viewModel = HotelDetailsDiscountCodeViewModel(onDismiss: didTapDismissDiscountModal)
            interactor.discountCodeViewModel = viewModel
            viewController = HotelDetailsDiscountCodeModule.build(viewModel: viewModel)
        }

        DispatchQueue.main.async { [weak self] in
            self?.view?.present(viewController)
        }
    }

    private func didTapDismissDiscountModal() {
        view?.updateViewModelTo(interactor.viewModel)
        interactor.applyUserEnteredDiscountCodeIfNeeded()
    }
}
