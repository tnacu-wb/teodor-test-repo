//
//  ChooseTableService.swift
//  PremierInn
//
//  Created by Simon Antoine on 17/08/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import Foundation

protocol ChooseTableServiceProtocol: AnyObject {
    func requestTable(completion: @escaping ((Result<TableModel, Error>) -> Void))
}

final class ChooseTableService {
}

extension ChooseTableService: ChooseTableServiceProtocol {
    func requestTable(completion: @escaping ((Result<TableModel, Error>) -> Void)) {
        // MARK: - Start Mock
        let model = TableModel(tables: [
            TableInfos(id: 001, name: "Table 1"),
            TableInfos(id: 002, name: "Table 2"),
            TableInfos(id: 002, name: "Table 3"),
            TableInfos(id: 002, name: "Table 4"),
            TableInfos(id: 002, name: "Table 5")
        ])
        completion(.success(model))
        // MARK: - End Mock
    }
}

struct TableModel: Codable {
    var tables: [TableInfos]
}

struct TableInfos: Codable {
    var id: Int
    var name: String
}
