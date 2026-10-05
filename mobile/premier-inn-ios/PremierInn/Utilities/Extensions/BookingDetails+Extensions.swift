//
//  BookingDetails+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

extension BookingDetails {
    var isUsingStoredCompanyCard: Bool {
        paymentMethod?.type == .storedCompanyBB
    }

    var isUsingStoredCard: Bool {
        guard let user = UserSessionManager.sharedInstance.currentUser,
              let paymentCard = paymentMethod?.method else { return false }
        return paymentCard == user.centrallyStoredBusinessCard || paymentCard == user.paymentPreference?.card
    }

	func substitutions(forRate rate: Rate) -> [RoomSubstitution] {
		guard let rooms = rate.rooms else { return [] }

        return rooms.enumerated().compactMap { (offset, room) -> RoomSubstitution? in
            let nonSilentRoomOptions = room.options?.filter { $0.silentSubstitution == false }
            guard nonSilentRoomOptions?.isNotEmpty == true else { return nil }
            guard offset < criteria.rooms.count else { return nil }

            let desiredRoomType = criteria.rooms[offset].type
            let substitutedRoomsConcatenated = nonSilentRoomOptions?
                .compactMap { $0.lettingType }
                .map { SettingsManager.sharedInstance.roomLabelFor(lettingType: $0 )}
                .joined(separator: " \(PILocalizedString("or")) ")
            let roomName = PILocalizedString("genericRoomTitle", comment: "") + " " + String(describing: offset + 1)

            return RoomSubstitution(
                desired: desiredRoomType,
                substituted: room.type,
                substitutedRoomsConcatenated: substitutedRoomsConcatenated,
                roomName: roomName
            )
        }
	}

	var mealInformationText: String? {
		mealInformationText(forRate: rate)
	}

	func mealInformationText(forRate rate: Rate?) -> String? {
        // if no qualifying breakfasts (no free kids breakfast) -> don't show message
        // if qualifying breakfast and Meal Deal is available -> show the full message
        // if qualifying breakfast and Meal Deal is not available -> show the plain text with only the qualifying breakfast
        // if qualifying breakfast is only Meal Deal -> show the plain text where %@ is the Meal Deal

		guard let rate = rate, let upsellItems = rate.upsellItems else { return nil }
        let qualifyingBreakfasts = upsellItems.filter { $0.freeBreakfastTrigger == true }
        guard qualifyingBreakfasts.isNotEmpty else { return nil }

        if qualifyingBreakfasts.count == 1 {
            guard let legend = qualifyingBreakfasts.first?.legend else { return nil }

            return String(format: PILocalizedString("upsellsFreeKidsBreakfastDescription"), legend)
        }

        let mealDealIsAvailable = qualifyingBreakfasts.contains(where: { $0.upsellOperaId == .mealDeal })

        // pick a meal other than Meal Deal
        guard let legend = qualifyingBreakfasts.first(where: { $0.upsellOperaId != .mealDeal })?.legend else { return nil }

        return String(
            format: PILocalizedString(mealDealIsAvailable ? "upsellsMealDealOfferDescription" :
                "upsellsFreeKidsBreakfastDescription"),
            legend
        )
	}

    var freeBreakfastCount: Int {
        guard let upsells = rate?.foodUpsells else { return 0 }

        var countedRoomNumbers = Set<Int>()

        return roomMealCombos?.reduce(0, { count, roomMealPreference in
            let (inserted, _) = countedRoomNumbers.insert(roomMealPreference.roomNumber)
            guard inserted else { return count }

            guard self.criteria.rooms.indices.contains(roomMealPreference.roomNumber) else { return count }
            guard let upsell = upsells.first(where: { $0.id == roomMealPreference.meal.id }) else { return count }
            let room = self.criteria.rooms[roomMealPreference.roomNumber]

            return (count ?? 0) + (upsell.freeBreakfastTrigger == true ? room.children : 0)
        }) ?? 0
    }

    var paidBreakfasts: [(upsell: UpsellItem, count: Int)] {
        guard let roomMealCombos = roomMealCombos else { return [] }
        guard let upsells = rate?.foodUpsells else { return [] }

        var tempUpsells: [String: Int] = [:]

        for roomMealCombo in roomMealCombos {
            guard let room = self.criteria.rooms[safe: roomMealCombo.roomNumber] else { continue }
            guard let upsell = upsells.first(where: { $0.id == roomMealCombo.meal.id }) else { continue }

            var runningTotal = tempUpsells[roomMealCombo.meal.id] ?? 0

            runningTotal += roomMealCombo.quantity
            runningTotal += (upsell.kidsHaveToPay == true ? room.children : 0)

            tempUpsells[roomMealCombo.meal.id] = runningTotal
        }

        return tempUpsells.keys.compactMap { key in
            guard let upsell = upsells.first(where: { $0.id == key }) else { return nil }
            guard let amount = tempUpsells[key] else { return nil }

            return (upsell, amount)
        }
    }

