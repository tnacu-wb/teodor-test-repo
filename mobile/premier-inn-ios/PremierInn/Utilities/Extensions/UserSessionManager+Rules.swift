//
//  UserSessionManager+Rules.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 03/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

public extension UserSessionManager {
    func piLoggedIn(with user: User) {
        loggedIn(with: user) {
            AdobeCampaignManager.shared.register(userKey: user.contactChannelId)
        }
    }

    func piUserLoggedOut() {
        AdobeCampaignManager.shared.unregister(userKey: currentUser?.contactChannelId)
        userLoggedOut()
    }
}
