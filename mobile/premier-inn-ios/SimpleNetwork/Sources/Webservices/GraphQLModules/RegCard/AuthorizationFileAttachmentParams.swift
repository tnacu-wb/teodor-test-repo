//
//  AuthorizationFileAttachmentParams.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 04.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//


public struct AuthorizationFileAttachmentParams {
    public let fileName: String
    public let reservationId: String
    let overwriteExistingFile: Bool
    let description: String
    public let hotelId: String
    let global: Bool
    public let fileAttachment: String

    public init(
        fileName: String,
        reservationId: String,
        overwriteExistingFile: Bool = true,
        description: String = "Pre-check-in registration card",
        hotelId: String,
        global: Bool = true,
        fileAttachment: String
    ) {
        self.fileName = fileName
        self.reservationId = reservationId
        self.overwriteExistingFile = overwriteExistingFile
        self.description = description
        self.hotelId = hotelId
        self.global = global
        self.fileAttachment = fileAttachment
    }

    enum Constants {
        static let fileName = "fileName"
        static let reservationId = "reservationId"
        static let overwriteExistingFile = "overwriteExistingFile"
        static let description = "description"
        static let hotelId = "hotelId"
        static let global = "global"
        static let fileAttachment = "fileAttachment"

        static let decodingKey = "attachFileToReservation"
    }
}
