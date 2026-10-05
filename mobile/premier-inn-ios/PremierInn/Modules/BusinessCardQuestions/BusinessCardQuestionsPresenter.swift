//
//  BusinessCardQuestionsPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol BusinessCardQuestionPresenterProtocol {
    func viewIsReady()
    func cancelButtonDidTap()
    func submitButtonDidTap(values: PIDictionary)
}

class BusinessCardQuestionPresenter {
    weak var view: BusinessCardQuestionViewProtocol?

    var router: BusinessCardQuestionsRouterProtocol?
    var interactor: BusinessCardQuestionsInteractorProtocol?
}

extension BusinessCardQuestionPresenter: BusinessCardQuestionPresenterProtocol {
    func viewIsReady() {
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters

        if let viewModel = interactor?.viewModel {
            view?.loadViewModel(with: viewModel)
        }

        if router?.shouldShowCancelButton == true {
            view?.showCancelButton()
        }
    }

    func cancelButtonDidTap() {
        router?.dismissController()
    }

    func submitButtonDidTap(values: PIDictionary) {
        guard var questions = interactor?.viewModel.questions else { return }

        for (index, questionAndAnswer) in questions.enumerated() {
            questions[index].answer = {
                switch questionAndAnswer.type {
                case .purchaseOrder:
                    return values["purchaseOrder"] as? String
                case .customerReference:
                    return values["customerReference"] as? String
                case .custom:
                    guard let id = questionAndAnswer.question.questionId else { return nil }
                    return values[id] as? String
                }
            }()
        }

        router?.submitBusinessCardQuestions(questions: questions)
    }
}
