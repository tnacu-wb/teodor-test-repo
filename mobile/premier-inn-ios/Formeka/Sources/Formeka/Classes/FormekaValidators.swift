//
//  FormekaValidators.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public struct RowValidatorError: LocalizedError {

	public let row: FormekaModelRow
    public let error: Error

    public var errorDescription: String? {
        return error.localizedDescription
    }

    public init(row: FormekaModelRow, error: Error) {

        self.row = row
        self.error = error
    }
}

public enum ValidationError: LocalizedError {
    case valueRequired(String?)
    case nameSpecialCharacters
    case nameTooLong
    case stringLength(ClosedRange<Int>)
    case invalidEmail
    case invalidPhoneNumber
    case invalidPostCode
    case invalidCVV
    case invalidDate
    case invalidCardType
    case invalidCompanyName
    case luhnFailed
    case notANumber
    case invalidAmount(Double)
    case customMessage(String)
    case passwordError(String)
    case passwordErrorSmall(String)
    case passwordEmpty(String)
    case passwordSuccess(String)
    case missingPassport
    case missingDateOfBirth
    case leadGuestUnderAge

    public var errorDescription: String? {
        switch self {
        case .valueRequired(let fieldName):
            guard let fieldName = fieldName else { return NSLocalizedString("This field cannot be empty", comment: "Empty form field error message") }
            return String(format: "%@ %@", NSLocalizedString("Please enter a valid", comment: "custom required field error prefix"), fieldName)
        case .nameSpecialCharacters:
            return NSLocalizedString("Please use only standard characters", comment: "Special characters error message")
        case .nameTooLong:
            return NSLocalizedString("Please use a shorter name", comment: "Name too long error message")
        case .stringLength(let range):
            let message = NSLocalizedString("This has to be between %d and %d characters long", comment: "Invalid text length error message") as NSString
            return NSString.localizedStringWithFormat(message, range.lowerBound, range.upperBound) as String
        case .invalidEmail:
            return NSLocalizedString("Please enter a valid email address", comment: "Invalid email address error message")
        case .invalidPhoneNumber:
            return NSLocalizedString("Please enter a valid number", comment: "Invalid phone number error message")
        case .invalidPostCode:
            return NSLocalizedString("Please enter a valid postcode", comment: "Invalid post code error message")
        case .invalidCVV:
            return NSLocalizedString("Please enter the card CVV number", comment: "Invalid CVV error message")
        case .invalidDate:
            return NSLocalizedString("Please enter a valid expiry date", comment: "Invalid date error message")
        case .invalidCardType:
            return NSLocalizedString("We do not accept this card type. Please use a different card.", comment: "Invalid credit card type error message")
        case .invalidCompanyName:
            return NSLocalizedString("Please enter a valid special character", comment: "Invalid special character")
        case .luhnFailed:
            return NSLocalizedString("Please enter a valid card number", comment: "Luhn check failed")
        case .notANumber:
            return NSLocalizedString("Please enter a valid amount", comment: "Not a number error message")
        case .invalidAmount(let amount):
            let message = NSLocalizedString("The amount must be no greater than %.2f", comment: "Invalid text amount error message") as NSString
            return NSString.localizedStringWithFormat(message, amount) as String
        case .customMessage(let message):
            return NSLocalizedString(message, comment: "custom error created on the fly")
        case .passwordError(let message):
            return NSString.localizedStringWithFormat(NSLocalizedString("Must include %@", comment: "") as NSString, message) as String
        case .passwordErrorSmall(let message):
            return message
        case .passwordEmpty(let message):
            return NSString.localizedStringWithFormat(NSLocalizedString("Must include %@", comment: "") as NSString, message) as String
        case .passwordSuccess(let message):
            return NSLocalizedString(message, comment: "password success")
        case .missingPassport:
            return NSLocalizedString("Please enter a valid passport", comment: "Invalid date error message")
        case .missingDateOfBirth:
            return NSLocalizedString("guestMissingDOBError", comment: "Invalid date error message")
        case .leadGuestUnderAge:
            return NSLocalizedString("guestLeadAgeError", comment: "Invalid lead guest age error message")

        }
    }
}


// MARK: Validators

open class Validator {

	public static let required = RequiredValidator()
	public static let name = NameValidator()
	public static let notNil = NotNilValidator()
	public static let email = EmailValidator()
	public static let phoneNumber = PhoneNumberValidator()
	public static let numeric = NumericValidator()
	public static let ukPostCode = UKPostCodeValidator()
    public static let dePostCode = DEPostCodeValidator()
	public static let creditCardNumber = CreditCardNumberValidator()
	public static let creditCardShortDate = CardShortDateValidator()
    public static let passport = PassportValidator()
    public static let companyName = CompanyNameValidator()
//    public static let password = PasswordValidator()

	public init() {

	}

