//
//  AdditionalInfoModule.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum AdditionalInfoModule {
    static func build(with hotel: Hotel, and infoType: AdditionalInfoType) -> UIViewController {
        let controller = AdditionalInfoViewController()
        controller.eventHandler = {
            let interactor = AdditionalInfoInteractor(with: hotel, and: infoType)

            let router = AdditionalInfoRouter()
            router.viewController = controller

            let presenter = AdditionalInfoPresenter()
            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            return presenter
        }()

        return controller
    }
}

enum AdditionalInfoType {
    case hotelNotes
    case hotelLocation
    case hotelParking
    case facilities
}

protocol AdditionalInfoViewModel {
    var infoType: AdditionalInfoType { get }
    var hotelName: String { get }
    var hotelNotes: [String]? { get }
    var hotelDescription: String? { get }
    var hotelDirections: String? { get }
    var parkingDetails: String? { get }
    var facilityDescriptions: [String]? { get }
    var facilityTitle: String? { get }
    var roomFeatureDescriptions: [String]? { get }
    var roomFeatureTitle: String? { get }
}

protocol AdditionalInfoViewProtocol: AnyObject {
    func update(with viewModel: AdditionalInfoViewModel)
}

protocol AdditionalInfoViewEventHandler {
    func viewIsReady()
    func closeButtonDidTap()
}

protocol AdditionalInfoInteractorProtocol {
    var viewModel: AdditionalInfoViewModel { get }
}

protocol AdditionalInfoRouterProtocol {
    func closeButtonDidTap()
}
