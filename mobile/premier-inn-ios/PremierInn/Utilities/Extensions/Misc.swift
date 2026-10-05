//
//  Misc.swift
//  PremierInn
//
//  Created by Marcello Mascia on 15/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import MapKit
import SimpleNetwork
import Formeka
import SafariServices
import CoreImage.CIFilterBuiltins

public func PILocalizedString(_ key: String, comment: String = "") -> String {
    guard let string = SettingsManager.sharedInstance.cmsStrings[key] else {
        return NSLocalizedString(key, comment: comment)
    }

    return string
}

public func printDev(_ object: Any...) {
    #if DEV
    for item in object {
        Swift.print(item)
    }
    #endif
}

extension ClosedRange {
    func clamp(_ value: Bound) -> Bound {
        lowerBound > value ? lowerBound : upperBound < value ? upperBound : value
    }
}

func linearInterpolation(start: CGFloat, end: CGFloat, percent: CGFloat) -> CGFloat {
    start + percent * (end - start)
}

func linearInterpolation(value: Double, lower: Double, upper: Double) -> Double {
    lower + value * (upper - lower)
}


func generateQRCode(from string: String) -> UIImage {
    let context = CIContext()
    let filter = CIFilter.qrCodeGenerator()

    guard let colorFilter = CIFilter(name: "CIFalseColor") else { return UIImage() }

    filter.setValue(Data(string.utf8), forKey: "inputMessage")
    filter.setValue("H", forKey: "inputCorrectionLevel")

    colorFilter.setValue(filter.outputImage, forKey: "inputImage")
    colorFilter.setValue(CIColor(color: UIColor.BaseWhite), forKey: "inputColor1")
    colorFilter.setValue(CIColor(color: UIColor.BaseBlack), forKey: "inputColor0")

    if let outputImage = colorFilter.outputImage, let cgimg = context.createCGImage(outputImage, from: outputImage.extent) {
        return UIImage(cgImage: cgimg)
    }
    return UIImage()
}

func clamp<T: Comparable>(value: T, lower: T, upper: T) -> T {
    min(max(value, lower), upper)
}

extension CGFloat {
	var degreesToRadians: CGFloat { self * CGFloat.pi / 180 }
	var radiansToDegrees: CGFloat { self * 180 / CGFloat.pi }
    var halved: CGFloat { self * 0.5 }
    var doubled: CGFloat { self * 2 }
}

extension MKMapView {
    typealias ReusableIdentifer = String

    func fitAnnotations(_ annotations: [MKAnnotation], inset: UIEdgeInsets, animated: Bool) {
		let rect: MKMapRect = {
			if annotations.count == 1, let annotation = annotations.first {
				return MKCoordinateRegion
				    .init(center: annotation.coordinate, latitudinalMeters: 500, longitudinalMeters: 500).mapRect
			}

			return annotations.reduce(MKMapRect.null) { (rect, annotation) -> MKMapRect in
				let annotationPoint = MKMapPoint.init(annotation.coordinate)
				let pointRect = MKMapRect.init(
				    x: annotationPoint.x,
				    y: annotationPoint.y,
				    width: Constants.mapRectPointSideLength,
				    height: Constants.mapRectPointSideLength
				)

				return rect.union(pointRect)
			}
		}()

		setVisibleMapRect(rect, edgePadding: inset, animated: animated)
	}

    func isAnnotationVisible(annotation: MKAnnotation) -> Bool {
        let visibleAnnotations = annotations(in: visibleMapRect)

        switch annotation {
        case let annotation as AnyHashable:
            return visibleAnnotations.contains(annotation)
        default:
            break
        }

        return false
    }

    class func region(for annotations: [MKAnnotation]) -> MKCoordinateRegion {
        guard annotations.isNotEmpty else { return MKCoordinateRegion() }

        var topLeftCoord = CLLocationCoordinate2D(latitude: -90, longitude: 180)
        var bottomRightCoord = CLLocationCoordinate2D(latitude: 90, longitude: -180)

