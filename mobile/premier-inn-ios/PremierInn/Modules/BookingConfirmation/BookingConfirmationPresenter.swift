//
//  BookingConfirmationPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import PassKit

class BookingConfirmationPresenter {
    weak var view: BookingConfirmationViewInput?
    var interactor: BookingConfirmationInteractorInput?
    var router: BookingConfirmationRouterInput?

    func reloadViewModel() {
        interactor?.reloadStay()

        guard let viewModel = self.interactor?.bookingConfirmationViewModel else { return }

        self.view?.update(with: viewModel)
    }
}

extension BookingConfirmationPresenter: BookingConfirmationPresenterInput, CanDisplayCIOLInformation {
    func viewIsReady() {
        if let interactor = interactor, interactor.isBookingFlowComplete {
            let shouldEnableGestureSwipe = !interactor.isBookingFlowComplete
            view?.disableBackNavigation(shouldEnableGestureSwipe: shouldEnableGestureSwipe)
        }

        view?.setScreenTitle(title: PILocalizedString("bookingConfirmationScreenTitle"))

        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters

        guard interactor?.bookingDoesNotNeedToRefresh != true else {
            interactor?.bookingDoesNotNeedToRefresh = false
            return
        }

        view?.showLoadingIndicator()

        interactor?.fetchHotel()
    }

    func hotelFetched() {
        self.interactor?.loadLatestDetails(completion: { (success) in
            self.interactor?.isOutOfDate = !success
            self.view?.hideLoadingIndicator()
            self.reloadViewModel()
        })
    }

    func hotelFetchError(error: Error?) {
        self.view?.hideLoadingIndicator()

        if let error = error {
            self.view?.showError(
                title: PILocalizedString(
                    "bookingConfirmationLoadingErrorTitle",
                    comment: "Booking confirmation loading error title"
                ),
                message: error.localizedDescription,
                shouldDie: true
            )
        }

        self.reloadViewModel()
    }

    func hotelInfoDidTap() {
        router?.selectedHotelInfo(hotel: interactor?.hotel)
    }

    func priceBreakdownDidTap() {
        interactor?.bookingDoesNotNeedToRefresh = true
        router?.priceBreakdown(hotel: interactor?.hotel, reservation: interactor?.reservation)
    }

    func amendDidTap() {
        guard let interactor,
              let router else { return }

        router.amendAction(stay: interactor.summary, hotel: interactor.hotel)
    }

    func faqDidTap(url: URL?) {
        router?.selectedFaqs(url: url)
    }

    func parkingInfoDidTap() {
        view?.deselectRow(animated: false)
        guard let parkingDescription = interactor?.hotel?.parkingDescription else {
            return
        }

        let informationImage = CiolInformationImage(type: .named(UIImage(named: "QRCodePILogo")))
        let ciolBottomSheetAnalyticsInfo = CiolBottomSheetAnalyticsInfo(
            screenNameForViewUnderneath: nil
        )
        displayCIOLInformation(
            model: CiolInformationModel(
                image: informationImage,
                title: PILocalizedString("parkingAtThisHotel"),
                showSubtitle: false,
                description: .init(type: .string(parkingDescription)),
                showCTA: false,
                delegate: self
            ),
            ciolInformationType: .checkIn,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            view: view,
            completion: nil
        )
    }

    func instructionsDidTap() {
        guard let model = interactor?.roomKeyInstructionsModel else {
            view?.showError(
                title: PILocalizedString("Something went wrong"),
                message: PILocalizedString("kioskPassAppleWalletFailed"),
                shouldDie: false
            )
            return
        }
        view?.deselectRow(animated: false)
        router?.showRoomKeyInstructions(model: model)
    }

    func addToCalendar() {
        interactor?.fetchCalendarEvent(calendarPrompt: { fullAccess, writeOnly, cancel in
            self.view?.showCalendarPrompt(fullAccessAction: fullAccess, writeOnlyAction: writeOnly, cancelAction: cancel)
        }, completion: { (event, store, error) in
            if let error = error {
                if let permissionDenied = error as? EventPermissionsError {
                    if permissionDenied == .permissionDenied {
                        self.accessPreviouslyDenied()
                        return
                    } else if permissionDenied == .userDidNotGrantAccess {
                        return
                    }
                }

                self.view?.showError(
                    title: PILocalizedString(
                        "calendarEventCreationFailureTitle",
                        comment: "Calendar event creation failure title"
                    ),
                    message: error.localizedDescription,
                    shouldDie: false
                )
                return
            }

            guard let event = event else { return }
            guard let store = store else { return }

            self.router?.addToCalendar(event: event, store: store)
        })
    }

