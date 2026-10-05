//
//  AddRoomInteractor.swift
//  PremierInn
//
//  Created by Nick Jones on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

enum AmendCancelRoomError: AmendError {
    case missingTemporaryReference
    case missingToken
    case missingRoomId
    case cancelRoomFailed

    case genericError
}

enum AmendCancelRoomAvailabilityError: AmendError {
    case checkAvailabilityFailed
    case checkAvailabilityRateNotFound
}

enum AmendAddOrEditRoomError: AmendError {
    case missingTemporaryReference
    case missingToken
    case leadGuestTitle
    case leadGuestFirstName
    case leadGuestSurname
    case editRoomFailed

    case genericError
}

enum AmendAddOrEditRoomAvailabilityError: AmendError {
    case checkAvailabilityFailed
    case checkAvailabilityRateNotFound
}

typealias AmendRoomsUpdatedModel = (criteria: Criteria, rate: Rate)

protocol AddRoomDataProvider {
    func cancelConnections()
    func hotelAvailabilityForAmendBooking(
        withHotelCode hotelCode: String,
        bookingDetails: BookingDetails,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool,
        completion: @escaping (_ result: HotelAvailabilityResponse?, _ error: Error?) -> Void
    )
    func amendEditRoom(
        amendEditDetails: AmendRoomCriteria,
        completion: @escaping (_ response: String?, _ error: Error?) -> Void
    )
    func amendRemoveRoom(
        amendRemoveDetails: AmendRoomCriteria,
        completion: @escaping (_ response: String?, _ error: Error?) -> Void
    )
}

extension RequestsManager: AddRoomDataProvider {}

class AddRoomInteractor {
    // MARK: - Properties

    /// Boolean to track if the user has decreased the number of adults
    /// Due to BE limitation that removes all upsells when adult is decreased from 2 to 1
    /// Initially set to false and only set to true when adults decrease from 2 to 1 in updateAdultsNumber function
    /// This is then finally used in refreshTemporaryBasket function to evaluate shouldShowUpsellsRemovedMessage flag
	private var hasAdultsDecreased: Bool = false
    weak var presenter: AddRoomPresenterProtocol?

    private var editingExistingRoom: Bool = false
    private(set) var room: Room
    private(set) var roomClass: String?

    private var cityTaxPriceForRoom: Cost?

    private var existingRoom: Room?
    private var existingRateClassification: String?
    private var roomSubstitutions: [RoomSubstitution]?
    private let addRoomDataProvider: AddRoomDataProvider = RequestsManager()

    private var roomTypeAlreadyConfirmedAsAvailable: RoomType?

    internal var rateFoundFromAvailability: Rate?
    internal var cost: Cost?

    var totalCost: Cost? {
        guard var existingAmount = cost?.amount.doubleValue else { return cost }
        guard let existingCurrencyCode = cost?.currencyCode else { return cost }

        // TODO: city tax calculation in Amend Room
        if let existingRoom = existingRoom {
            if addRoomAvailabilityRequirements.isStayingForBusiness {
                existingAmount -= existingRoom.cityTax?.amount.doubleValue ?? 0
            }
        } else if let cityTaxPriceForRoom = cityTaxPriceForRoom {
            if addRoomAvailabilityRequirements.isStayingForBusiness {
                existingAmount -= cityTaxPriceForRoom.amount.doubleValue
            }
        }

        let costWithCityTaxIncorporated = Cost(amount: existingAmount, currencyCode: existingCurrencyCode)

        return costWithCityTaxIncorporated
    }

    internal var isOnlyRoom: Bool = false

    internal var leadGuestTitle: String?
    internal var leadGuestFirstName: String?
    internal var leadGuestLastName: String?

    private(set) var existingFoodUpsells: [UpsellItem]?

    internal var addRoomAvailabilityRequirements: AddRoomAvailabilityRequirements

    private(set) var roomNumber: Int
    private(set) var hotelCode: String
    private(set) var arrivalDate: Date
    private(set) var numberOfNights: Int
    private(set) var isAmendableRoom: Bool
    private(set) var isCancellableRoom: Bool
    private(set) var canAmendGuests: Bool
    private(set) var amendOperaDetails: AmendOperaDetails?

    var analytics: AnalyticsType = AnalyticsManager.shared

    // MARK: - Lifecycle

    deinit {
        print("DEINIT: \(self)")

        addRoomDataProvider.cancelConnections()
    }

