//
//  ReviewAndBook+Formeka+PaymentMethods.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import Foundation
import UIKit

protocol PaymentIntervalViewModel {
    var userCanChooseWhenToPay: Bool { get }
    var title: String? { get }
    var interval: PaymentIntervalOption { get }
    var infoMessages: [PaymentInformationViewModel]? { get }
}

protocol PaymentInformationViewModel {
    var message: String { get }
    var style: NotificationStyle { get }
}

public enum PaymentOptionsReasonError: String, LocalizedError {
    case businessCentrallyStoredCardDisabled = "BUSINESS_CENTRALLY_STORED_CARD_DISABLED"
    case cardExpiredBeforeDeparture = "CARD_EXPIRED_BEFORE_DEPARTURE"
    case cardExpiredBeforeDepartureBB = "CARD_EXPIRED_BEFORE_DEPARTURE.bb"
    case cardNotAcceptedAtHotel = "CARD_NOT_ACCEPTED_AT_HOTEL"
    case cardNotValidForRate = "CARD_NOT_VALID_FOR_RATE"
    case companyDisabledNewCard = "COMPANY_DISABLED_NEW_CARD"
    case expired = "EXPIRED"
    case personalStoredCardDisabled = "PERSONAL_STORED_CARD_DISABLED"
    case pibaAllowedOnlyInUK = "PIBA_ALLOWED_ONLY_IN_UK"
    case savedCardLocalError = "SAVED_CARD_ERROR"
    case noMethodsAvailable = "noMethodsAvailable"
    case pibaEuroNotAllowedUKHotel = "PIBA_EU_ALLOWED_ONLY_IN_EU"
    case pibaUKNotAllowedDEHotel = "PIBA_UK_ALLOWED_ONLY_IN_UK"

    public func localizedDescription(for cardNumberLast4Digits: String) -> String {
        let isTravelManager = UserSessionManager.sharedInstance.currentUser?.accessLevel == .superUser

        switch self {
        case .businessCentrallyStoredCardDisabled:
            return String(
                format: PILocalizedString(isTravelManager ? "reviewAndBookCSCDisabledTravelManager" :
                    "reviewAndBookCSCDisabled"),
                cardNumberLast4Digits
            )
        case .cardExpiredBeforeDeparture, .cardExpiredBeforeDepartureBB:
            return String(
                format: PILocalizedString(isTravelManager ? "reviewAndBookCardExpiredBeforeArrivalBB" :
                    "reviewAndBookCardExpiredBeforeArrivalLeisure"),
                cardNumberLast4Digits
            )
        case .cardNotAcceptedAtHotel:
            return String(format: PILocalizedString("reviewAndBookCardNotAcceptedAtHotel"), cardNumberLast4Digits)
        case .cardNotValidForRate, .pibaAllowedOnlyInUK:
            return String(format: PILocalizedString("reviewAndBookCardNoteEligibleForRate"), cardNumberLast4Digits)
        case .companyDisabledNewCard, .personalStoredCardDisabled:
            return String(format: PILocalizedString("reviewAndBookPersonalCardDisabled"), cardNumberLast4Digits)
        case .expired:
            return String(format: PILocalizedString("reviewAndBookCardExpired"), cardNumberLast4Digits)
        case .savedCardLocalError:
            return String(format: PILocalizedString("reviewAndBookNo3CToken"), cardNumberLast4Digits)
        case .noMethodsAvailable:
            return String(format: PILocalizedString(isTravelManager ? "paymentMethodsNoMethodsAvailable" :
                "paymentMethodsNoMethodsAvailableNonManager"))
        case .pibaEuroNotAllowedUKHotel:
            return String(format: PILocalizedString("reviewAndBookBACEuroNotSupportedForPI"), cardNumberLast4Digits)
        case .pibaUKNotAllowedDEHotel:
            return String(format: PILocalizedString("reviewAndBookBACNotSupportedForPID"), cardNumberLast4Digits)
        }
    }

