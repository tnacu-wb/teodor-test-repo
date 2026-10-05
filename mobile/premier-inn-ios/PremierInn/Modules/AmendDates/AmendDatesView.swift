//
//  AmendDatesView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/01/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class AmendDatesView: AlternateCalendarViewController {
    var presenter: AmendDatesPresenter?

    private var continueView: AlternateCalendarAmendDateConfirmView?

    override func viewDidLoad() {
        super.viewDidLoad()

        submitButtonPrefix = PILocalizedString("changeDatesButtonTitle")
        showSubmitButtonOnDateSelection = false

        calendarDelegate = presenter
    }
}

extension AmendDatesView: AmendDatesCalendarViewControllerProtocol {
    func showContinueButton(with viewModel: AmendDatesConfirmViewModel) {
        guard let view = view else { return }

        let newContinueView: AlternateCalendarAmendDateConfirmView? = .fromNib()
        continueView = newContinueView

        guard let continueView = newContinueView else { return }

        continueView.delegate = presenter
        continueView.update(with: viewModel)
        continueView.frame.size.height = 160
        continueView.frame.size.width = view.frame.width
        continueView.frame.origin.y = view.frame.height

        view.addSubview(continueView)

        continueView.layer.shadowOffset = CGSize(width: 0.0, height: -1.0)
        continueView.layer.shadowColor = UIColor.black.cgColor
        continueView.layer.shadowOpacity = 0.5
        continueView.layer.shouldRasterize = true
        continueView.layer.rasterizationScale = UIScreen.main.scale

        UIView.animate(withDuration: .ocd) {
            continueView.frame.origin.y -= continueView.frame.height
        }
    }

    func hideContinueButton() {
        continueView?.removeFromSuperview()
    }
}