    func addToWallet() {
        AnalyticsManager.shared.trackAction(PIAnalytics.Action.addToWalletTap, userInfo: nil)

        view?.showLoadingIndicator()

        interactor?.fetchWalletPass { [weak self] state in
            defer {
                self?.view?.hideLoadingIndicator()
            }

            switch state {
            case .containsPass:
                self?.passExistsAlready()

            case .fetchSuccessful(let pass):
                self?.passFetchSuccessful(with: pass)

            case .fetchFailed(let error):
                self?.passFetchFailed(with: error)

            case .noReservationDetails:
                self?.view?.showError(
                    title: PILocalizedString("Something went wrong"),
                    message: nil,
                    shouldDie: false
                )
            }
        }
    }

    func accessPreviouslyDenied() {
        view?.promptUserToEnableCalendarAccess()
    }

    func callHotelButtonDidTap() {
        // Post booking: we don't charge for extra calls so we use national phone number
        let number = interactor?.hotel?.nationalPhoneNumber ?? PILocalizedString("telephoneNumber", comment: "")

        router?.callHotel(number: number)
    }

    func redirectToWeb() {
        guard let url = Constants.premierInnBaseURL else { return }
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }

    func cancelNavigationButtonDidTap() {
        router?.bookingFlowDidFinish()

        router?.askForAppReview()
    }

    func showDirections(withSender sender: UIView) {
        router?.showDirections(hotel: interactor?.hotel, withSender: sender)
    }

    func passFetchSuccessful(with pass: PKPass?) {
        guard let pass = pass else { return }

        self.router?.addToWallet(pass: pass)
    }

    func openExistingWalletDidTap() {
        guard let passUrl = interactor?.pass?.passURL else { return }

        UIApplication.shared.open(passUrl, options: [:], completionHandler: nil)
    }

    func passFetchFailed(with error: Error?) {
        if let error = error {
            if let permissionDenied = error as? EventPermissionsError {
                if permissionDenied == .permissionDenied {
                    self.accessPreviouslyDenied()
                    return
                } else if permissionDenied == .userDidNotGrantAccess {
                    return
                }
            }

            self.view?.showError(
                title: PILocalizedString("walletPassCreationFailureTitle", comment: "Wallet pass creation failure title"),
                message: error.localizedDescription,
                shouldDie: false
            )
        }
    }

    func passExistsAlready() {
        self.view?.showError(
            title: PILocalizedString("walletPassCreationFailureTitle", comment: "Wallet pass creation failure title"),
            message: PILocalizedString("walletPassExistsMessage", comment: "Wallet pass exists message"),
            shouldDie: false
        )
    }

    func showNotificationScrollingToTop() {
        self.view?.scrollToTop()
    }

    func tapOnCheckIn() {
        guard let stay = interactor?.summary else { return }

        // at this point we are only showing the disclaimer, shouldn't we track when they actually start the flow? or even when the flow starts
        trackStartCiol()
        AppsFlyerManager.sharedInstance.trackStartCiol(reference: stay.identifier, hotelCode: stay.hotelCode)

        let informationImage = CiolInformationImage(type: .named(UIImage(named: "QRCodePILogo")))
        let ciolBottomSheetAnalyticsInfo = CiolBottomSheetAnalyticsInfo(
            screenNameForViewUnderneath: nil
        )
        displayCIOLInformation(
            model: CiolInformationModel(
                image: informationImage,
                title: PILocalizedString("ciolHeadsUp"),
                subtitle: PILocalizedString("ciolOnlinecheckIn"),
                showSubtitle: true,
                description: .init(type: .string(PILocalizedString("ciolCheckInDisclaimer"))),
                showCTA: true,
                ctaTitle: PILocalizedString("ciolUnderstood"),
                delegate: self
            ),
            ciolInformationType: .checkIn,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            view: view,
            completion: nil
        )
    }

    func tapOnCheckOut() {
        let informationImage = CiolInformationImage(type: .named(UIImage(named: "QRCodePILogo")))
        let ciolBottomSheetAnalyticsInfo = CiolBottomSheetAnalyticsInfo(
            screenNameForViewUnderneath: nil
        )
        displayCIOLInformation(
            model: CiolInformationModel(
                image: informationImage,
                title: PILocalizedString("ciolCheckOutHeadsUpTitle"),
                showSubtitle: false,
                description: .init(type: .string(PILocalizedString("ciolCheckOutHeadsUpDescription"))),
                showCTA: true,
                ctaTitle: PILocalizedString("ciolCheckOutHeadsUpCTATitle"),
                delegate: self
            ),
            ciolInformationType: .checkOut,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            view: view,
            completion: nil
        )
    }