    var extraItems: [(upsell: UpsellItem, count: Int)] {
        guard let roomMealCombos = roomExtraPackages else { return [] }
        let upsells = (rate?.extraUpsells ?? []) + (rate?.wifiUpsells ?? [])

        var tempUpsells: [String: Int] = [:]

        for roomMealCombo in roomMealCombos {
            guard self.criteria.rooms.indices.contains(roomMealCombo.roomNumber) else { continue }
            guard upsells.first(where: { $0.id == roomMealCombo.meal.id }) != nil else { continue }

            var runningTotal = tempUpsells[roomMealCombo.meal.id] ?? 0

            runningTotal += 1 // charged once per room

            tempUpsells[roomMealCombo.meal.id] = runningTotal
        }

        return tempUpsells.keys.compactMap { key in
            guard let upsell = upsells.first(where: { $0.id == key }) else { return nil }
            guard let amount = tempUpsells[key] else { return nil }

            return (upsell, amount)
        }
    }

    func bookingMealAmount(for legend: String) -> Int {
        let amount: Int? = roomMealCombos?.filter {
            $0.meal.legend == legend
        }.reduce(0, { runningTotal, roomMealCombo in
            runningTotal + roomMealCombo.quantity
        })

        return amount ?? 0
    }

    /// For Business: Included if part of the allowed upsells AND does not contain non-standard room.
    /// For Leisure: Included if does not contain non-standard room.
    ///
    /// BugFix: [CTECH-4674](https://whitbreadis.atlassian.net/browse/CTECH-4674)
    var isUltimateWifiIncluded: Bool {
        let hasNonStandardClass = roomLettings?
            .flatMap({ $0.options ?? [] })
            .contains(where: { $0.roomClass != Constants.RoomClass.standardRoom })
        ?? false

        return switch bookingMode {
        case .leisure: !hasNonStandardClass
        case .business: (UserSessionManager.sharedInstance.currentUser?.company?.isUltimateWifiAllowed ?? false)
            && !hasNonStandardClass
        }
    }

    func loadPreselectedRateUpsells(upsells: [UpsellItem]) {
        let addedRoomMealCombos = upsells.enumerated().compactMap { ($0.offset, $0.element, $0.element.quantity ?? 1) }
        roomMealCombos = addedRoomMealCombos
    }

    func roomLettingsUsingBookingCriteria(for newRate: Rate) -> [RoomLettingOption] {
        guard let roomLettings = self.roomLettings else { return [] }

        let updatedOptions: [RoomLettingOption] = {
            var newOptionsArray: [RoomLettingOption] = []

            for (index, lettingOption) in roomLettings.enumerated() {
                guard newRate.rooms?.indices.contains(index) == true else { continue }
                guard let newRoom = newRate.rooms?[index] else { continue }
                guard newRoom.type == lettingOption.type else { continue }
                if let newOption = newRoom.options?
                   .first(where: { $0.lettingType == lettingOption.options?.first?.lettingType }) {
                    newOptionsArray.append(newOption)
                }
            }

            return newOptionsArray
        }()

        return updatedOptions
    }

    func roomCostUsingBookingCriteria(for newRate: Rate) -> Cost {
        let updatedOptions = roomLettingsUsingBookingCriteria(for: newRate)
        let currencyCode = updatedOptions.first?.totalCost?.currencyCode ?? "GBP"

        return Cost(
            amount: updatedOptions.compactMap { $0.totalCost?.amount.doubleValue }.reduce(0.0, +),
            currencyCode: currencyCode
        )
    }

    func priceHasIncreased(for newRate: Rate) -> Bool {
        guard let roomLettings = self.roomLettings else { return false }

        let updatedOptions = roomLettingsUsingBookingCriteria(for: newRate)

        guard updatedOptions.count == roomLettings.count else { return false }

        let existingTotal = roomLettings.compactMap { $0.options?.first?.totalCost?.amount.doubleValue }.reduce(0.0, +)
        let updatedTotal = updatedOptions.compactMap { $0.totalCost?.amount.doubleValue }.reduce(0.0, +)

        return updatedTotal > existingTotal
    }

