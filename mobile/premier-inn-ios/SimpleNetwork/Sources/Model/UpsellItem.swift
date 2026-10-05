//
//  UpsellItem.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

public typealias CheckInOnlineAddUpsellsResponse = (newSessionID: String, paymentRequired: Bool)
public typealias AllergenInformation = (name: String, path: String)

public enum UpsellItemsCode: Int {
    case premierInnBreakfast = 11
    case zipBreakfast = 13
    case continentalBreakfast = 12
    case freeChildBreakfast = 15
    case mealDeal = 17
    case hubBreakfast = 18
    case ultimateWifi = 135
}

// More info: https://whitbreadis.atlassian.net/browse/AI-3117?focusedCommentId=258582
public enum UpsellItemOperaId: String {
    case premierInnBreakfast = "BFADBF" // 11/13/18 in bart
    case premierInnBreakfastDE = "BBIB" // 11/13/18 in bart
    case continentalBreakfast = "BFADCT" // 12 in bart
    case freeChildBreakfast = "BFCHDF" // 15 in bart
    case mealDeal = "MDP" // 17 in bart
    case ultimateWifi24Hours = "FI24HR" // 135 in bart
    case ultimateWifi7Days = "FI7DAY" // 136 in bart
    case earlyCheckIn = "HSCKIN"
    case lateCheckOut = "HSCOU2"
    case freeBreakfastPromotion = "OBFPRO"

    public static var extras: [UpsellItemOperaId] {
        [
            .earlyCheckIn,
            .lateCheckOut
        ]
    }
}

public struct UpsellQuantity {
    public var upsellItem: UpsellItem
    public var room: Room?

    public init(upsellItem: UpsellItem, room: Room? = nil) {
        self.upsellItem = upsellItem
        self.room = room
    }

    public var quantityPerUpsell: Int {
        if upsellItem.foodUpsell {
            return upsellItem.kidsHaveToPay == true
            ? (room?.adults ?? 0) + (room?.children ?? 0)
            : room?.adults ?? 0
        } else if upsellItem.wifiUpsell || upsellItem.isExtraUpsell {
            return 1
        } else {
            return 0
        }
    }
}

private enum UpsellItemError: LocalizedError {
    case missingOperaId
    case missingCode
    case missingLegend
    case missingPrice

	var errorDescription: String? { String(describing: self)	}
}

public struct UpsellItem: Equatable {
    // MARK: - Properties

    public var code: Int?

    // Info
    public let legend: String
    public let itemDescription: String
    public let allergenInformation: AllergenInformation?
    public let image: URL?

    // Room
    public var roomId: String?
    public var adults: Int?
    public var children: Int?
    public var quantity: Int?
    public var postingDate: String?

    // Flags
    public var isExtraUpsell: Bool
    public var foodUpsell: Bool
    public var wifiUpsell: Bool
    public let freeBreakfastTrigger: Bool?

    // Free breakfast - Opera only
    let freeBreakfastCode: String?
    let freeBreakfastMaxPerMeal: Int

    // Cost
    public let price: Cost
    public let individualCost: Cost?

    // Opera
    public let id: String
    public let order: Int?
    public let availableCount: Int?

    // Menu
    public let menu: RestaurantMenuItem?

    // MARK: - PI Properties

    /// Unique ID to associate the item with the room with same `uniqueID`
    public var roomUniqueID: UUID?

    // MARK: - Init

