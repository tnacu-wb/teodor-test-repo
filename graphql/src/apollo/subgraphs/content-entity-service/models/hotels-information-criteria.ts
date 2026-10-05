export class HotelsInformationCriteria {
  hotelIds: string[];
  country: string;
  language: string;
  latitudeRef?: number;
  longitudeRef?: number;
  tripAdvisorDataRequired?: boolean;
  stayStartDate?: string;
  stayEndDate?: string;

  constructor(data: any) {
    this.hotelIds = data.hotelIds;
    this.country = data.country;
    this.language = data.language;
    this.latitudeRef = data.latitudeRef;
    this.longitudeRef = data.longitudeRef;
    this.tripAdvisorDataRequired = data.tripAdvisorDataRequired;
    this.stayStartDate = data.stayStartDate;
    this.stayEndDate = data.stayEndDate;
  }
}
