package uk.co.whitbread.company.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.company.model.CellCode;
import uk.co.whitbread.shared.cdh.model.company.CompanyCellCode;

@Mapper(componentModel = "spring")
public interface CellCodeMapper {

  @Mapping(target = "description", source = "cellCode")
  @Mapping(target = "type", source = "id")
  CellCode toCompanyCellCode(CompanyCellCode cellCode);

}
