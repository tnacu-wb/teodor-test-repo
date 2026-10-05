//
//  Constants.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 17/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Alamofire
import AlamofireImage
import UIKit

public typealias PIDictionary = [String: Any]
public typealias AuthCredentials = (username: String, password: String, business: Bool)

public extension Notification.Name {
    static let marketingPreferencesDidChange = NSNotification.Name(rawValue: "MarketingPreferencesDidChange")
    static let userDidChange = Notification.Name(rawValue: "CurrentUserDidChange")
	static let reservationSummariesDidChange = Notification.Name(rawValue: "ReservationSummariesDidChange")
}

public enum Constants {
    static let akamaiSensorDataKey = "X-acf-sensor-data"
    static let imagesBaseURL = URL(string: "https://www.premierinn.com")!
	static let hotelResourcesBaseAddress = "https://www.premierinn.com/gb/en/hotels"
    static let bookingChannelKey = "bookingChannel"
	static let bookingChannel = "MOBILE"
    static let businessBookingChannel = "CBT"
    static let deBookingChannel = "WEB_DE" // we're temporarily using WEB_DE channel for a few BART translations and emails
    static let language = "language"
    static let country = "country"
    static let hotelCodeKey = "hotel-code"
    static let placeOfIssueMaxLength = 24
    static let minimumNumberOfAdultsInRoom = 1
    static let hotelEventsGroupCode = "EVENTS"
    static let emailContactType = "email"
    static let emailJourney = "PERMISSIONCENTRE"
    static let arrivalDateOffset = 1

    static var searchRadius: Int {
        LanguageManager.supportedLanguage == .english ? 30 : 50
    }

    static var searchRadiusUnit: String {
        LanguageManager.supportedLanguage == .english ? "MILES" : "KILOMETERS"
    }

    static let maxStaysNumber = 40
    public static let outstandingAmountEmptyOperaCode: NSDecimalNumber = -9999

    public enum NotificationKeys {
        public static let reservationSummariesWereImported = "reservationsImported"
    }

    public enum PaymentCardCodes {
        public static let businessCard = "AT"
        public static let amex = "AM"
        public static let businessCardEuro = "BD"
    }

    public enum PIBAEuro {
        public static let subType = "PIBADE"
        public static let customType = "NEW_PIBA_EURO"
    }

    public enum CMS {
        // TODO: ELE-2097 Read business card questions and answers from a json response
        static let businessCardQuestionsAndAnswers: [BusinessCardQuestionAndAnswer] = {
            var questions: [BusinessCardQuestionAndAnswer] = []

            let businessCardLabel = NSLocalizedString(
                "Purchase order number",
                comment: ""
            )
            let businessCardAnswer = ManagementInformationAnswer(
                answerType: .field,
                answers: nil
            )
            let businessCardQuestion = CompanyManagementQuestion(
                questionId: nil,
                label: businessCardLabel,
                mandatory: false,
                active: true,
                managementInformationAnswer: businessCardAnswer,
                location: "B"
            )

            questions.append(BusinessCardQuestionAndAnswer(
                type: .purchaseOrder,
                question: businessCardQuestion,
                answer: nil
            ))

            let managementInfoLabel = NSLocalizedString(
                "Trip purpose",
                comment: ""
            )
            let managementInfoAnswer = ManagementInformationAnswer(
                answerType: .field,
                answers: nil
            )
            let managementInfoQuestion = CompanyManagementQuestion(
                questionId: nil,
                label: managementInfoLabel,
                mandatory: false,
                active: true,
                managementInformationAnswer: managementInfoAnswer,
                location: "B"
            )

            questions.append(BusinessCardQuestionAndAnswer(
                type: .customerReference,
                question: managementInfoQuestion,
                answer: nil
            ))

            return questions
        }()
    }

    enum ImageTags {
        static let primary = ["exterior", "internal", "meeting-room", "play-area", "reception", "surrounding-area"]
        static let room = [
        	"accessible-bathroom",
        	"standard-bathroom",
        	"bath",
        	"shower",
        	"double-room",
        	"family-room",
        	"ID2",
        	"ID3",
        	"ID4",
        	"pti",
        	"single-room",
        	"bedroom",
        	"twin-room",
        	"hub-accessible-room",
        	"hub-bigger-room",
        	"hub-standard-room",
        	"hub-bathroom"
        ]
        static let restaurant = [
        	"bar",
        	"beefeater",
        	"brewers-fayre",
        	"coffee-shop",
        	"food",
        	"orange-cow",
        	"restaurant",
        	"tgi-fridays",
        	"table-table",
        	"thyme",
        	"whitbread-inn",
        	"breakfast",
        	"family"
        ]
        static let parking = ["parking"]
        static let breakfast = [
        	"breakfast",
        	"coffee-shop",
        	"bar",
        	"beefeater",
        	"brewers-fayre",
        	"food",
        	"orange-cow",
        	"restaurant",
        	"tgi-fridays",
        	"table-table",
        	"thyme",
        	"whitbread-inn",
        	"family"
        ]
        static let accessibleBathroom = ["accessible-lowered-bathroom", "accessible-wet-room"]
    }

