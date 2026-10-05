//
//  Untitled.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 15.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CIOLStepperView: UIView {
    weak var updateDelegate: CIOLStepperDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    func updateEnablement(enabled: Bool) {
        minusButton.tintColor = enabled ? .BaseBlack : .BaseBlack.withAlphaComponent(0.1)
        minusButton.isEnabled = enabled
        plusButton.tintColor = enabled ? .BaseBlack : .BaseBlack.withAlphaComponent(0.1)
        plusButton.isEnabled = enabled
        valueLabel.textColor = enabled ? .TintD1 : .TintD1.withAlphaComponent(0.1)
        if !enabled {
            value = 0
        }
    }

    private func setup() {
        layer.cornerRadius = 10
        layer.borderWidth = 1.0
        layer.borderColor = UIColor.lightGray.cgColor
        clipsToBounds = true
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(minusButton)
        addSubview(valueLabel)
        addSubview(plusButton)

        minusButton.addTarget(self, action: #selector(didTapMinus), for: .touchUpInside)
        plusButton.addTarget(self, action: #selector(didTapPlus), for: .touchUpInside)

        NSLayoutConstraint.activate([
            minusButton.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 13),
            minusButton.centerYAnchor.constraint(equalTo: centerYAnchor),
            minusButton.heightAnchor.constraint(equalToConstant: 16.8),
            minusButton.widthAnchor.constraint(equalToConstant: 16.8),
            valueLabel.leadingAnchor.constraint(equalTo: minusButton.trailingAnchor, constant: 8.0),
            valueLabel.centerYAnchor.constraint(equalTo: centerYAnchor),
            valueLabel.widthAnchor.constraint(equalToConstant: 12.0),
            plusButton.leadingAnchor.constraint(equalTo: valueLabel.trailingAnchor, constant: 8.0),
            plusButton.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -13.0),
            plusButton.centerYAnchor.constraint(equalTo: centerYAnchor),
            plusButton.heightAnchor.constraint(equalToConstant: 16.8),
            plusButton.widthAnchor.constraint(equalToConstant: 16.8)
        ])
    }

    @objc private func didTapMinus() {
        updateDelegate?.didUpdate(action: .didWithdraw(value)) { [weak self] in
            self?.value -= 1
        }
    }

    @objc private func didTapPlus() {
        updateDelegate?.didUpdate(action: .didAdd(value)) { [weak self] in
            self?.value += 1
        }
    }

    var value = 0 {
        didSet {
            valueLabel.text = "\(value)"
        }
    }

    private lazy var minusButton: CiolStepperButton = {
        let button = CiolStepperButton(touchArea: Constants.buttonTouchArea)
        let image = UIImage(named: "minus")?.withRenderingMode(.alwaysTemplate)
        button.setImage(image, for: .normal)

        button.tintColor = .BaseBlack
        button.translatesAutoresizingMaskIntoConstraints = false
        button.contentMode = .scaleAspectFit
        return button
    }()

    private lazy var plusButton: CiolStepperButton = {
        let button = CiolStepperButton(touchArea: Constants.buttonTouchArea)
        let image = UIImage(named: "plus")?.withRenderingMode(.alwaysTemplate)
        button.setImage(image, for: .normal)
        button.tintColor = .BaseBlack
        button.contentMode = .scaleAspectFit
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()

    private lazy var valueLabel: UILabel = {
        let label = UILabel()
        label.font = .Heading3_Bold()
        label.textColor = .TintD1
        label.numberOfLines = 1
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = "\(value)"
        return label
    }()

    enum Constants {
        static let buttonTouchArea = UIEdgeInsets(top: -20, left: -20, bottom: -20, right: -20)
    }
}

enum CIOLStepperAction {
    case didAdd(Int)
    case didWithdraw(Int)
}

protocol CIOLStepperDelegate: AnyObject {
    func didUpdate(action: CIOLStepperAction, completion: @escaping () -> Void)
}

protocol CIOLUpsellDetailsDelegate: AnyObject {
    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> Void)
}

class CiolStepperButton: UIButton {
    init(touchArea: UIEdgeInsets) {
        self.touchArea = touchArea
        super.init(frame: .zero)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    let touchArea: UIEdgeInsets

    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        let area = bounds.inset(by: touchArea)
        return area.contains(point)
    }
}
