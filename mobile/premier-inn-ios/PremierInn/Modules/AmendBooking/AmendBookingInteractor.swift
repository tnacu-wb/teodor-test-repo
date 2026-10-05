//
//  AmendBookingInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import PassKit

typealias BookingDateRange = (arrivalDate: Date, nights: Int)
typealias AmendDifferencesModel = (existingStay: Stay, updateModel: AmendUpdateModel)
typealias AmendRoomRestrictions = (isAmendableRoom: Bool, isCancellableRoom: Bool, canAmendGuests: Bool)

struct AmendOperaDetails {
    let bookingReference: String?
    let originalBasketReference: String?
    let temporaryBasketReference: String?
    let token: String?
    let previousUpsells: [UpsellItem]
    let brand: HotelBrand?
    let bookerEmail: String?
    let bookerSurname: String?
    var paymentOptions: AmendPaymentOptions?
}

struct AmendPaymentOptions {
    let payNow: Bool
    let payOnArrival: Bool
    var paymentOptionSelected: PaymentIntervalOption
    let paymentCardDetails: PaymentCardDetails?
}

enum AmendTrackableAction {
    case arrivalDateChanged(Date)
    case numberOfNightsChanged(Int, Int)
    case roomAdded
    case roomRemoved
    case roomTypeChanged(String, String, String)
    case adultsChanged(String, Int, Int)
    case childrenChanged(String, Int, Int)

    var action: String {
        switch self {
        case .arrivalDateChanged(let newArrivalDate):
            return "Arrival date changed to \(newArrivalDate.analyticsDateFormat)"
        case .numberOfNightsChanged(let existingNights, let updatedNights):
            return "\(existingNights) nights to \(updatedNights) nights"
        case .roomAdded:
            return "Room added"
        case .roomRemoved:
            return "Room removed"
        case .roomTypeChanged(let roomName, let existingType, let updatedType):
            return "\(roomName) changed from \(existingType) to \(updatedType)"
        case .adultsChanged(let roomName, let existingAdults, let updatedAdults):
            return "\(roomName) adults changed from \(existingAdults) to \(updatedAdults)"
        case .childrenChanged(let roomName, let existingChildren, let updatedChildren):
            return "\(roomName) children changed from \(existingChildren) to \(updatedChildren)"
        }
    }
}

protocol AmendError: LocalizedError {}

extension AmendError {
    var errorDescription: String? {
        String(describing: self)
    }
}

enum CancelBookingError: AmendError {
    case missingReservation
}

enum AmendBookingSetupError: AmendError {
    case missingArrivalDate
    case copyBookingFailed
    case findBookingSourceFailed
    case missingReservation
    case missingReservationArrivalDate
    case getPackagesFailed
    case setupFailed
}

enum AmendBookingRefreshBasketError: AmendError {
    case getReservationFailed
    case getPackagesFailed
    case missingBasketReference
    case missingTemporaryReference
    case missingToken
    case missingSurname
    case missingArrivalDate
    case missingReservation
    case missingHotelCode
    case genericError
}

enum AmendBookingAmendSummaryError: AmendError {
    case missingBasketReference
    case missingTemporaryReference
    case missingToken
    case missingSurname
    case missingArrivalDate
}

private enum AmendmentType {
    case date
    case addedRoom
    case removedRoom
}

struct AmendmentDetails: AmendmentDetailsViewModel {
    let title: NSAttributedString
    let description: String?
}

struct AddRoomAvailabilityRequirements {
    var existingRooms: [Room]
    var existingUpsells: [UpsellItem]?
    var arrivalDate: Date
    var numberOfNights: Int
    var hotelCode: String
    var reservationID: String
    var kidsHaveToPayForCurrentBreakfast: Bool
    var isStayingForBusiness: Bool
    var existingRateClassification: String?
    var isBusiness: Bool
}

protocol AmendBookingDataProvider {
    func reservationForAmend(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        completion: @escaping (Reservation?, Error?) -> Void
    )

    func cancelReservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        completion: @escaping (PIDictionary?, Error?) -> Void
    )

    func loadHotel(
        with hotelCode: String,
        completion: @escaping (Hotel?, Error?) -> Void
    )

    func refreshStays(
        for user: User?,
        shouldAttemptLogin: Bool,
        completion: @escaping (Bool?) -> Void
    )

    func findBookingSource(
        findBookingDetails: FindBookingDetails,
        completion: @escaping (FindBookingSource?, Error?) -> Void
    )

    func copyBooking(
        reservationDetails: ReservationDetails,
        completion: @escaping (_ response: String?, _ error: Error?) -> Void
    )

    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool,
        completion: @escaping (_ response: ([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)?, _ error: Error?)
        -> Void
    )

    func amendPackages(
        packagesDetails: AmendPackagesDetail,
        completion: @escaping (_ response: Bool?, _ error: Error?) -> Void
    )

    func amendSummary(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        completion: @escaping (_ response: AmendSummary?, _ error: Error?) -> Void
    )

    func bookingConfirmationWithAmendSummary(
        reservationDetails: ReservationDetails,
        originalBookingRef: String,
        completion: @escaping (_ response: (Reservation?, AmendSummary?)?, _ error: Error?) -> Void
    )

    func getPromotionsInformation(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (_ response: PromotionsInformation?, _ error: Error?) -> Void
    )
}

extension RequestsManager: AmendBookingDataProvider {}

protocol AmendBookingInteractorDelegate: AnyObject {
    func interactorIsBusy()
    func failed(with errorMessage: String, goBack: Bool)

    func finishedLoadingRequireData(isNewRoomAdded: Bool)
}

