//
//  AlternateCalendarViewController.swift
//  PF
//
//  Created by Marcello Mascia on 26/08/2018.
//  Copyright © 2018 Whitbread PLC. All rights reserved.
//

import UIKit
import SimpleCalendar
import SimpleNetwork

private extension Int {
	static let todayLabel: Int = 1337
}

private extension CGFloat {
	static let todayLabelHeight: CGFloat = 14
}

protocol AlternateCalendarViewControllerProtocol: AnyObject {
    var view: UIView! { get }

    func toggle(is processing: Bool)
    func showError(with message: String)
    func hideError()
    func showDoneButton(show: Bool, animated: Bool)
}

protocol AlternateCalendarViewControllerDelegate: AnyObject {
	func calendarDidSelect(arrivalDate: Date, nights: Int)
    func calendarDidChange(arrivalDate: Date, nights: Int)
    func calendarDidInvalidate()
}

class AlternateCalendarViewController: CalendarViewController {
	@IBOutlet weak var arrivingTitleLabel: UILabel!
	@IBOutlet weak var leavingTitleLabel: UILabel!
    @IBOutlet weak var arrivingValueLabel: UILabel! {
        didSet {
            arrivingValueLabel.font = UIFont.Heading2_Regular()
        }
    }
    @IBOutlet weak var leavingValueLabel: UILabel! {
        didSet {
            leavingValueLabel.font = UIFont.Heading2_Regular()
        }
    }
	@IBOutlet weak var headerView: UIView! {
		didSet {
			headerView.backgroundColor = .Tint1
		}
	}
	@IBOutlet weak var submitButtonContainer: UIView!
	@IBOutlet weak var submitButtonBottomConstraint: NSLayoutConstraint!
	@IBOutlet weak var submitButton: RoundedCornersButton! {
		didSet {
            submitButton.setTitle("", for: .normal)
			submitButton.backgroundColor = .BasePurple
		}
	}

    @IBOutlet weak var error: UILabel!
    @IBOutlet weak var errorContainerHeightConstraint: NSLayoutConstraint!

    var submitButtonPrefix: String = PILocalizedString("Done", comment: "")
    var showSubmitButtonOnDateSelection: Bool = true
    var rulesToFollow: Restrictions = SettingsManager.sharedInstance.activeRules

	weak var calendarDelegate: AlternateCalendarViewControllerDelegate?

	private var arrivalDate: Date? {
		didSet {
			updateDateLabels(animated: true)
		}
	}
	private var departureDate: Date? {
		didSet {
			updateDateLabels(animated: true)
		}
	}
    private var processingView: ProcessingView?

    init(arrivalDate: Date?, departureDate: Date?, overRideRules: Restrictions? = nil, closeButtonTitle: String? = nil) {
		super.init(
			settings: .settings(arrivalDate: arrivalDate, overRideRules: overRideRules, closeButtonTitle: closeButtonTitle),
			nibName: String(describing: AlternateCalendarViewController.self),
            bundle: Bundle(for: AlternateCalendarViewController.self)
		)

		self.arrivalDate = arrivalDate
		self.departureDate = departureDate
        if let overRideRules = overRideRules {
            self.rulesToFollow = overRideRules
        }
	}

	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

	override func viewDidLoad() {
        super.viewDidLoad()

		calendarView.registerCellForNib(with: AlternateCalendarDayCell.self)

        navigationItem.leftBarButtonItem = UIBarButtonItem(
        	title: NSLocalizedString(settings.closeButtonTitle, comment: "Close button title"),
        	style: .plain,
        	target: self,
        	action: #selector(cancelButtonDidTap)
        )
        navigationItem.leftBarButtonItem?.tintColor = .white
		navigationItem.rightBarButtonItem = UIBarButtonItem(
			title: PILocalizedString("calendarResetButtonTitle", comment: "Calendar reset button title"),
			style: .plain,
			target: self,
			action: #selector(resetButtonDidTap)
		)
        navigationItem.rightBarButtonItem?.tintColor = .white
		updateDateLabels(animated: false)

		delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        refreshDoneButton(animated: false)

        if departureDate == nil || showSubmitButtonOnDateSelection == false { showDoneButton(show: false, animated: false) }
    }

    @IBAction func closeErrorButtonDidTap(_ sender: Any) {
        hideError()
    }
}

