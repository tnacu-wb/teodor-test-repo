//
//  PaymentMethodsInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

typealias PaymentMethodsTracking = (staName: String, screenType: String)

protocol PaymentMethodsInteractorDelegate: AnyObject {
    func cardDelete()
	func cardDidUpdate()
}

class PaymentMethodsInteractor {
	weak var delegate: PaymentMethodsInteractorDelegate?

	private let user: User?
    private var personalCard: PaymentCard? {
        didSet {
            UserSessionManager.sharedInstance.currentUser?.paymentPreference?.card = personalCard
        }
    }
    private let businessCard: PaymentCard?
	private let requestsManager = RequestsManager()

    internal let scope: PaymentMethodsScope
    private var canSelectCards: Bool
    private let departureDate: Date

    /// For Business Booker we need to know if the rate is pay now.
    private let isPrepaymentRequired: Bool
    private let acceptedCardTypes: [CardType]?

	deinit {
		requestsManager.cancelConnections()
        NotificationCenter.default.removeObserver(self, name: .creditCardDidChange, object: nil)
	}

    init(with user: User, scope: PaymentMethodsScope = .myPI, bookingParams: PaymentMethodsBookingParameters? = nil) {
        self.user = user
        self.personalCard = user.paymentPreference?.card
        self.businessCard = user.centrallyStoredBusinessCard
        self.canSelectCards = scope == .bookingFlow
        self.scope = scope
        self.isPrepaymentRequired = (scope == .bookingFlow) ? bookingParams?.isPrepaymentRequired ?? false : false
        self.acceptedCardTypes = bookingParams?.acceptedCardTypes
        self.departureDate = bookingParams?.departureDate ?? Date()
        // If there isn't a personal card, but an alternative one was entered then show it with the personal tag
        if let paymentCard = BookingDetails.sharedInstance.paymentMethod?.method,
           self.personalCard == nil && paymentCard != self.businessCard {
            self.personalCard = paymentCard
        }

		NotificationCenter.default.addObserver(
		    self,
		    selector: #selector(creditCardDidChange),
		    name: .creditCardDidChange,
		    object: nil
		)
    }

    // This is replacing personal card with updated card regardless of whether card was being stored in backend 😩
    // Added booking mode check so it doesn't break bb stored card bookings
	@objc private func creditCardDidChange(notification: Notification) {
		guard let updatedCard = notification.userInfo?["card"] as? PaymentCard else { return }
        guard BookingDetails.sharedInstance.bookingMode == .leisure else { return }

		self.personalCard = updatedCard

		delegate?.cardDidUpdate()
	}
}

extension PaymentMethodsInteractor: PaymentMethodsInteractorProtocol {
    private var sessionManager: UserSessionManager {
        UserSessionManager.sharedInstance
    }

    private var bookingDetails: BookingDetails {
        BookingDetails.sharedInstance
    }

    private var isInPersonalCardDetailsAsRetailCustomer: Bool {
        scope == .myPI &&
        sessionManager.currentUser?.company == nil
    }

    var paymentMethodsTracking: PaymentMethodsTracking {
        (
            PIAnalytics.StateNames.manageCards,
            PIAnalytics.StateTypes.myPI
        )
    }

    var customAnalyticsParameters: PIDictionary? {
        let savedCards: [PaymentCard] = {
            var cards: [PaymentCard] = []

            if let businessCard = businessCard {
                cards.append(businessCard)
            }
            if let personalCard = personalCard {
                cards.append(personalCard)
            }

            return cards
        }()

        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.productString] = ";\(BookingDetails.sharedInstance.hotel?.code ?? "")"
        data[PIAnalytics.Keys.bfSavedCards] = String(describing: savedCards.count)
        data[PIAnalytics.Keys.bfCardTypes] = savedCards.compactMap { $0.cardType.cardCode }.joined(separator: ",")
        data[PIAnalytics.Keys.bfExpiredCards] = String(describing: savedCards.filter { $0.expired(onDate: Date()) }.count)

