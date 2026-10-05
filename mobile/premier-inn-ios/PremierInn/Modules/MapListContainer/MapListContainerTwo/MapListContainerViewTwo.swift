//
//  MapListContainerView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol MapListContainerPresenterProtocolTwo {
	var screenTitle: String? { get }
	var screenSubtitle: String? { get }

    func viewIsReady()
    func editButtonDidTap()
	func mapButtonDidTap()
    func backButtonDidTap()
    func sortDidChange(with sortingRule: AvailabilitiesSorting)
    func mapWillGoFullScreen()
    func mapDidGoFullScreen()
	func mapWillGoSmall()
	func mapDidGoSmall()
    func selectedListCell() -> VenueCell?

    func condensedCriteriaTitle() -> NSAttributedString
    func nameOfLocationTextForCurrentCriteria() -> String?
    func dateTextForCurrentCriteria() -> String?
    func roomsAndGuestsTextForCriteria() -> String?

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
	static let defaultCriteriaHeaderHeight: CGFloat = 70
	static let expandedCriteriaHeaderHeight: CGFloat = 200
	static let defaultSearchLocationLabelLeadingSpace: CGFloat = 21
	static let expandedSearchLocationLabelLeadingSpace: CGFloat = 12
	static let defaultSearchCriteriaLabelLeadingSpace: CGFloat = -28
	static let expandedSearchCriteriaLabelLeadingSpace: CGFloat = 12
}

