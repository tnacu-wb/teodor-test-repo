package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppContactDetailsRequest;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppContactDetailsUpdateRequestDto;
import uk.co.whitbread.shared.cdh.model.Participants;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationParticipant;

@Mapper(componentModel = "spring")
public interface UpdateAppContactDetailsRequestMapper {

  WLAppContactDetailsUpdateRequestDto toModel(UpdateAppContactDetailsRequest updateAppContactDetailsRequest);

  List<Participants> toModel(List<ApplicationParticipant> applicationParticipants);

  @Mapping(target = "directdebit", source = "directDebit")
  Participants toModel(ApplicationParticipant applicationParticipant);

}
