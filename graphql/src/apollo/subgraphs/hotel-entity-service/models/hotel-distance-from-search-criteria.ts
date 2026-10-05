export class HotelDistanceFromSearchCriteria {
  hotelId: string;
  location: string;
  locationFormat: string;
  radius?: string;
  radiusUnit?: string;

  constructor(data: any) {
    this.hotelId = data.hotelId;
    this.location = data.location;
    this.locationFormat = data.locationFormat;
    this.radius = data.radius;
    this.radiusUnit = data.radiusUnit;
  }
}
