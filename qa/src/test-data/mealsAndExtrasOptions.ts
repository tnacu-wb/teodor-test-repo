import { type StringBase } from './stringBase';
import { Strings } from './strings';
import { IbStrings } from './pib/ibStrings';

/** Meals and extras options for hotel room preferences. */
export interface MealsAndExtrasOption {
  option: StringBase;
  /** Display order in the meals and extras preferences section. */
  optionNumber?: number;
  /** Value from graphQL getProfileDetails.bookingPreference.foodPreference. */
  value?: number;
}

/** Meal, Wi-Fi, and invoicing preference options used by profile and booking tests. */
export class MealsAndExtrasOptions {
  private constructor() {}

  static readonly PI_BREAKFAST: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_PI_BREAKFAST_MY_PROFILE_IB, optionNumber: 1, value: 11 };
  static readonly CONTINENTAL_BREAKFAST: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_CONTINENTAL_BREAKFAST_MY_PROFILE_IB, optionNumber: 2, value: 12 };
  static readonly MEAL_DEAL: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_MEAL_DEAL_MY_PROFILE_IB, optionNumber: 3, value: 17 };
  static readonly HUB_BREAKFAST: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_MEAL_DEAL_MY_PROFILE_IB, value: 18 };
  static readonly NO_MEAL: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_NO_MEAL_MY_PROFILE_IB, optionNumber: 4, value: 0 };
  static readonly WI_FI_ALWAYS: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_WIFI_ALWAYS_MY_PROFILE_IB, optionNumber: 1 };
  static readonly WI_FI_NEVER: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_WIFI_NEVER_MY_PROFILE_IB, optionNumber: 2 };
  static readonly INVOICING_EMAIL: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_INVOICING_EMAIL_MY_PROFILE_IB, optionNumber: 1 };
  static readonly INVOICING_CHECK_IN: MealsAndExtrasOption = { option: IbStrings.MEALS_AND_EXTRAS_INVOICING_CHECK_IN_MY_PROFILE_IB, optionNumber: 2 };
  static readonly ULTIMATE_WIFI1: MealsAndExtrasOption = { option: Strings.ULTIMATE_WIFI, value: 135 };
  static readonly ULTIMATE_WIFI2: MealsAndExtrasOption = { option: Strings.ULTIMATE_WIFI, value: 136 };
  static readonly ULTIMATE_WIFI3: MealsAndExtrasOption = { option: Strings.ULTIMATE_WIFI, value: 137 };

  /**
   * Get a meals and extras option by display name.
   * @param optionName Meal or extras option name.
   * @returns Matching meals and extras option.
   */
  static async getMealsAndExtrasOptionByName(optionName: string): Promise<MealsAndExtrasOption> {
    let found: MealsAndExtrasOption | null = null;
    for (const option of Object.values(MealsAndExtrasOptions)) {
      if (typeof option === 'object' && 'option' in option && await option.option.name === optionName) {
        found = option;
        break;
      }
    }
    if (!found) {
      throw new Error(`${optionName} option name is not found in the list!`);
    }
    return found;
  }
}
