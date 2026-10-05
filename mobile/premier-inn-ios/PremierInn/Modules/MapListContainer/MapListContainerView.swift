//
//  MapListContainerView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol MapListContainerPresenterProtocol {
    var condensedCriteriaTitle: NSAttributedString { get }
    var nameOfLocationTextForCurrentCriteria: String? { get }
    var dateTextForCurrentCriteriaSummary: String? {get}
    var roomsAndGuestsTextForCriteria: String? { get }
    var screenTitle: String? { get }
    var screenSubtitle: String? { get }
    var sortType: AvailabilitiesSorting { get }

    func viewIsReady(shouldFetchNearbyHotels: Bool)
    func presentErrorMessageOnListViewIfNeeded()
    func editButtonDidTap()
    func viewIsReady()
	func mapButtonDidTap()
    func backButtonDidTap()
    func comingBackFromHotelDetails()
    func sortDidChange(with sortingRule: AvailabilitiesSorting)
    func mapWillGoFullScreen()
    func mapDidGoFullScreen()
	func mapWillGoSmall()
	func mapDidGoSmall()
    func selectedListCell() -> VenueCell?
    func editGuestsButtonDidTap(sender: UIView)
    func editDatesButtonDidTap(sender: UIView)
    func editSuggestionButtonDidTap(sender: UIView)
    func setPreselectedHotel(with code: String)
    func selectedLocation()
    func selectedNights()
    func selectedGuests()
    func criteriaUpdated(to criteria: Criteria)
    func updatedSuggestion(to suggestion: Suggestion)
}

private enum RotationDirection {
	case clockwise
	case counterClockwise
}

private extension CGFloat {
	static let defaultCriteriaHeaderHeight: CGFloat = 88
	static let expandedCriteriaHeaderHeight: CGFloat = 200
	static let defaultSearchLocationLabelLeadingSpace: CGFloat = 21
	static let expandedSearchLocationLabelLeadingSpace: CGFloat = 12
	static let defaultSearchCriteriaLabelLeadingSpace: CGFloat = -28
	static let expandedSearchCriteriaLabelLeadingSpace: CGFloat = 12
}

