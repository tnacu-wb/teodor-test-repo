//
//  AdditionalGuestFormInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

typealias AdditionalGuestFormContent = (title: String, submitTitle: String)
typealias AdditionalGuestFormTracking = (screenName: String, screenType: String)

protocol AdditionalGuestFormInteractorProtocol {
    var viewContent: AdditionalGuestFormContent { get }
    var guest: AdditionalGuest? { get }
    var additionalGuestFormTracking: AdditionalGuestFormTracking { get }

    func save(guest: AdditionalGuest)
}

protocol AdditionalGuestFormInteractorDelegate: AnyObject {
    func savedGuest(withMessage message: String)
    func savedGuestFailed(withError error: Error)
}

protocol AdditionalGuestsRequestsProtocol: AnyObject {
	func updateUserAdditionalGuests(
	    for user: User,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	)
}

extension RequestsManager: AdditionalGuestsRequestsProtocol {
}

class AdditionalGuestFormInteractor {
    var viewContent: AdditionalGuestFormContent {
        (
            index == nil ? PILocalizedString("additionalGuestFormScreenTitle", comment: "") : PILocalizedString(
                "additionalGuestFormUpdateScreenTitle",
                comment: ""
            ),
            index == nil ? PILocalizedString("additionalGuestFormSaveButtonTitle", comment: "") : PILocalizedString(
                "additionalGuestFormUpdateButtonTitle",
                comment: ""
            )
        )
    }

    private(set) var guest: AdditionalGuest?
    private var index: Int?

    var requestsManager: AdditionalGuestsRequestsProtocol = RequestsManager()

    weak var delegate: AdditionalGuestFormInteractorDelegate?

    init(guest: AdditionalGuest? = nil, index: Int? = nil) {
        self.guest = guest
        self.index = index
    }

    private func update(user: User) {
		let sensorData = AkamaiProtection.sensorData

		requestsManager.updateUserAdditionalGuests(for: user, sensorData: sensorData) { [weak self] (success, error) in
            if success {
                self?.delegate?.savedGuest(withMessage: "")
            } else if let error = error {
                self?.delegate?.savedGuestFailed(withError: error)
            }
        }
    }
}

extension AdditionalGuestFormInteractor: AdditionalGuestFormInteractorProtocol {
    var additionalGuestFormTracking: AdditionalGuestFormTracking {
        (
            PIAnalytics.StateNames.addRegularGuest,
            PIAnalytics.StateTypes.myPI
        )
    }

    func save(guest: AdditionalGuest) {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return }

        if let index = index {
            user.additionalGuests?[index] = guest

            update(user: user)
        } else {
            if user.additionalGuests != nil {
                user.additionalGuests?.insert(guest, at: 0)
            } else {
                user.additionalGuests = [guest]
            }

            update(user: user)
        }
    }
}
