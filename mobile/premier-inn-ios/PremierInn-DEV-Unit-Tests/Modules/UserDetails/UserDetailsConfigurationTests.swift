//
//  UserDetailsConfigurationTests.swift
//  PremierInn
//
//  Created by Clint Mengolli on 07/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

final class UserDetailsConfigurationTests: XCTestCase {

    // MARK: - Initialisation Tests

    func test_init_whenScopeIsAmendFlow_marketingModelIsNil() {
        let mockMarketing = UserMarketingModel(
            description: NSAttributedString(string: "Mock description"),
            isActive: false
        )
        let sut = createMockConfig(scope: .amendFlow, marketingModel: mockMarketing)
        
        XCTAssertNil(sut.marketingModel)
    }

    func test_init_whenScopeIsUserPreferences_tripPurposeModelIsNil() {
        let mockTripPurpose = TripPurposeMessagesModel(
            leisureMessages: nil,
            businessMessages: nil
        )
        let sut = createMockConfig(scope: .userPreferences, tripPurposeModel: mockTripPurpose)

        XCTAssertNil(sut.tripPurposeModel, "Trip purpose should be nil for user preferences")
    }

    // MARK: - Visibility Logic Tests

    func test_visibilityFlags_forUserPreferencesScope() {
        let userPreferencesFlow = createMockConfig(scope: .userPreferences)

        XCTAssertFalse(userPreferencesFlow.shouldShowBookerStayer)
        XCTAssertFalse(userPreferencesFlow.shouldShowPurpose)
        XCTAssertTrue(userPreferencesFlow.shouldShowCountry)
        XCTAssertTrue(userPreferencesFlow.shouldShowCarSection)
        XCTAssertTrue(userPreferencesFlow.shouldShowDeleteAccount)
    }

    func test_visibilityFlags_forBookingFlowScope() {
        let sut = createMockConfig(scope: .bookingFlow)
        
        XCTAssertTrue(sut.shouldShowBookerStayer)
        XCTAssertTrue(sut.shouldShowPurpose)
        XCTAssertFalse(sut.shouldShowCountry)
        XCTAssertFalse(sut.shouldShowCarSection)
        XCTAssertFalse(sut.shouldShowDeleteAccount)
    }

    // MARK: - UI Element Tests

    func test_submitButtonBackgroundColor_variesByScope() {
        let bookingFlow = createMockConfig(scope: .bookingFlow)
        let userPreferencesFlow = createMockConfig(scope: .userPreferences)

        XCTAssertEqual(bookingFlow.submitButtonBackgroundColor, .Tint1)
        XCTAssertEqual(userPreferencesFlow.submitButtonBackgroundColor, .BasePurple)
    }

    func test_submitButtonTitle_returnsCorrectLocalizedStringKeys() {
        let bookingFlow = createMockConfig(scope: .bookingFlow)
        let bookingFlowEditing = createMockConfig(scope: .bookingFlowEditing)
        let userPreferencesFlow = createMockConfig(scope: .userPreferences)

        XCTAssertEqual(
            bookingFlow.submitButtonTitle,
            PILocalizedString("userDetailsSubmitButtonContinue")
        )

        XCTAssertEqual(
            bookingFlowEditing.submitButtonTitle,
            PILocalizedString("userDetailsSubmitButtonUpdate")
        )

        XCTAssertEqual(
            userPreferencesFlow.submitButtonTitle,
            PILocalizedString("userPreferenceRoomRequirementsSaveButtonTitle")
        )
    }
}

private extension UserDetailsConfigurationTests {
    func createMockConfig(
        scope: UserDetailScope = .bookingFlow,
        user: User? = nil,
        roomConfigs: [RoomConfig] = [],
        marketingModel: UserMarketingModel? = nil,
        tripPurposeModel: TripPurposeMessagesModel? = nil,
        shouldShowCreateAccount: Bool = false
    ) -> UserDetailsConfiguration {
        return UserDetailsConfiguration(
            scope: scope,
            user: user,
            roomConfigurations: roomConfigs,
            marketingModel: marketingModel,
            tripPurposeModel: tripPurposeModel
        )
    }
}
