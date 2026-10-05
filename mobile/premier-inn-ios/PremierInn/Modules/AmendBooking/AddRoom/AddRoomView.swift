//
//  AddRoomView.swift
//  PremierInn
//
//  Created by Nick Jones on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol AddRoomPresenterProtocol: AnyObject {
    var room: Room { get }
    var roomNumber: Int { get }
    var leadGuestTitle: String? { get }
    var leadGuestFirstName: String? { get }
    var leadGuestLastName: String? { get }

    var substitutions: [RoomSubstitution]? { get }

    var screenTitle: String { get }

    var guestsAndRoomTypeDescription: String { get }
    var roomPriceDescription: String? { get }

    var shouldShowRoomPickerLayout: Bool { get }

    var hasAvailabilityBeenChecked: Bool { get }
    var newAvailabilityCheckRequired: Bool { get }

    var cancellable: Bool { get }
    var roomsAreAmendable: Bool { get }
    var guestsAreAmendable: Bool { get }

    func roomTypeDidSelect(_ type: RoomType)
    func viewIsReady()
    func callCustomerServiceButtonDidTap()
    func numberOfAdultsDidChange(value: Int)
    func numberOfChildrenDidChange(value: Int)
    func cotValueDidChange(value: Bool)
    func numberOfPeopleNotAllowed(at indexPath: IndexPath, error: Error)
    func roomTypeButtonDidSelect()
    func checkAvailabilityButtonTapped()
    func continueButtonTapped()

    func firstNameDidChange(to firstName: String)
    func lastNameDidChange(to lastName: String)

    func salutationRowDidTap()
    func setSalutation(_ salutation: String?)
    func changeRoomCriteriaTapped()

    func userHasCheckedAvailability()
    func cancelRoom()
}

protocol AddRoomViewProtocol: AnyObject {
    func appendRoomSection(with room: Room, index: Int)
    func reloadVisibleHeaders()
    func refreshConfirmButton(with title: NSAttributedString?)
    func removeSection(at index: Int)
    func showError(_ error: Error, at indexPath: IndexPath)
    func reload()
    func reloadWithAnimation()
    func callCustomerService(phoneNumber: String)
    func showRoomTypeController(for room: Room)
    func showAddAdditionalRoomErrorAlert(with controller: UIAlertController)

    func provideSmallHapticFeedback()
    func provideErrorHapticFeedback()

    func showLoadingBox()
    func hideLoadingBox()
    func toggle(is processing: Bool)
    func animateConfirmButtonTextChange(with title: NSAttributedString?)
    func showNoAvailabilityBanner(withTitle title: String)
}

protocol AddRoomViewEventHandler: AnyObject {
    func addRoomControllerDidCancel(_ sender: AddRoomView)
}

class AddRoomView: FormekaViewController {
    @IBOutlet weak var confirmButton: RoundedCornersButton! {
        didSet {
            confirmButton.accessibilityIdentifier = "confirmButton"
            confirmButton.titleLabel?.font = .Button1()
            confirmButton.backgroundColor = .Tint1
        }
    }

    var eventHandler: AddRoomViewEventHandler?
    var presenter: AddRoomPresenterProtocol?

    private var loadingView: LoadingView?

