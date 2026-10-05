//
//  LocationDetailsCellDisplayable.swift
//  PremierInn
//
//  Created by Clint Mengolli on 24/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
import MapKit
import SimpleNetwork

protocol LocationDetailsDisplayable {
    var title: String? { get }
    var address: String? { get }
    var annotations: [MKAnnotation] { get }
    var mapImage: UIImage? { get }
}