        for annotation in annotations {
            topLeftCoord.longitude = fmin(topLeftCoord.longitude, annotation.coordinate.longitude)
            topLeftCoord.latitude = fmax(topLeftCoord.latitude, annotation.coordinate.latitude)

            bottomRightCoord.longitude = fmax(bottomRightCoord.longitude, annotation.coordinate.longitude)
            bottomRightCoord.latitude = fmin(bottomRightCoord.latitude, annotation.coordinate.latitude)
        }

        var region = MKCoordinateRegion()
        region.center.latitude = topLeftCoord.latitude - (topLeftCoord.latitude - bottomRightCoord.latitude) * 0.5
        region.center.longitude = topLeftCoord.longitude + (bottomRightCoord.longitude - topLeftCoord.longitude) * 0.5

        region.span
            .latitudeDelta = min(
                10,
                fabs(topLeftCoord.latitude - bottomRightCoord.latitude) * 2.4
            ) // Add a little extra vertical space on the sides

        region.span
            .longitudeDelta = min(
                10,
                fabs(bottomRightCoord.longitude - topLeftCoord.longitude) *
            2.0
            ) // Add a little extra horizontal space on the sides

        return region
    }

    func dequeueReusableAnnotationView<T: MKAnnotationView>(
        with identifier: ReusableIdentifer?,
        for annotation: MKAnnotation
    ) -> T? {
        guard let identifier = identifier else { return nil }

        return dequeueReusableAnnotationView(withIdentifier: identifier, for: annotation) as? T
    }
}

extension MKCoordinateRegion {
    var mapRect: MKMapRect {
        let topLeft = CLLocationCoordinate2D(
            latitude: center.latitude + (span.latitudeDelta / 2),
            longitude: center.longitude - (span.longitudeDelta / 2)
        )

        let bottomRight = CLLocationCoordinate2D(
            latitude: center.latitude - (span.latitudeDelta / 2),
            longitude: center.longitude + (span.longitudeDelta / 2)
        )

        let a = MKMapPoint.init(topLeft)
        let b = MKMapPoint.init(bottomRight)

        return MKMapRect(
            origin: MKMapPoint(x: min(a.x, b.x), y: min(a.y, b.y)),
            size: MKMapSize(width: abs(a.x - b.x), height: abs(a.y - b.y))
        )
    }
}

extension LengthFormatter {
	func attributedString(
	    distance: CLLocationDistance,
	    unit: LengthFormatter.Unit,
	    suffix: String = "",
	    boldFont: UIFont? = nil,
	    regularFont: UIFont? = nil,
	    boldColor: UIColor? = nil,
	    regularColor: UIColor? = nil
	) -> NSAttributedString? {
        guard distance > 0 else { return nil }

		var boldAttributes: [NSAttributedString.Key: Any] = [:]
		if let boldFont = boldFont {
			boldAttributes[NSAttributedString.Key.font] = boldFont
		}
		if let boldColor = boldColor {
			boldAttributes[NSAttributedString.Key.foregroundColor] = boldColor
		}

		let attributedString = NSMutableAttributedString(
		    string: string(fromValue: distance, unit: unit),
		    attributes: boldAttributes
		)

		if suffix.isNotEmpty {
			var regularAttributes: [NSAttributedString.Key: Any] = [:]
			if let regularFont = regularFont {
				regularAttributes[NSAttributedString.Key.font] = regularFont
			}
			if let regularColor = regularColor {
				regularAttributes[NSAttributedString.Key.foregroundColor] = regularColor
			}

			attributedString.append(NSAttributedString(string: suffix, attributes: regularAttributes))
		}

		return attributedString
	}

    static let distanceFormatter: LengthFormatter = {
        let formatter = LengthFormatter()
        formatter.unitStyle = LanguageManager.supportedLanguage == .english ? .long : .medium
        return formatter
    }()
}

public extension UITableView {
    func registerCellNib(with type: UITableViewCell.Type) {
        let identifier = String(describing: type)

        register(UINib(nibName: identifier, bundle: nil), forCellReuseIdentifier: identifier)
    }

    func registerCellClass(with type: UITableViewCell.Type) {
        let identifier = String(describing: type)

        register(type, forCellReuseIdentifier: identifier)
    }

    func registerHeaderFooterNib(with type: UITableViewHeaderFooterView.Type) {
        let identifier = String(describing: type)

        register(UINib(nibName: identifier, bundle: nil), forHeaderFooterViewReuseIdentifier: identifier)
    }

