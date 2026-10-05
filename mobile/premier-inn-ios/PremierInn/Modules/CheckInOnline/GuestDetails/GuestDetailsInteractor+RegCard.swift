//
//  GuestDetailsInteractor+RegCard.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 08.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

extension GuestDetailsInteractor: EditDetailsViewDelegate {
    func didUpdateUserDetails(with editDetailsModel: EditDetailsModel) {
        updateInput(with: editDetailsModel)
    }
}

extension GuestDetailsInteractor {
    func didReceiveAuthorizationMessage(string: String) {
        guard let jsonData = string.data(using: .utf8) else { return }
        let decoder = JSONDecoder()
        do {
            let message = try decoder.decode(AuthorizationMessage.self, from: jsonData)

            guard message.paymentStatus == .success,
                  let regCardInput = regCardInput(transactionID: message.transactionId),
                  let output = output else {
                throw CIOLError.performPrestayChecks
            }
            Task { @MainActor in
                finishRegCardWithoutOutstanding(regcardInput: regCardInput, provider: dataProvider, output: output)
            }
        } catch {
            Task { @MainActor in
                output?.stopLoadingUI(error: CIOLError.performPrestayChecks)
            }
        }
    }
}