protocol AmendBookingInteractorProtocol {
    var analyticsUpdates: [AmendmentDetailsViewModel] { get }
    var amendViewModel: AmendBookingViewModel? { get }
    var amendDatesStayDetails: AmendStayDatesDetails? { get }
    var amendDifferencesModel: AmendDifferencesModel? { get }
    var customAnalyticsParameters: PIDictionary? { get }
    var roomCount: Int { get }
    var canAddRoom: Bool { get }
    var hotel: Hotel? { get }
    var amendReservationRooms: [Room]? { get }
    var existingReservation: Reservation? { get }
    var amendReservationModel: AmendBookingInteractor.AmendReservationModel? { get }
    var amendOperaDetails: AmendOperaDetails? { get }
    var rulesToFollow: Restrictions { get }
    var shouldForceUpsellChange: Bool { get }
    var amendReservationReviewModel: AmendReservationReviewModel? { get }

    func startSetup()
    func existingRoom(forRoomNumber roomNumber: Int) -> Room?
    func cancelStay(completion: @escaping (Bool, String?) -> Void)
	func refreshTemporaryBasket(hasAdultsDecreased: Bool, isNewRoomAdded: Bool)
    func fetchPaymentOptions(completion: @escaping (_ error: Error?) -> Void)
    func trackState(of subView: String)
    func refreshStays()
    func addRoomAvailabilityRequirements(for room: Room?) -> AddRoomAvailabilityRequirements?
    func editRoomRestrictions(for room: Room?) -> AmendRoomRestrictions
}

class AmendBookingInteractor {
    // MARK: - Types

    private struct ViewModel: AmendBookingViewModel {
        let hotelImageUrl: URL?
        let hotelName: String
        let bannerMessage: String?
        let datesSummary: String
        let amendable: Bool
		let shouldShowUpsellsRemovedMessage: Bool
        let shouldShowChangeDatesButton: Bool
        let shouldShowChangeUpsellsButton: Bool
        let shouldShowEditRoomButton: Bool
        let shouldShowAddRoomButton: Bool
        let shouldShowCancelBookingButton: Bool
        let amendUpsellsModel: AmendUpsellsModel?
        let rooms: [AmendBookingRoomViewModel]
        let amendmentsModel: AmendmentsModel
    }

    private struct UpsellsViewModel: AmendUpsellsModel {
        let title: String
        let description: String?
        let actionTitle: String?
    }

    public struct AmendReservationModel {
        var reservation: Reservation
        var criteria: Criteria
        var rate: Rate?

        init(with reservation: Reservation, criteria: Criteria, and rate: Rate?) {
            self.reservation = reservation
            self.criteria = criteria
            self.rate = rate
            // we don't set the rate as a whole for actions in Opera, so start with reservation.rooms as a base
            self.rate?.rooms = reservation.rooms

            var upsellItems: [UpsellItem] = []

            reservation.upsellItems.forEach { item in
                guard item.upsellOperaId != .freeChildBreakfast else { return }

                guard upsellItems.contains(where: { $0.code == item.code && $0.roomId == item.roomId }) == false
                    else { return }

                var item = item

                // Using room.uniqueID to associate pre-existing upsell items
                if let room = criteria.rooms.first(where: { $0.roomId == item.roomId }) {
                    item.roomUniqueID = room.uniqueID
                }

                upsellItems.append(item)
            }

            self.reservation.upsellItems = upsellItems
        }

        func datesSummary() -> String? {
            guard let checkOutDate = criteria.arrivalDate.dateByAddingUnit(unitType: .day, number: criteria.nights)
                else { return nil }

            var string = criteria.arrivalDate.localizedVeryShortStringFormat
            string += " - " + checkOutDate.localizedVeryShortStringFormat
            string += " (\(criteria.nightsCountDescription))"

            return string
        }
    }

    private struct RoomViewModel: AmendBookingRoomViewModel {
        let existingRoom: Bool
        let roomNumber: Int
        let roomDescription: String
        let leadGuest: String
        let mealDescription: String?
        let extraDescription: String?
    }

    // MARK: - Properties
	var shouldShowUpsellsRemovedMessage: Bool = false
    var amendBookingDataProvider: AmendBookingDataProvider = RequestsManager()
    weak var delegate: AmendBookingInteractorDelegate?

    internal var hotel: Hotel?
    private var existingStay: Stay
    private var stay: Stay
    private var currentReservation: Reservation? {
        didSet {
            guard var reservation = currentReservation else { return }
            guard let arrivalDate = reservation.arrivalDate else { return }

            var criteria = Criteria()
            criteria.arrivalDate = arrivalDate
            criteria.nights = reservation.nights
            criteria.rooms = reservation.rooms.copy()

            // would this change? - we have it
            reservation.availableUpsells = existingReservation?.availableUpsells
            amendReservationModel = AmendReservationModel(with: reservation, criteria: criteria, and: reservation.rate)
        }
    }

    var amendOperaDetails: AmendOperaDetails?

    internal var existingReservation: Reservation?
    internal var amendReservationModel: AmendReservationModel?

    var fetchedOriginalReservation: Reservation?
    var fetchedTemporaryReservation: Reservation?
    var fetchedError: Error?

    var amendDatesPromotionsDetails: AmendStayDatesPromotionDetails?

    // Analytics
    var analyticsProperties: PIDictionary {
        var dict = analytics.analyticsProperties(stateType: PIAnalytics.StateTypes.myBookings)
        dict[PIAnalytics.Keys.amendBookingId] = stay.identifier

        return dict
    }
    var analytics: AnalyticsType = AnalyticsManager.shared

    // MARK: - Computed Properties

    // View Models
    private var amendments: [AmendmentDetails] {
        self.amendments(withIdentifiableDataRemoved: false)
    }
    private var analyticsAmendments: [AmendmentDetails] {
        guard let existingReservation = self.existingReservation else { return [] }
        guard let newReservation = self.amendReservationModel else { return [] }

        return self.analyticsAmendsChanges(for: existingReservation, and: newReservation)
    }
    var amendDifferencesModel: AmendDifferencesModel? {
        guard let reservation = existingReservation else { return nil }
        guard let criteria = amendReservationModel?.criteria else { return nil }
        guard let updatedReservationModel = amendReservationModel else { return nil }
        guard let rate = updatedReservationModel.rate else { return nil }
        guard let hotelCode = hotel?.code else { return nil }
        guard let newTotalCost = amendReservationModel?.reservation.totalCost else { return nil }

        let updateModel = AmendUpdateModel(
            hotelImage: hotel?.primaryImages.first,
            hotelName: hotel?.name,
            hotelBrand: hotel?.brand,
            updates: amendments,
            reservation: reservation,
            rate: rate,
            criteria: criteria,
            breakfasts: updatedReservationModel.reservation.upsellItems,
            hotelCode: hotelCode,
            newTotalCost: newTotalCost,
            isStayingForBusiness: reservation.businessTrip,
            showECILCORemovalMessage: shouldShowECILCORemovalMessage(
                existingReservation: reservation,
                updatedReservation: updatedReservationModel
            )
        )
        return (existingStay, updateModel)
    }

