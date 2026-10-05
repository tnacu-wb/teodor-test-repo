import { MenuRestaurant } from './menuRestaurant';

/**
 * The hotel restaurant response from API
 */
export class HotelRestaurantConfiguration {
  [key: string]: unknown;
  description?: string;
  logoSrc?: string;
  menus: MenuRestaurant[] = [];
  name?: string;

  /**
   * HotelRestaurantConfiguration constructor
   * @param data object data
   * @param data.hotelRestaurantConfiguration object
   */
  constructor(data: { hotelRestaurantConfiguration?: Record<string, unknown> } = {}) {
    const hotelRestaurantConfiguration = data.hotelRestaurantConfiguration ?? {};
    this.name = hotelRestaurantConfiguration.name as string | undefined;
    this.description = hotelRestaurantConfiguration.description as string | undefined;
    this.logoSrc = hotelRestaurantConfiguration.logoSrc as string | undefined;
    const menus = Array.isArray(hotelRestaurantConfiguration.menus) ? (hotelRestaurantConfiguration.menus as Array<Record<string, unknown>>) : [];
    this.menus = menus.map((menu) => new MenuRestaurant({ menuRestaurant: menu }));
  }

  static fromResponse(data: { hotelRestaurantConfiguration?: Record<string, unknown> }): HotelRestaurantConfiguration {
    return new HotelRestaurantConfiguration(data);
  }

}
