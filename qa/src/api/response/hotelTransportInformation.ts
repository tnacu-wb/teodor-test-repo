/**
 * The hotel transport information from API response
 * response example: 
 * {"data":{
      "hotelInformation":{
         "transportInformation":[
            "King's Cross St Pancras Station & tube - 7 mins walk",
            "London Euston - 17 mins walk",
            "ZSL London Zoo - 30 mins walk",
            "The British Museum - 25 mins walk",
            "Regents Park - 25 mins walk "
         ]
      }
   }
}
 */
export class HotelTransportInformation {
  [key: string]: unknown;
  transportInformation?: string[];

  /**
   * HotelTransportInformation constructor
   * @param data object data
   * @param data.hotelTransportInfoApiResponse response from API
   */
  constructor(data: { hotelTransportInfoApiResponse?: string[] } = {}) {
    this.transportInformation = data.hotelTransportInfoApiResponse;
  }

  static fromResponse(data: { hotelTransportInfoApiResponse?: string[] }): HotelTransportInformation {
    return new HotelTransportInformation(data);
  }
}
