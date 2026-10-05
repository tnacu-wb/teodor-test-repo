//
//  DigitalKeyQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

extension GraphQL {
    static let generateOTPMutation =
    """
    mutation digitalKeyGenerateOtp($email: String!) {
        digitalKeyGenerateOtp(email: $email) {
            success
        }
    }
    """

    static let verifyOTPMutation =
    """
       mutation verifyOtp($bookingReference: String!, otpCode: String!) {
              verifyOtp(bookingReference: $bookingReference, otpCode: $otpCode) {
               success
           }
       }
    """

    static let provisionDigitalKeyMutation =
    """
       mutation digitalKeyProvision($digitalkeyProvisionRequest: DigitalKeyProvisionRequest!) {
                digitalKeyProvision(digitalkeyProvisionRequest: $digitalkeyProvisionRequest) {
               provisioningCredentialIdentifier
               sharingInstanceIdentifier
               cardConfigurationIdentifier
               cardTemplateIdentifier
               serverEnvironmentIdentifier
               accountHash
               relyingPartyIdentifier
           }
       }
    """

    static let digitalKeyCheckinMutation =
    """
        mutation digitalKeyCheckIn($digitalKeyCheckInCriteria: DigitalKeyCheckInCriteria!) {
            digitalKeyCheckIn(digitalKeyCheckInCriteria: $digitalKeyCheckInCriteria) {
                roomNumber
                checkInStatus
           }
       }
    """
}
