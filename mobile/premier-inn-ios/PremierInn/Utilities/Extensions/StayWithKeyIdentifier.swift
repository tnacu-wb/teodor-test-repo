//
//  StayWithKeyIdentifier.swift
//  PremierInn
//
//  Created by Santa Gurung on 09/10/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum StayWithKeyIdentifierError: LocalizedError {
    case missingBookingReference
    case missingKeyIdentifier
}

struct StayWithKeyIdentifier: DictionaryInitialisable {
    var bookingReference: String
    var digitalKeyIdentifier: String

    init(dictionary: PIDictionary) throws {
        guard let bookingReference = dictionary["bookingReference"] as? String
            else { throw StayWithKeyIdentifierError.missingBookingReference }
        guard let digitalKeyIdentifier = dictionary["digitalKeyIdentifier"] as? String
            else { throw StayWithKeyIdentifierError.missingKeyIdentifier }

        self.bookingReference = bookingReference
        self.digitalKeyIdentifier = digitalKeyIdentifier
    }

    init?(stay: Stay) {
        guard let digitalKeyIdentifier = stay.digitalKeyIdentifier else { return nil }
        self.bookingReference = stay.identifier
        self.digitalKeyIdentifier = digitalKeyIdentifier
    }

    var identifier: String {
        bookingReference
    }

    var dictionary: PIDictionary {
        [
            "bookingReference": bookingReference,
            "digitalKeyIdentifier": digitalKeyIdentifier
        ]
    }
}
