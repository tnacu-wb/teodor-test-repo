//
//  AlertManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

public enum CallNumberType: String {
    case generic
    case groupBookings

    var phoneNumber: String {
        switch self {
        case .generic:
            return PILocalizedString("telephoneNumber", comment: "Default phone number")
        case .groupBookings:
            return PILocalizedString("telephoneGroupNumber", comment: "Phone number for group booking (more than 4 rooms)")
        }
    }
}

enum AlertManager {
    static func callUsAlert(withTitle title: String, message: String, number: String) -> UIAlertController? {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("alertCancelButton", comment: ""),
            style: .cancel,
            handler: nil
        ))

		guard let url = URL(string: "tel:" + number.replacingOccurrences(of: " ", with: "")) else { return alertController }
		guard UIApplication.shared.canOpenURL(url) else { return alertController }

        alertController.addAction(UIAlertAction(
            title: PILocalizedString("callUsButtonTitle", comment: ""),
            style: .default,
            handler: { _ in
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
        ))

        return alertController
    }

    static func goToWebAlert(withTitle title: String, message: String, url: URL?) -> UIAlertController? {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)

        alertController.addAction(UIAlertAction(title: PILocalizedString("operaFallbackAlertClose"), style: .cancel))
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("operaFallbackAlertContinue"),
            style: .default,
            handler: { _ in
            if let url {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
        ))

        return alertController
    }

    static func fallbackToWebsitePopup() -> UIAlertController? {
        let alertController = UIAlertController(
            title: PILocalizedString("operaFallbackAlertTitle"),
            message: PILocalizedString("operaFallbackAlertMessage"),
            preferredStyle: .alert
        )

        alertController.addAction(UIAlertAction(title: PILocalizedString("operaFallbackAlertClose"), style: .cancel))
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("operaFallbackAlertContinue"),
            style: .default,
            handler: { _ in
            guard let url = Constants.premierInnBaseURL else { return }
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
        ))

        return alertController
    }
}