class MapListContainerViewController: BaseViewController {
    override var screenName: String { PIAnalytics.StateNames.searchResultsMap }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }
    override var trackScreen: Bool { false }

    var presenter: MapListContainerPresenterProtocol?

    private var criteriaSummaryView: CriteriaSummaryView? {
        let view: CriteriaSummaryView? = .fromNib()

        if UIDevice.current.userInterfaceIdiom == .pad {
            view?.titleLabel.textAlignment = .center
            view?.subtitleLabel.textAlignment = .center
        }

        if let bounds = navigationController?.navigationBar.bounds {
            view?.frame = bounds
            view?.frame.size.height = 34 // Arbitrary size because it looks pretty
        }

        view?.titleLabel.text = presenter?.screenTitle
        view?.subtitleLabel.text = presenter?.screenSubtitle

        return view
    }

    var criteria: Criteria = BookingDetails.sharedInstance.criteria {
        didSet {
            BookingDetails.sharedInstance.criteria = criteria
        }
    }
    var updatedCriteria: Criteria?

    @IBOutlet weak var criteriaExpandWhereLabel: UILabel! {
        didSet {
            criteriaExpandWhereLabel.font = UIFont.BodySmall_Semibold()
            criteriaExpandWhereLabel.textColor = .TintD2
        }
    }
    @IBOutlet weak var criteriaExpandLocationLabel: UILabel! {
        didSet {
            criteriaExpandLocationLabel.font = UIFont.BodySmall()
            criteriaExpandLocationLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var criteriaDateTitleLabel: UILabel! {
        didSet {
            criteriaDateTitleLabel.font = UIFont.BodySmall_Semibold()
            criteriaDateTitleLabel.textColor = .TintD2
        }
    }
    @IBOutlet weak var criteriaDateSubtitleLabel: UILabel! {
        didSet {
            criteriaDateSubtitleLabel.font = UIFont.BodySmall()
            criteriaDateSubtitleLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var criteriaRoomsTitleLabel: UILabel! {
        didSet {
            criteriaRoomsTitleLabel.font = UIFont.BodySmall_Semibold()
            criteriaRoomsTitleLabel.textColor = .TintD2
        }
    }
    @IBOutlet weak var criteriaRoomSubtitleLabel: UILabel! {
        didSet {
            criteriaRoomSubtitleLabel.font = UIFont.BodySmall()
            criteriaRoomSubtitleLabel.textColor = .TintD1
        }
    }

    @IBOutlet weak var criteriaHeaderStackView: UIStackView!
    @IBOutlet weak var criteriaInformationsStackView: UIStackView!
    @IBOutlet weak var criteriaHeaderStackViewLeftLayout: NSLayoutConstraint!
    @IBOutlet weak var criteriaHeaderStackViewTopLayout: NSLayoutConstraint!
    @IBOutlet weak var changeButton: UIButton! {
        didSet {
            changeButton.setTitle(PILocalizedString("changeButtonMapCriteria"), for: .normal)
            changeButton.setTitleColor(.ColourDL5, for: .normal)
            changeButton.accessibilityIdentifier = "editSearch"
        }
    }

    @IBOutlet weak var criteriaTitleHeader: UILabel! {
        didSet {
            criteriaTitleHeader.font = .Body_Semibold()
        }
    }
    @IBOutlet weak var criteriaSubtitleHeader: UILabel! {
        didSet {
            criteriaSubtitleHeader.font = .Body()
        }
    }

    @IBOutlet weak var expandHeaderStackView: UIStackView!
    @IBOutlet weak var backButtonLayoutTop: NSLayoutConstraint!
    @IBOutlet weak var optionsView: UIView!
    @IBOutlet weak var priceSegmentedControl: UISegmentedControl! {
        didSet {
            priceSegmentedControl.setTitle(
                PILocalizedString("priceSegementedControlClosest", comment: "Sorting by distance button title"),
                forSegmentAt: 0
            )
            priceSegmentedControl.setTitle(
                PILocalizedString("priceSegementedControlCheapest", comment: "Sorting by price button title"),
                forSegmentAt: 1
            )
            priceSegmentedControl.tintColor = .TintD2
            priceSegmentedControl.setTitleTextAttributes(
                [NSAttributedString.Key.font: UIFont.Heading4_Semibold()],
                for: .normal
            )

            priceSegmentedControl.imageForSegment(at: 0)?.accessibilityLabel = AccessibilityIdentifiers.SearchResults
                .sortPriceSegment
            priceSegmentedControl.imageForSegment(at: 1)?.accessibilityLabel = AccessibilityIdentifiers.SearchResults
                .sortDistanceSegment
        }
    }
	@IBOutlet weak var mapToggleButton: UIButton! {
		didSet {
			mapToggleButton.setTitle(PILocalizedString("mapScreenTitle", comment: "Map Screen title"), for: .normal)
			mapToggleButton.titleLabel?.font = UIFont.Heading4_Semibold()
			mapToggleButton.setTitleColor(.TintD2, for: .normal)
			mapToggleButton.layer.cornerRadius = 5
			mapToggleButton.layer.borderWidth = 1
			mapToggleButton.layer.borderColor = UIColor.TintD2.cgColor
            mapToggleButton.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.mapButton
		}
	}
    @IBOutlet weak var mapContainerView: UIView! {
        didSet {
            mapContainerView.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.mapView
        }
    }
    @IBOutlet weak var listContainerView: UIView! {
        didSet {
            listContainerView.backgroundColor = .clear
        }
    }
    @IBOutlet weak var cardsContainerView: UIView! {
        didSet {
            cardsContainerView.backgroundColor = .clear
        }
    }
	@IBOutlet weak var criteriaScreenBlocker: UIView!
	@IBOutlet weak var criteriaHeaderHeight: NSLayoutConstraint!
    @IBOutlet weak var backButton: UIButton! {
        didSet {
            backButton.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.backButton
        }
    }

	@IBOutlet weak var currentLocationSection: UIView!
	@IBOutlet weak var dateSection: UIView!
	@IBOutlet weak var guestsAndRoomsSection: UIView!
	@IBOutlet weak var criteriaHeaderSection: UIView!
    @IBOutlet weak var criteriaHeaderBorderSection: UIView! {
        didSet {
            criteriaHeaderBorderSection.layer.borderWidth = 0.5
            criteriaHeaderBorderSection.layer.borderColor = UIColor.lightGray.cgColor
            criteriaHeaderBorderSection.layer.cornerRadius = 4.0
            criteriaHeaderBorderSection.layer.masksToBounds = true
            criteriaHeaderBorderSection.clipsToBounds = true
        }
    }
	@IBOutlet weak var cardsContainerViewHeight: NSLayoutConstraint!
	@IBOutlet weak var cardsContainerViewBottomHandle: NSLayoutConstraint!
    @IBOutlet weak var listTopHandle: NSLayoutConstraint!
	@IBOutlet weak var listBottomHandle: NSLayoutConstraint!
	@IBOutlet weak var optionsViewTopHandle: NSLayoutConstraint!

    // MARK: ℹ️🅿️🅰️ｄ buttons
    @IBOutlet weak var editGuestsButton: RoundedCornersButton? {
        didSet {
            editGuestsButton?.titleLabel?.font = .Action1()
            editGuestsButton?.setTitle(PILocalizedString("mapEditGuestsButtonTitle", comment: ""), for: .normal)
        }
    }
    @IBOutlet weak var editDatesButton: RoundedCornersButton? {
        didSet {
            editDatesButton?.titleLabel?.font = .Action1()
            editDatesButton?.setTitle(PILocalizedString("mapEditDatesButtonTitle", comment: ""), for: .normal)
        }
    }
    @IBOutlet weak var editPlaceButton: RoundedCornersButton? {
        didSet {
            editPlaceButton?.titleLabel?.font = .Action1()
            editPlaceButton?.setTitle(PILocalizedString("mapEditPlaceButtonTitle", comment: ""), for: .normal)
        }
    }

	private var priceSegmentedControlIsBeingToggledProgrammatically = false
    private var loadingView: LoadingView?
    private var canExpandCriteriaHeader = true
    private var originalBackImage: UIImage?
    private var criteriaHideGestureRecognizer: UITapGestureRecognizer?
    private var criteriaExpandGestureRecognizer: UITapGestureRecognizer?

    private lazy var statusBarUnderlay: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(view)

        view.leadingAnchor.constraint(equalTo: self.view.leadingAnchor).isActive = true
        view.topAnchor.constraint(equalTo: self.view.topAnchor).isActive = true
        view.trailingAnchor.constraint(equalTo: self.view.trailingAnchor).isActive = true
        view.bottomAnchor.constraint(equalTo: criteriaHeaderSection.topAnchor).isActive = true
        return view
    }()

    override func viewDidLoad() {
        super.viewDidLoad()

        var shouldPreFetch = false
        if UIDevice.current.userInterfaceIdiom == .pad && presenter == nil {
            shouldPreFetch = true
            MapListContainerRouter.buildMapListContainerView(with: self)
        }

        if cardsContainerViewHeight != nil {
            cardsContainerViewHeight.constant = Constants.cardCellSize.height + 20
        }

		if cardsContainerViewBottomHandle != nil {
            cardsContainerViewBottomHandle.constant = -cardsContainerView.frame.height - view.safeAreaInsets.bottom
        }

        cardsContainerView.isHidden = true
        presenter?.viewIsReady(shouldFetchNearbyHotels: shouldPreFetch)

        navigationItem.titleView = criteriaSummaryView

        if UIDevice.current.userInterfaceIdiom != .pad {
            navigationItem.rightBarButtonItem = UIBarButtonItem(
                barButtonSystemItem: .edit,
                target: self,
                action: #selector(editButtonDidTap)
            )
            navigationItem.setHidesBackButton(true, animated: false)
            navigationItem.leftBarButtonItem = UIBarButtonItem(
                image: #imageLiteral(resourceName: "back"),
                style: .plain,
                target: self,
                action: #selector(backButtonTapped)
            )

            navigationController?.interactivePopGestureRecognizer?.delegate = nil

            var backImage = #imageLiteral(resourceName: "arrowRight")
            originalBackImage = backImage

            if let backImageCGImage = backImage.cgImage {
                backImage = UIImage(cgImage: backImageCGImage, scale: 1.0, orientation: UIImage.Orientation.upMirrored)
                backImage = backImage.withRenderingMode(.alwaysTemplate)
                backButton.setImage(backImage, for: .normal)
            } else {
                backButton.setTitle("Back", for: .normal)
            }

            backButton.tintColor = .BasePurple
            backButton.addTarget(self, action: #selector(backButtonTapped), for: .touchUpInside)

            criteriaHideGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(hideCriteriaBlocker))
            criteriaExpandGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(tappedCriteriaButton))

            statusBarUnderlay.backgroundColor = .BasePurple

            setUpShadowsHeader()
            setUpCriteriaTitle()
            setUpCriteriaSubtitle()
            setUpNightsButtonTitle()
            setUpExpandCriteriaStackView()
            setUpRoomsAndGuestsTitle()

            slideOptionsViewInWithoutAnimation()

            if let tmpExpandGesture = criteriaExpandGestureRecognizer {
                criteriaHeaderStackView.addGestureRecognizer(tmpExpandGesture)
            }
            changeButton.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(tappedCriteriaButton)))

            currentLocationSection.addGestureRecognizer(UITapGestureRecognizer(
                target: self,
                action: #selector(selectedLocation)
            ))
            dateSection.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(selectedDate)))
            guestsAndRoomsSection.addGestureRecognizer(UITapGestureRecognizer(
                target: self,
                action: #selector(selectedGuestsAndRooms)
            ))
        } else {
            let appearance = UINavigationBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = .BasePurple
            appearance.shadowColor = .clear
            navigationController?.navigationBar.standardAppearance = appearance
            navigationController?.navigationBar.scrollEdgeAppearance = appearance
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        presenter?.presentErrorMessageOnListViewIfNeeded()

        guard updatedCriteria != nil else { return }
        showUpdateAlert(sender: self)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(UIDevice.current.userInterfaceIdiom != .pad, animated: animated)
    }

    @objc func selectedLocation() {
        presenter?.selectedLocation()
    }

    @objc func selectedDate() {
        presenter?.selectedNights()
    }

    @objc func selectedGuestsAndRooms() {
        presenter?.selectedGuests()
    }

    private func setUpShadowsHeaderBorder(isCollapse: Bool) {
        criteriaHeaderBorderSection.layer.masksToBounds = false
        criteriaHeaderBorderSection.layer.shadowRadius = 3
        criteriaHeaderBorderSection.layer.shadowOpacity = isCollapse ? 0 : 0.5
        criteriaHeaderBorderSection.layer.shadowColor = UIColor.gray.cgColor
        criteriaHeaderBorderSection.layer.shadowOffset = CGSize(width: 0, height: 1)
        criteriaHeaderBorderSection.layer.shadowPath = UIBezierPath(rect: CGRect(
            x: 0,
            y: criteriaHeaderBorderSection.bounds.maxY - criteriaHeaderBorderSection.layer.shadowRadius,
            width: criteriaHeaderBorderSection.bounds.width,
            height: criteriaHeaderBorderSection.layer.shadowRadius
        )).cgPath
    }

    private func setUpShadowsHeader() {
        criteriaHeaderSection.layer.masksToBounds = false
        criteriaHeaderSection.layer.shadowRadius = 3
        criteriaHeaderSection.layer.shadowOpacity = 0.5
        criteriaHeaderSection.layer.shadowColor = UIColor.gray.cgColor
        criteriaHeaderSection.layer.shadowOffset = CGSize(width: 0, height: 1)
        criteriaHeaderSection.layer.shadowPath = UIBezierPath(rect: CGRect(
            x: 0,
            y: criteriaHeaderSection.bounds
                                                                           .maxY - criteriaHeaderSection.layer.shadowRadius,
            width: criteriaHeaderSection.bounds.width,
            height: criteriaHeaderSection.layer.shadowRadius
        ))
                                                                           .cgPath
    }

    private func setUpCriteriaTitle() {
        guard let newText = presenter?.nameOfLocationTextForCurrentCriteria else { return }
        guard newText != criteriaTitleHeader.text else { return }
        self.criteriaTitleHeader.text = newText
    }

    private func setUpCriteriaSubtitle() {
        guard let newText = presenter?.condensedCriteriaTitle else { return }
        self.criteriaSubtitleHeader.attributedText = newText
    }

    private func setUpExpandCriteriaStackView() {
        guard let newText = presenter?.nameOfLocationTextForCurrentCriteria else { return }
        criteriaExpandWhereLabel.text = PILocalizedString("locationHeaderTitle")
        criteriaExpandLocationLabel.text = newText
    }

    private func setUpNightsButtonTitle() {
        guard let newText = presenter?.dateTextForCurrentCriteriaSummary else { return }
        criteriaDateTitleLabel.text = PILocalizedString("dateHeaderCriteria")
        criteriaDateSubtitleLabel.text = newText
    }

    private func setUpRoomsAndGuestsTitle() {
        guard let newText = presenter?.roomsAndGuestsTextForCriteria else { return }
        criteriaRoomsTitleLabel.text = PILocalizedString("roomHeaderCriteria")
        criteriaRoomSubtitleLabel.text = newText
    }

    @objc func selectedCriteriaBlocker() {
        tappedCriteriaButton()
    }

    @objc func backButtonTapped() {
        var controller = (navigationController?.viewControllers.first { $0 is CriteriaModifiable }) as? CriteriaModifiable

        if let currentCriteria = controller?.currentCriteria {
            if !(currentCriteria.arrivalDate == criteria.arrivalDate && currentCriteria.nights == criteria.nights) {
                controller?.updatedCriteria = criteria
            }
        }

        presenter?.backButtonDidTap()
    }

    @objc func editButtonDidTap() {
        presenter?.editButtonDidTap()
    }

    @IBAction func sortCriteriaChanged(_ sender: UISegmentedControl) {
        guard priceSegmentedControlIsBeingToggledProgrammatically == false else { return }

		presenter?.sortDidChange(with: sender.selectedSegmentIndex == 0 ? .distance : .price)
    }

    @IBAction func mapButtonDidTap(_ sender: UIButton) {
        presenter?.mapButtonDidTap()
    }

    private func spinBackButton(_ direction: RotationDirection) {
        let spinDirection = direction == .clockwise ? CGFloat.pi / 2 : 0

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.6,
            initialSpringVelocity: 0.3,
            options: [.curveEaseOut],
            animations: {
            self.backButtonLayoutTop.constant = direction == .clockwise ? 12 : 27
            self.backButton?.transform = CGAffineTransform(rotationAngle: spinDirection)
        }
        )
    }

    private func configureBackButtonToPopViewController(withAnimation animated: Bool) {
        guard UIDevice.current.userInterfaceIdiom != .pad else { return }

        backButton.removeTarget(self, action: #selector(hideCriteriaBlocker), for: .touchUpInside)
        backButton.addTarget(self, action: #selector(backButtonTapped), for: .touchUpInside)
        spinBackButton(.counterClockwise)
    }

    private func configureBackButtonToMinimizeCriteriaSection(withAnimation animated: Bool) {
        backButton.removeTarget(self, action: #selector(backButtonTapped), for: .touchUpInside)
        backButton.addTarget(self, action: #selector(hideCriteriaBlocker), for: .touchUpInside)
        spinBackButton(.clockwise)
    }

    @objc func hideCriteriaBlocker() {
        configureBackButtonToPopViewController(withAnimation: true)

        guard let gestureRecognizer = self.criteriaHideGestureRecognizer,
              let tmpExpandGesture = self.criteriaExpandGestureRecognizer else { return }
        listContainerView.removeGestureRecognizer(gestureRecognizer)
        criteriaHeaderStackView.addGestureRecognizer(tmpExpandGesture)

        criteriaHeaderHeight.constant = .defaultCriteriaHeaderHeight
        collapseHeaderAnimation()
    }

    @objc func tappedCriteriaButton() {
        guard canExpandCriteriaHeader else { return }
        guard let gestureRecognizer = self.criteriaHideGestureRecognizer,
              let tmpExpandGesture = self.criteriaExpandGestureRecognizer else { return }

        listContainerView.addGestureRecognizer(gestureRecognizer)
        criteriaHeaderStackView.removeGestureRecognizer(tmpExpandGesture)

        configureBackButtonToMinimizeCriteriaSection(withAnimation: true)

        if criteriaHeaderHeight.constant != .defaultCriteriaHeaderHeight {
            hideCriteriaBlocker()
            return
        }

        criteriaHeaderHeight.constant = .expandedCriteriaHeaderHeight
        expandHeaderAnimation()
    }

    private func expandHeaderAnimation() {
        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: {
                self.view.layoutIfNeeded()
                self.changeButton.alpha = 0
                self.criteriaInformationsStackView.alpha = 0
                self.criteriaScreenBlocker.alpha = 0.45
                self.criteriaHeaderStackViewLeftLayout.constant = 16
                self.criteriaHeaderStackViewTopLayout.constant = 48
                self.expandHeaderStackView.isHidden = false
                self.setUpShadowsHeader()
            },
            completion: { _ in
            self.changeButton.isHidden = true
            self.criteriaInformationsStackView.isHidden = true
            UIView.animate(withDuration: .ocd, animations: {
                self.setUpShadowsHeaderBorder(isCollapse: false)
                self.expandHeaderStackView.alpha = 1
            })
        }
        )
    }

    private func collapseHeaderAnimation() {
        UIView.animate(
            withDuration: .ocd,
            animations: {
                self.view.layoutIfNeeded()
                self.criteriaScreenBlocker.alpha = 0
                self.criteriaHeaderStackViewLeftLayout.constant = 34
                self.criteriaHeaderStackViewTopLayout.constant = 16
                self.expandHeaderStackView.alpha = 0
                self.changeButton.alpha = 1
                self.criteriaInformationsStackView.isHidden = false
                self.setUpShadowsHeader()
                self.setUpShadowsHeaderBorder(isCollapse: true)
            },
            completion: { _ in
            self.changeButton.isHidden = false
            self.expandHeaderStackView.isHidden = true
            UIView.animate(withDuration: .ocd, animations: {
                self.criteriaInformationsStackView.alpha = 1
            })
        }
        )
    }

    private func hideCardList(completion: (() -> Void)?) {
        if cardsContainerViewBottomHandle != nil {
            cardsContainerViewBottomHandle.constant = -cardsContainerView.frame.height - view.safeAreaInsets.bottom
        }

        UIView.animate(
            withDuration: .ocd,
            animations: {
                self.view.layoutIfNeeded()
            },
            completion: { _ in
            completion?()
        }
        )
    }

    // MARK: ℹ️🅿️🅰️ｄ button actions
    @IBAction func editGuestsButtonDidTap(_ sender: Any) {
        guard let button = sender as? UIView else { return }
        presenter?.editGuestsButtonDidTap(sender: button)
    }

    @IBAction func editDatesButtonDidTap(_ sender: Any) {
        guard let button = sender as? UIView else { return }
        presenter?.editDatesButtonDidTap(sender: button)
    }

    @IBAction func editPlaceButtonDidTap(_ sender: Any) {
        guard let button = sender as? UIView else { return }
        presenter?.editSuggestionButtonDidTap(sender: button)
    }
    // ℹ️🅿️🅰️ｄ

    func updateCriteriaHeaderExpandability(to: Bool) {
        canExpandCriteriaHeader = to
    }
}

extension MapListContainerViewController: MapListContainerViewProtocol {
    func errorDidOccur() {
        showAlertWith(
            title: PILocalizedString("searchResultsSearchFailedTitle", comment: "Search Results search failed title"),
            message: PILocalizedString("searchResultsSearchFailedMessage", comment: "Search Results search failed message")
        )
    }

    func showError(alertController: UIAlertController) {
        DispatchQueue.main.async {
            self.present(alertController, animated: true)
        }
   }

    func showIPadFallBackAlert() {
        DispatchQueue.main.async {
            let alertController = UIAlertController(
                title: PILocalizedString("operaFallbackAlertTitle"),
                message: PILocalizedString("operaFallbackAlertMessage"),
                preferredStyle: .alert
            )
            alertController.addAction(UIAlertAction(
                title: PILocalizedString("operaFallbackAlertClose"),
                style: .cancel,
                handler: { _ in
                UIView.animate(withDuration: .ocd, animations: {
                    self.listContainerView.alpha = 1
                    self.listContainerView.frame.origin.y = 0
                })
            }
            ))
            alertController.addAction(UIAlertAction(
                title: PILocalizedString("operaFallbackAlertContinue"),
                style: .default,
                handler: { _ in
                guard let url = Constants.premierInnBaseURL else { return }
                self.openURL(url: url)
            }
            ))

            self.present(alertController, animated: true)
        }
    }

    func showLoadingBox() {
        view.isUserInteractionEnabled = false

        loadingView = LoadingView(frame: CGRect(
            x: (view.frame.size.width / 2) - 50,
            y: (view.frame.size.height / 2) - 50,
            width: 80,
            height: 80
        ))
        loadingView?.alpha = 0

        guard let loadingView = loadingView else { return }

        view.addSubview(loadingView)

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 1
            },
            completion: { _ in
            loadingView.animate()
        }
        )
    }

    func hideLoadingBox() {
        guard let loadingView = loadingView else { return }

        view.isUserInteractionEnabled = true

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 0
            },
            completion: { _ in
            self.hideCriteriaBlocker()
            self.loadingView?.stopAnimating()
        }
        )
    }

    func updateCriteria(to criteria: Criteria) {
        self.criteria = criteria
        presenter?.criteriaUpdated(to: criteria)
    }

    func updatedSuggestion(to suggestion: Suggestion) {
        presenter?.updatedSuggestion(to: suggestion)
    }

    func refreshCriteriaElements() {
        guard UIDevice.current.userInterfaceIdiom != .pad else { return }

        DispatchQueue.main.async {
            self.setUpRoomsAndGuestsTitle()
            self.setUpNightsButtonTitle()
            self.setUpExpandCriteriaStackView()
            self.setUpCriteriaTitle()
            self.setUpCriteriaSubtitle()
        }
    }

    func selectedCell() -> VenueCell? {
        nil
    }

    func userDidScroll() {
        hideCriteriaBlocker()
    }

    func makeMapFullScreen() {
        if listTopHandle != nil {
            listTopHandle.constant = view.frame.height
        }

        if listBottomHandle != nil {
            listBottomHandle.constant = -view.frame.height
        }

        presenter?.mapWillGoFullScreen()

        UIView.animate(
            withDuration: .ocd,
            animations: {
                self.view.layoutIfNeeded()
            },
            completion: { _ in
            self.presenter?.mapDidGoFullScreen()
        }
        )
    }

    func toggleOptionsView(visible: Bool) {
        guard optionsView != nil else { return }

        if visible {
            self.optionsViewTopHandle.constant = 0
        } else {
            optionsViewTopHandle.constant = -optionsView.frame.height
        }
    }

    func makeMapSmall() {
		presenter?.mapWillGoSmall()

		hideCardList {
			self.optionsViewTopHandle.constant = .defaultCriteriaHeaderHeight
			self.listTopHandle.constant = 0
			self.listBottomHandle.constant = 0

			self.presenter?.mapDidGoSmall()

			UIView.animate(withDuration: .ocd) {
				self.view.layoutIfNeeded()
			}
        }
    }

	func popViewController() {
		navigationController?.popViewController(animated: true)
	}

	func updateMapConstraints(optionsViewTop: CGFloat) {
        optionsViewTopHandle?.constant = optionsViewTop + .defaultCriteriaHeaderHeight
    }

    func slideOptionsViewIn() {
        self.optionsViewTopHandle?.constant = .defaultCriteriaHeaderHeight

        UIView.animate(withDuration: .ocd, delay: 0, options: [.curveLinear], animations: {
            self.view.layoutIfNeeded()
        })
    }


    func slideOptionsViewInWithoutAnimation() {
        self.optionsViewTopHandle?.constant = .defaultCriteriaHeaderHeight
    }

    func slideOptionsViewOut() {
        self.optionsViewTopHandle?.constant = -Constants.optionsViewHeight
        UIView.animate(withDuration: .ocd) {
            self.view.layoutIfNeeded()
        }
    }

    func getCurrentOptionsViewTopHandleOffset() -> CGFloat {
        optionsViewTopHandle.constant
    }

    func hideCards() {
        hideCardList(completion: nil)
    }

	func showCards() {
		guard cardsContainerViewBottomHandle.constant != 0 else { return }
        cardsContainerViewBottomHandle.constant = 0
        cardsContainerView.isHidden = false

		UIView.animate(withDuration: .ocd) {
			self.view.layoutIfNeeded()
		}
	}

	func revertSegmentedControlToPreviousSetting() {
        guard priceSegmentedControl != nil else { return }

        priceSegmentedControl.selectedSegmentIndex = priceSegmentedControl.selectedSegmentIndex == 0 ? 1 : 0
		// This is to ensure that we don't get a loop
		priceSegmentedControlIsBeingToggledProgrammatically = true
		priceSegmentedControl.sendActions(for: .valueChanged)
		priceSegmentedControlIsBeingToggledProgrammatically = false
	}

	func provideHapticFeedback() {
        let feedbackGenerator = UIImpactFeedbackGenerator(style: .heavy)
        feedbackGenerator.impactOccurred()
    }

    func hotelsWereReloaded() {
        navigationItem.titleView = criteriaSummaryView
    }

    func showFallBack() {
        DispatchQueue.main.async {
            if UIDevice.current.userInterfaceIdiom == .pad {
                self.showIPadFallBackAlert()
            } else {
                guard let alertController = AlertManager.fallbackToWebsitePopup() else { return }
                self.present(alertController, animated: true)
            }
        }
   }
}

extension MapListContainerViewController: CriteriaModifiable {
    var currentCriteria: Criteria? { criteria }

    func update(with criteria: Criteria) {
        updatedCriteria = nil
        updateCriteria(to: criteria)
    }
}
