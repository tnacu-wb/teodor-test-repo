package uk.co.whitbread.hotel.account.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfo;
import uk.co.whitbread.hotel.account.model.AccountInfoResponseDto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AccountInfoMapper {

  AccountInfoResponseDto toResponseDto(AccountInfo accountInfo);

}