    public var localizedDescription: String { String(describing: self) }
    public var errorDescription: String? { String(describing: self) }
}

extension PaymentMethodType {
    var description: String? {
        switch self {
        case .newCreditDebitCard:
            return PILocalizedString("paymentMethodNewCreditDebitLabel")
        case .newBAC:
            return PILocalizedString("paymentMethodNewBACLabel")
        case .storedPersonalBB:
            return "   \(PILocalizedString("paymentMethodStoredPersonalBBLabel"))   "
        case .storedCompanyBB:
            return "   \(PILocalizedString("paymentMethodStoredCSCLabel"))   "
        case .applePay:
            return PILocalizedString("paymentMethodApplePayLabel")
        case .paypal:
            return PILocalizedString("paymentMethodPayPalLabel")
        case .reserveWithoutCard:
            return PILocalizedString("paymentMethodReserveWithoutCard")
        case .newBACEuro:
            return PILocalizedString("paymentMethodNewBACEuro")
        default:
            return nil
        }
    }

    var userCanChooseWhenToPay: Bool {
        switch self {
        case .storedCompanyBB, .newBAC:
            return false
        default:
            return true
        }
    }

    var isNewCard: Bool {
        switch self {
        case .newCreditDebitCard, .newBAC:
            return true
        default:
            return false
        }
    }

    var shouldShowBillingAddress: Bool {
        switch self {
        case .newCreditDebitCard, .newBAC, .paypal, .applePay, .newBACEuro:
            return true
        default:
            return false
        }
    }

    var isNewBACCard: Bool {
        switch self {
        case .newBAC, .newBACEuro:
            return true
        default:
            return false
        }
    }
}

protocol PaymentMethodViewModelType {
    var type: PaymentMethodType { get }
    var imageUrls: [URL]? { get }
    var maskedPAN: String? { get }
    var cardholder: String? { get }
    var expiry: String? { get }
    var selected: Bool { get }
    var cardName: String { get }
    var acceptedCardAccessibilityLabel: String? { get }
    var accessibilityLabel: String { get }
}

protocol PaymentMethodsViewModel {
    var paymentIntervalViewModel: PaymentIntervalViewModel { get }
    var paymentMethodsTitle: String { get }
    var invalidOptionsMessage: [PaymentInformationViewModel]? { get }
    var paymentMethods: [PaymentMethodViewModelType] { get }
    var shouldShowLeisureBusinessQuestions: Bool { get }
    var shouldShowCNP: Bool { get }
}

extension ReviewAndBookViewController {
    func startDisplayingLoadingElements() {
        activityIndicator.startAnimating()
        table.isUserInteractionEnabled = false
        table.alpha = 0.5
        toggleSubmitButton(enabled: false)
    }

    func stopDisplayingLoadingElements() {
        activityIndicator.stopAnimating()
        table.isUserInteractionEnabled = true
        table.alpha = 1
        toggleSubmitButton(enabled: true)
    }

    func cccpPaymentMethodsSections(
        bookingDetails: BookingDetails,
        and viewModel: PaymentMethodsViewModel
    ) -> [FormekaModelSection] {
        // Only ask for billing address if paying with a new card
        let shouldShowBillingAddressSection = bookingDetails.primaryPaymentMethod?.paymentMethodType?
            .shouldShowBillingAddress == true
        let shouldShowLeisurePibaSection = viewModel.shouldShowCNP || viewModel.shouldShowLeisureBusinessQuestions

        var sections: [FormekaModelSection] = [
            paymentMethodsSection(with: viewModel, isLastSection: false)
        ]

        let interval = intervalSection(
            with: viewModel.paymentIntervalViewModel,
            isLastSection: !shouldShowBillingAddressSection,
            addFooterSpacer: !shouldShowBillingAddressSection && shouldShowLeisurePibaSection
        )
        sections.append(interval)

        if shouldShowBillingAddressSection, let section = addressSectionView?.addressSection(
            for: self,
            optionalAddressHeader: PILocalizedString("reviewAndBookBillingAddressSectionTitle"),
            footerHeight: 0
        ) {
            sections.append(section)
        }

        if let pibaSection = leisurePibaSection(with: viewModel) {
            sections.append(pibaSection)
        }

        return sections
    }

