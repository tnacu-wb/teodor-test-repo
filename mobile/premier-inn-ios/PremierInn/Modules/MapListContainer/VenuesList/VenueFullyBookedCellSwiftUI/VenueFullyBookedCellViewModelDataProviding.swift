//
//  VenueFullyBookedCellViewModelDataProviding.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol VenueFullyBookedCellViewModelDataProviding {
    var name: String { get }
    var primaryImages: [URL] { get }
}

extension Hotel: VenueFullyBookedCellViewModelDataProviding { }
