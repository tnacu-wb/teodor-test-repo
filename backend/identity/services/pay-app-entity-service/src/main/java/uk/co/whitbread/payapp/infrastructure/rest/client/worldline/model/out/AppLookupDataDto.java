package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppLookupDataDto {

  private Map<String, List<String>> lookupData;

}
