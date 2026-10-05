//
//  CiolInformationPresenter.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 10.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

class CiolInformationPresenter {
    weak var view: CiolInformationViewProtocol?
    var interactor: CiolInformationInteractorProtocol?
    var router: CiolInformationRouterProtocol?
    let analytics: AnalyticsType

    init(analytics: AnalyticsType) {
        self.analytics = analytics
    }
}

extension CiolInformationPresenter: CiolInformationViewEventHandler {
    var customAnalyticsParameters: PIDictionary? {
        var dict = analytics.analyticsProperties(
            stateType: PIAnalytics.StateTypes.ciolFlow
        )
        dict[PIAnalytics.Keys.checkInOnlineBottomSheet] = true

        return dict
    }

    func viewIsReady() {
        view?.customAnalyticsParameters = customAnalyticsParameters

        guard let viewModel = interactor?.viewModel else { return }
        view?.reloadData(with: viewModel)
    }

    func close() {
        router?.close()
    }

    func logActionAnalytics() {
        analytics.trackAction(
            PIAnalytics.Action.ciolBottomSheetActionName,
            userInfo: [PIAnalytics.Keys.checkInOnlineBottomSheetClick: true]
        )
    }
}
