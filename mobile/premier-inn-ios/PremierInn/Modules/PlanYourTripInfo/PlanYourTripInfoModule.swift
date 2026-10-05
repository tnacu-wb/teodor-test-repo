//
//  PlanYourTripInfoModule.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum PlanYourTripInfoModule {
    static func build(
        with hotel: Hotel,
        delegate: PlanYourTripInfoRouterDelegate?
    ) -> UIViewController {
        let controller = PlanYourTripInfoView()

        controller.eventHandler = {
            let presenter = PlanYourTripInfoPresenter()

            let router = PlanYourTripInfoRouter()
            router.delegate = delegate

            let interactor = PlanYourTripInfoInteractor(hotel: hotel)

            presenter.view = controller
            presenter.interactor = interactor
            presenter.router = router

            return presenter
        }()

        return controller
    }
}

protocol PlanYourTripInfoViewModel {
    var title: String? { get }
    var hotelAddress: String? { get }
    var directions: String? { get }
    var hotelParking: String? { get }
}

protocol PlanYourTripInfoViewProtocol: AnyObject {
    func update(with viewModel: PlanYourTripInfoViewModel)
}

protocol PlanYourTripInfoViewEventHandler {
    func viewIsReady()
    func openDirections(withSender sender: UIView)
}

protocol PlanYourTripInfoInteractorProtocol: AnyObject {
    var viewModel: PlanYourTripInfoViewModel? { get }
}

protocol PlanYourTripInfoRouterProtocol: AnyObject {
    func openDirections(withSender sender: UIView)
}

protocol PlanYourTripInfoRouterDelegate: AnyObject {
    func openDirections(withSender sender: UIView)
}
