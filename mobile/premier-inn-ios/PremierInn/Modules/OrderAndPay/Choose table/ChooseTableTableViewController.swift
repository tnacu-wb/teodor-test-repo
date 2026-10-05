//
//  ChooseTableTableViewController.swift
//  PremierInn
//
//  Created by Simon Antoine on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

protocol ChooseTableTableViewControllerProtocol: AnyObject {
    func reloadTable()
    func goToMenu(table: TableInfos)
}

final class ChooseTableTableViewController: UITableViewController {
    private var presenter: ChooseTablePresenterProtocol
    private var header: OrderAndPayHeader?

    init(presenter: ChooseTablePresenterProtocol) {
        self.presenter = presenter
        super.init(nibName: String(describing: ChooseTableTableViewController.self), bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.register(
            UINib(nibName: String(describing: ChooseTableTableViewCell.self), bundle: nil),
            forCellReuseIdentifier: String(describing: ChooseTableTableViewCell.self)
        )

        tableView?.register(
            UITableViewHeaderFooterView.self,
            forHeaderFooterViewReuseIdentifier: String(describing: OrderAndPayHeader.self)
        )
        tableView?.rowHeight = UITableView.automaticDimension
        tableView?.estimatedRowHeight = 100.0

        presenter.linkViewController(self)
        presenter.viewDidLoad()

        setUp()
    }

    func setUp() {
        tableView.separatorStyle = .none
        tableView.backgroundColor = .TintL4

        header = .fromNib()
        header?.set(searchBarDelegate: self)
        presenter.build(header: header!)
        tableView.tableHeaderView = header!
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if header?.isSearchActive() ?? false {
            return presenter.countTableFilterModel()
        } else {
            return presenter.countTableModel()
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(
            withIdentifier: String(describing: ChooseTableTableViewCell.self),
            for: indexPath
        ) as? ChooseTableTableViewCell else {return UITableViewCell()}

        presenter.buildCell(cell: cell, index: indexPath.row)
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        presenter.didSelectCell(index: indexPath.row)
    }
}

extension ChooseTableTableViewController: ChooseTableTableViewControllerProtocol {
    func reloadTable() {
        tableView.reloadSections(IndexSet(integer: 0), with: .automatic)
    }

    func goToMenu(table: TableInfos) {
        let vc = MenuTableViewController(nibName: String(describing: MenuTableViewController.self), bundle: nil)
        navigationController?.pushViewController(vc, animated: true)
    }
}

extension ChooseTableTableViewController: UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        presenter.filterModel(searchterm: searchController.searchBar.text ?? "")
    }
}

extension ChooseTableTableViewController: UISearchBarDelegate {
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        presenter.filterModel(searchterm: searchText)
    }
}
