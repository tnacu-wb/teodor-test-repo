//
//  Mapper+CategoryLabels.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 07.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

public enum LabelsMapper {
    static func mapLabels(labelsType: LabelsConfig, from input: PIDictionary?) -> PIDictionary? {
        switch labelsType {
        case .roomDisclaimer:
            return mapRoomDisclaimer(config: labelsType, input: input)
        }
    }

    static func mapRoomDisclaimer(config: LabelsConfig, input: PIDictionary?) -> PIDictionary? {
        var dict = PIDictionary()
        for key in config.labels {
            dict[key] = input?[key]
        }
        return dict
    }
}

public enum LabelsConfig {
    case roomDisclaimer

    var labels: [String] {
        switch self {
        case .roomDisclaimer:
            return [
                HotelBrand.hub.categoryKeys,
                HotelBrand.premierInn.categoryKeys,
                HotelBrand.zip.categoryKeys,
                HotelBrand.premierInnGermany.categoryKeys
            ]
        }
    }

    var category: String {
        switch self {
        case .roomDisclaimer:
            return Constants.mainCategory
        }
    }

    public var descriptionKey: String? {
        switch self {
        case .roomDisclaimer:
            return nil
        }
    }

    public enum Constants {
        public static let mainCategory = "main"
    }
}
