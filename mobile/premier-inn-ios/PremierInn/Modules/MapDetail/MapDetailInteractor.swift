//
//  MapInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import MapKit

protocol MapDetailInteractorDataProvider {
    func loadHotel(with hotelCode: String, completion: @escaping (_ data: Hotel?, _ error: Error?) -> Void)
}

extension RequestsManager: MapDetailInteractorDataProvider {}

class MapDetailInteractor {
    private(set) var hotel: Hotel? {
        didSet {
            delegate?.finishedLoadingHotel()
        }
    }
    private(set) var layout: MapLayout

    private var referencePoint: MKAnnotation?

    private let dataProvider: MapDetailInteractorDataProvider = RequestsManager()

    weak var delegate: MapDetailInteractorDelegate?

    // Sometimes we're dealing with lovely hotel objects
    init(
        hotel: Hotel?,
        referencePoint: MKAnnotation? = nil,
        delegate: MapDetailInteractorDelegate?,
        and layout: MapLayout? = nil
    ) {
        // defer ensures that the didSet will be called, otherwise it doesn't call from the init
        defer {
            self.hotel = hotel
        }
        self.delegate = delegate
        self.referencePoint = referencePoint
        self.layout = layout ?? .map
    }

    // Sometimes we only have the hotel code because we're dealing with inadequate objects like in the stays request
    init(
        hotelCode: String,
        referencePoint: MKAnnotation? = nil,
        delegate: MapDetailInteractorDelegate?,
        and layout: MapLayout? = nil
    ) {
        self.delegate = delegate
        self.referencePoint = referencePoint
        self.layout = layout ?? .map

        dataProvider.loadHotel(with: hotelCode) { [weak self] hotel, _ in
            guard hotel != nil else { return }
            self?.hotel = hotel
        }
    }

    private func distance(with hotel: Hotel?) -> NSAttributedString? {
        guard let hotel = hotel else { return nil }

        return LengthFormatter.distanceFormatter.attributedString(
            distance: hotel.distance,
            unit: hotel.distanceUnit,
            suffix: PILocalizedString("mapDistanceSuffix", comment: "Map: distance description suffix"),
            boldFont: UIFont.Heading4_Semibold(),
            boldColor: .BasePurple
        )
    }

    private var mapCompassDesign: MapCompassDesign? {
        guard let brand = hotel?.brand else { return nil }

        switch brand {
        case .hub:
            return (.hubGreen, "hubLogo")
        case .zip:
            return (.zipRed, "zipLogo")
        default:
            return nil
        }
    }

    private var mapPinImageName: String {
        let imageName: String = {
            switch hotel?.brand {
            case .some(.zip):
                return "hotelMapPinZip"
            case .some(.hub):
                return "hotelMapPinHub"
            case .some(.premierInn), .some(.premierInnGermany):
                return "hotelMapPinSmall"
            case .none:
                return "hotelMapPinSmall"
            }
        }()

        return imageName
    }
}

extension MapDetailInteractor: MapDetailInteractorProtocol {
    private struct ViewModel: MapDetailViewModel {
        let layout: MapLayout
        let hotelName: String?
        let hotelAnnotation: MKAnnotation?
        let referenceAnnotation: MKAnnotation?
        let mapCompassDesign: MapCompassDesign?
        let mapPinImageName: String
        let distanceText: NSAttributedString?
    }

    var viewModel: MapDetailViewModel? {
        ViewModel(
            layout: layout,
            hotelName: hotel?.name,
            hotelAnnotation: hotel,
            referenceAnnotation: referencePoint,
            mapCompassDesign: mapCompassDesign,
            mapPinImageName: mapPinImageName,
            distanceText: distance(with: hotel)
        )
    }
}
