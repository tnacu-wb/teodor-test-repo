//
//  Country.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public struct Country: Codable {
    private static let workaroundCountryCodes = ["UAE": "AE"]

    public static var greatBritain: Country? { countriesList.first(where: { $0.code == "GB"}) }
    public static var germany: Country? { countriesList.first(where: { $0.code == "D"}) }
    public static var unitedStates: Country? { countriesList.first(where: { $0.code == "USA"}) }
    public static var unitedEmirates: Country? { countriesList.first(where: { $0.code == "UAE"}) }
	// This class is used just as an helper to get the right Bundle at runtime for testing purpouses
	class FakeClass {}

    public static var countriesList: [Country] = []

    // this is not really used as it's not localised
	public static let backupCountriesList: [Country] = {
        guard let fileURL = Bundle.simpleNetworkResources.url(forResource: "countries", withExtension: "json")
        	else { return [] }
		guard let data = try? Data(contentsOf: fileURL) else { return [] }

		let decoder = JSONDecoder()
		let countries = try? decoder.decode([Country].self, from: data)

		return countries ?? []
	}()

    public static func refreshCountries(completion: (() -> Void)? = nil) {
        let requestsManager = RequestsManager()
        requestsManager.getCountries(completion: { countries, error in
            guard error == nil else {
                completion?()
                return
            }

            countriesList = countries ?? []

            completion?()
        })
    }

	enum CodingKeys: String, CodingKey {
		case code = "countryCodeLegacy"
		case name = "countryName"
        case isoCode = "countryCode"
		case dialingCode
		case flagImage = "flagSrc"
		case passportRequired
        case nationality = "nationality"
	}

    public var mappedCode: String? {
        code

        // Enable this code to map known mismatches between stored codes, and those expected by the api
//        guard let code = code else { return nil }
//        guard Country.workaroundCountryCodes.keys.contains(code) else { return code }
//        return Country.workaroundCountryCodes[code]
    }

    public let code: String?
    public let name: String
    public let isoCode: String
    let dialingCode: String?
    let flagImage: String?
    public let passportRequired: Bool?
    public let nationality: String?

    /**
     Given the country has a valid iso code this returns the relevant flag emoji, for example 🇼🇸 for Samoa with an iso code of AS

     If the iso code for the country is invalid or missing we could either return nil or an empty String; the current implementation is that it will simply return nil to allow you to decide on whether you want to use the return value from here without having to do an empty check; i.e. if you get something back it'll be a flag 🙌
     */
    public var flagEmoji: String? {
        // 127397 is the root Unicode index for flags
        let baseFlagUnicodeIndex = 127397

        // Next we'll prepare an empty collection of Unicode Scalars
        var scalarContainer = String.UnicodeScalarView()

        let countryCode = self.isoCode.uppercased()

        // We loop over each of the scalars in the passed in country code
        for scalar in countryCode.unicodeScalars {
            let integerRepresentationOfScalarValue = Int(scalar.value)

            // Shift the letter index to the flags index
            guard let updatedFlagUnicodeIndex = UnicodeScalar(baseFlagUnicodeIndex + integerRepresentationOfScalarValue)
            	else { return nil }

            // And then append the scalar to the to the Unicode Scalar collection
            scalarContainer.append(updatedFlagUnicodeIndex)
        }

        // And finally flatten our Scalar collection back into a String
        return String(scalarContainer)
    }

    public init(
    	code: String?,
    	name: String,
    	isoCode: String,
    	dialingCode: String?,
    	flagImage: String?,
    	passportRequired: Bool?,
    	nationality: String?
    ) {
		self.code = code
		self.name = name
        self.isoCode = isoCode
		self.dialingCode = dialingCode
		self.flagImage = flagImage
		self.passportRequired = passportRequired
        self.nationality = nationality
	}
}

extension Country: Equatable {
	public static func == (lhs: Country, rhs: Country) -> Bool {
		lhs.code == rhs.code
	}
}
