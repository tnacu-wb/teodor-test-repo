package uk.co.whitbread.basket.domain.logic.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.reservation.in.DepositFolioRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFolioResponse;

@Mapper(componentModel = "spring")
public interface DepositFolioResponseMapper {

  List<DepositFolioRequest> toDepositRequestModel(
      List<DepositFolioResponse> depositFolioRequestList);
}
