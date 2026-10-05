//
//  CriteriaModifiable.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

protocol CriteriaModifiable {
    var currentCriteria: Criteria? { get }
    var updatedCriteria: Criteria? { get set }

    func update(with criteria: Criteria)
    func showUpdateAlert(sender: UIViewController)
}

extension CriteriaModifiable {
    func showUpdateAlert(sender: UIViewController) {
        guard let updatedCriteria = updatedCriteria else { return }
        guard currentCriteria?.arrivalDate != updatedCriteria.arrivalDate || currentCriteria?.nights != updatedCriteria
              .nights else { return }

        let title = "\(updatedCriteria.arrivalDate.localizedMediumDayMonthStringFormat) - \(updatedCriteria.checkOutDate?.localizedMediumDayMonthStringFormat ?? "")"

        let alertController = UIAlertController(
            title: title,
            message: PILocalizedString("searchResultsCriteriaUpdateMessage", comment: ""),
            preferredStyle: .alert
        )
        alertController.addAction(UIAlertAction(
            title: PILocalizedString(
                "searchResultsCriteriaUpdateYes",
                comment: "Search results: criteria update request message confirm action"
            ),
            style: .default,
            handler: { _ in
            self.update(with: updatedCriteria)
        }
        ))
        alertController.addAction(UIAlertAction(
            title: PILocalizedString(
                "searchResultsCriteriaUpdateNo",
                comment: "Search results: criteria update request message deny action"
            ),
            style: .cancel,
            handler: { _ in
            if let currentCriteria = self.currentCriteria {
                self.update(with: currentCriteria)
            }
        }
        ))

        sender.present(alertController, animated: true)
    }
}
