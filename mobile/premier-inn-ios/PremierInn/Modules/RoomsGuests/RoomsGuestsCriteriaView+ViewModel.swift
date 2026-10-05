//
//  RoomsGuestsCriteriaView+ViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

protocol CellWithRuleErrorColorToggleCell: AnyObject {
    var ruleLabel: UILabel? { get }
}

extension CustomStepperCell {
	func setup(with rangeManager: RangeManager, formatString: String) {
		self.rangeManager = rangeManager

		if let manager = self.rangeManager {
			minusButton.isEnabled = !manager.isLowerLimit()
			plusButton.fakeDisabled = manager.isUpperLimit()
			label.text = String.localizedStringWithFormat(formatString, manager.currentStep)
		}
	}
}

extension RoomsGuestsCriteriaView {
    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())
        viewModel?.delegate = self

        table?.delegate = viewModel
        table?.dataSource = viewModel
        table?.reloadData()
    }

    private func viewModelSections() -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        presenter?.selectedCriteria?.rooms.enumerated().forEach { index, room in
            sections.append(roomSection(with: room, at: index, with: presenter?.infants(for: index) ?? 0))
        }

        if presenter?.shouldShowAddRoomSection ?? true {
            sections.append(addAdditionalRoomSection())
        } else {
            sections.append(infoBoxSection())
        }

        return sections
    }

    func roomSection(with room: Room, at index: Int, with infants: Int = 0) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

		rows.append(FormekaModelRow(cellSetup: { [unowned self] (indexPath, _, table) in
			guard let cell: AdultsStepperCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.delegate = self
			cell.detail?.textColor = .slateGrey
			cell.setup(
			    with: RangeManager(range: 1...Constants.Config.maxNumberOfAdults, currentStep: room.adults),
			    formatString: PILocalizedString("%d", comment: "Message shown for number of adults")
			)

			return cell
		}))

		rows.append(FormekaModelRow(
		    tag: String(describing: ChildrenStepperCell.self) + "\(index)",
		    cellSetup: { [unowned self] (indexPath, _, table) in
			guard let cell: ChildrenStepperCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.delegate = self
			cell.detail?.textColor = .slateGrey
			cell.setup(
			    with: RangeManager(range: 0...Constants.Config.maxNumberOfChildren, currentStep: room.children),
			    formatString: PILocalizedString("%d", comment: "Message shown for number of children")
			)

			return cell
		}
		))

        rows.append(FormekaModelRow(
            tag: String(describing: InfantsStepperCell.self) + "\(index)",
            cellSetup: { [unowned self] (indexPath, _, table) in
            guard let cell: InfantsStepperCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.setup(
                with: RangeManager(range: 0...Constants.Config.maxNumberOfInfants, currentStep: infants),
                formatString: PILocalizedString("%d", comment: "Message shown for number of children")
            )

            return cell
        }
        ))

		if infants > 0 {
			rows.append(cotSwitchRow(cotRequired: room.cotRequired, index: index))
		}

        rows.append(
            FormekaModelRow(
                tag: String(describing: NextGenRoomTypeSelectorCell.self) + "\(index)",
                cellSetup: { (indexPath, _, table) in
                    guard let cell: NextGenRoomTypeSelectorCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.roomTypeSelectorButton?.accessibilityIdentifier = "roomType"
                    cell.roomTypeSelectorButton?.isEnabled = true
                    cell.roomTypeLabel.text = room.type.name

                    return cell
                },
                didSelect: { [unowned self] indexPath, _ in
                    presenter?.roomTypeButtonDidSelect(at: indexPath)
                }
            )
        )

		return FormekaModelSection(
		    header: roomHeader(title: PILocalizedString("Room", comment: "") + " \(index + 1)", canDelete: index > 0),
		    rows: rows,
		    footer: simpleFooter(lineColor: .TintL2)
		)
    }

    private func infoBoxSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(iconInfoRow(
            tag: String(describing: FlexibleContentInformationCell.self),
            text: PILocalizedString("addOnlyOneRoomMessage"),
            backgroundColor: .clear,
            style: .info
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

	private func addAdditionalRoomSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(
            tag: String(describing: AddRoomCell.self),
            cellSetup: { [unowned self] (indexPath, _, table) in
            guard let cell: AddRoomCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
			(cell.errorView as? ErrorAndPhoneViewInCell)?.delegate = self

			cell.contentView.backgroundColor = .whiteTwo

            cell.addButton.tintColor = .TintD2
            cell.addButton.setImage(nil, for: .normal)
            cell.addButton.setTitleColor(UIColor.TintD2, for: .normal)
            cell.addButton.backgroundColor = .TintL3
            cell.addButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = PILocalizedString("addRoomButtonTitle", comment: "Add room button title")
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var title = attribute
                    title.font = UIFont.Button1()
                    return title
                }
                config.contentInsets = NSDirectionalEdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20)
                button.configuration = config
            }
            cell.layer.borderColor = UIColor.TintL2.cgColor

            return cell
        }
        ))

		return FormekaModelSection(header: nil, rows: rows, footer: simpleFooter(lineColor: .clear))
    }

    private func roomHeader(title: String?, canDelete: Bool) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 66, viewSetup: { [weak self] _, table in
            guard let view: ActionableHeader = table.headerFooterView() else { return nil }

            view.heading.text = title
            view.heading.textColor = .BasePurple
            view.heading.font = UIFont.Heading1_Semibold()

			view.actionButton.setTitle(PILocalizedString("Remove", comment: ""), for: .normal)
            view.actionButton.isHidden = canDelete ? false : true
			view.actionButton.actionTouchUp {
				for section in 0..<table.numberOfSections where table.headerView(forSection: section) == view {
                    self?.presenter?.deleteRoomButtonDidTap(at: section)
                    break
				}
			}

            return view
        })
    }

	private func simpleFooter(lineColor: UIColor) -> FormekaModelHeaderFooter {
		FormekaModelHeaderFooter(height: 10, viewSetup: { _, table in
			let view: SimpleFooter? = table.headerFooterView()
			view?.contentView.backgroundColor = .whiteTwo
			view?.lineView.backgroundColor = lineColor

			return view
		})
	}

	func cotSwitchRow(cotRequired: Bool, index: Int) -> FormekaModelRow {
		FormekaModelRow(
		    tag: String(describing: NextGenCotSelectorCell.self) + "\(index)",
		    cellSetup: { [unowned self] (indexPath, _, table) in
			guard let cell: NextGenCotSelectorCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.delegate = self
			cell.cotSelectorSwitch.isOn = cotRequired

			return cell
		}
		)
	}
}

