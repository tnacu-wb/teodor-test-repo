package uk.co.whitbread.hotel.card.client.piba.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserRequest {
    private Scheme scheme;
    private String companyId;
    private List<TetheredGuidDetails> tetheredGuids;

}
