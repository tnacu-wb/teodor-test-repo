//
//  BathroomSelection+NewTwinRoom.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 18/06/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import SimpleNetwork

extension BathroomSelectionInteractor {
    static func buildTwinRoomStore(from rooms: [Room]) -> [RoomAndSelection] {
        var twinRoomOptions = [RoomAndSelection]()

        let twinRooms = rooms.filter { $0.isNewTwinRoom }

        for twinRoom in twinRooms {
            guard let roomOptions = twinRoom.options else {
                // no options should not be possible for Opera (or even BART v2)
                guard let lettingType = twinRoom.lettingType else { continue }

                let parsedTwinRoom = parseTwinRoom(fromLettingType: lettingType)

                twinRoomOptions.append(RoomAndSelection(
                    lettingType: lettingType,
                    price: twinRoom.totalCost,
                    isAccessibleRoom: false,
                    isTwinRoom: parsedTwinRoom != nil,
                    roomType: nil,
                    bathroomType: nil,
                    twinType: parsedTwinRoom
                )
                )

                continue
            }

            for roomOption in roomOptions {
                guard let lettingType = roomOption.lettingType else { continue }
                guard let parsedTwinRoom = parseTwinRoom(
                    fromLettingType: lettingType,
                    specialRequests: roomOption.specialRequests
                ) else { continue }

                let twinRoom = RoomAndSelection(
                    lettingType: lettingType,
                    price: roomOption.totalCost,
                    isAccessibleRoom: false,
                    isTwinRoom: true,
                    roomType: nil,
                    bathroomType: nil,
                    twinType: parsedTwinRoom
                )

                // this doesn't exist in Opera - need to maybe use specialRequests to understand which flow we should go for (considering numberAvailable is coming back for Opera)
                if roomOption.alternativeLettingType != .roomUpsell {
                    twinRoomOptions.append(twinRoom)
                } else {
                    /*
                      "The moon, the sign of hope
                       It appeared when we left the pain of the ice desert behind
                       We faced up to the curse and endured misery
                       Condemned we are
                       We brought hope, but also lies, and treachery"

                     if numberAvailable is provided then we add this option the respective amount of times on the first room that we see this option - if it's not provided then we add it once every time
                     e.g. room1Options = [WB, TB], room2Options = [WB, TB]
                     scenario 1: TB.numberAvailable = 2, then we add it 2 times when we parse room1Options, but skip adding it during room2Options
                     scenario 2: TB.numberAvailable = nil, then we add it one time when we parse room1Options, and one more time when we parse room2Options */

                    if roomOption.numberAvailable != nil {
                        // Once we have the room type we can check if we've already added, say, Wet rooms for a twin room
                        guard twinRoomOptions.contains(where: { $0.twinType == parsedTwinRoom }) == false else { continue }
                    }

                    // And if we haven't then we'll add enough bathroom and rooms to match
                    let twinRooms = [RoomAndSelection](repeating: twinRoom, count: roomOption.numberAvailable ?? 1)

                    twinRoomOptions.append(contentsOf: twinRooms)
                }
            }
        }

        return twinRoomOptions
    }

    private static func parseTwinRoom(
        fromLettingType lettingType: String,
        specialRequests: [String]? = nil
    ) -> NewTwinRoomType? {
        parseOperaTwinRoom(fromLettingType: lettingType, specialRequests: specialRequests)
    }

    private static func parseOperaTwinRoom(
        fromLettingType lettingType: String,
        specialRequests: [String]?
    ) -> NewTwinRoomType? {
        guard let specialRequest = specialRequests?
              .first(where: { twinRoomSpecialRequests.contains(SpecialRequest(rawValue: $0) ?? .unknown) })
        else { return nil }
        guard let twinSpecialRequest = SpecialRequest(rawValue: specialRequest) else { return nil }

        var twinRoomTypeToAdd: NewTwinRoomType

        switch twinSpecialRequest {
        case .twinTwoSingleBeds:
            twinRoomTypeToAdd = .TrueTwin
        case .twinDoubleBedAndSofa:
            twinRoomTypeToAdd = .PremierInnTwin
        default:
            return nil
        }

        return twinRoomTypeToAdd
    }
}
