//
//  AdditionalGuestsInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

typealias DeleteAlertContent = (title: String, message: String, cancel: String, confirm: String)
typealias AdditionalGuestsTracking = (screenName: String, screenType: String)

protocol AdditionalGuestsInteractorProtocol {
    var viewTitle: String { get }
    var additionalGuestsTracking: AdditionalGuestsTracking { get }
    var deleteAlertContent: DeleteAlertContent { get }
    var additionalGuests: [AdditionalGuest]? { get }
    var guestViewModels: [AdditionalGuestListViewModel]? { get }

    func deleteGuest(atIndex index: Int)
}

protocol AdditionalGuestsInteractorDelegate: AnyObject {
    func guestDeleted(withMessage message: String)
    func guestDeleteFailed(withError error: Error)
}

class AdditionalGuestsInteractor {
    weak var delegate: AdditionalGuestsInteractorDelegate?
    var viewTitle: String {
        PILocalizedString("additionalGuestsScreenTitle", comment: "")
    }
    var additionalGuestsTracking: AdditionalGuestsTracking {
        (
            PIAnalytics.StateNames.regularGuests,
            PIAnalytics.StateTypes.myPI
        )
    }
    var deleteAlertContent: DeleteAlertContent {
        (
            PILocalizedString("deleteGuestAlertTitle", comment: ""),
            PILocalizedString("deleteGuestAlertMessage", comment: ""),
            PILocalizedString("deleteGuestAlertCancel", comment: ""),
            PILocalizedString("deleteGuestAlertConfirm", comment: "")
        )
    }
    var additionalGuests: [AdditionalGuest]? {
        UserSessionManager.sharedInstance.currentUser?.additionalGuests
    }
    var guestViewModels: [AdditionalGuestListViewModel]? {
        guard let additionalGuests = additionalGuests else { return nil }
        return additionalGuests.map {
            AdditionalGuestListViewModel(fullName: "\($0.firstName) \($0.lastName)", email: $0.email ?? "")
        }
    }
	var requestsManager: AdditionalGuestsRequestsProtocol = RequestsManager()

    private func update(user: User) {
		let sensorData = AkamaiProtection.sensorData

		requestsManager.updateUserAdditionalGuests(for: user, sensorData: sensorData) { [weak self] success, error in
            if success {
                self?.delegate?.guestDeleted(withMessage: "")
            } else if let error = error {
                self?.delegate?.guestDeleteFailed(withError: error)
            }
        }
    }
}

extension AdditionalGuestsInteractor: AdditionalGuestsInteractorProtocol {
    func deleteGuest(atIndex index: Int) {
        guard let user = UserSessionManager.sharedInstance.currentUser else { return }
        user.additionalGuests?.remove(at: index)

        update(user: user)
    }
}
