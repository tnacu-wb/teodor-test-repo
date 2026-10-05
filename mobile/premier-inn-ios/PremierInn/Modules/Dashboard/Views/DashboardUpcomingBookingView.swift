//
//  DashboardUpcomingBookingView.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

enum DashboardUpcomingBookingActionType: String {
    case CIOL
    case DIRECTIONS
    case UPSELLS
    case BOOKING_DETAILS
}

struct DashboardUpcomingBookingAction {
    let type: DashboardUpcomingBookingActionType
    let title: String
}

struct DashboardUpcomingBooking {
    // Info
    var identifier: String
    var hotelCode: String
    var imageUrl: URL?
    var hotel: String
    var arrivalDate: Date
    var checkOutDate: Date
    var guestsNumber: Int
    var roomNumber: Int
    var roomType: String
    var isCheckedIn: Bool
    var isBusinessTrip: Bool

    // Actions
    var actions: [DashboardUpcomingBookingAction]
}

extension DashboardUpcomingBooking {
    var stayDatesSummaryString: String {
        arrivalDate.localizedShortDayMonthStringFormat + " - " + checkOutDate.localizedShortDayMonthStringFormat
    }

    var guestsSummaryString: String {
        Criteria.guestsCountDescription(for: guestsNumber)
    }

    var roomsSummaryString: String {
        "\(roomNumber) \(roomType)"
    }

    var guestsRoomsSummaryString: String {
        guestsSummaryString + ", \(roomsSummaryString)"
    }

    var subText1: NSAttributedString {
        let string = isCheckedIn ? stayDatesSummaryString + ", \(roomsSummaryString)" : stayDatesSummaryString
        return NSAttributedString(
            string: string,
            attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.ColourDL2]
        )
    }

    var subText2: NSAttributedString {
        if isCheckedIn {
            return NSAttributedString(
                string: PILocalizedString("bookingSummaryCheckedInText"),
                attributes: [.font: UIFont.BodySmall_Semibold(), .foregroundColor: UIColor.Tint4]
            )
        } else {
            return NSAttributedString(
                string: guestsRoomsSummaryString,
                attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.ColourDL2]
            )
        }
    }
}

protocol DashboardUpcomingBookingViewDelegate: AnyObject {
    func showCheckInOnline()
    func showBookingDetails()
    func showAmendBooking()
    func showHotelDirections()
    func didLayoutViews()
}

class DashboardUpcomingBookingView: UITableViewCell {
    // MARK: - Views

    weak var delegate: DashboardUpcomingBookingViewDelegate?

    @IBOutlet weak var labelComponentTitle: UILabel! {
        didSet {
            labelComponentTitle.accessibilityIdentifier = "DashboardUpcomingBookingTitleLbl"
            labelComponentTitle.text = PILocalizedString("upcomingBookingComponent")
            labelComponentTitle.font = UIFont.Heading2_ExtraBold()
            labelComponentTitle.accessibilityTraits.insert(.header)
            labelComponentTitle.textColor = .BasePurple
        }
    }

    @IBOutlet weak var stackViewUpcomingBooking: UIStackView! {
        didSet {
            stackViewUpcomingBooking.setBackgroundColor(.white, cornerRadius: 4.0, borderWidth: 0.5, borderColor: .lightGray)
        }
    }

    // Booking info
    @IBOutlet weak var imageViewHotel: UIImageView! {
        didSet {
            imageViewHotel.accessibilityIdentifier = "DashboardUpcomingBookingImage"
            imageViewHotel.contentMode = .scaleAspectFill
            imageViewHotel.image = UIImage(named: "hotelPlaceholder")
            imageViewHotel.layer.cornerRadius = 2
        }
    }
    @IBOutlet weak var labelTitle: UILabel! {
        didSet {
            labelTitle.accessibilityIdentifier = "DashboardUpcomingBookingHotelTitleLbl"
            labelTitle.text = "NA"
            labelTitle.font = UIFont.BodySmall_Semibold()
            labelTitle.textColor = .ColourDL1
        }
    }
    @IBOutlet weak var labelDate: UILabel! {
        didSet {
            labelDate.accessibilityIdentifier = "DashboardUpcomingBookingDateLbl"
            labelDate.text = "28 Aug - 29 Aug"
            labelDate.font = UIFont.BodySmall()
            labelDate.textColor = .ColourDL2
        }
    }
    @IBOutlet weak var labelRoom: UILabel! {
        didSet {
            labelRoom.accessibilityIdentifier = "DashboardUpcomingBookingRoomLbl"
            labelRoom.text = "1 guest, 1 double room"
            labelRoom.font = UIFont.BodySmall()
            labelRoom.textColor = .ColourDL2
        }
    }

    // Actions
    @IBOutlet weak var actionsView: UIStackView!

    // MARK: - Properties

    // MARK: - Lifecycle

    override func awakeFromNib() {
        super.awakeFromNib()
        setupCellStyling()
    }

    func update(with upcomingBooking: DashboardUpcomingBooking) {
        labelTitle.text = upcomingBooking.hotel
        labelDate.attributedText = upcomingBooking.subText1
        labelRoom.attributedText = upcomingBooking.subText2

        if let imageUrl = upcomingBooking.imageUrl {
            imageViewHotel.af.setImage(withURL: imageUrl, placeholderImage: UIImage(named: "hotelPlaceholder"))
        }

        _ = actionsView.arrangedSubviews.map { $0.removeFromSuperview() }

        for (index, action) in upcomingBooking.actions.enumerated() {
            if upcomingBooking.isBusinessTrip && (action.type == DashboardUpcomingBookingActionType.CIOL ||
                action.type == DashboardUpcomingBookingActionType.UPSELLS) {
                continue
            }
            if let actionBlock = actionBlock(for: action.type), let view = actionView(
                with: action.title,
                using: index == 0 ? 0 : 16,
                and: actionBlock
            ) {
                actionsView.addArrangedSubview(view)
            }
        }
    }

    // Implement action types here - if an action is not supported it will not be shown
    private func actionBlock(for actionType: DashboardUpcomingBookingActionType) -> (() -> Void)? {
        switch actionType {
        case .BOOKING_DETAILS:
            return { self.delegate?.showBookingDetails() }
        case .DIRECTIONS:
            return { self.delegate?.showHotelDirections() }
        case .CIOL:
            return { self.delegate?.showCheckInOnline() }
        case .UPSELLS:
            return { self.delegate?.showAmendBooking() }
        }
    }

    private func actionView(
        with title: String,
        using dividerSpacing: CGFloat = 16,
        and action: @escaping () -> Void
    ) -> PaymentOptionActionView? {
        guard let actionView: PaymentOptionActionView = UIView
              .fromNib(nibName: String(describing: PaymentOptionActionView.self)) else { return nil }
        actionView.horizontalDividerLeftSpacingConstraint.constant = dividerSpacing
        actionView.action.font = .Body()
        actionView.action.text = title
        actionView.selected = action
        actionView.accessibilityLabel = title
        actionView.accessibilityIdentifier = title

        return actionView
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        delegate?.didLayoutViews()
    }

    private func setupCellStyling() {
        selectionStyle = .none
    }
}
