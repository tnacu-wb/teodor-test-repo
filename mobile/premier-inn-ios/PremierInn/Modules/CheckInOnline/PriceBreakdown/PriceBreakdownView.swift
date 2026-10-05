//
//  PriceBreakdownView.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 24.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

protocol PriceBreakdownViewDelegate: AnyObject {
    func buttonDidTap()
    func toggleButtonDidTap()
}

extension PriceBreakdownViewDelegate {
    func toggleButtonDidTap() { }
}

final class PriceBreakdownView: UIView {
    weak var delegate: PriceBreakdownViewDelegate?

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!

    @IBOutlet weak var continueButton: RoundedCornersButton! {
        didSet {
            continueButton.titleLabel?.font = .Button1()
            continueButton.setTitleColor(.BaseWhite, for: .normal)
            continueButton.backgroundColor = .Tint1
        }
    }

    @IBOutlet weak var totalButton: UIButton! {
        didSet {
            totalButton.titleLabel?.font = .Heading2_Bold()
            totalButton.setTitleColor(.TintD1, for: .normal)
        }
    }

    @IBOutlet weak var priceBreakdownButton: UIButton! {
        didSet {
            priceBreakdownButton.setTitle(PILocalizedString("preStayPriceBreakdown"), for: .normal)
            priceBreakdownButton.setTitleColor(.Tint1, for: .normal)
            priceBreakdownButton.titleLabel?.font = .Heading4_Semibold()
        }
    }

    @IBOutlet weak var imageView: UIImageView! {
        didSet {
            let toggleGesture = UITapGestureRecognizer(target: self, action: #selector(toggleBreakdown))
            imageView.addGestureRecognizer(toggleGesture)
        }
    }
    @IBOutlet weak var stackView: UIStackView!

    @IBOutlet weak var separatorView: UIView!

    @IBAction func continueButtonDidTap(_ sender: RoundedCornersButton) {
        delegate?.buttonDidTap()
    }

    @IBAction func totalPriceDidTap(_ sender: Any) {
        toggleBreakdown()
    }

    @IBAction func priceBreakdownDidTap(_ sender: Any) {
        toggleBreakdown()
    }

    func customise(with viewModel: CIOLPriceBreakdownViewModelProtocol) {
        continueButton.setTitle(viewModel.ctaTitle, for: .normal)
        totalButton.setTitle(viewModel.totalValue, for: .normal)
        imageView.isHidden = !viewModel.displayTotalValue
        totalButton.isHidden = !viewModel.displayTotalValue
        priceBreakdownButton.isHidden = !viewModel.displayTotalValue

        stackView.subviews.forEach { $0.removeFromSuperview() }
        stackView.addArrangedSubview(separatorView)
        viewModel.items.forEach {
            guard let itemView: PriceBreakdownItemView = PriceBreakdownItemView.fromNib() else { return }
            itemView.customise(with: $0)
            stackView.addArrangedSubview(itemView)
        }
    }

    private func toggleImage(isCollapsed: Bool) {
        imageView.image = isCollapsed ? UIImage(named: "roundedDownArrow") : UIImage(named: "roundedUpArrow")
    }

    @objc private func toggleBreakdown() {
        stackView.isHidden.toggle()
        toggleImage(isCollapsed: stackView.isHidden)
        delegate?.toggleButtonDidTap()
    }
}
