//
//  RequestsManager+Payment.swift
//  PremierInn
//
//  Created by Marcello Mascia on 27/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public typealias DeviceData = (
    colorDepth: String,
    javaEnabled: Bool,
    language: String,
    screenHeight: Int,
    screenWidth: Int,
    timeZone: String,
    windowSize: String
)

public extension RequestsManager {
    func completePayment(
        with sessionId: String,
        and paRes: String,
        completion: @escaping (_ response: Bool?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.completePayment(with: sessionId, and: paRes)

            load(resource: resource, completion: completion)
        } catch {
            completion(false, error)
        }
    }
}

/*
 3C Payment Methods
 */

public extension RequestsManager {
    /// Creates a payment request with Microservices and 3C
    ///
    /// Use this method to start the payment process for a 3CP hotel.
    /// Retrieve the information to show a 3C iPage, for new card input
    /// or stored card auth, depending on parameters sent.
    ///
    /// - Parameters:
    ///     - paymentParams: Struct providing payment info for 3C payment (card, billing details, journey type, payment time, amount, is business account card)
    ///     - stayDetails: Struct providing booking info for 3C payment (hotel code, arrival date, departure date, lead guest, room details)
    ///     - sessionId: BART session ID as a string (used for extending booking session and checking for timeouts)
    /// - Returns: CCCPaymentResponse contains html for starting a 3C iPage session
    func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String,
        completion: @escaping (_ response: CCCPPaymentResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.cccpPayment(
                with: paymentParams,
                and: stayDetails,
                and: sessionId,
                and: isCiol,
                sensorData: sensorData
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
