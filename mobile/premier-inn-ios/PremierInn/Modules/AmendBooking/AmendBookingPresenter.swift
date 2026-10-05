//
//  AmendBookingPresenter.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

class AmendBookingPresenter {
    weak var view: AmendBookingViewProtocol?
    var interactor: AmendBookingInteractorProtocol?
    var router: AmendBookingRouterProtocol?

    deinit {
        print("DEINIT: \(self)")
    }

    private func showError(with title: String, and message: String, goBack: Bool) {
        view?.showAlert(with: title, and: message, completion: {
            if goBack {
                self.router?.goBackToResultsScreenIfPossible()
            }
        })
    }

    private func updateViewModel(isNewRoomAdded: Bool) {
        guard let viewModel = interactor?.amendViewModel else { return }
        view?.updateViewModel(
            with: viewModel,
            isNewRoomAdded: isNewRoomAdded
        )
        view?.set(processing: false)
        view?.toggleLock(is: false)
        AccessibilityManager.announce(PILocalizedString("completeLoadingAnnouncement"))
    }
}

extension AmendBookingPresenter: AmendBookingViewEventHandler {
    func viewIsReady() {
        interactor?.startSetup()
        updateViewModel(isNewRoomAdded: false)
        view?.customAnalyticsParameters = interactor?.customAnalyticsParameters
    }

    func addRoomButtonDidTap() {
        guard let interactor = interactor else { return }
        if !interactor.canAddRoom {
            let maxRooms = interactor.rulesToFollow.maxRoomsAmend
            let alertTitle = String.localizedStringWithFormat(
                PILocalizedString("maxRoomBookingMessage", comment: ""),
                maxRooms
            )
            let alertMessage = String.localizedStringWithFormat(
                PILocalizedString("maxRoomsNumberReached", comment: "Amend screen: maximum number of rooms error message"),
                maxRooms
            )

            view?.showAlert(
                with: alertTitle,
                and: alertMessage,
                completion: {}
            )
            return
        }

        let roomNumber: Int = {
            guard let currentNumberOfRooms = interactor.amendViewModel?.rooms.count else { return 1 }
            return currentNumberOfRooms + 1
        }()

        guard let addRoomAvailabilityRequirements = interactor.addRoomAvailabilityRequirements(for: nil) else { return }

        router?.addRoom(
            asRoomNumber: roomNumber,
            availabilityRequirements: addRoomAvailabilityRequirements,
            amendOperaDetails: interactor.amendOperaDetails
        )
    }

    func editButtonDidTap(forRoomNumber roomNumber: Int) {
        guard let interactor = interactor else { return }

        let currentNumberOfRooms = interactor.roomCount
        guard let room = interactor.existingRoom(forRoomNumber: roomNumber) else {
            // Maybe do some nice error handling here?
            return
        }

        guard let addRoomAvailabilityRequirements = interactor.addRoomAvailabilityRequirements(for: room) else { return }

        let amendRoomRestrictions = interactor.editRoomRestrictions(for: room)

        router?.editRoom(
            roomNumber,
            isOnlyRoom: currentNumberOfRooms <= 1,
            amendRoomRestrictions: amendRoomRestrictions,
            withExistingRoomValues: room,
            andAvailabilityRequirements: addRoomAvailabilityRequirements,
            amendOperaDetails: interactor.amendOperaDetails
        )
    }

    func cancelButtonDidTap() {
        view?.showOptionAlert(
            with: PILocalizedString("bookingManagementCancelAlertTitle", comment: "Booking management cancel alert title"),
            and: PILocalizedString(
                "bookingManagementCancelAlertMessage",
                comment: "Booking management cancel alert message"
            ),
            confirmTitle: nil,
            cancelTitle: nil,
            completion: { [weak self] in
            self?.view?.set(cancelling: true)

            self?.interactor?.cancelStay { success, errorMessage in
                self?.view?.set(cancelling: false)

                guard success else {
                    self?.showError(
                        with: PILocalizedString(
                            "bookingManagementCancelAlertTitle",
                            comment: "Booking management cancel alert title"
                        ),
                        and: errorMessage ?? "",
                        goBack: false
                    )
                    return
                }

                self?.view?.showAlert(
                    with: PILocalizedString("bookingCancelledAlertTitle", comment: ""),
                    and: PILocalizedString("bookingCancelledAlertMessage", comment: ""),
                    completion: {
                    self?.interactor?.refreshStays()

                    self?.router?.popController(animated: true)
                }
                )
            }
        }
        )
    }

    func editDatesButtonDidTap() {
        guard let amendDatesStayDetails = interactor?.amendDatesStayDetails else { return }

        router?.showCalendar(with: amendDatesStayDetails)
    }

    func continueButtonDidTap() {
        guard let interactor, let router else { return }

        guard interactor.shouldForceUpsellChange == false else {
            router.editUpsells(isForced: true)
            return
        }

        view?.toggleLock(is: true)
        interactor.fetchPaymentOptions(completion: { [weak self] error in
            self?.view?.toggleLock(is: false)
            if let error = error {
                self?.showError(
                    with: PILocalizedString("errorMessage"),
                    and: PILocalizedString("somethingWentWrongMessage"),
                    goBack: false
                )
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.amendAmendSummaryError)
            } else {
                self?.goToAmendReviewScreen()
            }
        })
    }

    private func goToAmendReviewScreen() {
        guard let amendReviewModel = interactor?.amendReservationReviewModel else { return }

        router?.showReview(amendReviewModel: amendReviewModel)
    }

    func backButtonDidTap() {
        // REMOVED AMEND STUFF
        router?.popController(animated: true)
    }

    func editUpsellsDidTap() {
        router?.editUpsells(isForced: false)
    }
}

extension AmendBookingPresenter: AmendBookingInteractorDelegate {
    func interactorIsBusy() {
        AccessibilityManager.announce(PILocalizedString("loadingAnnouncement"))
        view?.toggleLock(is: true)
    }

    func finishedLoadingRequireData(isNewRoomAdded: Bool) {
        updateViewModel(isNewRoomAdded: isNewRoomAdded)
    }

    func failed(with errorMessage: String, goBack: Bool) {
        showError(with: PILocalizedString("errorMessage", comment: ""), and: errorMessage, goBack: goBack)
    }
}
