//
//  ReviewAndBookPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka
import Foundation

protocol ReviewAndBookPresenterProtocol {
    func viewIsReady()
    func editUpsellsButtonDidTap()
    func editGuestButtonDidTap()
    func summaryButtonDidTap()
    func editAdditionalInformationButtonDidTap()
    func confirmButtonDidTap()
    func noMoreAvailabilityButtonDidTap()
    func cancelRateUpdateButtonDidTap()
    func paymentFailureButtonDidTap()
}

protocol ThreeCiPageProtocol {
    func dismiss()
}

class ReviewAndBookPresenter {
    weak var view: ReviewAndBookViewProtocol?
    var router: ReviewAndBookRouterProtocol?
    var interactor: ReviewAndBookInteractorProtocol?

    var pollingController: PollingController?

    private var failureCount = 0
    private var shouldAttemptAnotherAvailabilityCheck = true
    private var maxRetryAttemptReached: Bool {
        failureCount > Constants.Config.paymentRetriesBeforeAllowQuit
    }
    private var paymentMethod: PaymentMethodType?

    private let loadingDispatchGroup = DispatchGroup()
    private var guestDetailsCallFailed: Bool = false

    private var loadingError: Error?

    private var storedBookingConfirmationResult: Result<BookingConfirmation>?

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    func handleBookingResult(confirmation: BookingConfirmation, confirmationPollingFinished: Bool) {
        // we need to poll the basket status before we are certain that the booking is complete
        if confirmationPollingFinished == false {
            // TODO: This scenario is when it is Opera, as we do not call handleBookingResult() anymore for the booking flow, this scenario seems to be only with the error handling from 3DS. We need to remove these use cases.
            return
        }

        handleBookingSuccess(confirmation: confirmation)
    }

    func handleBookingSuccess(confirmation: BookingConfirmation) {
        self.interactor?.trackBookingConfirmation(confirmation: confirmation)

        completedBooking(confirmation: confirmation)
    }

    private func completedBooking(confirmation: BookingConfirmation) {
        // store a value to know that a booking was made by this device
        SettingsManager.sharedInstance.hasMadeAppBooking = true

        if let email = interactor?.bookingDetails.booker?.emailAddress {
            bookingNotification(withEmail: email, paymentOption: interactor?.bookingDetails.paymentOption ?? .later)
        }

        do {
            guard let stay = try interactor?.stay(with: confirmation) else {
                popToRootAndResetBookingDetails()
                return
            }

            interactor?.saveStayToLocalStore(summary: stay)

            if let hotelName = interactor?.bookingDetails.hotel?.name, let criteria = interactor?.bookingDetails.criteria {
                let recentSearchManager = RecentSearchesManager(dataSource: UserDefaults.standard)
                recentSearchManager.remove(searchWith: hotelName, criteria: criteria)
            }

            view?.provideSuccessFeedback()
            router?.showBookingConfirmation(with: stay)

            interactor?.resetBookingDetails()
        } catch {
            popToRootAndResetBookingDetails()
        }
    }

    private func handlePaymentFailure(error: Error) {
        failureCount += 1

        view?.stopDisplayingLoadingElements()

        if let cccError = error as? ReviewAndBookCCCPaymentError {
            handleCCCPaymentFailure(with: cccError)
            return
        }
    }

