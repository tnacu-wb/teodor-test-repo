//
//  UserSessionManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 28/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public protocol UserSessionManagerProtocol {
    var currentUser: User? { get }
}

public final class UserSessionManager: UserSessionManagerProtocol {
    public static let sharedInstance: UserSessionManager = UserSessionManager()
    public private(set) var currentUser: User?


    private let requestsManager = RequestsManager()

    var idToken: String?
    var operaCompanyId: String?

    public func loggedIn(with user: User, marketingPreferenceCompletion: (() -> Void)? = nil) {
        currentUser = user

        if BookingDetails.sharedInstance.booker == nil {
            if let user = user.copy() as? User {
                BookingDetails.sharedInstance.booker = user
            }
        }


        // OMEGA_HACK
        if let roomRequirements = user.bookingPreference?.roomRequirements,
           let roomType = roomRequirements.type {
            let availableTypes = roomType.availableTypes(
                adults: roomRequirements.adults,
                children: roomRequirements.children,
                cot: roomRequirements.cotRequired
            )

            if availableTypes.contains(roomType) == false {
                BookingDetails.sharedInstance.booker?.bookingPreference?.roomRequirements?.type = availableTypes
                    .first ?? .double
            }
        }

        if BookingDetails.sharedInstance.meal == nil {
            // don't change any criteria during the booking flow
            if BookingDetails.sharedInstance.isBookingHold == false {
                if let room = user.bookingPreference?.roomRequirements?.room {
                    if BookingDetails.sharedInstance.criteria.rooms.isEmpty == false {
                        BookingDetails.sharedInstance.criteria.rooms[0] = room
                    }
                }
            }

            if user.isBusiness && BookingDetails.sharedInstance.criteria.rooms.count > 1 {
                BookingDetails.sharedInstance.criteria.rooms = [BookingDetails.sharedInstance.criteria.rooms[0]]
            }
        }

        if BookingDetails.sharedInstance.purpose == nil {
            BookingDetails.sharedInstance.purpose = user.isBusiness ? .business : .leisure
        }

        updateMarketingPreference {
            NotificationCenter.default.post(name: .marketingPreferencesDidChange, object: nil)
            marketingPreferenceCompletion?()
        }

        NotificationCenter.default.post(name: .userDidChange, object: nil)
    }

    private func updateMarketingPreference(_ completion: @escaping () -> Void) {
        guard let emailAddress = currentUser?.emailAddress else {
            completion()
            return
        }

        requestsManager.getMarketingPreferences(
            for: emailAddress,
            and: .premierInn,
            isBusiness: currentUser?.isBusiness ?? false
        ) { marketingPrefs, _ in
            if let marketingPreferences = marketingPrefs {
                self.currentUser?.marketingPreferences = marketingPreferences
            }
            completion()
        }
    }

    private var cccPaymentMethodTokenRetryCount = 0
    private let cccPaymentMethodTokenMaxRetriesAmount = 2


    public func userLoggedOut() {
        currentUser = nil

        requestsManager.logout()

        BookingDetails.sharedInstance.booker = nil
        BookingDetails.sharedInstance.reset()

        NotificationCenter.default.post(name: .userDidChange, object: nil)
    }

    func refreshUser(completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {
        let isBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)
        let usernameKey: String = { isBusiness ? .storedBusinessUsernameKey : .storedUsernameKey }()
        guard let emailAddress = UserDefaults.standard.string(forKey: usernameKey) else { completion(
            false,
            RefreshUserError.missingStoredUsername
        )
return }
        guard let credentials = User.storedCredentials(for: emailAddress, business: isBusiness) else { completion(
            false,
            RefreshUserError.missingStoredCredentials
        )
return }

        requestsManager
            .login(withUsername: credentials.username, password: credentials.password, isBusiness: isBusiness) { result in
            switch result {
            case .success:
                User.saveCredentials(credentials)
                completion(true, nil)

            case .failure:
                completion(false, RefreshUserError.loginError)
            }
        }
    }
}

enum RefreshUserError: LocalizedError {
    case missingBusiness
    case missingStoredUsername
    case missingStoredCredentials
    case loginError

    var errorDescription: String? { String(describing: self)    }
}

extension String {
    static let storedUsernameKey = "storedUsername"
    static let storedBusinessUsernameKey = "storedBusinessUsername"
    static let storedBusinessKey = "storedBusiness"
}
