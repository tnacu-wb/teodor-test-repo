//
//  BookingSummaryInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

enum BookingSummaryError: Error {
    case missingArrivalDate
    case missingReservation
}

protocol BookingSummaryInteractorProtocol {
    var mealModels: [BookingSummaryMealViewModel] { get }
    var wifiModels: [BookingSummaryWifiViewModel] { get }
    var extrasModels: [BookingSummaryExtrasViewModel] { get }

    func load(completion: @escaping (Reservation?, BookingDetails?, Hotel?, Error?) -> Void)
}

class BookingSummaryInteractor {
    private let hotel: Hotel?
    private let bookingDetails: BookingDetails?

    private var reservation: Reservation?
    private var requestsManager = RequestsManager()

    deinit {
        requestsManager.cancelConnections()
    }

    init(hotel: Hotel, reservation: Reservation?) {
        self.hotel = hotel
        self.bookingDetails = nil
        self.reservation = reservation
    }

    init(bookingDetails: BookingDetails) {
        self.hotel = bookingDetails.hotel
        self.bookingDetails = bookingDetails
    }
}

struct MealCodeRoomIdPair: Hashable {
    let code: String
    let roomID: String?
}

extension MealCodeRoomIdPair: Equatable {
    static func == (lhs: MealCodeRoomIdPair, rhs: MealCodeRoomIdPair) -> Bool {
        lhs.code == rhs.code && lhs.roomID == rhs.roomID
    }
}

extension BookingSummaryInteractor: BookingSummaryInteractorProtocol {
    var mealModels: [BookingSummaryMealViewModel] {
        guard let numberOfNights = reservation?.nights ?? bookingDetails?.criteria.nights else { return [] }

        if let upsellBreakdown = reservation?.foodUpsells, let breakfasts = reservation?.breakfasts {
            let zuniqueMealRoomIds =
                Array(Set(upsellBreakdown.compactMap { MealCodeRoomIdPair(code: $0.id, roomID: $0.roomId) }))
            let zuniqueUpsells = Array(Set(upsellBreakdown.compactMap { $0.id }))
                .compactMap { id in upsellBreakdown.first(where: { breakfast in breakfast.id == id }) }

            let uniqueRoomMeals = zuniqueMealRoomIds.compactMap { zuniqueMealRoom in
                breakfasts.first(where: { $0.id == zuniqueMealRoom.code && $0.roomId == zuniqueMealRoom.roomID })
            }

            var freeChildBreakfastsCount = 0
            var freeChildBreakfastCountedRoomNumbers = Set<String?>()

            var roomMealModels: [BookingSummaryMealViewModel] = zuniqueUpsells.map { upsell in
                let isFreeBreakfastTrigger = upsell.freeBreakfastTrigger == true
                let isFreeChildBreakfastCountedForUpsellRoom = freeChildBreakfastCountedRoomNumbers.contains(upsell.roomId)
                let isUpsellFreeChildBreakfast = upsell.code == UpsellItem.freeChildrensBreakfast.code

                if isFreeBreakfastTrigger && !isUpsellFreeChildBreakfast && !isFreeChildBreakfastCountedForUpsellRoom {
                    let upsellRoomId = uniqueRoomMeals
                        .first(where: { uniqueRoomMeals in uniqueRoomMeals.code == upsell.code })?
                        .roomId

                    freeChildBreakfastsCount += reservation?
                        .rooms
                        .first(where: { room in room.roomId == upsellRoomId })?
                        .children
                    ?? 0

                    freeChildBreakfastCountedRoomNumbers.insert(upsellRoomId)
                }

                let gCount = uniqueRoomMeals.filter { uniqueRoomMeal in uniqueRoomMeal.code == upsell.code }.reduce(0, {
                    $0 + $1.guestCount
                })

                let gTotal = uniqueRoomMeals.filter { uniqueRoomMeal in uniqueRoomMeal.code == upsell.code }.reduce(0.0, {
                    $0 + $1.price.amount.doubleValue
                })

                let totalCostText = isUpsellFreeChildBreakfast
                ? PILocalizedString("bookingSummaryKidsBreakfastValue")
                : Cost(
                    amount: gTotal * Double(numberOfNights),
                    currencyCode: upsell.price.currencyCode
                ).localizedValue

                return BookingSummaryMealViewModel(
                    meal: upsell,
                    childrenCount: nil,
                    guestsCountDescription: Criteria.guestsCountDescription(for: gCount),
                    adultsCountDescription: nil,
                    childrenCountDescription: nil,
                    totalCostText: totalCostText
                )
            }

            let isFreeChildBreakfastReceivedFromBackend = uniqueRoomMeals
                .contains(where: { $0.code == UpsellItem.freeChildrensBreakfast.code })

            if (freeChildBreakfastsCount > 0) && !isFreeChildBreakfastReceivedFromBackend {
                roomMealModels.append(
                    BookingSummaryMealViewModel(
                        meal: UpsellItem.freeChildrensBreakfast,
                        childrenCount: nil,
                        guestsCountDescription: Criteria.guestsCountDescription(for: freeChildBreakfastsCount),
                        adultsCountDescription: nil,
                        childrenCountDescription: nil,
                        totalCostText: PILocalizedString("bookingSummaryKidsBreakfastValue")
                    )
                )
            }

            return roomMealModels
        } else if let roomMealModels = bookingDetails?.roomMealCombos {
            let uniqueMealsIds = Array(Set(roomMealModels.compactMap { $0.meal.id }))

            let uniqueMealsAndQuantities: [(upsell: UpsellItem, quantity: Int)] = uniqueMealsIds.compactMap { id in
                guard let uniqueMeal = roomMealModels.first(where: { $0.meal.id == id })?.meal else { return nil }
                let quantity = roomMealModels.filter { $0.meal.id == id }.reduce(0, { $0 + $1.quantity })

                return (uniqueMeal, quantity)
            }

            var freeChildBreakfastsCount = 0
            var freeChildBreakfastCountedRoomNumbers = Set<Int>()
            var mealViewModels: [BookingSummaryMealViewModel] = uniqueMealsAndQuantities.compactMap { mealWithQuantity in
                let meal = mealWithQuantity.upsell
                let quantity = mealWithQuantity.quantity

                let roomNumbers = roomMealModels.filter { $0.meal.id == meal.id }.compactMap { $0.roomNumber }

                if meal.freeBreakfastTrigger == true {
                    freeChildBreakfastsCount += roomNumbers.compactMap { roomNumber in
                        // Don't increment if this room's free child breakfast is already counted
                        // Prevent double-counting free child breakfasts for this room
                        guard !freeChildBreakfastCountedRoomNumbers.contains(roomNumber) else {
                            return nil
                        }

                        freeChildBreakfastCountedRoomNumbers.insert(roomNumber)

                        guard let room = self.bookingDetails?.criteria.rooms[roomNumber] else {
                            return nil
                        }

                        return room.children
                        }.reduce(0, +)
                }

                let total = meal.price.amount.doubleValue * Double(quantity) * Double(numberOfNights)
                let totalCost = Cost(amount: total, currencyCode: meal.price.currencyCode)

                return BookingSummaryMealViewModel(
                    meal: meal,
                    childrenCount: nil,
                    guestsCountDescription: Criteria.guestsCountDescription(for: quantity),
                    adultsCountDescription: nil,
                    childrenCountDescription: nil,
                    totalCostText: totalCost.localizedValue
                )
            }

            if freeChildBreakfastsCount > 0 {
                mealViewModels.append(BookingSummaryMealViewModel(
                    meal: UpsellItem.freeChildrensBreakfast,
                    childrenCount: nil,
                    guestsCountDescription: Criteria.guestsCountDescription(for: freeChildBreakfastsCount),
                    adultsCountDescription: nil,
                    childrenCountDescription: nil,
                    totalCostText: PILocalizedString("bookingSummaryKidsBreakfastValue")
                ))
            }

            return mealViewModels
        }

        return []
    }

