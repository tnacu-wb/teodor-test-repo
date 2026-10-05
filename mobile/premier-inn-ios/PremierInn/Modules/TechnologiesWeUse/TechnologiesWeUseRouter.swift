//
//  TechnologiesWeUserRouter.swift
//  PremierInn
//
//  Created by Nick Jones on 25/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

class TechnologiesWeUseRouter {
    var gdprEventHandler: GDPRInterstitialEventHandler?

    init(with gdprEventHandler: GDPRInterstitialEventHandler?) {
        self.gdprEventHandler = gdprEventHandler
    }
}

extension TechnologiesWeUseRouter: TechnologiesWeUseRouterProtocol {
    func userAcceptedTermsAndConditions() {
        gdprEventHandler?.userDidAccept(sender: nil)
    }
}
