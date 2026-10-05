//
//  HomePresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

protocol HomePresenterInput: AnyObject {
    var view: HomeView? { get set }
    var interactor: HomeInteractorInput? { get set }
    var router: HomeRouterInput? { get set }

    func refreshView()
    func selected(suggestion: Suggestion)
    func selected(criteria: Criteria)
    func foundLocation(suggestion: Suggestion)
}

class HomePresenter: NSObject {
    var view: HomeView?
    var interactor: HomeInteractorInput?
    var router: HomeRouterInput?
    var manager: SimpleStorageManager<Stay>
    var adobeTrackingCode: String?
    var adobeTrackingCodeUpdated: (() -> Void)?
    private var pendingCiolStay: Stay?

    private let notificationCenter: NotificationCenter

    deinit {
        notificationCenter.removeObserver(self)
    }

    init(notificationCenter: NotificationCenter = .default) {
        manager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        self.notificationCenter = notificationCenter
        super.init()

        notificationCenter.addObserver(
            self,
            selector: #selector(appShortcutDidChange),
            name: .appShortcutDidChange,
            object: nil
        )
        notificationCenter.addObserver(
            self,
            selector: #selector(userDidChange),
            name: .userDidChange,
            object: nil
        )
    }

    private func showAdditionalPopoversIfAny() {
        guard handleSavedAppShortcut() == false else { return }

        if let announcement = interactor?.announcementMessage {
            router?.showAnnouncementMessage(with: announcement)
        } else if SettingsManager.sharedInstance.hasSeenAppIncentive == false {
            showPromotionPopover()
        }
    }

    private func handleSavedAppShortcut() -> Bool {
        guard hasAcceptedGDPRChanges,
              LinkHandler.sharedInstance.activeAppShortcut != nil else { return false }

        return appShortcutDidChange(notification: nil)
    }

    private func handleAdobeTrackingCode(_ trackingCode: String?) {
        if let trackingCode,
           trackingCode.isNotEmpty {
            adobeTrackingCode = trackingCode
        }
        adobeTrackingCodeUpdated?()

        LinkHandler.sharedInstance.activeAppShortcut = nil
    }

