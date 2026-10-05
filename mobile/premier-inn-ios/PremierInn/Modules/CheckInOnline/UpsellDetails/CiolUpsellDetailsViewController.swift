//
//  CiolUpsellDetailsViewController.swift
//  PremierInn
//
//  Created by Oltean Vasile Bogdan on 16.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class CiolUpsellDetailsViewController: BaseViewController {
    @IBOutlet weak var contentView: UIView!
    @IBOutlet weak var itemImageView: UIImageView!
    @IBOutlet weak var removeView: UIView! {
        didSet {
            removeView.isHidden = true
        }
    }

    @IBOutlet weak var bookedLabelStackView: UIStackView!
    @IBOutlet weak var availableLabelStackView: UIStackView!
    @IBOutlet weak var availableLabel: UILabel! {
        didSet {
            availableLabel.font = UIFont.Heading2_Bold()
            availableLabel.text = PILocalizedString("ciolAvailableOptions")
        }
    }
    @IBOutlet weak var bookedLabel: UILabel! {
        didSet {
            bookedLabel.font = UIFont.Heading2_Bold()
            bookedLabel.text = PILocalizedString("ciolAlreadyReserved")
        }
    }

    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var addView: UIView!
    @IBOutlet weak var contentStackView: UIStackView! {
        didSet {
            contentStackView.spacing = 16.0
            contentStackView.alignment = .center
        }
    }

    @IBOutlet weak var bookedStackview: UIStackView! {
        didSet {
            bookedStackview.layoutMargins = .init(top: 16, left: 16, bottom: 16, right: 16)
            bookedStackview.isLayoutMarginsRelativeArrangement = true
            bookedStackview.layer.cornerRadius = 10
            bookedStackview.layer.borderWidth = 1.0
            bookedStackview.layer.borderColor = UIColor.TintL3.cgColor
            bookedStackview.clipsToBounds = true
            bookedStackview.spacing = 10
        }
    }

    @IBOutlet weak var addRemoveButton: CiolUpsellDetailsButton!
    @IBOutlet weak var removeButton: CiolUpsellDetailsButton! {
        didSet {
            removeButton.updateState(buttonState: .booked(false))
        }
    }
    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = .Body_Bold()
        }
    }
    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.font = .Subtext_Medium()
        }
    }

    @IBOutlet weak var viewMenuAndAllergensView: UIView!
    @IBOutlet weak var viewMenuView: UIStackView!
    @IBOutlet weak var viewMenuButton: UIButton! {
        didSet {
            viewMenuButton.titleLabel?.font = .BodySmall()
            viewMenuButton.setTitle(PILocalizedString("ciolViewMenu"), for: .normal)
        }
    }
    @IBOutlet weak var viewAllergensView: UIStackView!
    @IBOutlet weak var viewAllergensButton: UIButton! {
        didSet {
            viewAllergensButton.titleLabel?.font = .BodySmall()
            viewAllergensButton.setTitle(PILocalizedString("ciolAllergenGuide"), for: .normal)
        }
    }
    @IBOutlet weak var menuAndAllergensSeparator: UIView!

    var eventHandler: CiolUpsellDetailsViewEventHandler?
    var viewmodel: CiolUpsellDetailsViewModelProtocol?

    override var screenName: String { PIAnalytics.StateNames.ciolUpsellDetails }
    override var screenType: String { PIAnalytics.StateTypes.ciolFlow }
    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = view.backgroundColor?.withAlphaComponent(0.7)

        contentView.layer.cornerRadius = 5
        eventHandler?.viewIsReady()
    }

    @IBAction func closeTapped(_ sender: Any) {
        self.dismiss(animated: true)
    }

    @IBAction func addRemoveTapped(_ sender: Any) {
        eventHandler?.sendUpsellOutput(action: .add)
        self.dismiss(animated: true)
    }

    @IBAction func removeAction(_ sender: Any) {
        eventHandler?.sendUpsellOutput(action: .remove)
        self.dismiss(animated: true)
    }

    @IBAction func viewMenuTapped(_ sender: Any) {
        eventHandler?.showMenu()
    }

    @IBAction func viewAllergensTapped(_ sender: Any) {
        eventHandler?.showAllergyInfo()
    }

    private func setupBorder() {
        contentStackView.layoutMargins = .init(top: 16, left: 0, bottom: 16, right: 0)
        contentStackView.isLayoutMarginsRelativeArrangement = true
        contentStackView.layer.cornerRadius = 10
        contentStackView.layer.borderWidth = 1.0
        contentStackView.layer.borderColor = UIColor.TintL3.cgColor
        contentStackView.clipsToBounds = true
    }

    private func updateAddButtonVisibility(buttonState: CiolUpsellDetailsButton.ButtonState, animated: Bool = false) {
        var shouldHideAddButton: Bool = false
        if case .hidden = buttonState {
            shouldHideAddButton = true
        }
        if animated {
            UIView.animate(withDuration: .ocd) {
                self.addView.isHidden = shouldHideAddButton
                self.contentStackView.layoutIfNeeded()
            }
        } else {
            self.addView.isHidden = shouldHideAddButton
        }
    }

    private func setupPrebooked(prebookedItems: [String]) {
        availableLabel.isHidden = false
        bookedLabel.isHidden = false
        bookedStackview.isHidden = false

        for item in prebookedItems {
            let bookedView = CiolUpsellBookedItemView()
            bookedView.configure(item: item)
            bookedStackview.addArrangedSubview(bookedView)
        }
        scrollView.layoutIfNeeded()
    }
}