    func update(with company: Company?) {
        guard let questions = company?.businessCardQuestions else { return }
        businessCardQuestionsAndAnswers = questions
    }

    func updateBooking(with preStayInputParams: PreStayInputParams, rooms: [Room]? = nil) {
        if let booker = booker {
            booker.title = preStayInputParams.leadBookerTitle ?? ""
            booker.firstName = preStayInputParams.leadBookerFirstName ?? ""
            booker.lastName = preStayInputParams.leadBookerLastName ?? ""
            booker.emailAddress = preStayInputParams.email
            booker.contactNumber = preStayInputParams.contactNumber
            if !preStayInputParams.isDirect {
                booker.address = preStayInputParams.address
            }
        } else {
            booker = try? User(
                title: preStayInputParams.leadBookerTitle,
                firstName: preStayInputParams.leadBookerFirstName,
                lastName: preStayInputParams.leadBookerLastName,
                email: preStayInputParams.email,
                telephone: preStayInputParams.contactNumber
            )
            if !preStayInputParams.isDirect {
                booker?.address = preStayInputParams.address
            }
        }

        isBookingHold = true
        if let rooms {
            criteria.rooms = rooms
        } else {
            criteria.rooms = preStayInputParams.rooms
        }
    }

    func bookingAllowed(for card: PaymentCard) -> Bool {
        let acceptedCardCodes = hotel?.acceptedCreditCards?.compactMap { $0.cardCode } ?? []
        return acceptedCardCodes.contains(card.cardType.cardCode) && !card.expired(onDate: criteria.arrivalDate)
    }

    func updateDefaultCardForBooking() {
        let defaultMethod: (type: PaymentMethodType, card: PaymentCard?) = {
            let user: User? = UserSessionManager.sharedInstance.currentUser

            if bookingMode == .business, let companyCard = user?.centrallyStoredBusinessCard,
               bookingAllowed(for: companyCard) == true {
                if userCanChooseWhenToPay == true || companyCard.isBusiness {
                    return (.storedCompanyBB, companyCard)
                }
            }

            if let personalCard = user?.paymentPreference?.card, bookingAllowed(for: personalCard) == true {
                if bookingMode == .leisure || user?.company?.bookingAllowances?.allowIndividualCards == true {
                    return (bookingMode == .leisure ? .stored : .storedPersonalBB, personalCard)
                }
            }

            if let currentCard = paymentMethod?.method, currentCard != user?.paymentPreference?.card,
               currentCard != user?.centrallyStoredBusinessCard, bookingAllowed(for: currentCard) == true {
                return (currentCard.isBusiness ? .newBAC : .newCreditDebitCard, currentCard)
            }

            return (.newCreditDebitCard, nil)
        }()

        paymentMethod = PaymentMethod(type: defaultMethod.type, method: defaultMethod.card)
    }

    var shouldShowRestaurantAllowancesForCNP: Bool {
        switch primaryPaymentMethod?.paymentMethodType {
        case .newBAC, .newBACEuro:
            return hotel?.cnpAuthorisation?.dinnerAvailable ?? true
        case .stored, .storedCompanyBB, .storedPersonalBB:
            return paymentMethod?.method?.isBusiness == true ?
                hotel?.cnpAuthorisation?.dinnerAvailable ?? true : hotel?.cnpAuthorisation?.dinnerAvailableNonBa ?? false
        default:
            return false
        }
    }

    var shouldShowEmployeeQuestionsForOpera: Bool {
        SettingsManager.sharedInstance.employeeQuestionsOperaFeature
    }

    var shouldShowDonations: Bool {
        SettingsManager.sharedInstance.featureDonations && self.bookingMode != .business
    }

    var disabledPaymentMethods: [PaymentOption]? {
        BookingDetails.sharedInstance.paymentMethods?.filter({ $0.enabled == false })
    }

    var enabledPaymentMethods: [PaymentOption]? {
        BookingDetails.sharedInstance.paymentMethods?.filter({ $0.enabled == true })
    }

    // When all payment options are down, only RWC is available.
    var isPaymentDown: Bool {
        enabledPaymentMethods?.count == 1 && enabledPaymentMethods?
            .first(where: { $0.type == PaymentIntervalOption.rwc.cccpType }) != nil
    }
}

// default card selection for booking flow
extension BookingDetails {
    private func hotelAccepts(cardTypeCode: String) -> Bool? {
        guard let acceptedCardCodes = (hotel?.acceptedCreditCards?.compactMap { $0.cardCode }) else { return nil }
        return acceptedCardCodes.contains(cardTypeCode)
    }

