import { CorporateRate } from './corporate-rate';

export class Rate {
  corporateRates?: CorporateRate;
  ratePlanCodes?: string[];

  constructor(data: any) {
    this.corporateRates = data.corporateRates;
    this.ratePlanCodes = data.ratePlanCodes;
  }
}
