//
//  Mapper+CancelReservation.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum CancelReservationMapper {
    public static func map(input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()
        // It looks like we do not do anything with the cancellationId apart from check it is not nil so this just makes sure the new call has worked by checking we have a valid basketReference.
        dict["cancellationId"] = input["basketReference"]

        return dict
    }
}