	open func validate(row: FormekaModelRow) throws {

	}
}


public class RequiredValidator: Validator {

    let range: ClosedRange<Int> = 1...Int.max
    private var customErrorValue: String?
    
    public init(customErrorValue: String? = nil) {
        self.customErrorValue = customErrorValue
    }

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(customErrorValue ?? row.title ?? row.tag.lowercased())) }

		// TODO: This is odd...
        guard range.contains(value.count) else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(customErrorValue ?? row.title ?? row.tag.lowercased())) }
    }
}

public final class RequiredSelectionValidator<T>: Validator {
    private let fieldName: String?

    public init(fieldName: String? = nil) {
        self.fieldName = fieldName
        super.init()
    }

    public override func validate(row: FormekaModelRow) throws {
        guard let _ = row.value as? T else {
            throw RowValidatorError(
                row: row,
                error: ValidationError.valueRequired(fieldName ?? row.title ?? row.tag.lowercased())
            )
        }
    }
}

public class PasswordValidator: Validator {
    private var regexsDict: [String : String]?
    public var customSuccessValue: String
    private var isRequired: Bool
    
    public init(regexsDict: [String : String]?, customSuccessValue: String, isRequired: Bool = true) {
        self.regexsDict = regexsDict
        self.customSuccessValue = customSuccessValue
        self.isRequired = isRequired
    }
    
    public override func validate(row: FormekaModelRow) throws {
        var errorArray: [String] = []
        guard let regexsDict = regexsDict else { return }

        let value = row.value as? String
        guard value != nil || (isRequired == false) else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("password", comment: ""))) }

        if isRequired == false && value?.isEmpty ?? true { return }

        var specialCharError = false
        for regex in regexsDict {
            if value?.range(of: regex.key, options: .regularExpression, range: nil, locale: nil) == nil {
                errorArray.append(regex.value)
                if regex.key == "^[a-zA-Z0-9]*$" {
                    specialCharError = true
                }
            }
        }
        
        if isRequired && value?.isEmpty ?? true {
            let message = errorArray.joined(separator: ", ")
            throw RowValidatorError(row: row, error: ValidationError.passwordEmpty(message))
        } else if !errorArray.isEmpty {
            let message = errorArray.joined(separator: ", ")
            if errorArray.count == 1 && specialCharError == true {
                throw RowValidatorError(row: row, error: ValidationError.passwordErrorSmall(message))
            }
            throw RowValidatorError(row: row, error: ValidationError.passwordError(message))
        }
    }
}

public class NameValidator: Validator {

    private static let regEx = "^[A-zÀ-ÿ\\s'’-]{0,}$"
    private var customErrorValue: String?
    
    public init(customErrorValue: String? = nil) {
        self.customErrorValue = customErrorValue
    }

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(customErrorValue ?? NSLocalizedString("name", comment: ""))) }
        guard NSPredicate(format:"SELF MATCHES %@", NameValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw RowValidatorError(row: row, error: ValidationError.nameSpecialCharacters) }
    }
}

public class StringLengthValidator: Validator {

    let range: ClosedRange<Int>
    private var customErrorValue: String?

    public init(range: ClosedRange<Int>, customErrorValue: String? = nil) {

        self.range = range
        self.customErrorValue = customErrorValue
    }

    public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(customErrorValue ?? row.title)) }
        guard range.contains(value.count) else { throw RowValidatorError(row: row, error:  ValidationError.stringLength(range)) }
    }
}

public class NotNilValidator: Validator {

	public override func validate(row: FormekaModelRow) throws {

        guard row.value != nil else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(row.title)) }
    }
}

public class PassportValidator: Validator {

    public override func validate(row: FormekaModelRow) throws {

        guard row.value != nil else { throw RowValidatorError(row: row, error: ValidationError.missingPassport) }
    }
}

public class EmailValidator: Validator {

    private static let regEx = "[A-zÀ-ÿ0-9._%+-]+@[^-][A-zÀ-ÿ0-9.-]+\\.[A-z]+"

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("email address", comment: ""))) }
        guard NSPredicate(format:"SELF MATCHES %@", EmailValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw RowValidatorError(row: row, error: ValidationError.invalidEmail) }
    }
}

public class PhoneNumberValidator: Validator {

    private static let regEx = "\\+?[\\d ]*$"

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("phone number", comment: ""))) }
        guard NSPredicate(format:"SELF MATCHES %@", PhoneNumberValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw RowValidatorError(row: row, error: ValidationError.invalidPhoneNumber) }
    }
}

public class NumericValidator: Validator {

    private static let regEx = "\\d*"

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(row.tag.lowercased())) }
        guard NSPredicate(format:"SELF MATCHES %@", NumericValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw RowValidatorError(row: row, error: ValidationError.invalidPhoneNumber) }
    }
}

