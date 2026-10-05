//
//  CustomRadioButtonView.swift
//  PremierInn
//
//  Created by Clint Mengolli on 12/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

protocol SelectableType: Equatable {
    var title: String { get }
    var accessibilityIdentifier: String { get }
}

protocol CustomRadioButtonViewType {
    associatedtype T
    var shouldShowError: Bool { get set }
    var options: [T] { get set }
    var currentSelection: T? { get set }
    var shouldRequireSelection: Bool { get set }
    var onSelectionChanged: ((T?) -> Void)? { get }
}

final class CustomRadioButtonView<T: SelectableType>: UIView,
                                                      CustomRadioButtonViewType {
    private let stackView = UIStackView()
    private var buttons = [IndicatorButton]()

    var shouldShowError: Bool = false {
        didSet {
            updateButtons()
        }
    }

    var options: [T] {
        didSet {
            rebuildButtons()
            updateButtons()
        }
    }

    var currentSelection: (T)? {
        didSet {
            updateButtons()
            onSelectionChanged?(currentSelection)
        }
    }

    var shouldRequireSelection: Bool = true {
        didSet {
            updateButtons()
        }
    }

    var onSelectionChanged: (((T)?) -> Void)?

    init(options: [T]) {
        self.options = options
        super.init(frame: .zero)
        setupStack()
        rebuildButtons()
    }

    convenience init(options: (T, T)) {
        self.init(options: [options.0, options.1])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupStack() {
        stackView.axis = .horizontal
        stackView.spacing = 0
        stackView.distribution = .fillEqually
        addSubview(stackView)
        stackView.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: topAnchor),
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }

    private func rebuildButtons() {
        buttons.forEach {
            $0.removeFromSuperview()
        }

        stackView.arrangedSubviews.forEach {
            stackView.removeArrangedSubview($0)
            $0.removeFromSuperview()
        }

        buttons = []

        for (index, option) in options.enumerated() {
            let buttonView = IndicatorButton(title: option.title)
            buttonView.addTarget(self, action: #selector(buttonTapped(_:)), for: .touchUpInside)
            buttonView.accessibilityIdentifier = option.accessibilityIdentifier

            let lastOption = options.count - 1
            if index == 0 {
                buttonView.maskedCorners = [.layerMinXMinYCorner, .layerMinXMaxYCorner]
            } else if index == lastOption {
                buttonView.maskedCorners = [.layerMaxXMinYCorner, .layerMaxXMaxYCorner]
            } else {
                buttonView.maskedCorners = []
            }

            buttons.append(buttonView)
            stackView.addArrangedSubview(buttonView)
        }
    }

    @objc private func buttonTapped(_ sender: IndicatorButton) {
        guard let index = buttons.firstIndex(of: sender),
              let tappedOption = options[safe: index] else {
            return
        }

        if currentSelection == tappedOption, shouldRequireSelection == false {
            currentSelection = nil
        } else {
            currentSelection = tappedOption
        }
    }

    private func updateButtons() {
        for (index, button) in buttons.enumerated() {
            let option = options[index]

            let isButtonSelected = currentSelection == option
            let shouldShowErrorState = shouldShowError && currentSelection == nil

            button.buttonState = isButtonSelected ? .selected : .unselected

            if shouldShowErrorState {
                button.buttonState = .error
            }

            if shouldShowErrorState {
                stackView.layer.cornerRadius = button.style.cornerRadius
                stackView.layer.borderWidth = button.style.errorBorderWidth
                stackView.layer.borderColor = button.style.borderErrorColor.cgColor
            } else {
                stackView.layer.borderWidth = 0
                stackView.layer.borderColor = .none
            }
        }
    }
}
