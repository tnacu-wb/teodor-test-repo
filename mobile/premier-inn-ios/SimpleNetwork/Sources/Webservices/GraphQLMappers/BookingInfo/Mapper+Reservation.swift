//
//  Mapper+Reservation.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 22/07/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum ReservationMapper {
    public static func map(
        from input: PIDictionary,
        basketReference: String,
        manageBooking: PIDictionary?,
        packagesDict: PIDictionary?,
        isBusiness: Bool
    ) -> PIDictionary {
        var mainDict = PIDictionary()

        mainDict["reservationDetails"] = ReservationDetailsMapper.map(
            from: input,
            basketReference: basketReference,
            manageBooking: manageBooking,
            packagesDict: packagesDict,
            isBusiness: isBusiness
        )
        mainDict["upsellItemsAvailable"] = ReservationUpsellsMapper.map(from: packagesDict)

        return mainDict
    }
}

public enum ReservationUpsellsMapper {
    public static func map(from input: PIDictionary?) -> [PIDictionary]? {
        var upsellsBreakdownMapped = UpsellBreakdownMapper.map(from: input)

        guard let packages = input?["packages"] as? PIDictionary else {
            return upsellsBreakdownMapped
        }

        let extraUpsells = packages["extrasItems"] as? [PIDictionary]

        extraUpsells?.forEach { extraUpsell in
            let roomSelections = packages["roomSelection"] as? [PIDictionary]
            roomSelections?.forEach { room in
                let packagesSelections = room["packagesSelection"] as? [PIDictionary]
                let extraUpsellDict = packagesSelections?
                    .filter {
                        $0["id"] as? String == extraUpsell["id"] as? String
                    }
                    .map { package in
                        var dict = ExtraUpsellMapper.map(from: extraUpsell)
                        dict["roomId"] = room["reservationId"]
                        dict["quantity"] = package["noOfSelections"] as? Int
                        return dict
                    }
                upsellsBreakdownMapped?.append(contentsOf: extraUpsellDict ?? [])
            }
        }
        return upsellsBreakdownMapped
    }
}

public enum ReservationDetailsMapper {
    public static func map(
        from input: PIDictionary,
        basketReference: String,
        manageBooking: PIDictionary?,
        packagesDict: PIDictionary?,
        isBusiness: Bool
    ) -> PIDictionary {
        var dict = PIDictionary()

        if let reservationByIdList = input["reservationByIdList"] as? [PIDictionary] {
            dict["hotelCode"] = input["hotelId"]
            dict["rooms"] = RoomsMapper.map(from: reservationByIdList, currencyCode: input["currencyCode"] as? String)
            dict["booker"] = BookerMapper.map(from: reservationByIdList)
            dict["totalCost"] = TotalCostMapper.map(from: input)
            dict["confirmationNumber"] = input["bookingReference"] as? String
            dict["basketStatus"] = input["basketStatus"] as? String
            dict["operaBasketReference"] = basketReference
            dict["breakfasts"] = UpsellBreakdownMapper.map(from: packagesDict)
            dict["upsellBreakdown"] = ["upsellItems": ReservationUpsellsMapper.map(from: packagesDict)]
            dict["bookingFlowId"] = input["bookingFlowId"]
            dict["isThirdPartyBooking"] = input["isThirdPartyBooking"]

            dict["reservationPackageList"] = reservationByIdList.reduce([]) { (sum, roomStay) -> [PIDictionary] in
                let packageList = roomStay["reservationPackageList"] as? [PIDictionary] ?? []
                let sum = sum + packageList

                return sum
            }
            dict["preferences"] = BookingConfirmationPreferencesMapper.map(from: reservationByIdList)

            let singleRateDictionary = BookingConfirmationRatesMapper.map(from: reservationByIdList)

            dict["arrivalDate"] = singleRateDictionary["arrivalDate"]
            dict["departureDate"] = singleRateDictionary["departureDate"]
            dict["rateClass"] = singleRateDictionary["ratePlanCode"]
            dict["ratePlan"] = singleRateDictionary["ratePlanCode"]

            if let rateExtraInfo = singleRateDictionary["rateExtraInfo"] as? PIDictionary {
                dict["rateText"] = rateExtraInfo["rateName"]
                dict["rateDescription"] = rateExtraInfo["rateDescription"]
            }

            let isCheckInAvailable = manageBooking?["isCheckInOnlineAvailable"] as? Bool == true

            dict["cancelable"] = manageBooking?["isCancellable"] as? Bool == true
            dict["amendable"] = manageBooking?["isAmendable"] as? Bool == true
            dict["isDigitalKey"] = manageBooking?["isDigitalKey"] as? Bool == true
            dict["checkInOnlineAvailable"] = isCheckInAvailable
            dict["isCheckOutOnlineAvailable"] = manageBooking?["isCheckOutOnlineAvailable"] as? Bool == true

            // Booking-level fields from top-level bookingConfirmation response
            dict["upsellsAddOnEnabled"] = input["upsellsAddonsEnabled"] as? Bool

            // Map paymentOption from top-level bookingConfirmation response (ENUM: CC, PIBA_CP, PIBA_CNP)
            dict["paymentOption"] = input["paymentOption"] as? String

            dict["prepaidAmount"] = PrepaidAmountMapper.map(from: input)
            dict["balanceOutstanding"] = BalanceOutstanding.map(from: input)
            dict["business"] = isBusiness

            dict["amendRestrictions"] = AmendRestrictionsMapper.map(from: manageBooking)
        }
        return dict
    }
}

