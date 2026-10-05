//
//  MessagingInteractor.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 22/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import SimpleNetwork
import CoreLocation
import MapKit

class KeyMessagingInteractor: KeyMessagingInteractorInputProtocol {
    weak var presenter: KeyMessagingInteractorOutputProtocol?

    var messagingFlow: RoomAllocationState
    var stay: Stay
    private var analyticsManager = AnalyticsManager.shared
    private struct ViewModel: KeyMessagingViewModel {
        var title: String
        var messaging: String
        var shouldShowDirectionsButton: Bool
    }

    var viewModel: KeyMessagingViewModel {
        ViewModel(
            title: self.messagingFlow.titleLabel ?? "",
            messaging: messagingDescription,
            shouldShowDirectionsButton: self.messagingFlow == .userIsNotInRange
        )
    }

    private var messagingDescription: String {
        guard self.messagingFlow != .userIsNotInRange else { return self.messagingFlow.descriptionLabel ?? "" }
        let checkInHourLocalised = stay.isEarlyCheckInForAllRooms ? PILocalizedString("11am") : PILocalizedString("3pm")
        return String(format: messagingFlow.descriptionLabel ?? "", checkInHourLocalised)
    }

    init(messagingFlow: RoomAllocationState, stay: Stay) {
        self.messagingFlow = messagingFlow
        self.stay = stay
    }

    func trackStateAnalytics() {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.digitalKeyRoomAllocationMessaging] = messagingFlow.titleLabel
        data[PIAnalytics.Keys.rooms] = stay.numberOfRoooms
        data[PIAnalytics.Keys.checkIn] = stay.arrivalDate?.analyticsDateFormat
        data[PIAnalytics.Keys.checkOut] = stay.checkOutDate?.analyticsDateFormat
        data[PIAnalytics.Keys.rateCode] = stay.rateClassification
        data[PIAnalytics.Keys.bookingID] = stay.identifier

        // TODO: Just commenting out to make compiler happy. We will remove this whole file soon.
//        analyticsManager.trackState(messagingFlow.analyticsPageName ?? "", data: data)
    }
}

extension Stay: DirectionsAlertConfig {
    var placeMark: MKPlacemark? {
        MKPlacemark(coordinate: CLLocationCoordinate2D(latitude: self.hotelLatitude, longitude: self.hotelLongitude))
    }

    var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: self.hotelLatitude, longitude: self.hotelLongitude)
    }
}
