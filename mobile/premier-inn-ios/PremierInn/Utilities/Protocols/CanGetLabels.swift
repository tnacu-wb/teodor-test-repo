//
//  CanGetLabels.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 31.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol LabelsProvider {
    func getCategoryLabels(labelType: LabelsConfig, completion: @escaping (Result<CategoryLabels>) -> Void)
}

enum GetLabelsError: Error {
    case keyInstructions
}
extension GetLabelsError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .keyInstructions:
            return PILocalizedString("Something went wrong")
        }
    }
}
