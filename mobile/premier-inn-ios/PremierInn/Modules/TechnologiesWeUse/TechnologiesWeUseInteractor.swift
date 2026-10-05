//
//  TechnologiesWeUseInteractor.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

class TechnologiesWeUseInteractor {
    private var dataRequirements: TechnologiesWeUseDataRequirements

    init(with dataRequirements: TechnologiesWeUseDataRequirements) {
        self.dataRequirements = dataRequirements
    }
}

extension TechnologiesWeUseInteractor: TechnologiesWeUseInteractorProtocol {
    var userHasAccepted: Bool {
        dataRequirements.userHasAccepted
    }

    var navigationBarTitle: String {
        PILocalizedString("closeBarButtonTitle", comment: "Close bar button title")
    }

    var footerButtonTitle: String {
        PILocalizedString("gdprAcceptButtonTitle", comment: "GDPR accept button title")
    }
}
