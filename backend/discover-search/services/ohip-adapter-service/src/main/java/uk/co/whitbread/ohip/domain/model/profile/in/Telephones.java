package uk.co.whitbread.ohip.domain.model.profile.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Telephones {

  private List<TelephoneInfo> telephoneInfo;

}
