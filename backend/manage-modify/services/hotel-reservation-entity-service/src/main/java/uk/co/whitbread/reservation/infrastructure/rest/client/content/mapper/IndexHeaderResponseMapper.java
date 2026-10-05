package uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.entity.service.generated.models.content.BannerDto;
import uk.co.whitbread.content.entity.service.generated.models.content.IndexHeaderDataDto;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.Banner;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;

@Mapper(componentModel = "spring")
public interface IndexHeaderResponseMapper {

  IndexHeaderData toModel(IndexHeaderDataDto indexData);

  Banner toModel(BannerDto value);
}
