//
//  AddRoomPresenter.swift
//  PremierInn
//
//  Created by Nick Jones on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol AddRoomInteractorProtocol {
    var roomIsExistingRoom: Bool { get }
    var newRateFoundFromAvailability: Rate? { get }

    var room: Room { get }
    var roomClass: String? { get }
    var substitutions: [RoomSubstitution]? { get }
    var roomNumber: Int { get }

    var cancellable: Bool { get }
    var roomsAreAmendable: Bool { get }
    var guestsAreAmendable: Bool { get }

    var totalCost: Cost? { get }
    var numberOfAdults: Int { get }
    var numberOfChildren: Int { get }

    var leadGuestTitle: String? { get }
    var leadGuestFirstName: String? { get }
    var leadGuestLastName: String? { get }

    /**
     Use this to find out whether the type that you currently have selected is the same as the type that has already had its availability checked
     */
    var wasRoomTypeAlreadyChecked: Bool { get }

    func updateAdultsNumber(value: Int)
    func updateChildrenNumber(value: Int)
    func updateCotValue(_ value: Bool)
    func updateRoomType(_ type: RoomType)

    func updateLeadGuestTitle(with title: String)
    func updateLeadGuestFirstName(with firstName: String)
    func updateLeadGuestLastName(with lastName: String)

    func checkAvailability(completion: @escaping (_ success: Bool, _ rate: Rate?, _ error: Error?) -> Void)
	func amendRoom(completion: @escaping (_ success: Bool, _ hasAdultsDecreased: Bool, _ error: Error?) -> Void)
    func cancelRoom(completion: @escaping (_ success: Bool, _ rate: Rate?, _ error: Error?) -> Void)

    func finalisedRoomDetails() -> Room
}

class AddRoomPresenter: NSObject {
    weak var view: AddRoomViewProtocol?
    var interactor: AddRoomInteractorProtocol
    var router: AddRoomRouterProtocol?

    private var showRoomPickerLayout: Bool = true

    private var availabilityHasBeenChecked: Bool = false

    private var selectedIndexPath: IndexPath?
    private var callNumberType: CallNumberType = .generic
    private var attributedStringForConfirmButton: NSAttributedString? {
        if showRoomPickerLayout && interactor.wasRoomTypeAlreadyChecked == false {
            return NSAttributedString(string: PILocalizedString(
                "checkAvailabilyButtonTitle",
                comment: "Check Availability button title"
            ))
        }

        return NSAttributedString(string: PILocalizedString(
            "amendBookingAddRoomContinueButtonTitle",
            comment: "Amend Booking Add Room Continue Button Title"
        ))
    }

    deinit {
        print("DEINIT: \(self)")
    }

    init(interactor: AddRoomInteractorProtocol, asNewRoom: Bool) {
        self.interactor = interactor
        self.showRoomPickerLayout = asNewRoom
    }
}

extension AddRoomPresenter: AddRoomPresenterProtocol {
    var screenTitle: String {
        interactor.roomIsExistingRoom ?
            PILocalizedString("amendBookingEditRoomScreenTitle", comment: "Amend Booking Edit Room Screen Title") :
            PILocalizedString("amendBookingAddRoomScreenTitle", comment: "Amend Booking Add Room Screen Title")
    }

    var room: Room {
        interactor.room
    }

    var substitutions: [RoomSubstitution]? {
        interactor.substitutions
    }

    var roomNumber: Int {
        interactor.roomNumber
    }

    var leadGuestTitle: String? {
        interactor.leadGuestTitle
    }

    var leadGuestFirstName: String? {
        interactor.leadGuestFirstName
    }

    var leadGuestLastName: String? {
        interactor.leadGuestLastName
    }

    var shouldShowRoomPickerLayout: Bool {
        showRoomPickerLayout
    }

    var cancellable: Bool {
        interactor.cancellable
    }

    var roomsAreAmendable: Bool {
        interactor.roomsAreAmendable
    }

    var guestsAreAmendable: Bool {
        interactor.guestsAreAmendable
    }

    var newAvailabilityCheckRequired: Bool {
        // I know this looks schoolboy but the clarity over !interactor.wasRoomTypeAlreadyChecked I think is worth it
        if interactor.wasRoomTypeAlreadyChecked {
            return false
        } else {
            return true
        }
    }

    var guestsAndRoomTypeDescription: String {
        var entities: [String] = [
            String.localizedStringWithFormat(
                PILocalizedString("%d adult(s)"),
                interactor.numberOfAdults
            )
        ]

        if interactor.numberOfChildren > 0 {
            entities.append(String.localizedStringWithFormat(
                PILocalizedString("%d child(children)"),
                interactor.numberOfChildren
            ))
        }

        let adultsAndChildrenDescription = entities.joined(separator: ", ")
        let roomType = interactor.room.roomName ?? SettingsManager.sharedInstance.roomsTitleForAmend(
            for: interactor.room.lettingType,
            roomClass: interactor.roomClass
        ) ?? interactor.room.type.name

        return "\(adultsAndChildrenDescription), \(roomType.lowercased())"
    }

    var roomPriceDescription: String? {
        guard let roomCost = interactor.totalCost else { return nil }

        return roomCost.localizedValue
    }

