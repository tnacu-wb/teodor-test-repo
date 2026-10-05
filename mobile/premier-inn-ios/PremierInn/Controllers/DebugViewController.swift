//
//  DebugViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import MapKit
import Formeka
import SimpleNetwork

enum RouterType: String {
    case production
    case developmentGraphQL
    case uatGraphQL
    case qaGraphQLDit
    case qaGraphQLSit
    case demoGraphQL
    case preprodGraphQL
    case perfGraphQL
    case hulkGraphQL
    case wandaGraphQL

	var title: String {
        switch self {
        case .production:
            return "Production"
        case .developmentGraphQL:
            return "DEV⚛️"
        case .uatGraphQL:
            return "UAT⚛️"
        case .qaGraphQLDit:
            return "DIT⚛️🐛"
        case .qaGraphQLSit:
            return "SIT⚛️🐛💩"
        case .demoGraphQL:
            return "Demo⚛️"
        case .preprodGraphQL:
            return "Pre-Prod⚛️"
        case .perfGraphQL:
            return "Performance⚛️🐛"
        case .hulkGraphQL:
            return "Hulk⚛️"
        case .wandaGraphQL:
            return "Wanda⚛️"
        }
    }
    var subtitle: String? { nil }
	var accessoryType: UITableViewCell.AccessoryType {
        SettingsManager.sharedInstance.currentRouterType == self ? .checkmark : .none
    }
	var router: Router {
        switch self {
        case .production:
            return Router.production
        case .developmentGraphQL:
            return Router.developmentGraphQL
        case .uatGraphQL:
            return Router.uatGraphQL
        case .qaGraphQLDit:
            return Router.qaGraphQLDit
        case .qaGraphQLSit:
            return Router.qaGraphQLSit
        case .demoGraphQL:
            return Router.demoGraphQL
        case .preprodGraphQL:
            return Router.preprodGraphQL
        case .perfGraphQL:
            return Router.perfGraphQL
        case .hulkGraphQL:
            return Router.hulkGraphQL
        case .wandaGraphQL:
            return Router.wandaGraphQL
        }
	}
}

private struct DeepLink {
	var title: String
	var url: URL?
}

enum DebugRowTag: String {
    case appIncentiveSwitch
	case debugButtonOverlaySwitch
    case employeeOffer
}

class DebugViewController: BaseViewController {
    override var shouldShowReachability: Bool { false }

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 44
            table.backgroundColor = .whiteTwo
            table.tableFooterView = UIView()

