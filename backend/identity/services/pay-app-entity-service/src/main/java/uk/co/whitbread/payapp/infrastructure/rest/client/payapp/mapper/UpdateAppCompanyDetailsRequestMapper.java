package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetails;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppCompanyDetailsUpdateRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateAppCompanyDetailsRequestMapper {

  WLAppCompanyDetailsUpdateRequestDto toModel(
      AppCompanyDetails updateAppCompanyDetailsRequest);

}
