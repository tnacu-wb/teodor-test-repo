//
//  BusinessCardQuestionsInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 21/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

struct BusinessQuestionsCTASetup {
    let title: String
    let style: CTAButtonStyle
}

struct BusinessCardQuestionsAnswersViewModel {
    let screenTitle: String
    let questions: [BusinessCardQuestionAndAnswer]?
    let ctaSetup: BusinessQuestionsCTASetup
    let willShowPayment: Bool
}

protocol BusinessCardQuestionsInteractorProtocol {
    var viewModel: BusinessCardQuestionsAnswersViewModel { get }
    var customAnalyticsParameters: PIDictionary? { get }
}

class BusinessCardQuestionsInteractor {
    private var qsAndAs: [BusinessCardQuestionAndAnswer]?
    private var ctaSetup: BusinessQuestionsCTASetup
    private var willShowPayment = false

    init(businessCardQuestions: [BusinessCardQuestionAndAnswer], willShowPayment: Bool = false) {
        self.qsAndAs = businessCardQuestions
        self.ctaSetup = BusinessQuestionsCTASetup(
            title: willShowPayment ? PILocalizedString("Continue to payment") : PILocalizedString("Update"),
            style: .teal(icon: nil)
        )
        self.willShowPayment = willShowPayment
    }
}

extension BusinessCardQuestionsInteractor: BusinessCardQuestionsInteractorProtocol {
    var viewModel: BusinessCardQuestionsAnswersViewModel {
        BusinessCardQuestionsAnswersViewModel(
            screenTitle: PILocalizedString(
                "businessCardQuestionsScreenTitle",
                comment: "Business card questions screen title"
            ),
            questions: qsAndAs,
            ctaSetup: ctaSetup,
            willShowPayment: willShowPayment
        )
    }

    var customAnalyticsParameters: PIDictionary? {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.productString] = ";\(BookingDetails.sharedInstance.hotel?.code ?? "")"

        let company = UserSessionManager.sharedInstance.currentUser?.company

        data[PIAnalytics.Keys.bfUserDefinedQuestions] = company?.companyManagementDetails?.userDefinedManagement?
            .compactMap { $0.questionId }.joined(separator: ",") ?? ""

        var standardBusinessQuestions: [String] = []
        if company?.companyManagementDetails?.purchaseOrderManagement != nil {
            standardBusinessQuestions.append("PO")
        }
        if company?.companyManagementDetails?.customerReferenceManagement != nil {
            standardBusinessQuestions.append("CR")
        }
        data[PIAnalytics.Keys.bfBusinessAccQuestions] = standardBusinessQuestions.joined(separator: ",")

        return data
    }
}
