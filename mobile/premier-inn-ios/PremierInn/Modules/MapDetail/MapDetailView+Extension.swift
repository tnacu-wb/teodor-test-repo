//
//  MapViewController+Extension.swift
//  PremierInn
//
//  Created by Marcello Mascia on 10/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import MapKit
import SimpleNetwork

extension MapViewController: MKMapViewDelegate {
    func mapView(_ mapView: MKMapView, regionWillChangeAnimated animated: Bool) {
        // Horrible hack to update compass position even when the map is animating
        timer = Timer(timeInterval: 0.1, target: self, selector: #selector(timerDidFire), userInfo: nil, repeats: true)

        if let timer = timer {
            RunLoop.main.add(timer, forMode: RunLoop.Mode.common)
        }

        updateMapHelpers()
    }

    func mapView(_ mapView: MKMapView, regionDidChangeAnimated animated: Bool) {
        timer?.invalidate()

        updateMapHelpers()
    }

    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is Hotel {
            let identifier = "HotelPin"
            let imageName = viewModel?.mapPinImageName ?? ""

            var pinView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier)
            pinView = PIAnnotationView(annotation: annotation, reuseIdentifier: identifier, imageName: imageName)

            return pinView
        }

        if annotation is Suggestion {
            let identifier = "ReferencePin"

            var pinView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier)

            if pinView == nil {
                pinView = PIAnnotationView(annotation: annotation, reuseIdentifier: identifier, imageName: "referencePin")
            } else {
                pinView?.annotation = annotation
            }

            if let pinView = pinView as? PIAnnotationView,
               let title = annotation.title {
                pinView.text = title
            }

            return pinView
        }

        return nil
    }
}

extension MapViewController: MapAnnotationViewDelegate {
    func mapAnnotationViewDidChangeSize(_ view: MapAnnotationView, size: CGSize) {
        referenceCompassWidthConstraint.constant = size.width
        referenceCompassHeightConstraint.constant = size.height
    }
}

extension MapViewController {
    @IBAction func resetButtonDidTap(_ sender: UIButton) {
        centerAnnotations(animated: true)
    }

    @IBAction func backToReferenceDidTap(_ sender: UITapGestureRecognizer) {
        guard let mapView = mapView else { return }

        if let referenceAnnotation = viewModel?.referenceAnnotation {
            mapView.setCenter(referenceAnnotation.coordinate, animated: true)
        }
    }

    @IBAction func backToHotelButtonDidTap(_ sender: UIButton) {
        guard let mapView = mapView else { return }
        guard let hotelAnnotation = viewModel?.hotelAnnotation else { return }

        mapView.setCenter(hotelAnnotation.coordinate, animated: true)
    }
}