    func headerFooterView<T: UITableViewHeaderFooterView>() -> T? {
        let identifier = String(describing: T.self)

        return dequeueReusableHeaderFooterView(withIdentifier: identifier) as? T
    }

    func dequeueCell<T: UITableViewCell>(for indexPath: IndexPath) -> T? {
        let identifier = String(describing: T.self)

        return dequeueReusableCell(withIdentifier: identifier, for: indexPath) as? T
    }

    func performAnimation(_ action: () -> Void, completion: (() -> Void)? = nil) {
		CATransaction.begin()
		beginUpdates()
		CATransaction.setCompletionBlock {
			completion?()
		}
		action()
		endUpdates()
		CATransaction.commit()
	}
}

public extension UICollectionView {
    func registerCellForNib(with type: UICollectionViewCell.Type) {
        let identifier = String(describing: type)

        register(UINib(nibName: identifier, bundle: nil), forCellWithReuseIdentifier: identifier)
    }

	func registerCellForClass(with type: UICollectionViewCell.Type) {
		let identifier = String(describing: type)

		register(type, forCellWithReuseIdentifier: identifier)
	}

    func registerSupplementaryViewNib(with type: UICollectionReusableView.Type, kind: String) {
        let identifier = String(describing: type)

        register(UINib(nibName: identifier, bundle: nil), forSupplementaryViewOfKind: kind, withReuseIdentifier: identifier)
    }

    func dequeueCell<T: UICollectionViewCell>(for indexPath: IndexPath) -> T? {
        dequeueReusableCell(withReuseIdentifier: String(describing: T.self), for: indexPath) as? T
    }
}

extension MKCoordinateRegion {
    var isValidFor🌍: Bool {
        guard -90.0...90.0 ~= center.latitude else { return false }
        guard -180.0...180.0 ~= center.longitude else { return false }
        guard span.latitudeDelta >= 0 else { return false }
        guard span.longitudeDelta >= 0 else { return false }

        return true
    }
}

extension MKMapView {
    static func validRegion(for annotations: [MKAnnotation]) -> MKCoordinateRegion? {
        let currentRegion = MKMapView.region(for: annotations)

        guard currentRegion.isValidFor🌍 else { return nil }

        return currentRegion
    }
}

extension UIImageView {
    func loadMapImage(with annotations: [MKAnnotation], completion: @escaping (_ image: UIImage?) -> Void) {
        guard let region = MKMapView.validRegion(for: annotations) else {
            completion(nil)
            return
        }

        let options = MKMapSnapshotter.Options()
        options.region = region
        options.mapType = .standard
        options.size = CGSize(width: frame.width, height: frame.height)
        options.scale = UIScreen.main.scale

        MKMapSnapshotter(options: options).start(with: DispatchQueue.main) { [weak self] snapshot, _ in
            let image = self?.renderImage(snapshot: snapshot, annotations: annotations)

            self?.image = image
            completion(image)
        }
    }

    private func renderImage(
        snapshot: MKMapSnapshotter.Snapshot?,
        annotations: [MKAnnotation],
        forceUseNamePin: Bool = true
    ) -> UIImage? {
        guard let image = snapshot?.image else { return nil }

        UIGraphicsBeginImageContextWithOptions(image.size, false, image.scale)
        image.draw(at: CGPoint.zero)

        for annotation in annotations {
            var title: String?
            if annotation is Suggestion {
                title = annotation.title ?? PILocalizedString("Your search")
            }
            draw(point: snapshot?.point(for: annotation.coordinate), title: title)
        }

        let finalImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return finalImage
    }

    private func draw(point: CGPoint?, title: String?) {
        if var point = point {
            let pin = title != nil ? PIAnnotationView(annotation: nil, reuseIdentifier: nil, title: title) :
                                     PIAnnotationView(annotation: nil, reuseIdentifier: nil, imageName: "locationPoint")
            point.x -= pin.bounds.width.halved
            point.y -= pin.bounds.height.halved
            point.x += pin.centerOffset.x
            point.y += pin.centerOffset.y

            pin.drawHierarchy(in: CGRect(origin: point, size: pin.bounds.size), afterScreenUpdates: true)
        }
    }
}

