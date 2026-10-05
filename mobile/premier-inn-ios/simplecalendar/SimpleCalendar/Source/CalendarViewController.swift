//
//  CalendarViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 01/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

public protocol SimpleCalendarDelegate: AnyObject {

    func selectedDate(date: Date)
}

public struct SimpleCalendarSettings {

    public struct Colors {

        public let weekdayLabel: UIColor
        public let weekendLabel: UIColor
        public let selectable: UIColor
        public let highlighted: UIColor
        public let highlightedBackground: UIColor
        public let selected: UIColor
        public let notSelectable: UIColor
        public let footer: UIColor
        public let monthName: UIColor
        public let today: UIColor
        public let monthHeaderSeparator: UIColor

        public init(weekday: UIColor, weekend: UIColor, selectable: UIColor, highlighted: UIColor, highlightedBackground: UIColor, selected: UIColor, notSelectable: UIColor, footer: UIColor, month: UIColor, today: UIColor, monthHeaderSeparator: UIColor = UIColor(red: 242/255, green: 242/255, blue: 242/255, alpha: 1)) {

            self.weekdayLabel = weekday
            self.weekendLabel = weekend
            self.selectable = selectable
            self.highlighted = highlighted
            self.highlightedBackground = highlightedBackground
            self.selected = selected
            self.notSelectable = notSelectable
            self.monthName = month
            self.footer = footer
            self.today = today
            self.monthHeaderSeparator = monthHeaderSeparator
        }
    }

    public struct Fonts {

        public let weekdaysFont: UIFont
        public let daysFont: UIFont
        public let footerFont: UIFont
        public let monthFont: UIFont

        public init(weekdays: UIFont, days: UIFont, footer: UIFont, month: UIFont) {

            self.weekdaysFont = weekdays
            self.daysFont = days
            self.footerFont = footer
            self.monthFont = month
        }
    }

    public struct Metrics {
        let monthHeaderHeight: CGFloat
		let weekdayHeaderMargin: CGFloat

		public init(monthHeaderHeight: CGFloat = 117, weekdayHeaderMargin: CGFloat = 0) {

            self.monthHeaderHeight = monthHeaderHeight
			self.weekdayHeaderMargin = weekdayHeaderMargin
        }
    }

    public let colors: Colors
    public let fonts: Fonts
    public let metrics: Metrics
    public let footerText: String
    public let selectedDate: Date
    public let startDate: Date
    public let selectableDatesOffset: Int
    public let monthSpan: Int
    public let freeSelection: Bool
    public let customTitle: String
    public let monthAlignment: NSTextAlignment
    public let closeButtonTitle: String
    public let showTodayButton: Bool
    public let maxArrivalDateLimitation: Int

    public init(colors: Colors, fonts: Fonts, metrics: Metrics = Metrics(), footerText: String = "", selectedDate: Date, startDate: Date = Date(), selectableDatesOffset: Int, monthSpan: Int = 13, freeSelection: Bool = false, andCustomTitle title: String = NSLocalizedString("Select a date", comment: "Calendar view controller title"), monthAlignment: NSTextAlignment = .center, closeButtonTitle: String = "Cancel", showTodayButton: Bool = true, maxDepartureDateCount: Int) {

        self.colors = colors
        self.fonts = fonts
        self.metrics = metrics
        self.footerText = footerText
        self.selectedDate = selectedDate
        self.startDate = startDate
        self.selectableDatesOffset = selectableDatesOffset
        self.monthSpan = monthSpan
        self.freeSelection = freeSelection
        self.customTitle = title
        self.monthAlignment = monthAlignment
        self.closeButtonTitle = closeButtonTitle
        self.showTodayButton = showTodayButton
        self.maxArrivalDateLimitation = maxDepartureDateCount

    }
}

public struct CalendarMonthFooter {

    let type: UICollectionReusableView.Type
    let view: (_ collectionView: UICollectionView, _ kind: String, _ indexPath: IndexPath) -> UICollectionReusableView
    let height: CGFloat

    public init(type: UICollectionReusableView.Type, view: @escaping (_ collectionView: UICollectionView, _ kind: String, _ indexPath: IndexPath) -> UICollectionReusableView, height: CGFloat) {

        self.type = type
        self.view = view
        self.height = height
    }
}

public protocol CalendarCellAttributes {

    var dayLabel: UILabel { get }
    var contentView: UIView { get }
}

open class CalendarViewController: UIViewController {

	public weak var delegate: SimpleCalendarDelegate?
    public var monthFooter: CalendarMonthFooter?

    public let settings: SimpleCalendarSettings
    public let months: [CalendarMonth]
    private var shouldScrollToCurrentDate = true