class MapListContainerViewControllerTwo: BaseViewController {
    override var screenName: String { PIAnalytics.StateNames.searchResultsMap }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }
    override var trackScreen: Bool { false }
    override var event: String? { "scOpen" }

    var presenter: MapListContainerPresenterProtocolTwo?

    var criteria: Criteria = BookingDetails.sharedInstance.criteria {
        didSet {
            BookingDetails.sharedInstance.criteria = criteria
        }
    }
    var updatedCriteria: Criteria?

    @IBOutlet weak var optionsView: UIView!
    @IBOutlet weak var priceSegmentedControl: UISegmentedControl! {
        didSet {
            priceSegmentedControl.accessibilityIdentifier = "closestAndCheapestAcc"
            priceSegmentedControl.setTitle(
                PILocalizedString("priceSegementedControlClosest", comment: "Sorting by distance button title"),
                forSegmentAt: 0
            )
            priceSegmentedControl.setTitle(
                PILocalizedString("priceSegementedControlCheapest", comment: "Sorting by price button title"),
                forSegmentAt: 1
            )
            priceSegmentedControl.tintColor = .gunMetal
            priceSegmentedControl.setTitleTextAttributes(
                [NSAttributedString.Key.font: UIFont.premierInnBold(ofSize: 16)],
                for: .normal
            )
        }
    }
	@IBOutlet weak var mapToggleButton: UIButton! {
		didSet {
			mapToggleButton.setTitle(PILocalizedString("mapScreenTitle", comment: "Map Screen title"), for: .normal)
			mapToggleButton.titleLabel?.font = UIFont.premierInnBold(ofSize: 16)
			mapToggleButton.setTitleColor(.gunMetal, for: .normal)
			mapToggleButton.layer.cornerRadius = 5
			mapToggleButton.layer.borderWidth = 1
			mapToggleButton.layer.borderColor = UIColor.gunMetal.cgColor
		}
	}
    @IBOutlet weak var mapContainerView: UIView!
	@IBOutlet weak var listContainerView: UIView!
	@IBOutlet weak var cardsContainerView: UIView!
	@IBOutlet weak var criteriaButton: UIButton!
	@IBOutlet weak var criteriaScreenBlocker: UIView!
	@IBOutlet weak var criteriaHeaderHeight: NSLayoutConstraint!
	@IBOutlet weak var backButton: UIButton!
	@IBOutlet weak var currentLocation: UILabel!
	@IBOutlet weak var currentDateAndNights: UILabel!
	@IBOutlet weak var currentRoomsAndGuests: UILabel!
	@IBOutlet weak var currentLocationSection: UIView!
	@IBOutlet weak var dateSection: UIView!
	@IBOutlet weak var guestsAndRoomsSection: UIView!
	@IBOutlet weak var criteriaHeaderSection: UIView!
	@IBOutlet weak var searchLocationLabelLeadingSpace: NSLayoutConstraint!
	@IBOutlet weak var searchLocationLeadingSpace: NSLayoutConstraint!
	@IBOutlet weak var searchLocationButton: UIImageView!
	@IBOutlet weak var cardsContainerViewHeight: NSLayoutConstraint!
	@IBOutlet weak var cardsContainerViewBottomHandle: NSLayoutConstraint!
    @IBOutlet weak var listTopHandle: NSLayoutConstraint!
	@IBOutlet weak var listBottomHandle: NSLayoutConstraint!
	@IBOutlet weak var optionsViewTopHandle: NSLayoutConstraint!

	private var priceSegmentedControlIsBeingToggledProgrammatically = false
    private var loadingView: LoadingView?
    private let statusBarUnderlay = UIView()
    private var canExpandCriteriaHeader = true
    private var originalBackImage: UIImage?
    private var criteriaGestureRecognizer: UITapGestureRecognizer?

    override func viewDidLoad() {
        super.viewDidLoad()

		listContainerView.backgroundColor = .clear
		cardsContainerView.backgroundColor = .clear
		criteriaButton.layer.cornerRadius = 3.0

		if cardsContainerViewHeight != nil {
			cardsContainerViewHeight.constant = Constants.cardCellSize.height + 20
		}

		if cardsContainerViewBottomHandle != nil {
            cardsContainerViewBottomHandle.constant = -cardsContainerView.frame.height - bottomLayoutGuide.length
        }

        presenter?.viewIsReady()

        searchLocationLabelLeadingSpace.constant = 21

        criteriaGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(hideCriteriaBlocker))

        statusBarUnderlay.backgroundColor = .premierInnPurple
        view.addSubview(statusBarUnderlay)

        criteriaHeaderSection.backgroundColor = .premierInnPurple

        var backImage = #imageLiteral(resourceName: "arrowRight")
        originalBackImage = backImage

        if let backImageCGImage = backImage.cgImage {
            backImage = UIImage(cgImage: backImageCGImage, scale: 1.0, orientation: UIImage.Orientation.upMirrored)
            backImage = backImage.withRenderingMode(.alwaysTemplate)
            backButton.setImage(backImage, for: .normal)
        } else {
            backButton.setTitle("Back", for: .normal)
        }

        backButton.tintColor = .white
        backButton.addTarget(self, action: #selector(backButtonTapped), for: .touchUpInside)

        setUpCriteriaButtonTitle()
        setUpLocationButtonTitle()
        setUpNightsButtonTitle()
        setUpRoomsAndGuestsTitle()

        currentLocationSection.addGestureRecognizer(UITapGestureRecognizer(
            target: self,
            action: #selector(selectedLocation)
        ))
        dateSection.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(selectedDate)))
        guestsAndRoomsSection.addGestureRecognizer(UITapGestureRecognizer(
            target: self,
            action: #selector(selectedGuestsAndRooms)
        ))
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        guard updatedCriteria != nil else { return }
        showUpdateAlert(sender: self)
    }

    @objc private func selectedLocation() {
        presenter?.selectedLocation()
    }

    @objc private func selectedDate() {
        presenter?.selectedNights()
    }

    @objc private func selectedGuestsAndRooms() {
        presenter?.selectedGuests()
    }

    private func setUpCriteriaButtonTitle() {
        guard let newText = presenter?.condensedCriteriaTitle().string else { return }
        if newText == criteriaButton.title(for: .normal) { return }
        if newText == criteriaButton.title(for: .highlighted) { return }
        if newText == criteriaButton.title(for: .selected) { return }

        criteriaButton.setAttributedTitle(presenter?.condensedCriteriaTitle(), for: .normal)
        criteriaButton.setAttributedTitle(presenter?.condensedCriteriaTitle(), for: .highlighted)
        criteriaButton.setAttributedTitle(presenter?.condensedCriteriaTitle(), for: .selected)
    }

    private func setUpLocationButtonTitle() {
        guard let newText = presenter?.nameOfLocationTextForCurrentCriteria() else { return }
        if newText == currentLocation.text { return }

        currentLocation.fadeToBottomAndBounceInFromTopAnimation(withActionToPerformWhilstHidden: { [weak self] in
            self?.currentLocation.text = self?.presenter?.nameOfLocationTextForCurrentCriteria() ?? ""
        })
    }

    private func setUpNightsButtonTitle() {
        guard let newText = presenter?.dateTextForCurrentCriteria() else { return }
        if newText == currentDateAndNights.text { return }

        currentDateAndNights.fadeToBottomAndBounceInFromTopAnimation(withActionToPerformWhilstHidden: { [weak self] in
            self?.currentDateAndNights.text = self?.presenter?.dateTextForCurrentCriteria() ?? ""
        })
    }

    private func setUpRoomsAndGuestsTitle() {
        guard let newText = presenter?.roomsAndGuestsTextForCriteria() else { return }
        if newText == currentRoomsAndGuests.text { return }

        currentRoomsAndGuests.fadeToBottomAndBounceInFromTopAnimation(withActionToPerformWhilstHidden: { [weak self] in
            self?.currentRoomsAndGuests.text = self?.presenter?.roomsAndGuestsTextForCriteria() ?? ""
        })
    }

    @objc func selectedCriteriaBlocker() {
        tappedCriteriaButton()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)


        navigationController?.setNavigationBarHidden(true, animated: animated)
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        statusBarUnderlay.frame = UIApplication.shared.statusBarFrame
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
        let spinDirection = direction == .clockwise
            ? CGFloat.pi / 2
            : 0

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.6,
            initialSpringVelocity: 0.3,
            options: [.curveEaseOut],
            animations: {
            self.backButton?.transform = CGAffineTransform(rotationAngle: spinDirection)
        }
        )
    }

    private func configureBackButtonToPopViewController(withAnimation animated: Bool) {
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

        guard let gestureRecognizer = self.criteriaGestureRecognizer else { return }
        listContainerView.removeGestureRecognizer(gestureRecognizer)

        criteriaHeaderHeight.constant = .defaultCriteriaHeaderHeight
        searchLocationLeadingSpace.constant = .defaultSearchCriteriaLabelLeadingSpace
        searchLocationLabelLeadingSpace.constant = .defaultSearchLocationLabelLeadingSpace

        UIView.animate(
            withDuration: .ocd,
            animations: {
                self.dateSection.alpha = 0
                self.guestsAndRoomsSection.alpha = 0
                self.view.layoutIfNeeded()
                self.criteriaScreenBlocker.alpha = 0
                self.searchLocationButton.alpha = 0
            },
            completion: { _ in
            self.criteriaButton.alpha = 1
            UIView.animate(withDuration: .ocd, animations: {
                self.currentLocationSection.alpha = 0
            })
        })
    }

    @IBAction func tappedCriteriaButton() {
        guard canExpandCriteriaHeader else { return }
        guard let gestureRecognizer = self.criteriaGestureRecognizer else { return }

        listContainerView.addGestureRecognizer(gestureRecognizer)

        configureBackButtonToMinimizeCriteriaSection(withAnimation: true)

        if criteriaHeaderHeight.constant != .defaultCriteriaHeaderHeight {
            hideCriteriaBlocker()
            return
        }

        criteriaHeaderHeight.constant = .expandedCriteriaHeaderHeight
        searchLocationLeadingSpace.constant = .expandedSearchCriteriaLabelLeadingSpace
        searchLocationLabelLeadingSpace.constant = .expandedSearchLocationLabelLeadingSpace

        criteriaButton.alpha = 0
        currentLocationSection.alpha = 1

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: {
            self.searchLocationButton.alpha = 1
            self.view.layoutIfNeeded()
            self.dateSection.alpha = 1
            self.guestsAndRoomsSection.alpha = 1
            self.criteriaScreenBlocker.alpha = 0.45
        }
        )
    }

    private func hideCardList(completion: (() -> Void)?) {
        if cardsContainerViewBottomHandle != nil {
            cardsContainerViewBottomHandle.constant = -cardsContainerView.frame.height - bottomLayoutGuide.length
        }

        UIView.animate(
            withDuration: .ocd,
            animations: {
                self.view.layoutIfNeeded()
            },
            completion: { _ in
            completion?()
        })
    }

    func updateCriteriaHeaderExpandability(to: Bool) {
        canExpandCriteriaHeader = to
    }
}

