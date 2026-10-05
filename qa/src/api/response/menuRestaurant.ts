/**
 * One menu restaurant object from menu restaurant list
 */
export class MenuRestaurant {
  [key: string]: unknown;
  description?: string;
  disclaimer?: string;
  imageSrc?: string;
  menuLabel?: string;
  menuSrc?: string;
  name?: string;

  /**
   * MenuRestaurant constructor
   * @param data object data
   * @param data.menuRestaurant menuRestaurant
   */
  constructor(data: { menuRestaurant?: Record<string, unknown> } = {}) {
    const menuRestaurant = data.menuRestaurant ?? {};
    this.name = menuRestaurant.name as string | undefined;
    this.description = menuRestaurant.description as string | undefined;
    this.imageSrc = menuRestaurant.imageSrc as string | undefined;
    this.menuSrc = menuRestaurant.menuSrc as string | undefined;
    this.menuLabel = menuRestaurant.menuLabel as string | undefined;
    this.disclaimer = menuRestaurant.disclaimer as string | undefined;
  }

  static fromResponse(data: { menuRestaurant?: Record<string, unknown> }): MenuRestaurant {
    return new MenuRestaurant(data);
  }
}
