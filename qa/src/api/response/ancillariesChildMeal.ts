/**
 * The children meal information from API response
 * response example: 
 *  {
 *   "data": {
 *       "getPackages": {
 *           "packages": {
 *               "mealsKids": [
 *                   {
 *                      "allergyInfoLabel": null,
                        "allergyInfoSrc": null,
                        "currency": null,
                        "description": "<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n",
                        "id": "BFCHDF",
                        "imageSrc": "/content/dam/global/restaurants/Global/child-breakfast.jpg",
                        "name": "Free breakfast for kids",
                        "order": 0
 *                   }
 *               ]
 *           }
 *       }
 *   }
 *}
 */
export class AncillariesChildMeal {
  [key: string]: unknown;
  allergyInfoLabel?: string;
  allergyInfoSrc?: string;
  currency?: string;
  description?: string;
  id?: string;
  imageSrc?: string;
  menu?: unknown;
  name?: string;
  order?: number;
  price?: number;
  totalPrice?: number;

  /**
   * AncillariesChildMeal Constructor
   * @param data object data
   * @param data.kidsMeal kidsMeal
   */
  constructor(data: { kidsMeal?: Record<string, unknown> } = {}) {
    const kidsMeal = data.kidsMeal ?? {};
    this.allergyInfoLabel = kidsMeal.allergyInfoLabel as string | undefined;
    this.allergyInfoSrc = kidsMeal.allergyInfoSrc as string | undefined;
    this.currency = kidsMeal.currency as string | undefined;
    this.description = kidsMeal.description as string | undefined;
    this.id = kidsMeal.id as string | undefined;
    this.imageSrc = kidsMeal.imageSrc as string | undefined;
    this.name = kidsMeal.name as string | undefined;
    this.order = kidsMeal.order as number | undefined;
    this.menu = kidsMeal.menu;
    this.price = kidsMeal.price as number | undefined;
    this.totalPrice = kidsMeal.totalPrice as number | undefined;
  }

  static fromResponse(data: { kidsMeal?: Record<string, unknown> }): AncillariesChildMeal {
    return new AncillariesChildMeal(data);
  }
}