    @objc func appShortcutDidChange(notification: NSNotification?) -> Bool {
        guard let appShortcut = notification?.object as? AppShortcut ?? LinkHandler.sharedInstance.activeAppShortcut else {
            return false
        }
        let trackingCode = notification?.userInfo?[PushNotificationConfig.Keys.trackingCode] as? String

        handleAdobeTrackingCode(trackingCode)

        guard let router else { return false }

        router.resetNavigation()

        switch appShortcut {
        case .banner(let notificationsMessage):
            router.showAnnouncementMessage(with: notificationsMessage)

        case .hotelNearMe:
            router.searchNearMe()

        case .search(let criteria, let searchTerm):
            let suggestion = PISuggestion(title: searchTerm ?? "")
            let criteria = criteria ?? BookingDetails.sharedInstance.criteria

            interactor?.suggestion = suggestion
            interactor?.criteria = criteria
            view?.viewModel = interactor?.viewModel
            view?.setProcessing(is: true)
            interactor?.search(with: suggestion, and: criteria, completion: handleSearchResult)
        case .searchPlaceId(let criteria, let searchTerm, let placeId):
            var dictionary = PIDictionary()
            dictionary["suggestion"] = searchTerm ?? ""
            dictionary["placeId"] = placeId ?? ""

            guard let suggestion = PISuggestion(placeDictionary: dictionary) else { return false }
            let criteria = criteria ?? BookingDetails.sharedInstance.criteria

            interactor?.suggestion = suggestion
            interactor?.criteria = criteria
            view?.viewModel = interactor?.viewModel
            view?.setProcessing(is: true)
            interactor?.search(with: suggestion, and: criteria, completion: handleSearchResult)
        case .hotelDetails(let hotelCode, let hotelBrand, let criteria):
            handleHotelDetailsDeepLink(hotelCode: hotelCode, hotelBrand: hotelBrand, criteria: criteria)

        case .hotelsNearLocation(let suggestion, let criteria):
            interactor?.suggestion = suggestion
            interactor?.criteria = criteria ?? BookingDetails.sharedInstance.criteria
            view?.viewModel = interactor?.viewModel
            view?.setProcessing(is: true)
            interactor?.search(with: suggestion, and: BookingDetails.sharedInstance.criteria, completion: handleSearchResult)

        case .reservationDetails(let identifier):
            router.showBookingDetails(identifier: identifier)

        case .landingScreen:
            router.showHome()

        case .employeeRates:
            guard UserDefaults.standard.value(forKey: Constants.employeeRatesKey) == nil else { return false }

            view?.showEmployeeRatesConfirmationAlert()
        case .hotelDetailsBySlug(let slug, let criteria):
            fetchAndHandleHotelInformation(slug: slug, criteria: criteria)
        case .ciol(let arrivalDate, let reservationNumber, let lastName):
            guard let arrivalDate, let reservationNumber else {
                router.showHome()
                return false
            }

            if let matchedBooking = manager.items.upcomingStays.first(where: { $0.identifier == reservationNumber }) {
                self.router?.showBookingDetails(identifier: matchedBooking.identifier)
                return false
            }

            router.showFindReservation(arrivalDate: arrivalDate, reservationNumber: reservationNumber, lastName: lastName)
        case .ciolPush(let reservationNumber):

            router.showBookingDetails(identifier: reservationNumber)
        case .promotion:
            SettingsManager.sharedInstance.isAppIncentiveEnabled = true

            showPromotionPopover()

        case .freeBreakfast:
            SettingsManager.sharedInstance.isFreeBreakfastEnabled = true
            // NO NEED TO SHOW THE POPOVER FOR FREE BREAKFAST (AFTER REQUIREMENT UPDATE)
        }

        return true
    }

    @objc func userDidChange(notification: Notification) {
        let criteria = BookingDetails.sharedInstance.criteria

        let user = UserSessionManager.sharedInstance.currentUser

        // don't change any criteria during the booking flow
        if BookingDetails.sharedInstance.isBookingHold == false {
            if let adults = user?.bookingPreference?.roomRequirements?.adults {
                criteria.rooms.first?.adults = adults
            }

            if let children = user?.bookingPreference?.roomRequirements?.children {
                criteria.rooms.first?.children = children
            }

            if let cotRequired = user?.bookingPreference?.roomRequirements?.cotRequired {
                criteria.rooms.first?.cotRequired = cotRequired
            }

            // OMEGA HACK
            if let roomRequirements = user?.bookingPreference?.roomRequirements,
               let roomType = roomRequirements.type {
                let availableTypes = roomType.availableTypes(
                    adults: roomRequirements.adults,
                    children: roomRequirements.children,
                    cot: roomRequirements.cotRequired
                )

                if availableTypes.contains(roomType) == false {
                    criteria.rooms.first?.type = availableTypes.first ?? .double
                } else {
                    criteria.rooms.first?.type = roomType
                }
            }
        }

        interactor?.criteria = criteria

        refreshView()
    }

    func fetchAndHandleHotelInformation(slug: String, criteria: Criteria?) {
        interactor?.fetchHotelInformation(slug: slug) { [weak self] result in
            switch result {
            case .success(let hotelDetails):
                self?.handleHotelDetailsDeepLink(
                    hotelCode: hotelDetails.code,
                    hotelBrand: hotelDetails.brand,
                    criteria: criteria
                )
            case .failure:
                return
            }
        }
    }

    private func handleSearchResult(_ result: Result<UIViewController>) {
        view?.setProcessing(is: false)

        switch result {
        case .success(let viewController):

            if let alertController = viewController as? UIAlertController {
                view?.navigationController?.present(alertController, animated: true)
            } else {
                view?.navigationController?.pushViewController(viewController, animated: true)
            }
        case .failure(let error):
            refreshView()

            if BARTDowntimeHandler.canHandle(error: error, withParentNavigationController: view?.navigationController) {
                return
            }

            view?.showErrorAlertWith(
                title: PILocalizedString("hotelSearchAlertErrorTitle", comment: "Hotel search alert error title"),
                error: error
            )
        }
    }

