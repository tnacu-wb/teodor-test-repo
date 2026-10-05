//
//  RequestsManager+DashboardDataProvider.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

private extension String {
    static let checkInAction = "CIOL"
    static let directionsAction = "DIRECTIONS"
    static let upsellsAction = "UPSELLS"
    static let bookingDetailsAction = "BOOKING_DETAILS"
}

class RequestsManagerDashboardDataProvider: DashboardDataProvider {
    private var updateTimer: Timer?

    let requestsManager: DashboardRequestsProvider
    let recentSearchesManager = RecentSearchesManager(dataSource: UserDefaults.standard)

    var delegate: DashboardDataProviderDelegate?
    var hasRecentSearches: Bool
    var refreshInterval: TimeInterval? {
        didSet {
            resetRefreshTimer()
        }
    }
    var upcomingBooking: UpcomingBooking?
    var dashboardUpcomingBooking: DashboardUpcomingBooking? {
        didSet {
            self.delegate?.finishedRefreshingData()
        }
    }
    var frequentlyBooked: FrequentlyBooked?
    var dashboardFrequentlyBooked: [FrequentBookingViewModel]? {
        didSet {
            self.delegate?.finishedRefreshingData()
        }
    }
    var dashboardRecentSearches: Bool?
    var dashboardPastSearches: Bool?
    var searches: [RecentSearch]? {
        recentSearchesManager.selectedResults
    }
    var searchesComponentType: SearchesComponentType {
        recentSearchesManager.searchesComponentType
    }

    var destinationHeading: String?
    var destinationCards: [DestinationCardViewModel]?
    var contentCards: [ContentCardViewModel]?
    var promoCards: [PromoCardViewModel]?
    var notification: NotificationViewModel?

    private let dispatchGroup = DispatchGroup()

