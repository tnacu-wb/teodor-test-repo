import { GalleryImage } from './galleryImage';

/**
 * response example:
{
    "data": {
        "getUpcomingBookings": {
            "arrivalDate": "2025-05-24",
            "arrivalTime": "15:00:00",
            "bookingReference": "GAA7603446",
            "bookings": 2,
            "brand": "PID",
            "departureDate": "2025-05-25",
            "departureTime": "12:00:00",
            "galleryImages": [
                {
                    "alt": "",
                    "imageSrc": "/content/dam/pi/websites/hotelimages/gb/en/F/FRAMTI/Frankfurt Exterior-min.jpeg"
                }
            ],
            "hotelName": "Frankfurt Messe",
            "stays": 2
        }
    }
}
 */
export class UpcomingBooking {
  [key: string]: unknown;
  arrivalDate?: string;
  arrivalTime?: string;
  bookingReference?: string;
  bookings?: number;
  brand?: string;
  departureDate?: string;
  departureTime?: string;
  galleryImages: GalleryImage[] = [];
  hotelName?: string;
  stays?: number;

  /**
   * Upcoming booking constructor
   * @param data object data
   * @param data.upcomingBooking upcoming booking data
   */
  constructor(data: { upcomingBooking?: Record<string, unknown> } = {}) {
    const upcomingBooking = data.upcomingBooking ?? {};
    this.arrivalDate = upcomingBooking.arrivalDate as string | undefined;
    this.arrivalTime = upcomingBooking.arrivalTime as string | undefined;
    this.bookingReference = upcomingBooking.bookingReference as string | undefined;
    this.bookings = upcomingBooking.bookings as number | undefined;
    this.brand = upcomingBooking.brand as string | undefined;
    this.departureDate = upcomingBooking.departureDate as string | undefined;
    this.departureTime = upcomingBooking.departureTime as string | undefined;
    this.hotelName = upcomingBooking.hotelName as string | undefined;
    this.stays = upcomingBooking.stays as number | undefined;

    const galleryImages = Array.isArray(upcomingBooking.galleryImages) ? (upcomingBooking.galleryImages as Array<Record<string, unknown>>) : [];
    this.galleryImages = galleryImages.map((galleryImage) => new GalleryImage({ galleryImage }));
  }

  static fromResponse(data: { upcomingBooking?: Record<string, unknown> }): UpcomingBooking {
    return new UpcomingBooking(data);
  }

}