    private func paymentMethodsSection(
        with viewModel: PaymentMethodsViewModel,
        isLastSection: Bool = false
    ) -> FormekaModelSection {
        var paymentSectionRows: [FormekaModelRow] = []

        paymentSectionRows.append(contentsOf: viewModel.invalidOptionsMessage?.compactMap {
            iconInfoRow(tag: ReviewAndBookRow.invalidOptionsMessage3C.rawValue, text: $0.message, style: $0.style)
        } ?? [])

        paymentSectionRows.append(paymentMethodsRow(with: viewModel.paymentMethods))

        let selectedPaymentMethod = BookingDetails.sharedInstance.primaryPaymentMethod
        if selectedPaymentMethod?.paymentMethodType == .storedCompanyBB && selectedPaymentMethod?.card?.type
           .isBusiness == false {
            // ADD CVV REQUIRED FROM TRAVEL MANAGER INFO MESSAGE
            paymentSectionRows
                .append(cvvRequiredForBusinessCardRow(for: UserSessionManager.sharedInstance.currentUser?.accessLevel))
        }

        return FormekaModelSection(
            header: actionHeader(
                withHeading: viewModel.paymentMethodsTitle,
                isActionButtonHidden: true,
                accessibilityIdentifier: "",
                action: {}
            ),
            rows: paymentSectionRows,
            footer: nil
        )
    }

    private func leisurePibaSection(with viewModel: PaymentMethodsViewModel) -> FormekaModelSection? {
        guard viewModel.shouldShowLeisureBusinessQuestions || viewModel.shouldShowCNP else { return nil }

        var pibaSectionRows: [FormekaModelRow] = []

        if viewModel.shouldShowLeisureBusinessQuestions {
            pibaSectionRows.append(contentsOf: leisureBusinessCardQuestionRows)
        }

        if viewModel.shouldShowCNP {
            if pibaSectionRows.isNotEmpty {
                pibaSectionRows.append(dottedSeparatorRow())
            }
            pibaSectionRows.append(cardNotPresentRow(bookingDetails: BookingDetails.sharedInstance))
        }

        return FormekaModelSection(
            header: actionHeader(
                withHeading: PILocalizedString("businessCardQuestionsScreenTitle"),
                isActionButtonHidden: true,
                accessibilityIdentifier: "",
                action: {}
            ),
            rows: pibaSectionRows,
            footer: nil
        )
    }

    private var leisureBusinessCardQuestionRows: [FormekaModelRow] {
        let questionsAndAnswers = BookingDetails.sharedInstance.businessCardQuestionsAndAnswers
        guard questionsAndAnswers.isNotEmpty else { return [] }
        var traits = FormekaTextFieldTraits()

        return questionsAndAnswers.map { questionAndAnswer in
            let tag: String = {
                switch questionAndAnswer.type {
                case .purchaseOrder:
                    return "purchaseOrder"
                case .customerReference:
                    return "customerReference"
                default:
                    return ""
                }
            }()

            let label = questionAndAnswer.question.label ?? ""
            traits.placeholder = PILocalizedString("businessCardQuestionsPlaceholder")

            return textFieldRow(
                name: tag,
                title: label,
                value: questionAndAnswer.answer,
                traits: traits,
                inlineValidators: []
            )
        }
    }