extension UIApplication {
    func currentWindow() -> UIWindow? {
        UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }.flatMap { $0.windows }
            .first(where: { $0.isKeyWindow })
    }

    class func topViewController(controller: UIViewController? = UIApplication.shared.currentWindow()?
        .rootViewController) -> UIViewController? {
        if let navigationController = controller as? UINavigationController {
            return topViewController(controller: navigationController.visibleViewController)
        }

        if let tabController = controller as? UITabBarController {
            if let selected = tabController.selectedViewController {
                return topViewController(controller: selected)
            }
        }

        if let presented = controller?.presentedViewController {
            return topViewController(controller: presented)
        }

        return controller
    }

    func openAppleMapsDirections(to hotel: Hotel) {
        guard let placeMark = hotel.placeMark else { return }

        let mapItem = MKMapItem(placemark: placeMark)

        MKMapItem.openMaps(
            with: [MKMapItem.forCurrentLocation(), mapItem],
            launchOptions: [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving]
        )
    }

    func openAppleMapsDirections(to placemark: MKPlacemark) {
//        guard let placeMark = hotel.placeMark else { return }

        let mapItem = MKMapItem(placemark: placemark)

        MKMapItem.openMaps(
            with: [MKMapItem.forCurrentLocation(), mapItem],
            launchOptions: [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving]
        )
    }

    func open(url: URL) {
        UIApplication.shared.open(url)
    }

    func openGoogleMapsDirections(to hotel: Hotel) {
        var urlComponents = URLComponents(url: Constants.googleMapsURL, resolvingAgainstBaseURL: false)
        urlComponents?.queryItems = [
            URLQueryItem(name: "saddr", value: ""),
            URLQueryItem(name: "daddr", value: "\(hotel.coordinate.latitude),\(hotel.coordinate.longitude)"),
            URLQueryItem(name: "directionsmode", value: "driving")
        ]

        if let url = urlComponents?.url {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }

    func openGoogleMapsDirections(to coordinate: CLLocationCoordinate2D) {
        var urlComponents = URLComponents(url: Constants.googleMapsURL, resolvingAgainstBaseURL: false)
        urlComponents?.queryItems = [
            URLQueryItem(name: "saddr", value: ""),
            URLQueryItem(name: "daddr", value: "\(coordinate.latitude),\(coordinate.longitude)"),
            URLQueryItem(name: "directionsmode", value: "driving")
        ]

        if let url = urlComponents?.url {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
}

extension TimeInterval {
	static let ocd: TimeInterval = 0.28

    var stringInMinutesSeconds: NSString {
        let ti = NSInteger(self)

        let seconds = ti % 60
        let minutes = (ti / 60) % 60

        return NSString(format: "%0.2d:%0.2d", minutes, seconds)
    }
}

extension UIViewController {
	func isModal() -> Bool {
        if presentingViewController?.presentedViewController == self { return true }

        if navigationController != nil && navigationController?.presentingViewController?
           .presentedViewController == navigationController && navigationController?.viewControllers
           .first == self { return true }

        if tabBarController?.presentingViewController is UITabBarController { return true }

        return false
	}
}

extension AddressError: @retroactive LocalizedError {
	public var errorDescription: String? {
        switch self {
        case .missingAddressDictionary:
            return PILocalizedString("addressErrorDataNotValid", comment: "Address Error: data not valid")
        }
    }
}

extension Country: @retroactive FormekaValue {
	public var displayName: String {
        guard let countryFlagEmoji = self.flagEmoji else { return name }

        return "\(countryFlagEmoji) \(name)"
    }

    public var displayNationality: String {
        guard let countryFlagEmoji = self.flagEmoji else { return nationality ?? name }

        return "\(countryFlagEmoji) \(nationality ?? name)"
    }
}

class CardExpiredDateValidator: Validator {
    override func validate(row: FormekaModelRow) throws {
        guard let value = row.value as? String else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(PILocalizedString("expiry date", comment: ""))
        ) }
        guard let cardExpiryDate = PaymentCard.date(parameter: value)?.endOfMonth() else { throw RowValidatorError(
            row: row,
            error: ValidationError.invalidDate
        ) }
        guard cardExpiryDate >= Date() else { throw RowValidatorError(row: row, error: ValidationError.invalidDate) }
    }
}