public class UKPostCodeValidator: Validator {

    private static let regEx = "^(([gG][iI][rR] {0,}0[aA]{2})|((([a-pr-uwyzA-PR-UWYZ][a-hk-yA-HK-Y]?[0-9][0-9]?)|(([a-pr-uwyzA-PR-UWYZ][0-9][a-hjkstuwA-HJKSTUW])|([a-pr-uwyzA-PR-UWYZ][a-hk-yA-HK-Y][0-9][abehmnprv-yABEHMNPRV-Y]))) {0,}[0-9][abd-hjlnp-uw-zABD-HJLNP-UW-Z]{2}))$"

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("postcode", comment: ""))) }
        guard NSPredicate(format:"SELF MATCHES %@", UKPostCodeValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw RowValidatorError(row: row, error: ValidationError.invalidPostCode) }
    }

    public func validate(value: FormekaValue?) throws {
        guard let value = value as? String else { throw ValidationError.valueRequired(NSLocalizedString("postcode", comment: "")) }
        guard NSPredicate(format:"SELF MATCHES %@", UKPostCodeValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw ValidationError.invalidPostCode }
    }
}

public class DEPostCodeValidator: Validator {

    private static let regEx = "\\d*"

    public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("postcode", comment: ""))) }
        guard value.count == 5 else { throw RowValidatorError(row: row, error: ValidationError.invalidPostCode) }
        guard NSPredicate(format:"SELF MATCHES %@", DEPostCodeValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw ValidationError.invalidPostCode }
    }
}

public class CVVLengthValidator: Validator {

    private let length: Int

    public init(length: Int) {

        self.length = length
    }

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("security code", comment: ""))) }
        guard value.count == length else { throw RowValidatorError(row: row, error: ValidationError.invalidCVV) }
    }
}

public class CreditCardNumberValidator: Validator {

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("card number", comment: ""))) }

        guard !value.isEmpty else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("card number", comment: ""))) }
        let reversedString = String(value.reversed()).trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).replacingOccurrences(of: " ", with: "")
        var oddNumberSum = 0
        var evenNumberSum = 0

        for index in 0..<reversedString.count {
            let digit = Int(String(reversedString[reversedString.index(reversedString.startIndex, offsetBy: index)])) ?? 0
            let pos = index + 1
            if pos % 2 == 0  {
                var aDigit = digit * 2
                let remainder = aDigit%10
                let quotient = aDigit/10
                aDigit = remainder + quotient
                oddNumberSum += aDigit
            }
            else {
                evenNumberSum += digit
            }
        }
        let total = evenNumberSum + oddNumberSum
        guard total % 10 == 0 else { throw RowValidatorError(row: row, error: ValidationError.luhnFailed) }
    }
}

public class CardShortDateValidator: Validator {

    private static let regEx = "(0[1-9]|1[0-2])/([0-9]{2}$)"

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("date", comment: ""))) }
        guard NSPredicate(format:"SELF MATCHES %@", CardShortDateValidator.regEx).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw RowValidatorError(row: row, error: ValidationError.invalidDate) }
    }
}

public class CostValidator: Validator {

    private let min: Double
    private let max: Double

    public init(min: Double, max: Double) {
        self.min = min
        self.max = max
    }

	public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("amount", comment: ""))) }
        guard let cost = Double(value) else { throw RowValidatorError(row: row, error: ValidationError.notANumber) }
        guard min...max ~= cost else { throw RowValidatorError(row: row, error: ValidationError.invalidAmount(max)) }
    }
}

public class CompanyNameValidator: Validator {

    private static let regEx = #"^[a-zÀÁÂÃÄÅĀẶĄẮÆǼÇĆĈĊČĎĐÈÉÊËĒĔĖĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİĴĶĹĻĽĿŁÑŃŅŇȠÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽàáâãäåāặąắæǽçćĉċčďđèéêëēĕėěĝğġģĥħìíîïĩīǐįĵķĺļľŀłñńņňƞòóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž\u005B\u005D\u005C\u002F\u00AB\u00BB.,:;_!?"*%=+£$€¥&@#()\-'\d ]+$"#

    public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String, !value.isEmpty else {
            throw RowValidatorError(row: row, error: ValidationError.valueRequired(NSLocalizedString("Company name", comment: "company name")))
        }

        let regexOptions: NSRegularExpression.Options = [.caseInsensitive]
        let regex = try? NSRegularExpression(pattern: CompanyNameValidator.regEx, options: regexOptions)

        let trimmed = value.trimmingCharacters(in: .whitespaces)
        let range = NSRange(location: 0, length: trimmed.count)
        let match = regex?.firstMatch(in: trimmed, range: range)

        if match == nil {
            throw RowValidatorError(row: row, error: ValidationError.invalidCompanyName)
        }
    }
}

public class LengthValidator: Validator {

