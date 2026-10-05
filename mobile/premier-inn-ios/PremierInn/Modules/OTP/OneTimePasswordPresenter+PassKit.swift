//
//  OneTimePasswordPresenter+PassKit.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 13/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import PassKit

extension OneTimePasswordPresenter {
    func presentPassViewController(
        configuration: PKAddShareablePassConfiguration,
        delegate: PKAddSecureElementPassViewControllerDelegate
    ) {
        guard let passController = PKAddSecureElementPassViewController(configuration: configuration, delegate: self)
            else { return }

        router?.presentGetKeyView(passController)
    }
}

extension OneTimePasswordPresenter: PKAddSecureElementPassViewControllerDelegate {
    func addSecureElementPassViewController(
        _ controller: PKAddSecureElementPassViewController,
        didFinishAddingSecureElementPasses passes: [PKSecureElementPass]?,
        error: (any Error)?
    ) {
		if let identifier = interactor?.provisioningCredentialIdentifier {
			updateStayWithPassId(with: identifier)
		}

        // need to dismiss
        controller.dismiss(animated: true) {
            self.view?.updateTableError(error: nil)
            self.view?.stopLoadingAnimation()

            // Exit early if user cancelled
            if let error = error as? NSError {
                if error.domain == PKAddSecureElementPassErrorDomain,
                   error.code == PKAddSecureElementPassError.userCanceledError.rawValue {
                    return
                }
            }
        }
    }
}
