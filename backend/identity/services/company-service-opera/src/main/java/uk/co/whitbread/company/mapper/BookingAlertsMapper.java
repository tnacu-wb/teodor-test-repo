package uk.co.whitbread.company.mapper;

import static uk.co.whitbread.company.utils.EnumConverter.getEnum;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import uk.co.whitbread.company.model.BookingAlertsFrequency;

@Mapper(componentModel = "spring")
public interface BookingAlertsMapper {


  @Named("toBookingAlertsFrequency")
  default BookingAlertsFrequency toBookingAlertsFrequency(String frequency) {
    return getEnum(BookingAlertsFrequency.class, frequency);
  }

}
