import { MenuRestaurant } from './menuRestaurant';

/**
 * The restaurant response from ancillaries API
 * response example"
{
    "data": {
        "packages": {
            "restaurant": {
                "logoSrc": "/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg",
                "menus": [
                    {
                        "description": null,
                        "disclaimer": null,
                        "imageSrc": null,
                        "menuSrc": "/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf",
                        "menuLabel": null,
                        "name": "Breakfast"
                    }
                  ]
               }
            }
         }
      }
   }
}
 */
export class RestaurantDetails {
  [key: string]: unknown;
  logoSrc?: string;
  menus: MenuRestaurant[] = [];
  messageDescription?: string;
  messageHeader?: string;
  noMealsFound?: boolean;
  restaurantNotFound?: boolean;

  /**
   * Restaurant details constructor
   * @param data object data
   * @param data.restaurantDetails object
   */
  constructor(data: { restaurantDetails?: Record<string, unknown> } = {}) {
    const restaurantDetails = data.restaurantDetails ?? {};
    this.logoSrc = restaurantDetails.logoSrc as string | undefined;
    this.messageDescription = restaurantDetails.messageDescription as string | undefined;
    this.messageHeader = restaurantDetails.messageHeader as string | undefined;
    this.noMealsFound = restaurantDetails.noMealsFound as boolean | undefined;
    this.restaurantNotFound = restaurantDetails.restaurantNotFound as boolean | undefined;

    const menus = Array.isArray(restaurantDetails.menus) ? (restaurantDetails.menus as Array<Record<string, unknown>>) : [];
    this.menus = menus.map((menu) => new MenuRestaurant({ menuRestaurant: menu }));
  }

  static fromResponse(data: { restaurantDetails?: Record<string, unknown> }): RestaurantDetails {
    return new RestaurantDetails(data);
  }

}
