package uk.co.whitbread.ohip.domain.model.profile.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CreateProfileRequest {

  private ProfileDetails profileDetails;

  private List<ProfileIdList> profileIdList;
}
