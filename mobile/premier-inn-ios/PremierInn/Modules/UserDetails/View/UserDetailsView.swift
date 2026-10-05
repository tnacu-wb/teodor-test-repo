//
//  UserDetailsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

/**
 The search term to pre-fill the search box and automatically search with
 */
typealias SearchTerm = String

protocol UserDetailsPresenterProtocol {
	var user: User? { get }
	var rooms: [Room] { get }
	var bookerIsStaying: Bool { get }
	var purpose: TripPurpose? { get set }
    var cityTaxRequired: Bool { get }
    var purposeMessages: [TripPurposeMessageModel]? { get }
    var firstRoomConfig: RoomConfig? { get }

    func viewIsReady()
    func salutationRowDidTap(indexPath: IndexPath)
    func submitButtonDidTap()
    func didTapDeleteAccount()
    func bookerIsStayingDidChange(isStaying: Bool)
	func loginButtonDidTap()
	func setSalutation(_ salutation: String?, at indexPath: IndexPath)
    func countryChanged(for: String, email: String)
    func updateNewsletterRow(email: String, country: String)
    func updatePurpose(_ purpose: TripPurposeSelections?)
    func userDetailsDidChange()
}

class UserDetailsViewController: FormekaViewController {
    private enum ViewConstants {
        static let firstRoomSuffix = "0"
    }

    private(set) var addressSectionView: AddressSectionView?

    override var screenName: String {
        tabBarController?.tabBar.isHidden ?? false ? PIAnalytics.StateNames.bookerDetails : PIAnalytics.StateNames
            .changeAddress
    }
    override var screenType: String {
        tabBarController?.tabBar.isHidden ?? false ? PIAnalytics.StateTypes.bookingFlow : PIAnalytics.StateTypes.myPI
    }
    override var customParameters: [String: Any]? {
        var params: PIDictionary = [PIAnalytics.Keys.productString: ";\(BookingDetails.sharedInstance.hotel?.code ?? "")"]
        if tabBarController?.tabBar.isHidden ?? false {
            params[PIAnalytics.Keys.eventsString] = "scAdd"

            if let user = UserSessionManager.sharedInstance.currentUser {
                params[PIAnalytics.Keys.bfSuppressMarketingBox] = "\(user.suppressMarketingBox)"
                params[PIAnalytics.Keys.bfOptedInToMarketing] = "\(user.optedIn(for: .premierInn) ?? false)"
            }
        }

        if let promotionsAnalyticsData = AnalyticsManager.shared.getPromotionsAnalyticsDict(with: .sharedInstance) {
            params.mergePreferNew(promotionsAnalyticsData)
        }

        return params
    }

    var presenter: UserDetailsPresenterProtocol?
    var addressFooterModel: FormekaModelHeaderFooter?
    var shouldShowAddAddressManuallyFooter: Bool? {
        didSet {
            addressFooterModel?.height = shouldShowAddAddressManuallyFooter ?? false ? 70 : 0
        }
    }
    var persistedCompanyName: String?

