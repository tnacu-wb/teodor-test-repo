//
//  ChooseTablePresenter.swift
//  PremierInn
//
//  Created by Simon Antoine on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

protocol ChooseTablePresenterProtocol: AnyObject {
    func linkViewController(_ vc: ChooseTableTableViewControllerProtocol)
    func viewDidLoad()
    func countTableModel() -> Int
    func countTableFilterModel() -> Int
    func buildCell(cell: ChooseTableTableViewCellProtocol, index: Int)
    func didSelectCell(index: Int)
    func build(header: OrderAndPayHeaderProtocol)
    func filterModel(searchterm: String)
}

final class ChooseTablePresenter {
    private weak var viewController: ChooseTableTableViewControllerProtocol?
    private var service: ChooseTableServiceProtocol
    private var model: TableModel?
    private var filterModel: TableModel?

    init(service: ChooseTableServiceProtocol) {
        self.service = service
    }
}

extension ChooseTablePresenter: ChooseTablePresenterProtocol {
    func filterModel(searchterm: String) {
        let tmpFiltered = model?.tables.filter { tableInfo in
            tableInfo.name.contains(searchterm)
        }

        if let tmp = tmpFiltered {
            filterModel = TableModel(tables: tmp)
        } else {
            filterModel = nil
        }
        viewController?.reloadTable()
    }

    func build(header: OrderAndPayHeaderProtocol) {
        header.set(text: "test")
        header.text(hide: true)
    }

    func buildCell(cell: ChooseTableTableViewCellProtocol, index: Int) {
        let table = (filterModel != nil && filterModel?.tables.isNotEmpty == true) ?
        filterModel?.tables[index] :
        model?.tables[index]

        cell.set(title: table?.name ?? "")
    }

    func linkViewController(_ vc: ChooseTableTableViewControllerProtocol) {
        self.viewController = vc
    }

    func viewDidLoad() {
        service.requestTable { result in
            switch result {
            case .success(let model):
                self.model = model
                self.viewController?.reloadTable()
            case .failure(let error):
                print(error)
            }
        }
    }

    func countTableModel() -> Int {
        model?.tables.count ?? 0
    }

    func countTableFilterModel() -> Int {
        filterModel?.tables.count ?? 0
    }

    func didSelectCell(index: Int) {
        if let table = model?.tables[index] {
            viewController?.goToMenu(table: table)
        }
    }
}