    func handleCCCPaymentFailure(with error: Error) {
        view?.stopDisplayingLoadingElements()

        switch error {
        case ReviewAndBookCCCPaymentError.maintenanceMode:
            router?.showBartError()

        case ReviewAndBookCCCPaymentError.cnpPasswordIncorrect:
            if view?.scrollToAtosPasswordRow() == false {
                view?.showError(
                    title: PILocalizedString("timeoutErrorTitle", comment: "Timeout error title"),
                    message: error.localizedDescription
                )
            }

        case ReviewAndBookCCCPaymentError.errorUI(let ui):
            view?.showErrorUI(ui)

        case PollingError.errorUI(let ui):

            interactor?.trackConfirmationPollingBookingStatusFailed()
            interactor?.trackBookingFailure_3CP(error: ui)
            view?.showErrorUI(ui)

        case ReviewAndBookCCCPaymentError.genericError:
            view?.showGenericErrorAlert(
                title: PILocalizedString("paymentFailureCardErrorTitle", comment: "Payment failure: card error title"),
                message: error.localizedDescription,
                shouldShowFailureButton: self.maxRetryAttemptReached == true
            )

        case PollingError.bookingConfirmationFailed:

            interactor?.trackConfirmationPollingBookingStatusFailed()
            interactor?.trackBookingFailure(error: error)

            view?.showPollingFinishedError(
                title: PILocalizedString("Something went wrong"),
                message: PILocalizedString("bookingConfirmationFailedStatus"),
                shouldShowFailureButton: true,
                accessibilityButtonLabel: PILocalizedString("returnToHomePageAccessibilityLabel")
            )

        case PollingError.maxAttempts:
            // change this to be more customised for this scenario
            interactor?.trackConfirmationPollingReachedMaxAttemptsFailed()
            interactor?.trackBookingFailure(error: error)

            view?.showPollingFinishedError(
                title: PILocalizedString("Something went wrong"),
                message: String.localizedStringWithFormat(
                    PILocalizedString("paymentPollingReachedMaxAttemptsMessage"),
                    interactor?.bookingDetails.booker?.emailAddress ?? PILocalizedString("your email address")
                ),
                shouldShowFailureButton: true,
                accessibilityButtonLabel: PILocalizedString("returnToHomePageAccessibilityLabel")
            )
        case ReviewAndBookCCCPaymentError.stopCCCPPaymentProccess:
            view?.stopDisplayingLoadingElements()

        case PollingError.open:
            interactor?.trackBookingFailure(error: error)
            view?.showGenericErrorAlert(
                title: PILocalizedString("paymentFailureCardErrorTitle", comment: "Payment failure: card error title"),
                message: PILocalizedString("bookingGenericError"),
                shouldShowFailureButton: self.maxRetryAttemptReached == true
            )
        default:
            interactor?.trackBookingFailure(error: error)

            view?.showGenericErrorAlert(
                title: PILocalizedString("paymentFailureCardErrorTitle", comment: "Payment failure: card error title"),
                message: error.localizedDescription,
                shouldShowFailureButton: self.maxRetryAttemptReached == true
            )
        }
    }

    private func handleUpdateAvailabilityFailure(error: Error) {
        view?.stopDisplayingLoadingElements()

        switch error as? UpdateAvailabilityError {
        case .noMoreAvailability?:
            view?.showNoMoreAvailabilityMessage(
                title: PILocalizedString("noMoreAvailabilityAlertTitle", comment: "No more availability alert title"),
                message: PILocalizedString("noMoreAvailabilityAlertMessage", comment: "No more availability alert message")
            )

        case .maintenanceMode?:
            router?.showBartError()

        default:
            view?.showError(
                title: PILocalizedString("noMoreAvailabilityAlertTitle", comment: "No more availability alert title"),
                message: error.localizedDescription
            )
        }
    }

    private func handleHoldFailure(error: Error) {
        switch error as? HoldError {
        case .maintenanceMode?:
            router?.showBartError()

        default:
            view?.showError(
                title: PILocalizedString("reviewAlertTitle", comment: "Review and Book: alert title"),
                message: error.localizedDescription
            )
        }
    }

    private func bookingNotification(withEmail emailAddress: String, paymentOption: PaymentIntervalOption) {
        var body: String {
            switch paymentOption {
            case .later, .rwc:
                return String.localizedStringWithFormat(PILocalizedString("paymentSuccessPayLaterMessage"), emailAddress)
            case .now:
                return String.localizedStringWithFormat(PILocalizedString("paymentSuccessPayNowMessage"), emailAddress)
            }
        }

        NotificationManager.shared.showLocalNotification(
            withTitle: PILocalizedString("paymentSuccessTitle"),
            body: body
        )
    }

