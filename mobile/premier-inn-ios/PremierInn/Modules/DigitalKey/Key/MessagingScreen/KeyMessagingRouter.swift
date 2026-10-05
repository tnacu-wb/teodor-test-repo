//
//  MessagingRouter.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 22/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import SimpleNetwork
import UIKit

class KeyMessagingRouter: KeyMessagingRouterProtocol {
    weak var viewController: UIViewController?
    weak var presenter: KeyMessagingPresenter?
}