extension AlternateCalendarViewController {
	override func collectionView(
		_ collectionView: UICollectionView,
		cellForItemAt indexPath: IndexPath
	) -> UICollectionViewCell {
		guard let cell: AlternateCalendarDayCell = collectionView.dequeueCell(for: indexPath)
			else { return UICollectionViewCell() }

        cell.isSelected = false
        cell.dayLabel.text = ""

		guard indexPath.section < months.count else { return cell }
		let month = months[indexPath.section]
		guard indexPath.row < month.days.count else { return cell }
		let day = month.days[indexPath.row]

        cell.setCustomColours(
        	selectable: settings.colors.selectable,
        	notSelectable: settings.colors.notSelectable,
        	selected: settings.colors.selected,
        	highlighted: settings.colors.highlighted,
        	highlightedBackground: settings.colors.highlightedBackground
        )
        cell.setCustomFont(settings.fonts.daysFont)

        cell.dayLabel.text = day.title

        cell.isSelectable = {
            guard settings.freeSelection == false else { return true }
            return day.isSelectable
        }()

        if let dateFormatted = day.date, cell.isSelectable {
            cell.dayLabel.accessibilityLabel = Date.accFullDateSpoken(date: dateFormatted)
            cell.dayLabel.accessibilityIdentifier = Date.englishLocaleDateString(date: dateFormatted)
        } else {
            // setting to nil because Appium can target elements even when they are disabled from accessibility
            cell.dayLabel.accessibilityLabel = nil
            cell.dayLabel.accessibilityIdentifier = nil
        }

        cell.dayLabel.isAccessibilityElement = cell.isSelectable
        cell.dayLabel.accessibilityTraits = .button
        cell.isUserInteractionEnabled = cell.isSelectable

        customiseDayCell(cell: cell, date: day.date, indexPath: indexPath)

		return cell
	}

    private func customiseDayCell(cell: AlternateCalendarDayCell, date: Date?, indexPath: IndexPath) {
		let now = Date()
		resetDay(contentView: cell.contentView)

		cell.dayLabel.highlightedTextColor = cell.dayLabel.textColor
        cell.maskPosition = .middle

        guard let date = date else {
            customiseEmptyDayCell(with: cell.contentView, at: indexPath)
            return
        }

		if date.isBefore(date: now) && date.isOnTheSameDateAs(date: now) == false { return }

        var dayComponent = DateComponents()
        dayComponent.day = rulesToFollow.maxDepartureDateCount

		if let futureDate = Calendar.current.date(byAdding: dayComponent, to: Date()), date > futureDate { return }

		guard let arrivalDate = arrivalDate else {
			if date.isToday {
				toggle(view: cell.contentView, is: true)
			}

			return
		}

		if date.isToday && !date.isOnTheSameDateAs(date: arrivalDate) {
			toggle(view: cell.contentView, dayLabel: cell.dayLabel, is: true)
            return
		}

		toggle(view: cell.contentView, is: false)
		cell.dayLabel.highlightedTextColor = .BaseWhite

		if date.isOnTheSameDateAs(date: arrivalDate) {
            cell.maskPosition = .first
			styleSelected(with: cell.dayLabel, contentView: cell.contentView, and: .first)
            return
		}

        guard let departureDate = departureDate else {
            styleUnselected(with: cell.dayLabel, and: cell.contentView)
            return
        }

		if date.isOnTheSameDateAs(date: departureDate) {
            cell.maskPosition = .last
			styleSelected(with: cell.dayLabel, contentView: cell.contentView, and: .last)
		} else if date > arrivalDate, date < departureDate {
            cell.maskPosition = .middle
			styleSelected(with: cell.dayLabel, contentView: cell.contentView, and: .middle)
		} else {
			styleUnselected(with: cell.dayLabel, and: cell.contentView)
		}
	}
}

extension AlternateCalendarViewController {
	@objc private func cancelButtonDidTap() {
		dismiss(animated: true)
	}

	@objc private func resetButtonDidTap() {
		departureDate = nil
		arrivalDate = nil

        calendarDelegate?.calendarDidInvalidate()

        refreshDoneButton(animated: true)
		reloadCalendar()
	}

	@IBAction func submitButtonDidTap(_ sender: UIButton) {
		guard let arrivalDate = arrivalDate else { return }
		guard let departureDate = departureDate else { return }

		let nights = arrivalDate.daysToCheckInDate(departureDate)

		calendarDelegate?.calendarDidSelect(arrivalDate: arrivalDate, nights: nights)
	}

