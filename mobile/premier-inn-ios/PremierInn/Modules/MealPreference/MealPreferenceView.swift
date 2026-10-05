//
//  MealPreferenceView.swift
//  PremierInn
//
//  Created by Freddie Parks on 05/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import Formeka

protocol MealPreferenceViewInput: AnyObject {
    func refreshRows()

    var parentNavigationController: UINavigationController? { get }
    var isProcessing: Bool { get set }
}

class MealPreferenceView: BaseViewController {
    var presenter: MealPreferencePresenterInput?

    override var screenName: String {
        presenter?.mealPreferenceTracking.screenName ?? super.screenName
    }
    override var screenType: String {
        presenter?.mealPreferenceTracking.screenType ?? super.screenType
    }

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 100
            table.sectionHeaderHeight = .leastNormalMagnitude
            table.sectionFooterHeight = 10
            table.backgroundColor = .BaseWhite
            table.separatorStyle = .none
            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: .leastNormalMagnitude
            ))
            table.tableFooterView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: .leastNormalMagnitude
            ))
            table.separatorInset.left = 0

            table.registerCellNib(with: FlexibleTextContentCell.self)
            table.registerCellNib(with: DynamicListCell.self)
            table.registerCellNib(with: FormekaSubmitButtonCell.self)
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .whiteTwo
        navigationItem.title = PILocalizedString("mealPreferenceScreenTitle", comment: "")
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        presenter?.reloadRows()
    }
}

extension MealPreferenceView: MealPreferenceViewInput {
    var parentNavigationController: UINavigationController? {
        navigationController
    }

    func refreshRows() {
        table?.reloadData()
    }

    var isProcessing: Bool {
        get {
            processingActivityIndicator?.isAnimating ?? false
        }
        set {
            toggleViewFader(toBeVisible: newValue)
            toggleProcessing(isProcessing: newValue, with: navigationController?.view)
        }
    }

    private func attributedMealDescriptionString(forString string: String?) -> NSAttributedString {
        guard let string = string else { return NSAttributedString(string: "") }

        let attributedString = NSMutableAttributedString(string: string)

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 2

        attributedString.addAttributes(
            [
                NSAttributedString.Key.font: UIFont.BodySmall(),
                NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSAttributedString.Key.paragraphStyle: paragraphStyle
            ],
            range: NSRange(location: 0, length: string.count)
        )

        return NSAttributedString(attributedString: attributedString)
    }
}

extension MealPreferenceView: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        presenter?.numberOfRows(forSection: section) ?? 0
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        presenter?.numberOfSections() ?? 0
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        20
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.section {
        case 0:
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }
            cell.isUserInteractionEnabled = false
            cell.content.text = PILocalizedString(
                "userPreferenceMealOptionInformation",
                comment: "User Preferences Generic Update Error"
            )
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell

        case 1:
            guard let mealOptionRowModels = presenter?.allMealOptions() else { return UITableViewCell() }

            guard let cell: DynamicListCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }
            cell.paddingLeft.constant = 20
            cell.paddingRight.constant = 20
            cell.stackView.setBackgroundColor(.clear, cornerRadius: 4, borderWidth: 1, borderColor: .greyBorder)
            cell.stackView.arrangedSubviews.forEach {
                cell.stackView.removeArrangedSubview($0)
                $0.removeFromSuperview()
            }

            for (index, mealOptionRowModel) in mealOptionRowModels.enumerated() {
                let isLastOption = index == mealOptionRowModels.indices.last
                guard let mealCell = mealOptionCell(
                    mealOptionRowModel: mealOptionRowModel,
                    indexPath: indexPath,
                    isLast: isLastOption
                ) else {
                    continue
                }
                cell.stackView.addArrangedSubview(mealCell)
            }

            return cell

        default:
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

            cell.delegate = self
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.button.backgroundColor = .Tint1
            cell.button.setTitleColor(.white, for: .normal)
            cell.button.setTitle(
                PILocalizedString("userPreferenceRoomRequirementsSaveButtonTitle", comment: "Save changes button title"),
                for: .normal
            )
            cell.button.titleLabel?.font = .Body_Semibold()

            cell.button.layer.cornerRadius = 4
            cell.button.layer.borderColor = UIColor.Tint1.cgColor
            cell.button.layer.borderWidth = 1

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }
    }

    private func mealOptionCell(mealOptionRowModel: MealOptionRowModel, indexPath: IndexPath, isLast: Bool) -> UIView? {
        guard let mealCell: MealPreferenceCell = UIView.fromNib(nibName: String(describing: MealPreferenceCell.self))
            else { return nil }

        if mealOptionRowModel.selected {
            mealCell.layer.borderWidth = 2
            mealCell.layer.borderColor = UIColor.Tint1.cgColor
            mealCell.layer.cornerRadius = 4
        }
        mealCell.mealTitle.text = mealOptionRowModel.title
        mealCell.radioButton.isSelected = mealOptionRowModel.selected
        mealCell.separatorView.isHidden = isLast
        mealCell.tapAction = { [weak self] in
            self?.presenter?.selectMealOption(option: mealOptionRowModel.option)
            self?.table.reloadRows(at: [indexPath], with: .automatic)
        }
        return mealCell
    }
}

extension MealPreferenceView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        presenter?.saveChanges()
    }
}
