/**
 * The extra Items information from API response
 * response example: 
 *  {
 *   "data": {
 *       "packages": {
 *           "packages": {
 *               "extraItems": [
 *                   {
                        "name": "Late check-out",
                        "id": "HSCOU2",
                        "price": 10.0,
                        "imageSrc": "/content/dam/global/extras/late-checkout.png",
                        "description": "Check out any time until 2pm (normal check-out time is 12pm).",
                        "currency": "GBP",
                        "order": 2,
                        "available": 6
                    },
                    {
                        "name": "Early check-in",
                        "id": "HSCKIN",
                        "price": 10.0,
                        "imageSrc": "/content/dam/global/extras/early-check-in.png",
                        "description": "Check in any time from 11am (normal check-in time is 3pm).",
                        "currency": "GBP",
                        "order": 1,
                        "available": 9
                    }
 *               ]
 *           }
 *       }
 *   }
 *}
 */
export class AncillariesExtraItems {
  [key: string]: unknown;
  available?: number;
  currency?: string;
  description?: string;
  id?: string;
  imageSrc?: string;
  name?: string;
  order?: number;
  price?: number;

  /**
   * AncillariesExtraItems Constructor
   * @param data object data
   * @param data.extraItem extra item
   */
  constructor(data: { extraItem?: Record<string, unknown> } = {}) {
    const extraItem = data.extraItem ?? {};
    this.name = extraItem.name as string | undefined;
    this.id = extraItem.id as string | undefined;
    this.price = extraItem.price as number | undefined;
    this.imageSrc = extraItem.imageSrc as string | undefined;
    this.description = extraItem.description as string | undefined;
    this.currency = extraItem.currency as string | undefined;
    this.order = extraItem.order as number | undefined;
    this.available = extraItem.available as number | undefined;
  }

  static fromResponse(data: { extraItem?: Record<string, unknown> }): AncillariesExtraItems {
    return new AncillariesExtraItems(data);
  }
}
