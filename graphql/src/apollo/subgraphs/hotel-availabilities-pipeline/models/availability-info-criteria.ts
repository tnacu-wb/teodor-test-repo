import { AvailabilitySearchCriteria } from '../../hotel-entity-service/models/availability-search-criteria';

export class PipelineAvailabilityInfoCriteria {
  availabilityCriteria: AvailabilitySearchCriteria;
  brand: string;
  language: string;
  country: string;
  hotelId: string;
  channel?: string;
  ratePlans?: string[];

  constructor(data: any) {
    this.availabilityCriteria = data.availabilityCriteria;
    this.brand = data.brand;
    this.language = data.language;
    this.country = data.country;
    this.hotelId = data.hotelId;
    this.channel = data.channel;
    this.ratePlans = data.ratePlans;
  }
}