    func updatePaymentMethodIfRequired(for rate: Rate) {
        if BookingDetails.sharedInstance.bookingMode == .leisure {
            guard let card = paymentMethod?.method else { return }
            if card
               .expired(onDate: criteria.arrivalDate) == true || hotelAccepts(cardTypeCode: card.cardType.cardCode) !=
               true {
                paymentMethod = PaymentMethod(type: .newCreditDebitCard, method: nil)
                return
            }
            paymentMethod = PaymentMethod(type: .stored, method: card)
            return
        }

        updatePaymentMethodForBusinessIfRequired(for: rate)
    }

    private func updatePaymentMethodForBusinessIfRequired(for rate: Rate) {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return }
        guard let company = user.company else { return }

        let updatedPaymentMethod: PaymentMethod = {
            if let companyCard = user.centrallyStoredBusinessCard, canUserBookForBusiness(
                selected: rate,
                storedCard: companyCard,
                isCentralCard: true
            ) == true {
                return PaymentMethod(type: .storedCompanyBB, method: companyCard)
            }

            if let personalCard = user.paymentPreference?.card, company.bookingAllowances?.allowIndividualCards == true,
               canUserBookForBusiness(
                   selected: rate,
                   storedCard: personalCard,
                   isCentralCard: false
               ) == true {
                return PaymentMethod(type: .storedPersonalBB, method: personalCard)
            }

            if let newCard = paymentMethod?.method, company.bookingAllowances?.allowIndividualCards == true,
               canUserBookForBusiness(
                   selected: rate,
                   storedCard: newCard,
                   isCentralCard: false
               ) == true {
                return PaymentMethod(type: .newCreditDebitCard, method: newCard)
            }

            return PaymentMethod(type: .newCreditDebitCard, method: nil)
        }()

        paymentMethod = updatedPaymentMethod
    }

    private func canUserBookForBusiness(selected rate: Rate, storedCard card: PaymentCard, isCentralCard: Bool) -> Bool {
        guard card.expired(onDate: criteria.checkOutDate ?? Date()) == false else { return false }
        guard hotelAccepts(cardTypeCode: card.cardType.cardCode) == true else { return false }
        guard isCentralCard == true else { return true }
        // All centrlly stored cards must be pay on arrival, and only a PIBA can make a prepay rate pay on arrival
        guard card.isBusiness == true else { return false }

        return true
    }
}

// For analytics
extension BookingDetails {
    var selectedUpsellsForAnalytics: String {
        let foods = roomMealCombos?.compactMap { $0.meal.legend }
        let extras = roomExtraPackages?.compactMap { $0.meal.legend }
        let totalUpsells = (foods ?? []) + (extras ?? [])
        return Set(totalUpsells).map { $0 }.joined(separator: ", ")
    }

    private var combinedShownUpsells: [UpsellItem]? {
        guard let foodUpsells = rate?.foodUpsells,
              let extraUpsells = rate?.extraUpsells else { return nil }
        let availableExtraUpsells = extraUpsells.filter { $0.availableCount ?? 0 > criteria.rooms.count }
        return foodUpsells + availableExtraUpsells
    }

    func getShownUpsellsIdsForAnalytics() -> String {
        guard let combinedUpsells = combinedShownUpsells else { return "" }
        return (combinedUpsells.map { $0.id }).joined(separator: ", ")
    }

    func getShownUpsellsNamesForAnalytics() -> String {
        guard let combinedUpsells = combinedShownUpsells else { return "" }
        return (combinedUpsells.map { $0.legend }).joined(separator: ", ")
    }
}
    // MARK: - PIBA Booking Helpers
    extension BookingDetails {
        /// Checks if the current booking uses a PIBA (Premier Inn Business Account) card.
        /// PIBA bookings show a pop-up confirmation dialog during check-in.
        var isPIBABooking: Bool {
            guard let primaryPaymentMethod = self.primaryPaymentMethod else { return false }
            return primaryPaymentMethod.paymentMethodType == .newBAC ||
                   primaryPaymentMethod.card?.type.isBusiness == true
        }
        /// Detects PIBA CNP bookings based on payment method
        /// Used as temporary fallback in Stay.paymentOption until backend implements paymentOption field
        /// Backend will control PIBA CNP via Stay.paymentOption enum and isCheckInOnlineAvailable flag
        var isPIBACNP: Bool {
            guard let primaryPaymentMethod = self.primaryPaymentMethod else { return false }
            return primaryPaymentMethod.subType == "PIBAGB" &&
                   primaryPaymentMethod.paymentMethodType == .newBAC
        }
    }
