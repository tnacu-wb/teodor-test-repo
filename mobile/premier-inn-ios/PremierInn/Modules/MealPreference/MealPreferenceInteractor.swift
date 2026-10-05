//
//  MealPreferenceInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

private enum MealPreferenceSections {
    case info
    case meals
    case saveButton
}

class MealOptionRowModel {
    public let option: MealOption
    public let title: String
    public let subTitle: String?
    public var selected: Bool

    public init(option: MealOption, title: String, subTitle: String?, selected: Bool) {
        self.option = option
        self.title = title
        self.subTitle = subTitle
        self.selected = selected
    }
}

typealias MealPreferenceTracking = (screenName: String, screenType: String)

protocol MealPreferenceInteractorInput {
    var mealPreferenceTracking: MealPreferenceTracking { get }

    func numberOfRows(forSection section: Int) -> Int
    func numberOfSections() -> Int
    func allMealOptions() -> [MealOptionRowModel]
    func selectMealOption(option: MealOption)
    func saveChanges()
}

protocol MealPreferenceInteractorOutput {
    func mealPreferenceUpdated()
    func mealPreferenceUpdateFailed(with error: Error?)
}

class MealPreferenceInteractor {
    private var mealOptions: [MealOptionRowModel] = [
        MealOption.premierInnBreakfast.mealOptionRowModel,
        MealOption.continentalBreakfast.mealOptionRowModel,
        MealOption.mealDeal.mealOptionRowModel,
        MealOption.none.mealOptionRowModel
    ]
    private var requestsManager = RequestsManager()

    private var selectedMealOption: MealOption = .none

    private let sections: [MealPreferenceSections] = [
        .info,
        .meals,
        .saveButton
    ]

    var output: MealPreferenceInteractorOutput?

    init() {
        if let foodPreference = UserSessionManager.sharedInstance.currentUser?.bookingPreference?.foodPreference {
            setActivePreference(with: foodPreference)
        }
    }

    private func setActivePreference(with option: MealOption) {
        (mealOptions.first { $0.option == option })?.selected = true
        selectedMealOption = option
    }

    private func updateUser(withFoodPreference mealOption: MealOption, shouldAttemptLogin: Bool = true) {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return }

        user.bookingPreference?.foodPreference = mealOption

        UserSessionManager.sharedInstance.refreshUser { success, _ in
            guard success else {
                self.output?.mealPreferenceUpdateFailed(with: nil)
                return
            }

			let sensorData = AkamaiProtection.sensorData

			self.requestsManager.updateFoodPreference(for: user, sensorData: sensorData) { (success, error) in
                if error?.isSessionExpired == true && shouldAttemptLogin {
                    return self.requestsManager.autoLogin { _ in
                        self.updateUser(withFoodPreference: mealOption, shouldAttemptLogin: false)
                    }
                }

                if success && error == nil {
                    self.requestsManager.getUser(userId: user.emailAddress ?? "", isBusiness: user.isBusiness) { result in
                        DispatchQueue.main.async {
                            _ = result.handle()

                            BookingDetails.sharedInstance.booker?.bookingPreference?.foodPreference = mealOption

                            self.requestsManager.refreshStays(
                                for: UserSessionManager.sharedInstance.currentUser,
                                shouldAttemptLogin: false
                            ) { _ in }

                            self.output?.mealPreferenceUpdated()
                        }
                    }
                } else {
                    self.output?.mealPreferenceUpdateFailed(with: error)
                }
            }
        }
    }
}

extension MealPreferenceInteractor: MealPreferenceInteractorInput {
    var mealPreferenceTracking: MealPreferenceTracking {
        (
            PIAnalytics.StateNames.mealPrefs,
            PIAnalytics.StateTypes.myPI
        )
    }

    func numberOfRows(forSection section: Int) -> Int {
        1
    }

    func numberOfSections() -> Int {
        sections.count
    }

    func allMealOptions() -> [MealOptionRowModel] {
        mealOptions
    }

    func selectMealOption(option: MealOption) {
        mealOptions.forEach { $0.selected = false }
        setActivePreference(with: option)
    }

    func saveChanges() {
        updateUser(withFoodPreference: selectedMealOption)
    }
}
