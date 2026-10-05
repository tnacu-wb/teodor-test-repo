//
//  AddRoomRouter.swift
//  PremierInn
//
//  Created by Nick Jones on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork

protocol AddRoomRouterProtocol {
    func presentSalutationPicker(withPresenter presenter: AddRoomPresenterProtocol)
    func finaliseRoomChanges(hasAdultsDecreased: Bool, isNewRoomAdded: Bool)
}

protocol AmendRoomRouterDelegate: AnyObject {
	func finishedEditingRoom(hasAdultsDecreased: Bool, isNewRoomAdded: Bool)
}

class AddRoomRouter {
    private weak var viewController: UIViewController?
    weak var delegate: AmendRoomRouterDelegate?

    deinit {
        print("DEINIT: \(self)")
    }

    static func build(
        routerDelegate: AmendRoomRouterDelegate?,
        asNewRoom: Bool,
        existingRoom: Room?,
        isOnlyRoom: Bool = false,
        isAmendableRoom: Bool = true,
        isCancellableRoom: Bool = true,
        canAmendGuests: Bool = true,
        roomNumber: Int,
        availabilityRequirements: AddRoomAvailabilityRequirements,
        amendOperaDetails: AmendOperaDetails?
    ) -> AddRoomView {
        let controller = AddRoomView()
        controller.presenter = {
            let interactor = AddRoomInteractor(
                withRoom: existingRoom,
                existingUpsells: availabilityRequirements.existingUpsells,
                isOnlyRoom: isOnlyRoom,
                isAmendableRoom: isAmendableRoom,
                isCancellableRoom: isCancellableRoom,
                canAmendGuests: canAmendGuests,
                roomNumber: roomNumber,
                andAddRoomAvailabilityRequirements: availabilityRequirements,
                amendOperaDetails: amendOperaDetails
            )

            let router = AddRoomRouter()
            router.delegate = routerDelegate
            router.viewController = controller

            let presenter = AddRoomPresenter(interactor: interactor, asNewRoom: asNewRoom)
            presenter.view = controller
            presenter.router = router

            interactor.presenter = presenter

            return presenter
        }()

        return controller
    }
}

extension AddRoomRouter: AddRoomRouterProtocol {
    func presentSalutationPicker(withPresenter presenter: AddRoomPresenterProtocol) {
        let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "salutationListPlaceholder",
            comment: "Salutation list placeholder"
        )
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { sender, salutation in
            sender.dismiss(animated: true)

            presenter.setSalutation(salutation as? String)
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
        }

        viewController?.present(controller, animated: true)
    }

    func finaliseRoomChanges(hasAdultsDecreased: Bool, isNewRoomAdded: Bool) {
		delegate?.finishedEditingRoom(
		    hasAdultsDecreased: hasAdultsDecreased,
		    isNewRoomAdded: isNewRoomAdded
		)
    }
}
