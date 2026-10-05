//
//  CiolInformationModule.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 10.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

enum CiolInformationModule {
    static func build(
        model: CiolInformationModel,
        ciolInformationType: CiolInformationType = .general,
        ciolBottomSheetAnalyticsInfo: CiolBottomSheetAnalyticsInfo,
        completion: (() -> Void)?
    ) -> UIViewController {
        let viewController = CiolInformationViewController(
            ciolInformationType: ciolInformationType,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo
        )
        viewController.modalPresentationStyle = .overFullScreen
        viewController.modalTransitionStyle = .crossDissolve
        viewController.eventHandler = {
            let presenter = CiolInformationPresenter(
                analytics: AnalyticsManager.shared
            )
            let interactor = CiolInformationInteractor(viewModel: model)
            let router = CiolInformationRouter(completion: completion)

            presenter.view = viewController
            presenter.interactor = interactor
            presenter.router = router

            router.viewController = viewController
            return presenter
        }()
        return viewController
    }
}

protocol CiolInformationInteractorProtocol {
    var viewModel: CiolInformationModel { get }
}

protocol CiolInformationViewEventHandler {
    var customAnalyticsParameters: PIDictionary? { get }
    func viewIsReady()
    func close()
    func logActionAnalytics()
}

protocol CiolInformationRouterProtocol {
    func close()
}

protocol CiolInformationViewProtocol: AnyObject {
    var customAnalyticsParameters: PIDictionary? { get set }
    func reloadData(with viewModel: CiolInformationModel)
}

protocol CanDisplayCIOLInformation {
    func displayCIOLInformation(
        model: CiolInformationModel,
        ciolInformationType: CiolInformationType,
        ciolBottomSheetAnalyticsInfo: CiolBottomSheetAnalyticsInfo,
        view: AnyObject?,
        completion: (() -> Void)?
    )
}

extension CanDisplayCIOLInformation {
    func displayCIOLInformation(
        model: CiolInformationModel,
        ciolInformationType: CiolInformationType = .general,
        ciolBottomSheetAnalyticsInfo: CiolBottomSheetAnalyticsInfo,
        view: AnyObject?,
        completion: (() -> Void)?
    ) {
        guard let view = view as? UIViewController else { return }
        let informationViewController = CiolInformationModule.build(
            model: model,
            ciolInformationType: ciolInformationType,
            ciolBottomSheetAnalyticsInfo: ciolBottomSheetAnalyticsInfo,
            completion: completion
        )
        view.present(informationViewController, animated: true)
    }
}

struct CiolInformationModel {
    var image: CiolInformationImage?
    var title: String
    var subtitle: String?
    var showSubtitle: Bool
    var description: CiolInformationText
    var showCTA: Bool
    var ctaTitle: String?
    var delegate: CiolInformationDelegate?
}

struct CiolInformationImage {
    let type: ImageType

    enum ImageType {
        case named(UIImage?)
        case url(URL?)
    }
}

struct CiolInformationText {
    let type: TextType

    enum TextType: Equatable {
        case string(String)
        case html(NSAttributedString)
    }
}
