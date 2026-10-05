//
//  ReviewAndBookView+Formeka+BusinessCard.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka

extension ReviewAndBookViewController {
    func businessCardQuestionsAndAnswersSection(bookingDetails: BookingDetails) -> FormekaModelSection? {
        guard bookingDetails.shouldShowEmployeeQuestionsForOpera else { return nil }

        // We only ever show business questions for BAC or business bookers
        // this will always be true since .leisure is handled above, but keeping the business logic
        guard bookingDetails.bookingMode == .business || bookingDetails.primaryPaymentMethod?.card?.type
              .isBusiness == true || bookingDetails.primaryPaymentMethod?.paymentMethodType?.isNewBACCard == true
        else { return nil }

        // If there are no questions to ask (i.e. the company hasn't set any up) don't show the summary
        guard bookingDetails.businessCardQuestionsAndAnswers.isEmpty == false else { return nil }

        var rows = bookingDetails.businessCardQuestionsAndAnswers.filter { $0.answer != nil }.enumerated()
            .map { index, questionAndAnswer -> FormekaModelRow in
            let row = FormekaModelRow(tag: "question\(index)", cellSetup: { indexPath, row, table in
                guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.title.text = questionAndAnswer.question.label ?? "" + ":"
                cell.subtitle.text = row.value as? String
                cell.hiddenSeparatorLocations = [.top, .bottom]
                if bookingDetails.businessCardQuestionsAndAnswers.count != index + 1 {
                    cell.bottomConstraint.constant = 5
                }

                return cell
            })
            row.value = {
                guard let value = questionAndAnswer.answer else { return PILocalizedString(
                    "businessQuestionNoAnswerDescription",
                    comment: ""
                ) }
                guard value.isEmpty == false  else { return PILocalizedString(
                    "businessQuestionNoAnswerDescription",
                    comment: ""
                ) }

                return value
            }()

            return row
        }

        if rows.isEmpty {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.titleLabel.text = PILocalizedString("None")
                cell.titleLabel.font = .Body()
                cell.valueLabel.text = nil
                cell.backgroundColor = .white

                return cell
            }))
        }

        rows.append(separatorLineRow(style: .paddedGap(left: 16, right: -16)))

        return FormekaModelSection(
            header: actionHeader(withHeading: PILocalizedString("businessCardQuestionsScreenTitle"), action: { [weak self] in
                self?.presenter?.editAdditionalInformationButtonDidTap()
            }),
            rows: rows,
            footer: nil
        )
    }
}
