//
//  BookingConfirmationRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import EventKitUI
import StoreKit
import SimpleNetwork
import PassKit
import SwiftUI

class BookingConfirmationRouter {
    weak var viewController: UIViewController?
    weak var presenter: BookingConfirmationPresenter?
    var passManager = PassManager()
}

extension BookingConfirmationRouter: BookingConfirmationRouterInput {
    func selectedHotelInfo(hotel: Hotel?) {
		guard let hotel = hotel else { return }

        let shouldShowOperaFallback = SettingsManager.sharedInstance.shouldOperaRedirectToWeb

        if shouldShowOperaFallback, let alertController = AlertManager.fallbackToWebsitePopup() {
            viewController?.navigationController?.present(alertController, animated: true)
            return
        }

        // no fallback required - continue to HDP
        let controller = HotelDetailsModule.build(
            withCode: hotel.code,
            hotelBrand: hotel.brand,
            existingAvailability: nil,
            bookingAllowed: false,
            andSuggestion: nil,
            shouldShowCheckAvailability: true
        )

		viewController?.navigationController?.pushViewController(controller, animated: true)
	}

    func priceBreakdown(hotel: Hotel?, reservation: Reservation?) {
		guard let hotel = hotel else { return }

        let controller = BookingSummaryRouter.build(
            hotel: hotel,
            bookingDetails: nil,
            reservation: reservation,
            screenName: PIAnalytics.StateNames.summaryBreakdownRB
        )
        controller.hidesBottomBarWhenPushed = true

		viewController?.navigationController?.pushViewController(controller, animated: true)
	}

    func amendAction(stay: Stay?, hotel: Hotel?) {
		guard let stay = stay else { return }

        let controller = AmendBookingRouter.build(with: stay, hotel: hotel, and: self)
        controller.hidesBottomBarWhenPushed = true
        controller.navigationItem.title = PILocalizedString("Amend Booking", comment: "")

		viewController?.navigationController?.pushViewController(controller, animated: true)
	}

    func selectedFaqs(url: URL?) {
        viewController?.openFAQExternalLink(url: url)
	}

	func addToCalendar(event: EKEvent, store: EKEventStore) {
		let controller = EKEventEditViewController()
		controller.event = event
		controller.eventStore = store
		controller.editViewDelegate = viewController as? EKEventEditViewDelegate

		viewController?.present(controller, animated: true)
	}

    func showKeyPage(stay: Stay) {
        let controller = KeyModule.createModule(stay: stay, disableBackButton: true, howYourKeyWorks: true)
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func startOTP(stay: Stay, roomId: String) {
        let controller = KeyModule.createModule(stay: stay, disableBackButton: true)
        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func openKeyInWalletDidTap(stay: Stay) {
        guard let id = stay.digitalKeyIdentifier,
              let passURL = self.passManager.fetchPassFromLibrary(passId: id)?.passURL else { return }
        UIApplication.shared.open(passURL, options: [:], completionHandler: nil)
    }

    func addToWallet(pass: PKPass) {
        guard let controller = PKAddPassesViewController(pass: pass) else { return }
        AnalyticsManager.shared.trackState(
            PIAnalytics.StateNames.appleWalletBookingConf,
            data: [
                PIAnalytics.Keys.appleWallet: true,
                PIAnalytics.Keys.bookingID: presenter?.interactor?.summary.identifier ?? ""
            ]
        )
        viewController?.present(controller, animated: true)
    }

    func showDirections(hotel: Hotel?, withSender sender: UIView) {
		guard let hotel = hotel else { return }

        viewController?.showDirectionOptions(forHotel: hotel, withSender: sender)
	}

	func bookingFlowDidFinish() {
        BookingDetails.sharedInstance.reset()

        if let tabBarController = viewController?.tabBarController, let item = (tabBarController.tabBar.items?.first {
            $0.title == PILocalizedString("tabbarItemMyBookings", comment: "Tabbar item: my bookings")
            }), let index = tabBarController.tabBar.items?.firstIndex(of: item) {
            tabBarController.selectedIndex = index
        }

        _ = viewController?.navigationController?.popToRootViewController(animated: true)
    }

	func callHotel(number: String) {
		viewController?.showCallHotelAlert(number: number)
	}

    func askForAppReview() {
        guard UserSessionManager.sharedInstance.currentUser != nil else { return }
        guard NSClassFromString("EarlGreyImpl") == nil else { return }  // UI tests

        if let scene = UIApplication.shared.connectedScenes
           .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene {
            DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(2)) {
                AppStore.requestReview(in: scene)
            }
        }
    }

    func openWeb(url: URL) {
        guard UIApplication.shared.canOpenURL(url) else { return }

        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }

    func startCheckInOnline(inputParams: PreStayInputParams) {
        let preStayViewController = PreStayModule.build(preStayInputParams: inputParams)
        viewController?.navigationController?.pushViewController(preStayViewController, animated: true)
    }

    func startCheckOutOnline(checkOutDetails: CheckOutDetails) {
        let checkOutController = CheckOutConfirmationModule
            .build(checkOutDetails: CheckOutDetails(bookerFirstName: checkOutDetails.bookerFirstName))
        viewController?.navigationController?.pushViewController(checkOutController, animated: true)
    }

    func showRoomKeyInstructions(model: InstructionsViewModel) {
        let roomKeyInstructionsController = InstructionsModule.build(model: model)
        roomKeyInstructionsController.modalPresentationStyle = .pageSheet

        if let sheet = roomKeyInstructionsController.sheetPresentationController {
			switch model.size {
			case .fullSize:
				sheet.detents = [.large()]
			case .custom(let height):
				sheet.detents = [.custom(identifier: .init("medium")) { _ in height }]
			}
        }
        viewController?.present(roomKeyInstructionsController, animated: true)
    }
}

extension BookingConfirmationRouter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true)
    }
}

extension BookingConfirmationRouter: AmendBookingRouterDelegate {
    func bookingWasAmended() {
        presenter?.interactor?.amendedStay = true

        presenter?.reloadViewModel()
        presenter?.showNotificationScrollingToTop()
    }
}