private enum UpsellBreakdownMapper {
    static func map(from input: PIDictionary?) -> [PIDictionary]? {
        guard let packages = input?["packages"] as? PIDictionary else { return nil }
        guard var meals = packages["meals"] as? [PIDictionary] else { return nil }
        let mealsKids = packages["mealsKids"] as? [PIDictionary]

        if let mealsKids = mealsKids {
            meals = (meals + mealsKids)
        }

        guard let roomSelection = packages["roomSelection"] as? [PIDictionary] else { return nil }

        var breakfasts = [PIDictionary]()

        for roomPackages in roomSelection {
            guard let packageSelection = roomPackages["packagesSelection"] as? [PIDictionary] else { return nil }

            for package in packageSelection {
                let packageId = package["id"] as? String

                for meal in meals where (meal["id"] as? String) == packageId {
                    var breakfast = PIDictionary()

                    breakfast["operaId"] = packageId
                    breakfast["legend"] = meal["name"]
                    breakfast["roomId"] = roomPackages["reservationId"]
                    breakfast["freeBreakfastTrigger"] = (meal["freeBreakfastCode"] as? String)?.isEmpty == false
                    breakfast["freeBreakfastCode"] = meal["freeBreakfastCode"]
                    breakfast["freeBreakfastMaxPerMeal"] = meal["freeBreakfastMaxPerMeal"]

                    let noOfSelections = package["noOfSelections"] as? Int

                    if KidsBreakfastMapper.isKidsBreakfast(from: mealsKids, id: packageId) {
                        breakfast["code"] = UpsellItemsCode.freeChildBreakfast.rawValue
                        breakfast["adults"] = 0
                        breakfast["children"] = noOfSelections
                        breakfast["foodUpsell"] = false
                    } else {
                        breakfast["code"] = meal["bartId"] as? String
                        breakfast["adults"] = noOfSelections
                        breakfast["children"] = 0
                        breakfast["foodUpsell"] = true
                    }

                    breakfast["quantity"] = noOfSelections

                    let unitCostAmount = meal["price"] as? Double
                    let subTotalAmount = (unitCostAmount ?? 0.0) * (Double(noOfSelections ?? 1))
                    let currency = meal["currency"] as? String
                    let unitCost: [String: Any] = [
                        "amount": unitCostAmount ?? 0.0,
                        "currency": currency ?? CostUnit.pound.rawValue
                    ]
                    breakfast["unitCost"] = unitCost

                    let subtotal: [String: Any] = [
                        "amount": subTotalAmount,
                        "currency": currency ?? CostUnit.pound.rawValue
                    ]
                    breakfast["subtotal"] = subtotal
                    breakfast["imagePath"] = meal["imageSrc"]

                    breakfasts.append(breakfast)
                }
            }
        }

        return breakfasts
    }
}

