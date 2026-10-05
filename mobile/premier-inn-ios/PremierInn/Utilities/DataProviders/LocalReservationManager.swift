//
//  LocalReservationManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/01/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

private enum LocalReservationManagerConstants {
    static let updateTimeKey = "LocalReservationRefreshLastTime"
    static let minimumUpdateTime = secondsInHour * 2
    static let upcomingBookingSecondsCount = secondsInDay * 14
}

protocol LocalReservationManagerDataProvider {
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (Reservation?, Error?) -> Void
    )
    func findBookingSource(
        findBookingDetails: FindBookingDetails,
        completion: @escaping (FindBookingSource?, Error?) -> Void
    )
}

extension RequestsManager: LocalReservationManagerDataProvider {}

class LocalReservationManager {
    static let shared = LocalReservationManager()

    let dataProvider: LocalReservationManagerDataProvider = RequestsManager()

    func refreshReservations(ignoreSchedule: Bool = false) -> Bool {
        guard ({
            guard ignoreSchedule == false else { return true }
            guard let lastUpdate = UserDefaults.standard
                  .object(forKey: LocalReservationManagerConstants.updateTimeKey) as? Date else { return true }
            let difference = Date().timeIntervalSince(lastUpdate)
            guard difference >= LocalReservationManagerConstants.minimumUpdateTime else { return false }

            return true
            }()) == true else { return false }

        let stays: [Stay] = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard).items.activeStays

        for stay in stays {
            refresh(stay: stay)
        }

        UserDefaults.standard.set(Date(), forKey: LocalReservationManagerConstants.updateTimeKey)
        UserDefaults.standard.synchronize()

        return true
    }

    @discardableResult
    public func update(with summaries: [Stay], sendUpdateNotification: Bool = true) -> Bool {
        if sendUpdateNotification {
            NotificationCenter.default.post(name: .staysDidChange, object: nil)
        }

        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        return reservationsManager.update(with: summaries)
    }

    func refreshUpcomingReservations() {
        let stays: [Stay] = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard).items.activeStays.filter {
            guard let arrivalDate = $0.arrivalDate else { return false }
            return arrivalDate.timeIntervalSince(Date()) <= LocalReservationManagerConstants.upcomingBookingSecondsCount
        }

        for stay in stays {
            printDev(stay.arrivalDateString)
            refresh(stay: stay)
        }
    }

    func refresh(stay: Stay) {
        guard let arrivalDate = stay.arrivalDate else { return }
        guard let surname = stay.surname else { return }
        let reference = stay.identifier
        let findBookingDetails = FindBookingDetails(
            reservationId: reference,
            surname: surname,
            arrivalDate: arrivalDate,
            business: stay.isBusinessTrip
        )

        // TODO: for now we don't expect to import BB bookings

        self.dataProvider.findBookingSource(findBookingDetails: findBookingDetails) { source, _ in
            // Don't check for errors as if this call fails we want to continue with the assumption it is a Bart Booking

            let reservationDetails = ReservationDetails(
                reservationId: source?.basketReference ?? stay.identifier,
                surname: surname,
                arrivalDate: arrivalDate,
                business: stay.isBusinessTrip,
                token: nil // only needed for manageBooking which we don't do here
            )
            // this is a bookingConfirmation call only (no manageBooking), so if we have the basketReference we could skip the findBookingSource
            self.dataProvider.reservation(
                reservationDetails: reservationDetails,
                hotelCode: nil,
                bookingDetails: BookingDetails.sharedInstance
            ) { reservation, _ in
                if let reservation = reservation {
                    stay.totalCost = reservation.totalCost
                    stay.numberOfRoooms = reservation.rooms.count
                    stay.arrivalDateString = reservation.arrivalDate?.parameterString ?? stay.arrivalDateString
                    stay.checkOutDateString = reservation.checkOutDate?.parameterString ?? stay.checkOutDateString

                    stay.amendable = reservation.amendable
                    stay.cancelled = reservation.cancelled
                    stay.cancelable = reservation.cancelable

                    stay.checkedIn = (reservation.rooms.first(where: { $0.bookingStatus ?? .unknown == .checkedIn }) != nil)

                    _ = self.update(with: [stay], sendUpdateNotification: false)
                }
            }
        }
    }

    func cleanupLocalCheckInSessions() {
        let storageManager = SimpleStorageManager<CheckInSession>(dataSource: UserDefaults.standard)

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = CheckInSession.dateFormat

        _ = storageManager.items.filter {
            guard let date = dateFormatter.date(from: $0.timestamp) else { return true }
            return Date().timeIntervalSince(date) > secondsInDay
        }.map { storageManager.remove($0) }
    }

    public func updateDigitalKeyIdentifiers(with keys: [StayWithKeyIdentifier?]) {
        let keysIdentifierManager = SimpleStorageManager<StayWithKeyIdentifier>(dataSource: UserDefaults.standard)
		keys
			.compactMap { $0 }
			.filter { $0.digitalKeyIdentifier.isNotEmpty }
			.forEach {
				try? keysIdentifierManager.add($0)
			}
    }

    func getDigitalKeyIdentifierForReservation(_ identifier: String) -> String? {
        let keysIdentifierManager = SimpleStorageManager<StayWithKeyIdentifier>(dataSource: UserDefaults.standard)
        let stay = keysIdentifierManager.items.first(where: { $0.identifier == identifier })

        return stay?.digitalKeyIdentifier
    }
}
