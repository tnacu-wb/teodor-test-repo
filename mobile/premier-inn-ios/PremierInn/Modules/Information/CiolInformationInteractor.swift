//
//  CiolInformationInteractor.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 08.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

class CiolInformationInteractor: CiolInformationInteractorProtocol {
    var viewModel: CiolInformationModel

    init(viewModel: CiolInformationModel) {
        self.viewModel = viewModel
    }
}