    override func viewDidLoad() {
        super.viewDidLoad()

		if table != nil {
			table.backgroundColor = .ColourLD6
			table.separatorColor = .ColourLD3
            table.accessibilityIdentifier = "tableView"
            table.estimatedRowHeight = 150

			registerTableElements()
		}

        view.backgroundColor = .ColourLD6

		shouldShowAddAddressManuallyFooter = presenter?.user?.address == nil

        presenter?.viewIsReady()

        navigationItem.titleView?.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.userDetailsPageTitle
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)
    }

	override func viewDidLayoutSubviews() {
		super.viewDidLayoutSubviews()

		table.contentInset.bottom = view.safeAreaInsets.bottom
		table.verticalScrollIndicatorInsets.bottom = view.safeAreaInsets.bottom
	}

    private func registerTableElements() {
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaTextFieldPasswordCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: FormekaLoginRequestCell.self)
        table.registerCellNib(with: FormekaPostCodeCell.self)
        table.registerCellNib(with: FormekaSegmentedControlCell.self)
        table.registerCellNib(with: CreditCardsListCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: TwoButtonsCell.self)
        table.registerCellNib(with: SwitchCell.self)
        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellClass(with: RadioButtonContainerCell.self)
        table.registerCellClass(with: SimpleSeparatorsCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

    @objc private func cancelButtonDidTap() {
        dismiss(animated: true)
    }


    func userDidChange() {
        shouldShowAddAddressManuallyFooter = presenter?.user?.address == nil
    }

    func reloadMarketingValue(model: UserMarketingModel) {
        if let indexPath = self.viewModel?.indexPath(forRowNamed: Step1Row.marketing.rawValue) {
            viewModel?.row(at: indexPath)?.value = model.isActive
        }
    }

    func updateMarketingSection(model: UserMarketingModel?) {
        guard let table = table,
              let viewModel = viewModel else { return }

        if let model {
            if let indexPath = viewModel.indexPath(forRowNamed: Step1Row.submitButton.rawValue),
               self.viewModel?.indexPath(forRowNamed: Step1Row.marketing.rawValue) == nil {
                viewModel.add(section: marketingSection(with: model), index: indexPath.section)
                table.insertSections([indexPath.section], with: .automatic)
            }
            reloadMarketingValue(model: model)
        } else {
            if let indexPath = viewModel.remove(rowNamed: Step1Row.marketing.rawValue) {
                table.deleteRows(at: [indexPath], with: .automatic)
            }
        }
    }

    func updateTableWithoutAnimation() {
        UIView.performWithoutAnimation {
            table.beginUpdates()
            table.endUpdates()
        }
    }
}

extension UserDetailsViewController: UserDetailsViewProtocol {
    var parentNavigationController: UINavigationController? {
        navigationController
    }

    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?) {
        showErrorAlertWith(title: title, message: message, error: error, handler: handler)
    }

    func showErrorMessage(title: String, error: Error) {
		showErrorAlertWith(title: title, error: error)
    }

    func showErrorFor(row: FormekaModelRow) {
        scrollAndFocus(at: viewModel?.indexPath(for: row))
    }

    func addAddressManually() {
        addressSectionView?.addAddressManually()
    }
    func validateViewModel() throws {
        try viewModel?.validate()
    }

    func getViewModelValues() -> JsonDictionary? {
        viewModel?.values
    }

    func getErrorForRowTagged(_ tag: String) -> Error? {
        guard let purposeRow = viewModel?.row(named: GuestDetailsRow.purpose.rawValue) else { return nil }

        return RowValidatorError(row: purposeRow, error: ValidationError.valueRequired(PILocalizedString("trip purpose")))
    }

    func setSalutation(_ salutation: String?, indexPath: IndexPath) {
        guard let row = viewModel?.row(at: indexPath) else { return }
        row.value = salutation

        try? row.validate()

        if var cell = table.cellForRow(at: indexPath) as? FormekaErrorCell {
            cell.errorMessage = row.error?.localizedDescription
        }
    }

    func setSalutationForFirstGuest(_ salutation: String?) {
        viewModel?.row(named: Step1Row.salutation.rawValue + ViewConstants.firstRoomSuffix)?.value = salutation
    }

    var email: String {
        self.viewModel?.row(named: Step1Row.emailAddress.rawValue + "Booker")?.value as? String ?? ""
    }

    func reload() {
        table.reloadData()
    }

    func endEditing() {
        view.endEditing(true)
    }

    func showCancelButton() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }

    func setTitle(_ title: String?) {
        self.title = title
    }

    func loadMarketingModel(model: UserMarketingModel) {
        reloadMarketingValue(model: model)
    }

	func loadViewModel(conf: UserDetailsConfiguration) {
        addressSectionView = {
            let addressRequirements = AddressSectionRequirements(
                address: conf.user?.address,
                storedAddress: nil,
                shouldShowAddressSwitch: false,
                useStoredAddressSwitchDescription: nil,
                addressSwitchInitialState: false,
                shouldShowAddressForm: true,
                shouldShowHeader: true,
                shouldShowFooter: conf.user == nil,
                shouldShowAddressSummary: true
            )

            let view = AddressSectionRouter.buildSection(with: addressRequirements)
            view.parentFormekaViewController = self
            view.segmentedControlDelegate = self
            view.companyNameDelegate = self

            view.countryChanged = { countryCode in
                self.presenter?.countryChanged(for: countryCode, email: self.email)
            }

            return view
        }()

        viewModel = FormekaViewModel(sections: viewModelSections(conf: conf))
        viewModel?.delegate = self

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel
        }
    }

    func updateBookerStayer(isStaying: Bool) {
        viewModel?.row(named: Step1Row.bookerIsGuest.rawValue)?.value = !isStaying
        toggleFirstGuestUI()
        updateFirstGuestUI()
    }

    func toggleFormLock(locked: Bool, submitButtonTitle: String?) {
        self.isLocked = locked

        guard let cell: FormekaSubmitButtonCell = viewModel?.cell(forRowNamed: Step1Row.submitButton.rawValue, table: table)
            else { return }

        cell.button.setTitle(locked ? nil : submitButtonTitle, for: .normal)

        if locked {
            cell.activityIndicator.startAnimating()
        } else {
            cell.activityIndicator.stopAnimating()
        }
    }
}

