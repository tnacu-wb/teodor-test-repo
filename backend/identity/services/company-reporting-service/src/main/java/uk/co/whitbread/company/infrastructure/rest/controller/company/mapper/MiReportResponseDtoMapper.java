package uk.co.whitbread.company.infrastructure.rest.controller.company.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.company.domain.model.out.MiReportResult;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.MiReportResponseDto;

@Mapper(componentModel = "spring")
public interface MiReportResponseDtoMapper {

  MiReportResponseDto toResponseDto(MiReportResult miReportResult);

}