extension MapListContainerViewControllerTwo: MapListContainerViewProtocolTwo {
    func errorDidOccur() {
        showAlertWith(
            title: PILocalizedString("searchResultsSearchFailedTitle", comment: "Search Results search failed title"),
            message: PILocalizedString("searchResultsSearchFailedMessage", comment: "Search Results search failed message")
        )
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
        })
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
        })
    }

    func updateCriteria(to criteria: Criteria) {
        self.criteria = criteria
        presenter?.criteriaUpdated(to: criteria)
    }

    func updatedSuggestion(to suggestion: Suggestion) {
        presenter?.updatedSuggestion(to: suggestion)
    }

    func refreshCriteriaElements() {
        DispatchQueue.main.async {
            self.setUpRoomsAndGuestsTitle()
            self.setUpNightsButtonTitle()
            self.setUpLocationButtonTitle()
            self.setUpCriteriaButtonTitle()
        }
    }

    func selectedCell() -> VenueCell? {
        nil
    }

    func userDidScroll() {
        hideCriteriaBlocker()
    }

    func makeMapFullScreen() {
        optionsViewTopHandle.constant = -optionsView.frame.height
        listTopHandle.constant = view.frame.height
		listBottomHandle.constant = -view.frame.height

        presenter?.mapWillGoFullScreen()

        UIView.animate(
            withDuration: .ocd,
            animations: {
                self.view.layoutIfNeeded()
            },
            completion: { _ in
            self.presenter?.mapDidGoFullScreen()
        })
    }

    func makeMapSmall() {
		presenter?.mapWillGoSmall()

		hideCardList {
			self.optionsViewTopHandle.constant = .defaultCriteriaHeaderHeight
			self.listTopHandle.constant = .defaultCriteriaHeaderHeight
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
        optionsViewTopHandle.constant = optionsViewTop + .defaultCriteriaHeaderHeight
    }

    func slideOptionsViewIn() {
        self.optionsViewTopHandle.constant = .defaultCriteriaHeaderHeight

        UIView.animate(withDuration: .ocd, delay: 0, options: [.curveLinear], animations: {
            self.view.layoutIfNeeded()
        })
    }

    func slideOptionsViewOut() {
        self.optionsViewTopHandle.constant = -optionsView.frame.height

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

		UIView.animate(withDuration: .ocd) {
			self.view.layoutIfNeeded()
		}
	}

	func revertSegmentedControlToPreviousSetting() {
		priceSegmentedControl.selectedSegmentIndex = priceSegmentedControl.selectedSegmentIndex == 0 ? 1 : 0
		// This is to ensure that we don't get a loop
		priceSegmentedControlIsBeingToggledProgrammatically = true
		priceSegmentedControl.sendActions(for: .valueChanged)
		priceSegmentedControlIsBeingToggledProgrammatically = false
	}

	func provideHapticFeedback() {
		if #available(iOS 10.0, *) {
			let feedbackGenerator = UIImpactFeedbackGenerator(style: .heavy)
			feedbackGenerator.impactOccurred()
		}
	}
}