extension PaymentCardError: @retroactive LocalizedError {
	public var errorDescription: String? {
        switch self {
        case .missingCardDictionary:
            return PILocalizedString(
                "paymentCardErrorMissingCardDictionary",
                comment: "Payment card error: raw data not valid"
            )
        case .missingCardNumber:
            return PILocalizedString("paymentCardErrorMissingCardNumber", comment: "Payment card error: missing card number")
        case .wrongExpiryDateFormat:
            return PILocalizedString(
                "paymentCardErrorInvalidExpiryDate",
                comment: "Payment card error: expiry date not valid"
            )
        case .missingCardHolder:
            return PILocalizedString(
                "paymentCardErrorMissingCardHolder",
                comment: "Payment card error: missing card holder name"
            )
        case .missingCardCode:
            return PILocalizedString("paymentCardErrorMissingCardCode", comment: "Payment card error: missing card code")
        }
    }
}

extension BookingError: @retroactive LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .missingSessionIdentifier:
            return PILocalizedString(
                "bookingErrorMissingSessionIdentifier",
                comment: "Booking error: missing session identifier"
            )
        case .unexpectedSessionIdentifier:
            return PILocalizedString(
                "bookingErrorUnexpectedSessionIdentifier",
                comment: "Booking error: unexpected session identifier"
            )
        case .missingRate:
            return PILocalizedString("bookingErrorMissingRate", comment: "Booking error: unexpected session identifier")
        case .missingHotel:
            return PILocalizedString("bookingErrorMissingHotel", comment: "Booking error: missing hotel")
        case .missingRateRooms:
            return PILocalizedString("bookingErrorMissingRooms", comment: "Booking error: missing rooms")
        case .missingBooker:
            return PILocalizedString("bookingErrorMissingBooker", comment: "Booking error: missing booker")
        case .missingBookerEmail:
            return PILocalizedString("bookingErrorMissingEmail", comment: "Booking error: missing email")
        case .missingBookerContactNumber:
            return PILocalizedString(
                "bookingErrorMissingBookerContactNumber",
                comment: "Booking error: missing booker contact number"
            )
        case .missingPaymentCard:
            return PILocalizedString("bookingErrorMissingPaymentCard", comment: "Booking error: missing payment card")
        case .missingLeadGuest:
            return PILocalizedString("bookingErrorMissingLeadGuest", comment: "Booking error: missing lead guest")
        case .missingBookerAddress:
            return PILocalizedString("bookingErrorMissingBookerAddress", comment: "Booking error: missing booker address")
        case .missingCardAddress:
            return PILocalizedString(
                "bookingErrorMissingPaymentCardAddress",
                comment: "Booking error: missing payment card address"
            )
        case .missingHotelCode:
            return PILocalizedString("Missing hotel code", comment: "")
        }
    }
}

extension PaymentIntervalOption {
    var localizedString: String {
        switch self {
        case .now:
            return PILocalizedString("paymentOptionPayNow", comment: "Payment Interval Option: pay now")
        case .later:
            return PILocalizedString("paymentOptionPayOnArrival", comment: "Payment Interval Option: pay on arrival")
        case .rwc:
            return PILocalizedString("paymentOptionRWC", comment: "Payment Interval Option: Reserve without credit card")
        }
    }

    var textForReviewAndBookTotal: String {
        switch self {
        case .now:
            return PILocalizedString("toPayNow")
        case .later:
            return PILocalizedString("toPayOnArrival")
        case .rwc:
            return PILocalizedString("toReserveWithoutCard")
        }
    }
}

extension AddressType: @retroactive FormekaValue {
	public var displayName: String { self.rawValue }
}

extension MVTManager {
    enum GroupType: String {
        case control
        case variant
    }

    var groupType: GroupType {
        GroupType(rawValue: value(forTestWithIdentifier: Constants.mvtIdentifier) as? String ?? "") ?? .control
    }

    var urgencyMessagingGroupType: GroupType {
        let variant = value(forTestWithIdentifier: Constants.urgencyMessagingIdentifier) as? String ?? "control"
        return GroupType(rawValue: variant) ?? .control
    }
}

