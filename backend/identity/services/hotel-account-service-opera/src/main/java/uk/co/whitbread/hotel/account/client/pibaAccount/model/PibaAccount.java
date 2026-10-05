package uk.co.whitbread.hotel.account.client.pibaAccount.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PibaAccount {

    protected int schemeCustomerId;
    protected String tetheredGuid;
    protected String accountName;
    protected String accountNumber;
    protected String apiUserGuid;
    protected Scheme scheme;
}
