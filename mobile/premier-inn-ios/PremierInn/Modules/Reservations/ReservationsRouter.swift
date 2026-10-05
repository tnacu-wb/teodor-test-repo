//
//  ReservationsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import SwiftUI

protocol ReservationsRouterProtocol {
    func showFindBooking()
    func showBookingDetails(with: Stay)
    func showQRCode(with: Stay)
    func showKeyDetails(reservationDetails: ReservationDetails)
    func showPlanYourTrip(hotelCode: String)
    func showSearchHotel()
    func showLogin()
    func showCiolDisclaimer(viewModel: CiolInformationModel)
    func startCheckInOnline(preStayInputParams: PreStayInputParams)
    func showRoomKeyInstructions(model: InstructionsViewModel)
	func addDigitalKeyButtonDidTap(stay: Stay)
	func showDigitalKeyButtonDidTap(stay: Stay) throws
}

class ReservationsRouter {
    weak var viewController: UIViewController?

    static func build() -> UIViewController {
        let controller = ReservationsListViewController()
        controller.presenter = {
            let interactor = ReservationsInteractor()

            let router = ReservationsRouter()
            router.viewController = controller

            let presenter = ReservationsPresenter()
            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            return presenter
        }()

        return controller
    }
}
enum ReservationsRouterError: LocalizedError {
	case noDigitalKeyIdentifier
	case noPassFoundInWallet

	var errorDescription: String? {
		switch self {
		case .noDigitalKeyIdentifier:
			return "No digital key identifier found" // Not user facing error. Used for analytics.
		case .noPassFoundInWallet:
			return PILocalizedString("noPassInWallet")
		}
	}
}

extension ReservationsRouter: ReservationsRouterProtocol {
    func showFindBooking() {
        let controller = FindReservationRouter.build()
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

	func addDigitalKeyButtonDidTap(stay: Stay) {
		let controller = KeyModule.createModule(stay: stay, disableBackButton: true)
		controller.hidesBottomBarWhenPushed = true
		viewController?.navigationController?.pushViewController(controller, animated: true)
	}

	func showDigitalKeyButtonDidTap(stay: Stay) throws {
		guard let id = stay.digitalKeyIdentifier else {
			throw ReservationsRouterError.noDigitalKeyIdentifier
		}
		guard let passURL = PassManager().fetchPassFromLibrary(passId: id)?.passURL else {
			throw ReservationsRouterError.noPassFoundInWallet
		}
		UIApplication.shared.open(passURL, options: [:], completionHandler: nil)
	}

    func showKeyDetails(reservationDetails: ReservationDetails) {
        viewController?.navigationController?.pushViewController(
        	DoorKeyModule.build(reservationDetails: reservationDetails),
        	animated: true
        )
    }

    func showBookingDetails(with stay: Stay) {
        let controller = BookingConfirmationModule.build(summary: stay, isBookingFlowEnd: false)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func showQRCode(with stay: Stay) {
        let viewModel = KioskViewModel(stay: stay)
        let view = KioskPassView(viewModel: viewModel)

        let controller = UIHostingController(rootView: view)
        controller.title = viewModel.title
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func showPlanYourTrip(hotelCode: String) {
        let controller = MapDetailModule.build(with: nil, hotelCode: hotelCode, referencePoint: nil, and: .journeyPlanner)
        controller.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(controller, animated: true)
    }

    func showSearchHotel() {
        viewController?.tabBarController?.selectedIndex = 0
    }

    func showLogin() {
        guard let viewController = viewController else { return }
        let userBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)

        LoginRouter().presentLoginInterface(
        	from: viewController,
        	asBusinessLogin: userBusiness,
        	comingFromSplashScreen: false,
        	andIsFromBookingFlow: false
        )
    }

    func showCiolDisclaimer(viewModel: CiolInformationModel) {
        let ciolBottomSheetAnalyticsInfo = CiolBottomSheetAnalyticsInfo(
            screenNameForViewUnderneath: nil
        )
        let informationViewController = CiolInformationModule.build(
            model: viewModel,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            completion: nil
        )
        viewController?.tabBarController?.present(informationViewController, animated: true)
    }

    func startCheckInOnline(preStayInputParams: PreStayInputParams) {
        let preStayViewController = PreStayModule.build(preStayInputParams: preStayInputParams)
        preStayViewController.hidesBottomBarWhenPushed = true

        viewController?.navigationController?.pushViewController(preStayViewController, animated: true)
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
        viewController?.tabBarController?.present(roomKeyInstructionsController, animated: true)
    }
}

extension ReservationsRouter: WebViewControllerDelegate {
    func webViewControllerDidCancel(sender: WebViewController) {
        sender.dismiss(animated: true)
    }
}