    func shouldShowECILCORemovalMessage(
        existingReservation: Reservation,
        updatedReservation: AmendReservationModel
    ) -> Bool {
        let existingExtraUpsells = existingReservation.extraUpsells
        let updatedExtraUpsells = updatedReservation.reservation.upsellItems.filter { upsellItem in
            upsellItem.isExtraUpsell &&
            UpsellItemOperaId.extras.contains(where: { item in
                item.rawValue == upsellItem.id
            })
        }

        guard !existingExtraUpsells.isEmpty else {
            return false
        }

        return !existingExtraUpsells.contains(where: updatedExtraUpsells.contains)
    }

    private var roomModels: [AmendBookingRoomViewModel] {
        var rooms: [AmendBookingRoomViewModel] = []

        guard let amendReservationModel = amendReservationModel else { return [] }

        for (index, room) in amendReservationModel.criteria.rooms.enumerated() {
            let existingRoom = existingReservation?.rooms.first(where: { $0.roomId == room.roomId })

            let roomViewModel = RoomViewModel(
                existingRoom: existingRoom != nil,
                roomNumber: index + 1,
                roomDescription: room.bookingSummaryRoomDescription,
                leadGuest: room.leadGuest?.displayName ?? PILocalizedString("Unknown lead guest", comment: ""),
                mealDescription: {
                    let descriptions = upsellsDescription(upsells: amendReservationModel.reservation.foodUpsells, room: room)

                    return descriptions
            }(), extraDescription: {
                guard let wifiItem = amendReservationModel.reservation.upsellItems
                      .first(where: { ($0.roomUniqueID == room.uniqueID) && ($0.wifiUpsell == true) }) else { return nil }

                return "\(wifiItem.legend)"
            }()
            )

            rooms.append(roomViewModel)
        }

        return rooms
    }

    func upsellsDescription(upsells: [UpsellItem], room: Room) -> String? {
        var roomHasFreeBreakfast = false

        var upsellsDescription: [String]? = upsells.compactMap { meal in
            guard meal.roomUniqueID == room.uniqueID, let quantity = meal.quantity else { return nil }

            if meal.kidsHaveToPay == false && meal.freeBreakfastTrigger == true && room.children > 0 {
                roomHasFreeBreakfast = true
            }
            return "\(meal.legend) x \(quantity)"
        }

        if roomHasFreeBreakfast && upsellsDescription?.isNotEmpty == true {
            upsellsDescription?
                .append("\(PILocalizedString("bookingSummaryKidsBreakfastLabel", comment: "")) x \(room.children)")
        }

        guard upsellsDescription?.isNotEmpty == true else { return nil }
        return upsellsDescription?.joined(separator: "\n")
    }
    var amendDatesStayDetails: AmendStayDatesDetails? {
        guard hotel != nil else { return nil }
        guard let amendReservationModel = amendReservationModel else { return nil }
        guard amendReservationModel.reservation.amendRestrictions?.dates ?? false == false else { return nil }
        guard let currentTotalCost = amendReservationModel.reservation.totalCost else { return nil }
        let nightsRestriction = existingReservation?.amendRestrictions?.nights ?? false

        return (
            amendReservationModel.criteria,
            currentTotalCost,
            nightsRestriction,
            amendOperaDetails,
            amendReservationModel.reservation.business == true,
            amendDatesPromotionsDetails,
            rulesToFollow
        )
    }
    // Analytics
    private var foodRevenueChange: Double {
        guard let existingFoodTotal = existingReservation?.mealTotalCost else { return 0 }
        guard let amendReservationModel = amendReservationModel else { return 0 }

        let newFoodTotal = amendReservationModel.criteria.rooms.foodUpsellsCostAmount(
            for: amendReservationModel.reservation.upsellItems,
            availableUpsells: amendReservationModel.reservation.availableUpsells,
            nights: amendReservationModel.criteria.nights
        )

        return NSDecimalNumber(value: newFoodTotal).subtracting(existingFoodTotal.amount).doubleValue
    }
    private var roomsRevenueChange: Double {
        guard let existingRoomTotal = existingReservation?.rooms.totalCost else { return 0 }
        guard let updatedTotal = amendReservationModel?.rate?.rooms?.totalCost else { return 0 }

        return updatedTotal.amount.subtracting(existingRoomTotal.amount).doubleValue
    }
    private var roomTypesDidChange: Bool {
        guard let reservation = existingReservation else { return false }
        guard let updateModel = amendReservationModel else { return false }

        for room in reservation.rooms {
            if let updatedRoom = updateModel.criteria.rooms.first(where: { $0.roomId == room.roomId }) {
                if updatedRoom.type != room.type { return true }
            }
        }
        return false
    }
    private var analyticsReservationChanges: String {
        guard let existingReservation = self.existingReservation else { return "" }
        guard let newReservation = self.amendReservationModel else { return "" }

        let analyticsChanges = self.analyticsAmendsChanges(for: existingReservation, and: newReservation)

        return (analyticsChanges.map {
            var strings = [String]()
            strings.append($0.title.string.replacingOccurrences(of: ",", with: " "))
            if let description = $0.description { strings.append(description.replacingOccurrences(of: ",", with: " ")) }

            return strings.joined(separator: " ")
        }).joined(separator: ", ").replacingOccurrences(of: "\n", with: "")
    }