extension MapListContainerViewControllerTwo: CriteriaViewControllerDelegate {
	func criteriaController(
	    _ sender: CriteriaViewController,
	    didFinishWithSuggestion suggestion: Suggestion,
	    criteria: Criteria
	) {
		FlowController.sharedInstance.performSearch(with: suggestion, criteria: criteria) { (nextController, error) in
            BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.navigationController)

			if let error = error {
				var message = PILocalizedString("searchResultsError", comment: "Search results error")

				#if DEV
					message += "\n\n[ " + error.localizedDescription + " ]"
				#endif

				sender.dismiss(animated: true) {
					let controller = UIAlertController(
					    title: PILocalizedString("hotelSearchAlertErrorTitle", comment: "Hotel search alert error title"),
					    message: message,
					    preferredStyle: .alert
					)
					controller.addAction(UIAlertAction(
					    title: PILocalizedString("hotelSearchAlertErrorAction", comment: "Hotel search alert error action"),
					    style: .cancel
					) { _ in
					})
					self.present(controller, animated: true)
				}
			} else {
				sender.dismiss(animated: true)

				guard let nextController = nextController else { return }
				guard let navController = self.navigationController else { return }

				var stack = navController.viewControllers
				stack.removeLast()
				stack.append(nextController)

				// Animated is false to prevent a weird push effect coming from nowhere
				navController.setViewControllers(stack, animated: false)
			}
		}
	}

	func criteriaControllerDidCancel(_ sender: CriteriaViewController) {
		sender.dismiss(animated: true)
	}
}

extension MapListContainerViewControllerTwo: CriteriaModifiable {
    var currentCriteria: Criteria? { criteria }

    func update(with criteria: Criteria) {
        updatedCriteria = nil
        updateCriteria(to: criteria)
    }
}
