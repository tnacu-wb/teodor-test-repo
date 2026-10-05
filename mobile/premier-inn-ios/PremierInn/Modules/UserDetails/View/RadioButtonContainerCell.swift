//
//  RadioContainerCell.swift
//  PremierInn
//
//  Created by Clint Mengolli on 15/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Formeka
import UIKit

final class RadioButtonContainerCell: SimpleSeparatorsCell, FormekaErrorCell {
    private enum Constants {
        static let radioButtonHeight: CGFloat = 56
        static let noSpacing: CGFloat = 0
        static let medSpacing: CGFloat = 8
        static let largeSpacing: CGFloat = 16

        static let tailOffset: CGFloat = 16
        static let tailWidth: CGFloat = 24
        static let tailHeight: CGFloat = 12

        static let errorMessage: String = PILocalizedString("tripPurposeErrorMessage")
    }

    var errorMessage: String? {
        didSet {
            applyError(errorMessage)
        }
    }

    private let stack = UIStackView()

    let radioButtonView: CustomRadioButtonView<TripPurposeSelections> = {
        let options = [TripPurposeSelections.leisure, TripPurposeSelections.business]
        let view = CustomRadioButtonView(options: options)
        view.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.tripPurposeSelector
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    let inlineErrorMessage: InlineMessageView = {
        let view = InlineMessageView(
            text: Constants.errorMessage,
            style: .error,
            arrowPlacement: .topBorder(alignment: .leading(offsetBy: Constants.tailOffset)),
            tailDimensions: .init(width: Constants.tailWidth, height: Constants.tailHeight)
        )

        view.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.tripPurposeErrorMessage
        view.translatesAutoresizingMaskIntoConstraints = false
        view.isHidden = true
        return view
    }()

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        contentView.backgroundColor = .ColourLD1
        hiddenSeparatorLocations = [.top]

        stack.axis = .vertical
        stack.spacing = Constants.medSpacing
        stack.translatesAutoresizingMaskIntoConstraints = false

        stack.addArrangedSubview(radioButtonView)
        stack.addArrangedSubview(inlineErrorMessage)
        contentView.addSubview(stack)

        radioButtonView.setContentHuggingPriority(.required, for: .vertical)
        radioButtonView.setContentCompressionResistancePriority(.required, for: .vertical)
        radioButtonView.heightAnchor.constraint(equalToConstant: Constants.radioButtonHeight).isActive = true
        inlineErrorMessage.setContentHuggingPriority(.required, for: .vertical)
        inlineErrorMessage.setContentCompressionResistancePriority(.required, for: .vertical)

        NSLayoutConstraint.activate([
            stack.topAnchor.constraint(equalTo: contentView.topAnchor, constant: Constants.noSpacing),
            stack.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: Constants.largeSpacing),
            stack.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -Constants.largeSpacing),
            stack.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -Constants.largeSpacing)
        ])
    }

    override func prepareForReuse() {
        super.prepareForReuse()
        radioButtonView.onSelectionChanged = nil
        errorMessage = nil
    }

    private func applyError(_ errorMessage: String?) {
        let shouldShowError = (errorMessage?.isEmpty == false)

        radioButtonView.shouldShowError = shouldShowError
        inlineErrorMessage.isHidden = !shouldShowError

        setNeedsLayout()
    }
}