    init(with stay: Stay? = nil, and refreshInterval: TimeInterval? = nil, provider: DashboardRequestsProvider? = nil) {
        self.requestsManager = provider ?? RequestsManager()
        self.refreshInterval = refreshInterval
        self.hasRecentSearches = recentSearchesManager.hasRecentSearches

        NotificationCenter.default.addObserver(self, selector: #selector(update), name: .staysDidChange, object: nil)

        refreshDashboardData(with: stay)
    }

    deinit {
        updateTimer?.invalidate()
        NotificationCenter.default.removeObserver(self, name: .staysDidChange, object: nil)
    }

    private func resetRefreshTimer() {
        updateTimer?.invalidate()
        guard let refreshInterval = refreshInterval else { return }

        let date = Date().addingTimeInterval(refreshInterval)
        updateTimer = Timer(fire: date, interval: 0, repeats: false, block: { timer in
            guard timer == self.updateTimer else { return }
            self.update()
        })
        guard let timer = updateTimer else { return }
        RunLoop.current.add(timer, forMode: .common)
    }

    @objc private func update() {
        hasRecentSearches = recentSearchesManager.hasRecentSearches

        let simpleStorageManager = SimpleStorageManager<Stay>(dataSource: .standard)
        let stay = simpleStorageManager.items.upcomingStays.first
        refreshDashboardData(with: stay)
    }

    private func createPromoCardViewModel(promoCard: AppsCard) -> PromoCardViewModel {
        PromoCardViewModel(
            imageName: promoCard.imagePath,
            title: promoCard.title,
            description: promoCard.subtitle,
            tag: promoCard.imageTag,
            url: URL(string: promoCard.linkPath ?? ""),
            openLinkInApp: promoCard.openLinkInApp,
            analyticsKey: promoCard.trackingId,
            order: promoCard.order
        )
    }

    private func createContentCardViewModel(contentCard: AppsContentCard) -> ContentCardViewModel {
        ContentCardViewModel(
            imageName: contentCard.imagePath,
            title: contentCard.title,
            description: contentCard.subtitle,
            tag: contentCard.imageTag,
            url: URL(string: contentCard.linkPath ?? ""),
            openLinkInApp: contentCard.openLinkInApp,
            analyticsKey: contentCard.trackingId,
            order: contentCard.order
        )
    }

    private func createDestinationCardViewModel(destinationCard: AppsDestinationCard) -> DestinationCardViewModel {
        DestinationCardViewModel(
            imageName: destinationCard.imagePath,
            title: destinationCard.title,
            description: destinationCard.subtitle,
            tag: destinationCard.imageTag,
            url: URL(string: destinationCard.linkPath ?? ""),
            openLinkInApp: destinationCard.openLinkInApp,
            analyticsKey: destinationCard.trackingId,
            order: destinationCard.order,
            latitude: destinationCard.latitude,
            longitude: destinationCard.longitude
        )
    }

    func refreshDashboardData(with stay: Stay?) {
        resetRefreshTimer()

        self.hasRecentSearches = recentSearchesManager.hasRecentSearches

        dispatchGroup.enter()

        self.requestsManager.getHomepageContent(
            channel: BookingDetails.sharedInstance.bookingChannel,
            subchannel: Constants.homepageSubchannel,
            language: LanguageManager.supportedLanguage.rawValue,
            country: LanguageManager.supportedLanguage.countryCode
        ) { content, _ in
            self.destinationHeading = content?.heading

            self.destinationCards = content?.destinationCards?
                .map { self.createDestinationCardViewModel(destinationCard: $0) }
                .sorted { $0.order < $1.order }

            self.contentCards = content?.contentCards?
                .map { self.createContentCardViewModel(contentCard: $0) }
                .sorted { $0.order < $1.order }

            self.promoCards = content?.promoCards?
                .map { self.createPromoCardViewModel(promoCard: $0) }
                .sorted { $0.order < $1.order }

            self.notification = self.createNotificationViewModel(content?.notification)

            self.dispatchGroup.leave()
        }

        dispatchGroup.enter()

        self.requestsManager.getDashboardComponents(
            stay: stay,
            recentSearchesFlag: hasRecentSearches
        ) { (dashboardComponents: [DashboardComponent]?, error) in
            defer {
                self.dispatchGroup.leave()
            }

            self.dashboardUpcomingBooking = nil
            self.dashboardFrequentlyBooked = nil

            if let error = error {
                // If the dashboard call fails still show the recent searches also clear the old upcoming booking data if the user has just logged out
                self.dashboardPastSearches = true
                self.delegate?.finishedRefreshingData()
                return print(error)
            }

            guard let dashboardComponents = dashboardComponents else {
                self.dashboardPastSearches = true
                self.delegate?.finishedRefreshingData()
                return
            }

            self.dashboardRecentSearches = false
            self.dashboardPastSearches = false

            for component in dashboardComponents {
                switch component {
                case .upcomingBooking(let upcomingBooking):
                    self.loadUpcomingBooking(upcomingBooking, stay: stay)
                case .frequentlyBooked(let frequentlyBooked):
                    self.loadFrequentlyBooked(frequentlyBooked, stay: stay)
                case .recentSearches:
                    self.dashboardRecentSearches = true
                    self.delegate?.finishedRefreshingData()
                case .pastSearches:
                    self.dashboardPastSearches = true
                    self.delegate?.finishedRefreshingData()
                }
            }
        }

        dispatchGroup.notify(queue: .main) {
            self.delegate?.finishedRefreshingData()
        }
    }

    private func loadUpcomingBooking(_ upcomingBooking: UpcomingBooking, stay: Stay?) {
        self.upcomingBooking = upcomingBooking

        guard let hotelName = upcomingBooking.hotelName,
              let arrivalDate = upcomingBooking.arrivalDate,
              let departureDate = upcomingBooking.departureDate else {
                self.upcomingBooking = nil
                self.delegate?.finishedRefreshingData()
                return
        }

        let imageUrl: URL? = {
            guard let hotelImage = upcomingBooking.hotelImage else { return nil }
            return URL(string: hotelImage)
        }()

        let distinctRoomTypes = Set(upcomingBooking.rooms.compactMap { $0.type })
        let roomSuffix = upcomingBooking.rooms.count == 1 ? "room" : "rooms"
        let actions: [DashboardUpcomingBookingAction] = {
            guard let actions = upcomingBooking.actions else { return [] }
            return actions.compactMap {
                guard let actionType = DashboardUpcomingBookingActionType(rawValue: $0.type) else { return nil }
                return DashboardUpcomingBookingAction(type: actionType, title: $0.title)
            }
        }()
        guard let stay = stay else { return }

        self.dashboardUpcomingBooking = DashboardUpcomingBooking(
            identifier: stay.identifier,
            hotelCode: stay.hotelCode,
            imageUrl: imageUrl,
            hotel: hotelName,
            arrivalDate: arrivalDate,
            checkOutDate: departureDate,
            guestsNumber: upcomingBooking.guests,
            roomNumber: upcomingBooking.rooms.count,
            roomType: distinctRoomTypes
            .count == 1 ? PILocalizedString((distinctRoomTypes.first?.lowercased() ?? "") + " \(roomSuffix)") :
            PILocalizedString(roomSuffix),
            isCheckedIn: upcomingBooking.checkedIn,
            isBusinessTrip: stay.isBusinessTrip,
            actions: actions
        )
    }

    private func loadFrequentlyBooked(_ frequentlyBooked: FrequentlyBooked, stay: Stay?) {
        self.frequentlyBooked = frequentlyBooked

        self.dashboardFrequentlyBooked = frequentlyBooked.frequentBookings?.compactMap {
            guard let name = $0.hotelName,
                  let image = $0.hotelImage else { return nil }
            let code = $0.hotelCode
            let brand = $0.hotelBrand

            return FrequentBookingViewModel(name: name, code: code, image: image, brand: brand)
        }
    }

    func createNotificationViewModel(_ notification: NotificationBanner?) -> NotificationViewModel? {
        guard let notification = notification,
              let type = NotificationStyle(rawValue: notification.type) else { return nil }

        let url: URL? = {
            guard let linkPath = notification.linkPath,
                  let linkPathURL = URL(string: linkPath) else { return nil }

            return linkPathURL
        }()

        let message = {
            let attributedString = NSMutableAttributedString(string: "")

            if let title = notification.title {
                let titleString = NSMutableAttributedString(
                    string: title,
                    attributes: [.font: UIFont.BodySmall_Semibold()]
                )

                attributedString.append(titleString)
                attributedString.append(NSAttributedString(string: "\n"))
            }

            attributedString.append(NSMutableAttributedString(
                string: notification.message,
                attributes: [.font: UIFont.BodySmall()]
            ))

            // make sure both link label and path are provided and it's a valid URL
            if let linkLabel = notification.linkLabel, url != nil {
                let linkLabel = NSAttributedString(
                    string: linkLabel,
                    attributes: [
                        .font: UIFont.Link(),
                        .underlineStyle: 1
                    ]
                )

                attributedString.append(NSAttributedString(string: " "))
                attributedString.append(linkLabel)
            }

            return attributedString
        }()

        return NotificationViewModel(
            style: type,
            message: message,
            link: url,
            openLinkInApp: notification.openLinkInApp ?? false,
            dismissible: notification.dismissible ?? false
        )
    }
}
