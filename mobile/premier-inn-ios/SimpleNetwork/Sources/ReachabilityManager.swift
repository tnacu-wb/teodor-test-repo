//
//  ReachabilityManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 14/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Alamofire

public protocol ReachabilityManagerDelegate: AnyObject {
    func networkBecameReachable()
    func networkNotReachable()
}

public class ReachabilityManager {
    public weak var delegate: ReachabilityManagerDelegate?

    private let manager = NetworkReachabilityManager(host: "www.google.co.uk")
    private var status: NetworkReachabilityManager.NetworkReachabilityStatus? {
        didSet {
            switch status {
            case .notReachable?:
                delegate?.networkNotReachable()
            case .reachable?:
                delegate?.networkBecameReachable()
            default:
                break
            }
        }
    }

	public init() {
	}

    public func startObserving() {
        manager?.startListening { [weak self] status in
            self?.status = status
        }

        status = manager?.status
    }

    public func stopObserving() {
        manager?.stopListening()
    }
}
