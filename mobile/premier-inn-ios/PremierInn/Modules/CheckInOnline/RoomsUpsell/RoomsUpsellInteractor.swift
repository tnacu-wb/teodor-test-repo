//
//  RoomsUpsellInteractor.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//


class RoomsUpsellInteractor: RoomsUpsellInteractorProtocol {
    var roomsUpsellInputParams: RoomsUpsellInput
    var upsellOutput: RoomsUpsellOutput
    var editedUpsell: CiolUpsellItemViewModelProtocol
    weak var interactorOutput: RoomsUpsellInteractorOutput?

    init(roomsUpsellInputParams: RoomsUpsellInput) {
        self.roomsUpsellInputParams = roomsUpsellInputParams
        self.upsellOutput = .init(rooms: roomsUpsellInputParams.rooms, nights: roomsUpsellInputParams.nights)
        self.editedUpsell = roomsUpsellInputParams.upselltem
    }

    lazy var viewModel: RoomsUpsellViewModel = {
        ViewModel(
            addedPriceBreakdownViewModels: upsellOutput.priceItems,
            rooms: upsellOutput.rooms,
            priceBreakdownViewModel: roomsUpsellInputParams.priceBreakdownViewModel,
            nights: roomsUpsellInputParams.nights
        )
    }()

    var bookingReference: String? { roomsUpsellInputParams.bookingReference }
    var analyticsParams: PIDictionary { roomsUpsellInputParams.analyticsParams }

    func getRoomCellConfig(roomID: String) -> CiolUpsellCellSetup {
        upsellOutput.getRoomCellConfig(roomID: roomID, editedUpsellID: editedUpsell.id)
    }
}

extension RoomsUpsellInteractor {
    struct ViewModel: RoomsUpsellViewModel {
        var addedPriceBreakdownViewModels: [CIOLPriceBreakdownItemViewModelProtocol]
        var rooms: [UpsellRoom]
        var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol?
        var nights: Int

        mutating func updateRooms(_ rooms: [UpsellRoom]) {
            self.rooms = rooms
        }
    }

    struct CIOLPriceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol {
        var ctaTitle: String
        var totalValue: String
        var displayTotalValue: Bool { !totalValue.isEmpty }
        var items: [CIOLPriceBreakdownItemViewModelProtocol]
    }
}

extension RoomsUpsellInteractor {
    func didUpdateUpsell(for item: any CiolUpsellItemViewModelProtocol, action: CiolUpsellDetailsOutputAction) {
        switch action {
        case .add:
            upsellOutput.addUpsell(item)
        case .remove:
            upsellOutput.removeUpsell(item)
        }
        viewModel.updateRooms(upsellOutput.rooms)
        viewModel.updateAddedBreakdown(addedItems: upsellOutput.priceItems)
        interactorOutput?.reload(model: viewModel)
    }
}
