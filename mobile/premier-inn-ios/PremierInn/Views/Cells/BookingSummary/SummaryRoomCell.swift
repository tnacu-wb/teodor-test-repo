//
//  SummaryRoomCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/10/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class PIStackView: UIStackView {
    private var color: UIColor?

    override var backgroundColor: UIColor? {
        get { color }
        set { color = newValue }
    }

    private lazy var backgroundLayer: CAShapeLayer = {
        let layer = CAShapeLayer()
        self.layer.insertSublayer(layer, at: 0)
        return layer
    }()

    override func layoutSubviews() {
        super.layoutSubviews()

        backgroundLayer.path = UIBezierPath(rect: self.bounds).cgPath
        backgroundLayer.fillColor = self.backgroundColor?.cgColor
    }
}

class SummaryRoomCell: UITableViewCell {
	@IBOutlet weak var numberLabel: UILabel! {
		didSet {
			numberLabel.textColor = UIColor.TintD1
            numberLabel.text = nil
            numberLabel.font = .Heading3_Semibold()
		}
	}
	@IBOutlet weak var typeLabel: UILabel! {
		didSet {
			typeLabel.textColor = UIColor.Tint1
            typeLabel.text = nil
            typeLabel.font = .Heading4_Semibold()
		}
	}
	@IBOutlet weak var typeValueLabel: UILabel! {
		didSet {
			typeValueLabel.textColor = UIColor.TintD1
            typeValueLabel.text = nil
            typeValueLabel.font = .Body()
		}
	}
	@IBOutlet weak var guestsLabel: UILabel! {
		didSet {
			guestsLabel.textColor = UIColor.Tint1
            guestsLabel.text = nil
            guestsLabel.font = .Heading4_Semibold()
        }
	}
	@IBOutlet weak var guestsValueLabel: UILabel! {
		didSet {
			guestsValueLabel.textColor = UIColor.TintD1
            guestsValueLabel.text = nil
            guestsValueLabel.font = .Body()
		}
	}
	@IBOutlet weak var breakdownTitleLabel: UILabel! {
		didSet {
			breakdownTitleLabel.textColor = UIColor.sea
            breakdownTitleLabel.text = nil
            breakdownTitleLabel.font = .Heading4_Semibold()
		}
	}
    @IBOutlet weak var dailyRatesStackView: UIStackView!
	@IBOutlet weak var totalPriceLabel: UILabel! {
		didSet {
			totalPriceLabel.textColor = UIColor.TintD1
            totalPriceLabel.text = nil
            totalPriceLabel.font = .Heading1_Semibold()
		}
	}

    @IBOutlet weak var cityTaxLabel: UILabel! {
        didSet {
            cityTaxLabel.font = .SubtextSmall()
            cityTaxLabel.textColor = .TintD1
        }
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        dailyRatesStackView.arrangedSubviews.forEach { (view) in
            dailyRatesStackView.removeArrangedSubview(view)
        }
    }
}

private extension DailyRate {
    func priceDescription() -> String? {
        let tax = cityTax ?? Cost(amount: 0, currencyCode: price.currencyCode)

        return (price - tax)?.localizedValue
    }
}

extension SummaryRoomCell {
    func roomCostTextFrom(room: Room) -> String? {
        guard let fullRoomcost = room.totalCost else { return nil }

        let cityTaxCost = room.options?.first?.cityTax ?? Cost(amount: 0, currencyCode: fullRoomcost.currencyCode)

        let totalCostMinusCityTax = Cost(
            amount: fullRoomcost.amount.doubleValue - cityTaxCost.amount.doubleValue,
            currencyCode: fullRoomcost.currencyCode
        )

        return totalCostMinusCityTax.localizedValue
    }



    func configure(with room: Room, roomIndex: Int) {
        numberLabel.text = PILocalizedString("genericRoomTitle", comment: "Room information title") + " \(roomIndex + 1)"

        typeLabel.text = PILocalizedString("roomTypeScreenTitle", comment: "Room type: screen title")
        typeLabel.accessibilityIdentifier = "\(AccessibilityIdentifiers.BookingSummary.roomIndex)\(roomIndex)\(AccessibilityIdentifiers.BookingSummary.TypeTitle)"

        typeValueLabel.text = "-"

        guestsLabel.text = PILocalizedString("guestsLabel", comment: "Guests description label")
        guestsLabel.accessibilityIdentifier = "\(AccessibilityIdentifiers.BookingSummary.roomIndex)\(roomIndex)\(AccessibilityIdentifiers.BookingSummary.GuestTitle)"

        guestsValueLabel.text = "-"

        breakdownTitleLabel.text = PILocalizedString("priceNightLabel", comment: "Price by night label")

        // TODO: use content from roomTypesInformation for Opera (maybe also for BART) room types, for the price breakdown during the booking flow
        typeValueLabel.accessibilityIdentifier = "\(AccessibilityIdentifiers.BookingSummary.roomIndex)\(roomIndex)Type"

        guestsValueLabel.text = room.guestsSummary
        guestsValueLabel.accessibilityIdentifier = "\(AccessibilityIdentifiers.BookingSummary.roomIndex)\(roomIndex)\(AccessibilityIdentifiers.BookingSummary.GuestCount)"

        // Daily rates are not available in the reservation response, therefore we can't show in my bookings
        let selectedRoom = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first

        if selectedRoom?.silentSubstitution == false {
            typeValueLabel.text = SettingsManager.sharedInstance.roomLabelFor(lettingType: selectedRoom?.lettingType ?? "")
        } else {
            typeValueLabel.text = room.typeSummary
        }

        if let dailyRates = selectedRoom?.dailyRates {
            breakdownTitleLabel.isHidden = false

            for (index, dailyRate) in dailyRates.enumerated() {
                let view = createDailyRateView(rate: dailyRate)
                view.accessibilityIdentifier = "\(AccessibilityIdentifiers.BookingSummary.roomIndex)\(roomIndex)\(AccessibilityIdentifiers.BookingSummary.PriceByNightDateIndex)\(index)"
                dailyRatesStackView.addArrangedSubview(view)
            }
        } else {
            breakdownTitleLabel.isHidden = true
        }

        totalPriceLabel.text = roomCostTextFrom(room: room)
	}

    private func createDailyRateView(rate: DailyRate) -> UIView {
        let stack = PIStackView()
        stack.backgroundColor = .whiteTwo
        stack.axis = .horizontal
        stack.isLayoutMarginsRelativeArrangement = true
        stack.layoutMargins = UIEdgeInsets(top: 9, left: 10, bottom: 9, right: 10)

        let dateLabel = UILabel()
        dateLabel.font = .Body()
        dateLabel.textColor = .TintD1
        dateLabel.text = rate.date?.localizedVeryShortStringFormat
        dateLabel.accessibilityIdentifier = "dailyRateDateAcc"

        let priceLabel = UILabel()
        priceLabel.font = .Heading2_Semibold()
        priceLabel.textColor = .TintD1
        priceLabel.text = rate.priceDescription()
        priceLabel.textAlignment = .right
        priceLabel.accessibilityIdentifier = "dailyRatePriceAcc"

        stack.addArrangedSubview(dateLabel)
        stack.addArrangedSubview(priceLabel)

        return stack
    }
}
