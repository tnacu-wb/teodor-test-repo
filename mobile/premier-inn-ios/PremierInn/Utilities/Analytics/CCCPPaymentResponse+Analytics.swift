//
//  CCCPPaymentResponse+Analytics.swift
//  PremierInn
//
//  Created by Freddie Parks on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import SimpleNetwork

extension CCCPPaymentResponse {
    var trackingParams: PIDictionary {
        var dictionary = PIDictionary()

        guard let paymentRequiredDetails = paymentRequiredDetails else { return dictionary }

        if let template = paymentRequiredDetails.template {
            dictionary[PIAnalytics.Keys.cccTemplateId] = template
        }

        return dictionary
    }
}
