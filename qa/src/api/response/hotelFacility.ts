/**
 * The hotel facility from API response
 * response example: 
 *  {
 *    "data":{
 *       "hotelInformation":{
 *          "hotelFacilities":[
 *             {
 *                "code":"DIS",
 *                "description":"Barrierefreie Zimmer",
 *                "icon":"/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg",
 *                "isVisible":true,
 *                "name":"Barrierefreie Zimmer",
 *                "weight":2
 *             }
 *          ]
 *       }
 *    }
 *  }
 */
export class HotelFacility {
  [key: string]: unknown;
  code?: string;
  description?: string;
  icon?: string;
  isVisible?: boolean;
  name?: string;
  weight?: number;

  /**
   * HotelFacility constructor
   * @param data object data
   * @param data.hotelFacility hotelFacility
   */
  constructor(data: { hotelFacility?: Record<string, unknown> } = {}) {
    const hotelFacility = data.hotelFacility ?? {};
    this.code = hotelFacility.code as string | undefined;
    this.description = hotelFacility.description as string | undefined;
    this.icon = hotelFacility.icon as string | undefined;
    this.isVisible = hotelFacility.isVisible as boolean | undefined;
    this.name = hotelFacility.name as string | undefined;
    this.weight = hotelFacility.weight as number | undefined;
  }

  static fromResponse(data: { hotelFacility?: Record<string, unknown> }): HotelFacility {
    return new HotelFacility(data);
  }
}
