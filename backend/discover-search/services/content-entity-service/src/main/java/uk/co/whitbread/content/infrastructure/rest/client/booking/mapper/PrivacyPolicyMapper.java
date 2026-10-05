package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.PrivacyPolicy;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.PrivacyPolicyDto;

@Mapper(componentModel = "spring")
public interface PrivacyPolicyMapper {

  @Mapping(target = "name", source = "title")
  @Mapping(target = "linkSrc", source = "linkPath")
  PrivacyPolicy toDomainModel(PrivacyPolicyDto donationDto);
}