    // MARK: - Lifecycle

    deinit {
        print("DEINIT: \(self)")
    }

    private var amendable: Bool {
        self.stay.amendable && !SettingsManager.sharedInstance.shouldRedirectForAmendOpera
    }

    init(stay: Stay, hotel: Hotel?, delegate: AmendBookingInteractorDelegate) {
        existingStay = stay
        self.stay = stay
        self.hotel = hotel

        self.delegate = delegate
        self.delegate?.interactorIsBusy()
    }

    func startSetup() {
        setup(with: stay) { result in
            switch result {
            case .success(let tuple):
                self.existingReservation = tuple.originalReservation
                if let tempReservation = tuple.temporaryReservation, self.amendable {
                    self.amendOperaDetails = AmendOperaDetails(
                        bookingReference: self.stay.identifier,
                        originalBasketReference: self.stay.operaBasketReference,
                        temporaryBasketReference: tempReservation.operaBasketReference,
                        token: self.existingReservation?.token,
                        previousUpsells: tuple.originalReservation.upsellItems,
                        brand: self.hotel?.brand,
                        bookerEmail: self.existingReservation?.booker?.emailAddress,
                        bookerSurname: self.existingReservation?.booker?.lastName
                    )
                    self.currentReservation = tempReservation
                } else {
                    self.currentReservation = tuple.originalReservation
                }

                self.delegate?.finishedLoadingRequireData(isNewRoomAdded: false)
            case .failure(let error):

                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendSetupError)

                self.delegate?.failed(with: PILocalizedString("somethingWentWrongMessage", comment: ""), goBack: true)
            }
        }
    }

    private func amendments(withIdentifiableDataRemoved: Bool) -> [AmendmentDetails] {
        var amendments = [AmendmentDetails]()

        guard let existingReservation = self.existingReservation else { return amendments }
        guard let newReservation = self.amendReservationModel else { return amendments }

        amendments.append(contentsOf: changesFor(
            existingReservation: existingReservation,
            and: newReservation,
            with: withIdentifiableDataRemoved
        ))

        return amendments
    }

    // swiftlint:disable:next cyclomatic_complexity function_body_length
    private func setup(
        with summary: Stay,
        completion: @escaping (Result<(originalReservation: Reservation, temporaryReservation: Reservation?)>) -> Void
    ) {
        guard let arrivalDate = summary.arrivalDate else {
            completion(Result.failure(error: AmendBookingSetupError.missingArrivalDate))
            return
        }

        let surname = summary.importName ?? summary.lastName
        let reference = summary.identifier

        let findBookingDetails = FindBookingDetails(
            reservationId: reference,
            surname: surname,
            arrivalDate: arrivalDate,
            business: summary.isBusinessTrip
        )

        amendBookingDataProvider.findBookingSource(findBookingDetails: findBookingDetails) { source, error in
            if let error = error {
                return completion(Result.failure(error: error))
            }

            guard let source = source else {
                return completion(Result.failure(error: AmendBookingSetupError.findBookingSourceFailed))
            }

            let dispatchGroup = DispatchGroup()

            if let hotel = self.hotel {
                self.fetchPromotionsInformation(
                    with: summary,
                    basketReference: source.basketReference,
                    hotelBrand: hotel.brand,
                    dispatchGroup: dispatchGroup
                )
            } else {
                dispatchGroup.enter()
                self.amendBookingDataProvider.loadHotel(with: summary.hotelCode) { hotel, _ in
                    if let hotel {
                        self.hotel = hotel

                        self.fetchPromotionsInformation(
                            with: summary,
                            basketReference: source.basketReference,
                            hotelBrand: hotel.brand,
                            dispatchGroup: dispatchGroup
                        )
                    }

                    dispatchGroup.leave()
                }
            }

            // fetch original booking confirmation and packages
            let reservationDetails = ReservationDetails(
                reservationId: source.basketReference,
                surname: surname,
                arrivalDate: arrivalDate,
                business: summary.isBusinessTrip,
                token: source.token
            )

            dispatchGroup.enter()
            self.loadReservationDetails(reservationDetails: reservationDetails, hotelCode: summary.hotelCode) { result in
                self.fetchedOriginalReservation = self.handleGetBookingConfirmation(result: result)
                dispatchGroup.leave()
            }

            dispatchGroup.enter()
            if self.amendable {
                // fetch temporary booking confirmation and packages
                dispatchGroup.enter()
                self.amendBookingDataProvider
                    .copyBooking(reservationDetails: reservationDetails) { temporaryBasketReference, _ in
                    guard let temporaryBasketReference = temporaryBasketReference else {
                        return completion(Result.failure(error: AmendBookingSetupError.copyBookingFailed))
                    }

                    let reservationDetailsForTemporary = ReservationDetails(
                        reservationId: temporaryBasketReference,
                        surname: reservationDetails.surname,
                        arrivalDate: reservationDetails.arrivalDate,
                        business: reservationDetails.business,
                        token: reservationDetails.token
                    )

                    dispatchGroup.enter()
                    self.loadReservationDetails(
                        reservationDetails: reservationDetailsForTemporary,
                        hotelCode: summary.hotelCode
                    ) { result in
                        self.fetchedTemporaryReservation = self.handleGetBookingConfirmation(result: result)
                        dispatchGroup.leave()
                    }

                    dispatchGroup.leave()
                }
            }
            dispatchGroup.leave()

            dispatchGroup.notify(queue: .main) {
                if let fetchedError = self.fetchedError {
                    return completion(.failure(error: fetchedError))
                }

                if var fetchedOriginalReservation = self.fetchedOriginalReservation {
                    // update amendRestrictions
                    if self.amendDatesPromotionsDetails != nil {
                        fetchedOriginalReservation.amendRestrictions = AmendRestrictions.siteWidePromotionsAmendRestrictions
                    }

                    if self.amendable {
                        // If amendable
                        guard let fetchedTemporaryReservation = self.fetchedTemporaryReservation
                            else { return completion(.failure(error: AmendBookingSetupError.setupFailed)) }

                        let modifiedOriginalReservation = self.modifyExistingReservationRoomIds(
                            existingReservation: fetchedOriginalReservation,
                            temporaryReservation: fetchedTemporaryReservation
                        )
                        completion(.success(result: (modifiedOriginalReservation, fetchedTemporaryReservation)))
                    } else {
                        // If cancel only
                        completion(.success(result: (fetchedOriginalReservation, nil)))
                    }
                } else {
                    completion(.failure(error: AmendBookingSetupError.setupFailed))
                }
            }
        }
    }

    private func fetchPromotionsInformation(
        with summary: Stay,
        basketReference: String,
        hotelBrand: HotelBrand,
        dispatchGroup: DispatchGroup
    ) {
        if let arrivalDate = summary.arrivalDate,
           let checkOutDate = summary.checkOutDate {
            dispatchGroup.enter()
            let criteria = PromotionsInformationCriteria(
                brand: hotelBrand,
                promotionCode: "",
                bookingDate: nil,
                stayStartDate: arrivalDate,
                stayEndDate: checkOutDate,
                basketReference: basketReference
            )
            self.amendBookingDataProvider.getPromotionsInformation(criteria: criteria) { promotionsInformation, _ in
                let promotionCode = promotionsInformation?.promoBookingInfo?.promotionCode

                if let promotionCode, promotionCode.isNotEmpty {
                    self.amendDatesPromotionsDetails = (promotionCode, promotionsInformation?.appPromoAmendMessage)
                }

                dispatchGroup.leave()
            }
        }
    }

    private func handleGetBookingConfirmation(result: Result<Reservation>) -> Reservation? {
        switch result {
        case .success(result: let reservation):

            return reservation

        case .failure(error: let error):

            self.fetchedError = error
            return nil
        }
    }

    // we overwrite the room ids of the original reservation and its upsells with the ones from the temporary reservation to be able to compare before and after accurately
    private func modifyExistingReservationRoomIds(
        existingReservation: Reservation,
        temporaryReservation: Reservation
    ) -> Reservation {
        var newExistingReservation = existingReservation
        var newExistingUpsells: [UpsellItem] = []
        newExistingReservation.upsellItems.forEach { existingUpsell in
            var newExistingUpsell = existingUpsell

            guard let existingRoom = newExistingReservation.rooms.first(where: { $0.roomId == existingUpsell.roomId }),
                  let tempRoom = temporaryReservation.rooms.first(where: { existingRoom.leadGuest == $0.leadGuest })
            else { return }

            newExistingUpsell.roomId = tempRoom.roomId

            newExistingUpsells.append(newExistingUpsell)
        }

        newExistingReservation.upsellItems = newExistingUpsells
        newExistingReservation.rooms = temporaryReservation.rooms

        return newExistingReservation
    }

    private func loadReservationDetails(
        reservationDetails: ReservationDetails,
        hotelCode: String,
        completion: @escaping (Result<Reservation>) -> Void
    ) {
        self.amendBookingDataProvider
            .reservationForAmend(reservationDetails: reservationDetails, hotelCode: hotelCode) { reservation, _ in
            guard var reservation = reservation
                else { return completion(Result.failure(error: AmendBookingSetupError.missingReservation)) }

            guard let bookingDetails = BookingDetails(reservation: reservation)
                else { return completion(Result.failure(error: AmendBookingSetupError.missingReservation)) }

            // Don't make the packages call if the booking is not amendable
            guard self.amendable else {
                return completion(Result.success(result: reservation))
            }

            self.amendBookingDataProvider.getPackages(
                reservationId: reservationDetails.reservationId,
                bookingDetails: bookingDetails,
                hotelCode: hotelCode,
                bookingFlowId: reservation.bookingFlowId,
                showMealInclusiveRate: false
            ) { upsellItems, error in
                guard error == nil
                    else { return completion(Result.failure(error: AmendBookingSetupError.getPackagesFailed)) }

                let availableUpsells = upsellItems?.0
                let bookedUpsells = upsellItems?.1

                reservation.availableUpsells = availableUpsells
                reservation.upsellItems = bookedUpsells ?? []

                completion(Result.success(result: reservation))
            }
        }
    }

    func priceDifferenceString(for existingCost: Cost?, and newCost: Cost?) -> String {
        guard let existingCost = existingCost, let newCost = newCost else { return "" }
        let priceDifferenceAmount = newCost.amount.subtracting(existingCost.amount)
        let priceDifference = Cost(
            amount: priceDifferenceAmount.doubleValue,
            currencyCode: newCost.currencyCode,
            locale: newCost.locale
        )

        return "\(priceDifferenceAmount.doubleValue < 0 ? "" : "+")\(priceDifference.localizedValue)"
    }

    private func mealsPriceForRoom(upsellItem: UpsellItem) -> Double {
        guard let roomId = upsellItem.roomId,
              let room = amendReservationModel?.criteria.rooms.first(where: { $0.roomId == roomId })
        else { return upsellItem.price.amount.doubleValue }

        let guestsCount = room.adults + (upsellItem.kidsHaveToPay ?? true ? room.children : 0)

        return upsellItem.price.amount.multiplying(by: NSDecimalNumber(value: guestsCount)).doubleValue
    }

    private var extrasRevenueChange: Double {
        guard let existingReservation = existingReservation else { return 0 }
        guard let newCriteria = amendReservationModel?.criteria else { return 0 }

        let existingExtrasTotal = existingReservation.rooms.nonFoodUpsellsCostAmount(
            for: existingReservation.wifiUpsells,
            nights: 1
        )
        let newExtrasTotal = newCriteria.rooms.nonFoodUpsellsCostAmount(
            for: amendReservationModel?.reservation.upsellItems ?? [],
            nights: newCriteria.nights
        )

        return newExtrasTotal - existingExtrasTotal
    }

    // not called anymore
    func trackAvailabilityUpdate(available: Bool? = nil) {
        var data = analyticsProperties

        data[PIAnalytics.Keys.amendFoodRevenueChange] = foodRevenueChange.stringForAnalyticsCost
        data[PIAnalytics.Keys.amendRoomTypesDidChange] = roomTypesDidChange
        data[PIAnalytics.Keys.amendRoomRevenueChange] = roomsRevenueChange.stringForAnalyticsCost
        data[PIAnalytics.Keys.amendExtrasRevenueChange] = extrasRevenueChange.stringForAnalyticsCost
        data[PIAnalytics.Keys.amendRoomsAmountChange] = (
            amendReservationModel?.criteria.rooms.count ?? stay.numberOfRoooms
        ) -
            stay.numberOfRoooms
        data[PIAnalytics.Keys.amendNightsChange] = (amendReservationModel?.criteria.nights ?? stay.numberOfNights) - stay
            .numberOfNights
        data[PIAnalytics.Keys.amendTotalRevenueChange] = (foodRevenueChange + roomsRevenueChange + extrasRevenueChange)
            .stringForAnalyticsCost
        data[PIAnalytics.Keys.amendChanges] = analyticsReservationChanges
        data[PIAnalytics.Keys.amendTotalRevenueOriginal] = (stay.totalCost?.amount.doubleValue ?? 0.0).stringForAnalyticsCost
        data[PIAnalytics.Keys.amendTotalRevenue] = (amendReservationModel?.reservation.totalCost?.amount.doubleValue ?? 0.0)
            .stringForAnalyticsCost

        if let available = available {
            data[PIAnalytics.Keys.amendAvailability] = available
        }

        let stateName = PIAnalytics.StateNames.manageBooking + ": " + PIAnalytics.StateNames.amendAvailability
        analytics.trackState(stateName, data: data)
    }
}