    @IBOutlet public weak var calendarView: UICollectionView! {
        didSet {
            calendarView.backgroundColor = .white

            calendarView.registerCellForNib(with: CalendarDayCell.self)
            calendarView.registerSupplementaryViewNib(with: CalendarHeader.self, kind: UICollectionView.elementKindSectionHeader)
            calendarView.registerSupplementaryViewNib(with: CalendarFooter.self, kind: UICollectionView.elementKindSectionFooter)
            calendarView.registerSupplementaryViewNib(with: CalendarFooterReusableView.self, kind: UICollectionView.elementKindSectionFooter)

            if let monthFooter = monthFooter {
                calendarView.registerSupplementaryViewNib(with: monthFooter.type, kind: UICollectionView.elementKindSectionFooter)
            }
        }
    }

    deinit {
        print("DEINIT \(self)")
    }

	override open var preferredStatusBarStyle: UIStatusBarStyle {

        return .lightContent
    }

    public init(
        settings: SimpleCalendarSettings,
        nibName: String = String(describing: CalendarViewController.self),
        bundle: Bundle? = nil
    ) {
        self.settings = settings
        self.months = CalendarViewModel.calendarMonthsUsing(
            startDate: settings.startDate,
            months: settings.monthSpan,
            selectableOffset: settings.selectableDatesOffset,
            maxDepartureDateCount: settings.maxArrivalDateLimitation
        )

        let bundleToUse = bundle ?? .module
        super.init(
            nibName: nibName,
            bundle: bundleToUse
        )
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

	open override func viewDidLoad() {
        super.viewDidLoad()

        title = settings.customTitle

        navigationItem.leftBarButtonItem = UIBarButtonItem(title: NSLocalizedString(settings.closeButtonTitle, comment: "Close button title"), style: .plain, target: self, action: #selector(closeCalendarButtonTap))
    }

	override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        reloadCalendar()
        scrollToCurrentDateIfNeeded(animated: animated)
    }


    // MARK: -


    public func indexPathForDate(_ date: Date) -> IndexPath? {

        for month in months {
            if let day = (month.days.first { $0.date?.isOnTheSameDateAs(date: date) ?? false }) {
                return IndexPath(row: month.days.firstIndex(of: day) ?? 0, section: months.firstIndex(of: month) ?? 0)
            }
        }
        return nil
    }

    public func month(for indexPath: IndexPath) -> CalendarMonth? {

        guard indexPath.section < months.count else { return nil }

        return  months[indexPath.section]
    }

    public func day(for indexPath: IndexPath) -> CalendarDay? {

        guard let month = month(for: indexPath) else { return nil }
        guard indexPath.row < month.days.count else { return nil }

        return month.days[indexPath.row]
    }

    private func scrollToCurrentDateIfNeeded(animated: Bool) {

        guard shouldScrollToCurrentDate else { return }
        guard calendarView != nil else { return }
        guard let indexPath = indexPathForDate(settings.selectedDate) else { return }

        shouldScrollToCurrentDate = false

        calendarView.scrollToItem(at: indexPath , at: .centeredVertically, animated: animated)
	}

    public func reloadCalendar() {

        calendarView.reloadData()
    }

    func closeCalendar() {

        dismiss(animated: true, completion: nil)
    }

    @IBAction func todayButtonDidTap(_ sender: UIBarButtonItem) {

        guard let indexPath = indexPathForDate(Date()) else { return }

        calendarView.scrollToItem(at: indexPath, at: .centeredVertically, animated: true)
    }

    @IBAction func closeCalendarButtonTap(_ sender: UIBarButtonItem) {

        closeCalendar()
    }
}

extension CalendarViewController: UICollectionViewDelegate, UICollectionViewDataSource {

    open func numberOfSections(in collectionView: UICollectionView) -> Int {

        return months.count
    }

    open func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {

        return months[section].days.count
    }

    open func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {

        let calendarMonth = months[indexPath.section]

        switch kind {

        case UICollectionView.elementKindSectionHeader:
            if let calendarHeader = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: String(describing: CalendarHeader.self), for: indexPath) as? CalendarHeader {

                calendarHeader.setCustomColours(weekday: settings.colors.weekdayLabel, weekend: settings.colors.weekendLabel, month: settings.colors.monthName, separator: .white)
                calendarHeader.setCustomFonts(day: settings.fonts.weekdaysFont, month: settings.fonts.monthFont)
                calendarHeader.monthLabel.text = calendarMonth.title
                calendarHeader.monthLabel.textAlignment = settings.monthAlignment

                return calendarHeader
            }

            return UICollectionReusableView()

        case UICollectionView.elementKindSectionFooter:
            if calendarMonth == months.last {
                if let lastFooter = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: String(describing: CalendarFooterReusableView.self), for: indexPath) as? CalendarFooterReusableView {

                    lastFooter.label.text = settings.footerText
                    lastFooter.label.textColor = settings.colors.footer
                    lastFooter.label.font = settings.fonts.footerFont

                    return lastFooter
                }
            } else if let monthFooter = monthFooter {
                return monthFooter.view(collectionView, kind, indexPath)
            }

