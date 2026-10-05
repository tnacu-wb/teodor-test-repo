//
//  DatatransPaymentInteractor.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

class DatatransPaymentInteractor {
    private let basketId: String
    private let dataProvider: DatatransPaymentDataProvider

    init(basketId: String, dataProvider: DatatransPaymentDataProvider = RequestsManager()) {
        self.basketId = basketId
        self.dataProvider = dataProvider
    }
}

// MARK: - DatatransPaymentInteractorProtocol

extension DatatransPaymentInteractor: DatatransPaymentInteractorProtocol {
    func initiatePaymentSession(
        completion: @escaping (Result<DatatransPaymentSessionResponse>) -> Void
    ) {
        dataProvider.initMobileSDKPayment(basketId: basketId) { response, error in
            if let response = response {
                completion(.success(result: response))
            } else if let error = error {
                completion(.failure(error: DatatransPaymentFlowError.from(error: error)))
            } else {
                completion(.failure(error: DatatransPaymentFlowError.unknown))
            }
        }
    }
}