    private func handleUpdateAvailabilityResponse(result: Result<Bool>) {
        switch result {
        case .success:
            confirmButtonDidTap()
        case .failure(let error):
            handleUpdateAvailabilityFailure(error: error)
        }
    }

    private func popToRootAndResetBookingDetails() {
        interactor?.resetBookingDetails()
        router?.popToRoot()
    }

    @objc private func guestsDidChange() {
        loadRemoteData(refresh: true)
    }

    @objc private func businessQuestionsDidChange(notification: NSNotification) {
        guard let bookingDetails = interactor?.bookingDetails else { return }
        guard let qa = notification.object as? [BusinessCardQuestionAndAnswer] else { return }

        bookingDetails.businessCardQuestionsAndAnswers = qa
        // A change in business questions does not need a reload of remote data this means we do not show the spinner, we just need to update the view model.
        view?.loadViewModel(with: bookingDetails)
    }

    @objc private func creditCardDidChange() {
        loadRemoteData(refresh: true)
    }

    private func cccPaymentFlow(with values: PIDictionary, paypalNonce: String? = nil, paypalDeviceData: String? = nil) {
        do {
            try self.interactor?
                .startCccPayment(
                    with: values,
                    paypalNonce: paypalNonce,
                    paypalDeviceData: paypalDeviceData
                ) { [weak self] result in
                guard let self = self else { return }

                switch result {
                case .success(let paymentResponse):

                    pollingController = PollingController(delegate: self)

                    // for flows that skip the iframe e.g Reserve Without a Card or PayPal
                    if paymentResponse.status == .notRequired {
                        startPolling(sender: nil, transactionID: "")
                        return
                    }

                    setupThreeCIpage(for: paymentResponse)

                case .failure(let error):
                    handlePaymentFailure(error: error)
                }
            }
        } catch {
            self.view?.stopDisplayingLoadingElements()
            self.view?.showError(title: PILocalizedString("paymentProcessingError"), message: error.localizedDescription)
        }
    }

    private func setupThreeCIpage(for response: CCCPPaymentResponse) {
        do {
            let threeCiPageParams = try self.threeCIpageParams(for: response)
            interactor?.setWalletTypeSelected(nil)

            self.router?.processPayment(with: threeCiPageParams, using: self, walletAnalyticsDelegate: self, and: self)
        } catch {
            failureCount += 1
            view?.stopDisplayingLoadingElements()
            self.view?.showError(title: PILocalizedString("paymentProcessingError"), message: error.localizedDescription)
        }
    }

    private func threeCIpageParams(for response: CCCPPaymentResponse) throws -> ThreeCiPageParams {
        guard let htmlString = response.paymentRequiredDetails?.htmlString else { throw CCCPaymentError.missingProviderUrl }
        guard let paymentMethod = BookingDetails.sharedInstance.primaryPaymentMethod
            else { throw CCCPaymentError.noPaymentMethod }

        var trackingParams = response.trackingParams
        if let cardCode = BookingDetails.sharedInstance.primaryPaymentMethod?.card?.type.cardCode {
            trackingParams[PIAnalytics.Keys.cccCardSelected] = cardCode
        }
        if let paymentType = BookingDetails.sharedInstance.primaryPaymentMethod?.type {
            trackingParams[PIAnalytics.Keys.cccPaymentMethodType] = paymentType
        }

        let bookingDetails = BookingDetails.sharedInstance
        trackingParams[PIAnalytics.Keys.bfUserType] = bookingDetails.bookingMode == .leisure ? "Leisure" : "Business"
        trackingParams[PIAnalytics.Keys.cccPaymentTakenNow] = bookingDetails.paymentOption == .now
        trackingParams[PIAnalytics.Keys.productString] = bookingDetails.trackingProductString

        let allowedEvents = allowedEventsFor(paymentMethod: paymentMethod)

        return ThreeCiPageParams(
            html: htmlString,
            trackingParams: trackingParams,
            allowedEvents: allowedEvents
        )
    }