extension UserDetailsViewController {
    func handleEmailEndEditing(userSuffix: String) {
        guard let viewModel = self.viewModel else {
            return
        }

        let emailRow = Step1Row.emailAddress.rawValue + userSuffix
        let countryRow = CountryActionableRow.country.rawValue

        if let email = viewModel.row(named: emailRow)?.value as? String,
           let country = viewModel.row(named: countryRow)?.value as? Country {
            presenter?.updateNewsletterRow(email: email, country: country.isoCode)
        }

        updateFirstGuestUI()
        presenter?.userDetailsDidChange()
    }
}

// MARK: - Show/Hide First Room Logic

extension UserDetailsViewController {
    func updateFirstGuestUI() {
        guard let title = viewModel?.row(named: Step1Row.salutation.rawValue + Constants.bookerSuffix)?.value as? String,
              let firstName = viewModel?.row(named: Step1Row.firstName.rawValue + Constants.bookerSuffix)?.value as? String,
              let lastName = viewModel?.row(named: Step1Row.lastName.rawValue + Constants.bookerSuffix)?.value as? String,
              let emailAddress = viewModel?.row(named: Step1Row.emailAddress.rawValue + Constants.bookerSuffix)?
              .value as? String else {
            return
        }

        let user = try? User(title: title, firstName: firstName, lastName: lastName)
        user?.emailAddress = emailAddress

        if let indexPath = viewModel?.indexPath(forRowNamed: Step1Row.guestSummary.rawValue) {
            viewModel?.row(at: indexPath)?.value = user
            table.reloadRows(at: [indexPath], with: .automatic)
        }
    }

    func reloadFirstRoomSection(shouldShow: Bool) {
        let isFirstRoomCurrentlyShowing = checkIfFirstRoomSectionExists()
        guard isFirstRoomCurrentlyShowing != shouldShow else {
            return
        }

        if shouldShow {
            insertFirstRoomSection()
        } else {
            removeFirstRoomSection()
        }
    }

    private func checkIfFirstRoomSectionExists() -> Bool {
        viewModel?.indexPath(forRowNamed: Step1Row.guestSummary.rawValue) != nil ||
        viewModel?.indexPath(forRowNamed: Step1Row.firstName.rawValue + ViewConstants.firstRoomSuffix) != nil
    }

    private func insertFirstRoomSection() {
        guard let firstRoomIndex = firstRoomInsertSectionIndex(),
              let roomConfig = presenter?.firstRoomConfig else {
            return
        }

        let section = makeFirstRoomSection(from: roomConfig)

        viewModel?.add(section: section, index: firstRoomIndex)
        table.insertSections(IndexSet(integer: firstRoomIndex), with: .automatic)
        updateFirstGuestUI()
    }

