//
//  OneTimePasswordCaptureInteractor.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import SimpleNetwork

protocol OTPEmailCaptureInteractorInput {
    var stay: Stay { get set }
    var customAnalyticsParameters: PIDictionary? { get }
    var roomId: String { get }
    func generateOTP(
        withEmailAddress emailAddress: String,
        completion: @escaping (_ success: Bool?, _ error: Error?) -> Void
    )
}

class OneTimePasswordCaptureInteractor {
    internal var stay: Stay
    var roomId: String
    internal var requestsManager = RequestsManager()

    init(with stay: Stay, roomId: String) {
        self.stay = stay
        self.roomId = roomId
    }
}

extension OneTimePasswordCaptureInteractor: OTPEmailCaptureInteractorInput {
    func generateOTP(
        withEmailAddress emailAddress: String,
        completion: @escaping (_ success: Bool?, _ error: Error?) -> Void
    ) {
        requestsManager.generateOTP(email: emailAddress, completion: completion)
    }

    var customAnalyticsParameters: PIDictionary? {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.digitalKeyEmail] = "true"
        data[PIAnalytics.Keys.rateCode] = stay.rateClassification
        data[PIAnalytics.Keys.bookingID] = stay.identifier
        data[PIAnalytics.Keys.checkInConf] = stay.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOutConf] = stay.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.confRooms] = stay.numberOfRoooms
        data[PIAnalytics.Keys.confHotelCode] = stay.hotelCode
        data[PIAnalytics.Keys.productString] = ";\(stay.hotelCode)"
		data[PIAnalytics.Keys.customerType] = (stay.isBusinessTrip ? TripPurpose.business : TripPurpose.leisure)
		    .analyticsString
        return data
    }
}