extension Optional {
	mutating func remove() -> Optional {
		let value = self
		self = .none
		return value
	}
}

extension CLLocationCoordinate2D {
	var isValid: Bool { CLLocationCoordinate2DIsValid(self) }

    public static var zero: CLLocationCoordinate2D { CLLocationCoordinate2D(latitude: 0, longitude: 0)}
}

extension HotelBrand {
    var infoSegments: [HotelInformationSegment] {
        switch self {
        case .hub:
            return [
                HotelInformationSegment(
                    title: PILocalizedString("hubStandardRoomTitle", comment: "Hub Standard Room information title"),
                    content: PILocalizedString(
                        "hubStandardRoomDescription",
                        comment: "Hub Standard room information description"
                    )
                ),
                HotelInformationSegment(
                    title: PILocalizedString("hubBiggerRoomTitle", comment: "Hub Bigger Room information title"),
                    content: PILocalizedString(
                        "hubBiggerRoomDescription",
                        comment: "Hub Bigger room information description"
                    )
                ),
                HotelInformationSegment(
                    title: PILocalizedString("hubAccessibleRoomTitle", comment: "Hub Accessible Room information title"),
                    content: PILocalizedString(
                        "hubAccessibleRoomDescription",
                        comment: "Hub Accessible room information description"
                    )
                )
            ]

        case .zip:
            return [
                HotelInformationSegment(
                    title: PILocalizedString("genericRoomTitle", comment: "Room information title"),
                    content: PILocalizedString("zipRoomDescription", comment: "Room information description")
                )
            ]

        default:
            return [
                HotelInformationSegment(
                    title: PILocalizedString("genericRoomTitle", comment: "Room information title"),
                    content: PILocalizedString("genericRoomDescription", comment: "Room information description")
                ),
                HotelInformationSegment(
                    title: PILocalizedString("genericBathroomTitle", comment: "Room information bathroom title"),
                    content: PILocalizedString(
                        "genericBathroomDescription",
                        comment: "Room information bathroom description"
                    )
                ),
                HotelInformationSegment(
                    title: PILocalizedString("genericWifiTitle", comment: "Room information wifi title"),
                    content: PILocalizedString("genericWifiDescription", comment: "Room information wifi description")
                )
            ]
        }
    }

    var segmentedControlBackgroundColor: UIColor {
        switch self {
        case .hub:
            return .TintD1
        default:
            return .whiteTwo
        }
    }

    var selectorColor: UIColor {
        switch self {
        case .hub:
            return .hubGreen
        default:
            return .Tint1
        }
    }

    var segmentedTextAttributes: [NSAttributedString.Key: Any] {
        [.font: UIFont.Body_Semibold(), .foregroundColor: UIColor.TintD1]
    }

    var segmentedTextNormalAttributes: [NSAttributedString.Key: Any] {
        switch self {
        case .hub:
            return [.font: UIFont.BodySmall(), .foregroundColor: UIColor.white]
        default:
            return [.font: UIFont.BodySmall(), .foregroundColor: UIColor.TintD1]
        }
    }

    var segmentedTextSelectedAttributes: [NSAttributedString.Key: Any] {
        switch self {
        case .hub:
            return [.font: UIFont.BodySmall_Semibold(), .foregroundColor: UIColor.white]
        default:
            return [.font: UIFont.BodySmall_Semibold(), .foregroundColor: UIColor.TintD1]
        }
    }
}

class TermsAndConditionsValidator: Validator {
    override public func validate(row: FormekaModelRow) throws {
        guard row.value as? Bool ?? false == true else {
            let error = ValidationError.customMessage(PILocalizedString("createAccountTermsAndConditionsNotAccepted"))

            throw RowValidatorError(row: row, error: error)
        }
    }
}

class DeleteAccountToggleValidator: Validator {
    override public func validate(row: FormekaModelRow) throws {
        guard row.value as? Bool ?? false == true else {
            let error = ValidationError.customMessage(PILocalizedString("deleteAccountToggleErrorString"))

            throw RowValidatorError(row: row, error: error)
        }
    }
}

class CustomRequiredValidator: Validator {
    let range: ClosedRange<Int> = 1...Int.max
    private var customValidationValue: String