    private func intervalSection(
        with viewModel: PaymentIntervalViewModel,
        isLastSection: Bool = false,
        addFooterSpacer: Bool = false
    ) -> FormekaModelSection {
        var intervalSectionRows: [FormekaModelRow] = []

        if viewModel.userCanChooseWhenToPay {
            intervalSectionRows.append(paymentIntervalRow(initialValue: viewModel.interval))
        }

        intervalSectionRows.append(contentsOf: viewModel.infoMessages?.compactMap {
            simpleTextCell(string: $0.message, tagName: ReviewAndBookRow.paymentTimeMessage3C.rawValue)
        } ?? [])

        intervalSectionRows.append(separatorLineRow(style: .paddedGap(left: 16, right: -16)))

        return FormekaModelSection(
            header: viewModel.userCanChooseWhenToPay ? actionHeader(
                withHeading: viewModel.title ?? "",
                isActionButtonHidden: true,
                accessibilityIdentifier: "",
                action: {}
            ) : nil,
            rows: intervalSectionRows,
            footer: nil
        )
    }

    private func paymentMethodsRow(with paymentMethodModels: [PaymentMethodViewModelType]) -> FormekaModelRow {
        FormekaModelRow(tag: "", cellSetup: { [weak self] indexPath, _, table in
            guard let cell: DynamicListCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.paddingLeft.constant = 16
            cell.paddingRight.constant = 16
            cell.paddingBottom.constant = 8

            cell.stackView.setBackgroundColor(.clear, cornerRadius: 3, borderWidth: 1, borderColor: .greyBorder)

            let multipleOptionsAvailable = paymentMethodModels.count > 1

            _ = cell.stackView.arrangedSubviews.map { $0.removeFromSuperview() }

            if cell.stackView.arrangedSubviews.isEmpty {
                for (index, paymentMethod) in paymentMethodModels.enumerated() {
                    guard let paymentMethodView: UIView = {
                        switch paymentMethod.type {
                        case .newBAC, .newCreditDebitCard, .applePay, .reserveWithoutCard, .paypal, .newBACEuro:
                            return self?.newPaymentMethodView(
                                with: paymentMethod,
                                withRadio: multipleOptionsAvailable,
                                isLast: index == paymentMethodModels.indices.last
                            )
                        case .stored, .storedCompanyBB, .storedPersonalBB:
                            return self?.storedPaymentMethodView(
                                with: paymentMethod,
                                withRadio: multipleOptionsAvailable,
                                isLast: index == paymentMethodModels.indices.last
                            )
                        }
                    }() else { continue }

                    cell.stackView.addArrangedSubview(paymentMethodView)
                }
            }

            return cell
        })
    }

    private func storedPaymentMethodView(
        with viewModel: PaymentMethodViewModelType,
        withRadio: Bool,
        isLast: Bool
    ) -> UIView? {
        guard let paymentMethodView: StoredPaymentMethodView = UIView
            .fromNib(nibName: String(describing: StoredPaymentMethodView.self)) else {
            return nil
        }

        paymentMethodView.configure(
            with: viewModel,
            shouldShowRadio: withRadio,
            isLastCell: isLast
        )

        paymentMethodView.action = { [weak self] in
            self?.selectedPaymentMethod(of: viewModel.type)
        }

        paymentMethodView.paymentMethodType = viewModel.type

        if viewModel.selected {
            paymentMethodView.layer.borderWidth = 2
            paymentMethodView.layer.cornerRadius = 4
            paymentMethodView.layer.borderColor = UIColor.Tint1.cgColor
        }

        return paymentMethodView
    }