    public init(dictionary: PIDictionary) throws {
        guard let id = dictionary["operaId"] as? String else {
            throw UpsellItemError.missingOperaId
        }
        self.id = id

        self.code = dictionary["code"] as? Int ?? Int(dictionary["code"] as? String ?? "")

        guard let legend = dictionary["legend"] as? String else { throw UpsellItemError.missingLegend }

        self.legend = id == UpsellItemOperaId.mealDeal
            .rawValue ? NSLocalizedString("userPreferenceMealDeal", comment: "") : legend

        self.price = {
            let costDictionary: PIDictionary? = dictionary.value(forKeys: ["price", "unitCost"])
            guard let cost = try? Cost(dictionary: costDictionary) else { return Cost.zeroPounds }

            guard id != UpsellItemOperaId.freeBreakfastPromotion.rawValue else { return Cost.zeroPounds }

            if let quantity = dictionary["quantity"] as? Int {
                return Cost(amount: cost.amount.doubleValue * Double(quantity), currencyCode: cost.currencyCode)
            }

            return cost
        }()
        self.isExtraUpsell = dictionary["isExtraUpsell"] as? Bool ?? false
        self.foodUpsell = dictionary["foodUpsell"] as? Bool ?? false
        self.wifiUpsell = id == UpsellItemOperaId.ultimateWifi24Hours.rawValue
        self.postingDate = dictionary["postingDate"] as? String
        self.itemDescription = id == UpsellItemOperaId.ultimateWifi24Hours.rawValue ? NSLocalizedString(
            "utlimeWifiText",
            comment: ""
        ) :
            {
                var string = ""

                if let description = dictionary["description"] as? String {
                    string += description.htmlStripped()
                }

                return string
            }()
        self.freeBreakfastTrigger = {
            guard id != UpsellItemOperaId.freeBreakfastPromotion.rawValue else { return true }

            return dictionary["freeBreakfastTrigger"] as? Bool
        }()

        self.freeBreakfastCode = dictionary["freeBreakfastCode"] as? String
        self.freeBreakfastMaxPerMeal = dictionary["freeBreakfastMaxPerMeal"] as? Int ?? 0

        self.roomId = dictionary["roomId"] as? String
        self.quantity = dictionary["quantity"] as? Int
        self.adults = dictionary["adults"] as? Int
        self.children = dictionary["children"] as? Int

        self.order = dictionary["order"] as? Int
        self.availableCount = dictionary["available"] as? Int
        self.individualCost = UpsellItem.getIndividualCost(dictionary: dictionary)

        self.image = UpsellItem.getImageUrl(dictionary: dictionary)

        self.allergenInformation = UpsellItem.getAllergyInformation(dictionary: dictionary, legend: legend)

        if let menuDict = dictionary["menu"] as? PIDictionary {
            do {
                let data = try JSONSerialization.data(withJSONObject: menuDict, options: .prettyPrinted)
                let decoder = JSONDecoder()
                self.menu = try decoder.decode(RestaurantMenuItem.self, from: data)
            } catch {
                self.menu = nil
            }
        } else {
            self.menu = nil
        }
    }

    private static func getIndividualCost(dictionary: PIDictionary) -> Cost? {
        guard let costDictionary = dictionary["unitCost"] as? PIDictionary,
              let cost = try? Cost(dictionary: costDictionary) else { return nil }

        guard dictionary["operaId"] as? String != UpsellItemOperaId.freeBreakfastPromotion.rawValue
            else { return Cost.zeroPounds }

        return cost
    }

    private static func getImageUrl(dictionary: PIDictionary) -> URL? {
        guard let imagePath = dictionary["imagePath"] as? String else { return nil }
        return Constants.imagesBaseURL.appendingPathComponent(imagePath)
    }

    private static func getAllergyInformation(dictionary: PIDictionary, legend: String) -> AllergenInformation? {
        guard let path = dictionary["allergyInfoSrc"] as? String else { return nil }
        return AllergenInformation(name: legend, path: path)
    }

    // MARK: - Equatable

    public static func == (lhs: UpsellItem, rhs: UpsellItem) -> Bool {
        lhs.id == rhs.id
    }
}

extension UpsellItem {
    public var upsellOperaId: UpsellItemOperaId? {
        UpsellItemOperaId(rawValue: id)
    }

    public var kidsHaveToPay: Bool? {
        // kids never pay based on the latest business requirements
        false
    }

    private init(code: Int, id: String, legend: String, price: Cost, description: String, foodUpsell: Bool) {
        self.code = code
        self.legend = legend
        self.price = price
        self.itemDescription = description
        self.foodUpsell = foodUpsell
        self.wifiUpsell = code == 135
        self.isExtraUpsell = false
        self.postingDate = nil
        self.freeBreakfastTrigger = nil
        self.freeBreakfastCode = nil
        self.freeBreakfastMaxPerMeal = 0
        self.roomId = nil
        self.quantity = nil
        self.adults = nil
        self.children = nil
        self.individualCost = price
        self.image = nil
        self.allergenInformation = nil
        self.id = id
        self.order = nil
        self.availableCount = nil
        self.menu = nil
    }

    public static var freeChildrensBreakfast: UpsellItem {
        UpsellItem(
            code: 15,
            id: "BFCHDF",
            legend: "Free Child Breakfast",
            price: Cost(amount: 0.0, currencyCode: CostUnit.pound.rawValue),
            description: "",
            foodUpsell: false
        )
    }
}

extension UpsellItem: FoodSectionContent {
    public var title: String {
        self.legend + " - " + (self.price.localizedValue.isEmpty ? "" : self.price.localizedValue)
    }

    public var body: String {
        self.itemDescription
    }
}