    enum CCC {
        static let channelIOS = "APPS_IOS"
        static let channelBB = "BB"
        static let paymentSubType = "ECOMM"
        static let paypalSubType = "MIT"
        static let environment = "*"
        static let businessSiteTypeHotel = "HOTEL"
    }

    public enum EmployeeOffer {
        public static let rateCode = "EMP01"
        public static let rateClassification = "EMPLOYEE"
    }

    enum RequestCoding {
        static let data = "data"
    }
}

public extension Date {
    var parameterString: String {
        DateFormatter.parameterFormatter.string(from: self)
    }

    var localizedVeryShortStringFormat: String {
        DateFormatter.veryShortStringFormatter.string(from: self)
    }

    var expiryDateFormat: String {
        DateFormatter.expiryDateFormatter.string(from: self)
    }

    var creditCardDateFormat: String {
        DateFormatter.creditCardDateFormatter.string(from: self)
    }

    var creditCardDateFormatBusinessBooker: String {
        DateFormatter.creditCardDateFormatterBusinessBooker.string(from: self)
    }

    func startOfMonth() -> Date {
        NSCalendar.current.date(from: Calendar.current.dateComponents(
        	[.year, .month],
        	from: NSCalendar.current.startOfDay(for: self)
        )) ?? self
    }

    func endOfMonth() -> Date {
        Calendar.current.date(byAdding: DateComponents(month: 1, second: -1), to: self.startOfMonth()) ?? self
    }

    var localizedShortStringFormat: String {
        DateFormatter.shortStringFormatter.string(from: self)
    }

	var analyticsDateFormat: String {
		DateFormatter.analyticsDateFormatter.string(from: self)
	}

    var paymentMethodsFormat: String {
        DateFormatter.paymentMethodsDepartureDateFormatter.string(from: self)
    }

    func dateByAddingUnit(unitType: NSCalendar.Unit = .day, number: Int) -> Date? {
        switch unitType.rawValue {
        case NSCalendar.Unit.year.rawValue:
            return Calendar.current.date(byAdding: .year, value: number, to: self)
        case NSCalendar.Unit.month.rawValue:
            return Calendar.current.date(byAdding: .month, value: number, to: self)
        default:
            return Calendar.current.date(byAdding: .day, value: number, to: self)
        }
    }

	func numberOfNights(to endDate: Date?) -> Int {
		guard let endDate = endDate else { return 0 }
		guard let calendar = NSCalendar(calendarIdentifier: .gregorian) else { return 0 }

        // round dates to midnight
        let roundedStartDate = Date(timeIntervalSinceReferenceDate: (self.timeIntervalSinceReferenceDate / (24 * 60 * 60.0))
        	.rounded(.toNearestOrEven) * (24 * 60 * 60.0))
        let roundedEndDate = Date(timeIntervalSinceReferenceDate: (endDate.timeIntervalSinceReferenceDate / (24 * 60 * 60.0))
        	.rounded(.toNearestOrEven) * (24 * 60 * 60.0))

        let startDay = calendar.ordinality(of: .day, in: .era, for: roundedStartDate)
		let endDay = calendar.ordinality(of: .day, in: .era, for: roundedEndDate)

		guard startDay != NSNotFound else { return 0 }
		guard endDay != NSNotFound else { return 0 }

		return endDay - startDay
	}

	func daysToCheckInDate(_ checkInDate: Date) -> Int {
		let cal = Calendar.current

		let fixedSearchDate: Date? = {
			var components = cal.dateComponents([.year, .month, .day, .hour], from: self)
			components.hour = 14
			components.second = 0
			components.minute = 0

			return cal.date(from: components)
		}()

		let fixedCheckInDate: Date? = {
			var components = cal.dateComponents([.year, .month, .day, .hour], from: checkInDate)
			components.hour = 14
			components.second = 0
			components.minute = 0

			return cal.date(from: components)
		}()

		guard let startFixed = fixedSearchDate else { return 0 }
		guard let endFixed = fixedCheckInDate else { return 0 }
		guard let start = cal.ordinality(of: .day, in: .era, for: startFixed) else { return 0 }
		guard let end = cal.ordinality(of: .day, in: .era, for: endFixed) else { return 0 }

		return end - start
	}

