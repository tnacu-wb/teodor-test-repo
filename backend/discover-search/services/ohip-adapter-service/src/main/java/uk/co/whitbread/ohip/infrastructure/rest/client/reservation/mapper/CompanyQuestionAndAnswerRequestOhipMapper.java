package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;

@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {Arrays.class})
public abstract class CompanyQuestionAndAnswerRequestOhipMapper {

  private CompanyQuestionAndAnswerOhipMapper companyQuestionAndAnswerOhipMapper;

  @Autowired
  public void toCompanyQuestionAndAnswerOhipMapperForModel(
      final CompanyQuestionAndAnswerOhipMapper companyQuestionAndAnswerOhipMapper) {
    this.companyQuestionAndAnswerOhipMapper = companyQuestionAndAnswerOhipMapper;
  }

  @Mapping(expression =
      "java(injectHotelReservations(hotelId,reservationId,companyQuestionAndAnswerDetailsRequest))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(
      String hotelId,
      String reservationId,
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest
  );

  protected List<HotelReservationInstructionType> injectHotelReservations(
      String hotelId,
      String reservationId,
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest) {

    return Collections.singletonList(
        companyQuestionAndAnswerOhipMapper.fromDto(hotelId, reservationId, companyQuestionAndAnswerDetailsRequest));

  }
}
