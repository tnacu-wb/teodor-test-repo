//
//  PaymentDetailsRouter.swift
//  PremierInn
//
//
//  Created Freddie Parks on 07/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//
//

import UIKit
import SimpleNetwork

class PaymentDetailsRouter {
    var view: UIViewController?
}

extension PaymentDetailsRouter: PaymentDetailsRouterProtocol {
    func goBack(checkedIn: Bool, alsoPaid: Bool = false) {
        if let reservationsListController = view?.navigationController?.viewControllers
           .first(where: { $0 is ReservationsListViewController }) as? ReservationsListViewController {
            reservationsListController.justPaid = alsoPaid
            reservationsListController.checkedIn = checkedIn
        }

        if let bookingDetailsListController = view?.navigationController?.viewControllers
           .first(where: { $0 is BookingConfirmationViewController }) as? BookingConfirmationViewController {
            bookingDetailsListController.justPaid = alsoPaid
        }

        guard let destinationViewController = view?.navigationController?.viewControllers.reversed()
              .first(where: { $0 is ReservationsListViewController || $0 is BookingConfirmationViewController }) ?? view?
              .navigationController?.viewControllers.last else { return }

        view?.navigationController?.popToViewController(destinationViewController, animated: true)
    }
}
