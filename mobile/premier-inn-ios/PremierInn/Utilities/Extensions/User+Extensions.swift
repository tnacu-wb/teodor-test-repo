//
//  User+Extension.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 13.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork

extension User {
    var isMissingNationality: Bool {
        country == nil
    }

    var isMissingPassportNumber: Bool {
        guard let passport = passport else { return true }
        return passport.number.isEmpty
    }

    var isMissingFirstname: Bool {
		guard let firstName else { return true }
        return firstName.isEmpty
    }

    var isMissingLastName: Bool {
		guard let lastName else { return true }
        return lastName.isEmpty
    }

    var isMissingAddress: Bool {
        guard let address, let line1 = address.line1 else { return true}
        return line1.isEmpty
    }

    var isMissingPostcode: Bool {
        guard let address, let postcode = address.postcode else { return true }
        return postcode.isEmpty
    }

    var isMissingContactNumber: Bool {
        guard let contactNumber else { return true }
        return contactNumber.isEmpty
    }

    var isMissingEmail: Bool {
        guard let emailAddress else { return true }
        return emailAddress.isEmpty
    }

    var isMissingCountry: Bool {
        country == nil
    }

    var isMissingDOB: Bool {
        guard let dob else { return true }
        return dob.isEmpty
    }

    var isMissingBillingDetails: Bool {
         isMissingAddress ||
         isMissingEmail ||
         isMissingContactNumber
     }
}
