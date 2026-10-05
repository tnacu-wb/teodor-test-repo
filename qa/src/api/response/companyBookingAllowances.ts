/**
 * {
    "data": {
        "companyDetails": {
            "requestedCompany": {
                "bookingAllowances": {
                    "maxDinnerBudgets": {
                        "uKWide": {
                            "amount": 0,
                            "currency": "GBP"
                        },
                        "greaterLondon": {
                            "amount": 0,
                            "currency": "GBP"
                        },
                        "ireland": {
                            "amount": 0,
                            "currency": "EUR"
                        }
                    },
                    "extrasCodes": [],
                    "upsellItemsAllowed": [],
                    "allowAlcohol": false,
                    "allowCarParking": false,
                    "allowAdditionalCosts": false,
                    "allowPremierSaverRates": true,
                    "allowIndividualCards": false,
                    "maxNumberOfNights": 14
                }
            }
        }
    }
}
 */
export class CompanyBookingAllowances {
  [key: string]: unknown;
  allowAdditionalCosts?: boolean;
  allowAlcohol?: boolean;
  allowCarParking?: boolean;
  allowIndividualCards?: boolean;
  allowPremierSaverRates?: boolean;
  extrasCodes?: string[];
  maxDinnerBudgets?: {
    uKWide: { amount: number; currency: string };
    greaterLondon: { amount: number; currency: string };
    ireland: { amount: number; currency: string };
  };
  maxNumberOfNights?: number;
  upsellItemsAllowed?: string[];

  /**
   * Company Booking Allowances Info
   * @param data data
   * @param data.bookingAllowances company allowances
   */
  constructor(data: { bookingAllowances?: Record<string, unknown> } = {}) {
    const bookingAllowances = data.bookingAllowances ?? {};
    this.maxDinnerBudgets = bookingAllowances.maxDinnerBudgets as CompanyBookingAllowances['maxDinnerBudgets'];
    this.extrasCodes = bookingAllowances.extrasCodes as string[] | undefined;
    this.upsellItemsAllowed = bookingAllowances.upsellItemsAllowed as string[] | undefined;
    this.allowAlcohol = bookingAllowances.allowAlcohol as boolean | undefined;
    this.allowCarParking = bookingAllowances.allowCarParking as boolean | undefined;
    this.allowAdditionalCosts = bookingAllowances.allowAdditionalCosts as boolean | undefined;
    this.allowPremierSaverRates = bookingAllowances.allowPremierSaverRates as boolean | undefined;
    this.allowIndividualCards = bookingAllowances.allowIndividualCards as boolean | undefined;
    this.maxNumberOfNights = bookingAllowances.maxNumberOfNights as number | undefined;
  }

  static fromResponse(data: { bookingAllowances?: Record<string, unknown> }): CompanyBookingAllowances {
    return new CompanyBookingAllowances(data);
  }

  /**
   * Set allowances to default settings meals and individual payment cards included ON state
   */
  setAllowancesDefaultSettings(): void {
    if (this.maxDinnerBudgets) {
      this.maxDinnerBudgets.uKWide.amount = 0;
      this.maxDinnerBudgets.greaterLondon.amount = 0;
      this.maxDinnerBudgets.ireland.amount = 0;
    }
    this.extrasCodes = [];
    this.upsellItemsAllowed = ['11', '15', '12', '17', '18'];
    this.allowAlcohol = false;
    this.allowCarParking = false;
    this.allowAdditionalCosts = false;
    this.allowPremierSaverRates = true;
    this.allowIndividualCards = true;
    this.maxNumberOfNights = 14;
  }

  /**
   * Reset all allowances to OFF state
   */
  resetAllowancesToOffState(): void {
    if (this.maxDinnerBudgets) {
      this.maxDinnerBudgets.uKWide.amount = 0;
      this.maxDinnerBudgets.greaterLondon.amount = 0;
      this.maxDinnerBudgets.ireland.amount = 0;
    }
    this.extrasCodes = [];
    this.upsellItemsAllowed = [];
    this.allowAlcohol = false;
    this.allowCarParking = false;
    this.allowAdditionalCosts = false;
    this.allowPremierSaverRates = true;
    this.allowIndividualCards = false;
    this.maxNumberOfNights = 14;
  }

}
