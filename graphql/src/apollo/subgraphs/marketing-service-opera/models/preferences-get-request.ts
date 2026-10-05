export class PreferencesGetRequest {
  business?: boolean;
  contactType: string;
  contactValue: string;
  contactChannelId?: string;
  brandCodes: string;
  countryOfResidence?: string;
  language?: string;

  constructor(data: PreferencesGetRequest) {
    this.business = data.business;
    this.contactType = data.contactType;
    this.contactValue = data.contactValue;
    this.contactChannelId = data.contactChannelId;
    this.brandCodes = data.brandCodes;
    this.countryOfResidence = data.countryOfResidence;
    this.language = data.language;
  }
}
