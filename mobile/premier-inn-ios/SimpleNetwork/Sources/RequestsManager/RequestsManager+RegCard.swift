//
//  RequestsManager+RegCard.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 04.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    func authorizePayment() async throws -> CCCPPaymentProviderResponse? {
        let resource = try Router.current.authorizePayment()
        return try await load(resource: resource)
    }

    func attachFileToReservation(params: AuthorizationFileAttachmentParams) async throws -> StatusResult? {
        let resource = try Router.current.attachFileToReservation(params: params)
        return try await  load(resource: resource)
    }

    func updatePreCheckInStatus(params: UpdatePrecheckInParams) async throws -> StatusResult? {
        let resource = try Router.current.updatePreCheckInStatus(params: params)
        return try await load(resource: resource)
    }
}
