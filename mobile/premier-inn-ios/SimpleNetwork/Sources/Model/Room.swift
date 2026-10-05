//
//  Room.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 18/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public enum BookingStatus: String {
    case cancelled = "Cancelled"
    case checkedIn = "InHouse"
    case unknown
}

public protocol RoomAndNextDestinationModel {
    var room: Room { get set }
    var nextDestination: String? { get set }
}

public enum RoomGroupId: String {
    case single
    case double
    case twin
    case family
    case accessible
}

public class Room {
    // MARK: - Properties

	public var type: RoomType = .double
    public var lettingType: String?
    public var adults = 1 {
        didSet {
            guestsDidChange()
        }
    }
    public var children = 0 {
        didSet {
            guestsDidChange()
        }
    }
    public var infants = 0 {
        didSet {
            if infants == 0 {
                cotRequired = false
            }
        }
    }
    public var cotRequired = false {
        didSet {
            if cotRequired { infants = 1 }
            guestsDidChange()
        }
    }
    public var dailyRates: [DailyRate]?
    public var totalCost: Cost?
    public var cityTax: Cost?
    public var leadGuest: User?
    public var accompanyingGuest: User?
    public var roomId: String?
    public var roomName: String?
    public var roomNumber: String?
    public var groupId: RoomGroupId?
    public var bookingStatus: BookingStatus?
    public var reservationId: String?
    public var guestList: [User]?
    public var preCheckInStatus: Bool?
    public var deRegCardCompleted: Bool?

    public var options: [RoomLettingOption]?

    // MARK: - PI Properties

    public private(set) var uniqueID: UUID

    // MARK: - Init

    public init() {
        self.uniqueID = UUID()
    }

    public init(dictionary: PIDictionary) {
        self.uniqueID = UUID()

        self.adults = dictionary["adults"] as? Int ?? 1
        self.children = dictionary["children"] as? Int ?? 0
        self.cotRequired = dictionary.value(forKeys: ["cotRequired", "cot"]) ?? false
        self.lettingType = dictionary["lettingType"] as? String
        self.totalCost = try? Cost(dictionary: dictionary["totalCost"] as? PIDictionary)
        self.cityTax = try? Cost(dictionary: dictionary["cityTax"] as? PIDictionary)
        self.roomId = dictionary["roomId"] as? String
        self.roomName = dictionary["roomName"] as? String

        if let groupId = dictionary["groupId"] as? String {
            self.groupId = RoomGroupId(rawValue: groupId)
        }

        self.reservationId = dictionary["reservationId"] as? String
        self.preCheckInStatus = dictionary["preCheckInStatus"] as? Bool ?? false
        self.deRegCardCompleted = dictionary["deRegCardCompleted"] as? Bool

        self.leadGuest = {
            guard let dict = dictionary["guest"] as? PIDictionary else { return nil }
            let user = try? User(
                title: dict["title"] as? String,
                firstName: dict["firstName"] as? String,
                lastName: dict["lastName"] as? String,
                email: dict["emailAddress"] as? String,
                telephone: dict["telephone"] as? String
            )

            if let additionalDetails = dict["additionalDetails"] as? PIDictionary {
                user?.country = Country.countriesList
                    .first(where: { $0.isoCode == additionalDetails["nationality"] as? String })
            }
            return user
        }()

        self.accompanyingGuest = {
            guard let dict = dictionary["accompanyingGuest"] as? PIDictionary else { return nil }

            return try? User(
                title: dict["title"] as? String,
                firstName: dict["firstName"] as? String,
                lastName: dict["lastName"] as? String
            )
        }()

        if let type: String = dictionary.value(forKeys: ["type", "roomType"]) {
            self.type = RoomType(rawValue: type) ?? .double
        }

        if let dailyRatesArr = dictionary["dailyRates"] as? [PIDictionary] {
            self.dailyRates = dailyRatesArr.compactMap { DailyRate(dictionary: $0) }
        }

        self.bookingStatus = {
            guard let rawStatus = dictionary["bookingStatus"] as? String else { return .unknown }

            return BookingStatus(rawValue: rawStatus) ?? .unknown
        }()

        self.options = {
            guard let optionsDic = dictionary["options"] as? [PIDictionary],
                  let data = try? JSONSerialization.data(withJSONObject: optionsDic, options: .prettyPrinted)
            else { return nil }

            do {
                let decoder = JSONDecoder()
                return try decoder.decode([RoomLettingOption].self, from: data)
            } catch {
                printDev(error)
                return nil
            }
        }()

        self.guestList = getReservationGuest(dictionaries: dictionary["guestList"] as? [PIDictionary])
        self.roomNumber = dictionary["roomNumber"] as? String
    }

    private func guestsDidChange() {
        let availableTypes = type.availableTypes(adults: adults, children: children, cot: cotRequired)

        if availableTypes.contains(type) == false {
            type = availableTypes.first ?? .double
        }
    }

