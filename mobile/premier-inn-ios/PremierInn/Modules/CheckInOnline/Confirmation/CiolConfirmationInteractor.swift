//
//  CiolConfirmationInteractor.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import PassKit

protocol CiolConfirmationDataProvider: LabelsProvider {
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void)
	func findBookingSource(findBookingDetails: FindBookingDetails, completion: @escaping (FindBookingSource?, Error?) -> Void)
	func reservation(
		reservationDetails: ReservationDetails,
		hotelCode: String?,
		bookingDetails: BookingDetails?,
		completion: @escaping (Reservation?, Error?) -> Void
	)
	func loadHotel(with hotelCode: String, completion: @escaping (_ data: Hotel?, _ error: Error?) -> Void)
}
extension RequestsManager: CiolConfirmationDataProvider {}
class CiolConfirmationInteractor {
    let ciolConfirmationDataProvider: CiolConfirmationDataProvider
    var ciolConfirmationDetails: CiolConfirmationDetails

    init(
    	with dataProvider: CiolConfirmationDataProvider = RequestsManager(),
    	ciolConfirmationDetails: CiolConfirmationDetails
    ) {
        self.ciolConfirmationDetails = ciolConfirmationDetails
        self.ciolConfirmationDataProvider = dataProvider
    }

    func refreshStays(completion: @escaping (Bool?) -> Void) {
        ciolConfirmationDataProvider.refreshStays(
        	for: UserSessionManager.sharedInstance.currentUser,
        	shouldAttemptLogin: true,
        	completion: completion
        )
    }
}

extension CiolConfirmationInteractor: CiolConfirmationInteractorProtocol {
    var customAnalyticsParameters: PIDictionary? {
        var info = ciolConfirmationDetails.analyticsInfo
        info[PIAnalytics.Keys.checkInOnlineAction] = PIAnalytics.StateNames.checkInOnlineConfirmation
        return info
    }

    var roomKeyInstructionsModel: InstructionsViewModel? {
        guard let surname = ciolConfirmationDetails.stay.surname,
              let arrivalDate = ciolConfirmationDetails.stay.arrivalDate else {
            return nil
        }
        let reservationDetails = ReservationDetails(
        	reservationId: ciolConfirmationDetails.stay.identifier,
        	surname: surname,
        	arrivalDate: arrivalDate,
        	business: ciolConfirmationDetails.stay.business,
        	token: nil
        )
        let savedPass = PKPassLibrary().pass(
        	withPassTypeIdentifier: Constants.pkPassTypeIdentifier,
        	serialNumber: ciolConfirmationDetails.stay.pkPassSerialNumber
        )

		let type: InstructionsType = {
			switch (ciolConfirmationDetails.stay.qrCodeIsEnabled, ciolConfirmationDetails.stay.isDigitalKeyEnabled) {
			case (true, true):
                return .roomWithQRCodeAndDigitalKey(isKeyDownloaded: ciolConfirmationDetails.stay.userHasPassInWallet)
			case (false, true):
                return .roomWithoutQRCodeButWithDigitalKey(isKeyDownloaded: ciolConfirmationDetails.stay.userHasPassInWallet)
			case (true, false):
                return .roomKeyWithQRCode
			case (false, false):
                return .roomKeyWithoutQRCode
			}
		}()

        let model = InstructionsViewModel(
        	type: type,
        	bookingReference: ciolConfirmationDetails.stay.identifier,
        	reservationDetails: reservationDetails,
        	isQRCodeEnabled: ciolConfirmationDetails.stay.qrCodeIsEnabled,
        	savedPass: savedPass
        )
        return model
    }

	func callBookingConfirmation() {
		guard let surname = ciolConfirmationDetails.stay.surname,
		      let arrivalDate = ciolConfirmationDetails.stay.arrivalDate else {
			AnalyticsManager.shared.track(errorName: PIAnalytics.Error.ciolConfirmationMissingData)
            return
		}

		let reservationId = ciolConfirmationDetails.stay.identifier
		let isBusiness = ciolConfirmationDetails.stay.isBusinessTrip
		let hotelCode = ciolConfirmationDetails.stay.hotelCode
		let importType = ReservationImportType(rawValue: self.ciolConfirmationDetails.stay.importType) ?? ReservationImportType
			.unknown

		let findBookingDetails = FindBookingDetails(
			reservationId: reservationId,
			surname: surname,
			arrivalDate: arrivalDate,
			business: isBusiness
		)

		ciolConfirmationDataProvider.findBookingSource(findBookingDetails: findBookingDetails) { source, error in
			guard let token = source?.token,
			      let basketReference = source?.basketReference,
			      error == nil else {
				return AnalyticsManager.shared.track(errorName: PIAnalytics.Error.ciolConfirmationFindBookingFailed)
			}

			let reservationDetails = ReservationDetails(
				reservationId: basketReference,
				surname: surname,
				arrivalDate: arrivalDate,
				business: isBusiness,
				token: token
			)

			self.ciolConfirmationDataProvider.loadHotel(with: hotelCode) { hotel, _ in
				guard let hotel else {
					return AnalyticsManager.shared.track(errorName: PIAnalytics.Error.ciolConfirmationHotelInfoFailed)
				}

				self.ciolConfirmationDataProvider.reservation(
					reservationDetails: reservationDetails,
					hotelCode: hotelCode,
					bookingDetails: nil
				) { reservation, _ in
					guard let reservation else {
						return AnalyticsManager.shared.track(errorName: PIAnalytics.Error.ciolConfirmationBookingConfFailed)
					}

					let dictionary = Stay.dictionary(
						reservation: reservation,
						hotel: hotel,
						importType: importType,
						importName: surname
					)
					guard let stay = try? Stay(dictionary: dictionary) else { return }
					stay.isCheckInOnlineAvailable = reservation.isCheckInOnlineAvailable
					_ = LocalReservationManager.shared.update(with: [stay])
				}
			}
		}
	}
}
