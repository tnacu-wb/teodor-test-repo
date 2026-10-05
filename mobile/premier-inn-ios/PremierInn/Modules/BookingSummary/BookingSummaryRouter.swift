//
//  BookingSummaryRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum BookingSummaryRouter {
    static func build(
        hotel: Hotel?,
        bookingDetails: BookingDetails?,
        reservation: Reservation?,
        screenName: String = PIAnalytics.StateNames.summaryBreakdown
    ) -> UIViewController {
        let controller = BookingSummaryViewController(screenName: screenName)
        controller.presenter = {
            let presenter = BookingSummaryPresenter()
            presenter.view = controller
            presenter.interactor = {
                if let hotel = hotel {
                    return BookingSummaryInteractor(hotel: hotel, reservation: reservation)
                }

                if let bookingDetails = bookingDetails {
                    return BookingSummaryInteractor(bookingDetails: bookingDetails)
                }

                return nil
            }()

            return presenter
        }()

        return controller
    }
}
