//
//  RequestsManager+DigitalKey.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

public extension RequestsManager {
    func generateOTP(email: String, completion: @escaping (_ success: Bool?, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.generateOTP(email: email)

            load(resource: resource, completion: completion)
        } catch {
            completion(false, error)
        }
    }

    func verifyOTP(
        bookingReference: String,
        otpCode: String,
        completion: @escaping (_ success: Bool?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.verifyOTP(bookingReference: bookingReference, otpCode: otpCode)

            load(resource: resource, completion: completion)
        } catch {
            completion(false, error)
        }
    }

    func digitalKeyProvision(
        bookingReference: String,
        otpCode: String,
        email: String,
        reservationId: String,
        completion: @escaping (_ response: DigitalKeyProvisionResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.digitalKeyProvision(
                bookingReference: bookingReference,
                otpCode: otpCode,
                email: email,
                reservationId: reservationId
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func digitalKeyCheckIn(
        reservationId: String,
        hotelCode: String,
        completion: @escaping (_ response: DigitalKeyCheckInResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.digitalKeyCheckIn(reservationId: reservationId, hotelCode: hotelCode)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
