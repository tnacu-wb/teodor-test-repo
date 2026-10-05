//
//  RoomsGuestsCriteriaInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

typealias CustomerServicesModel = (phoneNumber: String, title: String, message: String)

enum RoomsGuestsError: LocalizedError {
    case cotAndAccessibleRoomNotAllowed
}

enum AddRoomError: Error {
    case callUs
    case goToWeb

    var title: String {
        PILocalizedString("maxRoomBookingMessageGeneric")
    }

    var message: String {
        switch self {
        case .callUs:
            return String.localizedStringWithFormat(
                PILocalizedString("maxRoomsNumberReachedCallUs", comment: ""),
                SettingsManager.sharedInstance.activeRules.maxRooms + 1,
                Constants.maxPhoneNumberRoomNumber
            )
        case .goToWeb:
            return String.localizedStringWithFormat(
                PILocalizedString("maxRoomsNumberReachedGoToWeb", comment: ""),
                Constants.maxPhoneNumberRoomNumber + 1
            )
        }
    }

    var alert: UIAlertController? {
        switch self {
        case .callUs:
            return AlertManager.callUsAlert(
                withTitle: self.title,
                message: self.message,
                number: CallNumberType.generic.phoneNumber
            )
        case .goToWeb:
            return AlertManager.goToWebAlert(
                withTitle: self.title,
                message: self.message,
                url: Constants.groupWebFormUrl
            )
        }
    }
}

class RoomsGuestsCriteriaViewModel {
    private(set) var criteria: Criteria
    private var roomsInfants = [String: Int]()

    init(criteria: Criteria) {
        // *=*=*=*=*=*=*=*=* 📜🤮 Horrible front-end business logic 🤮📜 *=*=*=*=*=*=*=*=*
        for room in criteria.rooms {
            if room.type == .accessible && room.cotRequired {
                room.cotRequired = false
            }
        }
        // *=*=*=*=*=*=*=*=**=*=*=*=*=*=*=*=**=*=*=*=*=*=*=*=**=*=*=*=*=*=*=*=**=*=*=*=*=*

        self.criteria = criteria
    }
}

extension RoomsGuestsCriteriaViewModel: RoomsGuestsCriteriaInteractorProtocol {
    var shouldShowAddRoomSection: Bool {
        UserSessionManager.sharedInstance.currentUser?.isBusiness ?? false == false
    }

    var infantCustomerServicesModel: CustomerServicesModel {
        (
            Constants.PhoneNumbers.nationalRateFallBack,
            PILocalizedString("Need more than 1 cot?", comment: ""),
            PILocalizedString("singleCotPerRoomReservationsCallNotice")
        )
    }

    var cotInAccessibleCustomerServicesModel: CustomerServicesModel {
        (
            Constants.PhoneNumbers.nationalRateFallBack,
            PILocalizedString("Do you need a cot provided?", comment: ""),
            PILocalizedString(
                "If so, please call our reservations team to complete your booking. Calls charged at the national rate."
            )
        )
    }

	func updateAdultsNumber(value: Int, roomIndex: Int) {
		guard roomIndex < criteria.rooms.count else { return }

		criteria.rooms[roomIndex].adults = value
	}

	func updateChildrenNumber(value: Int, roomIndex: Int) {
		guard roomIndex < criteria.rooms.count else { return }

		criteria.rooms[roomIndex].children = value
	}

	func updateCotValue(_ value: Bool, roomIndex: Int) throws {
		guard roomIndex < criteria.rooms.count else { return }

        guard !(criteria.rooms[roomIndex].type == .accessible && value == true) else {
            criteria.rooms[roomIndex].cotRequired = false
            throw RoomsGuestsError.cotAndAccessibleRoomNotAllowed
        }

		criteria.rooms[roomIndex].cotRequired = value
	}

	func updateRoomType(_ type: RoomType, roomIndex: Int) throws {
		guard roomIndex < criteria.rooms.count else { return }

        criteria.rooms[roomIndex].type = type

        guard !(type == .accessible && criteria.rooms[roomIndex].cotRequired == true) else {
            criteria.rooms[roomIndex].cotRequired = false
            throw RoomsGuestsError.cotAndAccessibleRoomNotAllowed
        }
	}

    func updateInfants(_ value: Int, roomIndex: Int) {
        roomsInfants["Room\(roomIndex)"] = value

        guard roomIndex < criteria.rooms.count else { return }

        criteria.rooms[roomIndex].infants = value
    }

    func appendRoom() throws -> Room {
        guard criteria.rooms.count < SettingsManager.sharedInstance.activeRules.maxRooms else {
            throw criteria.rooms.count >= Constants.maxPhoneNumberRoomNumber ? AddRoomError.goToWeb : AddRoomError.callUs
        }

        let room = Room()
        criteria.rooms.append(room)

        return room
    }

    func removeRoomAtIndex(_ index: Int) throws {
        guard criteria.rooms.count > Constants.minRoomNumber else {
            let userInfo = [NSLocalizedDescriptionKey: PILocalizedString(
                "minRoomsNumberReached",
                comment: "Error message showed when the minimum number of rooms limit is reached"
            )]

            throw NSError(domain: PIError.domain, code: PIError.Code.minRoomsNumberReached, userInfo: userInfo)
        }

		criteria.rooms.remove(at: index)
    }

    func infants(for section: Int) -> Int {
        criteria.rooms[section].infants
    }
}