    private func newPaymentMethodView(
        with viewModel: PaymentMethodViewModelType,
        withRadio: Bool,
        isLast: Bool
    ) -> UIView? {
        guard let paymentMethodView: PayWithNewCardView = UIView
            .fromNib(nibName: String(describing: PayWithNewCardView.self)) else {
            return nil
        }

        paymentMethodView.methodType.text = viewModel.type.description
        paymentMethodView.cardUrls = viewModel.imageUrls
        paymentMethodView.radioButton.isHidden = !withRadio
        paymentMethodView.radioButton.isSelected = viewModel.selected

        if viewModel.selected {
            paymentMethodView.layer.borderWidth = 2
            paymentMethodView.layer.cornerRadius = 4
            paymentMethodView.layer.borderColor = UIColor.Tint1.cgColor
        }

        paymentMethodView.accessibilityIdentifier = viewModel.type.getAccessbilityIdentifier
        paymentMethodView.isAccessibilityElement = true
        paymentMethodView.accessibilityLabel = viewModel.accessibilityLabel
        paymentMethodView.accessibilityTraits = .button

        if viewModel.selected {
            paymentMethodView.accessibilityTraits.insert(.selected)
        }

        paymentMethodView.methodType.isAccessibilityElement = false
        paymentMethodView.collectionView.isAccessibilityElement = false
        paymentMethodView.radioButton.isAccessibilityElement = false

        paymentMethodView.action = { [weak self] in
            self?.selectedPaymentMethod(of: viewModel.type)
        }

        if isLast {
            paymentMethodView.separatorView.isHidden = true
        }

        return paymentMethodView
    }

    private func selectedPaymentMethod(of type: PaymentMethodType) {
        BookingDetails.sharedInstance.primaryPaymentMethod = BookingDetails.sharedInstance.paymentMethods?
            .first(where: { $0.paymentMethodType == type })

        loadViewModel(with: BookingDetails.sharedInstance)
    }

    func updatePaymentTimeMessage(for option: PaymentIntervalOption) {
        BookingDetails.sharedInstance.userSelectedPaymentOption = option

        guard let indexPath = viewModel?.remove(rowNamed: ReviewAndBookRow.paymentTimeMessage3C.rawValue) else { return }
        guard let totalRowIndexPath = viewModel?.remove(rowNamed: ReviewAndBookRow.totalCost.rawValue) else { return }

        let textCell = simpleTextCell(
            string: option.cccpPaymentTimeMessage,
            tagName: ReviewAndBookRow.paymentTimeMessage3C.rawValue
        )
        let totalRow = costWithPayRow(with: BookingDetails.sharedInstance)

        viewModel?.add(row: textCell, at: indexPath)
        viewModel?.add(row: totalRow, at: totalRowIndexPath)

        table.reloadRows(at: [indexPath, totalRowIndexPath], with: .automatic)
    }
}

extension BookingDetails {
    struct BDPaymentMethodsViewModel: PaymentMethodsViewModel {
        let paymentIntervalViewModel: PaymentIntervalViewModel
        let paymentMethodsTitle: String
        let invalidOptionsMessage: [PaymentInformationViewModel]?
        let paymentMethods: [PaymentMethodViewModelType]
        let shouldShowLeisureBusinessQuestions: Bool
        let shouldShowCNP: Bool
    }

    struct BDPaymentIntervalModel: PaymentIntervalViewModel {
        let userCanChooseWhenToPay: Bool
        let title: String?
        let interval: PaymentIntervalOption
        let infoMessages: [PaymentInformationViewModel]?
    }

    struct BDPaymentInformationModel: PaymentInformationViewModel {
        let message: String
        let style: NotificationStyle
    }

    struct BDPaymentMethodViewModel: PaymentMethodViewModelType {
        let type: PaymentMethodType
        let imageUrls: [URL]?
        let maskedPAN: String?
        let cardholder: String?
        let expiry: String?
        let selected: Bool
        let cardName: String
        let acceptedCardAccessibilityLabel: String?
        let accessibilityLabel: String

        init(
            type: PaymentMethodType,
            imageUrls: [URL]?,
            maskedPAN: String?,
            cardholder: String?,
            expiry: String?,
            selected: Bool,
            cardName: String,
            acceptedCardAccessibilityLabel: String?,
            accessibilityLabel: String = ""
        ) {
            self.type = type
            self.imageUrls = imageUrls
            self.maskedPAN = maskedPAN
            self.cardholder = cardholder
            self.expiry = expiry
            self.selected = selected
            self.cardName = cardName
            self.acceptedCardAccessibilityLabel = acceptedCardAccessibilityLabel
            self.accessibilityLabel = accessibilityLabel
        }
    }