            return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: String(describing: CalendarFooter.self), for: indexPath)

        default:
            return UICollectionReusableView()
        }
    }

    open func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        guard let cell: CalendarDayCell = collectionView.dequeueCell(for: indexPath) else { return UICollectionViewCell() }

        cell.isSelected = false
        cell.dayLabel.text = ""

        guard indexPath.section < months.count else { return cell }
        let month = months[indexPath.section]
        guard indexPath.row < month.days.count else { return cell }
        let day = month.days[indexPath.row]

        cell.setCustomColours(selectable: settings.colors.selectable, notSelectable: settings.colors.notSelectable, selected: settings.colors.selected, highlighted: settings.colors.highlighted, highlightedBackground: settings.colors.highlightedBackground)
        cell.setCustomFont(settings.fonts.daysFont)

        cell.dayLabel.text = day.title
        if let dateFormatted = day.date {
            cell.dayLabel.accessibilityIdentifier = Date.englishLocaleDateString(date: dateFormatted)
        }
        cell.isSelectable = {
            guard settings.freeSelection == false else { return true }
            return day.isSelectable
        }()

        if day.date?.isOnTheSameDateAs(date: Date()) == true {
            cell.contentView.backgroundColor = settings.colors.today
        }

        if day.date?.isOnTheSameDateAs(date: settings.selectedDate) == true {
            cell.isSelected = true
        }

		return cell
    }

    open func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {

        let day = months[indexPath.section].days[indexPath.row]

        guard day.isSelectable || settings.freeSelection else { return }

        if let date = day.date {
            delegate?.selectedDate(date: date)
        }
    }
}

extension CalendarViewController: UICollectionViewDelegateFlowLayout {

    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {

        let cellSq = (collectionView.frame.width - settings.metrics.weekdayHeaderMargin * 2) / CGFloat(Constants.Metrics.cellsPerRow)
        let width: CGFloat = {
            // Add the remainder to the first day of the week
            // so that we have a row with no gaps between cells
            var result = cellSq.rounded()
            let remainder = collectionView.frame.width - result * CGFloat(Constants.Metrics.cellsPerRow)

            if indexPath.row % Constants.Metrics.cellsPerRow == 0 {
                result += remainder
            }

            return result
        }()

        return CGSize(width: width, height: cellSq)
    }

    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {

        return UIEdgeInsets(top: 0, left: settings.metrics.weekdayHeaderMargin, bottom: 0, right: settings.metrics.weekdayHeaderMargin)
    }

    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {

        return CGSize(width: collectionView.contentSize.width, height: settings.metrics.monthHeaderHeight)
    }

    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForFooterInSection section: Int) -> CGSize {

        let calendarMonth = months[section]

        if calendarMonth == months.last {
            return CGSize(width: collectionView.contentSize.width, height: Constants.Metrics.monthFooterLastHeight)
        } else if let monthFooter = monthFooter {
            return CGSize(width: collectionView.contentSize.width, height: monthFooter.height)
        }

        return CGSize(width: collectionView.contentSize.width, height: Constants.Metrics.monthFooterSeparatorHeight)
    }

    public func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {

        return 2
    }
}

extension CalendarViewController: UIScrollViewDelegate {

    public func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {

        toggleTodayButton()
    }

    public func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {

        toggleTodayButton()
    }

    public func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {

        if decelerate == false {
            toggleTodayButton()
        }
    }

    public func scrollViewDidScrollToTop(_ scrollView: UIScrollView) {

        toggleTodayButton()
    }

    @objc open func toggleTodayButton() {

        guard settings.showTodayButton else { return }
        guard let indexPath = indexPathForDate(Date()) else { return }

        if calendarView.indexPathsForVisibleItems.contains(indexPath) {
            navigationItem.setRightBarButton(nil, animated: true)
        } else {
            if navigationItem.rightBarButtonItem == nil {
                let button = UIBarButtonItem(title: NSLocalizedString("Today", comment: "Today button title"), style: .plain, target: self, action: #selector(todayButtonDidTap))
                navigationItem.setRightBarButton(button, animated: true)
            }
        }
    }
}
