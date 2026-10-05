package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.DiscountUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.PromotionUtils;

@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {Arrays.class})
public abstract class UpdateDiscountRequestOhipMapper {

  private UpdateDiscountOhipMapper updateDiscountOhipMapper;

  @Autowired
  public void toUpdateDiscountOhipMapperForModel(
      final UpdateDiscountOhipMapper updateDiscountOhipMapper) {
    this.updateDiscountOhipMapper = updateDiscountOhipMapper;
  }

  @Mapping(expression = "java(injectHotelReservations(updateDiscountRequest,hotelReservationTypeList))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(
      UpdateDiscountRequest updateDiscountRequest,
      List<HotelReservationType> hotelReservationTypeList);

  protected List<HotelReservationInstructionType> injectHotelReservations(
      UpdateDiscountRequest updateDiscountRequest,
      List<HotelReservationType> hotelReservationTypeList) {
    List<HotelReservationInstructionType> reservations = new ArrayList<>();

    var ratesList = extractAllRates(hotelReservationTypeList);

    var dividedAmounts = DiscountUtils.getWeightedDiscountAmounts(
        ratesList, updateDiscountRequest.getDiscountAmount());

    int hotelReservationsSize = hotelReservationTypeList.size();

    int[] lastVisited = {0};

    IntStream.range(0, hotelReservationsSize).forEach(index -> {
      HotelReservationType tempReservation = hotelReservationTypeList.get(index);
      var reservationTemp =
          updateDiscountOhipMapper.fromDto(updateDiscountRequest, tempReservation, dividedAmounts,
              lastVisited);

      PromotionUtils.truncatePromotion(reservationTemp);
      reservations.add(reservationTemp);
    });

    return reservations;
  }

  private List<BigDecimal> extractAllRates(List<HotelReservationType> reservationDetails) {
    return reservationDetails.stream()
        .map(res -> res.getRoomStay().getRoomRates())
        .flatMap(Collection::stream)
        .map(rate -> rate.getRates().getRate())
        .flatMap(Collection::stream)
        .map(amount -> {
          var amountBeforeTax = amount.getBase().getAmountBeforeTax();
          var discountObj = Optional.ofNullable(amount.getDiscount());
          if (discountObj.isPresent()) {
            return amountBeforeTax.add(discountObj.get().getAmount());
          }
          return amountBeforeTax;
        })
        .toList();
  }
}
