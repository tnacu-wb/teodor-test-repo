//
//  DashboardViewController.swift
//  PremierInn
//
//  Created by Filippo Minelle on 09/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit
import Formeka

class DashboardViewController: FormekaViewController {
    // MARK: - Types

    enum State {
        /// The view is not visible and under the TabBar
        case Invisible
        /// The view is visible and can be slide up (_only if it's contained only one item_)
        case Collapsed
        /// The view showns only the first component on top
        case Expanded
        /// The view is full screen (_we can have a partially full screen to leave space for the search component_)
        case FullView

        func getHeight(
            _ tabBarHeight: CGFloat = 0.0,
            _ topComponentHeight: CGFloat = 0.0,
            bottomPadding: CGFloat = 16.0
        ) -> CGFloat {
            switch self {
            case .Invisible: return UIScreen.main.bounds.height
            case .Collapsed: return UIScreen.main.bounds.height - (tabBarHeight + 60.0)
            case .Expanded: return (UIScreen.main.bounds.height / 2) * 1.1
            case .FullView: return UIScreen.main.bounds.height * 0.09
            }
        }

        var debugStringValue: String {
            switch self {
            case .Invisible: return "Invisible"
            case .Collapsed: return "Collapsed"
            case .Expanded: return "Expanded"
            case .FullView: return "FullView"
            }
        }
    }

    // MARK: - Properties

    override var screenName: String { PIAnalytics.StateNames.dashboard }
    override var screenType: String { PIAnalytics.StateTypes.home }
    override var customParameters: [String: Any]? {
        var bookingFormatted: String?
//        var recentSearchFormatted: String?
        var actionAvailableArray = [String]()

        // Generate the ID and Date for upcoming booking only if there is one
        if let upcomingBooking = dashboardViewModel?.upcomingBooking {
            let identifier = upcomingBooking.identifier
            let date = upcomingBooking.arrivalDate.localizedSlashDayMonthYearStringFormat
            bookingFormatted = String(format: "%@:%@", identifier, date)

            upcomingBooking.actions.forEach { (action) in
                switch action.type {
                case .BOOKING_DETAILS:
                    actionAvailableArray.append("Booking Details")
                case .CIOL:
                    guard upcomingBooking.isBusinessTrip else { break }
                    actionAvailableArray.append("CIOL Available")
                case .DIRECTIONS:
                    actionAvailableArray.append("Directions")
                case .UPSELLS:
                    actionAvailableArray.append("Add Meals")
                }
            }
        }

        var frequentlyBookedHotelCodes = [String]()
        if let frequentBookings = dashboardViewModel?.frequentBookings {
            frequentBookings.forEach { frequentBooking in
                frequentlyBookedHotelCodes.append(frequentBooking.code + ":undated")
            }
        }

        var analyticsData = [String: Any]()
        var trackingIds = [String]()

        if let destinations = dashboardViewModel?.destinationCards {
            let destinationIds = destinations.compactMap { $0.analyticsKey }
            trackingIds.append("Destination: \(destinationIds.joined(separator: ", "))")
        }

        if let contents = dashboardViewModel?.contentCards {
            let contentIds = contents.compactMap { $0.analyticsKey }
            trackingIds.append("Content: \(contentIds.joined(separator: ", "))")
        }

        if let promos = dashboardViewModel?.promoCards {
            let promoIds = promos.map { $0.analyticsKey ?? "" }
            trackingIds.append("Promo: \(promoIds.joined(separator: ", "))")
        }
        analyticsData[PIAnalytics.Keys.dashboardCardTrackingId] = trackingIds.joined(separator: ", ")

        guard bookingFormatted != nil || frequentlyBookedHotelCodes.isNotEmpty else { return analyticsData }

        var detailsJoinedArray = [String]()
//        if let tmpRecentSearchFormatted = recentSearchFormatted { detailsJoinedArray.append(tmpRecentSearchFormatted) }
        if let tmpBookingFormatted = bookingFormatted { detailsJoinedArray.append(tmpBookingFormatted)
        }

        if actionAvailableArray.isNotEmpty {
            detailsJoinedArray.append(actionAvailableArray.joined(separator: ", "))
        }

        if frequentlyBookedHotelCodes.isNotEmpty {
            detailsJoinedArray.append(frequentlyBookedHotelCodes.joined(separator: ", "))
        }

        analyticsData[PIAnalytics.Keys.homeContent] = detailsJoinedArray.joined(separator: ", ")
        return analyticsData
    }

    var customTrackingParameters: PIDictionary = [:]
    var eventHandler: DashboardViewEventHandler?

    // Screen dimensions

    var tabBarHeight: CGFloat {
        self.tabBarController?.tabBar.frame.height ?? 98.0
    }

    // Dashboard

    /// State of the Dashboard to regulate the height of the sheet in the homepage
    private var state: State = .Expanded

    // View Model

    private var dashboardViewModel: DashboardViewModel?

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        roundViews()