    var wifiModels: [BookingSummaryWifiViewModel] {
        if let wifiUpsells = reservation?.wifiUpsells {
            let zuniqueUpsells = Array(Set(wifiUpsells.compactMap { $0.id }))
                .compactMap { id in wifiUpsells.first(where: { wifi in wifi.id == id }) }
            let zuniqueMealRoomIds =
                Array(Set(wifiUpsells.compactMap { MealCodeRoomIdPair(code: $0.id, roomID: $0.roomId) }))
            let uniqueRoomMeals = zuniqueMealRoomIds.compactMap { zuniqueMealRoom in
                wifiUpsells.first(where: { $0.id == zuniqueMealRoom.code && $0.roomId == zuniqueMealRoom.roomID }) }

            return zuniqueUpsells.map { upsell in
                // the amount already includes the number of nights because we are using the data from the packages call
                let gAmount = uniqueRoomMeals.filter { uniqueRoomMeal in uniqueRoomMeal.code == upsell.code }.count
                let gTotal = upsell.price.amount.doubleValue * Double(gAmount)

                let totalCostText = Cost(amount: gTotal, currencyCode: upsell.price.currencyCode).localizedValue

                return BookingSummaryWifiViewModel(wifi: upsell, numberOfRooms: gAmount, totalCostText: totalCostText)
            }
        }

        return []
    }

    var extrasModels: [BookingSummaryExtrasViewModel] {
        if let reservedExtraUpsells = reservation?.extraUpsells {
            // Booking Confirmation Screen

            return calculateBookingSummaryExtrasViewModel(extraUpsells: reservedExtraUpsells)
        } else if let extrasPackages = bookingDetails?.roomExtraPackages {
            // review & book + upsells screen
            let extraUpsells = extrasPackages.map { $0.meal }

            return calculateBookingSummaryExtrasViewModel(extraUpsells: extraUpsells)
        }

        return []
    }

    private func calculateBookingSummaryExtrasViewModel(extraUpsells: [UpsellItem]) -> [BookingSummaryExtrasViewModel] {
        let groupedUpsellsById = Dictionary(grouping: extraUpsells, by: { $0.id })

        var extrasViewModels: [BookingSummaryExtrasViewModel] = []

        for (extraUpsellId, extraUpsells) in groupedUpsellsById {
            guard let extraUpsell = extraUpsells.first(where: { $0.id == extraUpsellId }) else { continue }

            let upsellsCount = extraUpsells.count
            let totalAmount = extraUpsell.price.amount.doubleValue * Double(upsellsCount)
            let totalCost = Cost(amount: totalAmount, currencyCode: extraUpsell.price.currencyCode)

            extrasViewModels.append(BookingSummaryExtrasViewModel(
                extraItem: extraUpsell,
                numberOfRooms: upsellsCount,
                totalCostText: totalCost.localizedValue
            ))
        }
        return extrasViewModels
    }

    func load(completion: @escaping (Reservation?, BookingDetails?, Hotel?, Error?) -> Void) {
        guard let reservation else {
            completion(nil, bookingDetails, hotel, nil)
            return
        }

        completion(reservation, nil, self.hotel, nil)
    }
}
