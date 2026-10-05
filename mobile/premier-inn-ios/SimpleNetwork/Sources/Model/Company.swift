//
//  Company.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 08/10/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

private extension String {
    static let bookingFlowLocation = "B"
    static let registrationFlowLocation = "R"
}

public struct Company: Codable {
    public let companyDetails: CompanyDetails?
    public let bookingAllowances: BookingAllowances?
    public var paymentDetails: BBPaymentDetails?
    public let allowCentralCreditCard: Bool?
    public let companyManagementDetails: CompanyManagementDetails?
    public let companyCellCodes: [CompanyCellCode]?

    public var businessCardQuestions: [BusinessCardQuestionAndAnswer]? {
        guard let companyManagementDetails = companyManagementDetails else { return nil }

        var qsAndAs = [BusinessCardQuestionAndAnswer]()

        if let customerRefQuestion = companyManagementDetails.customerReferenceManagement,
           customerRefQuestion.active == true,
           customerRefQuestion.location == .bookingFlowLocation {
            qsAndAs.append(BusinessCardQuestionAndAnswer(
                type: .customerReference,
                question: customerRefQuestion,
                answer: nil
            ))
        }

        if let rayPurchaseOrderQuestion = companyManagementDetails.purchaseOrderManagement,
           rayPurchaseOrderQuestion.active == true, rayPurchaseOrderQuestion.location == .bookingFlowLocation {
            qsAndAs.append(BusinessCardQuestionAndAnswer(
                type: .purchaseOrder,
                question: rayPurchaseOrderQuestion,
                answer: nil
            ))
        }

        // filter questions that are active and required in booking not registration
        qsAndAs.append(contentsOf:
            companyManagementDetails.userDefinedManagement?.filter { question in
                question.active == true && question.location == .bookingFlowLocation
            }.compactMap { activeQuestion in
                BusinessCardQuestionAndAnswer(type: .custom, question: activeQuestion, answer: nil)
            } ?? []
        )

        return qsAndAs
    }
}

public struct CompanyDetails: Codable {
    public let companyName: String?
    public let alternateCompanyName: String?
    public let companyAddress: Address?
}

public struct BBPaymentDetails: Codable {
    public var paymentCards: [PaymentCard]?
}

public struct BookingAllowances: Codable {
    public let maxDinnerBudgets: MaxDinnerBudgets?
    public let upsellItemsAllowed: [String]?
    public let allowAlcohol: Bool?
    public let allowCarParking: Bool?
    public let allowIndividualCards: Bool?
    public let allowAdditionalCosts: Bool?
}
public struct MaxDinnerBudgets: Codable {
    public let uKWide: Cost?
    public let greaterLondon: Cost?
    public let ireland: Cost?
}

public struct CompanyManagementDetails: Codable {
    public let purchaseOrderManagement: CompanyManagementQuestion?
    public let customerReferenceManagement: CompanyManagementQuestion?
    public let userDefinedManagement: [CompanyManagementQuestion]?
}

public struct CompanyManagementQuestion: Codable {
    public let questionId: String?
    public let label: String?
    public let mandatory: Bool?
    public let active: Bool?
    public let managementInformationAnswer: ManagementInformationAnswer?
    public let location: String?
}

public struct ManagementInformationAnswer: Codable {
    public let answerType: AnswerType?
    public let answers: [String]?
}

public enum AnswerType: String, Codable {
    case field = "F"
    case multiChoice = "U"
}

public struct CompanyCellCode: Codable {
    public let type: String?
    public let description: String?
}
