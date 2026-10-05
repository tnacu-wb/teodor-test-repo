//
//  RoomsGuestsCriteriaView.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol RoomsGuestsCriteriaPresenterProtocol: AnyObject {
    var selectedCriteria: Criteria? { get }
    var shouldShowAddRoomSection: Bool { get }

    func roomTypeDidSelect(_ type: RoomType)
    func viewIsReady()
    func addRoomButtonDidTap()
    func deleteRoomButtonDidTap(at index: Int)
	func callCustomerServiceButtonDidTap()
	func numberOfAdultsDidChange(value: Int, at indexPath: IndexPath)
	func numberOfChildrenDidChange(value: Int, at indexPath: IndexPath)
    func numberOfInfantsDidChange(value: Int, at indexPath: IndexPath)
	func cotValueDidChange(value: Bool, at indexPath: IndexPath)
	func numberOfPeopleNotAllowed(at indexPath: IndexPath, error: Error)
    func numberOfInfantsNotAllowed(at indexPath: IndexPath, error: Error)
	func roomTypeButtonDidSelect(at indexPath: IndexPath)
    func infants(for roomIndex: Int) -> Int
}

protocol RoomsGuestsCriteriaViewProtocol: AnyObject {
    func appendRoomSection(with room: Room, index: Int)
	func reloadVisibleHeaders()
	func refreshConfirmButton(with title: NSAttributedString?)
    func removeSection(at index: Int)
	func showError(_ error: Error, at indexPath: IndexPath)
    func reload()
    func callCustomerService(phoneNumber: String)
    func callCustomerServices(phoneNumber: String, title: String, message: String)
    func showRoomTypeController(for room: Room)
	func showAddAdditionalRoomErrorAlert(with controller: UIAlertController)

    func provideSmallHapticFeedback()
    func provideErrorHapticFeedback()
}

protocol RoomsGuestsCriteriaViewEventHandler: AnyObject {
    func criteriaController(_ sender: RoomsGuestsCriteriaView, didFinishWith criteria: Criteria)
    func criteriaControllerDidCancel(_ sender: RoomsGuestsCriteriaView)
}

class RoomsGuestsCriteriaView: FormekaViewController {
    @IBOutlet weak var confirmButton: RoundedCornersButton! {
        didSet {
            confirmButton.accessibilityIdentifier = "doneButton"
            confirmButton.backgroundColor = .Tint1
        }
    }

    var eventHandler: RoomsGuestsCriteriaViewEventHandler?
    var presenter: RoomsGuestsCriteriaPresenterProtocol?

    override var screenName: String { PIAnalytics.StateNames.criteria }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(closeButtonDidTap)
        )
        navigationItem.title = PILocalizedString("roomsGuestTitle", comment: "")

		table.backgroundColor = .whiteTwo

		registerCells()

        presenter?.viewIsReady()
    }

    override var preferredStatusBarStyle: UIStatusBarStyle { .default }

    @objc func closeButtonDidTap() {
        eventHandler?.criteriaControllerDidCancel(self)
    }

    @IBAction func confirmButtonDidTap(_ sender: Any) {
        guard let criteria = presenter?.selectedCriteria else { return }

        eventHandler?.criteriaController(self, didFinishWith: criteria)
    }

    private func registerCells() {
        table.registerCellNib(with: AddRoomCell.self)
        table.registerCellNib(with: AdultsStepperCell.self)
        table.registerCellNib(with: ChildrenStepperCell.self)
        table.registerCellNib(with: InfantsStepperCell.self)
        table.registerCellNib(with: NextGenCotSelectorCell.self)
        table.registerCellNib(with: NextGenRoomTypeSelectorCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
		table.registerHeaderFooterNib(with: ActionableHeader.self)
		table.registerHeaderFooterNib(with: SimpleFooter.self)
    }
}

extension RoomsGuestsCriteriaView: RoomsGuestsCriteriaViewProtocol {
    func showRoomTypeController(for room: Room) {
        let controller = RoomTypeSelectModule.build(with: room.availableRoomTypes(), selectedRoomType: room.type, and: self)

        presentNonFullScreenViewController(controller, animated: true)
    }

    func callCustomerService(phoneNumber: String) {
        showCallHotelAlert(number: phoneNumber)
    }

    func callCustomerServices(phoneNumber: String, title: String, message: String) {
        showCallHotelAlert(number: phoneNumber, title: title, message: message)
    }

    func reload() {
        loadViewModel()
    }

    func showAddAdditionalRoomErrorAlert(with controller: UIAlertController) {
        present(controller, animated: true)
    }

	func showError(_ error: Error, at indexPath: IndexPath) {
		guard let cell = table.cellForRow(at: indexPath) else { return }

        switch cell {
        case let cell as CellWithRuleErrorColorToggleCell:
            cell.ruleLabel?.textColor = .red
            cell.ruleLabel?.shake()

        default:
            table.beginUpdates()
            table.endUpdates()

            if let indexPath = table.indexPath(for: cell) {
                table.scrollToRow(at: indexPath, at: .bottom, animated: true)
            }
        }
    }

    func appendRoomSection(with room: Room, index: Int) {
        let section = roomSection(with: room, at: index)

        viewModel?.add(section: section, index: index)

        table.insertSections([index], with: .automatic)
	}

	func removeSection(at index: Int) {
		viewModel?.remove(sectionAtIndex: index)

		table.deleteSections([index], with: .left)
	}

	func refreshConfirmButton(with title: NSAttributedString?) {
		confirmButton.titleLabel?.numberOfLines = 0
		confirmButton.setAttributedTitle(title, for: .normal)
	}

	func reloadVisibleHeaders() {
		for section in 0..<table.numberOfSections {
			guard let header = table.headerView(forSection: section) as? ActionableHeader else { continue }

			header.heading.text = PILocalizedString("Room", comment: "") + " \(section + 1)"
		}
	}

    func provideSmallHapticFeedback() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
    }

    func provideErrorHapticFeedback() {
        NotificationFeedbackManager.shared.provideFeedback(for: .error)
    }
}
