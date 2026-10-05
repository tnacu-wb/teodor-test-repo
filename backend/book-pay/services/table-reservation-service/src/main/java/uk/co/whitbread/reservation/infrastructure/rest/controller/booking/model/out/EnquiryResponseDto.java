package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnquiryResponseDto {
  private int adults;
  private String bookingReference;
  private String cancelLink;
  private int children;
  private ConsentDto consent;
  private String date;
  private String editLink;
  private String emailAddress;
  private String firstname;
  private String id;
  private String lastname;
  private String occasionId;
  private String occasionName;
  private String paymentLink;
  private String siteId;
  private String siteName;
  private String telephoneNumber;
  private String time;
  private int turnTimeMinutes;




}
