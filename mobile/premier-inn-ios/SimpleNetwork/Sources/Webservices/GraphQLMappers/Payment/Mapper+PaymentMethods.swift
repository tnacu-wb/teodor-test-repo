//
//  Mapper+PaymentMethods.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 04/10/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum PaymentMethodsMapper {
    public static func map(from input: [PIDictionary]) -> PIDictionary {
        var dict = PIDictionary()
        var paymentMethods: [PIDictionary] = input

        // paymentMethodsAvailable bool is mocked
        dict["paymentMethodsAvailable"] = input.contains(where: { $0["enabled"] as? Bool == true })

        // If reserve without is enabled for any option then we have to create our own method
        if let reserveWithoutCard = ReserveWithoutCardMapper.map(from: input) {
            paymentMethods.append(reserveWithoutCard)
        }

        dict["paymentMethods"] = updatePaymentMethodsEnableStatus(paymentMethods: paymentMethods)

        return dict
    }

    private static func updatePaymentMethodsEnableStatus(paymentMethods: [PIDictionary]) -> [PIDictionary] {
        var updatedPaymentMethods = paymentMethods

        for i in 0..<updatedPaymentMethods.count {
            guard (updatedPaymentMethods[i])["type"] as? String != PaymentIntervalOption.rwc.cccpType else { continue }

            guard let paymentOptions = updatedPaymentMethods[i]["paymentOptions"] as? [PIDictionary] else { continue }

            let payNow = paymentOptions.first(where: { $0["type"] as? String == PaymentIntervalOption.now.cccpType })
            let payNowEnabled = payNow?["enabled"] as? Bool ?? false
            let payOnArrival = paymentOptions.first(where: { $0["type"] as? String == PaymentIntervalOption.later.cccpType })
            let payOnArrivalEnabled = payOnArrival?["enabled"] as? Bool ?? false

            if !payNowEnabled && !payOnArrivalEnabled {
                updatedPaymentMethods[i]["enabled"] = false
            }
        }
        return updatedPaymentMethods
    }
}

public enum ReserveWithoutCardMapper {
    static func map(from inputs: [PIDictionary]) -> PIDictionary? {
        var dict = PIDictionary()

        for input in inputs {
            guard let paymentOptions = input["paymentOptions"] as? [PIDictionary] else { return nil }
            // Reserve without a card should only be enabled if the parent payment method is enabled
            guard input["enabled"] as? Bool == true else { continue }

            for paymentOption in paymentOptions {
                if paymentOption["type"] as? String == PaymentIntervalOption.rwc
                   .cccpType && paymentOption["enabled"] as? Bool == true {
                    dict["name"] = paymentOption["type"]
                    dict["type"] = paymentOption["type"]
                    // Make the new method the last in the order
                    dict["order"] = inputs.count + 1
                    dict["enabled"] = paymentOption["enabled"]
                    // Base these two values on the method that the RWC has been found in
                    dict["cnpOptionAvailable"] = input["cnpOptionAvailable"]
                    dict["paymentOptions"] = mapPaymentOptionsForRWC(from: paymentOptions)
                    dict["acceptedCardTypes"] = []
                    dict["reasons"] = []
                    // For the first found instance of the reserve without card enabled then use that to build the new method
                    return dict
                }
            }
        }
        return nil
    }

    private static func mapPaymentOptionsForRWC(from input: [PIDictionary]) -> [PIDictionary]? {
        var dicts = [PIDictionary]()
        for option in input {
            var dict = PIDictionary()
            dict = option
            // For the reserve without card option we do not want this enabled
            if option["type"] as? String == PaymentIntervalOption.now
               .cccpType || option["type"] as? String == PaymentIntervalOption.later.cccpType {
                dict["enabled"] = false
            }
            dicts.append(dict)
        }
        return dicts
    }
}
