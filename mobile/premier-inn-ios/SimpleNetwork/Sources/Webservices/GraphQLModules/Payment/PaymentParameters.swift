//
//  PaymentParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

enum UserType: String {
    case LEISURE
    case BUSINESS
}

enum PaymentMethodsFlow: String {
    case CheckInOnline
}

public enum InitiatePaymentError: Error {
     case paymentTypeMissing
 }

protocol CCCPRequestKeys {
    static var requestId: String { get }
    static var payment: String { get }
    static var booking: String { get }
    static var sessionId: String { get }
}

extension String: CCCPRequestKeys {
    static var requestId: String { "requestId" }
    static var payment: String { "payment" }
    static var booking: String { "booking" }
    static var sessionId: String { "sessionId" }
    static var isCiol: String { "isCiol" }
    static var companyQuestionAndAnswerDetails: String { "companyQuestionAndAnswerDetails" }
    static var charityPackage: String { "charityPackageCode" }
    static var hotelId: String { "hotelId" }
}

extension GraphQL {
    private enum PIBAAllowance: String {
        case dinner
        case ultimateWifi
        case carParking
        case alcohol
    }

    static func getInitiatePaymentVariables(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        hostURL: String,
        isCiol: Bool
    ) throws -> PIDictionary {
        guard let sessionId = sessionId else {
            throw RequestsManagerError.unexpectedResponseError // FIXME: specific error?
        }

        var params = PIDictionary()

        var 📂 = PIDictionary()

        let uuid = UUID().uuidString
        📂[.booking] = bookingRequestVariables(paymentParams: paymentParams, stayDetails: stayDetails)
        📂[.payment] = try paymentRequestVariables(paymentParams: paymentParams, stayDetails: stayDetails, hostURL: hostURL)
        📂[.requestId] = uuid
        if let bbQandA = paymentParams.bbQuestionAndAnswers, BookingDetails.sharedInstance.bookingMode == .business {
            📂[.companyQuestionAndAnswerDetails] = companyQuestionAndAnswerDetails(questionsAndAnswer: bbQandA)
        }
        📂[.isCiol] = isCiol

        if let donation = paymentParams.donationPackage {
            📂[.charityPackage] = donation
            📂[.hotelId] = stayDetails.hotelIdentifier
        }

        params["basketReference"] = sessionId
        params["createPaymentCriteria"] = 📂

        return params
    }

    private static func bookingRequestVariables(
        paymentParams: CCCPPaymentParams,
        stayDetails: CCCPStayDetails
    ) -> PIDictionary {
        var 🧾: PIDictionary = [
            "channel": Constants.CCC.channelIOS,
            "journey": paymentParams.journey.rawValue,
            "type": paymentParams.paymentInterval.cccpType,
            "language": LanguageManager.supportedLanguage.rawValue
        ]

        if let arrivalDate = stayDetails.arrivalDate {
            🧾["arrivalDate"] = DateFormatter.parameterFormatter.string(from: arrivalDate)
        }
        if let departureDate = stayDetails.departureDate {
            🧾["departureDate"] = DateFormatter.parameterFormatter.string(from: departureDate)
        }

        if let hotelCode = stayDetails.hotelIdentifier {
            var 🏨: PIDictionary = [
                "identifier": hotelCode,
                "type": Constants.CCC.businessSiteTypeHotel
            ]

            if let hotelName = stayDetails.hotelName {
                🏨["name"] = hotelName
            }

            🏨["location"] = hotelCode

            🧾["businessSite"] = 🏨
        }

        if let leadGuest = stayDetails.leadGuest, let leadGuestDictionary = leadGuest.leadGuestDic {
            🧾["leadGuest"] = leadGuestDictionary
        }

        let 🛏 = stayDetails.roomDetails?.compactMap { room in
            [
                "adultsNumber": room["adults"],
                "rate": room["rate"],
                "type": room["type"]
            ] }
        🧾["rooms"] = 🛏

        return 🧾
    }

    private static func getBusinessItems(paymentParams: CCCPPaymentParams, stayDetails: CCCPStayDetails) -> PIDictionary {
        var dict = PIDictionary()
        var businessAllowances = [PIDictionary]()

        businessAllowances.append(allowance(
            allowance: PIBAAllowance.ultimateWifi,
            enabled: stayDetails.businessAccount?.wifiAccessAllowed ?? false
        ))

        let allowanceInt = NumberFormatter.gbNumberFormatter
            .number(from: stayDetails.businessAccount?.dinnerAllowance ?? "") ?? 0
        businessAllowances.append(allowance(
            allowance: PIBAAllowance.dinner,
            enabled: allowanceInt != 0,
            budget: allowanceInt
        ))
        businessAllowances.append(allowance(
            allowance: PIBAAllowance.carParking,
            enabled: stayDetails.businessAccount?.carParkingAllowed ?? false
        ))
        businessAllowances.append(allowance(
            allowance: PIBAAllowance.alcohol,
            enabled: stayDetails.businessAccount?.alcoholAllowed ?? false
        ))

        dict["purchaseOrderNumber"] = stayDetails.businessAccount?.purchaseOrder ?? ""
        dict["customReferenceNumber"] = stayDetails.businessAccount?.customerReference ?? ""
        dict["businessAllowances"] = businessAllowances

        return dict
    }

    private static func allowance(allowance: PIBAAllowance, enabled: Bool, budget: NSNumber = 0) -> PIDictionary {
        var dict = PIDictionary()
        dict["budget"] = budget
        dict["allowance"] = allowance.rawValue
        dict["isAuthorised"] = enabled

        return dict
    }