    func addKeyToWalletTap() {
        guard let stay = interactor?.summary, let roomId = interactor?.reservation?.rooms.first?.roomId else { return }
        self.router?.startOTP(stay: stay, roomId: roomId)
    }

    func viewKeyInWallet() {
        guard let stay = interactor?.summary else { return }
        self.router?.openKeyInWalletDidTap(stay: stay)
        interactor?.updateCiolStatus(to: .walletPass)
    }

    func howYourKeyWorksDidTap() {
        guard let stay = interactor?.summary else { return }
        self.router?.showKeyPage(stay: stay)
    }

    private func trackStartCiol() {
        var info = PIDictionary()
        info[PIAnalytics.Keys.checkInOnline] = true
        info[PIAnalytics.Keys.checkInOnlineBookingID] = interactor?.summary.identifier
        info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.StateNames.startCiol
        info[PIAnalytics.Keys.productString] = ";\(interactor?.hotel?.code ?? "")"
        info[PIAnalytics.Keys.checkInOnlineRateCode] = interactor?.summary.rateClassification
        info[PIAnalytics.Keys.environment] = interactor?.environment
        info[PIAnalytics.Keys.userLogin] = interactor?.loggedIn
        info[PIAnalytics.Keys.timeZone] = interactor?.timeZone
        info[PIAnalytics.Keys.language] = interactor?.language
        info[PIAnalytics.Keys.screenType] = PIAnalytics.StateTypes.ciolFlow
        info[PIAnalytics.Keys.userID] = AnalyticsManager.shared.userID
        info[PIAnalytics.Keys.time] = Date().analyticsTimeFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInDate] = interactor?.summary.arrivalDate?.analyticsDateFormat
        info[PIAnalytics.Keys.checkInOnlineCheckOutDate] = interactor?.summary.checkOutDate?.analyticsDateFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInDay] = interactor?.summary.arrivalDate?.analyticsDayFormat
        info[PIAnalytics.Keys.checkInOnlineCheckOutDay] = interactor?.summary.checkOutDate?.analyticsDayFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInOutDay] = "\(interactor?.summary.arrivalDate?.analyticsDayFormat ?? "")-\(interactor?.summary.checkOutDate?.analyticsDayFormat ?? "")"
        info[PIAnalytics.Keys.pushToken] = AdobeCampaignManager.shared.apnsTokenString

        if interactor?.preStayInputParams?.hotelBrand == .premierInnGermany {
            info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.Action.ciolDeRegCard
        }

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.startCiol, data: info)
    }

    private func trackCheckOutAnalytics(isSuccess: Bool) {
        var info = PIDictionary()
        info[PIAnalytics.Keys.checkInOnline] = true
        info[PIAnalytics.Keys.checkInOnlineBookingID] = interactor?.summary.identifier
        info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.Action.checkOut
        info[PIAnalytics.Keys.productString] = ";\(interactor?.hotel?.code ?? "")"
        info[PIAnalytics.Keys.checkInOnlineRateCode] = interactor?.summary.rateClassification
        info[PIAnalytics.Keys.environment] = interactor?.environment
        info[PIAnalytics.Keys.userLogin] = interactor?.loggedIn
        info[PIAnalytics.Keys.timeZone] = interactor?.timeZone
        info[PIAnalytics.Keys.language] = interactor?.language
        info[PIAnalytics.Keys.screenType] = PIAnalytics.StateTypes.ciolFlow
        info[PIAnalytics.Keys.userID] = AnalyticsManager.shared.userID
        info[PIAnalytics.Keys.time] = Date().analyticsTimeFormat

        if isSuccess {
            info[PIAnalytics.Keys.checkInOnlineCheckInDate] = interactor?.summary.arrivalDate?.analyticsDateFormat
            info[PIAnalytics.Keys.checkInOnlineCheckOutDate] = interactor?.summary.checkOutDate?.analyticsDateFormat
            info[PIAnalytics.Keys.checkInOnlineCheckInDay] = interactor?.summary.arrivalDate?.analyticsDayFormat
            info[PIAnalytics.Keys.checkInOnlineCheckOutDay] = interactor?.summary.checkOutDate?.analyticsDayFormat
            info[PIAnalytics.Keys.checkInOnlineCheckInOutDay] = "\(interactor?.summary.arrivalDate?.analyticsDayFormat ?? "")-\(interactor?.summary.checkOutDate?.analyticsDayFormat ?? "")"
            info[PIAnalytics.Keys.hotelCode] = interactor?.preStayInputParams?.hotelCode
            info[PIAnalytics.Keys.bookingID] = interactor?.preStayInputParams?.bookingReference
            info[PIAnalytics.Keys.nights] = interactor?.summary.numberOfNights
            info[PIAnalytics.Keys.rooms] = interactor?.summary.numberOfRoooms
            info[PIAnalytics.Keys.rateCode] = interactor?.preStayInputParams?.rateCode
            info[PIAnalytics.Keys.rateName] = interactor?.preStayInputParams?.rateDescription
            info[PIAnalytics.Keys.adults] = interactor?.preStayInputParams?.adultsCount
            info[PIAnalytics.Keys.children] = interactor?.preStayInputParams?.childrenCount
        } else {
            info[PIAnalytics.Keys.errorMessage] = "Something went wrong"
        }

        if interactor?.preStayInputParams?.hotelBrand == .premierInnGermany {
            info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.Action.ciolDeRegCard
        }
        AnalyticsManager.shared.trackAction(PIAnalytics.Action.checkOut, userInfo: info)
    }

    func resendInvoiceDidTap() {
        let title = PILocalizedString("bookingConfirmationResendInvoiceAlertTitle")
        let message = PILocalizedString("bookingConfirmationResendInvoiceAlertText")
        let buttonTitle = PILocalizedString("bookingConfirmationResendInvoiceAlertResendButton")

        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: PILocalizedString("alertCancelButton"), style: .cancel, handler: nil))
        alertController.addAction(UIAlertAction(title: buttonTitle, style: .default, handler: { _ in
            DispatchQueue.main.async {
                self.view?.showLoadingIndicator()
            }
            self.interactor?.resendInvoice {
                DispatchQueue.main.async {
                    self.view?.hideLoadingIndicator()
                }
                self.reloadViewModel()
            }
        }))

        view?.presentAlert(alertController)
    }
}

