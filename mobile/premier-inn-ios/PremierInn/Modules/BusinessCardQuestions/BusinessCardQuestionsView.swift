//
//  BusinessCardQuestionsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import SimpleNetwork
import UIKit

protocol BusinessCardQuestionViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }

    func setup(withTitle viewTitle: String)
    func loadViewModel(with businessCardQuestionsViewModel: BusinessCardQuestionsAnswersViewModel)
    func showCancelButton()
}

protocol BusinessCardQuestionsDelegate: AnyObject {
    func submitBusinessCardQuestions(questions: [BusinessCardQuestionAndAnswer])
}

class FormBusinessCardQuestionsController: FormekaViewController {
    override var screenName: String { PIAnalytics.StateNames.businessQuestions }
    override var screenType: String { PIAnalytics.StateTypes.bookingFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    var presenter: BusinessCardQuestionPresenterProtocol?

    weak var delegate: BusinessCardQuestionsDelegate?

    init() {
        super.init(nibName: String(describing: FormekaViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .BaseWhite
            table.separatorColor = .ColourLD3
        }
        registerTableElements()

        presenter?.viewIsReady()

        navigationItem.backBarButtonItem = UIBarButtonItem(
            title: PILocalizedString(
                "businessCardQuestionsBackButtonTitle",
                comment: "Business card questions back button title"
            ),
            style: .plain,
            target: nil,
            action: nil
        )
        navigationItem.title = PILocalizedString("businessCardQuestionsScreenTitle")
    }

    private func registerTableElements() {
        guard table != nil else { return }

        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: CreditCardsListCell.self)
    }
}

extension FormBusinessCardQuestionsController: BusinessCardQuestionViewProtocol {
    func setup(withTitle viewTitle: String) {
        title = viewTitle
    }

    func loadViewModel(with businessCardQuestionsViewModel: BusinessCardQuestionsAnswersViewModel) {
        viewModel = FormekaViewModel(sections: viewModelSections(with: businessCardQuestionsViewModel))
        viewModel?.delegate = self

        table.delegate = viewModel
        table.dataSource = viewModel

        table.reloadData()
    }

    func showCancelButton() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }

    @objc func cancelButtonDidTap(_ sender: UIBarButtonItem) {
        presenter?.cancelButtonDidTap()
    }
}

extension FormBusinessCardQuestionsController: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        do {
            try viewModel?.validate()

            guard let values = viewModel?.values else { return }
            presenter?.submitButtonDidTap(values: values)
        } catch let error as RowValidatorError {
            let customMessage = customErrorMessage(for: error)
            error.row.error = RowValidatorError(row: error.row, error: ValidationError.customMessage(customMessage))
            scrollAndFocus(at: viewModel?.indexPath(for: error.row))
        } catch {
            showErrorAlertWith(
                title: PILocalizedString("reviewAlertTitle", comment: "Review and Book: alert title"),
                error: error
            )
        }
    }

    private func customErrorMessage(for rowValidatorError: RowValidatorError) -> String {
        let inputMethodString: String = {
            guard viewModel?.cell(forRowNamed: rowValidatorError.row.tag, table: table) != nil
                else { return PILocalizedString("enter") }
            return PILocalizedString("select")
        }()
        let pretext = String(format: PILocalizedString("Please %@ a valid"), inputMethodString)
        let customMessage = String(format: "%@ '%@'", pretext, rowValidatorError.row.title ?? "")

        return customMessage
    }
}
