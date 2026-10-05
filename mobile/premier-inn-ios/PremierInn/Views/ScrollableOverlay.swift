//
//  ScrollableOverlay.swift
//  PremierInn
//
//  Created by Nick Jones on 26/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol ScrollOverlay {
	var isOverlay: Bool { get }
    var scrollParentController: UIViewController? { get }

	func scrolled(withOffset offset: CGPoint)
}

extension ScrollOverlay {
    var isOverlay: Bool { false }
    var scrollParentController: UIViewController? { nil }

    func scrolled(withOffset offset: CGPoint) {
		scrollParentController?.navigationController?.setBottomBorder(toVisible: offset.y > 0)
    }
}

extension FormekaViewController: ScrollOverlay {
    var isOverlay: Bool { isModal() }
    var scrollParentController: UIViewController? { self }

    override open func viewDidLoad() {
        super.viewDidLoad()

        enableScrollHack()
    }

    private func enableScrollHack() {
        guard isOverlay else {
            scrollParentController?.navigationController?.updateBarVisuals(withBottomBorder: true, theme: .premierInn)
            return
        }

        configureNavigationBar(navigationController: scrollParentController?.navigationController)
        scrolled(withOffset: .zero)

        scrollViewScrolled = { [weak self] scrollView in
            self?.scrolled(withOffset: scrollView.contentOffset)
        }
    }
}

extension BaseViewController: ScrollOverlay, UIScrollViewDelegate {
    var isOverlay: Bool { isModal() }
    var scrollParentController: UIViewController? { self }


	func scrollViewDidScroll(_ scrollView: UIScrollView) {
        scrolled(withOffset: scrollView.contentOffset)
    }

    func enableScrollHack() {
        guard isOverlay else {
            scrollParentController?.navigationController?.updateBarVisuals(withBottomBorder: true, theme: .premierInn)
            return
        }

        configureNavigationBar(navigationController: scrollParentController?.navigationController )
        scrolled(withOffset: .zero)

        for view in view.subviews {
            (view as? UIScrollView)?.delegate = self
        }
    }
}