extension AmendBookingInteractor: AmendBookingInteractorProtocol {
    func refreshStays() {
        guard let currentUser = UserSessionManager.sharedInstance.currentUser else { return }

        amendBookingDataProvider.refreshStays(for: currentUser, shouldAttemptLogin: false, completion: {_ in })
    }

    var analyticsUpdates: [AmendmentDetailsViewModel] {
        self.analyticsAmendments
    }

    var amendViewModel: AmendBookingViewModel? {
        guard let hotelName = hotel?.name else { return nil }
        guard let existingReservation = existingReservation else { return nil }
        guard let amendReservationModel = amendReservationModel else { return nil }

        let currentReservation = currentReservation ?? existingReservation

        let amendmentsModel: AmendmentsModel = {
            guard let existingPrice = existingStay.totalCost else { return (false, nil) }

            guard let newPrice = currentReservation.totalCost else { return (false, nil) }

            let priceDifference = newPrice.amount.subtracting(existingPrice.amount)
            let priceDifferenceCost = Cost(
                amount: priceDifference.doubleValue,
                currencyCode: newPrice.currencyCode,
                locale: newPrice.locale
            ).localizedValue

            return (
                amendments.isNotEmpty,
                priceDifference.doubleValue < 0 ? "\(priceDifferenceCost)" : "+\(priceDifferenceCost)"
            )
        }()

        return ViewModel(
            hotelImageUrl: hotel?.primaryImages.first,
            hotelName: hotelName,
            bannerMessage: amendDatesPromotionsDetails?.bannerMessage,
            datesSummary: amendReservationModel.datesSummary() ?? "",
            amendable: existingReservation.isAmendAllowed,
            shouldShowUpsellsRemovedMessage: shouldShowUpsellsRemovedMessage,
            shouldShowChangeDatesButton: existingReservation.isAmendAllowed && existingReservation.amendRestrictions?
            .dates ?? false == false,
            shouldShowChangeUpsellsButton: existingReservation.isAmendAllowed && existingReservation.amendRestrictions?
            .upsells ?? false == false,
            shouldShowEditRoomButton: existingReservation
            .isAmendAllowed &&
            (existingReservation.amendRestrictions?.editRoom ?? false == false || existingReservation.amendRestrictions?
            .editGuestNames ?? false == false),
            shouldShowAddRoomButton: existingReservation.isAmendAllowed && existingReservation.amendRestrictions?
            .addRoom ?? false == false && !existingReservation.businessTrip,
            shouldShowCancelBookingButton: existingReservation.cancelable,
            amendUpsellsModel: amendUpsellsModel,
            rooms: roomModels,
            amendmentsModel: amendmentsModel
        )
    }

