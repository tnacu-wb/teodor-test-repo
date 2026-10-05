//
//  VenueFullyBookedCellViewModel.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

struct VenueFullyBookedCellViewModel {
    private let provider: VenueFullyBookedCellViewModelDataProviding

    init(provider: VenueFullyBookedCellViewModelDataProviding) {
        self.provider = provider
    }

    var name: String {
        provider.name
    }

    var imageUrl: URL? {
        provider.primaryImages.first
    }
}
