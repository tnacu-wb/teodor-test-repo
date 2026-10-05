//
//  RoomTypeSelectView.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//
import UIKit
import SimpleNetwork

class RoomTypeSelectViewController: BaseViewController, CustomModalPresentable {
    override var screenName: String { PIAnalytics.StateNames.roomType }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }

    var eventHandler: RoomTypeSelectViewEventHandler?
    var isCustomModalPresentationEnabled: Bool = true

    private var viewModel: RoomTypeSelectViewModel?

    @IBOutlet var table: UITableView! {
        didSet {
            table.registerCellNib(with: DynamicListCell.self)

            table.isScrollEnabled = false
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 80
            table.separatorStyle = .none
            table.tableHeaderView = UIView(frame: CGRect(x: 0, y: 0, width: table.frame.size.width, height: 20))
        }
    }

    @IBOutlet weak var tableViewTopConstraint: NSLayoutConstraint!

    private func setupTableViewHeader() {
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: table.frame.width, height: 76))

        let titleLabel = UILabel(frame: CGRect(x: 0, y: 0, width: table.frame.width, height: 44))
        titleLabel.text = PILocalizedString("Room type")
        titleLabel.font = .Heading3_Semibold()
        titleLabel.translatesAutoresizingMaskIntoConstraints = false

        let button = UIButton(frame: CGRect(x: 0, y: 0, width: 24, height: 32))
        button.setImage(UIImage(named: "closeModal"), for: .normal)
        button.addTarget(self, action: #selector(fadeOutAndRemove), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false

        headerView.addSubview(titleLabel)
        headerView.addSubview(button)

        NSLayoutConstraint.activate([
            button.trailingAnchor.constraint(equalTo: headerView.trailingAnchor, constant: -16),
            button.centerYAnchor.constraint(equalTo: headerView.centerYAnchor),
            button.widthAnchor.constraint(equalToConstant: 32),
            button.heightAnchor.constraint(equalToConstant: 32),
            titleLabel.centerXAnchor.constraint(equalTo: headerView.centerXAnchor),
            titleLabel.centerYAnchor.constraint(equalTo: headerView.centerYAnchor)
        ])

        table.tableHeaderView = headerView
    }

    private func setupViewsForCustomPresentation() {
        table.layer.cornerRadius = 12
        table.clipsToBounds = true
        table.layer.maskedCorners = [.layerMaxXMinYCorner, .layerMinXMinYCorner]
        view.backgroundColor = UIColor.black.withAlphaComponent(0.4)
        tableViewTopConstraint.constant = view.frame.size.height
        view.layoutIfNeeded()
        bottomAnimation(with: 1.0, did: false, and: tableOffsetForBottomPresentation)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if isCustomModalPresentationEnabled {
            setupTableViewHeader()
            setupViewsForCustomPresentation()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        title = PILocalizedString("roomTypeScreenTitle", comment: "Room type: screen title")

        eventHandler?.viewIsReady()
    }

	override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)

		navigationController?.setNavigationBarHidden(false, animated: animated)
	}

    func shouldShowNavigationBarBorder() {
        navigationController?.updateBarVisuals(withBottomBorder: true, theme: .light)
    }

    func shouldHideNavigationBarBorder() {
        navigationController?.updateBarVisuals(withBottomBorder: false, theme: .light)
    }

    private var tableOffsetForBottomPresentation: CGFloat {
        let safeAreaClearance: CGFloat = view.safeAreaInsets.bottom
        let parentViewHeight = parent?.view.frame.size.height ?? 0
        let tableContentSnugOffset = (parentViewHeight - table.contentSize.height - safeAreaClearance)

        return (0...view.frame.size.height).clamp(tableContentSnugOffset)
    }

    @objc func fadeOutAndRemove() {
        bottomAnimation(with: 0.0, did: true, and: view.frame.height)
    }

    func bottomAnimation(with alpha: CGFloat, did finish: Bool, and topOffset: CGFloat) {
        tableViewTopConstraint.constant = topOffset

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            options: [.curveEaseOut],
            animations: {
                self.view.alpha = alpha
                self.view.layoutIfNeeded()
            },
            completion: { _ in
            guard finish == true else { return }
            self.view.removeFromSuperview()
            self.removeFromParent()
        }
        )
    }
}

extension RoomTypeSelectViewController: RoomTypeSelectViewProtocol {
    func update(with roomTypeSelectViewModel: RoomTypeSelectViewModel) {
        viewModel = roomTypeSelectViewModel
        table.reloadData()
    }
}

extension RoomTypeSelectViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let roomTypeModels = viewModel?.roomTypeOptions else { return UITableViewCell() }
        guard let cell: DynamicListCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

        cell.paddingLeft.constant = 20
        cell.paddingRight.constant = 20
        cell.stackView.setBackgroundColor(.clear, cornerRadius: 4, borderWidth: 1, borderColor: .greyBorder)
        cell.stackView.arrangedSubviews.forEach {
            cell.stackView.removeArrangedSubview($0)
            $0.removeFromSuperview()
        }
        for (index, roomTypeModel) in roomTypeModels.enumerated() {
            let isLastOption = index == roomTypeModels.indices.last
            guard let roomTypeCell = roomTypeOptionCell(
                roomTypeModel: roomTypeModel,
                indexPath: indexPath,
                isLast: isLastOption
            ) else {
                continue
            }
            cell.stackView.addArrangedSubview(roomTypeCell)
        }

        return cell
    }

    private func roomTypeOptionCell(
        roomTypeModel: RoomTypeSelectOptionViewModel,
        indexPath: IndexPath,
        isLast: Bool
    ) -> UIView? {
        guard let cell: RoomTypeCell = UIView.fromNib(nibName: String(describing: RoomTypeCell.self)) else { return nil }

        cell.typeTitle.text = roomTypeModel.name
        cell.typeTitle.accessibilityIdentifier = roomTypeModel.name + "True"
        cell.radioButton.isSelected = roomTypeModel.selected
        cell.separatorView.isHidden = isLast
        cell.typeDescription.text = roomTypeModel.description
        cell.typeImage.image = UIImage(named: roomTypeModel.iconName)
        cell.tapAction = { [weak self] in
            self?.eventHandler?.select(roomType: roomTypeModel)

            guard self?.isCustomModalPresentationEnabled == true else { return }
            self?.fadeOutAndRemove()
        }

        return cell
    }
}