    var dateNormalised: Date? {
        let cal = Calendar.current
        var components = cal.dateComponents([.year, .month, .day, .hour], from: self)
        components.hour = 14
        components.second = 0
        components.minute = 0

        return cal.date(from: components)
    }
}

public extension DateFormatter {
    static let parameterFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_GB")
        formatter.dateFormat = "yyyy-MM-dd"

        return formatter
    }()

    static let veryShortStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("d EEE MMM")

        return formatter
    }()

    static let creditCardDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_GB")
        formatter.dateFormat = "MM/yy"

        return formatter
    }()

    static let creditCardDateFormatterBusinessBooker: DateFormatter = {
        let formatter = DateFormatter()
            formatter.locale = Locale(identifier: "en_GB")
            formatter.dateFormat = "MMyy"

            return formatter
    }()

    static let alternateExpiryDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_GB")
        formatter.dateFormat = "MMyy"

        return formatter
    }()

    static let expiryDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_GB")
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"

        return formatter
    }()

    static let shortStringFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("d EEE MMMM")

        return formatter
    }()

	static let analyticsDateFormatter: DateFormatter = {
		let formatter = DateFormatter()
		formatter.locale = Locale(identifier: "en_GB")
		formatter.dateFormat = "dd/MM/yyyy"

		return formatter
	}()

    static let paymentMethodsDepartureDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_GB")
        formatter.dateFormat = "yyyy/MM/dd"

        return formatter
    }()
}

public extension NumberFormatter {
    static let defaultCurrencyFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.locale = Locale.current
        return formatter
    }()

    static let leftHandCurrencyFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.locale = Locale.init(identifier: "en_GBP")
        return formatter
    }()
}

extension Dictionary {
    func value<T>(forKeys keys: [Key]) -> T? {
        for key in keys {
            if let value = self[key] as? T {
                return value
            }
        }

        return nil
    }
}

public extension String {
    func htmlStripped() -> String {
        self.replacingOccurrences(of: "<[^>]+>", with: "", options: String.CompareOptions.regularExpression, range: nil)
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .replacingOccurrences(of: "&amp;", with: "&")
            .trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    }
}

public enum CustomDecodingError: Error {
	case keysNotFound
}

public extension KeyedDecodingContainerProtocol {
	func decode<T: Codable>(_ type: T.Type, forKeys keys: [Self.Key]) throws -> T {
		for key in keys {
			if let value = try? decode(type, forKey: key) {
				return value
			}
		}

		throw CustomDecodingError.keysNotFound
	}
}

public struct AdditionalGuest: Equatable {
    public let title: String
    public let firstName: String
    public let lastName: String
    public let nationality: String?
    public let email: String?

    public init(
        title: String,
        firstName: String,
        lastName: String,
        nationality: String?,
        email: String?
    ) {
		self.title = title
		self.firstName = firstName
		self.lastName = lastName
		self.nationality = nationality
		self.email = email
	}
}

private enum AdditionalGuestError: LocalizedError {
	case missingTitle
	case missingFirstName
	case missingLastName
	case missingEmail
	case missingNationality

	var errorDescription: String? { String(describing: self)	}
}

public extension AdditionalGuest {
	init(dictionary: PIDictionary) throws {
		guard let title = dictionary["title"] as? String else { throw AdditionalGuestError.missingTitle }
		guard let firstName = dictionary["firstName"] as? String else { throw AdditionalGuestError.missingFirstName }
		guard let lastName = dictionary["lastName"] as? String else { throw AdditionalGuestError.missingLastName }

		if let country = dictionary["nationality"] as? Country, let nationality = country.code {
			self.nationality = nationality
		} else {
			self.nationality = dictionary["nationality"] as? String
        }

		self.title = title
		self.firstName = firstName
		self.lastName = lastName
		self.email = dictionary["email"] as? String
	}

	var dictionary: PIDictionary {
		[
			"title": title,
			"firstName": firstName,
			"lastName": lastName,
			"nationality": nationality ?? "",
			"email": email ?? ""
		]
	}
}

public extension UIImageView {
    func setImage(with url: URL, transition: Bool = false, completion: ((Error?) -> Void)? = nil) {
		let imageTransition: UIImageView.ImageTransition = transition ? .crossDissolve(0.28) : .noTransition

        af.setImage(withURL: url, imageTransition: imageTransition) { response in
            completion?(response.error)
		}
	}

    func cancelImageRequest() {
        af.cancelImageRequest()
	}
}

public func printDev(_ object: Any...) {
    #if DEV
    for item in object {
        Swift.print(item)
    }
    #endif
}
