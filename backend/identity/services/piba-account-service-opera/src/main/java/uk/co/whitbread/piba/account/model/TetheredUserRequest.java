package uk.co.whitbread.piba.account.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.piba.account.model.enums.Scheme;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserRequest {
    private Scheme scheme;
    private String companyId;
    private List<TetheredGuidDetails> tetheredGuids;

}
