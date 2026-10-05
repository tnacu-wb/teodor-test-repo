//
//  RequestsManager+DashboardDataProviderTests.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 26/03/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Testing
import SimpleNetwork
@testable import PremierInn
import Foundation

@Suite("DataProvider Dashboard+HomepageContent tests") struct RequestsManagerDashboardDataProviderTests {

    private let dataProvider = RequestsManagerDashboardDataProvider(with: nil, and: nil)

    @Test func testHomepageContentNoNotification() {

        // nil object
        let viewModel = dataProvider.createNotificationViewModel(nil)
        #expect(viewModel == nil)
    }

    @Test func testHomepageContentNotificationEmptyDictionary() {
        // empty not nil dictionary
        let dictionary: PIDictionary = [
            "": ""
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel == nil)
    }

    @Test func testHomepageContentNotificationMissingMessage() {
        // missing message
        let dictionary: PIDictionary = [
            "type": "info"
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel == nil)
    }

    @Test func testHomepageContentNotificationMissingType() {
        // missing type
        let dictionary: PIDictionary = [
            "message": "text"
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel == nil)
    }

    @Test func testHomepageContentNotificationInvalidStyle() {
        // mandatory fields only
        let dictionary: PIDictionary = [
            "type": "inf",
            "message": "text"
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel == nil)
    }

    @Test func testHomepageContentNotificationMandatoryFieldsOnly() {
        // mandatory fields only
        let dictionary: PIDictionary = [
            "type": "info",
            "message": "text"
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel != nil)
        #expect(viewModel?.style == .info)
        #expect(viewModel?.message.string == "text")
        #expect(viewModel?.link == nil)
        #expect(viewModel?.openLinkInApp == false)
        #expect(viewModel?.dismissible == false)
    }

    @Test func testHomepageContentNotificationLinkPathMissing() {
        // missing link path so link will be missing
        let dictionary: PIDictionary = [
            "type": "alert",
            "message": "text",
            "linkLabel": "link",
            "openLinkInApp": true,
            "dismissible": true
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel != nil)
        #expect(viewModel?.style == .alert)
        #expect(viewModel?.message.string == "text")
        #expect(viewModel?.link == nil)
        #expect(viewModel?.openLinkInApp == true)
        #expect(viewModel?.dismissible == true)
    }

    @Test func testHomepageContentNotificationAllFieldsProvided() {
        // all fields provided
        let dictionary: PIDictionary = [
            "type": "error",
            "title": "title",
            "message": "text",
            "linkLabel": "link",
            "linkPath": "https://www.premierinn.com",
            "openLinkInApp": true,
            "dismissible": true
        ]

        let notificationBanner = NotificationBanner(dictionary: dictionary)
        let viewModel = dataProvider.createNotificationViewModel(notificationBanner)
        #expect(viewModel != nil)
        #expect(viewModel?.style == .error)
        #expect(viewModel?.message.string == "title\ntext link")
        #expect(viewModel?.link == URL(string: "https://www.premierinn.com"))
        #expect(viewModel?.openLinkInApp == true)
        #expect(viewModel?.dismissible == true)
    }
}

fileprivate extension NotificationBanner {

    private enum NotificationBannerError: LocalizedError {

        case missingMandatoryKey

        var errorDescription: String? { return String(describing: self)    }
    }

    init?(dictionary: PIDictionary) {

        let data = try! JSONSerialization.data(withJSONObject: dictionary)

        do {
            self = try JSONDecoder().decode(NotificationBanner.self, from: data)

        } catch {
            return nil
        }
    }
}