            table.registerCellNib(with: DebugMenuFeatureCell.self)
        }
    }

    let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
    var viewModel: FormekaViewModel?

	private static let deepLinks: [DeepLink] = [
	    DeepLink(
	        title: "Hotel details + criteria",
	        url: URL(
	            string: "https://www.premierinn.com/gb/en/hotels/england/greater-london/london/london-blackfriars-fleet-street.html?ARRdd=05&ARRmm=12&ARRyyyy=2024&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB"
	        )
	    ),
	    DeepLink(
	        title: "Hotel details - dateless",
	        url: URL(
	            string: "https://www.premierinn.com/gb/en/hotels/england/greater-london/london/london-blackfriars-fleet-street.html?cid=GLBC_LONBLA"
	        )
	    ),
	    DeepLink(
	        title: "Search with EH3 9DG",
	        url: URL(
	            string: "https://www.premierinn.com/gb/en/search.html?searchModel.searchTerm=EH3%209DG&ARRdd=26&ARRmm=11&ARRyyyy=2018&NIGHTS=1&ROOMS=1&CHILD1=0&COT1=0&ADULT1=1&INTTYP1=DB&SMO1=NON&CID=TRA_UK_META-DESKTOP_EDIPTI&refid=WIIIqwokK2AAAmkH1isAAAA8"
	        )
	    ),
	    DeepLink(
	        title: "Search with NG1 5LT",
	        url: URL(
	            string: "https://www.premierinn.com/gb/en/search.html?searchModel.searchTerm=NG1%205LT&ARRdd=22&ARRmm=09&ARRyyyy=2018&NIGHTS=1&ROOMS=1&CHILD1=0&COT1=0&ADULT1=2&INTTYP1=DB&SMO1=NON&CID=SS_UK_Meta_Desktop_hotelWebsiteLink_NOTMTI_35d54716-df16-11e6-9d88-35af60f3e030&skyscanner_redirectid=NdVHFt8WEeadiDWvYPPgMA"
	        )
	    )
	]

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        title = "Debug"

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(userDidChange(notification:)),
            name: .userDidChange,
            object: nil
        )

        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .done,
            target: self,
            action: #selector(closeButtonDidTap)
        )

        let versionLabel = UILabel(frame: CGRect(x: 0, y: 0, width: 60, height: 28))
        versionLabel.text = versionText
        versionLabel.textColor = currentNavigationTheme.foregroundColor
        versionLabel.accessibilityIdentifier = AccessibilityIdentifiers.Debug.buildVersionLabel
        navigationItem.leftBarButtonItem = UIBarButtonItem(customView: versionLabel)

        loadViewModel()
    }

    internal func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())

        table.delegate = viewModel
        table.dataSource = viewModel
    }

    internal func reload() {
        loadViewModel()
        table.reloadData()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        if let selectedRows = table.indexPathForSelectedRow {
            table.deselectRow(at: selectedRows, animated: animated)
        }
    }

    @objc func userDidChange(notification: Notification) {
        reload()
    }

    @objc func closeButtonDidTap(_ sender: UIBarButtonItem) {
        dismiss(animated: true)
    }


    // MARK: - Helpers

    private var versionText: String {
        guard let infoDictionary = Bundle.main.infoDictionary,
              let versionText = infoDictionary["CFBundleShortVersionString"] as? String,
              let buildVersion = infoDictionary["CFBundleVersion"] as? String else {
            return ""
        }

        return "v\(versionText) (\(buildVersion))"
    }

    private func switchRow(tag: String, title: String?, subtitle: String?) -> FormekaModelRow {
        FormekaModelRow(tag: tag, cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: DebugMenuFeatureCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.selectionStyle = .none
            cell.titleLabel.text = title
            cell.subtitleLabel.text = subtitle
            cell.accessoryType = .none
            cell.activeSwitch.isHidden = false
            cell.activeSwitch.isOn = {
                switch DebugRowTag(rawValue: tag) {
                case DebugRowTag.appIncentiveSwitch?:
                    return SettingsManager.sharedInstance.isAppIncentiveEnabled
                case DebugRowTag.debugButtonOverlaySwitch?:
                    return SettingsManager.sharedInstance.shouldShowDebugButtonOverlay
                case .employeeOffer:
                    return SettingsManager.sharedInstance.enableEmployeeRates
                case .none:
                    return false
                }
            }()

            return cell
        })
    }

    private func regularRow(
        tag: String = "",
        title: String?,
        subtitle: String?,
        accessoryType: UITableViewCell.AccessoryType,
        didSelect: TableRowCellSelection?
    ) -> FormekaModelRow {
        FormekaModelRow(
            tag: tag,
            cellSetup: { indexPath, _, table in
                guard let cell: DebugMenuFeatureCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.selectionStyle = .default
                cell.titleLabel.text = title
                cell.subtitleLabel.text = subtitle
                cell.accessoryType = accessoryType
                cell.activeSwitch.isHidden = true

                return cell
            },
            didSelect: didSelect
        )
    }

    private func reservationRow(reservation: Stay) -> FormekaModelRow {
        let row = regularRow(
            tag: reservation.identifier,
            title: reservation.hotelName,
            subtitle: reservation.datesString,
            accessoryType: .none,
            didSelect: nil
        )
        row.editable = true
        row.deleteAction = { [weak self] _ in
            if let removedIndexPath = self?.viewModel?.remove(row: row) {
                self?.table.deleteRows(at: [removedIndexPath], with: .automatic)
                _ = self?.reservationsManager.remove(reservation)

                NotificationCenter.default.post(name: .reservationSummariesDidChange, object: nil)
            }
        }

        return row
    }

    private func header(height: CGFloat, title: String?) -> FormekaModelHeaderFooter {
		FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view = UITableViewHeaderFooterView(frame: CGRect(x: 0, y: 0, width: table.frame.width, height: height))

            let label = UILabel(frame: view.bounds.insetBy(dx: 15, dy: 0))
            label.textColor = .darkGray
            label.font = UIFont.systemFont(ofSize: 14)
            label.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            label.text = title

            view.addSubview(label)

            return view
        })
    }

    private func footer(height: CGFloat) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let view = UITableViewHeaderFooterView(frame: .zero)
            view.contentView.backgroundColor = .whiteTwo

            return view
        })
    }

    private func updateRouterType(with type: RouterType) {
		SettingsManager.sharedInstance.currentRouterType = type
        SettingsManager.sharedInstance.shouldAttemptAutoLogin = false

        UserSessionManager.sharedInstance.piUserLoggedOut()

		RequestsManager.removeStays()
        RequestsManager.setupCountries()

        dismiss(animated: true)
    }

    // MARK: - Sections

    private func viewModelSections() -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        sections.append(prodRoutersSection())
        sections.append(lowerRoutersSection())

        sections.append(controllersSection())
        sections.append(genericSection())
		sections.append(deepLinksSection())
        sections.append(reservationsSection())

        return sections
    }

    private func prodRoutersSection() -> FormekaModelSection {
        let routerTypes: [RouterType] = [
            .production,
            .hulkGraphQL,
            .wandaGraphQL
        ]

        let rows = routerTypes.map { type in
            regularRow(
                title: type.title,
                subtitle: type.subtitle,
                accessoryType: type.accessoryType,
                didSelect: { [unowned self] _, _ in
                updateRouterType(with: type)
            }
            )
        }

        return FormekaModelSection(
            header: header(height: 30, title: "Production/Live environment"),
            rows: rows,
            footer: footer(height: 20)
        )
    }

    private func lowerRoutersSection() -> FormekaModelSection {
        let routerTypes: [RouterType] = [
            .developmentGraphQL,
            .qaGraphQLDit,
            .uatGraphQL,
            .qaGraphQLSit,
            .perfGraphQL,
            .demoGraphQL,
            .preprodGraphQL
        ]

        let rows = routerTypes.map { type in
            regularRow(
                title: type.title,
                subtitle: type.subtitle,
                accessoryType: type.accessoryType,
                didSelect: { [unowned self] _, _ in
                updateRouterType(with: type)
            }
            )
        }

        return FormekaModelSection(
            header: header(height: 30, title: "Lower environments"),
            rows: rows,
            footer: footer(height: 20)
        )
    }

    private func genericSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(switchRow(
            tag: DebugRowTag.appIncentiveSwitch.rawValue,
            title: "Enable App Incentive",
            // swiftlint:disable:next line_length
            subtitle: "You can skip the deeplink by enabling this, and may disable it which would not be possible otherwise without uninstalling the app"
        ))

        rows.append(switchRow(
            tag: DebugRowTag.debugButtonOverlaySwitch.rawValue,
            title: "Debug Floating Button",
            subtitle: "Shows a floating debug button with the current webservice name"
        ))

        rows.append(switchRow(
            tag: DebugRowTag.employeeOffer.rawValue,
            title: "Enable Employee Offer",
            subtitle: "Switch for enabling and disabling employee offer"
        ))

        let resetEmployeeOfferRow = regularRow(
            title: "Reset Employee Offer",
            // swiftlint:disable:next line_length
            subtitle: "Disables and hides the employee offer functionality until it is enabled again via the deeplink/qr code",
            accessoryType: .none,
            didSelect: { _, _ in
                UserDefaults.standard.removeObject(forKey: Constants.employeeRatesKey)
                NotificationCenter.default.post(name: .userDidChange, object: nil)
            }
        )

        rows.append(resetEmployeeOfferRow)

        let clearHasMadeBookingRow = regularRow(
            title: "Clear HasMadeBooking flag",
            subtitle: "(App Incentive) clears the flag to allow re-using the app incentive without uninstalling the app",
            accessoryType: .none,
            didSelect: { _, _ in
                SettingsManager.sharedInstance.hasMadeAppBooking = false
            }
        )

        rows.append(clearHasMadeBookingRow)

        if let fcmToken = AdobeCampaignManager.shared.fcmToken {
            let copyFCMTokenToClipboardRow = regularRow(
                title: "Copy FCM Token to clipboard",
                subtitle: fcmToken,
                accessoryType: .none,
                didSelect: { _, _ in
                UIPasteboard.general.string = fcmToken
            }
            )
            rows.append(copyFCMTokenToClipboardRow)
        }

        if let contactChannelId = UserSessionManager.sharedInstance.currentUser?.contactChannelId {
            let copyDeviceTokenToClipboardRow = regularRow(
                title: "Copy contact channel id to clipboard",
                subtitle: contactChannelId,
                accessoryType: .none,
                didSelect: { _, _ in
                UIPasteboard.general.string = contactChannelId
            }
            )
            rows.append(copyDeviceTokenToClipboardRow)
        }

        let simulateCrashRow = regularRow(
            title: "App Crash",
            subtitle: "Simulate an app crash",
            accessoryType: .none,
            didSelect: { _, _ in
            fatalError()
        }
        )
        rows.append(simulateCrashRow)

		return FormekaModelSection(header: header(height: 30, title: "Other stuff"), rows: rows, footer: footer(height: 20))
	}

	private func deepLinksSection() -> FormekaModelSection {
		var rows: [FormekaModelRow] = DebugViewController.deepLinks.compactMap { (deepLink) -> FormekaModelRow? in
			guard let url = deepLink.url else { return nil }

			return regularRow(title: deepLink.title, subtitle: nil, accessoryType: .none) { [unowned self] _, _ in
				dismiss(animated: true) {
					guard let shortcut = LinkHandler.appShortcut(for: url) else { return }

					LinkHandler.sharedInstance.activeAppShortcut = shortcut
				}
			}
		}

        rows.append(regularRow(title: "Near me", subtitle: nil, accessoryType: .none) { [unowned self] _, _ in
            dismiss(animated: true) {
                LinkHandler.sharedInstance.activeAppShortcut = AppShortcut.hotelNearMe
            }
        })

        rows.append(regularRow(title: "Near Glasgow", subtitle: nil, accessoryType: .none) { [unowned self] _, _ in
            dismiss(animated: true) {
                LinkHandler.sharedInstance.activeAppShortcut = AppShortcut.hotelsNearLocation(
                    suggestion: PISuggestion(dictionary: ["name": "Glasgow", "lat": 55.86425, "long": -4.25048]),
                    criteria: nil
                )
            }
        })

        rows.append(regularRow(title: "Home", subtitle: nil, accessoryType: .none) { [unowned self] _, _ in
            dismiss(animated: true) {
                LinkHandler.sharedInstance.activeAppShortcut = AppShortcut.landingScreen
            }
        })

        if let stay = reservationsManager.items.first {
            rows
                .append(regularRow(
                    title: "Reservation details: \(stay.identifier)",
                    subtitle: nil,
                    accessoryType: .none
                ) { [unowned self] _, _ in
                dismiss(animated: true) {
                    LinkHandler.sharedInstance.activeAppShortcut = AppShortcut
                        .reservationDetails(identifier: stay.identifier)
                }
            })
        }

        rows.append(regularRow(title: "Check in online", subtitle: nil, accessoryType: .none) { [unowned self] _, _ in
            dismiss(animated: true) {
                LinkHandler.sharedInstance.activeAppShortcut = AppShortcut.ciol(
                    arrivalDate: "2024-10-30",
                    reservationNumber: "FAKEAKU1099261",
                    lastName: "Testus"
                )
            }
        })

        return FormekaModelSection(header: header(height: 30, title: "Deep links"), rows: rows, footer: footer(height: 20))
    }

    private func controllersSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

		rows
		    .append(regularRow(
		        title: "Review And Book",
		        subtitle: "",
		        accessoryType: .disclosureIndicator
		    ) { [weak self] _, _ in
			let bookingDetails = BookingDetails()
            bookingDetails.basketReference = "FAKEBASKETREFERENCE"
            let paymentCard = PaymentCard(
                cardNumber: "4444333322221111",
                expiryDate: Date() + 111111111,
                cardholderName: "Mr McTester",
                cardName: "Visa",
                cardCode: SimpleNetwork.Constants.PaymentCardCodes.businessCard
//                cardCode: "VI"
            )
            bookingDetails.paymentMethod = PaymentMethod(type: .newCreditDebitCard, method: paymentCard)
            bookingDetails.booker = {
                let user = try? User(title: "Mr", firstName: "Nick", lastName: "Smith")
                user?.emailAddress = "marcellomascia@mac.com"
                user?.contactNumber = "123323423423"
                user?.address = try? Address(dictionary: [
                    "addressline1": "Royal Victoria Dock",
                    "addressline2": "2 Festoon Way",
                    "addressline3": "London",
                    "countryCode": "GB",
                    "postcode": "E16 1SJ"
                ])

                return user
            }()

            let acceptedCreditCards: [PIDictionary] = [
                [
                    "code": "VI",
                    "feeAmount": "1.00",
                    "feeCurrency": "£",
                    "paymentOnly": false,
                    "listOrder": "3",
                    "name": "Visa",
                    "schemeLogo": "/content/dam/global/booking/Mastercard.jpg"
                ],
                [
                    "code": "AM",
                    "feeAmount": "99.99",
                    "feeCurrency": "£",
                    "paymentOnly": false,
                    "listOrder": "4",
                    "name": "Amex",
                    "schemeLogo": "/content/dam/global/booking/Mastercard.jpg"
                ]
            ]

            bookingDetails.hotel = try? Hotel(dictionary: [
                "name": "Fake Hotel",
                "code": "LONLEI",
                "prepaymentAllowed": true,
                "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
                "images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]],
                "acceptedCreditCards": acceptedCreditCards
            ])

            let totalCost: [String: Any] = [
                "amount": "123.0",
                "currency": "GBP"
            ]
            bookingDetails.rate = Rate(dictionary: [
                "classification": "A",
                "code": "RTX199",
                "totalCost": totalCost
            ])
            bookingDetails.criteria = {
                var criteria = Criteria()
                criteria.nights = 1
                criteria.rooms = {
                    let totalCost: [String: Any] = ["amount": 123.0, "currency": "GBP"]
                    let price: [String: Any] = ["amount": 123.0, "currency": "GBP"]
                    let dailyRate: [String: Any] = ["date": "", "price": price]

                    let room1 = Room(dictionary: [
                        "adults": 1,
                        "children": 0,
                        "cotRequired": false,
                        "lettingType": "ASDASD",
                        "totalCost": totalCost,
                        "dailyRates": [dailyRate]
                    ])
                    room1.leadGuest = try? User(title: "Mr", firstName: "Test", lastName: "McTester")
                    room1.adults = 2
                    room1.children = 2

                    return [room1]
                }()

                return criteria
            }()

			let controller = ReviewAndBookRouter.build(with: bookingDetails)

			self?.navigationController?.pushViewController(controller, animated: true)
		})

		return FormekaModelSection(
		    header: header(height: 30, title: "View Controllers"),
		    rows: rows,
		    footer: footer(height: 20)
		)
    }

    private func reservationsSection() -> FormekaModelSection {
        FormekaModelSection(
            header: header(height: 30, title: "Local reservations"),
            rows: reservationsManager.items.map { reservationRow(reservation: $0) },
            footer: footer(height: 20)
        )
    }
}

extension DebugViewController: DebugMenuFeatureCellDelegate {
    func featureCellActiveSwitchDidToggle(_ sender: DebugMenuFeatureCell, active: Bool) {
        guard let indexPath = table.indexPath(for: sender) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        switch DebugRowTag(rawValue: row.tag) {
        case .appIncentiveSwitch?:
            SettingsManager.sharedInstance.isAppIncentiveEnabled = active
            _ = SettingsManager.sharedInstance.isAppIncentiveAvailable
        case .debugButtonOverlaySwitch?:
            SettingsManager.sharedInstance.shouldShowDebugButtonOverlay = active
        case .employeeOffer:
            SettingsManager.sharedInstance.enableEmployeeRates = active
        case .none:
            break
        }
    }
}