extension CiolUpsellDetailsViewController: CiolUpsellDetailsViewProtocol {
    func updateButton(state: CiolUpsellDetailsButton.ButtonState) {
        updateAddButtonVisibility(buttonState: state, animated: true)
        addRemoveButton.updateState(buttonState: state, font: .Heading3_Semibold())
    }

    func updateEnablement(models: [CiolUpsellSubitemViewModel]) {
        contentStackView.subviews.forEach( { view in
            if let detailsView = view as? CIOLUpsellDetailsView,
               let updatedModel = models.first(where: {$0.id == detailsView.model?.id}) {
                detailsView.reconfigure(viewModel: updatedModel)
            }
        })
    }

    func displayUpsellData(viewModel: CiolUpsellDetailsViewModelProtocol) {
        self.viewmodel = viewModel
        if viewModel.shouldShowBorder {
            setupBorder()
        }
        updateAddRemoveButton(viewModel: viewModel)
        titleLabel.text = viewModel.upsell.title
        for (index, subitem) in viewModel.upsell.subitems.enumerated() {
            addUpsellDetailItem(
                viewModel: subitem,
                withSeparator: index != viewModel.upsell.subitems.count - 1,
                withBorder: viewModel.shouldShowBorder
            )
        }

        descriptionLabel.text = viewModel.upsell.subtitle

        if let imageURL = viewModel.upsell.imageURL {
            itemImageView.setImage(with: imageURL)
        }

        if viewModel.shouldShowMenu {
            viewMenuView.isHidden = false
        } else {
            viewMenuView.isHidden = true
            menuAndAllergensSeparator.isHidden = true
        }

        if viewModel.shouldShowAllergens {
            viewAllergensView.isHidden = false
        } else {
            viewAllergensView.isHidden = true
            menuAndAllergensSeparator.isHidden = true
        }

        viewMenuAndAllergensView.isHidden = viewMenuView.isHidden && viewAllergensView.isHidden
        if let prebookedItems = viewModel.prebookedItems, prebookedItems.isNotEmpty {
            setupPrebooked(prebookedItems: prebookedItems)
        }
        scrollView.layoutIfNeeded()
        let contentSizeWithoutScrollView = 300.0
        let maxScrollViewHeight = view.frame.height - contentSizeWithoutScrollView
        let scrollViewHeight = scrollView.contentSize.height
        let availableScrollViewHeight = min(maxScrollViewHeight, scrollViewHeight)

        scrollView.heightAnchor.constraint(equalToConstant: availableScrollViewHeight).isActive = true
    }

    private func addUpsellDetailItem(
        viewModel: CiolUpsellSubitemViewModel,
        withSeparator: Bool = true,
        withBorder: Bool
    ) {
        let itemDetailsView = CIOLUpsellDetailsView()
        itemDetailsView.configure(viewModel: viewModel)
        itemDetailsView.updateDelegate = self

        contentStackView.addArrangedSubViewWithSeparator(itemDetailsView, shouldAddSeparator: withSeparator)
        let horizontalInset: CGFloat = withBorder ? ViewConstants.Spacing.medium : ViewConstants.Spacing.none
        itemDetailsView.leadingAnchor.constraint(equalTo: contentStackView.leadingAnchor, constant: horizontalInset)
            .isActive = true
        itemDetailsView.trailingAnchor.constraint(equalTo: contentStackView.trailingAnchor, constant: -horizontalInset)
            .isActive = true
    }
}

private extension CiolUpsellDetailsViewController {
    func updateAddRemoveButton(viewModel: CiolUpsellDetailsViewModelProtocol) {
        updateAddButtonVisibility(buttonState: viewModel.butonConfig)
        if let removeConfig = viewModel.removeButtonConfig {
            removeView.isHidden = false
            removeButton.updateState(buttonState: removeConfig, font: .Heading3_Semibold())
        }
        addRemoveButton.updateState(buttonState: viewModel.butonConfig, font: .Heading3_Semibold())
    }
}

extension CiolUpsellDetailsViewController: CIOLUpsellDetailsDelegate {
    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> Void) {
        eventHandler?.didUpdateUpsell(with: id, action: action, completion: completion)
    }
}

class CiolUpsellBookedItemView: UIStackView {
    lazy var userImageView: UIImageView = {
        let imView = UIImageView(image: .init(named: "user"))
        imView.heightAnchor.constraint(equalToConstant: 18).isActive = true
        imView.widthAnchor.constraint(equalToConstant: 18).isActive = true
        return imView
    }()

    lazy var itemLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.BodySmall_Semibold()
        return label
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setup() {
        translatesAutoresizingMaskIntoConstraints = false
        axis = .horizontal
        distribution = .fillProportionally
        spacing = 8.0

        addArrangedSubview(userImageView)
        addArrangedSubview(itemLabel)
    }

    func configure(item: String) {
        itemLabel.text = item
    }
}