    override var screenName: String { PIAnalytics.StateNames.criteria }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(closeButtonDidTap)
        )
        navigationItem.leftBarButtonItem?.accessibilityIdentifier = "cancelBarButtonItem"

        navigationItem.title = presenter?.screenTitle

        table.backgroundColor = .BaseWhite
        table.separatorColor = .ColourLD3

        registerCells()

        presenter?.viewIsReady()
    }

    override var preferredStatusBarStyle: UIStatusBarStyle { .default }

    @objc func closeButtonDidTap() {
        eventHandler?.addRoomControllerDidCancel(self)
    }

    private func registerCells() {
        table.registerCellNib(with: AdultsStepperCell.self)
        table.registerCellNib(with: ChildrenStepperCell.self)
        table.registerCellNib(with: NextGenCotSelectorCell.self)
        table.registerCellNib(with: NextGenRoomTypeSelectorCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: AddRoomDetailsRow.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.registerCellNib(with: CancelButtonCell.self)
        table.registerHeaderFooterNib(with: ActionableHeader.self)
        table.registerHeaderFooterNib(with: SimpleFooter.self)
    }

    @IBAction func checkAvailabilityButtonDidTap(_ sender: UIButton) {
        if (presenter?.shouldShowRoomPickerLayout ?? true) && (presenter?.newAvailabilityCheckRequired ?? true) {
            presenter?.checkAvailabilityButtonTapped()
        } else {
            do {
                try viewModel?.validate()

                presenter?.continueButtonTapped()
            } catch let error as RowValidatorError {
                scrollAndFocus(at: viewModel?.indexPath(for: error.row))
            } catch {
                showErrorAlertWith(
                    title: PILocalizedString("reviewAlertTitle", comment: "Review and Book: alert title"),
                    error: error
                )
            }
        }
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
        }
        )
    }
    private var processingView: ProcessingView?

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

    func hideLoadingBox() {
        guard let loadingView = loadingView else { return }

        UIView.animate(
            withDuration: .ocd,
            animations: {
                loadingView.alpha = 0
            },
            completion: { [weak self] _ in
            self?.view.isUserInteractionEnabled = true
            loadingView.stopAnimating()
            loadingView.removeFromSuperview()
        }
        )
    }

    private func showOptionAlert(with title: String, and message: String, completion: @escaping () -> Void) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("Cancel", comment: "Title for alert cancel button"),
            style: .cancel,
            handler: nil
        ))
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("removeTitle", comment: "Title for alert confirm button"),
            style: .default,
            handler: { _ in
            completion()
        }
        ))

        present(alertController, animated: true, completion: nil)
    }
}

extension AddRoomView: AddRoomViewProtocol {
    func showRoomTypeController(for room: Room) {
        let controller = RoomTypeSelectModule.build(with: room.availableRoomTypes(), selectedRoomType: room.type, and: self)

        presentNonFullScreenViewController(controller, animated: true)
    }

    func callCustomerService(phoneNumber: String) {
        showCallHotelAlert(number: phoneNumber)
    }

    func reload() {
        loadViewModel()
    }

    func reloadWithAnimation() {
        UIView.animate(
            withDuration: .ocd,
            animations: { [weak self] in
                self?.table.alpha = 0
            },
            completion: { [weak self] _ in
                self?.loadViewModel()
                UIView.animate(withDuration: .ocd, animations: { [weak self] in
                    self?.table.alpha = 1
                })
            }
        )
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
        let section = roomSection(with: room)

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

    func animateConfirmButtonTextChange(with title: NSAttributedString?) {
        // No point animating if the title is the same
        if self.confirmButton.attributedTitle(for: .normal) == title { return }

        guard let superViewHeight = confirmButton.superview?.frame.size.height else { return }

        let originalButtonYPosition = confirmButton.frame.origin.y

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: { [weak self] in
                self?.confirmButton.frame.origin.y = superViewHeight + 10
            },
            completion: { _ in
                self.confirmButton.setAttributedTitle(title, for: .normal)
                UIView.animate(
                    withDuration: .ocd,
                    delay: 0,
                    usingSpringWithDamping: 0.7,
                    initialSpringVelocity: 0.9,
                    options: [.curveEaseOut],
                    animations: { [weak self] in
                        self?.confirmButton.frame.origin.y = originalButtonYPosition
                    },
                    completion: nil
                )
            }
        )
    }

    func showNoAvailabilityBanner(withTitle title: String) {
        ErrorBanner(withMessage: title, on: self.view).show()
    }
}

extension AddRoomView: CancelButtonCellDelegate {
    func cancelButtonDidTap(cell: CancelButtonCell) throws {
        showOptionAlert(
            with: PILocalizedString("amendBookingCancelRoomAlertTitle", comment: "Amend Booking cancel room alert title"),
            and: PILocalizedString("amendBookingCancelRoomAlertMessage", comment: "Amend Booking cancel room alert message"),
            completion: { [weak self] in
            self?.presenter?.cancelRoom()
        }
        )
    }
}
