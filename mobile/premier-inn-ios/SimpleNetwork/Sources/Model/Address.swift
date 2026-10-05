//
//  Address.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 16/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Contacts

public enum AddressType: String {
    case home = "HOME"
    case commercial = "BUSINESS"
}

public enum AddressError: Error {
    case missingAddressDictionary
}

public struct Address: CustomStringConvertible, Equatable {
    public let line1: String?
    public let line2: String?
    public let line3: String?
    public let line4: String?
    public let line5: String?
    public let cityName: String?
    public let country: Country?
    public let postcode: String?
    let label: String?
    let type: AddressType?
    public let companyName: String?

    public init(
        line1: String?,
        line2: String?,
        line3: String?,
        line4: String? = nil,
        line5: String? = nil,
        cityName: String? = nil,
        country: Country? = nil,
        postcode: String?,
        label: String? = nil,
        type: AddressType? = nil,
        companyName: String? = nil
    ) {
        self.line1 = line1
        self.line2 = line2
        self.line3 = line3
        self.line4 = line4
        self.line5 = line5
        self.cityName = cityName
        self.country = country
        self.postcode = postcode
        self.label = label
        self.type = type
        self.companyName = companyName
    }

    public init(dictionary: PIDictionary?) throws {
        guard let dictionary = dictionary else { throw AddressError.missingAddressDictionary }

        self.postcode = (dictionary.value(forKeys: ["postcode", "postCode", "postalCode"]) as String?) ?? ""
        self.line1 = (dictionary.value(forKeys: ["line1", "addressline1", "addressLine1"]) as String?) ?? ""
        self.line2 = (dictionary.value(forKeys: ["line2", "addressline2", "addressLine2"]) as String?) ?? ""
        self.line3 = (dictionary.value(forKeys: ["line3", "addressline3", "addressLine3"]) as String?) ?? ""
        self.line4 = dictionary["line4"] as? String ?? ""
        self.line5 = dictionary["line5"] as? String ?? ""
        self.cityName = dictionary["cityName"] as? String ?? ""

        self.country = {
            // country - reservation
            // countryCode, countryCodeISO - account

            if let country = Country.countriesList.first(where: { $0 == (dictionary["country"] as? Country) }) {
                return country
            }

            if let country = Country.countriesList.first(where: { $0.code == dictionary["countryCode"] as? String }) {
                return country
            }

            if let country = Country.countriesList.first(where: { $0.isoCode == dictionary["countryCodeISO"] as? String }) {
                return country
            }

            if let country = Country.countriesList.first(where: { $0.isoCode == dictionary["countryCode"] as? String }) {
                return country
            }

            // Shot in the dark
            if let country = Country.countriesList.first(where: { $0.name == dictionary["country"] as? String }) {
                return country
            }

            // Shot in the dark take 2
            if let country = Country.countriesList.first(where: { $0.code == dictionary["country"] as? String }) {
                return country
            }

            return nil
        }()

		self.type = {
			if let addressType = dictionary["type"] as? AddressType {
				return addressType
			}

			let addressType = dictionary["type"] as? String ?? "HOME"

			return AddressType(rawValue: addressType)
		}()

        self.label = dictionary["label"] as? String
        self.companyName = dictionary["companyName"] as? String ?? ""
    }

	public var description: String {
        var array = [String]()

        if let companyName = companyName, companyName.isEmpty == false {
            array.append(companyName)
        }

        if let line1 = line1, line1.isEmpty == false {
            array.append(line1)
        }

        if let line2 = line2, line2.isEmpty == false {
            array.append(line2)
        }

        if let line3 = line3, line3.isEmpty == false {
            array.append(line3)
        }

        if let postcode = postcode, postcode.isEmpty == false {
            array.append(postcode)
        }

        return array.joined(separator: ", ")
    }
}

public extension Address {
    var postalAddressDictionary: PIDictionary? {
        var dict = PIDictionary()
        dict[CNPostalAddressStreetKey] = line1
        dict[CNPostalAddressCountryKey] = country?.code
        dict[CNPostalAddressISOCountryCodeKey] = country?.code
        dict[CNPostalAddressPostalCodeKey] = postcode

        if let city = line2 {
            dict[CNPostalAddressCityKey] = city
        }

        if let state = line3 {
            dict[CNPostalAddressStateKey] = state
        }

        return dict
    }
}

extension Address: Decodable {
    enum CodingKeys: String, CodingKey {
        case addressLine1
        case addressLine2
        case addressLine3
        case addressLine4
        case addressLine5
        case postCode
        case country
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self.line1 = try? container.decode(String.self, forKey: .addressLine1)
        self.line2 = try? container.decode(String.self, forKey: .addressLine2)
        self.line3 = try? container.decode(String.self, forKey: .addressLine3)
        self.line4 = try? container.decode(String.self, forKey: .addressLine4)
        self.line5 = try? container.decode(String.self, forKey: .addressLine5)
        self.postcode = try? container.decode(String.self, forKey: .postCode)

        let countryCode = try? container.decode(String.self, forKey: .country)
        self.country = Country.countriesList.first { $0.code == countryCode }

        self.label = nil
        self.type = nil
        self.companyName = nil
        self.cityName = nil
    }
}

extension Address: Encodable {
    public func encode(to encoder: Encoder) throws {}
}
