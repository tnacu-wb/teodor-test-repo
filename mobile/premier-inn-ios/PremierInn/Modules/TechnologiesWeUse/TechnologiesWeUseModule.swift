//
//  TechnologiesWeUseModule.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

enum TechnologiesWeUseModule {
    static func build(
        withUserHavingAcceptedTermsAndConditions userHasAccepted: Bool,
        andViewModel viewModel: TechnologiesWeUseViewModel,
        and gdprEventHandler: GDPRInterstitialEventHandler?
    ) -> TechnologiesWeUseViewController {
        let controller = TechnologiesWeUseViewController(with: viewModel)
        controller.eventHandler = {
            let presenter = TechnologiesWeUsePresenter()

            let interactor =
                TechnologiesWeUseInteractor(with: TechnologiesWeUseDataRequirements(userHasAccepted: userHasAccepted))

            let router = TechnologiesWeUseRouter(with: gdprEventHandler)

            presenter.router = router
            presenter.view = controller
            presenter.interactor = interactor

            return presenter
        }()

        return controller
    }
}

// 👀*=*=*=*=*=*=* View *=*=*=*=*=*=*👀
// MARK: View Protocols
struct TechnologiesWeUseViewModel {
    var title: String
    var html: String
    var screenName: String
    var screenType: String
}

protocol TechnologiesWeUseViewProtocol: AnyObject {
    func configureLeftNavigationBar(withTitle title: String)
    func addFooter(withButtonTitle buttonTitle: String)
    func showNavigationBar(withAnimation animated: Bool)

    func performAdditionalLayoutSetup()
}

// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*


// 🎪*=*=*=*=*=*=* Presenter *=*=*=*=*=*=*🎪
// MARK: Presenter Protocols
protocol TechnologiesWeUseViewEventHandler: AnyObject {
    var userHasAccepted: Bool { get }

    func viewHasLoaded()
    func viewHasFinishedLayout()
    func viewIsAppearing(withAnimation animated: Bool)

    func userDidAccept()
}
// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*


// 🗺*=*=*=*=*=*=* Router *=*=*=*=*=*=*🗺
// MARK: Router Protocols
protocol TechnologiesWeUseRouterProtocol {
    func userAcceptedTermsAndConditions()
}
// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*


// 🤖*=*=*=*=*=*=* Interactor *=*=*=*=*=*=*🤖
// MARK: Interactor Protocols
struct TechnologiesWeUseDataRequirements {
    var userHasAccepted: Bool
}

protocol TechnologiesWeUseInteractorProtocol {
    var userHasAccepted: Bool { get }
    var navigationBarTitle: String { get }
    var footerButtonTitle: String { get }
}
// *=*=*=*=*=*=**=*=*=*=*=*=**=*=*=*=*=*

protocol GDPRInterstitialEventHandler {
    func userDidAccept(sender: UIViewController?)
}
