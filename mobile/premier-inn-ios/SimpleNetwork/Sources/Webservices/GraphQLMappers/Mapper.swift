//
//  Mapper.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 31/05/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public protocol Mapper {
    static func map(from input: PIDictionary) -> PIDictionary
}
