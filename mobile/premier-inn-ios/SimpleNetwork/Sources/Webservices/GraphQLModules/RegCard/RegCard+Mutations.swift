//
//  File.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 07.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

extension GraphQL {
    static let regCardAuthorizeMutation =
    """
    mutation AuthorizeCard(
      $requestId: String!
      $environment: String!
      $language: String!
      $country: String
    ) {
      authorizeCard(
        initiateAuthorizeScaRequest: {
          requestId: $requestId
          environment: $environment
          language: $language
          country: $country
        }
      ) {
        paymentRedirect
        template
        sessionId
        providerUrl
      }
    }
    """

    static let regAttachPDFMutation =
       """
       mutation attachFileToReservation(
         $fileName: String!
         $reservationId: String!
         $overwriteExistingFile: Boolean!
         $description: String!
         $hotelId: String!
         $global: Boolean!
         $fileAttachment: String!
       ) {
         attachFileToReservation(
           fileAttachmentCriteria: {
             fileName: $fileName
             reservationId: $reservationId
             overwriteExistingFile: $overwriteExistingFile
             description: $description
             hotelId: $hotelId
             global: $global
             fileAttachment: $fileAttachment
           }
         ) {
           status
           message
         }
       }
       """

    static let regCardPreCheckinMutation =
    """
    mutation preCheckIn(
      $hotelId: String!
      $reservationId: String!
      $arrivalTime: String!
    ) {
      preCheckInStatus(
        preCheckInCriteria: {
          hotelId: $hotelId
          reservationId: $reservationId
          arrivalTime: $arrivalTime
        }
      ) {
        status
        message
      }
    }
    """
}
