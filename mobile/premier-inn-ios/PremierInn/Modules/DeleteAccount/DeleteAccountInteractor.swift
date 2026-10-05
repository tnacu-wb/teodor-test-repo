//
//  DeleteAccountInteractor.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 28/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol DeleteAccountInteractorInput {
    func deleteAccountRequest(
        withPassword password: String,
        completion: @escaping (_ error: Error?, _ success: Bool) -> Void
    )
}

class DeleteAccountInteractor {
    internal var requestsManager = RequestsManager()
}

extension DeleteAccountInteractor: DeleteAccountInteractorInput {
    func deleteAccountRequest(
        withPassword password: String,
        completion: @escaping (_ error: Error?, _ success: Bool) -> Void
    ) {
        guard let email = UserSessionManager.sharedInstance.currentUser?.emailAddress else { return }

        self.requestsManager.login(withUsername: email, password: password, isBusiness: false) { result in
            switch result {
            case .success:

                self.requestsManager.deleteUser { success, _ in
                    if success {
                        completion(nil, true)
                    } else {
                        completion(nil, false)
                    }
                }

            case .failure:
                completion(nil, false)
            }
        }
    }
}
