//
//  BookingDetails.swift
//  PremierInn
//
//  Created by Freddie Parks on 01/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public typealias RoomMealCombo = (roomNumber: Int, meal: UpsellItem, quantity: Int)

public enum BookingMode: String {
    case leisure = "leisure"
    case business = "business-booker"
}

public enum PaymentIntervalOption: String {
    case now = "paymentOptionPayNow"
    case later = "paymentOptionPayOnArrival"
    case rwc = "paymentOptionRWC"

    public var cccpType: String {
        switch self {
        case .now:
            return "PAY_NOW"
        case .later:
            return "PAY_ON_ARRIVAL"
        case .rwc:
            return "RESERVE_WITHOUT_CARD"
        }
    }
}

public enum TripPurpose: String {
    case leisure = "tripPurposeLeisure"
    case business = "tripPurposeBusiness"

    var operaReasonForStay: String {
        switch self {
        case .leisure:
            return "LEI"
        case .business:
            return "BUS"
        }
    }

    public var analyticsString: String {
        switch self {
        case .leisure:
            return "Leisure"
        case .business:
            return "Business"
        }
    }
}

public enum BusinessCardQuestionType {
    case customerReference
    case purchaseOrder
    case custom
}

public enum PaymentMethodType: String {
    case newCreditDebitCard = "NEW_CARD"
    case newBAC = "NEW_PIBA"
    case newBACEuro = "NEW_PIBA_EURO"
    case stored = "LEISURE_STORED_CARD"
    case storedPersonalBB = "BUSINESS_PERSONAL_STORED_CARD"
    case storedCompanyBB = "BUSINESS_CENTRALLY_STORED_CARD"
    case applePay = "AP"
    case paypal = "PAYPAL"
    case reserveWithoutCard = "RESERVE_WITHOUT_CARD"

    public var isStored: Bool {
        switch self {
        case .stored, .storedPersonalBB, .storedCompanyBB:
            return true
        default:
            return false
        }
    }

    public var getAccessbilityIdentifier: String {
        switch self {
        case .newBAC:
            return "newBusinessCardCell"
        case .newCreditDebitCard:
            return "newCreditDebitCardCell"
        case .stored:
            return "storedCardCell"
        case .storedCompanyBB:
            return "storedCompanyCardBBCell"
        case .storedPersonalBB:
            return "storedPersonalCardBBCell"
        case .applePay:
            return "applePayCell"
        case .paypal:
            return "paypalVaultCell"
        case .reserveWithoutCard:
            return "reserveWithoutCardCell"
        case .newBACEuro:
            return "newBusinessEuroCardCell"
        }
    }
}

public struct PaymentMethod {
    public let type: PaymentMethodType
    public var method: PaymentCard?

    public init(type: PaymentMethodType, method: PaymentCard?) {
        self.type = type
        self.method = method
    }
}

public struct BusinessCardQuestionAndAnswer {
    public let type: BusinessCardQuestionType
    public let question: CompanyManagementQuestion

    public var answer: String?

    public init(type: BusinessCardQuestionType, question: CompanyManagementQuestion, answer: String?) {
        self.type = type
        self.question = question
        self.answer = answer
    }
}

public class BookingDetails {
	public static let sharedInstance = BookingDetails()

    public var primaryPaymentMethod: PaymentOption?
    public var paymentMethods: [PaymentOption]?
    public var paymentMethod: PaymentMethod?
    public var meal: UpsellItem?
    public var roomMealCombos: [RoomMealCombo]?
    public var roomExtraPackages: [RoomMealCombo]?
    public var booker: User?
    public var token: String?
    public var basketReference: String?
    public var isBookingHold = false
    public var hotel: Hotel?
    public var rate: Rate? {
        didSet {
            guard let foodPreferenceCode = booker?.bookingPreference?.foodPreference else { return }
            meal = rate?.upsellItems?.first { $0.id == foodPreferenceCode.id }
        }
    }
    public var roomLettings: [Room]?
    public var criteria = Criteria(arrivalDate: Date().dateByAddingUnit(unitType: .day, number: Constants.arrivalDateOffset))
    public var userSelectedPaymentOption: PaymentIntervalOption?
    public var purpose: TripPurpose?
    var billingAddressIsBookerAddress = true
    public var bookingFlowHoldTime: Date?
    public var cvv: String?
    public var businessCardQuestionsAndAnswers = Constants.CMS.businessCardQuestionsAndAnswers
    public var cbtAnswers: [PIDictionary]?
    public var paymentFailureCount = 0
    public var businessAccount: BusinessAccount?
    public var goshDonation: Cost?
    public var employeeRatesEnabled = false
    public var marketingOptIn = false

    /// This is to prevent duplication of CIOL third party Background Charge (mutation backgroundCharge) call
    public var isCiolBackgroundChargePerformedSuccessfully = false

    public var isBookerStaying: Bool?

