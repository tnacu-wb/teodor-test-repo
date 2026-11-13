/**
 * Defines the Room object used in hotelAvailabilitiesInput
 * Example of input object for Hotel Availabilities request (executed when the user is searching available hotels for a specific location)
{
    "oldWorldChannel": "WEB",
    "country": "de",
    "endDate": "2022-11-09",
    "initialPageSize": "40",
    "language": "de",
    "lazyLoadPageSize": "10",
    "page": 2,
    "place": {
        "location": "ChIJdd4hrwug2EcRmSrV3Vo6llI",
        "locationFormat": "PLACEID",
        "radius": 40,
        "radiusUnit": "KILOMETERS"
    },
    "rooms": [
        {
        "adultsNumber": 1,
        "childrenNumber": 0,
        "type": "SB"
        }
    ],
    "sort": "DISTANCE",
    "startDate": "2022-11-08",
    "filters": ["LFT","CPP"],
    "channel": "PI",
    "subChannel": "WEB"
}
 */
export interface RoomData {
  adultsNumber?: number;
  childrenNumber?: number;
  type?: string;
  roomType?: string;
  cotRequired?: boolean;
  roomNumber?: number;
}

export class Room {
  [key: string]: unknown;
  adultsNumber?: number;
  childrenNumber?: number;
  type?: string;
  roomType?: string;
  cotRequired?: boolean;
  roomNumber?: number;

  constructor({ adultsNumber, childrenNumber, type, roomType, cotRequired, roomNumber }: RoomData = {}) {
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.type = type ?? roomType;
    this.roomType = roomType ?? type;
    this.cotRequired = cotRequired ?? false;
    this.roomNumber = roomNumber;
  }

  static fromRequest(data: RoomData): Room {
    return new Room(data);
  }
}
