//
//  RoomRequirementsView.swift
//  PremierInn
//
//  Created by Nick Jones on 01/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit
import SimpleNetwork

private extension String {
    static let submitButtonTag = "SubmitButton"
}

protocol RoomRequirementsViewInput: AnyObject {
    var parentNavigationController: UINavigationController? { get }

    func reload(with: RoomRequirementsModel)
    func provideSmallHapticFeedback()
    func toggleLoading(isLoading: Bool)
}

class RoomRequirementsView: BaseViewController {
    override var screenName: String {
        presenter?.roomRequirementsTracking.screenName ?? super.screenName
    }
    override var screenType: String {
        presenter?.roomRequirementsTracking.screenType ?? super.screenType
    }

    var presenter: RoomRequirementsPresenterInput?

    private var viewModel: FormekaViewModel?

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            ))
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 140
            table.sectionFooterHeight = 50
            table.backgroundColor = .BaseWhite
            table.separatorStyle = .none

            registerCells()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString(
            "roomRequirementsPreferencesTitle",
            comment: "User Preference Room requirements screen title */"
        )

        view.backgroundColor = .whiteTwo

        presenter?.viewIsReady()
    }

    private func registerCells() {
        table.registerCellNib(with: AdultsStepperCell.self)
        table.registerCellNib(with: ChildrenStepperCell.self)
        table.registerCellNib(with: NextGenCotSelectorCell.self)
        table.registerCellNib(with: NextGenRoomTypeSelectorCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
    }
}

private extension RoomRequirementsView {
    func viewModelSections(with model: RoomRequirementsModel) -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()
        sections.append(roomSection(with: model))
        sections.append(submitButtonSection())

        return sections
    }

    func roomSection(with model: RoomRequirementsModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { [weak self] (indexPath, _, table) in
            guard let cell: AdultsStepperCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.detail?.textColor = .slateGrey
            cell.setup(
                with: RangeManager(range: 1...Constants.Config.maxNumberOfAdults, currentStep: model.adults),
                formatString: PILocalizedString("%d", comment: "Message shown for number of adults")
            )

            return cell
        }))

        rows.append(FormekaModelRow(cellSetup: { [weak self] (indexPath, _, table) in
            guard let cell: ChildrenStepperCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.detail?.textColor = .slateGrey
            cell.setup(
                with: RangeManager(range: 0...Constants.Config.maxNumberOfChildren, currentStep: model.children),
                formatString: PILocalizedString("%d", comment: "Message shown for number of children")
            )

            return cell
        }))

        rows.append(cotSwitchRow(cotRequired: model.shouldIncludeCot))

        rows.append(FormekaModelRow(cellSetup: { (indexPath, _, table) in
            guard let cell: NextGenRoomTypeSelectorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.roomTypeSelectorButton?.isEnabled = true
            cell.roomTypeLabel.text = model.roomType.name

            return cell
        }, didSelect: { [weak self] _, _ in
            self?.presenter?.roomTypeDidSelect()
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func cotSwitchRow(cotRequired: Bool) -> FormekaModelRow {
        FormekaModelRow(
            tag: String(describing: NextGenCotSelectorCell.self) + "\(String(describing: index))",
            cellSetup: { [unowned self] (indexPath, _, table) in
            guard let cell: NextGenCotSelectorCell = table.dequeueCell(for: indexPath) else { return nil }

            let includeCotString = PILocalizedString("criteriaIncludeCotSwitchLabel")
            let mutableString = NSMutableAttributedString(
                string: includeCotString,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Heading4_Semibold()]
            )
            let yearsOld = PILocalizedString("infantYearsRange")
            let mutableYearsOld = NSMutableAttributedString(
                string: yearsOld,
                attributes: [.foregroundColor: UIColor.TintD1, .font: UIFont.Body()]
            )
            mutableString.append(mutableYearsOld)
            cell.titleLabel.attributedText = NSAttributedString(attributedString: mutableString)

            cell.delegate = self
            cell.cotSelectorSwitch.isOn = cotRequired

            return cell
        }
        )
    }

    private func submitButtonSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(tag: .submitButtonTag, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.button.backgroundColor = .Tint1
            cell.button.setTitleColor(.white, for: .normal)
            cell.button.setTitle(
                PILocalizedString("userPreferenceRoomRequirementsSaveButtonTitle", comment: "Save changes button title"),
                for: .normal
            )
            cell.button.titleLabel?.font = .Body_Semibold()

            cell.button.layer.cornerRadius = 5
            cell.button.layer.borderColor = UIColor.Tint1.cgColor
            cell.button.layer.borderWidth = 1

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}

extension RoomRequirementsView: RoomRequirementsViewInput {
    var parentNavigationController: UINavigationController? { navigationController }

    func reload(with model: RoomRequirementsModel) {
        viewModel = FormekaViewModel(sections: viewModelSections(with: model))

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table.reloadData()
    }

    func toggleLoading(isLoading: Bool) {
        guard let cell: FormekaSubmitButtonCell = viewModel?.cell(forRowNamed: .submitButtonTag, table: table)
            else { return }

        cell.button.setTitle(
            isLoading ? nil :
                PILocalizedString("userPreferenceRoomRequirementsSaveButtonTitle", comment: "Save changes button title"),
            for: .normal
        )

        if isLoading {
            cell.activityIndicator.startAnimating()
        } else {
            cell.activityIndicator.stopAnimating()
        }
        toggleViewFader(toBeVisible: isLoading)
    }

    func provideSmallHapticFeedback() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
    }
}

extension RoomRequirementsView: CustomStepperCellDelegate {
    func customStepperCellDidChangeValue(cell: CustomStepperCell, value: Int) {
        switch cell {
        case is AdultsSelectorCell:
            presenter?.adultRequirementsChanged(to: value)

        case is ChildrenSelectorCell:
            presenter?.childrenRequirementsChanged(to: value)

        default:
            break
        }
    }

    func customStepperCellDidFailChangingValue(cell: CustomStepperCell, error: NSError) {
        print(error)
    }
}

extension RoomRequirementsView: CotSelectorCellDelegate {
    func cotSwitchDidChange(cell: CotSelectorCell) {
        var value: Bool {
            guard cell.cotSelectorSwitch != nil else { return false }

            return cell.cotSelectorSwitch.isOn
        }

        presenter?.cotRequirementChanged(toRequired: value)
    }
}

extension RoomRequirementsView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        presenter?.saveChanges()
    }
}
