package uk.co.whitbread.payments.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.payments.model.SaveCardDetails;
import uk.co.whitbread.payments.model.SaveCardRequest;

@Mapper(componentModel = "spring")
public interface SaveCardMapper {

  SaveCardDetails toDomain(SaveCardRequest saveCardRequest);
}