    private var amendUpsellsModel: AmendUpsellsModel? {
        guard let reservation = existingReservation,
              reservation.isAmendAllowed else { return nil }

        return UpsellsViewModel(
            title: PILocalizedString("MealsAndWiFi"),
            description: nil,
            actionTitle: PILocalizedString("ChangeMealsAndWiFi")
        )
    }

    var shouldForceUpsellChange: Bool {
        let latestSelectedUpsells = amendReservationModel?.reservation.upsellItems.filter { $0.foodUpsell == true }
        guard let selectedUpsells = latestSelectedUpsells else { return false }
        guard let hotel = hotel else { return false }

        let filteredUpsells = hotel.upsellsAvailable(
            arrivalDate: amendReservationModel?.criteria.arrivalDate,
            departureDate: amendReservationModel?.criteria.checkOutDate,
            upsells: selectedUpsells
        )

        // Check if all the selected upsells are in the filtered array, if so we do not need to force an upsell change because the upsells are available
        let allUpsellsExistAfterFilter = selectedUpsells.allSatisfy({ upsell in
            filteredUpsells.contains(where: { $0.code == upsell.code })
        })

        return !allUpsellsExistAfterFilter
    }

    func addRoomAvailabilityRequirements(for room: Room?) -> AddRoomAvailabilityRequirements? {
        guard let arrivalDate = amendReservationModel?.criteria.arrivalDate else { return nil }
        guard let numberOfNights = amendReservationModel?.criteria.nights else { return nil }
        guard let existingRooms = amendReservationModel?.criteria.rooms else { return nil }

        let breakfasts = amendReservationModel?.reservation.upsellItems
        let kidsHaveToPayForCurrentBreakfast: Bool = breakfasts?.first { $0.roomId == room?.roomId }?.kidsHaveToPay ?? true

        return AddRoomAvailabilityRequirements(
            existingRooms: existingRooms,
            existingUpsells: breakfasts,
            arrivalDate: arrivalDate,
            numberOfNights: numberOfNights,
            hotelCode: stay.hotelCode,
            reservationID: stay.identifier,
            kidsHaveToPayForCurrentBreakfast: kidsHaveToPayForCurrentBreakfast,
            isStayingForBusiness: existingReservation?.businessTrip ?? false,
            existingRateClassification: existingReservation?.rate?.classification,
            isBusiness: existingReservation?.business == true
        )
    }