private enum AvailableUpsellsMealsMapper {
    static func map(meal: PIDictionary, mealsKids: [PIDictionary]?) -> PIDictionary {
        var upsell = PIDictionary()

        let mealId = meal["id"] as? String

        if KidsBreakfastMapper.isKidsBreakfast(from: mealsKids, id: mealId) {
            upsell["code"] = UpsellItemsCode.freeChildBreakfast.rawValue
            upsell["foodUpsell"] = false
            upsell["description"] = meal["description"]
        } else {
            upsell["code"] = meal["bartId"] as? String
            upsell["foodUpsell"] = true
            upsell["description"] = meal["shortDescription"]
        }

        upsell["operaId"] = mealId
        upsell["legend"] = meal["name"]

        let unitCostAmount = meal["price"] as? Double
        let currency = meal["currency"] as? String
        let unitCost: [String: Any] = [
            "amount": unitCostAmount ?? 0.0,
            "currency": currency ?? CostUnit.pound.rawValue
        ]
        upsell["unitCost"] = unitCost

        upsell["order"] = meal["order"]
        upsell["freeBreakfastMaxPerMeal"] = meal["freeBreakfastMaxPerMeal"]
        upsell["freeBreakfastCode"] = meal["freeBreakfastCode"]
        upsell["freeBreakfastTrigger"] = (meal["freeBreakfastCode"] as? String)?.isEmpty == false

        upsell["imagePath"] = meal["imageSrc"]
        upsell["allergyInfoSrc"] = meal["allergyInfoSrc"]

        upsell["menu"] = RestaurantMenuItemMapper.map(from: meal["menu"] as? PIDictionary ?? [:])

        return upsell
    }
}

public enum AvailableUpsellsMapper {
    public static func map(from input: PIDictionary?) -> [PIDictionary]? {
        guard let packages = input?["packages"] as? PIDictionary else { return nil }
        guard var meals = packages["meals"] as? [PIDictionary] else { return nil }
        let mealsKids = packages["mealsKids"] as? [PIDictionary]

        if let mealsKids = mealsKids {
            meals = (meals + mealsKids)
        }

        var availableUpsells: [PIDictionary] = []

        let mealsArray: [PIDictionary] = meals.map { AvailableUpsellsMealsMapper.map(meal: $0, mealsKids: mealsKids) }
        availableUpsells.append(contentsOf: mealsArray)

        if let extraUpsells = packages["extrasItems"] as? [PIDictionary] {
            let extraItemsArray = extraUpsells.map {
                ExtraUpsellMapper.map(from: $0)
            }
            availableUpsells.append(contentsOf: extraItemsArray)
        }

        return availableUpsells
    }
}

private enum ExtraUpsellMapper {
    static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["operaId"] = input["id"]
        dict["legend"] = input["name"]
        dict["description"] = input["description"]
        dict["order"] = input["order"]
        dict["isExtraUpsell"] = true
        dict["available"] = input["available"]
        dict["price"] = [
            "amount": input["price"] as? Double ?? 0.0,
            "currency": input["currency"] ?? CostUnit.pound.rawValue
        ]
        dict["unitCost"] = dict["price"]
        dict["imagePath"] = input["imageSrc"]

        return dict
    }
}

private enum RoomsMapper {
    static func map(from input: [PIDictionary], currencyCode: String?) -> [PIDictionary] {
        var dicts = [PIDictionary]()
        for room in input {
            var dict = PIDictionary()

            dict["roomId"] = room["reservationId"] as? String

            if let roomStay = room["roomStay"] as? PIDictionary {
                dict["reservationId"] = room["reservationId"]
                dict["preCheckInStatus"] = room["preCheckInStatus"]
                dict["deRegCardCompleted"] = room["deRegCardCompleted"]
                let roomExtraInfo = roomStay["roomExtraInfo"] as? PIDictionary
                dict["roomName"] = roomExtraInfo?["roomName"] as? String
                dict["roomNumber"] = roomStay["roomNumber"] as? String

                dict["groupId"] = roomExtraInfo?["groupId"] as? String
                dict["roomType"] = roomStay["roomType"]
                dict["lettingType"] = roomStay["roomType"]
                dict["adults"] = roomStay["adultsNumber"]
                dict["children"] = roomStay["childrenNumber"]
                dict["cot"] = roomStay["cot"]
                dict["totalCost"] = [
                    "amount": roomStay["roomPrice"],
                    "currency": currencyCode
                ]
            }

            if let status = room["reservationStatus"] as? String {
                dict["bookingStatus"] = BookingStatus(rawValue: status)?.rawValue ?? BookingStatus.unknown.rawValue
            }

            if let reservationGuestList = room["reservationGuestList"] as? [PIDictionary] {
                let guestList = reservationGuestList.map { RoomGuestMapper.map(from: $0) }
                dict["guestList"] = guestList

                if let leadGuest = reservationGuestList.first {
                    dict["guest"] = RoomGuestMapper.map(from: leadGuest)
                }

                if let accompanyingGuest = reservationGuestList.first(where: { dictionary in
                    (dictionary["isAccompanyingGuest"] as? Bool) == true
                }) {
                    dict["accompanyingGuest"] = RoomGuestMapper.map(from: accompanyingGuest)
                }
            }
            dicts.append(dict)
        }
        return dicts
    }
}

