import { SourceDetails } from './source-details';
import { CustomerDetails } from './customer-details';

export class UpdateMarketingPreferencesRequest {
  brandCodes: string[];
  optIn: boolean;
  doubleOptIn: boolean;
  customer: CustomerDetails;
  sourceDetails: SourceDetails;

  constructor(data: any) {
    this.brandCodes = data.brandCodes;
    this.optIn = data.optIn;
    this.doubleOptIn = data.doubleOptIn;
    this.customer = new CustomerDetails(data.customer);
    this.sourceDetails = new SourceDetails(data.sourceDetails);
  }
}
