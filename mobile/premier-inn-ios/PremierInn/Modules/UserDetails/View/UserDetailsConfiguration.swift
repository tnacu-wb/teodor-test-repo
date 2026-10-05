//
//  UserDetailsConfiguration.swift
//  PremierInn
//
//  Created by Clint Mengolli on 30/12/2025.
//  Copyright © 2025 Whitbread. All rights reserved.

import SimpleNetwork
import UIKit

struct UserDetailsConfiguration {
    private enum Constants {
        static let headerTitle = "userDetailsScreenTitle"
        static let userDetailsSubmitButtonContinue = "userDetailsSubmitButtonContinue"
        static let userDetailsSubmitButtonUpdate = "userDetailsSubmitButtonUpdate"
        static let userPreferenceRoomRequirementsSaveButtonTitle = "userPreferenceRoomRequirementsSaveButtonTitle"
    }

    let scope: UserDetailScope
    let user: User?
    let roomConfigurations: [RoomConfig]
    var marketingModel: UserMarketingModel?
    let tripPurposeModel: TripPurposeMessagesModel?

    init(
        scope: UserDetailScope,
        user: User?,
        roomConfigurations: [RoomConfig],
        marketingModel: UserMarketingModel?,
        tripPurposeModel: TripPurposeMessagesModel?,
    ) {
        self.scope = scope
        self.user = user

        self.marketingModel = scope == .amendFlow ? nil : marketingModel
        self.tripPurposeModel = scope != .userPreferences ? tripPurposeModel : nil

        self.roomConfigurations = roomConfigurations
    }

    var headerTitle: String {
        PILocalizedString(Constants.headerTitle, comment: "User details form: screen title")
    }

    var shouldShowBookerStayer: Bool {
        scope != .userPreferences
    }

    var shouldShowPurpose: Bool {
        scope != .userPreferences
    }

    var shouldShowCountry: Bool {
        scope == .userPreferences
    }

    var shouldShowCarSection: Bool {
        scope == .userPreferences
    }

    var shouldShowDeleteAccount: Bool {
        scope == .userPreferences
    }

    var submitButtonForegroundColor: UIColor {
        .BaseWhite
    }

    var shouldShowLoginSection: Bool {
        switch scope {
        case .bookingFlow:
            return UserSessionManager.sharedInstance.currentUser == nil
        default:
            return false
        }
    }

    var submitButtonBackgroundColor: UIColor {
        scope == .bookingFlow ? .Tint1 : .BasePurple
    }

    var submitButtonTitle: String {
        scopeStrings[scope] ?? ""
    }

    private var scopeStrings: [UserDetailScope: String] = [
        .amendFlow: "",
        .bookingFlow: PILocalizedString(Constants.userDetailsSubmitButtonContinue),
        .bookingFlowEditing: PILocalizedString(Constants.userDetailsSubmitButtonUpdate),
        .userPreferences: PILocalizedString(Constants.userPreferenceRoomRequirementsSaveButtonTitle)
    ]
}
