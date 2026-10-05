//
//  SelectAreaViewController.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class SelectAreaViewController: BaseViewController {
    @IBOutlet weak var table: UITableView! {
        didSet {
            table.accessibilityIdentifier = "selectAreaTableView"
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 44

            table.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            ))
            table.backgroundColor = UIColor.BaseGrey

            table.registerCellClass(with: UITableViewCell.self)

            table.tableFooterView = UIView()
        }
    }

    var items = ["Inside", "Garden"]

    override func viewDidLoad() {
        super.viewDidLoad()

        title = "Order & Pay"
    }
}

extension SelectAreaViewController: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = table.dequeueReusableCell(withIdentifier: String(describing: UITableViewCell.self), for: indexPath)

        cell.textLabel?.text = items[indexPath.row]

        return cell
    }

    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        section == 0 ? "Brewers Fayre Test" : nil
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        section == 0 ? 45 : 12
    }

    /*
    func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {

        if let header = view as? UITableViewHeaderFooterView {
            header.contentView.backgroundColor = UIColor.whiteTwo
            header.textLabel?.font = UIFont.Heading4_Semibold()
            header.textLabel?.textColor = UIColor.TintD2
            header.textLabel?.text = self.tableView(tableView, titleForHeaderInSection: section)

            guard let accessibilityPrefixFormat = viewModel.accessibilityPrefix else { return }
            header.textLabel?.accessibilityIdentifier = String(format: accessibilityPrefixFormat, AccessibilityIdentifiers.ButtonRowList.pageHeader)
        }
    }
     */


    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}
