//
//  BusinessCardQuestionsRouter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol BusinessCardQuestionsRouterProtocol: AnyObject {
    var shouldShowCancelButton: Bool { get }

    func dismissController()
    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer])
}

class BusinessCardQuestionsRouter {
    private var isModal: Bool = false
    private weak var viewController: FormBusinessCardQuestionsController?

    static func build(
        with businessQsAndAs: [BusinessCardQuestionAndAnswer],
        isModal: Bool
    ) -> FormBusinessCardQuestionsController {
        let view = FormBusinessCardQuestionsController()
        view.presenter = {
            let router = BusinessCardQuestionsRouter()
            router.isModal = isModal
            router.viewController = view

            let interactor = BusinessCardQuestionsInteractor(
                businessCardQuestions: businessQsAndAs,
                willShowPayment: !isModal
            )

            let presenter = BusinessCardQuestionPresenter()
            presenter.interactor = interactor
            presenter.view = view
            presenter.router = router

            return presenter
        }()

        return view
    }
}

extension BusinessCardQuestionsRouter: BusinessCardQuestionsRouterProtocol {
    var shouldShowCancelButton: Bool { isModal }

    func dismissController() {
        viewController?.dismiss(animated: true)
    }

    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer]) {
        viewController?.delegate?.submitBusinessCardQuestions(questions: questions)
    }
}