        return data
    }

    var sections: [PaymentMethodsCardSection] {
        var sections = [PaymentMethodsCardSection]()

        if let businessCardSection {
            sections.append(businessCardSection)
        }

        if let personalCardSection {
            sections.append(personalCardSection)
        }

        return sections
    }

    private var personalCardSection: PaymentMethodsCardSection? {
        guard let card = personalCard else {
            return nil
        }

        let cardName = card.cardLabel ?? card.cardNameDescription
        let usageDescription = PILocalizedString("paymentMethodsPersonalCard")
        let accessibilityLabel = card.accessibilityDescription(
            cardName: cardName,
            usageDescription: usageDescription
        )

        let paymentMethodLink = PaymentMethodsLink(
            title: PILocalizedString("paymentMethodsReplaceCardActionTitle"),
            color: .BasePurple,
            action: .edit
        )

        let links = isInPersonalCardDetailsAsRetailCustomer ? [paymentMethodLink] : []

        let section = PaymentMethodsCardSection(
            cardName: cardName,
            cardType: PILocalizedString("paymentMethodsPersonalCard"),
            cardHiddenNumber: card.cardNumberSummary,
            cardHolderName: card.cardholderName,
            cardExpiration: card.expiryDateCardFormat,
            links: links,
            usageDescription: usageDescription,
            cardImageURL: card.cardType.imageURL,
            selectable: personalCardSelectable,
            selected: canSelectCards ? bookingDetails.paymentMethod?.method == personalCard : false,
            cardInfoMessage: personalCardInfoMessage,
            isBookingFlow: scope == .bookingFlow,
            pibaMessaging: getPibaMessaging(cardCode: personalCard?.cardType.cardCode),
            isDeleteHidden: true,
            accessibilityLabel: accessibilityLabel
        )

        return section
    }

    private var businessCardSection: PaymentMethodsCardSection? {
        guard let card = businessCard, user?.company?.allowCentralCreditCard == true else {
            return nil
        }

        let cardName = card.cardLabel ?? card.cardNameDescription
        let usageDescription = PILocalizedString("paymentMethodsBusinessCard")
        let accessibilityLabel = card.accessibilityDescription(
            cardName: cardName,
            usageDescription: usageDescription
        )

        let section = PaymentMethodsCardSection(
            cardName: cardName,
            cardType: PILocalizedString("paymentMethodsBusinessCard"),
            cardHiddenNumber: card.cardNumberSummary,
            cardHolderName: card.cardholderName,
            cardExpiration: card.expiryDateCardFormat,
            links: [],
            usageDescription: usageDescription,
            cardImageURL: card.cardType.imageURL,
            selectable: businessCardSelectable,
            selected: canSelectCards ? bookingDetails.paymentMethod?.method == businessCard : false,
            cardInfoMessage: businessCardInfoMessage,
            isBookingFlow: scope == .bookingFlow,
            pibaMessaging: getPibaMessaging(cardCode: businessCard?.cardType.cardCode),
            isDeleteHidden: true,
            accessibilityLabel: accessibilityLabel
        )

        return section
    }

    // Business Card

    private var businessCardSelectable: Bool {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return false }
        guard let company = user.company else { return false }
        guard businessCard?.expired(onDate: departureDate) == false else { return false}

        let centralCreditCardAllowed = company.allowCentralCreditCard ?? false
        let personalCardAllowed = company.bookingAllowances?.allowIndividualCards ?? false
        let isBusiness = businessCard?.isBusiness ?? false // CSC is allocated if businessCard != nil

        if isBusiness == true, let acceptedCardTypes = acceptedCardTypes {
            let acceptedCardTypeCodes = acceptedCardTypes.compactMap { $0.cardCode }
            guard acceptedCardTypeCodes.contains("AT") else { return false }
        }

        switch (isPrepaymentRequired, centralCreditCardAllowed, isBusiness, personalCardAllowed) {
        case (true, true, false, true):
            return false
        default:
            return canSelectCards
        }
    }

    private var businessCardInfoMessage: String? {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return nil }
        guard let company = user.company else { return nil }
        guard businessCard?.expired(onDate: departureDate) == false
            else { return PILocalizedString("hotelDetailsBBCompanyCardExpiredTitle") }

        let isAuthenticationRequired = true // always true for 3CP
        let centralCreditCardAllowed = company.allowCentralCreditCard ?? false
        let personalCardAllowed = company.bookingAllowances?.allowIndividualCards ?? false
        let isBusiness = businessCard?.isBusiness ?? false // CSC is allocated if businessCard != nil

        switch (isPrepaymentRequired, centralCreditCardAllowed, isBusiness, personalCardAllowed, isAuthenticationRequired) {
        case (_, true, false, _, true):
            return PILocalizedString("paymentMethodsCVVRequiredBBMessage")
        case (true, true, true, true, _):
            return PILocalizedString("paymentMethodsBBCSCExistsPersonalAllowedManager")
        case (true, true, false, true, _):
            return PILocalizedString("paymentMethodsBBCSCBACExistsPersonalAllowedManager")
        default:
            return nil
        }
    }

    // Personal Card

    private var personalCardSelectable: Bool {
        guard personalCard?.expired(onDate: departureDate) == false else { return false }
        guard let user = UserSessionManager.sharedInstance.currentUser else { return canSelectCards }
        guard user.company != nil else { return canSelectCards }
        guard let personalCard = personalCard else { return canSelectCards }
        guard personalCard.isBusiness == true else { return canSelectCards }
        guard let acceptedCardTypes = acceptedCardTypes else { return canSelectCards }

        return acceptedCardTypes.compactMap { $0.cardCode }.contains("AT") ? canSelectCards : false
    }

    private var personalCardIsBACAndNotAcceptedMessage: String? {
        guard let card = personalCard else { return nil }
        guard card.isBusiness == true else { return nil }
        guard let acceptedCardTypes = acceptedCardTypes else { return nil }

        return acceptedCardTypes.compactMap { $0.cardCode }
            .contains("AT") ? nil : PILocalizedString("paymentMethodsBACNotAcceptedAtHotel")
    }

    private var personalCardInfoMessage: String? {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return nil }
        guard let company = user.company else { return nil }
        return company.bookingAllowances?.allowIndividualCards == true ? personalCardIsBACAndNotAcceptedMessage : user
            .accessLevel == .superUser ? PILocalizedString("paymentMethodsBBPersonalStoredCardButNoPermissionManager") :
            PILocalizedString("paymentMethodsBBPersonalStoredCardButNoPermissionNonManager")
    }

    private func getPibaMessaging(cardCode: String?) -> String? {
        let pibaBanner = cardCode == SimpleNetwork.Constants.PaymentCardCodes.businessCard
        let pibaEuroBanner = cardCode == SimpleNetwork.Constants.PaymentCardCodes
            .businessCardEuro// CSC is allocated if businessCard != nil

        switch(pibaEuroBanner, pibaBanner) {
        case(_, true):
            return PILocalizedString("paymentMethodsPibaCardNotSupportedInDE")
        case(true, _):
            return PILocalizedString("paymentMethodsPibaEuroNotSupportedInUK")
        case (false, false):
            return nil
        }
    }
    // cta

    private var ctaTitleForScope: String {
        scope == .myPI ? PILocalizedString("+ Add new card") : PILocalizedString("Use alternative card")
    }

    var ctaTitle: String? {
        guard scope == .bookingFlow else { return nil }
        guard BookingDetails.sharedInstance.bookingMode == .business else { return ctaTitleForScope }

        return user?.company?.bookingAllowances?.allowIndividualCards == true ? ctaTitleForScope : nil
    }

    var infoFooterMessage: String? {
        paymentMethodsInfoMessage
    }

    private var paymentMethodsInfoMessage: String? {
        guard BookingDetails.sharedInstance.bookingMode == .business
            else { return PILocalizedString("paymentMethodsInfoFooterText") }
        guard let user = UserSessionManager.sharedInstance.currentUser else { return nil }
        guard let company = user.company else { return nil }

        let userIsTravelManager = UserSessionManager.sharedInstance.currentUser?.accessLevel == .superUser
        let personalCardAllowed = company.bookingAllowances?.allowIndividualCards ?? false
        let personalCardExists = personalCard != nil
        let cscAllocated = businessCard != nil

        switch (userIsTravelManager, personalCardAllowed, personalCardExists, cscAllocated) {
        case (true, true, true, true):
            return PILocalizedString("paymentMethodsBBAllCardsAllowedManager")
        case (false, true, true, true):
            return PILocalizedString("paymentMethodsBBAllCardsAllowedNonManager")
        case (true, true, true, false), (false, true, true, false):
            return PILocalizedString("paymentMethodsBBNoCSCPersonalExistsAndAllowed")
        case (true, true, false, false), (false, true, false, false):
            return PILocalizedString("paymentMethodsBBNoCSCNoPersonalAndAllowed")
        case (true, false, false, false):
            return PILocalizedString("paymentMethodsBBNoCSCPersonalNotAllowedManager")
        case (false, false, false, false):
            return PILocalizedString("paymentMethodsBBNoCSCPersonalNotAllowedNonManager")
        case (true, false, false, true), (true, false, true, true):
            return PILocalizedString("paymentMethodsBBCSCExistsPersonalNotAllowedManager")
        case (false, false, false, true), (false, false, true, true):
            return PILocalizedString("paymentMethodsBBCSCPersonalNotAllowedNonManager")
        case (true, true, false, true):
            return PILocalizedString("paymentMethodsBBCSCNoPersonalAndAllowedManager")
        case (false, true, false, true):
            return PILocalizedString("paymentMethodsBBCSCNoPersonalAndAllowedNonManager")
        default:
            return nil
        }
    }

    // Actions

	func delete(section: PaymentMethodsCardSection, completion: @escaping (Bool, Error?) -> Void) {
        guard let user = user else {
            completion(false, nil)
            return
        }

        UserSessionManager.sharedInstance.refreshUser { [weak self] success, error in
            guard success else { return completion(false, error) }

			let sensorData = AkamaiProtection.sensorData

			self?.requestsManager.deletePaymentCard(for: user, sensorData: sensorData) { (success, error) in
                if success {
                    var dataToTrack: PIDictionary?
                    if let cardType = UserSessionManager.sharedInstance.currentUser?.paymentPreference?.card?.cardType {
                        dataToTrack =
                            [PIAnalytics.Keys
                            .cardType: "\(cardType.cardCode):\(cardType.cardFee == nil ? "NO CCHF" : "CCHF")"]
                    }
                    AnalyticsManager.shared.trackAction(PIAnalytics.Action.removeCard, userInfo: dataToTrack)

                    user.paymentPreference?.card = nil
                    BookingDetails.sharedInstance.paymentMethod?.method = nil
                    BookingDetails.sharedInstance.booker?.paymentPreference?.card = nil
                }
                completion(success, error)
            }
        }
	}

	func cardForSection(section: PaymentMethodsCardSection) -> PaymentCard? {
		// We will have to add some logic when we add business card support
		personalCard
	}

    func shouldCheckWithUserBeforeChanging(to cardAtIndex: Int) -> Bool {
        guard canSelectCards else { return false }
        guard BookingDetails.sharedInstance.bookingMode == .business else { return false }

        if let currentCard = BookingDetails.sharedInstance.paymentMethod?.method, currentCard == businessCard,
           cardAtIndex > 0 {
            guard let card = card(at: cardAtIndex) else { return false }
            guard card == personalCard else { return false }
            return card.cardNotPresentRequired ?? false == false
        }
        return false
    }

    private func card(at index: Int) -> PaymentCard? {
        if businessCard != nil, user?.company?.allowCentralCreditCard == true {
            return index == 0 ? businessCard : personalCard
        }
        return personalCard
    }

    func selectedCard(at index: Int) {
        guard canSelectCards else { return }

        let selectedCard: PaymentCard? = card(at: index)

        BookingDetails.sharedInstance.paymentMethod?.method = selectedCard
    }
}