    let range: ClosedRange<Int>
    private var customErrorValue: String?

    public init(range: ClosedRange<Int>, customErrorValue: String? = nil) {

        self.range = range
        self.customErrorValue = customErrorValue
    }

    public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? "")) }
        guard range.contains(value.count) else { throw RowValidatorError(row: row, error:  ValidationError.customMessage(customErrorValue ?? "")) }
    }
}

public class MinimumLengthValidator: Validator {

    private var minimumLength: Int
    private var customErrorValue: String?

    public init(minimumLength: Int,
                customErrorValue: String? = nil) {
        
        self.minimumLength = minimumLength
        self.customErrorValue = customErrorValue
    }

    public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? "")) }
        guard value.count > minimumLength else { throw RowValidatorError(row: row, error:  ValidationError.customMessage(customErrorValue ?? "")) }
    }
}

public class NotEmptyValidator: Validator {

    private var customErrorValue: String?

    public init(customErrorValue: String? = nil) {
        
        self.customErrorValue = customErrorValue
    }

    public override func validate(row: FormekaModelRow) throws {

        guard let value = row.value as? String else { throw RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? "")) }
        guard !value.isEmpty else { throw RowValidatorError(row: row, error:  ValidationError.customMessage(customErrorValue ?? "")) }
    }
}

public class NotEmptyDateValidator: Validator {

    public override func validate(row: FormekaModelRow) throws {

        guard let _ = row.value as? Date else { throw RowValidatorError(row: row, error: ValidationError.missingDateOfBirth) }
    }
}

public class GuestAgeValidator: Validator {
    private var isLeadGuest: Bool

    public init(isLeadGuest: Bool) {

        self.isLeadGuest = isLeadGuest
    }
    public override func validate(row: FormekaModelRow) throws {

        guard let date = row.value as? Date else { throw RowValidatorError(row: row, error: ValidationError.missingDateOfBirth) }
        guard isLeadGuest else { return }
        let calendar = Calendar.current
        guard let age = calendar.dateComponents([.year], from: date, to: Date()).year else { throw RowValidatorError(row: row, error: ValidationError.missingDateOfBirth) }
        if age < 18 {
            throw RowValidatorError(row: row, error: ValidationError.leadGuestUnderAge)
        }
    }
}

public class GuestAddressValidator: Validator {
    private let regex = "^[ \\p{L}0-9äöüÄÖÜß'.,\\-]*$"
    private var customErrorValue: String?

    public init(customErrorValue: String? = nil) {

        self.customErrorValue = customErrorValue
    }
    public override func validate(row: FormekaModelRow) throws {
        let error = RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? ""))
        guard let value = row.value as? String else { throw error }
        guard NSPredicate(format:"SELF MATCHES %@", regex).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw error }
    }
}

public class GuestCityValidator: Validator {
    private let regex = "[a-zA-Z0-9äöüÄÖÜß/'., \\-]*"
    private var customErrorValue: String?
    
    public init(customErrorValue: String? = nil) {
        
        self.customErrorValue = customErrorValue
    }
    public override func validate(row: FormekaModelRow) throws {
        let error = RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? ""))
        guard let value = row.value as? String else { throw error }
        guard NSPredicate(format:"SELF MATCHES %@", regex).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw error }
    }
}

public class GuestPostcodeValidator: Validator {
    private let regex = "^[A-Za-z0-9\\s\\-]{1,10}$"
    private var customErrorValue: String?

    public init(customErrorValue: String? = nil) {

        self.customErrorValue = customErrorValue
    }
    public override func validate(row: FormekaModelRow) throws {
        let error = RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? ""))
        guard let value = row.value as? String else { throw error }
        guard NSPredicate(format:"SELF MATCHES %@", regex).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw error }
    }
}

public class GuestPassportValidator: Validator {
    private let regex = "^[A-Za-z0-9]+$"
    private var customErrorValue: String?
    
    public init(customErrorValue: String? = nil) {
        
        self.customErrorValue = customErrorValue
    }
    public override func validate(row: FormekaModelRow) throws {
        let error = RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? ""))
        guard let value = row.value as? String else { throw error }
        guard NSPredicate(format:"SELF MATCHES %@", regex).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw error }
    }
}

public class OnlyLettersValidator: Validator {
    private let regex = "^[A-Za-z]+$"
    private var customErrorValue: String?

    public init(customErrorValue: String? = nil) {
        
        self.customErrorValue = customErrorValue
    }
    public override func validate(row: FormekaModelRow) throws {
        let error = RowValidatorError(row: row, error: ValidationError.customMessage(customErrorValue ?? ""))
        guard let value = row.value as? String else { throw error }
        guard NSPredicate(format:"SELF MATCHES %@", regex).evaluate(with: value.trimmingCharacters(in: .whitespaces)) else { throw error }
    }
}