    // Opera
    public var previousRoomMealCombos: [RoomMealCombo]?
    public var previousRoomExtraPackages: [RoomMealCombo]?
    public var operaConfirmedTotalCost: Cost?
    public var operaBookingReference: String?
    public var goshOptions: GoshPackage?
    public var selectedGoshPackage: String?
    public var appIncentivePromoCode: String?
    public var freeBreakfastPromoCode: String?
    public var siteWidePromoCode: String?
    public var userEnteredPromoCode: String?
    public var promoKind: String?
    public var promoRateTags: String?
    public var shouldRequestCot = false

    // Computed properties
    public var roomAndMealCost: Cost? {
        guard let roomsCost = roomLettings?.totalCost else { return nil }
        guard let mealTotalCost = mealTotalCost else { return roomsCost }

        return roomsCost + mealTotalCost
    }

    public var bookingMode: BookingMode {
        UserSessionManager.sharedInstance.currentUser?.company != nil ? .business : .leisure
    }

    public var bookingChannel: Channel {
        (bookingMode == .leisure) ? .PI : .BB
    }

    public var totalCost: Cost {
        guard let currency = roomLettings?.totalCost?.currencyCode else { return .zeroPounds }

        let costs = [roomLettings?.totalCost, mealTotalCost, extrasTotalCost, goshDonation]

        return costs.reduce(Cost(amount: 0, currencyCode: currency)) { (result, cost) -> Cost in
            guard let cost = cost, let sum = result + cost else { return result }
            return sum
        }
    }

    public var totalCostOperaWithDonations: Cost? {
        if let donationAmount = goshDonation, let totalCost = operaConfirmedTotalCost {
            return totalCost + donationAmount
        }
        return operaConfirmedTotalCost
    }

    public var earlyCheckInCost: Cost? {
        guard let roomExtraPackages = roomExtraPackages else { return nil }

        let eci = roomExtraPackages.first(where: { $0.meal.id == UpsellItemOperaId.earlyCheckIn.rawValue })
        let eciCost = eci?.meal.price.amount.doubleValue ?? 0.0
        let currencyCode = eci?.meal.price.currencyCode ?? "GBP"

        return Cost(amount: eciCost, currencyCode: currencyCode)
    }

    public var lateCheckOutCost: Cost? {
        guard let roomExtraPackages = roomExtraPackages else { return nil }

        let lco = roomExtraPackages.first(where: { $0.meal.id == UpsellItemOperaId.lateCheckOut.rawValue })
        let lcoCost = lco?.meal.price.amount.doubleValue ?? 0.0
        let currencyCode = lco?.meal.price.currencyCode ?? "GBP"

        return Cost(amount: lcoCost, currencyCode: currencyCode)
    }

    public var mealTotalCost: Cost? {
        guard let roomMealCombos = roomMealCombos else { return nil }
        guard let currencyCode = roomMealCombos.first?.meal.price.currencyCode else { return nil }

        let runningTotal: Double = roomMealCombos.reduce(0.0, { total, roomMealCombo in
            guard criteria.rooms.indices.contains(roomMealCombo.roomNumber) else { return total }

            return total + (roomMealCombo.meal.price.amount.doubleValue * Double(roomMealCombo.quantity))
        })

        return Cost(amount: runningTotal * Double(criteria.nights), currencyCode: currencyCode)
    }

    public var hasSeenMidFlowBBLoginPrompt: Bool?

    public var extrasTotalCost: Cost? {
        guard let roomExtraPackages = roomExtraPackages else { return nil }
        guard let currencyCode = roomExtraPackages.first?.meal.price.currencyCode else { return nil }

        let runningTotal: Double = roomExtraPackages.reduce(0.0, { total, roomExtraPackage in
            guard criteria.rooms.indices.contains(roomExtraPackage.roomNumber) else { return total }

            return total + (roomExtraPackage.meal.price.amount.doubleValue)
        })

        return Cost(amount: runningTotal, currencyCode: currencyCode)
    }

    public var isPromotionalBooking: Bool {
        appIncentivePromoCode?.isEmpty == false ||
        freeBreakfastPromoCode?.isEmpty == false ||
        siteWidePromoCode?.isEmpty == false ||
        userEnteredPromoCode?.isEmpty == false ||
        rate?.promotionCode?.isEmpty == false
    }

	public init() { }

    public init?(reservation: Reservation) {
        guard let arrivalDate = reservation.arrivalDate else { return nil }
        self.criteria = setCriteria(numberOfNights: reservation.nights, arrivalDate: arrivalDate, rooms: reservation.rooms)
    }

    public init(numberOfNights: Int, arrivalDate: Date) {
        self.criteria = setCriteria(numberOfNights: numberOfNights, arrivalDate: arrivalDate)
    }

    private func setCriteria(numberOfNights: Int, arrivalDate: Date, rooms: [Room]? = nil) -> Criteria {
        var criteria = Criteria()
        criteria.arrivalDate = arrivalDate
        criteria.nights = numberOfNights
        if let rooms = rooms {
            criteria.rooms = rooms.copy()
        }
        self.criteria = criteria
        return criteria
    }

