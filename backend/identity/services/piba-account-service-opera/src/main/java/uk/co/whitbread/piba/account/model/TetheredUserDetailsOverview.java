package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserDetailsOverview {
    protected String tetheredUserGuid;
    protected String apiUserGuid;
    protected String userRole;
    protected int myCards;
}