    func editRoomRestrictions(for room: Room?) -> AmendRoomRestrictions {
        // we check if it's a newly added room that we are now editing and ignore these restrictions
        guard let room = room,
              existingReservation?.rooms.contains(where: { $0.roomId == room.roomId }) == true else {
            return AmendRoomRestrictions(isAmendableRoom: true, isCancellableRoom: true, canAmendGuests: true)
        }

        let isAmendableRoom = existingReservation?.amendRestrictions?.editRoom ?? false == false
        let isCancellableRoom = existingReservation?.amendRestrictions?.removeRoom ?? false == false
        let canAmendGuests = existingReservation?.amendRestrictions?.editGuestNames ?? false == false

        return AmendRoomRestrictions(
            isAmendableRoom: isAmendableRoom,
            isCancellableRoom: isCancellableRoom,
            canAmendGuests: canAmendGuests
        )
    }

    var bookingDatesRange: BookingDateRange? {
        guard let arrivalDate = stay.arrivalDate else { return nil }

        return (arrivalDate, stay.numberOfNights)
    }

    var customAnalyticsParameters: PIDictionary? {
        let identifier = stay.identifier
        return [PIAnalytics.Keys.amendBookingId: identifier]
    }

    var rulesToFollow: Restrictions {
        var channel: Channel {
            if stay.business {
                return .BB
            } else if existingReservation?.rate?.classification == SimpleNetwork.Constants.EmployeeOffer.rateClassification {
                return .EMPLOYEE
            }
            return .PI
        }

        return SettingsManager.sharedInstance.rulesForChannel(channel: channel)
    }

    var roomCount: Int {
        roomModels.count
    }

    func existingRoom(forRoomNumber roomNumber: Int) -> Room? {
        guard let numberOfExistingRooms = amendReservationModel?.criteria.rooms.count else { return nil }

        if roomNumber > numberOfExistingRooms {
            return nil
        }

        let roomNumberAsIndex = roomNumber - 1

        return amendReservationModel?.criteria.rooms[roomNumberAsIndex]
    }

    var canAddRoom: Bool {
        guard let amendReservationModel = amendReservationModel else { return false }

        return amendReservationModel.criteria.rooms.count < rulesToFollow.maxRoomsAmend
    }

    var amendReservationRooms: [Room]? {
        amendReservationModel?.criteria.rooms
    }

    var amendReservationReviewModel: AmendReservationReviewModel? {
        guard let amendUpdateModel = amendDifferencesModel?.updateModel else { return nil }

        return AmendReservationReviewModel(
            existingStay: existingStay,
            amendUpdateModel: amendUpdateModel,
            hotel: hotel,
            amendOperaDetails: amendOperaDetails,
            analyticsUpdates: analyticsUpdates
        )
    }

    func cancelStay(completion: @escaping (Bool, String?) -> Void) {
        // we need basket reference
        guard let reservationDetails = ReservationDetails(stay: existingStay) else {
            completion(false, CancelBookingError.missingReservation.errorDescription)
            return
        }

        amendBookingDataProvider.cancelReservation(
            reservationDetails: reservationDetails,
            hotelCode: existingStay.hotelCode
        ) { [weak self] dictionary, error in
            if dictionary?["cancellationId"] is String, let identifier = self?.existingStay.identifier {
                let trackingIdentifier = identifier

                self?.analytics.trackAction(PIAnalytics.Action.cancelledBooking, userInfo: [
                    PIAnalytics.Keys.didCancel: true,
                    PIAnalytics.Keys.cancelBookingId: trackingIdentifier,
                    PIAnalytics.Keys.cancelNights: self?.stay.numberOfNights ?? 0,
                    PIAnalytics.Keys.cancelRooms: self?.stay.numberOfRoooms ?? 0
                ])

                let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

                if let stay = reservationsManager.items.first(where: {$0.identifier == identifier }) {
                    stay.cancelled = true
                    _ = LocalReservationManager.shared.update(with: [stay])

                    self?.removePKPass(with: stay.pkPassSerialNumber)
                }

                return completion(true, nil)
            } else {
                switch error {
                case RequestsManagerError.serverError(let dict)?:
                    completion(false, String(describing: dict))
                default:
                    completion(false, PILocalizedString("somethingWentWrongMessage"))
                }
            }
        }
    }

    func removePKPass(with serialNumber: String) {
        let passLibrary: PKPassLibrary = PKPassLibrary.init()

        if let pass = passLibrary.pass(withPassTypeIdentifier: Constants.pkPassTypeIdentifier, serialNumber: serialNumber) {
            passLibrary.removePass(pass)
        }
    }

