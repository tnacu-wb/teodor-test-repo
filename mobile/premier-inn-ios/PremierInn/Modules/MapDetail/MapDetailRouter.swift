//
//  MapRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

class MapDetailRouter {
    var view: UIViewController?
    var presenter: MapDetailPresenterProtocol?
}

extension MapDetailRouter: MapDetailRouterProtocol {
    func openDirections(with hotel: Hotel, withSender sender: UIView) {
        guard UIApplication.shared.canOpenURL(Constants.googleMapsURL) else {
            UIApplication.shared.openAppleMapsDirections(to: hotel)
            return
        }

        let controller = UIAlertController(
            title: PILocalizedString("directionsOptionsAlertTitle", comment: "Directions options alert title"),
            message: PILocalizedString("directionsOptionsAlertMessage", comment: "Directions options alert message"),
            preferredStyle: .actionSheet
        )
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "directionsOptionsAlertActionCancel",
                comment: "Directions options alert action: cancel"
            ),
            style: .cancel,
            handler: nil
        ))
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "directionsOptionsAlertActionAppleMaps",
                comment: "Directions options alert action: Apple Maps"
            ),
            style: .default
        ) { _ in
            UIApplication.shared.openAppleMapsDirections(to: hotel)
        })
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "directionsOptionsAlertActionGoogleMaps",
                comment: "Directions options alert action: Google Maps"
            ),
            style: .default
        ) { _ in
            UIApplication.shared.openGoogleMapsDirections(to: hotel)
        })

        if UIDevice.current.userInterfaceIdiom == .pad, view != nil {
            controller.modalPresentationStyle = .popover
            controller.popoverPresentationController?.sourceView = sender
            controller.popoverPresentationController?.sourceRect = sender.bounds
        }

        view?.present(controller, animated: true)
    }

    func showOverlayController(with hotel: Hotel) {
        guard let view = view as? MapDetailViewProtocol else { return }

        let planViewController = PlanYourTripInfoModule.build(with: hotel, delegate: self)

        try? view.displayOverlayController(controller: planViewController)
    }
}

extension MapDetailRouter: PlanYourTripInfoRouterDelegate {
    func openDirections(withSender sender: UIView) {
        presenter?.openDirections(withSender: sender)
    }
}
