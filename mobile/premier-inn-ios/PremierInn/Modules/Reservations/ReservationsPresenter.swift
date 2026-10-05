//
//  ReservationsPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

protocol ReservationsPresenterProtocol {
    func viewIsReady()
    func findBookingButtonDidTap()
    func bookingDetailsButtonDidTap(stay: Stay)
    func qrCodeButtonDidTap(stay: Stay)
    func connectToWifiDidTap(freeSSID: String, paidSSID: String)
    func getKeyButtonDidTap(stay: Stay)
    func planTripButtonDidTap(hotelCode: String)
    func searchHotelButtonDidTap()
    func loginButtonDidTap()
    func refreshButtonDidTap()
    func checkInOnlineDidTap(stay: Stay)
    func instructionsDidTap(stay: Stay)
	func addDigitalKeyButtonDidTap(stay: Stay)
	func showDigitalKeyButtonDidTap(stay: Stay)
}

class ReservationsPresenter {
    weak var view: ReservationsViewProtocol?
    var router: ReservationsRouterProtocol?
    var interactor: ReservationsInteractorProtocol?

    func handleChanges(result: Result<ActivePastStays>) {
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters

        view?.toggleLoadingIndicator(isLoading: false, hideTableAsWell: true)

        if UserSessionManager.sharedInstance.currentUser != nil {
            view?.showRefreshButton()
        } else {
            view?.hideRefreshButton()
        }

        switch result {
        case .success(let response):
            view?.loadViewModel(with: response)
        case .failure(let error):
            view?.showErrorMessage(
                title: PILocalizedString("reservationsEmptyListTitle", comment: "Reservations list: empty list error title"),
                message: error.localizedDescription
            )
        }
    }

    @objc private func reservationsWillChange(notification: Notification) {
        view?.toggleLoadingIndicator(isLoading: true, hideTableAsWell: true)
    }
}

extension ReservationsPresenter: ReservationsPresenterProtocol {
	func addDigitalKeyButtonDidTap(stay: Stay) {
		router?.addDigitalKeyButtonDidTap(stay: stay)
	}

	func showDigitalKeyButtonDidTap(stay: Stay) {
		do {
			try router?.showDigitalKeyButtonDidTap(stay: stay)
            interactor?.updateCiolStatus(forStay: stay, to: .walletPass)
		} catch let error as ReservationsRouterError {
			switch error {
			case .noPassFoundInWallet:
				let alertController = UIAlertController(
				    title: PILocalizedString("somethingWentWrongMessage"),
				    message: error.localizedDescription,
				    preferredStyle: .alert
				)
				let okAction = UIAlertAction(
				    title: PILocalizedString("OK", comment: "OK button title"),
				    style: .cancel
				)
				alertController.addAction(okAction)
				view?.presentAlertController(alertController)
			case .noDigitalKeyIdentifier:
				// Some analytics for later
				break
			}
		} catch {
			// Maybe some analytics later
		}
	}

    func instructionsDidTap(stay: Stay) {
        guard let model = interactor?.getRoomKeyInstructionsModel(stay: stay) else {
            self.view?.showErrorMessage(
                title: PILocalizedString("Something went wrong"),
                message: PILocalizedString("kioskPassAppleWalletFailed")
            )
            return
        }
        router?.showRoomKeyInstructions(model: model)
    }

