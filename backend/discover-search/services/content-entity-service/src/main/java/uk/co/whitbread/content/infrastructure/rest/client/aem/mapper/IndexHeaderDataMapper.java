package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.AemIndexHeaderDataDto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface IndexHeaderDataMapper {

  IndexHeaderData toDomainModel(AemIndexHeaderDataDto aemIndexHeaderDataDto);
}