    private  func getReservationGuest(dictionaries: [PIDictionary]?) -> [User] {
        var tempUsers = [User]()

        guard let dictionaries else { return [] }

        for dict in dictionaries {
            let user = try? User(
                title: dict["title"] as? String,
                firstName: dict["firstName"] as? String,
                lastName: dict["lastName"] as? String,
                regCardUser: true
            )
            guard let user else { return [] }
            if let additionalDetails = dict["additionalDetails"] as? PIDictionary {
                user.dob = additionalDetails["dob"] as? String
                let nationality = additionalDetails["nationality"] as? String
                if let passportNumber = additionalDetails["passportNumber"] as? String,
                   let nationality {
                    user.passport = Passport(number: passportNumber, countryOfIssue: nationality)
                }
                if let nationality,
                   let country = Country.countriesList.first(where: { $0.isoCode == nationality }) {
                    user.country = country
                }
            }
            user.isAccompanyingGuest = dict["isAccompanyingGuest"] as? Bool ?? false
            user.profileId = dict["profileId"] as? String

            if let address = dict["address"] as? PIDictionary {
                user.address = try? Address(dictionary: address)
            }
            tempUsers.append(user)
        }
        return tempUsers
    }
}

public struct RoomRequirements: Equatable {
    let hotelBrand: String?
    let lettingType: String?
    public var adults: Int
    public var children: Int
    public var cotRequired: Bool
    public var type: RoomType?

    static var standard: RoomRequirements = RoomRequirements(
        hotelBrand: "PI",
        lettingType: nil,
        adults: 1,
        children: 0,
        cotRequired: false,
        type: .double
    )
}

extension RoomRequirements: Codable {
    enum CodingKeys: String, CodingKey {
        case hotelBrand
        case lettingType
        case adults
        case children
        case cotRequired
        case type
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self.hotelBrand = try? container.decode(String.self, forKey: .hotelBrand)
        self.lettingType = try? container.decode(String.self, forKey: .lettingType)
        self.children = try container.decode(Int.self, forKey: .children)
        self.cotRequired = try container.decode(Bool.self, forKey: .cotRequired)
        self.type = try? container.decode(RoomType.self, forKey: .type)

        let adultsValue = try container.decode(Int.self, forKey: .adults)
        adults = adultsValue < Constants.minimumNumberOfAdultsInRoom ? Constants.minimumNumberOfAdultsInRoom : adultsValue
    }
}

public extension RoomRequirements {
    var room: Room {
        let room = Room()
        room.type = type ?? .double
        room.adults = adults
        room.children = children
        room.cotRequired = cotRequired

        return room
    }
}

extension Room: NSCopying {
    /**

     Duplicate a room, copying over all of the existing properties and returning a new room with a new randomly generated UUID

     If you want to completely copy a room in that you even want to copy the unique ID rather than generating a new one then use copy()

     */
    public func duplicate(with zone: NSZone? = nil) -> Any {
        let room = Room()
        room.adults = self.adults
        room.bookingStatus = self.bookingStatus
        room.children = self.children
        room.infants = self.infants
        room.cotRequired = self.cotRequired
        room.dailyRates = self.dailyRates
        room.cityTax = self.cityTax
        room.leadGuest = self.leadGuest
        room.lettingType = self.lettingType
        room.totalCost = self.totalCost
        room.type = self.type
        room.roomId = self.roomId
        room.roomName = self.roomName
        room.groupId = self.groupId
        room.options = self.options
        room.reservationId = self.reservationId
        room.guestList = self.guestList
        room.preCheckInStatus = self.preCheckInStatus
        room.deRegCardCompleted = self.deRegCardCompleted
        return room
    }

    /**

     Copy a room, taking all of its existing properties and creating a new room with the same unique ID; NOTE: this is not recommended in most situations

     If you want to duplicate this room in that you want to copy all of an existing room's properties but generate a new unique ID then use duplicate()

     */
    public func copy(with zone: NSZone? = nil) -> Any {
        let room = Room()
        room.uniqueID = self.uniqueID
        room.adults = self.adults
        room.bookingStatus = self.bookingStatus
        room.children = self.children
        room.infants = self.infants
        room.cotRequired = self.cotRequired
        room.dailyRates = self.dailyRates
        room.cityTax = self.cityTax
        room.leadGuest = self.leadGuest
        room.lettingType = self.lettingType
        room.totalCost = self.totalCost
        room.type = self.type
        room.roomId = self.roomId
        room.roomName = self.roomName
        room.groupId = self.groupId
        room.options = self.options
        room.reservationId = self.reservationId
        room.guestList = self.guestList
        room.preCheckInStatus = self.preCheckInStatus
        room.deRegCardCompleted = self.deRegCardCompleted
        return room
    }
}

extension Array where Element == Room {
    var totalCost: Cost? {
        guard let currencyCode = first?.options?.first?.totalCost?.currencyCode else { return nil }

        let startingAmount: Double = 0

        let totalAmount: Double = compactMap { room in
            let selection = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first

            return selection?.totalCost?.amount.doubleValue
        }.reduce(startingAmount, +)

        return Cost(amount: totalAmount, currencyCode: currencyCode)
    }
}

extension Room: Equatable {
    public static func == (lhs: Room, rhs: Room) -> Bool {
        (lhs.type == rhs.type &&
                lhs.adults == rhs.adults &&
                lhs.children == rhs.children &&
                lhs.cotRequired == rhs.cotRequired)
    }
}
