//
//  Rate.swift
//  PremierInn
//
//  Created by Freddie Parks on 29/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public struct Rate {
    public private(set) var uniqueID: UUID

    public var isBiggerRoom: Bool
    public let code: String
	public var totalCost: Cost
    public let description: String?
    public var name: String?
	public let text: String?
    public let classification: String?
    public var rooms: [Room]?
    public var upsellItems: [UpsellItem]? {
        didSet {
            let sorted = upsellItems?.sorted(by: { $0.order ?? 0 < $1.order ?? 1 })
            if sorted != upsellItems {
                upsellItems = sorted
            }
        }
    }
    public let cellCode: String?
    public let promotionCode: String?
    public var lettingTypes: [LettingType]?
    public var foodUpsells: [UpsellItem]? {
        guard let upsellItemsAllowed = UserSessionManager.sharedInstance.currentUser?.company?.bookingAllowances?
              .upsellItemsAllowed else { return upsellItems?.filter { $0.foodUpsell } }

        return upsellItems?.filter { $0.foodUpsell && upsellItemsAllowed.contains(String($0.code ?? 0)) }
    }
    public var wifiUpsells: [UpsellItem]? {
        upsellItems?.filter { $0.id == UpsellItemOperaId.ultimateWifi24Hours.rawValue }
    }
    public var extraUpsells: [UpsellItem]? {
        upsellItems?.filter { upsellItem in
            upsellItem.isExtraUpsell &&
            UpsellItemOperaId.extras.contains(where: { item in
                item.rawValue == upsellItem.id
            }) &&
            upsellItem.availableCount ?? 0 > 0
        }
    }
    public var foodUpsellsImages: [URL] {
        guard let foodUpsells = foodUpsells else { return [] }
        return foodUpsells.compactMap { $0.image }
    }
}

public extension Rate {
    /**

     Creates a new `Rate` object from the existing object but with a new randomly generated UUID

     Rationale: This is currently needed because BART does not return seperate rates for bigger rooms and so we need to generate our own rates on the front end manually. Because of this we need to be able to copy, say, a flex rate and then manually modify certain properties such as the price and letting code; generating a new UUID is even more important to ensure that the two rates are not "the same"

     */
    func copy() -> Rate {
        var copiedVersion = self
        copiedVersion.uniqueID = UUID()
        return copiedVersion
    }
}

extension Rate {
    init(isBiggerRoom: Bool = false, plan: String, classification: String, description: String?, text: String?, cost: Cost) {
        self.uniqueID = UUID()

        self.isBiggerRoom = isBiggerRoom
        self.code = plan
        self.description = description
        self.classification = classification
        self.text = text
        self.totalCost = cost
        self.name = nil
        self.rooms = nil
        self.upsellItems = nil
        self.cellCode = nil
        self.lettingTypes = []
        self.promotionCode = nil
    }

    public init(dictionary: PIDictionary) {
        self.uniqueID = UUID()

        self.isBiggerRoom = false
        self.code = dictionary["code"] as? String ?? ""
        self.description = dictionary["description"] as? String ?? ""
        self.classification = dictionary["classification"] as? String
        self.totalCost = {
            guard let cost = try? Cost(dictionary: dictionary["totalCost"] as? PIDictionary) else {
                let room = (dictionary["rooms"] as? [PIDictionary])?.first
                let options = (room?["options"] as? [PIDictionary])?.first
                let firstOptionCost = options?["totalCost"] as? PIDictionary
                let currencyCode = firstOptionCost?["currency"] as? String

                guard let optionCost = try? Cost(dictionary: firstOptionCost) else {
                    return Cost.zeroCost(currency: currencyCode ?? CostUnit.pound.rawValue)
                }

                return optionCost
            }
            return cost
        }()

        // Text
        self.text = dictionary["text"] as? String ?? ""

        self.name = dictionary["name"] as? String

        if let roomsArray = dictionary["rooms"] as? [PIDictionary] {
            self.rooms = roomsArray.map { Room(dictionary: $0) }
        } else {
            self.rooms = nil
        }

        self.lettingTypes = self.rooms?.compactMap { LettingType(rawValue: $0.lettingType ?? "") ?? .double }

        self.upsellItems = {
            if let array = dictionary["upsellItems"] as? [PIDictionary] {
                return try? array.compactMap { try UpsellItem(dictionary: $0) }
            } else if let array = dictionary["food"] as? [PIDictionary] {
                return array.compactMap({ foodDict -> UpsellItem? in
                    var dict = foodDict
                    dict["foodUpsell"] = true

                    return try? UpsellItem(dictionary: dict)
                })
            }

            return nil
        }()

        self.cellCode = dictionary["cellCode"] as? String
        self.promotionCode = dictionary["promotionCode"] as? String ?? ""
    }
}
