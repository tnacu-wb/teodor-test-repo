/**
 * Defines the Place object used in hotelAvailabilitiesInput
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
export interface PlaceData {
  location?: string;
  locationFormat?: string;
  radius?: number;
  radiusUnit?: string;
}

export class Place {
  [key: string]: unknown;
  location?: string;
  locationFormat?: string;
  radius?: number;
  radiusUnit?: string;

  constructor({ location, locationFormat, radius, radiusUnit }: PlaceData = {}) {
    this.location = location;
    this.locationFormat = locationFormat;
    this.radius = radius;
    this.radiusUnit = radiusUnit;
  }

  static fromRequest(data: PlaceData): Place {
    return new Place(data);
  }
}