extension BookingConfirmationPresenter: CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        // If preStayInputParams is not set yet, wait for loadLatestDetails to complete
        guard let inputParams = interactor?.preStayInputParams else {
            view?.showLoadingIndicator()
            interactor?.loadLatestDetails { [weak self] (success: Bool) in
                DispatchQueue.main.async {
                    self?.view?.hideLoadingIndicator()
                    guard success, let inputParams = self?.interactor?.preStayInputParams else {
                        self?.view?.showError(
                            title: PILocalizedString("somethingWentWrongMessage"),
                            message: PILocalizedString("ciolCheckErrorMessage"),
                            shouldDie: false
                        )
                        return
                    }
                    self?.startCheckInFlow(inputParams: inputParams, ciolInformationType: ciolInformationType)
                }
            }
            return
        }
        startCheckInFlow(inputParams: inputParams, ciolInformationType: ciolInformationType)
    }

    private func startCheckInFlow(inputParams: PreStayInputParams, ciolInformationType: CiolInformationType) {
        switch ciolInformationType {
        case .checkIn:
            interactor?.updateCiolStatus(to: .ciolStarted)
            router?.startCheckInOnline(inputParams: inputParams)
        case .checkOut:
            interactor?.performOnlineCheckout { [weak self] success in
                self?.trackCheckOutAnalytics(isSuccess: success)
                guard success else {
                    self?.view?.showError(
                        title: PILocalizedString("somethingWentWrongMessage"),
                        message: PILocalizedString("ciolCheckErrorMessage"),
                        shouldDie: false
                    )
                    return
                }
                self?.router?
                    .startCheckOutOnline(checkOutDetails: CheckOutDetails(bookerFirstName: inputParams
                    .leadBookerFirstName ?? ""))
            }
        default:
            return
        }
    }
}

// MARK: - Private Helper Functions

private extension BookingConfirmationPresenter {
    func showAlert(
        title: String,
        message: String,
        buttonTitle: String,
        buttonHandler: @escaping () -> Void
    ) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(
            UIAlertAction(
                title: PILocalizedString(buttonTitle),
                style: .cancel,
                handler: { _ in
                    buttonHandler()
                }
            )
        )
        view?.presentAlert(alertController)
    }

    func showPopupAmendIsUnavailableForPromotionBooking() {
        showAlert(
            title: PILocalizedString("promotionalBookingUnavailableTitle"),
            message: PILocalizedString("promotionalBookingUnavailableMessage"),
            buttonTitle: PILocalizedString("promotionalBookingUnavailableButtonText"),
            buttonHandler: { self.view?.deselectRow(animated: true) }
        )
    }
}
