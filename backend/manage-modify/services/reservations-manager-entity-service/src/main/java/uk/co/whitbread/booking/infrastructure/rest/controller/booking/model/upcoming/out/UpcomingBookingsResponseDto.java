package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.GalleryImageDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.LinksDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.ThumbnailImageDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.TopSectionImageDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingBookingsResponseDto {

  private Integer stays;
  private Integer bookings;
  private String hotelName;
  private LocalDate arrivalDate;
  private LocalTime arrivalTime;
  private LocalDate departureDate;
  private LocalTime departureTime;
  private String bookingReference;
  private List<GalleryImageDto> galleryImages;
  private String brand;
  private LinksDto links;
  private List<ThumbnailImageDto> thumbnailImages;
  private List<TopSectionImageDto> topSectionImages;
}