    func viewIsReady() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(reservationsWillChange),
            name: .staysWillChange,
            object: nil
        )
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters

        view?.toggleLoadingIndicator(isLoading: true, hideTableAsWell: true)

        interactor?.listenToReservationChanges { [weak self] result in
            DispatchQueue.main.async {
                self?.view?.toggleLoadingIndicator(isLoading: true, hideTableAsWell: true)
                self?.handleChanges(result: result)
            }
        }

        interactor?.refreshStays { _ in }
    }

    func refreshButtonDidTap() {
        view?.toggleLoadingIndicator(isLoading: true, hideTableAsWell: true)

        interactor?.refreshStays { response in
            if response == false {
                DispatchQueue.main.async {
                    self.view?.toggleLoadingIndicator(isLoading: false, hideTableAsWell: true)
                    self.view?.showErrorMessage(
                        title: PILocalizedString("reservationsGetStaysErrorTitle"),
                        message: PILocalizedString("reservationsGetStaysErrorMessage")
                    )
                }
            }
        }
    }

    func findBookingButtonDidTap() {
        router?.showFindBooking()
    }

    func bookingDetailsButtonDidTap(stay: Stay) {
        self.router?.showBookingDetails(with: stay)
    }

    func qrCodeButtonDidTap(stay: Stay) {
        AnalyticsManager.shared.trackAction(
            PIAnalytics.Action.qrCodeShown,
            userInfo: [PIAnalytics.Keys.qrCodeCid: CampaignId.qrCode.rawValue]
        )

        self.router?.showQRCode(with: stay)
    }

    func connectToWifiDidTap(freeSSID: String, paidSSID: String) {
        AnalyticsManager.shared.log(
            event: FirebaseAnalytics.Event.wifiConnectionCTA,
            parameters: ["freeSSID": freeSSID, "paidSSID": paidSSID]
        )

        let alertController = UIAlertController(
            title: PILocalizedString("wifiConnectAlertTitle"),
            message: PILocalizedString("wifiConnectAlertMessage"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(title: freeSSID, style: .default) { _ in
            HotSpotManager.connectToWifi(ssid: freeSSID)
        })
        alertController.addAction(UIAlertAction(title: paidSSID, style: .default) { _ in
            HotSpotManager.connectToWifi(ssid: paidSSID)
        })
        alertController.addAction(UIAlertAction(title: PILocalizedString("Cancel"), style: .cancel))

        view?.presentAlertController(alertController)
    }

    func getKeyButtonDidTap(stay: Stay) {
        guard let router = self.router, let arrivalDate = stay.arrivalDate else { return }

        let reservationDetails = ReservationDetails(
            reservationId: stay.identifier,
            surname: stay.importName ?? stay.lastName,
            arrivalDate: arrivalDate,
            business: stay.isBusinessTrip,
            token: nil
        )

        router.showKeyDetails(reservationDetails: reservationDetails)
    }

    func planTripButtonDidTap(hotelCode: String) {
        router?.showPlanYourTrip(hotelCode: hotelCode)
    }

    func searchHotelButtonDidTap() {
        router?.showSearchHotel()
    }

    func loginButtonDidTap() {
        router?.showLogin()
    }

    func checkInOnlineDidTap(stay: Stay) {
        interactor?.stayToBeCheckedId = stay

        // at this point we are only showing the disclaimer, shouldn't we track when they actually start the flow? or even when the flow starts
        trackStartCiol()
        AppsFlyerManager.sharedInstance.trackStartCiol(reference: stay.identifier, hotelCode: stay.hotelCode)

        let informationImage = CiolInformationImage(type: .named(UIImage(named: "QRCodePILogo")))
        let viewModel = CiolInformationModel(
            image: informationImage,
            title: PILocalizedString("ciolHeadsUp"),
            subtitle: PILocalizedString("ciolOnlinecheckIn"),
            showSubtitle: true,
            description: .init(type: .string(PILocalizedString("ciolCheckInDisclaimer"))),
            showCTA: true,
            ctaTitle: PILocalizedString("ciolUnderstood"),
            delegate: self
        )
        router?.showCiolDisclaimer(viewModel: viewModel)
    }

    private func trackStartCiol() {
        var info = PIDictionary()
        info[PIAnalytics.Keys.checkInOnline] = true
        info[PIAnalytics.Keys.checkInOnlineBookingID] = interactor?.stayToBeCheckedId?.identifier
        info[PIAnalytics.Keys.productString] = ";\(interactor?.stayToBeCheckedId?.hotelCode ?? "")"
        info[PIAnalytics.Keys.checkInOnlineRateCode] = interactor?.stayToBeCheckedId?.rateClassification
        info[PIAnalytics.Keys.checkInOnlineCheckInDate] = interactor?.stayToBeCheckedId?.arrivalDate?.analyticsDateFormat
        info[PIAnalytics.Keys.checkInOnlineCheckOutDate] = interactor?.stayToBeCheckedId?.checkOutDate?.analyticsDateFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInDay] = interactor?.stayToBeCheckedId?.arrivalDate?.analyticsDayFormat
        info[PIAnalytics.Keys.checkInOnlineCheckOutDay] = interactor?.stayToBeCheckedId?.checkOutDate?.analyticsDayFormat
        info[PIAnalytics.Keys.checkInOnlineCheckInOutDay] = "\(interactor?.stayToBeCheckedId?.arrivalDate?.analyticsDayFormat ?? "")-\(interactor?.stayToBeCheckedId?.checkOutDate?.analyticsDayFormat ?? "")"
        info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.StateNames.startCiol
        info[PIAnalytics.Keys.environment] = interactor?.environment
        info[PIAnalytics.Keys.userLogin] = interactor?.loggedIn
        info[PIAnalytics.Keys.timeZone] = interactor?.timeZone
        info[PIAnalytics.Keys.language] = interactor?.language
        info[PIAnalytics.Keys.screenType] = PIAnalytics.StateTypes.ciolFlow
        info[PIAnalytics.Keys.userID] = AnalyticsManager.shared.userID
        info[PIAnalytics.Keys.time] = Date().analyticsTimeFormat
        info[PIAnalytics.Keys.pushToken] = AdobeCampaignManager.shared.apnsTokenString


        if let hotelCountryItem = CountryItem(country: interactor?.stayToBeCheckedId?.hotelCountry),
           hotelCountryItem.isGerman {
            info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.Action.ciolDeRegCard
        }

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.startCiol, data: info)
    }
}

extension ReservationsPresenter: CiolInformationDelegate {
    func ctaAction(ciolInformationType: CiolInformationType) {
        view?.toggleLoadingIndicator(isLoading: true, hideTableAsWell: false)
        interactor?.startCheckInOnline { [weak self] isSuccessful, preStayInputParams in
            guard let self = self else { return }

            view?.toggleLoadingIndicator(isLoading: false, hideTableAsWell: false)
            guard isSuccessful, let preStayInputParams = preStayInputParams else {
                view?.showErrorMessage(
                    title: PILocalizedString("somethingWentWrongMessage"),
                    message: PILocalizedString("ciolCheckErrorMessage")
                )
                return
            }
            router?.startCheckInOnline(preStayInputParams: preStayInputParams)
        }
    }
}
