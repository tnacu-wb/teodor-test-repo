package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DonationPackagesResponseMapper {

  DonationPackagesResponse toModel(DonationPackagesResponseDto donationPackagesResponseOhipDto);

}
