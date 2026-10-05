//
//  SimpleListViewController.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 18.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

class SimpleListViewController: UIViewController {
    private let listItems: [String]

    private let tableView: UITableView = {
       let tableView = UITableView()
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.rowHeight = 44
        tableView.backgroundColor = .ColourLD5
        tableView.separatorColor = .TintL2
        tableView.register(
            SimpleListCell.self,
            forCellReuseIdentifier: SimpleListCell.reuseIdentifier
        )
        return tableView
    }()

    var itemSelected: ((String) -> Void)?

    init(listItems: [String]) {
        self.listItems = listItems
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        configureModalNavigationBar()
        tableView.dataSource = self
        tableView.delegate = self

        setupView()
        setupConstraints()
    }

    // MARK: - Setup View
    private func setupView() {
        view.addSubview(tableView)
    }

    private func setupConstraints() {
        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 21),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
}

extension SimpleListViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        listItems.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
         guard let cell = tableView.dequeueReusableCell(
             withIdentifier: SimpleListCell.reuseIdentifier,
             for: indexPath
         ) as? SimpleListCell else { return UITableViewCell() }
        cell.titleLabel.text = listItems[indexPath.row]
        return cell
    }
}

extension SimpleListViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        itemSelected?(listItems[indexPath.row])
    }
}
