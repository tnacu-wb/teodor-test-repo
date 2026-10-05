//
//  BusinessCardQuestionsView+Formeka.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import Foundation

extension FormBusinessCardQuestionsController {
    func viewModelSections(with businessCardQuestionsViewModel: BusinessCardQuestionsAnswersViewModel)
        -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(messageSection())
        if let questionsAndAnswers = businessCardQuestionsViewModel.questions {
            sections.append(questionsSection(with: questionsAndAnswers))
        }
        sections.append(submitSection(
            with: businessCardQuestionsViewModel.ctaSetup,
            showCards: businessCardQuestionsViewModel.willShowPayment
        ))

        return sections
    }
}

private extension FormBusinessCardQuestionsController {
    func messageSection() -> FormekaModelSection {
        let row = FormekaModelRow(tag: Step2Row.paymentTimeMessage.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FormekaFreeTextCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.message.text = PILocalizedString("businessCardQuestionsMessage", comment: "Business card questions message")
            cell.message.font = .BodySmall()
            cell.topConstraint.constant = 25
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })

        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }

    func questionsSection(with questionsAndAnswers: [BusinessCardQuestionAndAnswer]) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []
        var traits = FormekaTextFieldTraits()

        for (index, questionAndAnswer) in questionsAndAnswers.enumerated() {
            let tag: String = {
                switch questionAndAnswer.type {
                case .purchaseOrder:
                    return "purchaseOrder"
                case .customerReference:
                    return "customerReference"
                case .custom:
                    return questionAndAnswer.question.questionId ?? "\(index)"
                }
            }()

            var label = questionAndAnswer.question.label ?? ""
            if questionAndAnswer.question.mandatory == false {
                label.append(" (\(PILocalizedString("optional")))")
            }

            switch questionAndAnswer.question.managementInformationAnswer?.answerType {
            case .field:
                traits.placeholder = label.contains(PILocalizedString("optional")) ? "" : questionAndAnswer.question
                    .mandatory == true ? "" : PILocalizedString(
                        "businessCardQuestionsPlaceholder",
                        comment: "Business card questions textfield placeholder"
                    )

                rows.append(textFieldRow(
                    name: tag,
                    title: label,
                    value: questionAndAnswer.answer,
                    traits: traits,
                    inlineValidators: questionAndAnswer.question.mandatory == true ? [.required] : []
                ))
            case .multiChoice:
                guard let answers = questionAndAnswer.question.managementInformationAnswer?.answers,
                      answers.isNotEmpty else { continue }
                rows.append(buttonRow(
                    tag: tag,
                    title: label,
                    validators: questionAndAnswer.question.mandatory == true ? [.required] : [],
                    value: questionAndAnswer.answer ?? UserDefaults.standard
                    .string(forKey: "BBAnswer\(questionAndAnswer.question)")
                ) { [weak self] _, _ in
                    self?.showPicker(with: tag, question: label, and: answers)
                })
            case nil:
                continue
            }
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func submitSection(with ctaSetup: BusinessQuestionsCTASetup, showCards: Bool = false) -> FormekaModelSection {
        var rows = [FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.setTitle(ctaSetup.title, for: .normal)

            let style = ctaSetup.style.buttonStyling

            cell.button.backgroundColor = style.buttonColour
            cell.button.setTitleColor(style.titleColour, for: .normal)
            cell.button.titleLabel?.font = style.font
            cell.button.accessibilityIdentifier = AccessibilityIdentifiers.BusinessCardQuestions.ctaButton

            cell.contentView.backgroundColor = .BaseWhite
            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.delegate = self

            return cell
        })]


        if showCards, let cards = BookingDetails.sharedInstance.hotel?.acceptedCreditCards {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: CreditCardsListCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.cardUrls = cards.sorted { $0.listOrder < $1.listOrder }.compactMap { $0.logoURL }
                cell.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.cardsAcceptedTitle
                cell.hiddenSeparatorLocations = [.top, .bottom]

                return cell
            }))
        }

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: nil
        )
    }

    private func showPicker(with tag: String, question: String, and answers: [String]) {
        let viewModel = BusinessQuestionAnswersListViewModel(
            answers: answers.sorted(by: { $0 < $1 }),
            mainSectionTitle: question.replacingOccurrences(of: " (\(PILocalizedString("optional")))", with: "")
        )
        viewModel.showsSearchBar = true

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString("Search")
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { [weak self] sender, answer in
            DispatchQueue.main.async {
                sender.dismiss(animated: true)
            }

            UserDefaults.standard.set(answer as? String, forKey: "BBAnswer\(tag)")

            self?.viewModel?.row(named: tag)?.value = answer as? String
            self?.table.reloadData()
        }
        controller.cancelButtonDidTap = { sender in
            DispatchQueue.main.async {
                sender.dismiss(animated: true)
            }
        }

        DispatchQueue.main.async { [weak self] in
            self?.present(controller, animated: true)
        }
    }
}
