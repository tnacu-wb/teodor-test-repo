package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpInformationDto;

@Mapper(componentModel = "spring", uses = {
    FaqItemMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)

public interface AemClientDlpInformationMapper {

  DlpInformationRequestDto toDto(DlpInformationRequest dlpInformationRequest);

  @Mapping(source = "map", target = "coordinates")
  @Mapping(source = "faqs", target = "faq")
  @Mapping(source = "image", target = "picture")
  DlpInformation toDomainModel(DlpInformationDto dlpInformationDto);
}