    init(withCustomValidatorErrorValue customValidatorErrorValue: String) {
        customValidationValue = customValidatorErrorValue
    }

    override func validate(row: FormekaModelRow) throws {
        guard let value = row.value as? String else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(customValidationValue)
        ) }
        guard range.contains(value.count) else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(customValidationValue)
        ) }
    }
}

class CustomStringLengthValidator: Validator {
    private var customValidationValue: String
    let range: ClosedRange<Int>

    init(withCustomValidatorErrorValue customValidatorErrorValue: String, andRange range: ClosedRange<Int>) {
        self.customValidationValue = customValidatorErrorValue
        self.range = range
    }

    override func validate(row: FormekaModelRow) throws {
        guard let value = row.value as? String else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(customValidationValue)
        ) }
        guard range.contains(value.count) else { throw RowValidatorError(
            row: row,
            error: ValidationError.valueRequired(customValidationValue)
        ) }
    }
}

extension Array where Element: Hashable {
    func difference(from other: [Element]) -> [Element] {
        let thisSet = Set(self)
        let otherSet = Set(other)

        return Array(thisSet.symmetricDifference(otherSet))
    }
}

extension Array where Element: NSCopying {
    func copy() -> [Element] {
        self.compactMap { $0.copy(with: nil) as? Element }
    }
}

extension Array where Element == UpsellItem {
    var totalCost: Cost? {
        guard let currencyCode = first?.price.currencyCode else { return nil }
        let total = self.reduce(0.0, { $0 + $1.price.amount.doubleValue })

        return Cost(amount: total, currencyCode: currencyCode)
    }
}

extension Array where Element == Room {
    // this is only used in Amend currently
    func totalRoomsCost(is businessTrip: Bool) -> Double {
        let roomsCost: Double = reduce(0.0, { $0 + $1.amountToPay(is: businessTrip) })
        return roomsCost
    }
}

extension Array where Element == UpsellItem {
    // compare upsells for a room - can now have multiple upsells so check count first and then code and quantity
    func isEqual(rhs: [Element]?) -> Bool {
        guard let rhs else { return false }
        guard self.count == rhs.count else { return false }

        for item in self {
            guard let matchedMeal = rhs.first(where: { $0.id == item.id }),
                  matchedMeal.quantity == item.quantity else { return false }
        }

        return true
    }
}

extension Room {
    func amountToPay(is businessTrip: Bool) -> Double {
        if options != nil {
            // This is when we use this function from when we work out the amount to pay from a response from AvailbilityV2

            let selectedRoom = options?.first(where: { $0.lettingType == lettingType }) ?? options?.first

            // TODO: cityTax depends on flags - not just if a cityTax exists
            return ((selectedRoom?.totalCost?.amount.doubleValue ?? 0) -
                (businessTrip ? selectedRoom?.cityTax?.amount.doubleValue ?? 0 : 0))
        } else {
            // This is when we use this function from Reservation, Reservation does not have the options object.

            return ((totalCost?.amount.doubleValue ?? 0) - (businessTrip ? cityTax?.amount.doubleValue ?? 0 : 0))
        }
    }
}

extension RequestsManagerError: @retroactive LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .serverError(let dict):

            guard let codeString = dict?["code"] as? String else { return String(describing: self) }
            guard let code = Int(codeString) else { return String(describing: self) }

            switch code {
//            case 29:
//                return RegisterInteractorError.emailAlreadyRegistered.errorDescription
            default:
                guard let messages = dict?["details"] as? [String] else { return PILocalizedString(
                    "loginScreenGenericError",
                    comment: "Login screen: generic error"
                ) }

                return messages.joined(separator: ", ")
            }
        default:
            return String(describing: self)
        }
    }
}

