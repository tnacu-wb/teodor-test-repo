package uk.co.whitbread.piba.account.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;

@Mapper(componentModel = "spring")
public interface PibaTetheredGuidResponseMapper {

    List<PibaTetheredGuidResponse> map(List<uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse> pibaTetheredGuidResponses);

    default List<String> mapTetheredGuid(String tetheredGuid) {
        if (tetheredGuid == null)
            return List.of();
        return List.of(tetheredGuid);
    }

}