        prepareTableView()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        eventHandler?.viewIsReady()
    }

    private func prepareTableView() {
        if table != nil {
            table.separatorStyle = .none
            table.backgroundColor = .white
            registerTableElements()

            let gesture = UIPanGestureRecognizer(target: self, action: #selector(panGesture))
            gesture.delegate = self
            table.addGestureRecognizer(gesture)
        }
    }

    private func registerTableElements() {
        table.registerCellClass(with: SwiftUIContainerTableViewCell<DragIndicatorView>.self)
        table.registerCellClass(with: SwiftUIContainerTableViewCell<ContentListView>.self)
        table.registerCellClass(with: SwiftUIContainerTableViewCell<DestinationListView>.self)
        table.registerCellClass(with: SwiftUIContainerTableViewCell<PromoListView>.self)
        table.registerCellNib(with: DashboardUpcomingBookingView.self)
        table.registerCellNib(with: DashboardFrequentBookingView.self)
        table.registerCellNib(with: RecentSearchCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerHeaderFooterNib(with: SimpleFooter.self)
        table.registerHeaderFooterNib(with: SimpleHeaderWithActionLabel.self)
    }

    private func hideDashboard() {
        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: { [weak self] in
            guard let self = self else { return }

            let yComponent = State.Expanded.getHeight(tabBarHeight, 0)
            view.frame = CGRect(x: 0, y: yComponent, width: view.frame.width, height: view.frame.height)
            view.isHidden = true
        }
        )
    }

    private func roundViews() {
        view.layer.cornerRadius = 12
        view.clipsToBounds = true
    }

    // MARK: - Actions

    @objc func panGesture(_ recognizer: UIPanGestureRecognizer) {
        let translation = recognizer.translation(in: view)
        let velocity = recognizer.velocity(in: view)
        let y = view.frame.minY
        let yTranslation = y + translation.y

        // Direction
        let isVelocityPositive = velocity.y >= 0

        table.isScrollEnabled = shouldEnableTableViewScroll(isVelocityPositive: isVelocityPositive)
        if table.isScrollEnabled {
            return
        }

        // Recognise Gesture
        if (yTranslation >= State.FullView.getHeight()) && (yTranslation <= State.Collapsed.getHeight()) {
            view.frame = CGRect(x: 0, y: yTranslation, width: view.frame.width, height: view.frame.height)
            recognizer.setTranslation(CGPoint.zero, in: view)
        }

        // Gesture Ended
        if recognizer.state == .ended {
            UIView.animate(
                withDuration: 0.5,
                delay: 0.0,
                usingSpringWithDamping: 0.7,
                initialSpringVelocity: 0.8,
                options: [.allowUserInteraction],
                animations: {
                    switch self.state {
                    case .Invisible: break
                    case .Collapsed: self.state = isVelocityPositive ? .Collapsed : .Expanded // unused
                    case .Expanded: self.state = isVelocityPositive ? .Expanded : .FullView
                    case .FullView: self.state = isVelocityPositive ? .Expanded : .FullView
                    }

                    let y = self.state.getHeight(self.tabBarHeight)
                    printDev("§ moving to: \(self.state.debugStringValue) [\(y)]")
                    self.view.frame = CGRect(x: 0, y: y, width: self.view.frame.width, height: self.view.frame.height)
                },
                completion: { _ in
                self.table.isScrollEnabled = self.shouldEnableTableViewScroll(isVelocityPositive: isVelocityPositive)
            }
            )
        }
    }

    func shouldEnableTableViewScroll(isVelocityPositive: Bool) -> Bool {
        guard self.state == .FullView else { return false }

        if isVelocityPositive && table.contentOffset.y <= 0 {  return false }

        return true
    }
}

extension DashboardViewController: DashboardViewProtocol {
    func update(with dashboardViewModel: DashboardViewModel) {
        self.dashboardViewModel = dashboardViewModel

        // dashboard feature flag
        if SettingsManager.sharedInstance.featureShowDashboard && dashboardViewModel.isNotEmpty {
            self.view.isHidden = false

            let y = self.state.getHeight(self.tabBarHeight)
            printDev("§ starting with: \(self.state.debugStringValue) [\(y)]")
            self.view.frame = CGRect(x: 0, y: y, width: self.view.frame.width, height: self.view.frame.height)

            viewModel = FormekaViewModel(sections: viewModelSections(with: dashboardViewModel))

            table.delegate = viewModel
            table.dataSource = viewModel
            table.reloadData()
        } else {
            hideDashboard()
        }
    }
}

extension DashboardViewController: UIGestureRecognizerDelegate {
    func gestureRecognizer(
        _ gestureRecognizer: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
    ) -> Bool {
        guard let gestureRecognizer = gestureRecognizer as? UIPanGestureRecognizer else {
            return false
        }
        let velocity = gestureRecognizer.velocity(in: view)
        let isHorizontal = abs(velocity.x) > abs(velocity.y)

        return !isHorizontal
    }
}

extension DashboardViewController: DashboardUpcomingBookingViewDelegate {
    func showBookingDetails() {
        guard let identifier = dashboardViewModel?.upcomingBooking?.identifier else { return }
        eventHandler?.showBooking(with: identifier)
    }

    func showCheckInOnline() {
        guard let identifier = dashboardViewModel?.upcomingBooking?.identifier else { return }
        eventHandler?.showCheckInOnline(with: identifier)
    }

    func showAmendBooking() {
        guard let identifier = dashboardViewModel?.upcomingBooking?.identifier else { return }
        eventHandler?.showAmendBooking(with: identifier)
    }

    func showHotelDirections() {
        eventHandler?.showHotelDirections()
    }

    func didLayoutViews() {
    }
}

extension DashboardViewController: DashboardFrequentBookingViewDelegate {
    func selectHotelDate(with dashboardHotelDetails: DashboardHotelDetails) {
        eventHandler?.showHotelCalendar(with: dashboardHotelDetails)
    }

    func didLayoutFrequentlyBookedView() {
    }
}

extension DashboardViewController: SimpleHeaderWithActionLabelDelegate {
    func actionLabelDidTap(header: SimpleHeaderWithActionLabel) {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
        eventHandler?.clearRecentSearchesDidTap()
    }
}
