//
//  RoomsUpsellModule.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 14.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//
import UIKit

enum RoomsUpsellModule {
    static func build(
        _ roomsUpsellInputParams: RoomsUpsellInputParams,
        outputDelegate: RoomsUpsellOutputDelegate
    ) -> UIViewController {
        let viewController = RoomsUpsellViewController()

        viewController.eventHandler = {
            let presenter = RoomsUpsellPresenter()
            presenter.outputDelegate = outputDelegate
            let interactor = RoomsUpsellInteractor(roomsUpsellInputParams: roomsUpsellInputParams)
            interactor.interactorOutput = presenter

            let router = RoomsUpsellRouter()

            viewController.eventHandler = presenter

            presenter.view = viewController
            presenter.interactor = interactor
            presenter.router = router

            router.viewController = viewController
            return presenter
        }()
        return viewController
    }
}

protocol RoomsUpsellViewModel: UpsellPriceUpdateable {
    var rooms: [UpsellRoom] { get set }
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol? { get set }
    var nights: Int { get }
    mutating func updateRooms(_ rooms: [UpsellRoom])
}

protocol RoomsUpsellModuleEventHandler {
    func handleContinueButtonTap()
    func viewIsReady()
    func showUpsellDetails(room: UpsellRoom)
    func updateOutput()
    var editedUpsell: CiolUpsellItemViewModelProtocol? { get }
    func getRoomCellConfig(roomID: String) -> CiolUpsellCellSetup?
}

protocol RoomsUpsellViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }
    func loadData(with roomsUpsellViewModel: RoomsUpsellViewModel)
}

protocol RoomsUpsellInteractorProtocol: CiolUpsellOutputInteractable, CiolUpsellDetailsOutputDelegate {
    var viewModel: RoomsUpsellViewModel { get set }
    var bookingReference: String? { get }
    var editedUpsell: CiolUpsellItemViewModelProtocol { get set }
    var upsellOutput: RoomsUpsellOutput { get set }
    var analyticsParams: PIDictionary { get }
    func getRoomCellConfig(roomID: String) -> CiolUpsellCellSetup
}

protocol RoomsUpsellInteractorOutput: AnyObject {
    func reload(model: RoomsUpsellViewModel)
}

protocol RoomsUpsellRouterProtocol {
    func popController()
    func showUpsellDetails(input: CiolUpsellDetailsInputParams, outputDelegate: CiolUpsellDetailsOutputDelegate)
}

protocol RoomsUpsellInput {
    var upselltem: CiolUpsellItemViewModelProtocol { get set }
    var rooms: [UpsellRoom] { get set }
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol? { get set }
    var nights: Int { get set }
    var bookingReference: String? { get }
    var analyticsParams: PIDictionary { get }
}

struct RoomsUpsellInputParams: RoomsUpsellInput {
    var upselltem: CiolUpsellItemViewModelProtocol
    var rooms: [UpsellRoom]
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol?
    var nights: Int
    var bookingReference: String?
    var analyticsParams: PIDictionary
}

protocol RoomsUpsellOutputDelegate: AnyObject {
    func updated(with roomOutput: RoomsUpsellOutput?)
}
