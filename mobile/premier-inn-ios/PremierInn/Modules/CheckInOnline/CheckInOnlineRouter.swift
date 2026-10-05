//
//  CheckInOnlineRouter.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka
import Foundation

protocol CheckInOnlineRouterProtocol {
    func goBack(checkedIn: Bool)
    func open(url: URL)
    func show(edit leadGuest: LeadGuest, at index: Int, hasAdditionalGuest: Bool, bookerAddress: Address)
    func showUpsells(with checkInOnlineModel: CheckInOnlineModel)
}

struct CheckInOnlinePaymentParams {
    let checkInSessionReponse: CheckInOnlineSessionResponse
    let acceptedCreditCards: [CardType]?
}