    var paymentMethodsViewModel: BDPaymentMethodsViewModel {
        let intervalModel = BDPaymentIntervalModel(
            userCanChooseWhenToPay: userCanChooseWhenToPay,
            title: userCanChooseWhenToPay ? PILocalizedString("reviewPaymentSectionIntervalFlexibleTitle") :
            PILocalizedString("reviewPaymentSectionIntervalFixedTitle"),
            interval: paymentOption,
            infoMessages: paymentIntervalInfoMessages
        )

        return BDPaymentMethodsViewModel(
            paymentIntervalViewModel: intervalModel,
            paymentMethodsTitle: PILocalizedString("reviewPayment3CPSectionTitle"),
            invalidOptionsMessage: invalidMethodViewModels,
            paymentMethods: paymentMethodsViewModels,
            shouldShowLeisureBusinessQuestions: shouldShowLeisureBusinessQuestions,
            shouldShowCNP: shouldShowCNPOptions
        )
    }

    var acceptedCardTypeUrls: [URL]? {
        guard let hotel = hotel, let acceptedCards = hotel.acceptedCreditCards else { return nil }
        return acceptedCards.filter { $0.cardCode != "AT" }.compactMap { $0.logoURL }
    }

    var bacImageUrl: URL? {
        guard let hotel = hotel, let acceptedCards = hotel.acceptedCreditCards else { return nil }
        return acceptedCards.filter { $0.cardCode == "AT" }.compactMap { $0.logoURL }.first
    }

    private var paymentIntervalInfoMessages: [PaymentInformationViewModel]? {
        guard BookingDetails.sharedInstance.primaryPaymentMethod != nil else { return [] }

        var messages: [BDPaymentInformationModel] = []

        let basicMessage = paymentOption.cccpPaymentTimeMessage
        messages.append(
            BDPaymentInformationModel(
                message: basicMessage,
                style: .info
            )
        )

        // add any additional messages like requiring cvv from travel manager

        return messages
    }

    private var invalidMethodViewModels: [PaymentInformationViewModel] {
        guard BookingDetails.sharedInstance.primaryPaymentMethod != nil else { return [BDPaymentInformationModel(
            message: PaymentOptionsReasonError.noMethodsAvailable.localizedDescription(for: ""),
            style: .error
        )] }

        // here we assume that the order is by priority so we can show the more important reason
        guard let disabledPaymentMethod = BookingDetails.sharedInstance.disabledPaymentMethods else { return [] }

        // If all the payment methods are disabled except RWC
        if BookingDetails.sharedInstance.isPaymentDown {
            return [BDPaymentInformationModel(message: PILocalizedString("onlyRWCAvailable"), style: .alert)]
        }

        var paymentMethodViewModel = [PaymentInformationViewModel]()

        for method in disabledPaymentMethod {
            guard let firstReason = method.reasons?.first,
                  let reasonError = PaymentOptionsReasonError(rawValue: firstReason),
                  let last4 = method.card?.last4 else { continue }

            let localizedError = reasonError.localizedDescription(for: last4)
            paymentMethodViewModel.append(BDPaymentInformationModel(message: localizedError, style: .alert))
        }

        return paymentMethodViewModel
    }

