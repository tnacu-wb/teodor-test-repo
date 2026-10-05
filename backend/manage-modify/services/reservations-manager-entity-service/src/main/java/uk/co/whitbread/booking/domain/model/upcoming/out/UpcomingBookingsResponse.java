package uk.co.whitbread.booking.domain.model.upcoming.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import uk.co.whitbread.booking.domain.model.information.out.GalleryImage;
import uk.co.whitbread.booking.domain.model.information.out.Links;
import uk.co.whitbread.booking.domain.model.information.out.ThumbnailImage;
import uk.co.whitbread.booking.domain.model.information.out.TopSectionImage;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UpcomingBookingsResponse {

  private Integer stays;
  private Integer bookings;
  private String hotelName;
  private LocalDate arrivalDate;
  private LocalTime arrivalTime;
  private LocalDate departureDate;
  private LocalTime departureTime;
  private String bookingReference;
  private String brand;
  private Links links;
  private List<GalleryImage> galleryImages;
  private List<ThumbnailImage> thumbnailImages;
  private List<TopSectionImage> topSectionImages;
}
