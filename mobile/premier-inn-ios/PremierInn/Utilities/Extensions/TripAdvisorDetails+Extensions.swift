//
//  TripAdvisorDetails+Extensions.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 12/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

public extension TripAdvisorDetails {
    var ratingDescription: String {
        switch rating {
        case 0..<1:
            return PILocalizedString("Terrible", comment: "TripAdvisor rating description for 0-1")
        case 1..<2:
            return PILocalizedString("Poor", comment: "TripAdvisor rating description for 1-2")
        case 2..<3:
            return PILocalizedString("Average", comment: "TripAdvisor rating description for 2-3")
        case 3..<4:
            return PILocalizedString("Very good", comment: "TripAdvisor rating description for 3-4")
        case 4...5:
            return PILocalizedString("Excellent", comment: "TripAdvisor rating description for 4-5")
        default:
            return ""
        }
    }

    // swiftlint:disable:next cyclomatic_complexity
    static func image(with rating: Double) -> UIImage {
        switch rating {
        case 0:
            return UIImage(imageLiteralResourceName: "tripScore0")
        case 0...0.5:
            return UIImage(imageLiteralResourceName: "tripScore0-5")
        case 0.5...1:
            return UIImage(imageLiteralResourceName: "tripScore1")
        case 1...1.5:
            return UIImage(imageLiteralResourceName: "tripScore1-5")
        case 1.5...2:
            return UIImage(imageLiteralResourceName: "tripScore2")
        case 2...2.5:
            return UIImage(imageLiteralResourceName: "tripScore2-5")
        case 2.5...3:
            return UIImage(imageLiteralResourceName: "tripScore3")
        case 3...3.5:
            return UIImage(imageLiteralResourceName: "tripScore3-5")
        case 3.5...4:
            return UIImage(imageLiteralResourceName: "tripScore4")
        case 4...4.5:
            return UIImage(imageLiteralResourceName: "tripScore4-5")
        case 4.5...5:
            return UIImage(imageLiteralResourceName: "tripScore5")
        default:
            return UIImage(imageLiteralResourceName: "tripScore0")
        }
    }
}