    func handleHotelDetailsDeepLink(hotelCode: String, hotelBrand: HotelBrand, criteria: Criteria?) {
        let suggestion = interactor?.suggestion.remove()

        interactor?.criteria = criteria ?? BookingDetails.sharedInstance.criteria
        view?.viewModel = interactor?.viewModel
        view?.setProcessing(is: true)

        if let criteria = criteria {
            interactor?
                .searchAvailability(hotelCode: hotelCode, hotelBrand: hotelBrand, criteria: criteria) { [weak self] result in
                switch result {
                case .success(let availabilityResponse):
                    self?.router?.showHotelDetails(
                        hotelCode: hotelCode,
                        hotelBrand: hotelBrand,
                        availabilityResponse: availabilityResponse,
                        and: suggestion
                    )
                case .failure:
                    self?.view?.setProcessing(is: false)
                }
            }
        } else {
            router?.showDatelessHotelDetailsDeepLink(hotelCode: hotelCode, hotelBrand: hotelBrand)
        }
    }

    private func checkShouldShowBBCardExpired(completion: @escaping () -> Void) {
        guard interactor?.shouldShowBBCardExpiredAlert == true else {
            completion()
            return
        }

        interactor?.hasShownBBCardExpiredMessage = true
        router?.showBBCentralCardExpired(with: PILocalizedString("hotelDetailsBBCardExpiredMessage"), completion: completion)
    }

    private func showPromotionPopover() {
        guard SettingsManager.sharedInstance.isAppIncentiveAvailable else { return }

        AnalyticsManager.shared.trackState(
            PIAnalytics.StateNames.appIncentivePopover,
            data: [PIAnalytics.Keys.popoverPromoCode: SettingsManager.sharedInstance
                                           .appIncentivePromoCode]
        )
        router?.showPromotionPopover()
        SettingsManager.sharedInstance.hasSeenAppIncentive = true
    }
}

extension HomePresenter: HomePresenterInput {
    func refreshView() {
        view?.viewModel = interactor?.viewModel
        view?.setProcessing(is: false)
    }

    func selected(suggestion: Suggestion) {
        interactor?.suggestion = suggestion
        refreshView()
    }

    func selected(criteria: Criteria) {
        interactor?.criteria = criteria
        refreshView()
    }

    func foundLocation(suggestion: Suggestion) {
        interactor?.suggestion = suggestion
        refreshView()

        selectedSearch()
    }
}

extension HomePresenter: HomeViewEventHandler {
    var currentCriteria: Criteria? { interactor?.criteria }
    var hasAcceptedGDPRChanges: Bool { interactor?.hasAcceptedGDPR ?? false }

    func viewIsReady() {
        refreshView()

        showAdditionalPopoversIfAny()
    }

    func selectedLocation(sender: UIView) {
        checkShouldShowBBCardExpired(completion: {
            self.router?.selectedLocation(sender: sender)
        })
    }

    func selectedNights(sender: UIView) {
        checkShouldShowBBCardExpired(completion: {
            self.router?.selectedNights(sender: sender)
        })
    }

    func selectedGuests(sender: UIView) {
        checkShouldShowBBCardExpired(completion: {
            self.router?.selectedGuests(sender: sender)
        })
    }

    func selectedSearch() {
        checkShouldShowBBCardExpired(completion: {
            guard let suggestion = self.interactor?.suggestion else {
                NotificationFeedbackManager.shared.provideFeedback(for: .success)
                self.router?.searchNearMe()
                return
            }
            guard let criteria = self.interactor?.criteria else {
                NotificationFeedbackManager.shared.provideFeedback(for: .error)
                return
            }

            NotificationFeedbackManager.shared.provideFeedback(for: .success)
            self.view?.setProcessing(is: true)

            self.interactor?.search(with: suggestion, and: criteria, completion: self.handleSearchResult)
        })
    }

