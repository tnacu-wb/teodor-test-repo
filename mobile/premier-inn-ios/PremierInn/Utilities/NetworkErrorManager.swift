//
//  NetworkErrorManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/12/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

enum ErrorPresentationStyle {
    case inline
    case alert
    case customAlert
}

struct ErrorUI {
    let title: String?
    let description: String?
    let presentationStyle: ErrorPresentationStyle?
    let actions: [UIAlertAction]?
    let code: String?
}

class NetworkErrorManager {
    static let sharedInstance = NetworkErrorManager()

    var errors: PIDictionary = [:]

    class func errorUI(forError error: Error) -> ErrorUI? {
        switch error {
        case RequestsManagerError.serverError(let dict):

            if let code = NetworkErrorManager.errorCode(from: dict), let mappedError = MSMappedError(rawValue: code) {
                return ErrorUI(
                    title: PILocalizedString("Something went wrong"),
                    description: mappedError.errorMessage,
                    presentationStyle: .customAlert,
                    actions: nil,
                    code: nil
                )
            }

            guard let messages = dict?["details"] as? [String] else { return nil }

            for message in messages {
                let messageComponents = message.components(separatedBy: ":")

                guard let errorKeyComponent = messageComponents.first else { return nil }
                guard let errorKey = NetworkErrorManager.sharedInstance.errors[errorKeyComponent.uppercased()]
                    else { continue }

                switch errorKeyComponent {
                case "FRAUD_CHECK_FAILED":
                    return NetworkErrorManager.fraudErrorUI(
                        with: PILocalizedString("\(errorKey)_TITLE", comment: ""),
                        and: PILocalizedString("\(errorKey)_DESCRIPTION", comment: "")
                    )
                default:
                    return ErrorUI(
                        title: PILocalizedString("\(errorKey)_TITLE", comment: ""),
                        description: PILocalizedString("\(errorKey)_DESCRIPTION", comment: ""),
                        presentationStyle: .alert,
                        actions: nil,
                        code: nil
                    )
                }
            }

            return nil

        default:
            return nil
        }
    }

    private class func fraudErrorUI(with title: String, and message: String) -> ErrorUI {
        ErrorUI(
            title: title,
            description: message,
            presentationStyle: .alert,
            actions: [
                UIAlertAction(title: PILocalizedString("OK", comment: "Generic ok string"), style: .cancel, handler: nil),
                UIAlertAction(title: PILocalizedString("phoneCallAlertAction"), style: .default, handler: { _ in
                    guard let url = URL(string: "tel:" + PILocalizedString(
                        "telephoneNumber",
                        comment: "Default phone number"
                    )) else { return }
                    guard UIApplication.shared.canOpenURL(url)
                        else { return print("Cannot call \(url.absoluteString) on this device") }

                    UIApplication.shared.open(url, options: [:], completionHandler: nil)
                })
            ], code: nil
        )
    }

    class func errorCode(from dic: PIDictionary?) -> String? {
        guard let dic = dic else { return nil }
        if let errorCode = dic["code"] as? String { return errorCode }
        if let errorCode = dic["errorCode"] as? String { return errorCode }
        guard let code = dic["errorCode"] as? Int else { return nil }
        let errorCode = String(format: "%d", code)
        guard !errorCode.isEmpty else { return nil }

        return errorCode
    }

    func updateErrors(with dictionary: PIDictionary?) {
        guard let dictionary = dictionary else { return }
        errors = dictionary
    }
}