    init(
        withRoom room: Room?,
        existingCost cost: Cost? = nil,
        existingUpsells: [UpsellItem]?,
        isOnlyRoom: Bool,
        isAmendableRoom: Bool,
        isCancellableRoom: Bool,
        canAmendGuests: Bool,
        roomNumber: Int,
        andAddRoomAvailabilityRequirements addRoomAvailabilityRequirements: AddRoomAvailabilityRequirements,
        amendOperaDetails: AmendOperaDetails?
    ) {
        self.roomNumber = roomNumber
        self.hotelCode = addRoomAvailabilityRequirements.hotelCode
        self.arrivalDate = addRoomAvailabilityRequirements.arrivalDate
        self.numberOfNights = addRoomAvailabilityRequirements.numberOfNights
        self.existingRateClassification = addRoomAvailabilityRequirements.existingRateClassification

        self.addRoomAvailabilityRequirements = addRoomAvailabilityRequirements

        self.isOnlyRoom = isOnlyRoom
        self.isAmendableRoom = isAmendableRoom
        self.isCancellableRoom = isCancellableRoom
        self.canAmendGuests = canAmendGuests
        self.amendOperaDetails = amendOperaDetails

        self.cost = room?.totalCost

        if let room = room {
            self.room = room

            self.setUpWithExistingRoom(with: room)
            // we used to assign the room here instead of copy - need to check if we will cause any issues
            self.existingRoom = room.copy() as? Room

            if let existingUpsells = existingUpsells {
                for upsell in existingUpsells where upsell.roomId == room.roomId {
                    self.existingFoodUpsells = [upsell]
                }
            }
        } else {
            // This is the add room scenario; in which case isOnlyRoom should always be true so that the cancel button does not show
            self.room = Room()
            self.isOnlyRoom = true
        }
    }

    private func setUpWithExistingRoom(with room: Room) {
        roomTypeAlreadyConfirmedAsAvailable = room.type
        editingExistingRoom = true

        guard let existingLeadGuestDetails = room.leadGuest else { return }

		let localisedTitle = try? Title(title: existingLeadGuestDetails.title ?? "")

        self.leadGuestTitle = localisedTitle?.localised ?? existingLeadGuestDetails.title
        self.leadGuestFirstName = existingLeadGuestDetails.firstName
        self.leadGuestLastName = existingLeadGuestDetails.lastName
    }

    private var isRoomTypeChanged: Bool {
        room.type != existingRoom?.type
    }

    /// The backend sends a `roomType` string which may not map to our `RoomType` enum.
    ///
    /// During parsing:
    /// - `room.type` is derived from `roomType` using `RoomType` and defaults to `.double` if unmapped.
    /// - `room.lettingType` stores the *original* `roomType` string from the backend.
    ///
    /// To avoid losing unmapped backend values:
    /// - If the user **changes the room type**, we send the mapped enum value (`RoomType.rawValue`).
    /// - If the room type is **unchanged**, we send `lettingType`, preserving the original backend string.
    private var roomTypeAmendRoomCriteria: String? {
        isRoomTypeChanged
        ? room.type.rawValue
        : room.lettingType
    }
}

extension AddRoomInteractor: AddRoomInteractorProtocol {
    var roomIsExistingRoom: Bool {
        self.editingExistingRoom
    }

    var wasRoomTypeAlreadyChecked: Bool {
        room.type == roomTypeAlreadyConfirmedAsAvailable
    }

    var numberOfAdults: Int {
        self.room.adults
    }

    var numberOfChildren: Int {
        self.room.children
    }

    var substitutions: [RoomSubstitution]? {
        self.roomSubstitutions
    }

    var cancellable: Bool {
        isCancellableRoom && (isOnlyRoom ? false : true)
    }

    var roomsAreAmendable: Bool {
        self.isAmendableRoom
    }

    var guestsAreAmendable: Bool {
        // if the lead guest is missing then the restriction gets overriden
        self.canAmendGuests || self.room.leadGuest == nil
    }

    func updateAdultsNumber(value: Int) {
		hasAdultsDecreased = room.adults > value
        let changeAdultsAction = AmendTrackableAction.adultsChanged("Room \(roomNumber)", room.adults, value).action
        analytics.trackAction(PIAnalytics.Action.amendChange, userInfo: [PIAnalytics.Action.amendChange: changeAdultsAction])

        room.adults = value
    }

    func updateChildrenNumber(value: Int) {
        let changeChildrenAction = AmendTrackableAction.childrenChanged("Room \(roomNumber)", room.children, value).action
        analytics.trackAction(
            PIAnalytics.Action.amendChange,
            userInfo: [PIAnalytics.Action.amendChange: changeChildrenAction]
        )

        room.children = value
    }

    func updateCotValue(_ value: Bool) {
        room.cotRequired = value
    }

    func updateRoomType(_ type: RoomType) {
        let changeRoomTypeAction = AmendTrackableAction.roomTypeChanged(
            "Room \(roomNumber)",
            room.type.localizedName,
            type.localizedName
        ).action
        analytics.trackAction(
            PIAnalytics.Action.amendChange,
            userInfo: [PIAnalytics.Action.amendChange: changeRoomTypeAction]
        )

        room.type = type
    }