    private var paymentMethodsViewModels: [PaymentMethodViewModelType] {
        var paymentMethods: [PaymentMethodViewModelType] = []
        let fetchedPaymentMethods = BookingDetails.sharedInstance.paymentMethods ?? []

        paymentMethods = fetchedPaymentMethods
            .filter { $0.enabled }
            .compactMap { paymentMethod in
                guard let paymentMethodType = paymentMethod.paymentMethodType,
                      isPaymentMethodAllowed(paymentMethod: paymentMethod) else {
                    return nil
                }

                let selected = BookingDetails.sharedInstance.primaryPaymentMethod == paymentMethod

                return BDPaymentMethodViewModel(
                    type: paymentMethodType,
                    imageUrls: cardImageUrls(for: paymentMethod),
                    maskedPAN: paymentPANDescription(for: paymentMethod),
                    cardholder: paymentMethod.card?.cardholderName,
                    expiry: expiryDateString(for: paymentMethod),
                    selected: selected,
                    cardName: paymentMethod.card?.type.cardName ?? "",
                    acceptedCardAccessibilityLabel: paymentMethod.acceptedCardAccessibilityLabel,
                    accessibilityLabel: paymentMethodAccessibilityLabel(for: paymentMethod)
                )
            }

        return paymentMethods
    }

    private func paymentPANDescription(for paymentMethod: PaymentOption) -> String {
        guard let card = paymentMethod.card else {
            return ""
        }

        let prefix = cardName(for: paymentMethod)

        return "\(prefix) (**** \(card.last4))"
    }

    private func paymentMethodAccessibilityLabel(for paymentMethod: PaymentOption) -> String {
        switch paymentMethod.paymentMethodType {
        case .stored, .storedCompanyBB, .storedPersonalBB:
            return storedPaymentMethodAccessibilityLabel(for: paymentMethod)
        default:
            return newPaymentMethodAccessibilityLabel(for: paymentMethod)
        }
    }

    private func storedPaymentMethodAccessibilityLabel(for paymentMethod: PaymentOption) -> String {
        guard let card = paymentMethod.card else {
            return nonEmpty(paymentMethod.name) ?? ""
        }

        let cardName = cardName(for: paymentMethod)

        let cardNumberSummary = PILocalizedString("paymentCardNumberSummary") +
        " " +
        card.last4

        let cardExpirySummary = paymentCardExpiryAccessibilityLabel(
            month: card.expiryMonth,
            year: card.expiryYear
        )

        return [cardName, cardNumberSummary, cardExpirySummary]
            .compactMap { nonEmpty($0) }
            .joined(separator: ", ")
    }

    private func newPaymentMethodAccessibilityLabel(for paymentMethod: PaymentOption) -> String {
        let paymentTypeDescription = nonEmpty(paymentMethod.paymentMethodType?.description) ?? paymentMethod.name

        let acceptedCardsLabel = nonEmpty(paymentMethod.acceptedCardAccessibilityLabel).map {
            PILocalizedString("acceptedCardsTitleAccessibilityLabel") + " " + $0
        }

        return [paymentTypeDescription, acceptedCardsLabel]
            .compactMap { nonEmpty($0) }
            .joined(separator: ", ")
    }

    private func cardName(for paymentMethod: PaymentOption) -> String {
        guard let card = paymentMethod.card else { return nonEmpty(paymentMethod.name) ?? "" }

        return nonEmpty(hotel?.acceptedCreditCards?.first(where: { acceptedCardType in
            acceptedCardType.cardCode == card.type.cardCode
        })?.cardName) ?? nonEmpty(card.type.cardName) ?? nonEmpty(paymentMethod.name) ?? ""
    }

    private func paymentCardExpiryAccessibilityLabel(month: String, year: String) -> String? {
        guard let expiryDate = paymentCardExpiryDate(month: month, year: year) else {
            return nil
        }

        let expiryStateKey = expiryDate >= Date() ?
        "paymentCardFutureExpiration" :
        "paymentCardPastExpiration"

        return expiryStateKey +
        " " +
        DateFormatter.paymentCardAccessibilityExpiryFormatter.string(from: expiryDate)
    }