private enum BookerMapper {
    static func map(from input: [PIDictionary]) -> PIDictionary {
        var dict = PIDictionary()

        guard let room = input.first(where: { $0["billing"] is PIDictionary }),
              let billing = room["billing"] as? PIDictionary else {
            return dict
        }
        dict["title"] = billing["title"]
        dict["firstName"] = billing["firstName"]
        dict["lastName"] = billing["lastName"]
        dict["emailAddress"] = billing["email"]
        dict["telephone"] = billing["telephone"]
        if let address = billing["address"] as? PIDictionary {
            dict["address"] = BookerAddressMapper.map(from: address)
        }
        return dict
    }
}

public enum TotalCostMapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["amount"] = input["totalCost"]
        dict["currency"] = input["currencyCode"]

        return dict
    }
}

private enum BookingConfirmationRatesMapper {
    static func map(from input: [PIDictionary]) -> PIDictionary {
        var dict = PIDictionary()
        for room in input {
            if let roomDict = room["roomStay"] as? PIDictionary {
                dict = roomDict
                break
            }
        }
        return dict
    }
}

private enum RoomGuestMapper {
    static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["title"] = input["nameTitle"]
        dict["firstName"] = input["givenName"]
        dict["lastName"] = input["surName"]
        if let additionalDetails = input["additionalDetails"] as? PIDictionary {
            dict["additionalDetails"] = AdditionalDetailsMapper.map(from: additionalDetails)
        }
        dict["isAccompanyingGuest"] = input["isAccompanyingGuest"]
        dict["profileId"] = input["profileId"]
        if let address = input["address"] as? PIDictionary {
            dict["address"] = BookerAddressMapper.map(from: address)
        }
        return dict
    }
}

private struct AdditionalDetailsMapper: Mapper {
    static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["dob"] = input["dob"]
        dict["passportNumber"] = input["passportNumber"]
        dict["nationality"] = input["nationality"]

        return dict
    }
}

private struct BookerAddressMapper: Mapper {
    static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["line1"] = input["addressLine1"]
        dict["line2"] = input["addressLine2"]
        dict["line3"] = input["addressLine3"]
        dict["line4"] = input["addressLine4"]
        dict["line5"] = input["addressLine5"]
        dict["cityName"] = input["cityName"]
        dict["countryCode"] = input["country"] ?? input["countryCode"]
        dict["postcode"] = input["postalCode"]

        return dict
    }
}

private enum PrepaidAmountMapper {
    static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        if let totalCost = input["totalCost"] as? Double, let balanceOutstanding = input["balanceOutstanding"] as? Double {
            dict["amount"] = totalCost - balanceOutstanding
            dict["currency"] = input["currencyCode"]
        }
        return dict
    }
}

public enum BalanceOutstanding {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        if let balanceOutstanding = input["balanceOutstanding"] as? Double {
            dict["amount"] = balanceOutstanding
            dict["currency"] = input["currencyCode"]
        }
        return dict
    }
}

private enum KidsBreakfastMapper {
    static func isKidsBreakfast(from input: [PIDictionary]?, id: String?) -> Bool {
        guard let mealKidsArray = input else { return false }

        let isKidsBreakfast = mealKidsArray.contains(where: { $0["id"] as? String == id })
        return isKidsBreakfast
    }
}

private enum AmendRestrictionsMapper {
    static func map(from input: PIDictionary?) -> PIDictionary {
        var dict = PIDictionary()

        let amendable = input?["isAmendable"] as? Bool == true
        let cancellable = input?["isCancellable"] as? Bool == true

        // this is how we currently identify when this limited amend functionality should be applied
        if amendable && !cancellable {
            // true for restricted
            dict["dates"] = false
            dict["nights"] = true
            dict["upsells"] = false
            dict["addRoom"] = false
            dict["editRoom"] = true
            dict["editGuestNames"] = true
            dict["removeRoom"] = true
        }

        return dict
    }
}

private enum BookingConfirmationPreferencesMapper {
    static func map(from input: [PIDictionary]) -> [PIDictionary] {
        var dicts = [PIDictionary]()
        for room in input {
            if let roomPreferenceDict = room["preferences"] as? [PIDictionary] {
                dicts.append(contentsOf: roomPreferenceDict)
            }
        }
        return dicts
    }
}

public enum UpdateReservationPreferencesMapper {
    public static func map(from input: String) -> PIDictionary {
        var dict = PIDictionary()
        // We don't need to do anything with the response
        dict["updateReservationPreferences"] = input
        return dict
    }
}
