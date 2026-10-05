//
//  MenuTableViewController.swift
//  PremierInn
//
//  Created by Simon Antoine on 25/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

class MenuTableViewController: UITableViewController {
    private var model: MenuModel? {
        didSet {
            tableView.reloadData()
        }
    }

    private var selectedCell: [MenuTableViewCell] = []
    private var selectedModel: [MenuItemModel] = []

    @IBOutlet weak var continueButton: UIButton!
    @IBOutlet weak var footerView: UIView!
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.register(
            UINib(nibName: String(describing: MenuTableViewCell.self), bundle: nil),
            forCellReuseIdentifier: String(describing: MenuTableViewCell.self)
        )
        tableView?.rowHeight = UITableView.automaticDimension
        tableView?.estimatedRowHeight = 100.0
        tableView?.backgroundColor = .TintL4

        footerView.backgroundColor = .TintL4
        continueButton.setTitle(PILocalizedString("Continue to payment"), for: .normal)
        continueButton.backgroundColor = .BasePurple
        continueButton.setTitleColor(.white, for: .normal)
        continueButton.layer.cornerRadius = continueButton.frame.height / 2

        parseJSON()
    }

    func parseJSON() {
        if let url = Bundle.main.url(forResource: "example-menu", withExtension: "json") {
            do {
                let data = try Data(contentsOf: url)
                let jsonData = try JSONDecoder().decode(MenuModel.self, from: data)
                model = jsonData
              } catch {
                   print(error)
              }
        }
    }
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        model?.menu.categories.count ?? 0
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        model?.menu.categories[section].items.count ?? 0
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let item = model?.menu.categories[indexPath.section].items[indexPath.row] {
            selectedModel.append(item)
            tableView.reloadRows(at: [indexPath], with: .automatic)
        }
    }

    private func selectionCounter(id: Int) -> Int {
        let listFiltered = selectedModel.filter { item in
            item.id == id
        }
        return listFiltered.count
    }

    override func tableView(
        _ tableView: UITableView,
        commit editingStyle: UITableViewCell.EditingStyle,
        forRowAt indexPath: IndexPath
    ) {
        if editingStyle == .delete {
            let idSelected = model?.menu.categories[indexPath.section].items[indexPath.row].id
            selectedModel = selectedModel.compactMap({ item in
                item.id != idSelected ? item : nil
            })
            tableView.reloadRows(at: [indexPath], with: .automatic)
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(
            withIdentifier: String(describing: MenuTableViewCell.self),
            for: indexPath
        ) as? MenuTableViewCell else { return UITableViewCell() }

        cell.containerView.layer.cornerRadius = 4
        cell.backgroundColor = .TintL4
        var tmpCount = 0
        if selectedModel.isNotEmpty {
            tmpCount = selectionCounter(id: model?.menu.categories[indexPath.section].items[indexPath.row].id ?? 0)
        }
        cell.addButton.setTitle(tmpCount > 0 ? "\(tmpCount)" : "Add", for: .normal)

        if tmpCount > 0 {
            cell.addButton.backgroundColor = .BasePurple
            cell.addButton.setTitleColor(.white, for: .normal)
        } else {
            cell.addButton.backgroundColor = .clear
            cell.addButton.setTitleColor(.black, for: .normal)
        }

        cell.addButton.layer.cornerRadius = cell.addButton.frame.height / 2
        cell.titleLabel.text = model?.menu.categories[indexPath.section].items[indexPath.row].name
        cell.descriptionLabel.text = model?.menu.categories[indexPath.section].items[indexPath.row].description
        cell.price.text = "£\(Double(model?.menu.categories[indexPath.section].items[indexPath.row].price ?? 0) / 100)"

        return cell
    }

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let viewHeader = MenuHeaderSection(frame: CGRect(
            x: 0,
            y: 0,
            width: tableView.frame.width,
            height: model?.menu.categories[section].description.isEmpty ?? true ? 40 : 75
        ))
        viewHeader.titleLabel.text = model?.menu.categories[section].name
        viewHeader.descriptionLabel.text = model?.menu.categories[section].description
        viewHeader.contentView.backgroundColor = .TintL4
        viewHeader.descriptionLabel.isHidden = viewHeader.descriptionLabel.text?.isEmpty ?? true

        return viewHeader
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        model?.menu.categories[section].description.isEmpty ?? true ? 40 : 75
    }

    @IBAction func continuePressed(_ sender: Any) {
        if selectedModel.isNotEmpty {
            let vc = CheckoutTableViewController.init(model: selectedModel)
            navigationController?.pushViewController(vc, animated: true)
        }
    }
}

struct MenuModel: Codable {
    var info: InfoModel
    var areas: [AreaModel]
    var menu: MenuInfoModel
}

struct MenuInfoModel: Codable {
    var categories: [MenuCategoryModel]
}

struct MenuCategoryModel: Codable {
    var name: String
    var description: String
    var restrictions: String
    var items: [MenuItemModel]
}

struct MenuItemModel: Codable {
    var id: Int
    var name: String
    var description: String
    var price: Int
    var dietaryOptions: MenuDietaryModel?
    var options: [MenuOptionModel]?
    var sides: [MenuSideModel]?
}

struct MenuSideModel: Codable {
    var name: String
    var price: Int?
}

struct MenuOptionModel: Codable {
    var name: String
    var price: Int?
}

struct MenuDietaryModel: Codable {
    var vegan: Bool
    var meat: Bool
}

struct AreaModel: Codable {
    var type: String
    var tables: [Int]
}

struct InfoModel: Codable {
    var name: String
    var telephone: String
    var vatNumber: String
    var orgNumber: String
    var address: AddressModel
}

struct AddressModel: Codable {
    var line1: String
    var line2: String
    var line3: String
    var postCode: String
}