    private func allowedEventsFor(paymentMethod: PaymentOption) -> [ThreeCEvent]? {
        if paymentMethod.paymentMethodType == .applePay {
            return [.applePaySelected, .googlePaySelected]
        }

        return nil
    }
}

extension ReviewAndBookPresenter: PollingDelegate {
    func startConfirmationPolling() {
        view?.startConfirmationPolling()
    }

    func stopConfirmationPolling() {
        view?.stopConfirmationPolling()
    }

    func checkBasketStatus(
        transactionID: String,
        completion: @escaping (SimpleNetwork.Result<SimpleNetwork.BookingConfirmation>) -> Void
    ) {
        interactor?.checkBasketStatus(completion: completion)
    }
}

extension ReviewAndBookPresenter: ReviewAndBookPresenterProtocol {
    func viewIsReady() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(guestsDidChange),
            name: .guestsDidChange,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(businessQuestionsDidChange),
            name: .businessQuestionsDidChange,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(creditCardDidChange),
            name: .creditCardDidChange,
            object: nil
        )

        if let cccPaymentOptionTrackingParams = interactor?.cccPaymentOptionTrackingParams {
            view?.trackPaymentOptionsState(with: cccPaymentOptionTrackingParams)
        }

        loadRemoteData(refresh: false)
    }

    private func loadRemoteData(refresh: Bool) {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        view?.startDisplayingLoadingElements()
        AccessibilityManager.announce(PILocalizedString("loadingAnnouncement"))

        if refresh == false {
            if bookingDetails.bookingMode == .business {
                holdBookingWithGuests()
            }
            loadPaymentMethods()
        }

        loadTotalCostWithCityTax()

        loadingDispatchGroup.notify(queue: .main) {
            guard self.guestDetailsCallFailed == false else { return }
            self.view?.stopDisplayingLoadingElements()

            if self.loadingError != nil {
                // TODO: show this error and go back?
                return
            }

            self.view?.loadViewModel(with: bookingDetails)
        }
    }

    private func holdBookingWithGuests() {
        DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup.enter()
        interactor?.holdBookingWithGuests(completion: { _, error in
            if let error = error {
                self.guestDetailsCallFailed = true
                self.view?.showErrorMessage(
                    title: PILocalizedString("somethingWentWrongAlertTitle"),
                    message: PILocalizedString("guestDetailsErrorMessage"),
                    error: error,
                    handler: { _ in
                        self.router?.goBack()
                    }
                )
            }
            DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup.leave()
        })
    }

    private func loadPaymentMethods() {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        loadingDispatchGroup.enter()

        DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup.notify(queue: .main) {
            guard self.guestDetailsCallFailed == false else {
                self.loadingDispatchGroup.leave()
                return
            }
            self.interactor?.getPaymentMethods { result in
                switch result {
                case .success(let paymentMethodsResponse):

                    bookingDetails.paymentMethods = paymentMethodsResponse.paymentMethods?
                        .sorted(by: { $0.order < $1.order })
                    bookingDetails.primaryPaymentMethod = bookingDetails.paymentMethods?
                        .first(where: { $0.enabled })

                case .failure(let error):

                    self.loadingError = error
                }

                self.loadingDispatchGroup.leave()
            }
        }
    }

    private func loadTotalCostWithCityTax() {
        loadingDispatchGroup.enter()

        DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup.notify(queue: .main) {
            guard self.guestDetailsCallFailed == false else {
                self.loadingDispatchGroup.leave()
                return
            }
            self.interactor?.getTotalCostWithCityTax { error in
                if let error = error {
                    self.loadingError = error
                }

                self.loadingDispatchGroup.leave()
            }
        }
    }

    func editUpsellsButtonDidTap() {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        router?.showEditUpsells(bookingDetails: bookingDetails)
    }

    func editGuestButtonDidTap() {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        router?.showEditGuest(bookingDetails: bookingDetails)
    }

    func summaryButtonDidTap() {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        router?.showSummary(bookingDetails: bookingDetails)
    }

    func editAdditionalInformationButtonDidTap() {
        guard let qA = interactor?.businessCardQuestionsAndAnswers else { return }

        router?.showEditBusinessCardQuestions(questionsAndAnswers: qA)
    }

    func confirmButtonDidTap() {
        view?.stopEditing()
        view?.startDisplayingLoadingElements()

        do {
            guard let values = try view?.validateForm() else { throw ReservationDetailsError.missingFormValues }
            let paymentMethod = interactor?.bookingDetails.primaryPaymentMethod?.paymentMethodType
            switch paymentMethod {
            case .paypal:
                self.paypalVaultFlow(values: values)
                return
            case .newCreditDebitCard:
                // Use Datatrans SDK for new card payments
                guard let basketId = interactor?.bookingDetails.basketReference else {
                    return
                }
                router?.startDatatransPayment(basketId: basketId)
                view?.stopDisplayingLoadingElements()
            default:
                self.interactor?.appendValuesToBookingDetails(values: values)
                cccPaymentFlow(with: values)
                return
            }
        } catch let error as RowValidatorError {
            view?.stopDisplayingLoadingElements()
            view?.showRowError(error)
        } catch {
            view?.stopDisplayingLoadingElements()
            view?.showError(
                title: PILocalizedString("reviewAlertTitle", comment: "Review and Book: alert title"),
                message: error.localizedDescription
            )
        }
    }

    func noMoreAvailabilityButtonDidTap() {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        router?.goBackToSearchForAvailabilityController(hotel: bookingDetails.hotel)
    }

    func paymentFailureButtonDidTap() {
        guard let bookingDetails = interactor?.bookingDetails else { return }

        router?.goBackToSearchForAvailabilityController(hotel: bookingDetails.hotel)
    }

    func cancelRateUpdateButtonDidTap() {
        popToRootAndResetBookingDetails()
    }
}

