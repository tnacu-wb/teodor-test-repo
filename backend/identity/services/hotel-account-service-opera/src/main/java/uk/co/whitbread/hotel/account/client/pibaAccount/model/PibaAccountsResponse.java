package uk.co.whitbread.hotel.account.client.pibaAccount.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PibaAccountsResponse {
    List<PibaAccount> accounts;
}
