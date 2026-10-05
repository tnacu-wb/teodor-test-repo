package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.out.CompanyDetails;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppCompanyDetailsLookupDto;

@Mapper(componentModel = "spring")
public interface AppCompanyDetailsLookupMapper {

  List<CompanyDetails> toModel(List<AppCompanyDetailsLookupDto> data);
}
