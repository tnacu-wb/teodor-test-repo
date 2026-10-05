import { AncillariesAdultMeal } from './ancillariesAdultMeals';
import { AncillariesChildMeal } from './ancillariesChildMeal';
import { RoomSelection } from './roomSelection';

/**
 The adult meal information from API response
 response example: 
  {
   "data": {
       "getPackages": {
           "packages": {
               "meals": [
                    {
                        "name": "Premier Inn Breakfast",
                        "order": 2,
                        "id": "BFADBF",
                        "price": 9.5,
                        "currency": "GBP",
                        "allergyInfoLabel": "Allergy & nutrition info",
                        "allergyInfoSrc": "/content/dam/global/restaurants/Global/breakfast-allergy.pdf",
                        "description": "<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n",
                        "imageSrc": "/content/dam/global/restaurants/Global/full-breakfast-booking.png",
                        "freeBreakfastOption": true,
                        "freeBreakfastCode": "BFCHDF",
                        "freeBreakfastMaxPerMeal": 2
                    },
                    {
                        "name": "Continental Breakfast",
                        "order": 3,
                        "id": "BFADCT",
                        "price": 7.5,
                        "currency": "GBP",
                        "allergyInfoLabel": "Allergy & nutrition info",
                        "allergyInfoSrc": "/content/dam/global/restaurants/Global/breakfast-allergy.pdf",
                        "description": "<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n",
                        "imageSrc": "/content/dam/global/restaurants/Global/continental-breakfast-booking.png",
                        "freeBreakfastOption": false,
                        "freeBreakfastCode": "",
                        "freeBreakfastMaxPerMeal": 2
                    },
                    {
                        "name": "Meal Deal",
                        "order": 0,
                        "id": "MDP",
                        "price": 24.99,
                        "currency": "GBP",
                        "allergyInfoLabel": null,
                        "allergyInfoSrc": null,
                        "description": "<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n",
                        "imageSrc": "/content/dam/global/restaurants/Global/pi-mealdeal-aw22-334x189.jpg",
                        "freeBreakfastOption": true,
                        "freeBreakfastCode": "BFCHDF",
                        "freeBreakfastMaxPerMeal": 2
                    }
                ],
                "mealsKids": [
                    {
                        "name": "Free breakfast for kids",
                        "order": 0,
                        "id": "BFCHDF",
                        "price": null,
                        "currency": null,
                        "allergyInfoLabel": null,
                        "allergyInfoSrc": null,
                        "description": "<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n",
                        "imageSrc": "/content/dam/global/restaurants/Global/child-breakfast.jpg"
                    }
                ],
                "roomSelection": [
                    {
                        "packagesSelection": [
                            {
                                "id": "MDP",
                                "noOfSelections": 1
                            }
                        ]
                    },
                    {
                        "packagesSelection": [
                            {
                                "id": "BFCHDF",
                                "noOfSelections": 1
                            },
                            {
                                "id": "MDP",
                                "noOfSelections": 1
                            }
                        ]
                    }
                ]
            }
        }
    }
 }
 */
export class MealPackages {
  [key: string]: unknown;
  adultMeals?: AncillariesAdultMeal[];
  childMeals?: AncillariesChildMeal[];
  hotelHasCityTaxForBusiness?: boolean;
  hotelHasCityTaxForLeisure?: boolean;
  roomSelection: RoomSelection[] = [];

  /**
   * MealPackages constructor
   * @param data object data
   * @param data.childMeals childMeals data
   * @param data.adultMeals adultMeals data
   * @param data.roomSelection roomSelection data
   * @param data.hotelHasCityTaxForLeisure hotel has city tax for leisure reason of stay
   * @param data.hotelHasCityTaxForBusiness hotel has city tax for business reason of stay
   */
  constructor(
    data: {
      childMeals?: AncillariesChildMeal[];
      adultMeals?: AncillariesAdultMeal[];
      roomSelection?: RoomSelection[];
      hotelHasCityTaxForLeisure?: boolean;
      hotelHasCityTaxForBusiness?: boolean;
    } = {},
  ) {
    this.childMeals = data.childMeals;
    this.adultMeals = data.adultMeals;
    this.roomSelection = data.roomSelection ?? [];
    this.hotelHasCityTaxForLeisure = data.hotelHasCityTaxForLeisure;
    this.hotelHasCityTaxForBusiness = data.hotelHasCityTaxForBusiness;
  }

  static fromResponse(data: {
    childMeals?: AncillariesChildMeal[];
    adultMeals?: AncillariesAdultMeal[];
    roomSelection?: RoomSelection[];
    hotelHasCityTaxForLeisure?: boolean;
    hotelHasCityTaxForBusiness?: boolean;
  }): MealPackages {
    return new MealPackages(data);
  }

  /**
   * Validate the actual package codes against expected
   * @param expectedCodes expected package codes
   */
  async validateMealPackages(expectedCodes: string[]): Promise<void> {
    console.log('Validate actual package codes against expected');

    const packageCodes: string[] = [];
    for (const room of this.roomSelection) {
      for (const pack of room.packagesSelection) {
        if (pack.id !== undefined) {
          packageCodes.push(pack.id);
        }
      }
    }
    if (packageCodes.length !== expectedCodes.length) {
      throw new Error('Lengths not equal.');
    }

    const sortedExpectedCodes = [...expectedCodes].sort();
    const sortedPackageCodes = [...packageCodes].sort();
    for (const [index, code] of sortedExpectedCodes.entries()) {
      if (sortedPackageCodes[index] !== code) {
        throw new Error('Codes not matching.');
      }
    }
  }

}
