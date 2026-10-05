//
//  UserPreferencesInteractor.swift
//  PremierInn
//
//  Created by Nick Jones on 30/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

enum UserPreferenceType {
    case title
    case room
    case meal
    case wifi
}

enum UserPreferenceRowType: String {
    case title = "FlexibleTextContentCell"
    case generic = "GenericPreferencesCell"
    case room = "StayPreferencesCell"
}

class UserPreferenceRow {
    let cellIdentifier: UserPreferenceRowType
    let title: String?
    let subTitle: String?
    let buttonTitle: String
    let userPreferenceType: UserPreferenceType

    init(
        withCellIdentifier cellIdentifier: UserPreferenceRowType,
        userPreferenceType: UserPreferenceType,
        title: String?,
        subTitle: String?,
        buttonTitle: String
    ) {
        self.cellIdentifier = cellIdentifier
        self.title = title
        self.userPreferenceType = userPreferenceType
        self.subTitle = subTitle
        self.buttonTitle = buttonTitle
    }
}

class RoomRequirementsRow: UserPreferenceRow {
    let adults: Int
    let children: Int
    let room: Room

    init(room: Room, userPreferenceType: UserPreferenceType, title: String?, subTitle: String?, buttonTitle: String) {
        self.adults = room.adults
        self.children = room.children
        self.room = room

        super.init(
            withCellIdentifier: .room,
            userPreferenceType: userPreferenceType,
            title: title,
            subTitle: subTitle,
            buttonTitle: buttonTitle
        )
    }
}

extension UserPreferenceRow {
    static func row(forPreferenceType preferenceType: UserPreferenceType) -> UserPreferenceRow? {
        let currentUser = UserSessionManager.sharedInstance.currentUser

        switch preferenceType {
        case .title:
            let title = PILocalizedString("bookingPreferenceTitle", comment: "Booking preferences title")
            return UserPreferenceRow(
                withCellIdentifier: .title,
                userPreferenceType: preferenceType,
                title: title,
                subTitle: nil,
                buttonTitle: ""
            )

        case .room:
            guard let room = currentUser?.bookingPreference?.roomRequirements?.room else { return nil }

            let title = PILocalizedString("userPreferenceScreenStayPreferencesTitle", comment: "Stay preferences title")
            let subTitle = PILocalizedString(
                "userPreferenceScreenStayPreferencesSubTitle",
                comment: "Stay preferences sub title"
            )

            let buttonTitle = PILocalizedString("actionableEditButtonTitle", comment: "Change button title")
            return RoomRequirementsRow(
                room: room,
                userPreferenceType: preferenceType,
                title: title,
                subTitle: subTitle,
                buttonTitle: buttonTitle
            )

        case .meal:
            let title = PILocalizedString("userPreferenceScreenMealTitle", comment: "Meal Row Title")
            let subTitle = currentUser?.bookingPreference?.foodPreference?.title.replacingOccurrences(of: "\r", with: "")

            let buttonTitle = PILocalizedString("actionableEditButtonTitle", comment: "Change button title")
            return UserPreferenceRow(
                withCellIdentifier: .generic,
                userPreferenceType: preferenceType,
                title: title,
                subTitle: subTitle,
                buttonTitle: buttonTitle
            )

        case .wifi:
            let title = PILocalizedString("userPreferenceScreenWiFiTitle", comment: "WiFi Row Title")
            let subTitle = currentUser?.bookingPreference?.preselectWifi ?? true ? "Yes" : "None"

            let buttonTitle = PILocalizedString("actionableEditButtonTitle", comment: "Change button title")
            return UserPreferenceRow(
                withCellIdentifier: .generic,
                userPreferenceType: preferenceType,
                title: title,
                subTitle: subTitle,
                buttonTitle: buttonTitle
            )
        }
    }
}

typealias UserPreferencesTracking = (screenName: String, screenType: String)

protocol UserPreferencesInteractorInput {
    var numberOfRows: Int { get }
    var userPreferencesTracking: UserPreferencesTracking { get }
    var viewTitle: String { get }

    func userPreferenceRow(forPath path: IndexPath) -> UserPreferenceRow?
    func updateAllRows()
    func errorOccuredWhenUpdatingUserPreferences()
    func shouldShowUserPreferenceUpdateErrorRow() -> Bool
    func resetErrorOccuredWhenUpdatingUserPreferencesSettings()
    func currentUser() -> User?
}

class UserPreferencesInteractor {
    private lazy var titleRow = UserPreferenceRow.row(forPreferenceType: .title)
    private lazy var roomRequirementsRow = UserPreferenceRow.row(forPreferenceType: .room)
    private lazy var mealPreferenceRow = UserPreferenceRow.row(forPreferenceType: .meal)
    private lazy var wiFiPreferenceRow = UserPreferenceRow.row(forPreferenceType: .wifi)
    private lazy var userPreferenceRows = [titleRow, mealPreferenceRow, roomRequirementsRow]

    var shouldShowErrorMessage = false
}

extension UserPreferencesInteractor: UserPreferencesInteractorInput {
    var numberOfRows: Int { userPreferenceRows.count + (shouldShowErrorMessage ? 1 : 0) }
    var userPreferencesTracking: UserPreferencesTracking {
        (
            PIAnalytics.StateNames.bookingPrefs,
            PIAnalytics.StateTypes.myPI
        )
    }
    var viewTitle: String { PILocalizedString("userPreferenceScreenTitle", comment: "") }

    func updateAllRows() {
        titleRow = UserPreferenceRow.row(forPreferenceType: .title)
        roomRequirementsRow = UserPreferenceRow.row(forPreferenceType: .room)
        mealPreferenceRow = UserPreferenceRow.row(forPreferenceType: .meal)
        wiFiPreferenceRow = UserPreferenceRow.row(forPreferenceType: .wifi)

        userPreferenceRows = [titleRow, mealPreferenceRow, roomRequirementsRow]
    }

    func userPreferenceRow(forPath path: IndexPath) -> UserPreferenceRow? {
        let correctSectionWithPathAndErrorMessageSection = path.section - (shouldShowErrorMessage ? 1 : 0)
        guard correctSectionWithPathAndErrorMessageSection < numberOfRows else { return nil }

        return userPreferenceRows[safe: correctSectionWithPathAndErrorMessageSection] ?? nil
    }

    func errorOccuredWhenUpdatingUserPreferences() { shouldShowErrorMessage = true }

    func resetErrorOccuredWhenUpdatingUserPreferencesSettings() { shouldShowErrorMessage = false }

    func shouldShowUserPreferenceUpdateErrorRow() -> Bool { shouldShowErrorMessage }

    func currentUser() -> User? {
        UserSessionManager.sharedInstance.currentUser
    }
}
