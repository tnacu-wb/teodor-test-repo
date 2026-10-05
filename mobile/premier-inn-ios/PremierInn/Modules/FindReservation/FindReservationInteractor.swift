//
//  FindReservationInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum ReservationDetailsError: LocalizedError {
    case missingFormValues
    case missingReferenceNumber
    case missingBookerSurname
    case missingCheckInDate
    case unexpectedError

    var errorDescription: String? { String(describing: self)    }
}

protocol FindReservationInteractorProtocol {
    var arrivalDate: Date? { get }
    var reservationNumber: String? { get }
    var lastName: String? { get }
    func submitFormData(values: PIDictionary, completion: @escaping (Result<Reservation>) -> Void) throws
    func searchForLocalStayUsingReservation(_ reservation: Reservation) -> Stay?
    func trackError(_ error: Error)
}

final class FindReservationInteractor {
    var arrivalDate: Date?
    var reservationNumber: String?
    var lastName: String?
    private let requestsManager = RequestsManager()

    init(arrivalDate: String?, reservationNumber: String?, lastName: String?) {
        if let arrivalDate {
            self.arrivalDate = DateFormatter.parameterFormatter.date(from: arrivalDate)
        }
        self.reservationNumber = reservationNumber
        self.lastName = lastName
    }

    private func saveStayToLocalStore(
        reservation: Reservation,
        importName: String? = nil,
        completion: @escaping (Result<Reservation>) -> Void
    ) {
        requestsManager.loadHotel(with: reservation.hotelCode) { hotel, error in
            do {
                let dictionary = Stay.dictionary(
                    reservation: reservation,
                    hotel: hotel,
                    importType: .imported,
                    importName: importName
                )
                let stay = try Stay(dictionary: dictionary)

                let extractedDigitalKeyIdentifier = LocalReservationManager.shared
                    .getDigitalKeyIdentifierForReservation(reservation.confirmationNumber)
                stay.digitalKeyIdentifier = extractedDigitalKeyIdentifier

                // Update the cached reservations list
                _ = LocalReservationManager.shared.update(with: [stay])
                LocalReservationManager.shared.updateDigitalKeyIdentifiers(with: [StayWithKeyIdentifier(stay: stay)])

                ReservationsListViewController.shareDataWithTodayWidget()

                completion(.success(result: reservation))
            } catch {
                completion(.failure(error: error))
            }
        }
    }
}

extension FindReservationInteractor: FindReservationInteractorProtocol {
    func searchForLocalStayUsingReservation(_ reservation: Reservation) -> Stay? {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        let stay = reservationsManager.items.first(where: {
            $0.identifier == reservation.confirmationNumber
        })

        return stay
    }

    func submitFormData(values: PIDictionary, completion: @escaping (Result<Reservation>) -> Void) throws {
        guard let referenceNumber = values[ReservationFormRows.referenceNumber.rawValue] as? String
            else { throw ReservationDetailsError.missingReferenceNumber }
        guard let bookerSurname = values[ReservationFormRows.bookerSurname.rawValue] as? String
            else { throw ReservationDetailsError.missingBookerSurname }
        guard let checkInDate = values[ReservationFormRows.checkInDate.rawValue] as? Date
            else { throw ReservationDetailsError.missingCheckInDate }

        let findBookingDetails = FindBookingDetails(
            reservationId: referenceNumber,
            surname: bookerSurname,
            arrivalDate: checkInDate,
            business: false
        )

        // TODO: for now we don't expect to import BB bookings

        requestsManager.findBookingSource(findBookingDetails: findBookingDetails) { source, _ in
            // Don't check for errors as if this call fails we want to continue with the assumption it is a Bart Booking

            let reservationDetails = ReservationDetails(
                reservationId: source?.basketReference ?? referenceNumber,
                surname: bookerSurname,
                arrivalDate: checkInDate,
                business: false,
                token: source?.token
            )

            self.requestsManager.reservation(
                reservationDetails: reservationDetails,
                hotelCode: source?.hotelId,
                bookingDetails: nil
            ) { reservation, error in
                self.handleReservationResponse(
                    reservation: reservation,
                    error: error,
                    bookerSurname: bookerSurname,
                    completion: completion
                )
            }
        }
    }

    private func handleReservationResponse(
        reservation: Reservation?,
        error: Error?,
        bookerSurname: String,
        completion: @escaping (Result<Reservation>) -> Void
    ) {
        if let reservation = reservation {
            self.saveStayToLocalStore(reservation: reservation, importName: bookerSurname, completion: completion)
        } else if let error = error {
            completion(Result.failure(error: error))
        } else {
            completion(Result.failure(error: ReservationDetailsError.unexpectedError))
        }
    }

    func trackError(_ error: Error) {
        var data = PIDictionary()
        data[PIAnalytics.Keys.addBookingErrorMessage] = error.localizedDescription
        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.addBooking, data: data)
    }
}