extension ReviewAndBookPresenter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true) {
            self.view?.stopDisplayingLoadingElements()
        }
    }
}

extension ReviewAndBookPresenter: ThreeCiPageDelegate {
    func startPolling(sender: ThreeCiPageViewController?, transactionID: String) {
        sender?.dismiss()
        pollingController?.startPollingForBasketComplete()
    }

    func finishedAuth(cardType: String?) {
        interactor?.setCccCardType(cardType)
    }
}

// MARK: Wallet Analytics tracking

extension ReviewAndBookPresenter: WalletAnalyticsDelegate {
    func appleWalletSelected() {
        interactor?.setWalletTypeSelected(PIAnalytics.WalletType.applePay)
    }

    func googleWalletSelected() {
        interactor?.setWalletTypeSelected(PIAnalytics.WalletType.googlePay)
    }
}

extension ReviewAndBookPresenter {
    func paypalVaultFlow(values: PIDictionary) {
        interactor?.startPaypalVault(completion: { nonce, paypalDeviceData, error in
            if let paypalNonce = nonce {
                return self.cccPaymentFlow(with: values, paypalNonce: paypalNonce, paypalDeviceData: paypalDeviceData)
            } else {
                self.view?.stopDisplayingLoadingElements()

                // If the user has cancelled the paypal journey themselves, then do not show an error pop up
                guard (error as? NSError)?.code != Constants.paypalUserCancelledErrorCode else { return }
                // In any instance of a paypal error we want to just show the generic text
                self.view?.showError(
                    title: PILocalizedString("reviewAlertTitle", comment: "Review and Book: alert title"),
                    message: PayPalError.genericError.localizedDescription
                )
            }
        })
    }
}