    var hasAvailabilityBeenChecked: Bool {
        self.availabilityHasBeenChecked
    }

    func viewIsReady() {
        view?.reload()
        view?.refreshConfirmButton(with: attributedStringForConfirmButton)
    }

    func callCustomerServiceButtonDidTap() {
        view?.callCustomerService(phoneNumber: callNumberType.phoneNumber)
    }

    func numberOfAdultsDidChange(value: Int) {
        view?.provideSmallHapticFeedback()

        interactor.updateAdultsNumber(value: value)

        view?.reload()
        view?.animateConfirmButtonTextChange(with: attributedStringForConfirmButton)
    }

    func numberOfChildrenDidChange(value: Int) {
        view?.provideSmallHapticFeedback()

        interactor.updateChildrenNumber(value: value)

        if value == 0 {
            interactor.updateCotValue(false)
        }

        view?.reload()
        view?.animateConfirmButtonTextChange(with: attributedStringForConfirmButton)
    }

    func cotValueDidChange(value: Bool) {
        interactor.updateCotValue(value)

        view?.reload()
        view?.animateConfirmButtonTextChange(with: attributedStringForConfirmButton)
    }

    func numberOfPeopleNotAllowed(at indexPath: IndexPath, error: Error) {
        view?.provideErrorHapticFeedback()
        view?.showError(error, at: indexPath)
    }

    func roomTypeButtonDidSelect() {
        view?.showRoomTypeController(for: interactor.room)
    }

    func roomTypeDidSelect(_ type: RoomType) {
        interactor.updateRoomType(type)

        view?.reload()
        view?.animateConfirmButtonTextChange(with: attributedStringForConfirmButton)
    }

    func checkAvailabilityButtonTapped() {
        interactor.checkAvailability { success, _, error in
            guard success == true else {
                switch error {
                case is AmendAddOrEditRoomAvailabilityError:
                    self.noAvailabilityFound()
                case is AmendAddOrEditRoomError:
                    self.errorFound()
                default:
                    self.errorFound()
                }

                let error = error ?? AmendAddOrEditRoomError.genericError
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendEditRoomChangeError)

                return
            }

            self.toggleToRoomDetailsLayout()
        }

        view?.showLoadingBox()
    }

    func continueButtonTapped() {
        if showRoomPickerLayout == true {
            toggleToRoomDetailsLayout()
            return
        }
        view?.toggle(is: true)

        interactor.amendRoom { success, hasAdultsDecreased, error in
            self.view?.toggle(is: false)

            guard success, error == nil else {
                self.errorFound()
                return
            }

			self.router?.finaliseRoomChanges(
			    hasAdultsDecreased: hasAdultsDecreased,
			    isNewRoomAdded: !self.interactor.roomIsExistingRoom
			)
        }
    }

    private func toggleToRoomDetailsLayout() {
        showRoomPickerLayout = false

        view?.hideLoadingBox()
        view?.reloadWithAnimation()
        view?.animateConfirmButtonTextChange(with: attributedStringForConfirmButton)
    }

    func salutationRowDidTap() {
        router?.presentSalutationPicker(withPresenter: self)
    }

    func setSalutation(_ salutation: String?) {
        interactor.updateLeadGuestTitle(with: salutation ?? "")
        view?.reload()
    }

    func changeRoomCriteriaTapped() {
        showRoomPickerLayout = true

        view?.reloadWithAnimation()

        if availabilityHasBeenChecked == false {
            view?.animateConfirmButtonTextChange(with: attributedStringForConfirmButton)
        }
    }

    private func noAvailabilityFound() {
        view?.hideLoadingBox()

        let errorBannerTitle = String.localizedStringWithFormat(
            PILocalizedString(
                "noAvailabilityForDesiredRoomTypeText",
                comment: "Amend Booking Add Room No Availability For Desired Room Type"
            ),
            room.type.localizedName
        )

        view?.showNoAvailabilityBanner(withTitle: errorBannerTitle)
    }

    private func errorFound() {
        view?.hideLoadingBox()

        let errorBannerTitle = String.localizedStringWithFormat(
            PILocalizedString("amendBookingTryAgainError"),
            room.type.localizedName
        )

        view?.showNoAvailabilityBanner(withTitle: errorBannerTitle)
    }

    func userHasCheckedAvailability() {
        availabilityHasBeenChecked = true
    }

    func firstNameDidChange(to firstName: String) {
        interactor.updateLeadGuestFirstName(with: firstName)
    }

    func lastNameDidChange(to lastName: String) {
        interactor.updateLeadGuestLastName(with: lastName)
    }

    func cancelRoom() {
        view?.showLoadingBox()
        interactor.cancelRoom { success, _, error in
            guard success == true else {
                switch error {
                case is AmendCancelRoomAvailabilityError:
                    self.noAvailabilityFound()
                case is AmendCancelRoomError:
                    self.errorFound()
                default:
                    self.errorFound()
                }

                let error = error ?? AmendCancelRoomError.genericError
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendCancelRoomError)

                return
            }

            self.router?.finaliseRoomChanges(
                hasAdultsDecreased: false,
                isNewRoomAdded: false
            )
        }
    }
}
