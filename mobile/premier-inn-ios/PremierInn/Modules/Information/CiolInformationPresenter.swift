//
//  CiolInformationPresenter.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 08.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

class CiolInformationPresenter {
    weak var view: CiolInformationViewProtocol?
    var interactor: CiolInformationInteractorProtocol?
    var router: CiolInformationRouterProtocol?
}

extension CiolInformationPresenter: CiolInformationEventHandler {
    func close() {
        router?.close()
    }
}
