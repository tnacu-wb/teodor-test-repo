package uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.model.in.WalletRequestDto;

@Mapper(componentModel = "spring")
public interface WalletRequestMapper  {
  WalletRequest toModel(WalletRequestDto amendConfirmationPricesResponse);
}
