//
//  RoomsUpsellPresenter.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//



class RoomsUpsellPresenter {
    weak var view: RoomsUpsellViewProtocol?
    var interactor: RoomsUpsellInteractorProtocol?
    var router: RoomsUpsellRouterProtocol?
    weak var outputDelegate: RoomsUpsellOutputDelegate?
}

extension RoomsUpsellPresenter: RoomsUpsellInteractorOutput {
    func reload(model: any RoomsUpsellViewModel) {
        view?.loadData(with: model)
    }
}

extension RoomsUpsellPresenter: RoomsUpsellModuleEventHandler {
    var editedUpsell: CiolUpsellItemViewModelProtocol? {
        interactor?.editedUpsell
    }
    func getRoomCellConfig(roomID: String) -> CiolUpsellCellSetup? {
        interactor?.getRoomCellConfig(roomID: roomID)
    }
    func updateOutput() {
        outputDelegate?.updated(with: interactor?.upsellOutput)
    }

    func handleContinueButtonTap() {
        router?.popController()
    }

    func viewIsReady() {
        guard let viewModel = interactor?.viewModel else { return }
        view?.customAnalyticsParameters = interactor?.analyticsParams
        view?.loadData(with: viewModel)
    }

    func showUpsellDetails(room: UpsellRoom) {
        guard let interactor else { return }
        var upsellToDisplay = interactor.editedUpsell
        if let existingUpsell = interactor.upsell(from: room.id, upsellID: upsellToDisplay.id) {
            upsellToDisplay = existingUpsell
        }
        upsellToDisplay.room = room
        let detailsInput = CiolUpsellDetailsInputParams(
            upsell: upsellToDisplay,
            nights: interactor.viewModel.nights,
            addedFoodItems: interactor.addedFoodItems(
                for: room.id,
                withoutUpsellID: upsellToDisplay.id
            ) ?? 0,
            addedKidsItems: interactor.addedKidsItems(
                for: room.id,
                withoutUpsellID: upsellToDisplay.id
            ) ?? 0,
            prebookedItems: interactor.prebookedItems(
                for: upsellToDisplay.id,
                roomID: room.id
            ),
            bookingReference: interactor.bookingReference,
            analyticsParams: interactor.analyticsParams
        )
        router?.showUpsellDetails(input: detailsInput, outputDelegate: interactor)
    }
}