    static func paymentRequestVariables(
        paymentParams: CCCPPaymentParams,
        stayDetails: CCCPStayDetails,
        hostURL: String
    ) throws -> PIDictionary {
        guard let paymentType = paymentParams.paymentType?.rawValue else { throw InitiatePaymentError.paymentTypeMissing }

        var 💰: PIDictionary = [
            "type": paymentType,
            "subType": paymentParams.paymentType == .PAYPAL ? Constants.CCC.paypalSubType : Constants.CCC.paymentSubType,
            "environment": hostURL,
            "billing": paymentParams.billingDetails.toGraphQLDictionary
        ]

        // If pay now this field is always true if not pay now this field is based on if CNP has been selected
        💰["pibaCardPresent"] = paymentParams.paymentInterval == .now ? true : stayDetails.businessAccount?
            .cardNotPresentAuth != true

        if BookingDetails.sharedInstance.bookingMode == .business || paymentParams.payingWithPIBA {
            💰["businessItems"] = getBusinessItems(paymentParams: paymentParams, stayDetails: stayDetails)
        }

        if paymentParams.paymentType == .PAYPAL {
            💰["paypalNonce"] = paymentParams.paypalNonce
            💰["paypalDeviceData"] = paymentParams.paypalDeviceData ?? ""
        }

        if var 💳 = paymentParams.card?.cccpDic {
            💳["cardType"] = paymentParams.card?.cardType
            💳["cnpRequired"] = paymentParams.card?.cnpRequired
            💳["logoUrl"] = paymentParams.card?.logoUrl
            💳["type"] = paymentParams.card?.type.cardCode
            💰["card"] = 💳
        }

        return 💰
    }

    static func paymentMethodsVariables(bookingDetails: BookingDetails, isCiol: Bool = false) throws -> PIDictionary {
        var params = PIDictionary()
        guard let basketReference = bookingDetails.basketReference else {
            throw RequestsManagerError.unexpectedResponseError // FIXME: specific error?
        }

        params["paymentMethodsCriteria"] = paymentMethodsCriteria(basketReference: basketReference, isCiol: isCiol)
        return params
    }

    private static func paymentMethodsCriteria(basketReference: String, isCiol: Bool = false) -> PIDictionary {
        var params = PIDictionary()

        params["basketReference"] = basketReference
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["userType"] = BookingDetails.sharedInstance.bookingMode == .business ? UserType.BUSINESS.rawValue : UserType
            .LEISURE.rawValue
        params["clientChannel"] = Constants.CCC.channelIOS
        if isCiol {
            params["flowType"] = PaymentMethodsFlow.CheckInOnline.rawValue
        }

        return params
    }

    private static func companyQuestionAndAnswerDetails(questionsAndAnswer: [OperaBusinessCardQuestionAndAnswer])
        -> PIDictionary {
        var dict = PIDictionary()
        if let customerReference = questionsAndAnswer.first(where: { $0.type == .customerReference }) {
            dict["customerReferenceQuestionAndAnswer"] = [
                "question": customerReference.operaQuestionAndAnswer.question,
                "answer": customerReference.operaQuestionAndAnswer.answer
            ]
        }
        if let purchaseOrder = questionsAndAnswer.first(where: { $0.type == .purchaseOrder }) {
            dict["purchaseOrderQuestionAndAnswer"] = [
                "question": purchaseOrder.operaQuestionAndAnswer.question,
                "answer": purchaseOrder.operaQuestionAndAnswer.answer
            ]
        }
        var customQuestionsDict = [PIDictionary]()
        for questionAndAnswer in questionsAndAnswer where questionAndAnswer.type == .custom {
            customQuestionsDict.append([
                "question": questionAndAnswer.operaQuestionAndAnswer.question,
                "answer": questionAndAnswer.operaQuestionAndAnswer.answer
            ])
        }

        dict["userDefinedQuestionAndAnswers"] = customQuestionsDict
        return dict
    }

    static func getAuthorizePaymentVariables() throws -> PIDictionary {
        var params = PIDictionary()
        params["requestId"] = UUID().uuidString
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["country"] = LanguageManager.supportedLanguage.countryCode
        var environment: String = ""
        if let router = Router.current as? LowerEnvGraphQLRouter {
            environment = "\(router.restService?.scheme ?? "")://\(router.restService?.host ?? "")"
        } else {
            environment = "\(Webservice.migratedLiveMicroservices.scheme)://\(Webservice.migratedLiveMicroservices.host)"
        }
        params["environment"] = environment
        return params
    }

    static func getAttachFileToReservationVariables(params: AuthorizationFileAttachmentParams) -> PIDictionary {
        var paramsDict = PIDictionary()
        paramsDict[AuthorizationFileAttachmentParams.Constants.description] = params.description
        paramsDict[AuthorizationFileAttachmentParams.Constants.fileAttachment] = params.fileAttachment
        paramsDict[AuthorizationFileAttachmentParams.Constants.fileName] = params.fileName
        paramsDict[AuthorizationFileAttachmentParams.Constants.global] = params.global
        paramsDict[AuthorizationFileAttachmentParams.Constants.hotelId] = params.hotelId
        paramsDict[AuthorizationFileAttachmentParams.Constants.overwriteExistingFile] = params.overwriteExistingFile
        paramsDict[AuthorizationFileAttachmentParams.Constants.reservationId] = params.reservationId
        return paramsDict
    }

    static func getUpdatePreCheckInStatusVariables(params: UpdatePrecheckInParams) -> PIDictionary {
        var paramsDict = PIDictionary()
        paramsDict[UpdatePrecheckInParams.Constants.arrivalTime] = params.arrivalTime
        paramsDict[UpdatePrecheckInParams.Constants.hotelId] = params.hotelId
        paramsDict[UpdatePrecheckInParams.Constants.reservationId] = params.reservationId

        return paramsDict
    }
}
