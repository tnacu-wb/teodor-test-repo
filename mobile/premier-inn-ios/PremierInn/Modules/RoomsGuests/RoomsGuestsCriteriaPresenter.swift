//
//  RoomsGuestsCriteriaPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import UIKit

protocol RoomsGuestsCriteriaInteractorProtocol {
    var criteria: Criteria { get }
    var shouldShowAddRoomSection: Bool { get }
    var infantCustomerServicesModel: CustomerServicesModel { get }
    var cotInAccessibleCustomerServicesModel: CustomerServicesModel { get }

	func updateAdultsNumber(value: Int, roomIndex: Int)
	func updateChildrenNumber(value: Int, roomIndex: Int)
	func updateCotValue(_ value: Bool, roomIndex: Int) throws
	func updateRoomType(_ type: RoomType, roomIndex: Int) throws
    func updateInfants(_ value: Int, roomIndex: Int)
    func removeRoomAtIndex(_ index: Int) throws
    func appendRoom() throws -> Room
    func infants(for section: Int) -> Int
}

class RoomsGuestsCriteriaPresenter: NSObject {
    weak var view: RoomsGuestsCriteriaViewProtocol?
    var interactor: RoomsGuestsCriteriaInteractorProtocol

    private var selectedIndexPath: IndexPath?
    private var callNumberType: CallNumberType = .generic
	private var attributedStringForConfirmButton: NSAttributedString? {
		let prefixString = PILocalizedString("Done", comment: "")
		let suffixString = String("(" + interactor.criteria.guestsCountDescriptionBreakdown + ")")
		let fullString = String(prefixString + "\n" + suffixString)

		let paragraphStyle = NSMutableParagraphStyle()
		paragraphStyle.alignment = .center

		let mutableString = NSMutableAttributedString(
			string: fullString,
			attributes: [.foregroundColor: UIColor.white, .paragraphStyle: paragraphStyle]
		)

		let prefixRange = NSRange(location: 0, length: prefixString.count)
		mutableString.addAttributes([.font: UIFont.Button1()], range: prefixRange)

		guard let suffixRange = fullString.ranges(of: suffixString).first
			else { return NSAttributedString(attributedString: mutableString) }
		mutableString.addAttributes([.font: UIFont.BodySmall()], range: suffixRange)

		return NSAttributedString(attributedString: mutableString)
	}

	init(interactor: RoomsGuestsCriteriaInteractorProtocol) {
        self.interactor = interactor
    }
}

extension RoomsGuestsCriteriaPresenter: RoomsGuestsCriteriaPresenterProtocol {
    var selectedCriteria: Criteria? { interactor.criteria }

    var shouldShowAddRoomSection: Bool { interactor.shouldShowAddRoomSection }

    func viewIsReady() {
        view?.reload()
		view?.refreshConfirmButton(with: attributedStringForConfirmButton)
    }

    func addRoomButtonDidTap() {
        do {
            let room = try interactor.appendRoom()

            view?.appendRoomSection(with: room, index: interactor.criteria.rooms.count - 1)
			view?.reloadVisibleHeaders()
			view?.refreshConfirmButton(with: attributedStringForConfirmButton)
        } catch {
            callNumberType = .groupBookings
            guard let alertController = (error as? AddRoomError ?? AddRoomError.callUs).alert else { return }

            view?.showAddAdditionalRoomErrorAlert(with: alertController)
        }
    }

    func deleteRoomButtonDidTap(at index: Int) {
        do {
            try interactor.removeRoomAtIndex(index)
			view?.removeSection(at: index)
			view?.reloadVisibleHeaders()
			view?.refreshConfirmButton(with: attributedStringForConfirmButton)
        } catch {
            debugPrint(error)
        }
    }

	func callCustomerServiceButtonDidTap() {
		view?.callCustomerService(phoneNumber: callNumberType.phoneNumber)
	}

	func numberOfAdultsDidChange(value: Int, at indexPath: IndexPath) {
        view?.provideSmallHapticFeedback()

		interactor.updateAdultsNumber(value: value, roomIndex: indexPath.section)

		view?.reload()
		view?.refreshConfirmButton(with: attributedStringForConfirmButton)
	}

	func numberOfChildrenDidChange(value: Int, at indexPath: IndexPath) {
        view?.provideSmallHapticFeedback()

		interactor.updateChildrenNumber(value: value, roomIndex: indexPath.section)

		view?.reload()
		view?.refreshConfirmButton(with: attributedStringForConfirmButton)
	}

    func numberOfInfantsDidChange(value: Int, at indexPath: IndexPath) {
        view?.provideSmallHapticFeedback()

        interactor.updateInfants(value, roomIndex: indexPath.section)

        view?.reload()
    }

	func cotValueDidChange(value: Bool, at indexPath: IndexPath) {
        do {
            try interactor.updateCotValue(value, roomIndex: indexPath.section)
        } catch {
            switch error {
            case RoomsGuestsError.cotAndAccessibleRoomNotAllowed:

                let cotCSModel = interactor.cotInAccessibleCustomerServicesModel

                view?.provideErrorHapticFeedback()
                view?.callCustomerServices(
                	phoneNumber: cotCSModel.phoneNumber,
                	title: cotCSModel.title,
                	message: cotCSModel.message
                )
            default:
                break
            }
        }

		view?.reload()
		view?.refreshConfirmButton(with: attributedStringForConfirmButton)
	}

	func numberOfPeopleNotAllowed(at indexPath: IndexPath, error: Error) {
        view?.provideErrorHapticFeedback()
		view?.showError(error, at: indexPath)
	}

    func numberOfInfantsNotAllowed(at indexPath: IndexPath, error: Error) {
        let infantsCSModel = interactor.infantCustomerServicesModel

        view?.provideErrorHapticFeedback()
        view?.callCustomerServices(
        	phoneNumber: infantsCSModel.phoneNumber,
        	title: infantsCSModel.title,
        	message: infantsCSModel.message
        )
    }

	func roomTypeButtonDidSelect(at indexPath: IndexPath) {
		guard indexPath.section < interactor.criteria.rooms.count else { return }

		selectedIndexPath = indexPath

		let room = interactor.criteria.rooms[indexPath.section]

		view?.showRoomTypeController(for: room)
	}

    func roomTypeDidSelect(_ type: RoomType) {
        guard let indexPath = selectedIndexPath else { return }
		guard indexPath.section < interactor.criteria.rooms.count else { return }

        do {
            try interactor.updateRoomType(type, roomIndex: indexPath.section)
        } catch {
            switch error {
            case RoomsGuestsError.cotAndAccessibleRoomNotAllowed:

                let cotCSModel = interactor.cotInAccessibleCustomerServicesModel

                view?.provideErrorHapticFeedback()
                view?.callCustomerServices(
                	phoneNumber: cotCSModel.phoneNumber,
                	title: cotCSModel.title,
                	message: cotCSModel.message
                )
            default:
                break
            }
        }

        view?.reload()
		view?.refreshConfirmButton(with: attributedStringForConfirmButton)

        selectedIndexPath = nil
    }

    func infants(for roomIndex: Int) -> Int {
        interactor.infants(for: roomIndex)
    }
}