    func updateLeadGuestTitle(with title: String) {
        leadGuestTitle = title
    }

    func updateLeadGuestFirstName(with firstName: String) {
        leadGuestFirstName = firstName
    }

    func updateLeadGuestLastName(with lastName: String) {
        leadGuestLastName = lastName
    }

    var newRateFoundFromAvailability: Rate? {
        self.rateFoundFromAvailability
    }

    func cancelRoom(completion: @escaping (_ success: Bool, _ rate: Rate?, _ error: Error?) -> Void) {
        guard let temporaryBookingReference = amendOperaDetails?.temporaryBasketReference else {
            completion(false, nil, AmendCancelRoomError.missingTemporaryReference)
            return
        }

        guard let token = amendOperaDetails?.token else {
            completion(false, nil, AmendCancelRoomError.missingToken)
            return
        }

        guard room.roomId?.isNotEmpty ?? false else {
            completion(false, nil, AmendCancelRoomError.missingRoomId)
            return
        }

        let removeRoomCriteria = AmendRoomCriteria(
            tempBookingRef: temporaryBookingReference,
            roomOccupancy: nil,
            leadGuest: nil,
            roomType: nil,
            token: token,
            reservationId: room.roomId,
            isBusiness: addRoomAvailabilityRequirements.isBusiness,
            specialRequests: nil
        )

        addRoomDataProvider.amendRemoveRoom(amendRemoveDetails: removeRoomCriteria) { _, error in
            guard error == nil else {
                return completion(false, nil, AmendCancelRoomError.cancelRoomFailed)
            }

            completion(true, nil, nil)
        }
    }

    func checkAvailability(completion: @escaping (_ success: Bool, _ rate: Rate?, _ error: Error?) -> Void) {
        let bookingDetailsForAvailability = BookingDetails()

        // just use the existing rate to decide if employee offer should be enabled
        bookingDetailsForAvailability.employeeRatesEnabled = existingRateClassification == SimpleNetwork.Constants
            .EmployeeOffer.rateClassification

        self.room.leadGuest = try? User(
            title: self.leadGuestTitle,
            firstName: self.leadGuestFirstName,
            lastName: self.leadGuestLastName
        )

        bookingDetailsForAvailability.criteria.rooms = addRoomAvailabilityRequirements.existingRooms

        bookingDetailsForAvailability.criteria.rooms = [room]
        bookingDetailsForAvailability.criteria.arrivalDate = self.arrivalDate
        bookingDetailsForAvailability.criteria.nights = self.numberOfNights

        var existingRoomIDs = [String]()

        for room in addRoomAvailabilityRequirements.existingRooms {
            if let roomID = room.roomId {
                existingRoomIDs.append(roomID)
            }
        }

        addRoomDataProvider.hotelAvailabilityForAmendBooking(
            withHotelCode: self.hotelCode,
            bookingDetails: bookingDetailsForAvailability,
            brand: amendOperaDetails?.brand,
            // override the employee offer feature flag to allow Amend for users with existing bookings
            allowEmployeeOffer: true
        ) { [weak self] (response, error) in
                SettingsManager.sharedInstance.roomTypesContent = response?.roomTypeContent

                guard error == nil, let hotelAvailabilityResponse = response else {
                    return completion(false, nil, AmendAddOrEditRoomAvailabilityError.checkAvailabilityFailed)
                }

                self?.rateFoundFromAvailability = hotelAvailabilityResponse.rates
                    .first(where: { $0.classification == self?.existingRateClassification })

                guard let rate = self?.rateFoundFromAvailability, let roomsInRate = rate.rooms else {
                    return completion(false, nil, AmendAddOrEditRoomAvailabilityError.checkAvailabilityRateNotFound)
                }

                var selectedRoom: RoomLettingOption?

                let selectedRoomInRate = roomsInRate.first

                // when editing an existing room and we are checking availability, it means that we are changing the room type, so in this case we should check existing rooms to pick the same room type as other rooms (e.g. only Premier Plus or only standard)
                // when adding a room, we need to look at other rooms in the booking and try and pick the same room type (standard vs premier plus) they have
                let existingRooms = self?.addRoomAvailabilityRequirements.existingRooms

                // looking for the first option that matches any of the existing rooms
                selectedRoom = selectedRoomInRate?.options?.first(where: { option in
                    existingRooms?.compactMap({ $0.lettingType }).contains(option.lettingType) ?? false
                }) ?? selectedRoomInRate?.options?.first

                if self?.editingExistingRoom == false {
                    // suppress the new cost for edit room
                    self?.cost = selectedRoom?.totalCost
                }
                self?.room.lettingType = selectedRoom?.lettingType
                self?.room.roomName = SettingsManager.sharedInstance.roomsTitleForAmend(
                    for: self?.room.lettingType,
                    roomClass: self?.roomClass
                )
                self?.cityTaxPriceForRoom = selectedRoom?.cityTax
                self?.roomClass = selectedRoom?.roomClass
                self?.room.options = selectedRoomInRate?.options

                // Here we're storing our already checked rate type; this is used so that if we transition to the room criteria screen we can show the continue button straight away and only show the check availability button again when the room type changes (And thus a new availability check needs to be made)
                self?.roomTypeAlreadyConfirmedAsAvailable = self?.room.type

                // TODO: handle substitution in Amend properly
                guard let substitutions = rate.substitutions(), let substition = substitutions.first,
                      let substitutedType = substition.substituted else {
                    return completion(true, rate, nil)
                }

                self?.room.type = substitutedType
                self?.roomSubstitutions = substitutions
                completion(true, rate, nil)
        }
    }

