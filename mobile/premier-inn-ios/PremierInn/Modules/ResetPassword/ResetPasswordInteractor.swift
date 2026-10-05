//
//  ResetPasswordInteractor.swift
//  PremierInn
//
//  Created by Nick Jones on 08/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol ResetPasswordInteractorInput {
    var emailAddress: String? { get set }

    func submitPasswordResetRequest(
        withEmailAddress emailAddress: String,
        completion: @escaping (_ error: Error?, _ success: Bool) -> Void
    )
}

class ResetPasswordInteractor {
    var emailAddress: String?

    private let isBusiness: Bool

    internal var requestsManager = RequestsManager()

    init(withEmailAddress emailAddress: String?, isBusiness: Bool) {
        self.emailAddress = emailAddress
        self.isBusiness = isBusiness
    }
}

extension ResetPasswordInteractor: ResetPasswordInteractorInput {
    func submitPasswordResetRequest(
        withEmailAddress emailAddress: String,
        completion: @escaping (_ error: Error?, _ success: Bool) -> Void
    ) {
        requestsManager.forgotPassword(withEmailAddress: emailAddress, isBusiness: isBusiness) { success, error in
            if error != nil && success {
                AnalyticsManager.shared.trackAction(PIAnalytics.Action.resetPasswordSuccess, userInfo: nil)
            }

            completion(error, success)
        }
    }
}
