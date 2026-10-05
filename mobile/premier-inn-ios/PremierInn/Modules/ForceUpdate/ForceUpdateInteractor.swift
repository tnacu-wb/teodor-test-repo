//
//  ForceUpdateInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

protocol ForceUpdateInteractorProtocol {
    var viewModel: ForceUpdateViewModel? { get }
    var appStoreUrl: URL? { get }
}

protocol ForceUpdateInteractorDataProvider {
    var forceUpdateTitle: String { get }
    var forceUpdateDescription: String { get }
}

extension SettingsManager: ForceUpdateInteractorDataProvider {}

class ForceUpdateInteractor {
    private let dataProvider: ForceUpdateInteractorDataProvider = SettingsManager.sharedInstance
}

extension ForceUpdateInteractor: ForceUpdateInteractorProtocol {
    private struct ViewModel: ForceUpdateViewModel {
        let title: String
        let description: String
        let actionButtonTitle: String
        let actionButtonDescription: String
    }

    var viewModel: ForceUpdateViewModel? {
        ViewModel(
            title: dataProvider.forceUpdateTitle,
            description: dataProvider.forceUpdateDescription,
            actionButtonTitle: PILocalizedString("Update", comment: ""),
            actionButtonDescription: PILocalizedString("takes you to the app store", comment: "")
        )
    }

    var appStoreUrl: URL? {
        Constants.appStoreUrl
    }
}