    private func paymentCardExpiryDate(month: String, year: String) -> Date? {
        let monthDigits = month.filter { $0.isNumber }
        let yearDigits = year.filter { $0.isNumber }
        guard let month = Int(monthDigits), (1...12).contains(month), var year = Int(yearDigits) else { return nil }

        if yearDigits.count == 2 {
            year += 2000
        }

        return Calendar.current.date(from: DateComponents(year: year, month: month))
    }

    private func nonEmpty(_ string: String?) -> String? {
        let trimmedString = string?.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedString?.isEmpty == false ? trimmedString : nil
    }

    private func cardImageUrls(for paymentMethod: PaymentOption) -> [URL] {
        guard let baseUrl = Constants.imageBaseUrl else { return [] }

        if let logoUrl = paymentMethod.logoSrc {
            return [baseUrl.appendingPathComponent(logoUrl)]
        }

        if let logoUrl = paymentMethod.card?.logoUrl {
            return [baseUrl.appendingPathComponent(logoUrl)]
        }

        let imageUrls: [URL] = paymentMethod.acceptedCardTypes?.compactMap {
            guard let logoUrl = $0.logoUrl else { return nil }

            return baseUrl.appendingPathComponent(logoUrl)
        } ?? []

        return imageUrls
    }

    private func expiryDateString(for paymentMethod: PaymentOption) -> String {
        guard let card = paymentMethod.card else { return "" }

        return "\(card.expiryMonth) / \(card.expiryYear)"
    }

    private var shouldShowLeisureBusinessQuestions: Bool {
        guard bookingMode == .leisure else { return false }
        guard let questionsAndAnswers = businessCardQuestions, questionsAndAnswers.isNotEmpty else { return false }
        return primaryPaymentMethod?.card?.type.isBusiness == true || primaryPaymentMethod?.paymentMethodType?
            .isNewBACCard == true
    }

    private var shouldShowCNPOptions: Bool {
        if primaryPaymentMethod?.paymentMethodType != .storedCompanyBB {
            return primaryPaymentMethod?.cnpOptionAvailable == true
        }
        return false
    }

    private var businessCardQuestions: [BusinessCardQuestionAndAnswer]? {
        BookingDetails.sharedInstance.businessCardQuestionsAndAnswers
    }

    private func isPaymentMethodAllowed(paymentMethod: PaymentOption) -> Bool {
        switch paymentMethod.paymentMethodType {
        case .paypal:
            guard SettingsManager.sharedInstance.featurePayPal else { return false }
            return paymentMethod.clientToken != nil
        case .applePay:
            guard Constants.osVersionSupportsApplePay else { return false }
            guard SettingsManager.sharedInstance.featureApplePay else { return false }
            let appleAcceptedCardTypes = paymentMethod.acceptedCardTypes ?? []
            return PassKitManager.isAppleWalletAllowed(cardTypes: appleAcceptedCardTypes)
        default:
            return true
        }
    }
}

private extension PaymentCard {
    var last4: String {
        cardNumberMasked.lastFourCharacters
    }
}

private extension Card {
    var last4: String {
        token.lastFourCharacters
    }
}

private extension String {
    var lastFourCharacters: String {
        let digits = filter { $0.isNumber }
        let source = digits.isEmpty ? self : digits
        guard source.count > 4 else { return source }

        return String(source.suffix(4))
    }
}

private extension DateFormatter {
    static let paymentCardAccessibilityExpiryFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale.current
        formatter.setLocalizedDateFormatFromTemplate("MMMM yyyy")

        return formatter
    }()
}

private extension PaymentIntervalOption {
    var cccpPaymentTimeMessage: String {
        switch self {
        case .now:
            return PILocalizedString("payNow3CMessage")
        case .later:
            return PILocalizedString("payLater3CMessage")
        case .rwc:
            let isDEHotel = BookingDetails.sharedInstance.hotel?.brand == .premierInnGermany
            return isDEHotel ? PILocalizedString("rwcMessageDE") : PILocalizedString("rwcMessageUK")
        }
    }
}
