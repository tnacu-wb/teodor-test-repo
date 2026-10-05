//
//  CheckInOnlineView.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit

protocol CheckInOnlineViewModel {
    var hotelName: String { get }
    var staySummary: String { get }
    var rooms: [CheckInOnlineRoomViewModel] { get }
    var canCheckIn: Bool { get }
    var footerModel: CheckInOnlineFooterViewModel { get }
}

protocol CheckInOnlineRoomViewModel {
    var roomName: String { get }
    var guestDescription: NSAttributedString { get }
    var guestInformationComplete: Bool { get }
    var editGuestDidTap: () -> Void { get }
}

protocol CheckInOnlineFooterViewModel {
    var termsMessage: NSAttributedString? { get }
    var checkInDescription: NSAttributedString? { get }
    var breakdownRows: [(title: String, value: String)]? { get }
    var totalPrice: (title: String, value: String)? { get }
    var ctaTitle: String { get }
    var ctaIcon: UIImage? { get }
    var showCardsAccepted: Bool { get }
    var paymentCardImageUrls: [URL]? { get }
}

struct CIOLPaymentAnalyticsParams {
    var prepaid: Bool
    var cardType: String = ""
    var total: String = ""
    var nightsChange: Int = 0
    var roomsChange: Int = 0
    var roomTypeChange: Bool = false
    var extrasBooked: String = ""
    var foodRevenueChange: String = ""
    var roomRevenueChange: Int = 0
    var extrasRevenueChange: Int = 0
    var totalRevenueChange: Int = 0
}

protocol CheckInOnlineViewEventHandler: AnyObject {
    func viewIsReady()
    func dataNeededForTracking() -> [String: Any]?
    func termsButtonDidTap()
    func movingBackToParent()
}

class CheckInOnlineView: FormekaViewController {
    // MARK: - Properties

    override var screenName: String { PIAnalytics.StateNames.checkInOnline }
    override var screenType: String {
        guard let bookingFlow = navigationController?.viewControllers.contains(where: { $0 is ReviewAndBookViewController })
            else { return PIAnalytics.StateTypes.myBookings }
        return bookingFlow ? PIAnalytics.StateTypes.bookingFlow : PIAnalytics.StateTypes.myBookings
    }
    override var customParameters: [String: Any]? {
        eventHandler?.dataNeededForTracking()
    }
    override var trackScreen: Bool { false }

    var eventHandler: CheckInOnlineViewEventHandler?

    private var loadingView: LoadingView?

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        hidesBottomBarWhenPushed = true

        if table != nil {
            table.backgroundColor = .whiteTwo
            table.separatorStyle = .none
        }

        registerTableElements()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        eventHandler?.viewIsReady()
    }

    override func willMove(toParent parent: UIViewController?) {
        super.willMove(toParent: parent)

        guard parent == nil else { return }
        eventHandler?.movingBackToParent()
    }

    private func registerTableElements() {
        guard table != nil else { return }

        table.registerCellNib(with: SimpleSubtitleCell.self)
        table.registerCellNib(with: MultiLineSubtitleCell.self)
        table.registerCellNib(with: CheckInRoomDetailsInvalidCell.self)
        table.registerCellNib(with: CheckInRoomDetailsValidCell.self)
        table.registerCellNib(with: ConfirmCheckInActionCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: BookingReviewAdditionsCell.self)
        table.registerCellNib(with: CreditCardsListCell.self)

        table.registerHeaderFooterNib(with: SimpleFooter.self)
    }

    private func showLoadingView() {
        view.showLoadingView()
    }

    private func hideLoadingView() {
        view.hideLoadingView()
    }
}