	func amendRoom(completion: @escaping (_ success: Bool, _ hasAdultsDecreased: Bool, _ error: Error?) -> Void) {
        self.room.leadGuest = try? User(
            title: self.leadGuestTitle,
            firstName: self.leadGuestFirstName,
            lastName: self.leadGuestLastName
        )

        // guard there are changes, else return success
        if let existingRoom = existingRoom {
            // these are the same here
            guard room.isIdenticalTo(room: existingRoom) == false else {
                completion(true, false, nil)
                return
            }
        }

        guard let temporaryReference = amendOperaDetails?.temporaryBasketReference else {
            completion(false, false, AmendAddOrEditRoomError.missingTemporaryReference)
            return
        }

        guard let token = amendOperaDetails?.token else {
            completion(false, false, AmendAddOrEditRoomError.missingToken)
            return
        }

        guard let leadGuestTitle = leadGuestTitle else {
            completion(false, false, AmendAddOrEditRoomError.leadGuestTitle)
            return
        }

        guard let leadGuestFirstName = leadGuestFirstName else {
            completion(false, false, AmendAddOrEditRoomError.leadGuestFirstName)
            return
        }

        guard let leadGuestLastName = leadGuestLastName else {
            completion(false, false, AmendAddOrEditRoomError.leadGuestSurname)
            return
        }

        // if roomId is set, we call edit room. if empty, add room
        var roomId: String?
        if room.roomId?.isNotEmpty == true {
            roomId = room.roomId
        }

        // Only include email for business accounts to fix self booker business account amend issue
        // For consumer accounts, email should remain nil to avoid validation errors
        let emailAddress = addRoomAvailabilityRequirements.isBusiness ? amendOperaDetails?.bookerEmail : nil
        let leadGuest = SimpleNetwork.LeadGuest(
            title: leadGuestTitle,
            firstName: leadGuestFirstName,
            lastName: leadGuestLastName,
            emailAddress: emailAddress
        )

        let roomOccupancy = RoomOccupancyAmend(
            adultsNumber: numberOfAdults,
            childrenNumber: numberOfChildren,
            cotRequired: room.cotRequired
        )

        let roomOption = room.options?.first(where: { $0.lettingType == room.lettingType })

        let criteria = AmendRoomCriteria(
            tempBookingRef: temporaryReference,
            roomOccupancy: roomOccupancy,
            leadGuest: leadGuest,
            roomType: roomTypeAmendRoomCriteria,
            token: token,
            reservationId: roomId,
            isBusiness: addRoomAvailabilityRequirements.isBusiness,
            specialRequests: roomOption?.specialRequests
        )

        addRoomDataProvider.amendEditRoom(amendEditDetails: criteria) { temporaryBasketReference, error in
            guard let temporaryBasketReference = temporaryBasketReference, temporaryBasketReference == temporaryReference,
                  error == nil else {
                return completion(false, false, AmendAddOrEditRoomError.editRoomFailed)
            }

			return completion(true, self.hasAdultsDecreased, nil)
        }
    }

    func finalisedRoomDetails() -> Room {
        let finalRoomDetails = self.room

        if let title = self.leadGuestTitle,
           let firstName = self.leadGuestFirstName,
           let lastName = self.leadGuestLastName {
            finalRoomDetails.leadGuest = try? User(title: title, firstName: firstName, lastName: lastName)
        }

        return finalRoomDetails
    }
}

private extension Room {
    func isIdenticalTo(room: Room) -> Bool {
        if self.type != room.type { return false }
        if self.adults != room.adults { return false }
        if self.children != room.children { return false }
        if self.cotRequired != room.cotRequired { return false }
        if self.leadGuest?.title != room.leadGuest?.title { return false }
        if self.leadGuest?.firstName != room.leadGuest?.firstName { return false }
        if self.leadGuest?.lastName != room.leadGuest?.lastName { return false }

        return true
    }
}