extension ReservationsListViewController {
    static func shareDataWithTodayWidget() {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        var summaries: [Stay] {
            reservationsManager.items
        }

        let sharedUserDefaults = UserDefaults(suiteName: AppExtensionConstants.CurrentReservation.groupContainerName)

        guard summaries.isNotEmpty else {
            sharedUserDefaults?.removeObject(forKey: AppExtensionConstants.CurrentReservation.currentHotelKey)
            return
        }

        let activeSummaries = summaries.filter {
            guard let checkOutDate = $0.checkOutDate else { return false }
            guard $0.cancelled == false else { return false }

            return checkOutDate >= Date() || checkOutDate.isToday
        }

        let sortedSummaries = activeSummaries.sorted(by: { (summary1, summary2) -> Bool in
            if let date1 = summary1.arrivalDate, let date2 = summary2.arrivalDate {
                return date1 < date2
            }

            return false
        })

        guard let summary = sortedSummaries.first else {
            let dict: PIDictionary = [
                AppExtensionConstants.CurrentReservation.name: "",
                AppExtensionConstants.CurrentReservation.identifier: "",
                AppExtensionConstants.CurrentReservation.datesString: ""
            ]
            sharedUserDefaults?.set(dict, forKey: AppExtensionConstants.CurrentReservation.currentHotelKey)
            return
        }

        let dict: PIDictionary = [
            AppExtensionConstants.CurrentReservation.name: summary.hotelName,
            AppExtensionConstants.CurrentReservation.identifier: summary.identifier,
            AppExtensionConstants.CurrentReservation.datesString: summary.datesString
        ]
        sharedUserDefaults?.set(dict, forKey: AppExtensionConstants.CurrentReservation.currentHotelKey)
    }
}

extension Bool {
    mutating func reset() -> Bool {
        let value = self
        self = false
        return value
    }
}

extension Double {
    var stringForAnalyticsCost: String {
        String(format: "%.2f", self)
    }
}

typealias UIAlertActionAccessibilityIdentifierSetup = (labelText: String, accessibilityIdentifier: String)

extension UIAlertController {
    func setActionAccessibilityIdentifiers(with identifierSetups: [UIAlertActionAccessibilityIdentifierSetup]) {
        recursivelySetAccessibilityIdentifiers(in: view, with: identifierSetups)
    }

    private func recursivelySetAccessibilityIdentifiers(
        in view: UIView,
        with identifierSetups: [UIAlertActionAccessibilityIdentifierSetup]
    ) {
        for subView in view.subviews {
            switch subView {
            case let label as UILabel:
                if let accessIdentifierSetup = identifierSetups.first(where: { $0.labelText == label.text }) {
                    label.accessibilityIdentifier = accessIdentifierSetup.accessibilityIdentifier
                }
            default:
                recursivelySetAccessibilityIdentifiers(in: subView, with: identifierSetups)
            }
        }
    }
}

enum RefreshUserError: LocalizedError {
    case missingBusiness
    case missingStoredUsername
    case missingStoredCredentials
    case loginError

    var errorDescription: String? { String(describing: self)    }
}

extension UserSessionManager {
    private static let requestsManager = RequestsManager()

    // This should be modified once refresh has been implemented properly
    // Currently we need to login to get a fresh app token and get user to access new session-id
    func refreshUser(completion: @escaping (_ success: Bool, _ error: Error?) -> Void) {
        guard let isBusiness = UserSessionManager.sharedInstance.currentUser?.isBusiness else {
            completion(false, RefreshUserError.missingBusiness)
            return
        }

        let usernameKey: String = { isBusiness ? .storedBusinessUsernameKey : .storedUsernameKey }()

        guard let emailAddress = UserDefaults.standard.string(forKey: usernameKey) else {
            completion(false, RefreshUserError.missingStoredUsername)
            return
        }

        guard let credentials = User.storedCredentials(for: emailAddress, business: isBusiness) else {
            completion(false, RefreshUserError.missingStoredCredentials)
            return
        }

        UserSessionManager.requestsManager.login(
            withUsername: credentials.username,
            password: credentials.password,
            isBusiness: isBusiness
        ) { result in
            switch result {
            case .success:
                User.saveCredentials(credentials)
                completion(true, nil)

            case .failure:
                completion(false, RefreshUserError.loginError)
            }
        }
    }
}

extension AncillaryCloseOutItem: BookingDatesInfoProtocol {}

extension Int {
    var localisedString: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .none
        formatter.locale = Locale.current

        if let formattedNumber = formatter.string(from: NSNumber(value: self)) {
            return formattedNumber
        } else {
            return "\(self)"
        }
    }
}