extension RoomsGuestsCriteriaView: RoomTypeSelectRouterDelegate {
    func roomTypeSelectViewControllerDidUpdate(roomType: SelectableRoomType) {
        guard let roomType = roomType as? RoomType else { return }
        presenter?.roomTypeDidSelect(roomType)
    }
}

extension RoomsGuestsCriteriaView: AddRoomCellDelegate {
    func addRoomCellDidTap(cell: AddRoomCell) {
        presenter?.addRoomButtonDidTap()
    }
}

extension RoomsGuestsCriteriaView: ErrorAndPhoneViewProtocol {
	func callCustomerService() {
		presenter?.callCustomerServiceButtonDidTap()
	}
}

extension RoomsGuestsCriteriaView: CustomStepperCellDelegate {
    func customStepperCellDidChangeValue(cell: CustomStepperCell, value: Int) {
        switch cell {
        case is AdultsStepperCell:
            if let indexPath = table.indexPath(for: cell) {
                presenter?.numberOfAdultsDidChange(value: value, at: indexPath)
            }
        case is ChildrenStepperCell:
            if let indexPath = table.indexPath(for: cell) {
                presenter?.numberOfChildrenDidChange(value: value, at: indexPath)
            }
        case is InfantsStepperCell:
            if let indexPath = table.indexPath(for: cell) {
                presenter?.numberOfInfantsDidChange(value: value, at: indexPath)
            }
        default:
            break
        }
	}

    func customStepperCellDidFailChangingValue(cell: CustomStepperCell, error: NSError) {
        switch cell {
        case is AdultsStepperCell, is ChildrenStepperCell:
            if let indexPath = table.indexPath(for: cell) {
                presenter?.numberOfPeopleNotAllowed(at: indexPath, error: error)
            }
        case is InfantsStepperCell:
            if let indexPath = table.indexPath(for: cell) {
                presenter?.numberOfInfantsNotAllowed(at: indexPath, error: error)
            }
        default:
            break
        }
	}
}

extension RoomsGuestsCriteriaView: CotSelectorCellDelegate {
	func cotSwitchDidChange(cell: CotSelectorCell) {
		if let indexPath = table.indexPath(for: cell) {
			presenter?.cotValueDidChange(value: cell.cotSelectorSwitch.isOn, at: indexPath)
		}
	}
}
