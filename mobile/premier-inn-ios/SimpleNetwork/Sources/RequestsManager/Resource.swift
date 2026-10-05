//
//  Resource.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 30/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

public struct Resource<T> {
	let url: URL
	let parameters: PIDictionary?
	let data: Data?
	let method: HTTPMethod
	let encoding: ParameterEncoding
	let headers: [String: String]?
	let parse: (Any) throws -> T?
	let authCredentials: AuthCredentials?
    let version: String?

    init(
    	url: URL,
    	parameters: PIDictionary? = nil,
    	data: Data? = nil,
    	method: HTTPMethod,
    	encoding: ParameterEncoding,
    	headers: [String: String]? = nil,
    	authCredentials: AuthCredentials? = nil,
    	version: String? = nil,
    	parse: @escaping (Any) throws -> T?
    ) {
		self.url = url
        self.parameters = parameters
		self.data = data
		self.method = method
		self.encoding = encoding
		self.headers = headers
		self.parse = parse
		self.authCredentials = authCredentials
        self.version = version
	}
}