	private func updateDateLabels(animated: Bool) {
        NotificationFeedbackManager.shared.provideLightTapFeedback()

		arrivingTitleLabel.font = arrivalDate == nil ? .BodySmall_Semibold() : .BodySmall()
        arrivingTitleLabel.text = PILocalizedString("arrivingCellLabel")
		arrivingValueLabel.text = arrivalDate?.localizedVeryShortStringFormat ?? PILocalizedString(
			"calendarSelectDateLableTitle",
			comment: "Calendar select date label title"
		)

		leavingTitleLabel.font = departureDate == nil ? .BodySmall_Semibold() : .BodySmall()
        leavingTitleLabel.text = PILocalizedString("leavingCellLabel")
		leavingValueLabel.text = departureDate?.localizedVeryShortStringFormat ?? PILocalizedString(
			"calendarSelectDateLableTitle",
			comment: "Calendar select date label title"
		)

		refreshDoneButton(animated: animated)
	}

	private func refreshDoneButton(animated: Bool) {
        guard let arrivalDate = arrivalDate, let departureDate = departureDate else {
            if showSubmitButtonOnDateSelection {
                showDoneButton(show: false, animated: animated)
            }
            return
        }

        submitButton.titleLabel?.numberOfLines = 0

        submitButton.accessibilityHint = PILocalizedString("checkInCellLabel") + Date
        	.accFullDateSpoken(date: arrivalDate) + PILocalizedString("checkOutCellLabel") + Date
        	.accFullDateSpoken(date: departureDate)
        submitButton.setAttributedTitle(
        	attributedStringForConfirmButton(with: arrivalDate, and: departureDate),
        	for: .normal
        )

        if showSubmitButtonOnDateSelection {
            showDoneButton(show: true, animated: animated)
        }
	}

    var maxNightsMessage: String {
        String.localizedStringWithFormat(
        	PILocalizedString("criteriaMaxNightsErrorMessage", comment: "User preference user registered banner title"),
        	rulesToFollow.maxNights
        )
    }

	private func showCallAlert() {
        NotificationFeedbackManager.shared.provideFeedback(for: .error)
		guard let controller = AlertManager.callUsAlert(
			withTitle: maxNightsMessage,
			message: PILocalizedString("telephoneNoCost", comment: "Call cost message"),
			number: CallNumberType.generic.phoneNumber
		) else { return }

		present(controller, animated: true)
	}
}

extension AlternateCalendarViewController: AlternateCalendarViewControllerProtocol {
    func toggle(is processing: Bool) {
        if processing {
            guard let window = UIApplication.shared.currentWindow() else { return }

            processingView = ProcessingView(frame: window.bounds)
            guard let processingView = processingView else { return }
            window.addSubview(processingView)
        } else {
            UIView.animate(
            	withDuration: .ocd,
            	animations: {
                    self.processingView?.alpha = 0
                },
            	completion: { _ in
                self.processingView?.removeFromSuperview()
            }
            )
        }
    }

    func showError(with message: String) {
        error.text = message
        let idealHeight = error.sizeThatFits(CGSize(width: error.frame.width, height: CGFloat.greatestFiniteMagnitude))

        errorContainerHeightConstraint.constant = idealHeight.height + (error.frame.origin.y * 2)
    }

    func hideError() {
        errorContainerHeightConstraint.constant = 0
    }

    func showDoneButton(show: Bool, animated: Bool) {
        submitButtonContainer.layer.shadowOffset = CGSize(width: 0.0, height: -1.0)
        submitButtonContainer.layer.shadowColor = UIColor.black.cgColor
        submitButtonContainer.layer.shadowOpacity = 0.5
        submitButtonContainer.layer.shouldRasterize = true
        submitButtonContainer.layer.rasterizationScale = UIScreen.main.scale

        submitButtonBottomConstraint.constant = show ? 10 : -200

        if animated {
            UIView.animate(withDuration: .ocd) {
                self.submitButtonContainer.layoutIfNeeded()
            }
        }
    }
}

extension AlternateCalendarViewController {
	private func resetDay(contentView: UIView) {
		toggle(view: contentView, is: false)
	}

