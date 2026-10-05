package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out.DlpInformationDto;

@Mapper(componentModel = "spring")
public interface ControllerDlpInformationMapper {

  DlpInformationRequest toDomainModel(DlpInformationRequestDto dlpInformationRequestDto);

  DlpInformationDto toDto(DlpInformation dlpInformation);
}
