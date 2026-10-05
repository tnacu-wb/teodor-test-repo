//
//  PassKitManager.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 16/10/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation
import PassKit
import SimpleNetwork

class PassKitManager: NSObject {
    /*  full list
        AC("Mastercard Credit"),
        AM("American Express"),
        AT("Business Account"), -
        DI("Diners Club"), -
        DL("Visa Debit"),
        EL("Electron"),
        MA("Maestro"),
        MD("Mastercard Debit"),
        VI("Visa Credit"),
        MC("Mastercard Credit"),
        AX("Mastercard Credit"),
        PI("Business Account"), -
        DN("Diners Club"), -
        VS("Visa Debit"),
        BD("Business Account"), -
        AP("Apple Pay"), -
        GP("Google Pay"); -
    */
    static let cardTypesForApplePay =
    [
        "AC": PKPaymentNetwork.masterCard,
        "AM": PKPaymentNetwork.amex,
        "DL": PKPaymentNetwork.visa,
        "EL": PKPaymentNetwork.electron,
        "MA": PKPaymentNetwork.maestro,
        "MD": PKPaymentNetwork.masterCard,
        "VI": PKPaymentNetwork.visa,
        "MC": PKPaymentNetwork.masterCard,
        "AX": PKPaymentNetwork.masterCard,
        "VS": PKPaymentNetwork.visa
    ]

    public static func isAppleWalletAllowed(cardTypes: [AcceptedCardType]) -> Bool {
        let paymentNetworks = cardTypes.compactMap({ $0.mappedForApplePay() })
        let isAllowed = PKPaymentAuthorizationViewController.canMakePayments(usingNetworks: paymentNetworks)

        return isAllowed
    }
}

public extension AcceptedCardType {
    func mappedForApplePay() -> PKPaymentNetwork? {
        PassKitManager.cardTypesForApplePay[type]
    }
}
