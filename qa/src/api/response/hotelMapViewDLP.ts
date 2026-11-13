/**
Example:
{
  "data": {
    "name": "Milton Keynes Central(Xscape)",
    "brand": "PI",
    "coordinates": {
      "latitude": 52.03957,
      "longitude": -0.75216,
    }
    "distanceFromReference": 0.32593957777703575,
    "hotelFacilities": [],
    "tripAdvisorReviews": {
      "rating": 5,
    },
    "links": {
      "detailsPage": "/england/greater-london/london/london-victoria",
    }
    "topSectionImages": [{
      thumbnailSrc: "/content/dam/pi/websites/hotelimages/gb/en/M/MILBAR/MILBAR 1.jpg",
    }]
  }
}
 */
export class HotelMapViewDLP {
  [key: string]: unknown;
  brand?: string;
  distanceFromReference?: number;
  hotelFacilities?: unknown[];
  latitude?: number;
  longitude?: number;
  name?: string;
  numberOfReviews?: number;
  position?: string;
  rating?: number;
  slug?: string;
  thumbnailSrc?: string;

  /**
   * HotelInventory constructor
   * @param data object data
   * @param data.name hotel name
   * @param data.brand hotel brand
   * @param data.coordinates location coordinates
   * @param data.distanceFromReference location distance from reference
   * @param data.hotelFacilities hotel facilities
   * @param data.tripAdvisorReviews tripadvisor reviews
   * @param data.links hotel links
   * @param data.topSectionImages hotel images
   */
  constructor(
    data: {
      name?: string;
      brand?: string;
      coordinates?: { latitude?: number; longitude?: number };
      distanceFromReference?: number;
      hotelFacilities?: unknown[];
      tripAdvisorReviews?: { rating?: number; numberOfReviews?: number };
      links?: { detailsPage?: string };
      topSectionImages?: Array<{ thumbnailSrc?: string }>;
    } = {},
  ) {
    const { name, brand, coordinates, distanceFromReference, hotelFacilities, tripAdvisorReviews, links, topSectionImages } = data;
    this.name = name;
    this.brand = brand;
    this.latitude = coordinates?.latitude;
    this.longitude = coordinates?.longitude;
    this.position = `${coordinates?.latitude},${coordinates?.longitude}`;
    this.distanceFromReference = distanceFromReference;
    this.hotelFacilities = hotelFacilities;
    this.rating = tripAdvisorReviews?.rating;
    this.numberOfReviews = tripAdvisorReviews?.numberOfReviews;
    this.slug = links?.detailsPage;
    if (topSectionImages?.[0]?.thumbnailSrc != null) {
      this.thumbnailSrc = topSectionImages[0].thumbnailSrc;
    }
  }

  static fromResponse(data: {
    name?: string;
    brand?: string;
    coordinates?: { latitude?: number; longitude?: number };
    distanceFromReference?: number;
    hotelFacilities?: unknown[];
    tripAdvisorReviews?: { rating?: number; numberOfReviews?: number };
    links?: { detailsPage?: string };
    topSectionImages?: Array<{ thumbnailSrc?: string }>;
  }): HotelMapViewDLP {
    return new HotelMapViewDLP(data);
  }

}