	private func toggle(view: UIView, dayLabel: UILabel? = nil, is today: Bool) {
		view.viewWithTag(.todayLabel)?.removeFromSuperview()

		if today {
			let label = UILabel(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: .todayLabelHeight))
			label.backgroundColor = .clear
			label.textColor = .TintL1
			label.font = .SubtextSmallStrong()
			label.text = PILocalizedString("Today", comment: "")
			label.textAlignment = .center
			label.autoresizingMask = [.flexibleLeftMargin, .flexibleRightMargin]
			label.tag = .todayLabel

			view.addSubview(label)

			view.backgroundColor = .BaseWhite
			dayLabel?.highlightedTextColor = .TintD1
		}
	}

	private func customiseEmptyDayCell(with contentView: UIView, at indexPath: IndexPath) {
		styleUnselected(with: UILabel(), and: contentView)

		guard let arrivalDate = arrivalDate, let departureDate = departureDate else { return }
		guard let arrivalIndexPath = indexPathForDate(arrivalDate) else { return }
		guard let departureIndexPath = indexPathForDate(departureDate) else { return }
		guard indexPath > arrivalIndexPath && indexPath < departureIndexPath else { return }

		styleSelected(with: UILabel(), contentView: contentView, and: .middle)
	}

	private func styleUnselected(with label: UILabel, and contentView: UIView) {
		label.textColor = .TintD1
		label.highlightedTextColor = label.textColor
		label.font = .BodySmall()

		contentView.backgroundColor = .clear
	}

	private func styleSelected(with label: UILabel, contentView: UIView, and maskPosition: DayCellMaskPosition) {
		label.textColor = .BaseWhite
        label.highlightedTextColor = label.textColor
		label.font = UIFont.BodySmall_Semibold()

		contentView.backgroundColor = .Tint1
	}

	private func attributedStringForConfirmButton(with arrivalDate: Date, and departureDate: Date) -> NSAttributedString? {
		let prefixString = submitButtonPrefix
		let suffixString = "(\(String.localizedStringWithFormat(PILocalizedString("%d night(s)", comment: "Message shown for number of nights"), arrivalDate.daysToCheckInDate(departureDate))))"
		let fullString = String(prefixString + "\n" + suffixString)

		let paragraphStyle = NSMutableParagraphStyle()
		paragraphStyle.alignment = .center

		let mutableString = NSMutableAttributedString(
			string: fullString,
			attributes: [.foregroundColor: UIColor.BaseWhite, .paragraphStyle: paragraphStyle]
		)

		let prefixRange = NSRange(location: 0, length: prefixString.count)
		mutableString.addAttributes([.font: UIFont.Body_Semibold()], range: prefixRange)

		guard let suffixRange = fullString.ranges(of: suffixString).first
			else { return NSAttributedString(attributedString: mutableString) }
		mutableString.addAttributes([.font: UIFont.BodySmall()], range: suffixRange)

		return NSAttributedString(attributedString: mutableString)
	}
}

extension AlternateCalendarViewController: SimpleCalendarDelegate {
    func selectedDate(date: Date) {
        guard let arrivalDate = arrivalDate,
              !date.isBefore(date: arrivalDate),
              !date.isOnTheSameDateAs(date: arrivalDate),
              departureDate == nil else {
            self.arrivalDate = date
            departureDate = nil

            reloadCalendar()
            calendarDelegate?.calendarDidInvalidate()

            announceArrivalSelection(date)
            return
        }

        guard arrivalDate.daysToCheckInDate(date) <= rulesToFollow.maxNights else {
            showCallAlert()
            return
        }

        departureDate = date
        reloadCalendar()

        let totalNights = arrivalDate.daysToCheckInDate(date)
        calendarDelegate?.calendarDidChange(arrivalDate: arrivalDate, nights: totalNights)

        announceDepartureSelection(date)
    }
}

private extension AlternateCalendarViewController {
    enum Constants {
        static let arrivalDateAccessibilityMessage = "calendarArrivalDateSelectedAccessibilityMessage"
        static let departureDateAccessibilityMessage = "calendarDepartureDateSelectedAccessibilityMessage"
    }

    func announceArrivalSelection(_ arrivalDate: Date) {
        let message = accessibilityMessage(
            key: Constants.arrivalDateAccessibilityMessage,
            date: arrivalDate
        )

        announceSelection(with: message)
    }

    func announceDepartureSelection(_ departureDate: Date) {
        let message = accessibilityMessage(
            key: Constants.departureDateAccessibilityMessage,
            date: departureDate
        )

        announceSelection(with: message)
    }

    func accessibilityMessage(key: String, date: Date) -> String {
        let localizedFormat = PILocalizedString(key)
        let spokenDate = Date.accFullDateSpoken(date: date)
        return String.localizedStringWithFormat(localizedFormat, spokenDate)
    }

    func announceSelection(with message: String) {
        UIAccessibility.post(notification: .announcement, argument: message)
    }
}
