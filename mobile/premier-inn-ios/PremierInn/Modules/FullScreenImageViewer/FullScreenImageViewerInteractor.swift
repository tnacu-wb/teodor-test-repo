//
//  FullScreenImageViewerInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 12/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

class FullScreenImageViewerInteractor {
    private var roundelDesigns: [RoundelDesign]
    private var startIndex: Int

    init(with roundelDesigns: [RoundelDesign], and startIndex: Int) {
        self.roundelDesigns = roundelDesigns
        self.startIndex = startIndex
    }
}

extension FullScreenImageViewerInteractor: FullScreenImageViewerInteractorProtocol {
    struct ViewModel: FullScreenImageViewerViewModel {
        var carouselRoundelDesigns: [RoundelDesign]
        let startIndex: Int
    }

    var viewModel: FullScreenImageViewerViewModel? {
        ViewModel(
            carouselRoundelDesigns: roundelDesigns,
            startIndex: startIndex
        )
    }

    func trackState() {
        var data: PIDictionary = [
            PIAnalytics.Keys.environment: AnalyticsConstants.environment,
            PIAnalytics.Keys.userLogin: UserSessionManager.sharedInstance.currentUser != nil ? LoggedInAnalytic.loggedIn
            .rawValue : LoggedInAnalytic.notLoggedIn.rawValue,
            PIAnalytics.Keys.timeZone: TimeZone.current.description,
            PIAnalytics.Keys.language: Locale.current.language.languageCode?.identifier ?? "n/a",
            PIAnalytics.Keys.screenType: PIAnalytics.StateTypes.bookingFlow
        ]

        if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
           let accessLevel = user.accessLevel?.rawValue {
            data[PIAnalytics.Keys.companyID] = companyId
            data[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

        AnalyticsManager.shared.trackState(PIAnalytics.StateNames.hotelImages, data: data)
    }
}