	func refreshTemporaryBasket(hasAdultsDecreased: Bool, completion: @escaping (_ error: Error?) -> Void) {
        // rename these errors and reuse code
        guard let originalBasketReference = amendOperaDetails?.originalBasketReference else {
            completion(AmendBookingRefreshBasketError.missingBasketReference)
            return
        }

        guard let temporaryReference = amendOperaDetails?.temporaryBasketReference else {
            completion(AmendBookingRefreshBasketError.missingTemporaryReference)
            return
        }

        guard let token = amendOperaDetails?.token else {
            completion(AmendBookingRefreshBasketError.missingToken)
            return
        }

        // surname and arrivalDate are not used by this call but still required in ReservationDetails
        guard let surname = existingReservation?.surname else {
            completion(AmendBookingRefreshBasketError.missingSurname)
            return
        }

        guard let arrivalDate = existingReservation?.arrivalDate else {
            completion(AmendBookingRefreshBasketError.missingArrivalDate)
            return
        }

        let reservationDetails = ReservationDetails(
            reservationId: temporaryReference,
            surname: surname,
            arrivalDate: arrivalDate,
            business: existingReservation?.business == true,
            token: token
        )

        amendBookingDataProvider.bookingConfirmationWithAmendSummary(
            reservationDetails: reservationDetails,
            originalBookingRef: originalBasketReference
        ) { response, error in
            guard var reservation = response?.0, error == nil else {
                return completion(AmendBookingRefreshBasketError.getReservationFailed)
            }

            let summaryOptions = response?.1
            let payNow = summaryOptions?.paymentOptions?.payNow ?? false
            let payOnArrival = summaryOptions?.paymentOptions?.payOnArrival ?? false
            let amendPaymentOptions = AmendPaymentOptions(
                payNow: payNow,
                payOnArrival: payOnArrival,
                paymentOptionSelected: .later,
                paymentCardDetails: summaryOptions?.paymentCardDetails
            )
            self.amendOperaDetails?.paymentOptions = amendPaymentOptions


            guard let bookingDetails = BookingDetails(reservation: reservation) else {
                return completion(AmendBookingRefreshBasketError.missingReservation) }

            guard let hotelCode = self.hotel?.code else {
                return completion(AmendBookingRefreshBasketError.missingHotelCode)
            }

            self.amendBookingDataProvider.getPackages(
                reservationId: reservationDetails.reservationId,
                bookingDetails: bookingDetails,
                hotelCode: hotelCode,
                bookingFlowId: reservation.bookingFlowId,
                showMealInclusiveRate: false
            ) { upsellItems, error in
                guard error == nil else { return completion(AmendBookingRefreshBasketError.getPackagesFailed) }

                let availableUpsells = upsellItems?.0
                let bookedUpsells = upsellItems?.1

				let wifiGotRemoved = (self.existingReservation?.wifiUpsells.count ?? 0) >
				    (bookedUpsells?.filter { $0.wifiUpsell }.count ?? 0)
				let mealsGotRemoved = (self.existingReservation?.foodUpsells.count ?? 0) >
				    (bookedUpsells?.filter { $0.foodUpsell }.count ?? 0)

				/// Due to BE limitation, when adults change from 2 to 1, all the upsells are removed
				/// So, need this flag to show some message
				self.shouldShowUpsellsRemovedMessage = wifiGotRemoved || (hasAdultsDecreased && mealsGotRemoved)

                reservation.availableUpsells = availableUpsells
                reservation.upsellItems = bookedUpsells ?? []

                self.currentReservation = reservation

                completion(nil)
            }
        }
    }

    // Update

	func refreshTemporaryBasket(hasAdultsDecreased: Bool, isNewRoomAdded: Bool) {
        self.delegate?.interactorIsBusy()

        refreshTemporaryBasket(hasAdultsDecreased: hasAdultsDecreased) { error in
            guard error == nil else { return }

            self.delegate?.finishedLoadingRequireData(isNewRoomAdded: isNewRoomAdded)
        }
    }

    func fetchPaymentOptions(completion: @escaping (_ error: Error?) -> Void) {
        guard amendOperaDetails?.paymentOptions == nil else {
            completion(nil)
            return
        }

        guard let temporaryReference = amendOperaDetails?.temporaryBasketReference else {
            completion(AmendBookingAmendSummaryError.missingTemporaryReference)
            return
        }

        guard let originalBasketReference = amendOperaDetails?.originalBasketReference else {
            completion(AmendBookingAmendSummaryError.missingBasketReference)
            return
        }

        guard let surname = existingReservation?.surname else {
            completion(AmendBookingAmendSummaryError.missingSurname)
            return
        }

        guard let arrivalDate = existingReservation?.arrivalDate else {
            completion(AmendBookingAmendSummaryError.missingArrivalDate)
            return
        }

        guard let token = amendOperaDetails?.token else {
            completion(AmendBookingAmendSummaryError.missingToken)
            return
        }

        let reservationDetails = ReservationDetails(
            reservationId: originalBasketReference,
            surname: surname,
            arrivalDate: arrivalDate,
            business: existingReservation?.business == true,
            token: token
        )
        amendBookingDataProvider.amendSummary(
            reservationDetails: reservationDetails,
            tempBookingReference: temporaryReference
        ) { [weak self] response, error in
                let payNow = response?.paymentOptions?.payNow ?? false
                let payOnArrival = response?.paymentOptions?.payOnArrival ?? false
                let amendPaymentOptions = AmendPaymentOptions(
                    payNow: payNow,
                    payOnArrival: payOnArrival,
                    paymentOptionSelected: .later,
                    paymentCardDetails: response?.paymentCardDetails
                )
                self?.amendOperaDetails?.paymentOptions = amendPaymentOptions

                completion(error)
            }
    }

    // Analytics

    func trackState(of subView: String) {
        let stateName = PIAnalytics.StateNames.manageBooking + ": " + subView
        analytics.trackState(stateName, data: analyticsProperties)
    }
}

protocol AmendableBookingProtocol {
    var isAmendable: Bool { get }
    var isAmendAllowed: Bool { get }
}

extension AmendableBookingProtocol {
    var isAmendAllowed: Bool {
        // stayer should not be allowed to amend their booking - booked from someone else
        guard UserSessionManager.sharedInstance.currentUser?.accessLevel != .stayer else { return false }

        guard SettingsManager.sharedInstance.shouldRedirectForAmendOpera == true else { return isAmendable }

        return false
    }
}

extension Reservation: AmendableBookingProtocol {
    var isAmendable: Bool {
        amendable
    }
}

extension Stay: AmendableBookingProtocol {
    var isAmendable: Bool {
        amendable
    }
}
