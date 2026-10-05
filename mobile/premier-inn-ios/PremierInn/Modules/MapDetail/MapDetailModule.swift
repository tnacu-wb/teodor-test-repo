//
//  MapModule.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import MapKit

enum MapLayout {
    case map
    case journeyPlanner
}

enum MapDetailModule {
    static func build(
        with hotel: Hotel?,
        hotelCode: String?,
        referencePoint: MKAnnotation?,
        and layoutType: MapLayout = .map
    ) -> UIViewController {
        let controller = MapViewController()
        controller.eventHandler = {
            let presenter = MapDetailPresenter()

            let interactor: MapDetailInteractor = {
                if let hotel = hotel {
                    return MapDetailInteractor(
                        hotel: hotel,
                        referencePoint: referencePoint,
                        delegate: presenter,
                        and: layoutType
                    )
                }
                return MapDetailInteractor(
                    hotelCode: hotelCode ?? "",
                    referencePoint: referencePoint,
                    delegate: presenter,
                    and: layoutType
                )
            }()

            let router = MapDetailRouter()
            router.view = controller
            router.presenter = presenter

            presenter.interactor = interactor
            presenter.view = controller
            presenter.router = router

            return presenter
        }()

        return controller
    }
}

typealias MapCompassDesign = (colour: UIColor, imageName: String)

protocol MapDetailViewModel {
    var layout: MapLayout { get }
    var hotelName: String? { get }
    var hotelAnnotation: MKAnnotation? { get }
    var referenceAnnotation: MKAnnotation? { get }
    var mapCompassDesign: MapCompassDesign? { get }
    var mapPinImageName: String { get }
    var distanceText: NSAttributedString? { get }
}

protocol MapDetailViewProtocol: AnyObject {
    func update(with viewModel: MapDetailViewModel)
    func displayOverlayController(controller: UIViewController) throws
}

protocol MapDetailViewEventHandler {
    func viewIsReady()
}

protocol MapDetailInteractorProtocol: AnyObject {
    var viewModel: MapDetailViewModel? { get }
    var hotel: Hotel? { get }
    var layout: MapLayout { get }
}

protocol MapDetailInteractorDelegate: AnyObject {
    func finishedLoadingHotel()
}

protocol MapDetailPresenterProtocol {
    func openDirections(withSender sender: UIView)
}

protocol MapDetailRouterProtocol: AnyObject {
    func openDirections(with hotel: Hotel, withSender sender: UIView)
    func showOverlayController(with hotel: Hotel)
}
