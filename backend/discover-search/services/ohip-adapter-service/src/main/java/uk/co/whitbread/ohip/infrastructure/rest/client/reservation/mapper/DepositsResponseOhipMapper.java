package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RetrievedDepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.ohip.domain.model.reservation.out.Deposits;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;

@Mapper(componentModel = "spring")
public interface DepositsResponseOhipMapper {

  @Mapping(source = "retrievedDepositFolio", target = "deposits",
            qualifiedByName = "toDepositsForModel")
  DepositsResponse toModel(RetrievedDepositFolio retrievedDepositFolio);

  @Named("toDepositsForModel")
  default List<Deposits> toDepositsForModel(RetrievedDepositFolio retrievedDepositFolio) {
    return retrievedDepositFolio.getReservationDepositFoliosInfo().get(0)
                .getDeposits().stream()
                .map(depositPostingType -> Deposits.builder()
                        .paymentReference(depositPostingType.getReference())
                        .postedAmount(CurrencyAmountType
                                .builder()
                                .amount(depositPostingType.getPostedAmount().getAmount())
                                .currencyCode(depositPostingType.getPostedAmount().getCurrencyCode())
                                .build())
                        .build()).toList();
  }
}