package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.NumericUDFType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDefinedFields {

  private List<CharacterUDFs> characterUDFs;
  private List<NumericUDFType> numericUDFs;

}
