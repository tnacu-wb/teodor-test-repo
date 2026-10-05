//
//  ProvisionKey.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

public struct DigitalKeyProvisionResponse: Decodable {
    public let provisioningCredentialIdentifier: String?
    public let sharingInstanceIdentifier: String?
    public let cardConfigurationIdentifier: String?
    public let cardTemplateIdentifier: String?
    public let serverEnvironmentIdentifier: String?
    public let accountHash: String?
    public let relyingPartyIdentifier: String?
}