    func navigateToSRP(with suggestion: Suggestion) {
        guard let criteria = interactor?.criteria else {
            NotificationFeedbackManager.shared.provideFeedback(for: .error)
            return
        }

        NotificationFeedbackManager.shared.provideFeedback(for: .success)
        view?.setProcessing(is: true)

        interactor?.search(with: suggestion, and: criteria, completion: handleSearchResult)
    }

    func showGDPRInfo() {
        router?.showGDPRInfo()
    }

    func update(with criteria: Criteria) {
        interactor?.criteria = criteria
        refreshView()
    }

    func selectedRecentSearch(at index: Int) {
        guard interactor?.shouldShowBBRecentSearchError(forRecentSearchAt: index) == false else {
            guard let messageModel = interactor?.bbRecentSearchErrorViewModel, let view = view else {
                return
            }

            view.showAlertWith(title: messageModel.title, message: messageModel.message)
            return
        }

        guard interactor?.shouldShowEmployeeRateSearchError(forRecentSearchAt: index) == false else {
            guard let messageModel = interactor?.employeeRecentSearchErrorViewModel, let view = view else {
                return
            }

            view.showAlertWith(title: messageModel.title, message: messageModel.message)
            return
        }

        checkShouldShowBBCardExpired(completion: {
            self.view?.setProcessing(is: true)
            self.interactor?.search(withRecentSearchAt: index, completion: self.handleSearchResult)
        })
    }

    func showBooking(with identifier: String) {
        router?.showBookingDetails(identifier: identifier)
    }

    func showCheckInOnline(with identifier: String) {
        guard let stay = manager.items.first(where: { $0.identifier == identifier }) else { return }
        guard let viewController = view as? UIViewController else { return }

        pendingCiolStay = stay

        let informationImage = CiolInformationImage(type: .named(UIImage(named: "QRCodePILogo")))
        let model = CiolInformationModel(
           image: informationImage,
           title: PILocalizedString("ciolHeadsUp"),
           subtitle: PILocalizedString("ciolOnlinecheckIn"),
           showSubtitle: true,
           description: .init(type: .string(PILocalizedString("ciolCheckInDisclaimer"))),
           showCTA: true,
           ctaTitle: PILocalizedString("ciolUnderstood"),
           delegate: self
        )

        let ciolBottomSheetAnalyticsInfo = CiolBottomSheetAnalyticsInfo(
           screenNameForViewUnderneath: nil
        )
        displayCIOLInformation(
           model: model,
           ciolInformationType: .checkIn,
           ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
           view: viewController,
           completion: { [weak self] in
               self?.pendingCiolStay = nil
           }
        )
    }

    func showAmendBooking(with identifier: String) {
        router?.showAmendBooking(identifier: identifier)
    }

    func dismissCoronavirusInformationBannerTapped() {
        interactor?.userDismissedCoronavirusInformationBanner()
    }

    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails) {
        router?.showCalendarAndHotel(with: dashboardHotelDetails)
    }
}

extension HomePresenter: CanDisplayCIOLInformation, CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        guard let stay = pendingCiolStay,
              let router = router,
              let interactor = interactor else {
            return
        }

        pendingCiolStay = nil
        view?.toggleProcessing(isProcessing: true)

        interactor.startCheckInOnline(for: stay) { [weak self] isSuccessful, preStayInputParams in
            guard isSuccessful, let preStayInputParams else {
                self?.view?.toggleProcessing(isProcessing: false)
                self?.view?.showAlertMessage(
                    title: PILocalizedString("somethingWentWrongMessage"),
                    message: PILocalizedString("ciolCheckErrorMessage")
                )
                return
            }

            router.startCheckInOnline(preStayInputParams: preStayInputParams) {
                self?.view?.toggleProcessing(isProcessing: false)
            }
        }
    }
}
