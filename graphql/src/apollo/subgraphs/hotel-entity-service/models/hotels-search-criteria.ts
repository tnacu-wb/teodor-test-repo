export class HotelsSearchCriteria {
  location: string;
  locationFormat: string;
  radius: string;
  radiusUnit: string;

  constructor(data: any) {
    this.location = data.location;
    this.locationFormat = data.locationFormat;
    this.radius = data.radius;
    this.radiusUnit = data.radiusUnit;
  }
}
