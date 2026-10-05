//
//  GoogleAPIDataProvider.swift
//  PremierInn
//
//  Created by Freddie Parks on 06/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import CoreLocation
import GooglePlaces

private enum GoogleAPIConfig {
    enum Endpoints {
        static let place = "/maps/api/place/details/json"
    }

    enum ParameterKeys {
        static let apiKey = "key"
        static let placeId = "placeid"
    }

    static let scheme = "https"
    static let baseURL = "maps.googleapis.com"
    static let apiKey = "AIzaSyBlYjv2KANhDYyNSJadMJvXi2FMIIobEB8"
    static let headers: [String: String?] = ["X-Ios-Bundle-Identifier": Bundle.main.bundleIdentifier]
}

private enum GoogleAPIURLs {
    enum Service {
        case placeDetails(String)
    }

    static func url(for service: Service) -> URL? {
        var urlComponents = URLComponents()
        urlComponents.scheme = GoogleAPIConfig.scheme
        urlComponents.host = GoogleAPIConfig.baseURL

        switch service {
        case .placeDetails(let placeId):

            urlComponents.path = GoogleAPIConfig.Endpoints.place
            urlComponents.queryItems = [
                URLQueryItem(name: GoogleAPIConfig.ParameterKeys.apiKey, value: GoogleAPIConfig.apiKey),
                URLQueryItem(name: GoogleAPIConfig.ParameterKeys.placeId, value: placeId)
            ]
        }

        return try? urlComponents.asURL()
    }
}

class GoogleAPIDataProvider: NSObject {
    static let sharedInstance: GoogleAPIDataProvider = GoogleAPIDataProvider()

    private var placesClient: GMSPlacesClient?

    func setup() {
        guard GMSPlacesClient.provideAPIKey(GoogleAPIConfig.apiKey) else { return }
        placesClient = GMSPlacesClient.shared()
    }

    func updateCoordinates(
        for identifier: String,
        completion: @escaping (_ coordinate: CLLocationCoordinate2D?, _ success: Bool) -> Void
    ) {
        placesClient?
            .fetchPlace(fromPlaceID: identifier, placeFields: GMSPlaceField.coordinate, sessionToken: nil) { place, _ in
            guard let place = place else { return completion(nil, false) }
            guard CLLocationCoordinate2DIsValid(place.coordinate) else { return completion(nil, false) }

            completion(place.coordinate, true)
        }
    }
}
