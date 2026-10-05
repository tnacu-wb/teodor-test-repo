//
//  CiolConfirmationModule.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum CiolConfirmationModule {
    static func build(ciolConfirmationDetails: CiolConfirmationDetails) -> UIViewController {
        let viewController = CiolConfirmationViewController()
        let presenter = CiolConfirmationPresenter()
        let interactor = CiolConfirmationInteractor(ciolConfirmationDetails: ciolConfirmationDetails)
        let router = CiolConfirmationRouter(ciolFlow: ciolConfirmationDetails.ciolStartFlow)

        viewController.presenter = presenter
        presenter.view = viewController
        presenter.interactor = interactor
        presenter.router = router
        router.viewController = viewController

        return viewController
    }
}

protocol CiolConfirmationRouterProtocol: AnyObject {
    func navigateToMyBookings()
    func showRoomKeyInstructions(model: InstructionsViewModel)
}

protocol CiolConfirmationInteractorProtocol: AnyObject {
    var ciolConfirmationDetails: CiolConfirmationDetails { get set }
    var customAnalyticsParameters: PIDictionary? { get }
    var roomKeyInstructionsModel: InstructionsViewModel? { get }
    func refreshStays(completion: @escaping (Bool?) -> Void)
	func callBookingConfirmation()
}

struct CiolConfirmationDetails {
    let bookerFirstName: String
    let hotelBrand: HotelBrand
    let ciolStartFlow: CIOLStartFlow
    let hotelImage: URL?
    var analyticsInfo: PIDictionary
    let stay: Stay

    var confirmationMessage: String {
        String(format: PILocalizedString("ciolYouAreCheckedInMessage"), bookerFirstName)
    }
}

protocol CiolConfirmationPresenterProtocol: AnyObject {
    func viewIsReady()
    func navigateToMyBookings()
    func refreshStays()
    func showKeyInstructions()
    func trackCiolComplete()
}

struct CiolInstructionsConfig {
    let category: String
    let titleLabel: String
    let descriptionLabel: String
}
