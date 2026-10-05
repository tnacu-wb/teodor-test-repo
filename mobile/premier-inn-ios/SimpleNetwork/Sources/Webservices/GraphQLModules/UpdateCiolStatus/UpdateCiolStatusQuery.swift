//
//  UpdateCiolStatusQuery.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 11/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let updateCiolStatusQuery: String =
    """
    mutation UpdateCiolStatus($reservationIds: [String!]!, $hotelId: String!, $ciolStatus: CiolStatusEnum!) {
      updateUdfc20(
        updateUdfc20Criteria: {
          reservationIds: $reservationIds
          hotelId: $hotelId
          ciolStatus: $ciolStatus
        }
      )
    }
    """
}