    private func removeFirstRoomSection() {
        let sectionIndex = viewModel?.indexPath(forRowNamed: Step1Row.guestSummary.rawValue)?.section ??
        viewModel?.indexPath(forRowNamed: Step1Row.firstName.rawValue + ViewConstants.firstRoomSuffix)?.section

        guard let sectionIndex else {
            return
        }

        viewModel?.remove(sectionAtIndex: sectionIndex)
        table.deleteSections(IndexSet(integer: sectionIndex), with: .automatic)
    }

    private func toggleFirstGuestUI() {
        let currentSectionIndex =
            viewModel?.indexPath(forRowNamed: Step1Row.guestSummary.rawValue)?.section ??
            viewModel?.indexPath(forRowNamed: Step1Row.firstName.rawValue + ViewConstants.firstRoomSuffix)?.section

        guard let currentSectionIndex,
              let roomConfig = presenter?.firstRoomConfig else {
            return
        }

        let section = makeFirstRoomSection(from: roomConfig)

        viewModel?.remove(sectionAtIndex: currentSectionIndex)
        viewModel?.add(section: section, index: currentSectionIndex)
        table.reloadSections([currentSectionIndex], with: .automatic)
    }

    private func firstRoomInsertSectionIndex() -> Int? {
        guard let viewModel else {
            return nil
        }

        let bookerIsGuestRow = Step1Row.bookerIsGuest.rawValue
        let purposeRow = GuestDetailsRow.purpose.rawValue

        if let stayerSection = viewModel.indexPath(
            forRowNamed: bookerIsGuestRow
        )?.section {
            return stayerSection + 1
        }

        if let purposeSection = viewModel.indexPath(forRowNamed: purposeRow)?.section {
            return purposeSection
        }

        return nil
    }

    private func makeFirstRoomSection(from roomConfig: RoomConfig) -> FormekaModelSection {
        if roomConfig.shouldSummarise {
            return userSummarySection(
                user: roomConfig.user,
                headerTitle: roomConfig.roomTitle,
                footerTitle: nil,
                footerHeight: roomConfig.footerHeight
            )
        }

        let userConf = UserSectionConf(
            userTitle: roomConfig.user?.title,
            userFirstName: roomConfig.user?.firstName,
            userLastName: roomConfig.user?.lastName,
            userContactNumber: roomConfig.user?.contactNumber,
            userEmail: roomConfig.user?.emailAddress,
            userCountry: roomConfig.user?.country,
            userPassport: nil,
            suffix: ViewConstants.firstRoomSuffix,
            headerTitle: roomConfig.roomTitle,
            headerHeight: 70,
            footerHeight: roomConfig.footerHeight,
            showCountry: false,
            emailIsOptional: !roomConfig.isEmailRequired,
            shouldShowGdprWarning: true,
            accessibilityIdentifiers: createFirstRoomAccessibilityIdentifiers()
        )

        return userSection(conf: userConf)
    }

    private func createFirstRoomAccessibilityIdentifiers() -> GuestAccessibilityIdentifiers {
        GuestAccessibilityIdentifiers(
            header: String(format: AccessibilityIdentifiers.UserDetails.leadRoomHeaderFormat, 0),
            titleLabel: String(format: AccessibilityIdentifiers.UserDetails.leadTitleLabelFormat, 0),
            titleInput: AccessibilityIdentifiers.UserDetails.leadTitleInput,
            firstNameLabel: String(format: AccessibilityIdentifiers.UserDetails.leadFirstNameLabelFormat, 0),
            firstNameInput: AccessibilityIdentifiers.UserDetails.leadFirstNameInput,
            lastNameLabel: String(format: AccessibilityIdentifiers.UserDetails.leadLastNameLabelFormat, 0),
            lastNameInput: AccessibilityIdentifiers.UserDetails.leadLastNameInput,
            emailLabel: String(format: AccessibilityIdentifiers.UserDetails.leadEmailLabelFormat, 0),
            emailInput: AccessibilityIdentifiers.UserDetails.leadEmailInput,
            contactNumberLabel: nil,
            contactNumberInput: nil
        )
    }
}
