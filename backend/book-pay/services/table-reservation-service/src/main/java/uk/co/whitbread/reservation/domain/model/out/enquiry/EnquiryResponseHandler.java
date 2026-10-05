package uk.co.whitbread.reservation.domain.model.out.enquiry;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnquiryResponseHandler {

  private EnquiryResponse enquiryResponse;
}
