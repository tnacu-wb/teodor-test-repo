/**
 * The adult meal information from API response
 * response example: 
 *  {
 *   "data": {
 *       "getPackages": {
 *           "packages": {
 *               "meals": [
 *                   {
 *                       "currency": "GBP",
 *                       "id": "BFADBF",
 *                       "freeBreakfastOption": true,
 *                       "price": 9.5,
 *                       "name": "Premier Inn Breakfast",
 *                       "imageSrc": "/content/dam/global/restaurants/Global/full-breakfast-booking.png",
 *                       "description": "<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n",
 *                       "allergyInfoSrc": "/content/dam/global/restaurants/Global/breakfast-allergy.pdf",
 *                       "allergyInfoLabel": "Allergy & nutrition info"
 *                   }
 *               ]
 *           }
 *       }
 *   }
 *}
 */
export class AncillariesAdultMeal {
  [key: string]: unknown;
  allergyInfoLabel?: string;
  allergyInfoSrc?: string;
  bartId?: string;
  currency?: string;
  description?: string;
  freeBreakfastMaxPerMeal?: number;
  freeBreakfastOption?: boolean;
  id?: string;
  imageSrc?: string;
  menu?: unknown;
  name?: string;
  order?: number;
  price?: number;

  /**
   * AncillariesAdultMeal Constructor
   * @param data object data
   * @param data.adultMeal adultMeal
   */
  constructor(data: { adultMeal?: Record<string, unknown> } = {}) {
    const adultMeal = data.adultMeal ?? {};
    this.currency = adultMeal.currency as string | undefined;
    this.id = adultMeal.id as string | undefined;
    this.freeBreakfastOption = adultMeal.freeBreakfastOption as boolean | undefined;
    this.price = adultMeal.price as number | undefined;
    this.name = adultMeal.name as string | undefined;
    this.imageSrc = adultMeal.imageSrc as string | undefined;
    this.description = adultMeal.description as string | undefined;
    this.allergyInfoSrc = adultMeal.allergyInfoSrc as string | undefined;
    this.allergyInfoLabel = adultMeal.allergyInfoLabel as string | undefined;
    this.order = adultMeal.order as number | undefined;
    this.menu = adultMeal.menu;
    this.bartId = adultMeal.bartId as string | undefined;
    this.freeBreakfastMaxPerMeal = adultMeal.freeBreakfastMaxPerMeal as number | undefined;
  }

  static fromResponse(data: { adultMeal?: Record<string, unknown> }): AncillariesAdultMeal {
    return new AncillariesAdultMeal(data);
  }
}
