//
//  RoomRequirementsInteractor.swift
//  PremierInn
//
//  Created by Nick Jones on 01/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

typealias RoomRequirementsTracking = (screenName: String, screenType: String)

protocol RoomRequirementsInteractorInput {
    var roomRequirementsTracking: RoomRequirementsTracking { get }

    func saveChanges(shouldAttemptLogin: Bool, completion: @escaping (Result<Bool>) -> Void)

    var model: RoomRequirementsModel { get }
    var currentNumberOfAdults: Int { get set }
    var currentNumberOfChildren: Int { get set }
    var cotCurrentlyRequired: Bool { get set }
    var currentRoomType: RoomType { get set }
    var availableTypes: [RoomType] { get }
}

struct RoomRequirementsModel {
    var adults: Int
    var children: Int
    var shouldIncludeCot: Bool
    var roomType: RoomType
}

class RoomRequirementsInteractor {
    private let requestsManager = RequestsManager()
    var presenter: RoomRequirementsPresenterInput?

    var model: RoomRequirementsModel {
        didSet {
            if model.children > 0 {
                model.roomType = .family
            } else {
                if (model.shouldIncludeCot || model.roomType == .family) && model.children == 0 {
                    model.roomType = .double
                }
            }
        }
    }

    init(model: RoomRequirementsModel) {
        self.model = model
    }
}

extension RoomRequirementsInteractor: RoomRequirementsInteractorInput {
    var availableTypes: [RoomType] {
        var availableTypes = model.roomType.availableTypes(
            adults: model.adults,
            children: model.children,
            cot: model.shouldIncludeCot
        )

        if model.adults > 0 && model.children == 0 && model.shouldIncludeCot {
            availableTypes.removeAll(where: { $0 == .accessible })
        }

        return availableTypes
    }

    var roomRequirementsTracking: RoomRequirementsTracking {
        (
            PIAnalytics.StateNames.roomPrefs,
            PIAnalytics.StateTypes.myPI
        )
    }

    func saveChanges(shouldAttemptLogin: Bool, completion: @escaping (Result<Bool>) -> Void) {
        UserSessionManager.sharedInstance.refreshUser { success, _ in
            guard success else { return completion(.failure(error: UserSessionError.sessionExpired)) }

            guard let user = UserSessionManager.sharedInstance.currentUser else { return }

			user.bookingPreference?.roomRequirements?.adults = self.model.adults
            user.bookingPreference?.roomRequirements?.children = self.model.children
            user.bookingPreference?.roomRequirements?.cotRequired = self.model.shouldIncludeCot
            user.bookingPreference?.roomRequirements?.type = self.model.roomType

			let sensorData = AkamaiProtection.sensorData

			self.requestsManager.updateRoomPreference(for: user, sensorData: sensorData) { (success, error) in
                if error?.isSessionExpired == true && shouldAttemptLogin {
                    return self.requestsManager.autoLogin { _ in
                        self.saveChanges(shouldAttemptLogin: false, completion: completion)
                    }
                }

                if let error = error {
                    return completion(.failure(error: error))
                }

                self.requestsManager.getUser(userId: user.emailAddress ?? "", isBusiness: user.isBusiness) { result in
                    DispatchQueue.main.async {
                        _ = result.handle()

                        if let room = user.bookingPreference?.roomRequirements?.room {
                            BookingDetails.sharedInstance.criteria.rooms = [room]
                        }

                        self.requestsManager.refreshStays(
                            for: UserSessionManager.sharedInstance.currentUser,
                            shouldAttemptLogin: false
                        ) { _ in }

                        completion(.success(result: success))
                    }
                }
            }
        }
    }

    var currentNumberOfAdults: Int {
        get {
            model.adults
        } set {
            model.adults = newValue
        }
    }

    var currentNumberOfChildren: Int {
        get {
            model.children
        } set {
            model.children = newValue
        }
    }

    var currentRoomType: RoomType {
        get {
            model.roomType
        } set {
            model.roomType = newValue
        }
    }

    var cotCurrentlyRequired: Bool {
        get {
            model.shouldIncludeCot
        } set {
            model.shouldIncludeCot = newValue
        }
    }
}
