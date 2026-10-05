//
//  OneTimePasswordInteractor.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 08/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//
import SimpleNetwork
import PassKit

class OneTimePasswordInteractor {
    var stay: Stay
    var otpEmail: String
    var roomId: String
	var provisioningCredentialIdentifier: String?

    internal var requestsManager = RequestsManager()

    init(stay: Stay, otpEmail: String, roomId: String) {
        self.stay = stay
        self.otpEmail = otpEmail
        self.roomId = roomId
    }

    var checkInDateTime: Date? {
        let checkInHour = stay.isEarlyCheckInForAllRooms ? 11 : 15
        guard let arrivalDate = stay.arrivalDate else { return nil }
        return Calendar.current.date(bySettingHour: checkInHour, minute: 0, second: 0, of: arrivalDate)
    }

    var walletKeyImage: UIImage {
        let baseImage = UIImage(resource: .walletKeyLogo)

        let overlays = [
            UIImage.OverlayText(
                text: "\(Date.walletKeyFormat(arrival: stay.arrivalDate, departure: stay.checkOutDate) ?? "")",
                position: .topRight,
                attributes: [
                    .font: UIFont.Body_Semibold(),
                    .foregroundColor: UIColor.BaseWhite
                ],
                padding: 16
            ),
            UIImage.OverlayText(
                text: stay.hotelName,
                position: .bottomLeft,
                attributes: [
                    .font: UIFont.Heading3_Semibold(),
                    .foregroundColor: UIColor.BaseWhite
                ],
                padding: 16
            )
        ]
        return baseImage.overlayWithText(overlays: overlays)
    }
}

extension OneTimePasswordInteractor: OneTimePasswordInteractorInput {
    func otpResend(completion: @escaping (Bool?, Error?) -> Void) {
        requestsManager.generateOTP(email: otpEmail, completion: completion)
    }

    func validate(withCode code: String, completion: @escaping (Bool?, Error?) -> Void) {
        requestsManager.verifyOTP(bookingReference: stay.identifier, otpCode: code, completion: completion)
    }

    func fetchKeyProvisioningData(otpCode: String, completion: @escaping (DigitalKeyProvisionResponse?) -> Void) {
        requestsManager.digitalKeyProvision(
            bookingReference: stay.identifier,
            otpCode: otpCode,
            email: otpEmail,
            reservationId: roomId
        ) { response, error in
            guard error == nil else { return completion(nil) }
            completion(response)
        }
    }

    func fetchKeyProvisioningData(otpCode: String) async -> DigitalKeyProvisionResponse? {
          await withCheckedContinuation { continuation in
              fetchKeyProvisioningData(otpCode: otpCode) { provisioningData in
                  continuation.resume(returning: provisioningData)
              }
          }
    }

    func updateStayWithPassId(with id: String) {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        stay.digitalKeyIdentifier = id
        _ = reservationsManager.update(with: [stay])
        // update the local storage for digital key identifiers
        let keyIdList = [StayWithKeyIdentifier(stay: stay)]
        LocalReservationManager.shared
            .updateDigitalKeyIdentifiers(with: keyIdList)
    }

    func trackStateAnalyticsForPKAddSecurePass(pageName: String) {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.checkInConf] = stay.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOutConf] = stay.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.confRooms] = stay.numberOfRoooms
        data[PIAnalytics.Keys.rateCode] = stay.rateClassification
        data[PIAnalytics.Keys.bookingID] = stay.identifier
        data[PIAnalytics.Keys.confHotelCode] = stay.hotelCode
        data[PIAnalytics.Keys.productString] = ";\(stay.hotelCode)"

        AnalyticsManager.shared.trackState(pageName, data: data)
    }

    var customAnalyticsParameters: PIDictionary? {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.checkInConf] = stay.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOutConf] = stay.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.confRooms] = stay.numberOfRoooms
        data[PIAnalytics.Keys.rateCode] = stay.rateClassification
        data[PIAnalytics.Keys.bookingID] = stay.identifier
        data[PIAnalytics.Keys.confHotelCode] = stay.hotelCode
        data[PIAnalytics.Keys.productString] = ";\(stay.hotelCode)"

        return data
    }
}

extension OneTimePasswordInteractor {
    func provisionAppleWalletKey(otpCode: String) async throws -> PKAddShareablePassConfiguration {
        guard let provisioningData = await self.fetchKeyProvisioningData(otpCode: otpCode),
              let provisioningCredentialIdentifier = provisioningData.provisioningCredentialIdentifier,
              let sharingInstanceIdentifier = provisioningData.sharingInstanceIdentifier else {
            throw PassProvisioningError.missingProvisioningData
        }

		self.provisioningCredentialIdentifier = provisioningCredentialIdentifier

        guard let image = walletKeyImage.cgImage else {
            throw PassProvisioningError.passCantBeAdded
        }

        // 1. Create a sharable pass metadata
        let passMetadata: PKShareablePassMetadata

        if let cardTemplateIdentifier = provisioningData.cardTemplateIdentifier,
           cardTemplateIdentifier.isNotEmpty {
            passMetadata = .init(
                provisioningCredentialIdentifier: provisioningCredentialIdentifier,
                sharingInstanceIdentifier: sharingInstanceIdentifier,
                cardTemplateIdentifier: cardTemplateIdentifier,
                preview: .init(
                    passThumbnail: image,
                    localizedDescription: PILocalizedString("digitalKeyDescription")
                )
            )
        } else if let cardConfigurationIdentifier = provisioningData.cardConfigurationIdentifier,
                  cardConfigurationIdentifier.isNotEmpty {
            passMetadata = .init(
                provisioningCredentialIdentifier: provisioningCredentialIdentifier,
                sharingInstanceIdentifier: sharingInstanceIdentifier,
                cardConfigurationIdentifier: cardConfigurationIdentifier,
                preview: .init(
                    passThumbnail: image,
                    localizedDescription: PILocalizedString("digitalKeyDescription")
                )
            )
        } else {
            throw PassProvisioningError.invalidProvisioningData
        }

        if let accountHash = provisioningData.accountHash {
            passMetadata.accountHash = accountHash
        }

        if let relyingPartyIdentifier = provisioningData.relyingPartyIdentifier {
            passMetadata.relyingPartyIdentifier = relyingPartyIdentifier
        }

        if let serverEnvironmentIdentifier = provisioningData.serverEnvironmentIdentifier {
            passMetadata.serverEnvironmentIdentifier = serverEnvironmentIdentifier
        }

        // 2. Create a pass configuration with the metadata
        let passConfiguration = try await PKAddShareablePassConfiguration.forPassMetadata([passMetadata], action: .add)

        guard await PKAddSecureElementPassViewController.canAddSecureElementPass(configuration: passConfiguration) else {
            throw PassProvisioningError.passCantBeAdded
        }
        return passConfiguration
    }
}
