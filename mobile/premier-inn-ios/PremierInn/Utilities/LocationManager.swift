//
//  LocationManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import CoreLocation

protocol LocationManagerDelegate: AnyObject {
    func locationManagerDidFind(userLocation location: CLLocation, locationManager: LocationManager)
    func locationManagerAuthorizationDenied(locationManager: LocationManager, shouldShowAlert: Bool)
    func locationManagerDidFailWithError(locationManager: LocationManager, error: NSError)
}

class LocationManager: NSObject {
	weak var delegate: LocationManagerDelegate?

    let coreLocationManager = CLLocationManager()

    private func startLocationManager() {
        coreLocationManager.delegate = self

        switch coreLocationManager.authorizationStatus {
        case .authorizedWhenInUse:
            coreLocationManager.startUpdatingLocation()

        case .notDetermined:
            coreLocationManager.requestWhenInUseAuthorization()

        default:
            delegate?.locationManagerAuthorizationDenied(locationManager: self, shouldShowAlert: true)
        }
    }
}

extension LocationManager {
	@objc func findUserLocation() {
		startLocationManager()
	}

    func authDeniedSettingsAlertController() -> UIAlertController {
        let alertController = UIAlertController(
            title: PILocalizedString("locationServiceErrorTitle", comment: "Location service: error alert title"),
            message: PILocalizedString("locationServiceErrorMessage", comment: "Location service: error alert message"),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(
            title: PILocalizedString(
                "locationServiceErrorCancelAction",
                comment: "Location service: error alert cancel action button title"
            ),
            style: .cancel
        ) { _ in
            AnalyticsManager.shared.trackAction(PIAnalytics.Action.locationAuthSettingsCancelButtonTap, userInfo: nil)
        })
        alertController.addAction(UIAlertAction(
            title: PILocalizedString(
                "locationServiceErrorSettingsAction",
                comment: "Location service: error alert settings action button title"
            ),
            style: .default
        ) { _ in
            AnalyticsManager.shared.trackAction(PIAnalytics.Action.locationAuthSettingsGoButtonTap, userInfo: nil)

            guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        })

        return alertController
    }

    func alertControllerWithError(_ error: NSError) -> UIAlertController {
        let controller = UIAlertController(
            title: error.localizedDescription,
            message: error.localizedExceptionMessage,
            preferredStyle: .alert
        )
        controller.addAction(UIAlertAction(
            title: PILocalizedString(
                "locationServiceErrorCancelAction",
                comment: "Location service: error alert cancel action button title"
            ),
            style: .cancel,
            handler: nil
        ))

        return controller
    }
}

extension LocationManager: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse {
            AnalyticsManager.shared.trackAction(PIAnalytics.Action.locationAuthorizationConfirmed, userInfo: nil)

            findUserLocation()
        } else {
            AnalyticsManager.shared.trackAction(PIAnalytics.Action.locationAuthorizationDenied, userInfo: nil)

            delegate?.locationManagerAuthorizationDenied(locationManager: self, shouldShowAlert: false)
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if locations.isNotEmpty {
            manager.stopUpdatingLocation()

            if let location = locations.last {
                DispatchQueue.main.async {
                    self.delegate?.locationManagerDidFind(userLocation: location, locationManager: self)
                }
            }
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        let userInfo = [
            NSLocalizedDescriptionKey: PILocalizedString(
                "locationServiceErrorTitle",
                comment: "Location service: error alert title"
            ),
            NSLocalizedFailureReasonErrorKey: PILocalizedString(
                "locationServiceErrorDisabled",
                comment: "Location service: error alert service disabled"
            )
        ]
        let customError = NSError(domain: PIError.domain, code: PIError.Code.locationUnavailable, userInfo: userInfo)

        DispatchQueue.main.async {
            self.delegate?.locationManagerDidFailWithError(locationManager: self, error: customError)
        }
    }
}
