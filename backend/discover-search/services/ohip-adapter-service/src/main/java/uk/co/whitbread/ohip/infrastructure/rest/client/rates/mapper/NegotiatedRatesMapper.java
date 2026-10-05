package uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.NegotiatedRates;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;

@Mapper(componentModel = "spring")
public interface NegotiatedRatesMapper {
  NegotiatedRatesResponse toDomainModel(NegotiatedRates negotiatedRates);
}
