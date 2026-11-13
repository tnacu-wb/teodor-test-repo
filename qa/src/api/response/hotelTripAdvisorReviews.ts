/**
Example:
{
  "data": {
    "tripAdvisorReviews": {
      "rating": 5,
      "numberOfReviews": 6,
    },
    "links": {
      "detailsPage": "/england/greater-london/london/london-victoria",
    }
  }
}
 */
export class HotelTripAdvisorReviews {
  [key: string]: unknown;
  numberOfReviews?: number;
  rating?: number;
  slug?: string;

  /**
   * HotelTripAdvisorReviews constructor
   * @param data object data
   * @param data.tripAdvisorReviews tripadvisor reviews
   * @param data.links hotel links
   */
  constructor(data: { tripAdvisorReviews?: { rating?: number; numberOfReviews?: number }; links?: { detailsPage?: string } } = {}) {
    this.rating = data.tripAdvisorReviews?.rating;
    this.numberOfReviews = data.tripAdvisorReviews?.numberOfReviews;
    this.slug = data.links?.detailsPage;
  }

  static fromResponse(data: { tripAdvisorReviews?: { rating?: number; numberOfReviews?: number }; links?: { detailsPage?: string } }): HotelTripAdvisorReviews {
    return new HotelTripAdvisorReviews(data);
  }
}