    public func isCardTypeAccepted(paymentCardType: String) -> Bool {
        guard let acceptedCards = hotel?.acceptedCreditCards else { return false }
        guard (acceptedCards.first { $0.cardCode == paymentCardType }) != nil else { return false }

        return true
    }

    func resetPaymentMethod() {
        paymentMethod = {
            if let paymentCard = UserSessionManager.sharedInstance.currentUser?.centrallyStoredBusinessCard {
                return PaymentMethod(type: .storedCompanyBB, method: paymentCard)
            }
            if let paymentCard = UserSessionManager.sharedInstance.currentUser?.paymentPreference?.card {
                let type = bookingMode == .business ? PaymentMethodType.storedPersonalBB : .stored
                return PaymentMethod(type: type, method: paymentCard)
            }

            return nil
        }()
    }

    public func reset() {
        basketReference = nil
        isBookingHold = false
        hotel = nil
        rate = nil
        meal = nil
        resetPaymentMethod()
        roomLettings = nil
        criteria = {
            var result = Criteria(arrivalDate: Date().dateByAddingUnit(unitType: .day, number: Constants.arrivalDateOffset))

            if let room = UserSessionManager.sharedInstance.currentUser?.bookingPreference?.roomRequirements?.room {
                result.rooms = [room]
            }

            return result
        }()
        roomMealCombos = nil
        roomExtraPackages = nil
        previousRoomMealCombos = nil
        previousRoomExtraPackages = nil
        userSelectedPaymentOption = nil
        cvv = nil
        billingAddressIsBookerAddress = true
        paymentFailureCount = 0
        purpose = nil
        goshDonation = nil
        businessCardQuestionsAndAnswers = UserSessionManager.sharedInstance.currentUser?.company?
            .businessCardQuestions ?? Constants.CMS.businessCardQuestionsAndAnswers
        businessAccount = nil
        hasSeenMidFlowBBLoginPrompt = nil
        cbtAnswers = nil
        operaConfirmedTotalCost = nil
        operaBookingReference = nil
        selectedGoshPackage = nil
        goshOptions = nil
        isBookerStaying = nil
        shouldRequestCot = false
    }

    public func bookingReleased() {
        basketReference = nil
        isBookingHold = false
        previousRoomMealCombos = nil
        previousRoomExtraPackages = nil
        roomMealCombos = nil
        roomExtraPackages = nil
        selectedGoshPackage = nil
        goshOptions = nil
        shouldRequestCot = false
    }
}

public extension BookingDetails {
    var paymentOption: PaymentIntervalOption {
        if let option = userSelectedPaymentOption, userCanChooseWhenToPay {
            return option
        }

        guard let enabledOptions = BookingDetails.sharedInstance.primaryPaymentMethod?.paymentOptions?
              .filter({ $0.enabled }) else {
            return .later
        }
        if enabledOptions.first(where: { $0.type == "PAY_ON_ARRIVAL" }) != nil {
            return .later
        } else if enabledOptions.first(where: { $0.type == "RESERVE_WITHOUT_CARD" }) != nil {
            return .rwc
        } else {
            return .now
        }
    }

    var userCanChooseWhenToPay: Bool {
        BookingDetails.sharedInstance.primaryPaymentMethod?.paymentOptions?.filter { !$0.enabled }.isEmpty ?? false
    }
}

enum BookingFlowId {
    static let PI = "booking-a1"
    static let PID = "booking-ct-a1"
    static let HUB = "booking-hub"
    static let ZIP = "booking-zip"
    static let BB = "booking-business"
    static let BBHUB = "booking-business-hub"

    static func getBookingFlowId(hotelBrand: HotelBrand?, isBusiness: Bool) -> String? {
        switch hotelBrand {
        case .premierInn:
            return isBusiness == true ? BB : PI
        case .premierInnGermany:
            return isBusiness == true ? BB : PID
        case .hub:
            return isBusiness == true ? BBHUB : HUB
        case .zip:
            return ZIP
        case .none:
            return nil
        }
    }
}

public struct OperaBusinessCardQuestionAndAnswer {
    let type: BusinessCardQuestionType
    let operaQuestionAndAnswer: OperaQuestionAndAnswer
}

public struct OperaQuestionAndAnswer: Codable {
    let question: String
    let answer: String
}

public extension BookingDetails {
    var operaBusinessQuestionsAndAnswers: [OperaBusinessCardQuestionAndAnswer]? {
        let operaQAndA = businessCardQuestionsAndAnswers.compactMap({
            if let question = $0.question.label, let answer = $0.answer {
                return OperaBusinessCardQuestionAndAnswer(
                    type: $0.type,
                    operaQuestionAndAnswer: OperaQuestionAndAnswer(question: question, answer: answer)
                )
            }
            return nil
        })
        return operaQAndA
    }
}

extension Array where Element: NSCopying {
    func copy() -> [Element] {
        self.compactMap { $0.copy(with: nil) as? Element }
    }
}
