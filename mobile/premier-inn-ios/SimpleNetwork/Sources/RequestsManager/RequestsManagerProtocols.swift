//
//  RequestsManagerProtocols.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 17/02/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation


// MARK: CheckInOnline
// MARK: - Start CheckIn Req. Params
public protocol StartCheckInRequestParameters {
    var confirmationNumber: String { get }
    var surname: String { get }
    var arrivalDate: Date { get }
    var guestHistoryNumber: String? { get }
}

public protocol DashboardRequestsProvider {
    func getDashboardComponents(
        stay: Stay?,
        recentSearchesFlag: Bool,
        completion: @escaping (_ dashboardComponents: [DashboardComponent]?, _ error: Error?) -> Void
    )

    func getHomepageContent(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String,
        completion: @escaping (HomepageAppsContent?, Error?) -> Void
    )
}
