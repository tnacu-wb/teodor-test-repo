//
//  CheckoutTableViewController.swift
//  PremierInn
//
//  Created by Simon Antoine on 31/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class CheckoutTableViewController: UITableViewController {
    private var model: [MenuItemModel]
    private var checkoutModel: [CheckoutModel] = []
    @IBOutlet weak var buttonPay: UIButton!
    @IBOutlet weak var footerView: UIView!
    private var amount = 0

    init(model: [MenuItemModel]) {
        self.model = model
        super.init(nibName: String(describing: CheckoutTableViewController.self), bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        for item in model where !(checkoutModel.contains(where: { value in
            value.item.id == item.id
        })) {
            var count = 0
            for tmp in model where tmp.id == item.id {
                count += 1
            }
            checkoutModel.append(CheckoutModel(item: item, quantity: count))
        }

        for item in checkoutModel {
            amount += item.item.price
        }

        buttonPay.setTitle("Pay (£\(Double(amount) / 100))", for: .normal)
        buttonPay.backgroundColor = .BasePurple
        buttonPay.setTitleColor(.white, for: .normal)
        buttonPay.layer.cornerRadius = buttonPay.frame.height / 2

        tableView.register(
            UINib(nibName: String(describing: CheckoutCardTableViewCell.self), bundle: nil),
            forCellReuseIdentifier: String(describing: CheckoutCardTableViewCell.self)
        )
        tableView.register(
            UINib(nibName: String(describing: CheckoutMenuTableViewCell.self), bundle: nil),
            forCellReuseIdentifier: String(describing: CheckoutMenuTableViewCell.self)
        )

        tableView.backgroundColor = .TintL4
        footerView.backgroundColor = .TintL4
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return checkoutModel.count
        } else if section == 1 {
            return 1
        }
        return 0
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 1 {
            return 180.0
        }
        return UITableView.automaticDimension
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell()

        if indexPath.section == 0 {
            guard let menuCell = tableView.dequeueReusableCell(
                withIdentifier: String(describing: CheckoutMenuTableViewCell.self),
                for: indexPath
            ) as? CheckoutMenuTableViewCell else {return UITableViewCell()}

            menuCell.contentView.backgroundColor = .TintL4
            menuCell.containerView.layer.cornerRadius = 4
            menuCell.titleLabel?.text = "\(checkoutModel[indexPath.row].item.name) x\(checkoutModel[indexPath.row].quantity)"

            return menuCell
        } else if indexPath.section == 1 {
            guard let paymentCell = tableView.dequeueReusableCell(
                withIdentifier: String(describing: CheckoutCardTableViewCell.self),
                for: indexPath
            ) as? CheckoutCardTableViewCell else {return UITableViewCell()}

            return paymentCell
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let viewHeader = MenuHeaderSection(frame: CGRect(x: 0, y: 0, width: tableView.frame.width, height: 30))
        viewHeader.titleLabel.text = section == 0 ? "Order" : "Payment"
        viewHeader.descriptionLabel.text = ""
        viewHeader.contentView.backgroundColor = .TintL4
        viewHeader.descriptionLabel.isHidden = viewHeader.descriptionLabel.text?.isEmpty ?? true

        return viewHeader
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        35
    }

    override func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
        if section == 0 {
            return "Total: £\(Double(amount) / 100)"
        } else if section == 1 {
            return nil
        }
        return ""
    }
}

struct CheckoutModel {
    var item: MenuItemModel
    var quantity: Int
}
